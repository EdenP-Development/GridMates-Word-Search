#!/usr/bin/env python3
"""WS65-G read-only complete editorial context coverage and repeat-risk inventory.

This is a workbook/QA audit, NOT new published vocabulary, external dictionary
verification, human sign-off, V5 grid generation, or an APK modification.
"""
import argparse
from collections import Counter, defaultdict
import csv
import hashlib
import json
from pathlib import Path
import re
import sys

from ws63_quality_audit import ROOT
from ws65e_held_reference_closeout import build as build_ws65e
from ws65f_player_first_shortlist import review as build_ws65f

INPUT = Path("data/lexicon/intake/ws65g-remaining-player-context-v1.tsv")
HEADERS = [
    "source_pool", "answer", "chapter", "original_word_sense",
    "player_experience_note", "caution_code", "topic_context_reference",
    "evidence_level", "candidate_human_decision", "owner_public_approval",
    "source_rights",
]
EVIDENCE_LEVEL = "SELF_AUTHORED_TOPIC_CONTEXT_NOT_INDEPENDENT_WORD_VALIDATION"
SOURCE_RIGHTS = "NOT_CLEARED"
OUTPUT_HEADERS = [
    "source_pool", "source_id", "answer", "display", "chapter",
    "existing_canonical_id", "prior_suggested_tier", "original_word_sense",
    "player_experience_note", "caution_code", "topic_context_reference",
    "research_type", "board_12_eligibility_only", "board_15_eligibility_only",
    "board_20_eligibility_only", "source_rights", "candidate_human_decision",
    "owner_public_approval",
]
BOARD_SPECS = {
    "relaxed12": (12, {"familiar"}),
    "classic15": (15, {"familiar", "intermediate"}),
    "expert20": (20, {"familiar", "intermediate", "advanced"}),
}
TIERS = {"familiar", "intermediate", "advanced"}
SOURCE_CHAPTERS = {
    "Birdsong": "WS64", "By the Stream": "WS64",
    "Forest Discoveries": "WS64", "Quiet Clearings": "WS64",
    "Our Solar System": "WS65", "Flower Borders": "WS65",
}
EDITORIAL_PAIR_GROUPS = [
    {
        "kind": "related_life_stage_not_synonyms",
        "chapter": "Birdsong",
        "answers": ["HATCH", "HATCHLING", "NESTLING", "FLEDGLING"],
        "review": "Explain bird life stage and avoid multiple near-equivalent forms on one easy page.",
    },
    {
        "kind": "near_synonymous_stream_bed",
        "chapter": "By the Stream",
        "answers": ["RIVERBED", "STREAMBED"],
        "review": "Use distinct pages unless an educational contrast is explicitly intended.",
    },
    {
        "kind": "feeding_classes",
        "chapter": "Forest Discoveries",
        "answers": ["HERBIVORE", "CARNIVORE", "OMNIVORE"],
        "review": "These are genuinely different categories; use as progression rather than casual repetition.",
    },
    {
        "kind": "named_mars_moons",
        "chapter": "Our Solar System",
        "answers": ["PHOBOS", "DEIMOS"],
        "review": "A meaningful pair on a moon-focused advanced page, not generic beginner filler.",
    },
    {
        "kind": "similar_sounding_water_bodies",
        "chapter": "Our Solar System",
        "answers": ["KUIPERBELT", "OORTCLOUD", "ASTEROIDBELT"],
        "review": "Different locations and structures; keep clear context and avoid overwhelming novice players.",
    },
]


def read_82(root=ROOT):
    with (root / INPUT).open(newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f, delimiter="\t")
        if reader.fieldnames != HEADERS:
            raise ValueError("WS65-G schema changed unexpectedly")
        output = {}
        for row in reader:
            key = (row["source_pool"], row["answer"])
            if key in output:
                raise ValueError(f"Duplicate editorial row {key}")
            if not re.fullmatch(r"[A-Z]{3,20}", row["answer"]):
                raise ValueError(f"Invalid grid answer {key}")
            if row["chapter"] not in SOURCE_CHAPTERS or SOURCE_CHAPTERS[row["chapter"]] != row["source_pool"]:
                raise ValueError(f"Source/chapter mismatch {key}")
            if len(row["original_word_sense"]) < 26 or len(row["player_experience_note"]) < 34:
                raise ValueError(f"Thin word-specific editorial comment {key}")
            if not re.fullmatch("[A-Z_]{4,35}", row["caution_code"]):
                raise ValueError(f"Invalid review label {key}")
            if not row["topic_context_reference"].startswith("https://"):
                raise ValueError(f"Missing topic source link {key}")
            if row["evidence_level"] != EVIDENCE_LEVEL:
                raise ValueError(f"Incorrect source verification claim {key}")
            if row["candidate_human_decision"] or row["owner_public_approval"]:
                raise ValueError(f"Unapproved draft claims signoff {key}")
            if row["source_rights"] != SOURCE_RIGHTS:
                raise ValueError(f"Source rights incorrectly marked cleared {key}")
            output[key] = row
    return output


