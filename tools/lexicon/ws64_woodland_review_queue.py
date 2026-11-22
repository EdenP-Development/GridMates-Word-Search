#!/usr/bin/env python3
"""WS64: reconcile 122 EXISTING unapproved Woodland proposals to canonical QA IDs.

This is a READ-ONLY editorial review queue builder; no word/approval/puzzle/save
source is changed, and no public/owner release approvals are granted.

    python3 tools/lexicon/ws64_woodland_review_queue.py --out-dir qa-ws64-review
"""
import argparse
from collections import Counter
import csv
import hashlib
import io
import json
from pathlib import Path
import re
import sys

from validate import normalized
from ws63_quality_audit import ROOT, EDITIONS, normalize_catalog, conflicts_between_editions

MANUSCRIPTS = (
    ("birdsong-ws51-qa-manuscript.tsv", "ws51_editorial_candidate_unapproved", "Birdsong", 5, 23),
    ("woodland-ws54-stream-manuscript.tsv", "ws54_streamlife_candidate_unapproved", "By the Stream", 6, 33),
    ("woodland-ws55-wildlife-manuscript.tsv", "ws55_wildlife_candidate_unapproved", "Forest Discoveries", 8, 30),
    ("woodland-ws56-clearings-manuscript.tsv", "ws56_clearings_candidate_unapproved", "Quiet Clearings", 7, 36),
)
HEADINGS = [
    "candidate_id", "answer", "display", "source_chapter", "chapter_number",
    "source_page", "source_page_title", "source_provenance", "source_manuscript",
    "proposed_tier", "proposed_subtopic", "canonical_existing_word_id",
    "canonical_existing_edition", "canonical_qa_status",
    "intake_classification", "mechanics_flags", "owner_decision",
    "owner_reviewer", "owner_reviewed_on", "editorial_notes",
]


def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def load_canonical(root: Path) -> dict:
    eds = {e: normalize_catalog(e, root / "data/lexicon/authoring" / e)
           for e in EDITIONS}
    conflicts = conflicts_between_editions(eds)
    if conflicts:
        raise ValueError("Do not export a review queue with canonical ID drift: " + "; ".join(conflicts))
    by_answer = {}
    for e in EDITIONS:
        for wid, word in eds[e]["word_rows"].items():
            answer = word["answer"]
            approved = wid in eds[e]["eligible_ids"]
            if answer not in by_answer:
                by_answer[answer] = {
                    "word_id": wid, "edition": e,
                    "qa_approved": approved,
                }
            else:
                if by_answer[answer]["word_id"] != wid:
                    raise ValueError("Canonical cross-edition answer conflict: " + answer)
                by_answer[answer]["qa_approved"] |= approved
                by_answer[answer]["edition"] += "," + e
    return by_answer


def read_manuscripts(root: Path, canonical: dict) -> tuple[list, dict]:
    base = root / "data/lexicon/authoring/chapter-recipes-v1"
    candidates = []
    hashes = {}
    for file, tag, title, chapter, expected in MANUSCRIPTS:
        path = base / file
        hashes[str(path.relative_to(root))] = sha256(path)
        with path.open(encoding="utf8-sig", newline="") as stream:
            reader = csv.DictReader(stream, delimiter="\t")
            required = {"page", "page_title", "answer", "word_id", "display", "tier", "provenance"}
            if not reader.fieldnames or not required.issubset(set(reader.fieldnames)):
                raise ValueError(f"{file}: missing required manuscript columns")
            rows = list(reader)
        selected = []
        for row in rows:
            if row.get("chapter") and int(row["chapter"]) != chapter:
                raise ValueError(f"{file}: wrong chapter identity")
            if not re.fullmatch("[A-Z]{3,20}", row["answer"]):
                raise ValueError(f"{file}: invalid answer {row['answer']}")
            if normalized(row["display"]) != row["answer"]:
                raise ValueError(f"{file}: display/answer mismatch: {row['answer']}")
            if row["tier"] not in {"familiar", "intermediate", "advanced"}:
                raise ValueError(f"{file}: bad tier on {row['answer']}")
            if row["provenance"] == tag:
                selected.append({
                    "candidate_id": row["word_id"],
                    "answer": row["answer"],
                    "display": row["display"],
                    "source_chapter": title,
                    "chapter_number": str(chapter),
                    "source_page": row["page"],
                    "source_page_title": row["page_title"],
                    "source_provenance": row["provenance"],
                    "source_manuscript": file,
                    "proposed_tier": row["tier"],
                    "proposed_subtopic": row.get("subtopic") or "birds",
                })
            else:
                # Original canonical record must not be mislabeled or rewritten.
                existing = canonical.get(row["answer"])
                if not existing or row["word_id"] != existing["word_id"]:
                    raise ValueError(f"{file}: original existing QA word identity mismatch: {row['answer']}")
        if len(selected) != expected:
            raise ValueError(f"{file}: expected {expected} old unapproved proposals; got {len(selected)}")
        candidates += selected
    return candidates, hashes


