#!/usr/bin/env python3
"""WS65-F: join player-first editorial sketches to immutable candidate inventory.

This is not third-party dictionary validation, human/editor approval, playable
board generation, catalog publication, or a change to existing player saves.
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
from ws65b_editorial_triage import build as build_preflight
from ws65e_held_reference_closeout import build as build_earlier

SHORTLIST = Path("data/lexicon/intake/ws65f-player-first-shortlist-v1.tsv")
SAMPLES = Path("data/lexicon/intake/ws65f-sample-page-wordlists-v1.tsv")
SOURCE_COLS = [
    "source_pool", "answer", "chapter", "first_pass_priority",
    "provisional_reader_tier", "topic_sense", "player_readability_note",
    "topic_reference", "source_evidence_level", "human_editor_decision",
    "owner_release_decision", "rights_status",
]
SAMPLE_COLS = [
    "sample_page_id", "chapter", "page_title", "board_tier", "slot", "answer",
    "sample_status", "editor_note",
]
SOURCE_LEVEL = "TOPIC_CONTEXT_LINK_ONLY_NOT_INDEPENDENT_DICTIONARY_SIGNOFF"
SAMPLE_STATUS = "UNSIGNED_WORD_LIST_CONCEPT_NOT_GENERATED_BOARD"
TIERS = {"familiar", "intermediate", "advanced"}
BOARD = {"RELAXED12": (12, {"familiar"}), "CLASSIC15": (15, {"familiar", "intermediate"})}
OUTPUT_COLS = [
    "source_pool", "source_id", "answer", "display", "chapter",
    "existing_canonical_id", "prior_tier", "provisional_reader_tier",
    "first_pass_priority", "topic_sense", "player_readability_note",
    "topic_reference", "evidence_kind", "mechanical_limitations",
    "human_editor_decision", "owner_release_decision", "rights_status",
]


def read_tsv(path, headers):
    with path.open(encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f, delimiter="\t")
        if reader.fieldnames != headers:
            raise ValueError(f"Unexpected column schema: {path}")
        return list(reader)


def review(root=ROOT):
    earlier_rows, _, earlier_receipt = build_earlier(root)
    preflight_rows, _ = build_preflight(root)
    candidate_by_key = {(r["source_pool"], r["answer"]): r for r in earlier_rows}
    tier_by_key = {(r["source_pool"], r["answer"]): r["suggested_tier"] for r in preflight_rows}
    if len(candidate_by_key) != len(earlier_rows) or len(tier_by_key) != len(preflight_rows):
        raise ValueError("Old candidate inventories have duplicated source identities")

    annotated = read_tsv(root / SHORTLIST, SOURCE_COLS)
    seen = set()
    review_rows = []
    by_book, tier_count, identity_count = Counter(), Counter(), Counter()
    for row in annotated:
        key = (row["source_pool"], row["answer"])
        if key in seen:
            raise ValueError(f"Duplicate shortlisting key {key}")
        seen.add(key)
        if row["source_pool"] not in {"WS64", "WS65"} or not re.fullmatch(r"[A-Z]{3,20}", row["answer"]):
            raise ValueError(f"Invalid shortlist source spelling {key}")
        source = candidate_by_key.get(key)
        if not source:
            raise ValueError(f"Invented source answer {key}")
        if source["original_preflight_lane"] != "PROMISING":
            raise ValueError(f"Selected candidate was never PROMISING in WS65-B: {key}")
        if (source["ws65c_evidence_status"] != "NOT_IN_THIS_EVIDENCE_BATCH"
                or source["ws65d_evidence_status"] != "NOT_IN_THIS_PASS"
                or source["ws65e_evidence_status"] != "NOT_IN_THIS_PASS"):
            raise ValueError(f"Already evidence-covered candidate should not be counted again: {key}")
        if row["chapter"] != source["original_chapter"]:
            raise ValueError(f"Shortlist silently changed original chapter for {key}")
        if row["provisional_reader_tier"] not in TIERS:
            raise ValueError(f"Invalid suggested tier {key}")
        if row["provisional_reader_tier"] != tier_by_key[key]:
            raise ValueError(f"Earlier editorial tier caution was overridden without justification: {key}")
        if len(row["topic_sense"]) < 21 or len(row["player_readability_note"]) < 17:
            raise ValueError(f"Meaning and familiarity note too short {key}")
        if not row["topic_reference"].startswith("https://"):
            raise ValueError(f"Missing contextual reference {key}")
        if row["source_evidence_level"] != SOURCE_LEVEL:
            raise ValueError(f"Untrue claim of independent dictionary verification {key}")
        if row["human_editor_decision"] or row["owner_release_decision"]:
            raise ValueError(f"Unsigned shortlist must not contain decisions {key}")
        if row["rights_status"] != "NOT_CLEARED":
            raise ValueError(f"Unreviewed rights cannot become cleared {key}")
        if row["first_pass_priority"] not in {"COZY_CORE", "FAMILIAR_CORE"}:
            raise ValueError(f"Unrecognized shortlist level {key}")
        by_book[row["chapter"]] += 1
        tier_count[row["provisional_reader_tier"]] += 1
        identity_count["existing_canonical_reuse" if source["existing_canonical_id"] else "candidate_new_not_approved"] += 1
        limitations = []
        answer = row["answer"]
        if len(answer) >= 12:
            limitations.append("NOT_RELAXED12_LENGTH")
        if answer == answer[::-1]:
            limitations.append("V5_PALINDROME_REVIEW")
        review_rows.append({
            "source_pool": row["source_pool"], "source_id": source["source_id"],
            "answer": answer, "display": source["display"],
            "chapter": row["chapter"], "existing_canonical_id": source["existing_canonical_id"],
            "prior_tier": tier_by_key[key],
            "provisional_reader_tier": row["provisional_reader_tier"],
            "first_pass_priority": row["first_pass_priority"],
            "topic_sense": row["topic_sense"],
            "player_readability_note": row["player_readability_note"],
            "topic_reference": row["topic_reference"],
            "evidence_kind": SOURCE_LEVEL,
            "mechanical_limitations": ";".join(limitations),
            "human_editor_decision": "", "owner_release_decision": "",
            "rights_status": "NOT_CLEARED",
        })
    if not review_rows:
        raise ValueError("No shortlisted rows")

    sample_data = read_tsv(root / SAMPLES, SAMPLE_COLS)
    map_by_key = {(r["chapter"], r["answer"]): r for r in review_rows}
    by_page = defaultdict(list)
    sample_rows = []
    for item in sample_data:
        page = item["sample_page_id"]
        if not re.fullmatch(r"[A-Z][A-Z0-9_]+", page):
            raise ValueError(f"Invalid sample page ID {page}")
        if item["sample_status"] != SAMPLE_STATUS or not item["editor_note"]:
            raise ValueError(f"Sample falsely claims a playable puzzle or lacks warning {page}")
        if item["board_tier"] not in BOARD:
            raise ValueError(f"Invalid sample board size {page}")
        if not re.fullmatch(r"[A-Z]{3,20}", item["answer"]):
            raise ValueError(f"Bad sample spelling {page}")
        try:
            slot = int(item["slot"])
        except ValueError:
            raise ValueError(f"Noninteger sample slot {page}")
        if slot < 1 or slot > 12:
            raise ValueError(f"Unexpected sample word position {page}")
        origin = map_by_key.get((item["chapter"], item["answer"]))
        if not origin:
            raise ValueError(f"Sample uses word not in WS65-F shortlist {page}: {item['answer']}")
        grid, allowed = BOARD[item["board_tier"]]
        if origin["provisional_reader_tier"] not in allowed or len(item["answer"]) > grid:
            raise ValueError(f"Sample exceeds proposed tier/length {page}: {item['answer']}")
        if item["answer"] == item["answer"][::-1]:
            raise ValueError(f"V5 palindrome review hold {page}: {item['answer']}")
        by_page[page].append(item)
        sample_rows.append(item)
    for page, words in by_page.items():
        if len(words) != 12 or sorted(int(w["slot"]) for w in words) != list(range(1, 13)):
            raise ValueError(f"Sample {page} must have exactly twelve sequential unique slots")
        if len({w["answer"] for w in words}) != len(words):
            raise ValueError(f"Sample has duplicate target words {page}")
        if len({(w["chapter"], w["board_tier"], w["page_title"]) for w in words}) != 1:
            raise ValueError(f"Sample title/chapter/tier drift inside page {page}")
        answers = [w["answer"] for w in words]
        for i, a in enumerate(answers):
            for b in answers[i + 1:]:
                if a in b or b in a:
                    raise ValueError(f"Sample has substring-confusable word pair {page}: {a},{b}")
    if len(by_page) != 3:
        raise ValueError("Expected three independent concept pages")

    remaining = earlier_receipt["still_without_individual_reference"] - len(review_rows)
    if remaining < 0:
        raise ValueError("Shortlist greater than uncovered promising inventory")
    receipt = {
        "format": "WS65F_PLAYER_FIRST_SHORTLIST_READ_ONLY_V1",
        "status": "UNSIGNED_PLAYER_FRIENDLINESS_SKETCHES_NOT_PUZZLES_OR_RELEASE",
        "source_proposals_total": earlier_receipt["all_source_proposals_unchanged"],
        "prior_individually_referenced_senses": earlier_receipt["cumulative_research_references"],
        "new_topic_context_shortlists_not_independently_verified": len(review_rows),
        "remaining_promising_without_individual_context": remaining,
        "original_unreferenced_promising": earlier_receipt["still_without_individual_reference"],
        "shortlist_by_chapter": dict(sorted(by_book.items())),
        "shortlist_tier_suggestions": dict(sorted(tier_count.items())),
        "shortlist_existing_vs_potential_new": dict(sorted(identity_count.items())),
        "concept_sample_pages": len(by_page),
        "concept_sample_words": len(sample_rows),
        "concept_samples": {p: [x["answer"] for x in by_page[p]] for p in sorted(by_page)},
        "original_candidate_new_spellings_not_approved": earlier_receipt["candidate_new_spellings_not_reviewed_or_approved"],
        "shortlist_file_sha256": hashlib.sha256((root / SHORTLIST).read_bytes()).hexdigest(),
        "sample_file_sha256": hashlib.sha256((root / SAMPLES).read_bytes()).hexdigest(),
        "new_editor_release_approvals": 0,
        "new_owner_release_approvals": 0,
        "new_android_words": 0,
        "warnings": [
            "A topic-context reference and self-authored gloss are not word-level external lexicographic checks, rights clearance, human-tested familiarity, or publisher approval.",
            "Each sample is a set of twelve draft words with basic length, tier, palindrome, duplicate and substring checks only. No V5 placement, exact path, unique occurrence, board hashes, saved-state or APK testing was performed.",
            "A player-friendly shortlist is not a released word pack. Keep existing English/Fall authoring, immutable 63+18 pages and all older saves untouched.",
        ],
    }
    return review_rows, sample_rows, receipt


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repo-root", type=Path, default=ROOT)
    parser.add_argument("--out-dir", type=Path, default=Path("qa-ws65f-player-first"))
    args = parser.parse_args(argv)
    try:
        words, samples, report = review(args.repo_root)
        args.out_dir.mkdir(parents=True, exist_ok=True)
        with (args.out_dir / "unsigned-player-shortlist.csv").open(
                "w", encoding="utf-8", newline="") as f:
            writer = csv.DictWriter(f, fieldnames=OUTPUT_COLS, lineterminator="\n")
            writer.writeheader()
            writer.writerows(words)
        with (args.out_dir / "concept-only-opening-pages.csv").open(
                "w", encoding="utf-8", newline="") as f:
            writer = csv.DictWriter(f, fieldnames=SAMPLE_COLS, lineterminator="\n")
            writer.writeheader()
            writer.writerows(samples)
        (args.out_dir / "player-first-receipt.json").write_text(
            json.dumps(report, sort_keys=True, indent=2) + "\n", encoding="utf-8")
        (args.out_dir / "README.md").write_text(
            "# WS65-F — unsigned, player-first vocabulary workshop\n\n"
            f"{report['new_topic_context_shortlists_not_independently_verified']} candidates with original editorial glosses; "
            f"{report['remaining_promising_without_individual_context']} promising source proposals remain without a new context note.\n\n"
            "The three opening-page word lists are **NOT generated puzzle boards**. "
            "No approvals, edits to frozen pages, source rights or Android changes.\n",
            encoding="utf-8")
        print("WS65F_SHORTLIST_OK:", len(words), "unsigned player-context notes;",
              len(samples), "concept-page slots;", report["remaining_promising_without_individual_context"],
              "promising still unaddressed; zero approvals")
        return 0
    except (OSError, csv.Error, ValueError, KeyError) as exc:
        print(f"WS65F_SHORTLIST_FAIL: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
