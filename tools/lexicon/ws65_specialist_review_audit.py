#!/usr/bin/env python3
"""WS65: read-only identity, source and eligibility audit for specialist draft intake.

Creates a reproducible *unsigned* editorial worksheet/report; never writes to
authoring editions, Android source, saves, old page manifests or approvals.
"""
import argparse
from collections import Counter, defaultdict
import csv
import hashlib
import json
from pathlib import Path
import re
import sys

from validate import normalized
from ws63_quality_audit import ROOT
from ws64_woodland_review_queue import load_canonical, read_manuscripts

INPUT = Path("data/lexicon/intake/ws65-specialist-chapter-candidates-v1.tsv")
COLUMNS = (
    "candidate_id", "answer", "display", "topic_id", "chapter_title",
    "subtopic_id", "content_group", "proposed_tier", "why_it_fits",
    "editorial_reference", "editorial_origin", "canonical_existing_word_id",
    "canonical_existing_edition", "prior_woodland_manuscript", "mechanics_flags",
    "review_status", "editor_decision", "owner_decision", "review_notes",
)
CHAPTERS = {
    "Our Solar System": ("space", "solar-system"),
    "Flower Borders": ("garden", "flowers"),
}
RELEASE_FIELDS = ("editor_decision", "owner_decision", "review_notes")


def load_rows(root=ROOT):
    with (root / INPUT).open(encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f, delimiter="\t")
        if reader.fieldnames != list(COLUMNS):
            raise ValueError("WS65 columns changed; review schema before ingest")
        return list(reader)