def reconcile(proposals: list[dict], canonical: dict) -> tuple[list[dict], dict]:
    ids, answers = set(), set()
    rows = []
    by_chapter = Counter()
    reuse_by_chapter = Counter()
    flags = Counter()
    for r in proposals:
        candidate_id = r["candidate_id"]
        answer = r["answer"]
        if candidate_id in ids or answer in answers:
            raise ValueError(f"Duplicate proposed candidate ID/answer: {candidate_id} / {answer}")
        ids.add(candidate_id)
        answers.add(answer)
        old = canonical.get(answer)
        mechanics = []
        if answer == answer[::-1]:
            mechanics.append("V5_PALINDROME_INELIGIBLE")
        if len(answer) >= 12:
            mechanics.append("NOT_12X12_FIT")
        if r["proposed_tier"] != "familiar":
            mechanics.append("NOT_RELAXED_TIER")
        if old:
            mechanics.append("REUSE_EXISTING_CANONICAL_ID")
            reuse_by_chapter[r["source_chapter"]] += 1
        for flag in mechanics:
            flags[flag] += 1
        by_chapter[r["source_chapter"]] += 1
        rows.append({
            **r,
            "canonical_existing_word_id": old["word_id"] if old else "",
            "canonical_existing_edition": old["edition"] if old else "",
            "canonical_qa_status": ("approved-internal-QA" if old["qa_approved"] else
                                    "candidate-only") if old else "not-in-English-or-Fall-QA",
            "intake_classification": ("REUSE_EXISTING_QA_CANONICAL" if old else
                                       "PROPOSED_NEW_CANONICAL_NEEDS_REVIEW"),
            "mechanics_flags": ";".join(mechanics),
            # Explicitly unsigned. No assistant or automated reviewer can
            # impersonate the owner by copying historic QA review metadata.
            "owner_decision": "",
            "owner_reviewer": "",
            "owner_reviewed_on": "",
            "editorial_notes": "",
        })
    count_reused = sum(reuse_by_chapter.values())
    by_title = dict(sorted(by_chapter.items()))
    report = {
        "format": "ws64-woodland-proposal-reconciliation-v1",
        "status": "REVIEW_QUEUE_ONLY_NO_OWNER_OR_PUBLIC_APPROVAL",
        "proposals": len(rows),
        "distinct_proposal_answers": len(answers),
        "already_present_in_English_or_Fall_qa": count_reused,
        "potential_new_canonical_after_review": len(rows) - count_reused,
        "newly_owner_approved_words": 0,
        "newly_activated_android_words": 0,
        "by_chapter": by_title,
        "existing_canonical_by_chapter": dict(sorted(reuse_by_chapter.items())),
        "soft_eligibility_flags": dict(sorted(flags.items())),
        "unchanged_source_manuscript_count": len(MANUSCRIPTS),
        "notes": [
            "All source words are unapproved chapter suggestions even when the same answer is already present in internal QA English/Fall.",
            "When a candidate answer already has a global QA word ID, reuse that ID in a NEW future content edition; do not copy the ws51/ws54/ws55/ws56 proposal ID as a second canonical answer.",
            "Potential new words are NOT validated, reviewed by owner, shipped, or guaranteed to be eligible for a complete generated board.",
            "Do not modify old 63+18 frozen QA page IDs/board hashes, V1-V5 save IDs, or Android runtime.",
        ],
    }
    return rows, report


def build(root: Path = ROOT) -> tuple[list, dict]:
    canonical = load_canonical(root)
    proposals, hashes = read_manuscripts(root, canonical)
    rows, report = reconcile(proposals, canonical)
    report["source_sha256"] = hashes
    report["canonical_source_sha256"] = {
        e: {
            f: sha256(root / "data/lexicon/authoring" / e / f)
            for f in ("words_candidate.csv", "word_topics_candidate.csv")
        } for e in EDITIONS
    }
    return rows, report


def render_markdown(report: dict) -> str:
    return "\n".join([
        "# WS64 — Woodland candidate intake without duplicate canonical IDs",
        "",
        "> **Not an owner-approved or production content release.** Source files remain frozen.",
        "",
        f"**Existing unapproved manuscript proposals:** {report['proposals']}",
        f"**Already in English/Fall QA catalog (must reuse original IDs):** {report['already_present_in_English_or_Fall_qa']}",
        f"**Potential genuinely new canonical answers, subject to all editorial gates:** {report['potential_new_canonical_after_review']}",
        "**New owner/public approvals or activated Android words:** 0",
        "",
        "| Chapter | Original unapproved proposals | Existing QA canonical reused |",
        "| --- | ---: | ---: |",
        *[f"| {ch} | {n} | {report['existing_canonical_by_chapter'].get(ch,0)} |"
          for ch,n in report["by_chapter"].items()],
        "",
        "CSV holds one row per original proposal including source, page, proposed tier,",
        "canonical match ID if any, optional V5 eligibility flags and **blank owner",
        "review/decision fields**. Existing QA approval does NOT equal owner-public approval.",
        "It makes no change to the 670-word first English QA pack, 150-word Fall edition,",
        "the 63+18 immutable QA book pages, source manifests, Daily, Nearby or any saves.",
        "",
    ])


def main(argv=None) -> int:
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--repo-root", type=Path, default=ROOT)
    p.add_argument("--out-dir", type=Path, default=Path("qa-ws64-review"))
    args = p.parse_args(argv)
    try:
        rows, report = build(args.repo_root)
        args.out_dir.mkdir(parents=True, exist_ok=True)
        with (args.out_dir / "woodland-proposals-review.csv").open(
            "w", newline="", encoding="utf8") as f:
            writer = csv.DictWriter(f, fieldnames=HEADINGS, lineterminator="\n")
            writer.writeheader()
            writer.writerows(rows)
        (args.out_dir / "source-reconciliation.json").write_text(
            json.dumps(report, indent=2, sort_keys=True) + "\n", encoding="utf8")
        (args.out_dir / "README.md").write_text(render_markdown(report), encoding="utf8")
        print("WS64_REVIEW_QUEUE_OK:",
              report["proposals"], "unapproved source proposals;",
              report["already_present_in_English_or_Fall_qa"], "canonical existing;",
              report["potential_new_canonical_after_review"], "potential new;",
              "0 new owner approvals; no game changed")
        return 0
    except (ValueError, KeyError, OSError, csv.Error, json.JSONDecodeError) as exc:
        print(f"WS64_REVIEW_QUEUE_FAIL: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