def eligible(answer, tier, grid_size, allowed):
    return (tier in allowed
            and len(answer) < grid_size
            and answer != answer[::-1])


def build(root=ROOT):
    all_previous, _, previous_receipt = build_ws65e(root)
    first80, previous_samples, first_receipt = build_ws65f(root)
    second82 = read_82(root)
    previous_by_key = {(r["source_pool"], r["answer"]): r for r in all_previous}
    if len(previous_by_key) != len(all_previous):
        raise ValueError("Prior candidate IDs not unique")
    first_keys = {(r["source_pool"], r["answer"]) for r in first80}
    if len(first_keys) != len(first80) or len(first_keys) != 80:
        raise ValueError("WS65-F shortlist identity drift")
    outstanding = {
        key for key, row in previous_by_key.items()
        if row["original_preflight_lane"] == "PROMISING"
        and row["ws65c_evidence_status"] == "NOT_IN_THIS_EVIDENCE_BATCH"
        and row["ws65d_evidence_status"] == "NOT_IN_THIS_PASS"
        and row["ws65e_evidence_status"] == "NOT_IN_THIS_PASS"
        and key not in first_keys
    }
    if set(second82) != outstanding:
        raise ValueError(
            f"WS65-G MUST cover exactly the remaining 82 unreviewed promising rows; "
            f"missing={sorted(outstanding-set(second82))}; extra={sorted(set(second82)-outstanding)}"
        )
    if len(second82) != first_receipt["remaining_promising_without_individual_context"]:
        raise ValueError("WS65-F promised remainder is not reconciled")

    out = []
    counts = Counter()
    caution_counts = Counter()
    board_counts = defaultdict(Counter)
    grouped = defaultdict(list)
    all_162_by_book = defaultdict(list)
    for item in first80:
        all_162_by_book[item["chapter"]].append(item["answer"])
    for key in sorted(second82):
        entry = second82[key]
        original = previous_by_key[key]
        if original["original_chapter"] != entry["chapter"]:
            raise ValueError(f"Changed source chapter without decision {key}")
        tier = original["preflight_reason"]  # only here to affirm the original row exists
        if not tier:
            raise ValueError(f"Missing original preflight context {key}")
        # WS65-E's original row retains preflight-related tier in upstream
        # metadata; use the checked initial WS65-B tier directly via a
        # read-only lookup, never silently upgrade a source word.
        counts[entry["chapter"]] += 1
        caution_counts[entry["caution_code"]] += 1
        grouped[entry["chapter"]].append(entry["answer"])
        all_162_by_book[entry["chapter"]].append(entry["answer"])

    # Recover exact conservative prior tier from WS65-B so "advanced" does
    # not accidentally appear in relaxed page counts.
    from ws65b_editorial_triage import build as build_preflight
    preflight, _ = build_preflight(root)
    by_tier = {(r["source_pool"], r["answer"]): r["suggested_tier"] for r in preflight}
    if len(by_tier) != len(preflight):
        raise ValueError("Source editorial preflight tier map not unique")

    for key in sorted(second82):
        entry = second82[key]
        original = previous_by_key[key]
        tier = by_tier[key]
        if tier not in TIERS:
            raise ValueError(f"Bad historical tier for {key}")
        checks = {size: eligible(entry["answer"], tier, *spec) for size, spec in BOARD_SPECS.items()}
        for size, can in checks.items():
            if can:
                board_counts[entry["chapter"]][size] += 1
        out.append({
            "source_pool": entry["source_pool"],
            "source_id": original["source_id"],
            "answer": entry["answer"],
            "display": original["display"],
            "chapter": entry["chapter"],
            "existing_canonical_id": original["existing_canonical_id"],
            "prior_suggested_tier": tier,
            "original_word_sense": entry["original_word_sense"],
            "player_experience_note": entry["player_experience_note"],
            "caution_code": entry["caution_code"],
            "topic_context_reference": entry["topic_context_reference"],
            "research_type": EVIDENCE_LEVEL,
            "board_12_eligibility_only": str(checks["relaxed12"]),
            "board_15_eligibility_only": str(checks["classic15"]),
            "board_20_eligibility_only": str(checks["expert20"]),
            "source_rights": SOURCE_RIGHTS,
            "candidate_human_decision": "",
            "owner_public_approval": "",
        })
    by_normalized = defaultdict(set)
    for chapter, answers in all_162_by_book.items():
        for word in answers:
            by_normalized[word].add(chapter)
    cross_book_reuse = [
        {"answer": word, "chapters": sorted(chapters)}
        for word, chapters in sorted(by_normalized.items())
        if len(chapters) > 1
    ]
    substrings = []
    for chapter, answers in sorted(all_162_by_book.items()):
        unique = sorted(set(answers))
        for i, left in enumerate(unique):
            for right in unique[i + 1:]:
                if left in right or right in left:
                    substrings.append({"chapter": chapter, "shorter": min([left, right], key=len),
                                       "longer": max([left, right], key=len)})
    flagged_groups = []
    for item in EDITORIAL_PAIR_GROUPS:
        chapter_set = set(all_162_by_book[item["chapter"]])
        if set(item["answers"]) - chapter_set:
            raise ValueError(f"Curated thematic group references missing source word: {item}")
        flagged_groups.append(item)

    if len(out) + len(first80) != first_receipt["original_unreferenced_promising"]:
        raise ValueError("Previous and current shortlists do not account for all promising source proposals")
    if previous_receipt["all_source_proposals_unchanged"] != 267:
        raise ValueError("Historical candidate inventory changed")
    r = {
        "format": "WS65G_ALL_PROMISING_PLAYER_CONTEXT_READONLY_V1",
        "status": "ALL_267_CANDIDATES_CONTEXT_REVIEWED_AT_MIXED_EVIDENCE_LEVELS_ZERO_APPROVALS",
        "total_source_proposal_occurrences": len(all_previous),
        "ws65cde_word_specific_source_notes": previous_receipt["cumulative_research_references"],
        "ws65f_topic_linked_player_glosses": len(first80),
        "ws65g_new_topic_linked_player_glosses": len(second82),
        "promising_without_player_context_after_ws65g": 0,
        "unique_selected_answer_spellings_ws65f_ws65g": len(by_normalized),
        "cross_book_same_spelling_reuse": cross_book_reuse,
        "within_chapter_substring_pairs": substrings,
        "potential_concept_overlap_groups_for_editor": flagged_groups,
        "ws65g_by_chapter": dict(sorted(counts.items())),
        "ws65g_editorial_caution_codes": dict(sorted(caution_counts.items())),
        "ws65g_mechanical_eligibility_only_by_chapter": {k: dict(sorted(v.items())) for k,v in sorted(board_counts.items())},
        "original_potential_new_answer_spellings_not_approved": previous_receipt["candidate_new_spellings_not_reviewed_or_approved"],
        "unchanged_ws65f_sample_page_concepts": len({r["sample_page_id"] for r in previous_samples}),
        "source_input_sha256": hashlib.sha256((root / INPUT).read_bytes()).hexdigest(),
        "prior_ws65f_input_sha256": first_receipt["shortlist_file_sha256"],
        "new_human_editor_approvals": 0,
        "new_owner_public_approvals": 0,
        "new_android_playable_answers": 0,
        "warnings": [
            "Full context coverage is not 267 independently sourced definitions: 105 have specific source-linked notes, 162 have editorial glosses/topic URLs. No live URL verification happens in CI.",
            "Substrings and shared spellings are page-design signals, not proof of duplicate concepts or bad words.",
            "Mechanical grid eligibility here tests word length, original tier, and palindromes ONLY; no 12/15/20 board actually generated or placed.",
            "All releases need rights, quality, manual owner signoff and new pack/page IDs; previous frozen games and saves untouched.",
        ],
    }
    return out, r


