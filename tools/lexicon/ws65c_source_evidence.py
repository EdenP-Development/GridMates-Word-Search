#!/usr/bin/env python3
"""WS65-C: source-referenced editorial evidence overlay, read-only and unsigned.

References are editorially checked excerpts/sense evidence, NOT automatically
fetched/tested by CI, not license clearance, not QA/public approval, and not
authorization to modify old puzzles, V5 saves, or the Android package.
"""
import argparse
from collections import Counter
import csv
import hashlib
import json
from pathlib import Path
import re
import sys

from ws63_quality_audit import ROOT
from ws65b_editorial_triage import build as build_preflight

EVIDENCE_PATH = Path("data/lexicon/intake/ws65c-source-evidence-v1.tsv")
EVIDENCE_HEADERS = [
    "source_pool", "answer", "sense_confirmed", "source_checked",
    "source_label", "editorial_recommendation", "target_chapter",
    "suggested_tier", "review_rationale", "rights_status",
    "qa_editor_decision", "owner_decision",
]
EVIDENCE_RECOMMENDATIONS = {
    "KEEP_SPECIALIST", "KEEP_WITH_SENSE_NOTE", "MOVE_CHAPTER",
    "HOLD_CONCEPT_OVERLAP", "HOLD_FAMILY_TONE",
}
TIERS = {"familiar", "intermediate", "advanced"}
OUTPUT_HEADERS = [
    "source_pool", "source_id", "answer", "display", "original_chapter",
    "original_page", "original_tier", "content_group", "existing_canonical_id",
    "preflight_lane", "preflight_reason", "preflight_suggested_tier",
    "preflight_suggested_chapter", "mechanics_flags",
    "source_evidence_status", "verified_word_sense", "source_url",
    "source_label", "editorial_recommendation", "candidate_target_chapter",
    "candidate_tier", "editorial_rationale", "source_rights_status",
    "qa_editor_decision", "owner_decision", "owner_reviewer",
]


def load_evidence(root=ROOT):
    with (root / EVIDENCE_PATH).open(encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f, delimiter="\t")
        if reader.fieldnames != EVIDENCE_HEADERS:
            raise ValueError("WS65-C evidence schema drift")
        result = {}
        for row in reader:
            key = (row["source_pool"], row["answer"])
            if key in result:
                raise ValueError(f"Duplicate verified evidence row {key}")
            if row["source_pool"] not in {"WS64", "WS65"}:
                raise ValueError(f"Unknown evidence source pool {key}")
            if not re.fullmatch(r"[A-Z]{3,20}", row["answer"]):
                raise ValueError(f"Invalid evidence answer {key}")
            if len(row["sense_confirmed"]) < 35 or len(row["review_rationale"]) < 35:
                raise ValueError(f"Insufficient specific sense/rationale {key}")
            if not row["source_checked"].startswith("https://") or not row["source_label"]:
                raise ValueError(f"Missing source citation {key}")
            if row["editorial_recommendation"] not in EVIDENCE_RECOMMENDATIONS:
                raise ValueError(f"Bad editorial recommendation {key}")
            if row["editorial_recommendation"] == "MOVE_CHAPTER" and not row["target_chapter"]:
                raise ValueError(f"Move has no chapter destination {key}")
            if row["suggested_tier"] and row["suggested_tier"] not in TIERS:
                raise ValueError(f"Bad proposed tier {key}")
            if row["rights_status"] != "NOT_CLEARED":
                raise ValueError(f"Evidence implies unreviewed source-rights clearance {key}")
            if row["qa_editor_decision"] or row["owner_decision"]:
                raise ValueError(f"Signed approval is forbidden in read-only WS65-C {key}")
            result[key] = row
    if not result:
        raise ValueError("Empty evidence set")
    return result