def audit(rows, root=ROOT):
    canonical = load_canonical(root)
    previous, manuscript_hashes = read_manuscripts(root, canonical)
    old_by_answer = defaultdict(list)
    for old in previous:
        old_by_answer[old["answer"]].append(old["source_manuscript"])

    seen_answers, seen_ids = set(), set()
    chapter_counts, group_counts = Counter(), Counter()
    reuse_by_chapter, prior_by_chapter = Counter(), Counter()
    flags = Counter()
    ready_12 = Counter()
    qa_matches, new_candidates, existing_unapproved = 0, 0, 0
    for row in rows:
        answer, cid = row["answer"], row["candidate_id"]
        if not re.fullmatch(r"[A-Z]{3,20}", answer):
            raise ValueError(f"Bad A-Z answer: {answer!r}")
        if not re.fullmatch(r"ws65c[0-9]{3}", cid):
            raise ValueError(f"Bad candidate id {cid!r}")
        if normalized(row["display"]) != answer:
            raise ValueError(f"Display normalization fails {cid}: {row['display']}")
        if answer in seen_answers or cid in seen_ids:
            raise ValueError(f"Duplicate candidate answer or ID: {answer} / {cid}")
        seen_answers.add(answer)
        seen_ids.add(cid)
        if row["chapter_title"] not in CHAPTERS:
            raise ValueError(f"Unknown chapter {row['chapter_title']}")
        topic, subtopic = CHAPTERS[row["chapter_title"]]
        if (row["topic_id"], row["subtopic_id"]) != (topic, subtopic):
            raise ValueError(f"Wrong topic/subtopic {cid}")
        if row["proposed_tier"] not in ("familiar", "intermediate", "advanced"):
            raise ValueError(f"Unknown proposed tier {cid}")
        if not row["why_it_fits"] or not row["content_group"]:
            raise ValueError(f"Missing specific context/group {cid}")
        if not row["editorial_reference"].startswith("https://"):
            raise ValueError(f"Missing context source URL {cid}")
        if row["editorial_origin"] != "EDITOR_ORIGINAL_HAND_SELECTED_CANDIDATE":
            raise ValueError(f"Uncleared import/source identity {cid}")
        if row["review_status"] != "UNREVIEWED_CANDIDATE":
            raise ValueError(f"Candidate incorrectly promoted {cid}")
        if any(row[k] for k in RELEASE_FIELDS):
            raise ValueError(f"Review field filled without owner workflow {cid}")

        old = canonical.get(answer)
        if old:
            qa_matches += bool(old["qa_approved"])
            existing_unapproved += not old["qa_approved"]
            if row["canonical_existing_word_id"] != old["word_id"]:
                raise ValueError(f"Canonical ID drift on {answer}")
            # First occurrence is en-v1 when present in both; do not
            # misinterpret multiple catalog appearances as new words.
            if row["canonical_existing_edition"] != old["edition"].split(",")[0]:
                raise ValueError(f"Canonical edition drift on {answer}")
            reuse_by_chapter[row["chapter_title"]] += 1
        else:
            new_candidates += 1
            if row["canonical_existing_word_id"] or row["canonical_existing_edition"]:
                raise ValueError(f"Invented canonical reference for new {answer}")

        previous_refs = old_by_answer.get(answer, [])
        if row["prior_woodland_manuscript"] != ";".join(previous_refs):
            raise ValueError(f"Old Woodland source overlap drift on {answer}")
        if previous_refs:
            prior_by_chapter[row["chapter_title"]] += 1
        listed = set(filter(None, row["mechanics_flags"].split(";")))
        required = set()
        if answer == answer[::-1]:
            required.add("PALINDROME_V5_HOLD")
        if len(answer) >= 12:
            required.add("NO_12X12")
        if row["proposed_tier"] != "familiar":
            required.add("NOT_RELAXED_DEFAULT")
        if previous_refs:
            required.add("OLD_WOODLAND_DRAFT_REUSE")
        if old and not old["qa_approved"]:
            required.add("EXISTING_NOT_QA_APPROVED")
        if not required.issubset(listed):
            raise ValueError(f"Missing required mechanical/provenance flags on {answer}: {required-listed}")
        flags.update(listed)
        chapter_counts[row["chapter_title"]] += 1
        group_counts[row["content_group"]] += 1
        if (row["proposed_tier"] == "familiar"
                and len(answer) < 12 and answer != answer[::-1]):
            ready_12[row["chapter_title"]] += 1

    if not rows:
        raise ValueError("Empty candidate intake")
    return {
        "format": "WS65_SPECIALIST_EDITORIAL_DRAFT_RECEIPT_V1",
        "status": "UNREVIEWED_CANDIDATES_NO_OWNER_OR_ANDROID_APPROVAL",
        "candidate_rows": len(rows),
        "distinct_answers": len(seen_answers),
        "canonical_matches_existing_qa_approved": qa_matches,
        "canonical_matches_existing_but_unapproved": existing_unapproved,
        "not_in_english_or_fall_catalog": new_candidates,
        "old_woodland_draft_overlap": sum(prior_by_chapter.values()),
        "chapter_rows": dict(sorted(chapter_counts.items())),
        "chapter_canonical_reuse": dict(sorted(reuse_by_chapter.items())),
        "chapter_prior_manuscript_overlap": dict(sorted(prior_by_chapter.items())),
        "suggested_relaxed_12_candidates_not_playtested": dict(sorted(ready_12.items())),
        "subgroups": dict(sorted(group_counts.items())),
        "review_flags": dict(sorted(flags.items())),
        "input_sha256": hashlib.sha256((root / INPUT).read_bytes()).hexdigest(),
        "woodland_manuscript_sha256": manuscript_hashes,
        "new_owner_public_approvals": 0,
        "new_android_activated_words": 0,
        "warnings": [
            "Reference links are context material, not independent verification of every answer or rights to import external lists.",
            "Existing QA approval is assistant/internal-only and does not authorize public release.",
            "12x12 counts reflect word-length/tier/palindrome only; not actual successful board placement.",
            "Chapter fit, ambiguity, sensitive terms, regional names and all human decisions are still unresolved.",
        ],
    }


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repo-root", type=Path, default=ROOT)
    parser.add_argument("--out-dir", type=Path, default=Path("qa-ws65-specialist"))
    args = parser.parse_args(argv)
    try:
        rows = load_rows(args.repo_root)
        receipt = audit(rows, args.repo_root)
        args.out_dir.mkdir(parents=True, exist_ok=True)
        (args.out_dir / "editorial-receipt.json").write_text(
            json.dumps(receipt, sort_keys=True, indent=2) + "\n", encoding="utf-8")
        with (args.out_dir / "unsigned-candidate-review.csv").open(
                "w", encoding="utf-8", newline="") as f:
            writer = csv.DictWriter(f, fieldnames=COLUMNS, lineterminator="\n")
            writer.writeheader()
            writer.writerows(rows)
        (args.out_dir / "README.md").write_text(
            "# WS65 specialist draft intake — UNSIGNED\n\n"
            f"Candidate rows: {receipt['candidate_rows']}; "
            f"existing QA matched: {receipt['canonical_matches_existing_qa_approved']}; "
            f"candidate-new to English/Fall: {receipt['not_in_english_or_fall_catalog']}.\n\n"
            "**Zero editor signoffs, owner releases or new Android playable words.** "
            "Review this CSV with source rights and chapter-fit decisions before any pack proposal.\n",
            encoding="utf-8")
        print("WS65_DRAFT_RECEIPT_OK:", receipt["candidate_rows"],
              "rows;", receipt["not_in_english_or_fall_catalog"],
              "candidate-new; 0 owner/public approvals")
        return 0
    except (KeyError, OSError, ValueError, csv.Error) as exc:
        print("WS65_DRAFT_RECEIPT_FAIL:", exc, file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