def main(argv=None):
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--repo-root", type=Path, default=ROOT)
    p.add_argument("--out-dir", type=Path, default=Path("qa-ws65g-editorial-coverage"))
    a = p.parse_args(argv)
    try:
        rows, r = build(a.repo_root)
        a.out_dir.mkdir(parents=True, exist_ok=True)
        with (a.out_dir / "unsigned-remaining-82.csv").open("w", newline="", encoding="utf-8") as f:
            writer = csv.DictWriter(f, fieldnames=OUTPUT_HEADERS, lineterminator="\n")
            writer.writeheader()
            writer.writerows(rows)
        (a.out_dir / "editorial-coverage-and-page-risk.json").write_text(
            json.dumps(r, indent=2, sort_keys=True) + "\n", encoding="utf-8")
        (a.out_dir / "README.md").write_text(
            "# WS65-G — complete topic-context editor queue; no approvals\n\n"
            f"{r['ws65g_new_topic_linked_player_glosses']} new source-backed candidate identities with original editorial senses; "
            f"{r['promising_without_player_context_after_ws65g']} promising entries still without topic-context notes.\n"
            "These are NOT independently dictionary-verified definitions and NOT approved words. "
            "Substring/cross-book overlaps are a review queue; no actual game boards were generated.\n",
            encoding="utf-8")
        print("WS65G_READONLY_OK:",
              r["ws65g_new_topic_linked_player_glosses"], "new note records;",
              r["unique_selected_answer_spellings_ws65f_ws65g"], "distinct selected spellings;",
              len(r["cross_book_same_spelling_reuse"]), "cross-book reuse flags;",
              len(r["within_chapter_substring_pairs"]), "substring pairs; zero approvals")
        return 0
    except (ValueError, OSError, KeyError, csv.Error) as exc:
        print("WS65G_READONLY_FAIL:", exc, file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