def build(root=ROOT):
    preflight_rows, older = build_preflight(root)
    evidence = load_evidence(root)
    known = {(r["source_pool"], r["answer"]) for r in preflight_rows}
    extra = set(evidence) - known
    if extra:
        raise ValueError(f"Evidence references missing source candidate: {sorted(extra)}")
    if len(known) != len(preflight_rows):
        raise ValueError("Original preflight has duplicated candidate source keys")

    combined = []
    editorial_counts, evidence_by_lane, original_lanes = Counter(), Counter(), Counter()
    holds_remaining = Counter()
    covered_canonical = Counter()
    for row in preflight_rows:
        key = (row["source_pool"], row["answer"])
        proof = evidence.get(key)
        if any(row[x] for x in ("owner_decision", "qa_editor_decision", "owner_reviewer")):
            raise ValueError(f"Earlier source has unexpected approval {key}")
        original_lanes[row["preflight_lane"]] += 1
        if proof:
            recommendation = proof["editorial_recommendation"]
            if row["preflight_lane"] == "MOVE" and recommendation != "MOVE_CHAPTER":
                # Explicitly changing a previous MOVE recommendation needs a new
                # review discussion, not silent erasure of its concern.
                raise ValueError(f"WS65-C incorrectly overrides a MOVE preflight {key}")
            evidence_by_lane[row["preflight_lane"]] += 1
            editorial_counts[recommendation] += 1
            covered_canonical["existing_QA_canonical" if row["existing_canonical_id"]
                              else "potential_new_not_approved"] += 1
        else:
            holds_remaining[row["preflight_lane"]] += 1
        combined.append({
            "source_pool": row["source_pool"],
            "source_id": row["source_id"],
            "answer": row["answer"], "display": row["display"],
            "original_chapter": row["original_chapter"],
            "original_page": row["original_page"],
            "original_tier": row["original_tier"],
            "content_group": row["content_group"],
            "existing_canonical_id": row["existing_canonical_id"],
            "preflight_lane": row["preflight_lane"],
            "preflight_reason": row["reason"],
            "preflight_suggested_tier": row["suggested_tier"],
            "preflight_suggested_chapter": row["suggested_chapter"],
            "mechanics_flags": row["mechanics_flags"],
            "source_evidence_status": "EXTERNAL_CONTEXT_CHECKED" if proof
                                      else "NOT_IN_THIS_EVIDENCE_BATCH",
            "verified_word_sense": proof["sense_confirmed"] if proof else "",
            "source_url": proof["source_checked"] if proof else "",
            "source_label": proof["source_label"] if proof else "",
            "editorial_recommendation": proof["editorial_recommendation"] if proof
                                        else "NOT_YET_FACT_CHECKED",
            "candidate_target_chapter": proof["target_chapter"] if proof
                                         else row["suggested_chapter"],
            "candidate_tier": proof["suggested_tier"] if proof
                               else row["suggested_tier"],
            "editorial_rationale": proof["review_rationale"] if proof else "",
            "source_rights_status": proof["rights_status"] if proof
                                    else "NOT_CLEARED",
            "qa_editor_decision": "", "owner_decision": "", "owner_reviewer": "",
        })
    receipt = {
        "format": "WS65C_READ_ONLY_SOURCE_EVIDENCE_LEDGER_V1",
        "status": "WORD_SENSE_EVIDENCE_ONLY_NOT_LICENSED_OR_OWNER_APPROVED",
        "full_source_rows": len(combined),
        "source_checked_spelling_senses": len(evidence),
        "still_without_individual_evidence": len(combined) - len(evidence),
        "source_checked_by_preflight_lane": dict(sorted(evidence_by_lane.items())),
        "remaining_without_individual_evidence_by_lane": dict(sorted(holds_remaining.items())),
        "baseline_preflight_lanes_unchanged": dict(sorted(original_lanes.items())),
        "evidence_editorial_recommendations": dict(sorted(editorial_counts.items())),
        "evidence_existing_id_vs_new": dict(sorted(covered_canonical.items())),
        "possible_new_distinct_canonical_total_not_approved":
            older["candidate_new_vs_en_and_fall_distinct_combined_not_approved"],
        "evidence_file_sha256": hashlib.sha256((root / EVIDENCE_PATH).read_bytes()).hexdigest(),
        "preflight_annotation_sha256": older["annotation_input_sha256"],
        "ws65_a_source_sha256": older["ws65_input_sha256"],
        "historical_manuscript_sha256": older["original_manuscript_sha256"],
        "new_qa_editor_signoffs": 0,
        "new_owner_public_approvals": 0,
        "new_playable_android_answers": 0,
        "limits": [
            "Source URLs are editorial reference evidence checked when these notes were written; CI checks their presence and integrity but does NOT retrieve or verify live source contents.",
            "A source confirming a spelling/sense is not a validated frequency or a green light for beginner familiarity, rights, a chapter placement, a future release, or an approved word.",
            "No frozen-page generation, replay, human device QA, or public approval in this stage.",
            "Prior WS65-B preliminary lanes remain unchanged; this report overlays evidence, not signed editorial decisions.",
        ],
    }
    if len(combined) != older["total_rows"]:
        raise ValueError("Evidence overlay lost or added source rows")
    return combined, receipt


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repo-root", type=Path, default=ROOT)
    parser.add_argument("--out-dir", type=Path, default=Path("qa-ws65c-evidence"))
    args = parser.parse_args(argv)
    try:
        rows, receipt = build(args.repo_root)
        args.out_dir.mkdir(parents=True, exist_ok=True)
        with (args.out_dir / "source-evidence-ledger.csv").open(
                "w", encoding="utf-8", newline="") as f:
            writer = csv.DictWriter(f, fieldnames=OUTPUT_HEADERS, lineterminator="\n")
            writer.writeheader()
            writer.writerows(rows)
        (args.out_dir / "source-evidence-receipt.json").write_text(
            json.dumps(receipt, sort_keys=True, indent=2) + "\n", encoding="utf-8")
        (args.out_dir / "README.md").write_text(
            "# WS65-C — source-backed word sense, still unsigned\n\n"
            f"Entire proposal pool: {receipt['full_source_rows']}; "
            f"source-checked sense entries: {receipt['source_checked_spelling_senses']}; "
            f"not separately checked: {receipt['still_without_individual_evidence']}.\n\n"
            "No release rights or editor/owner approval is inferred. This does not "
            "alter historical QA content, edition catalogs, Android puzzles, or saves.\n",
            encoding="utf-8")
        print("WS65C_EVIDENCE_OK:", receipt["source_checked_spelling_senses"],
              "evidence rows overlaying", receipt["full_source_rows"],
              "proposals; 0 approvals, 0 runtime changes")
        return 0
    except (KeyError, ValueError, OSError, csv.Error) as exc:
        print("WS65C_EVIDENCE_FAIL:", exc, file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
