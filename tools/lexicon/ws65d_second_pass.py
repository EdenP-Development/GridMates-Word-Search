#!/usr/bin/env python3
"""WS65-D second-pass source and chapter review; not a word approval operation.

The script combines WS64, WS65-A, WS65-B and WS65-C sources in read-only mode
and places new evidence suggestions in separate review columns. No app data,
old puzzles, saved boards, owner decisions or word status is modified.
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
from ws65c_source_evidence import build as build_ws65c

EVIDENCE_PATH = Path("data/lexicon/intake/ws65d-second-pass-evidence-v1.tsv")
EVIDENCE_HEADERS = [
    "source_pool", "answer", "evidence_action", "suggested_tier",
    "suggested_chapter", "source_url", "source_supported_sense", "editorial_reason",
    "source_rights", "qa_editor_decision", "owner_decision",
]
ACTION_CODES = {
    "REAFFIRM_MOVE", "RECONSIDER_MOVE", "SPECIALIST_SENSE_CONFIRMED",
    "FLOWER_SENSE_CONFIRMED", "CLARIFY_SENSE", "TONE_HOLD", "CONTEXT_HOLD",
}
TIERS = {"familiar", "intermediate", "advanced"}
OUTPUT_FIELDS = [
    "source_pool", "source_id", "answer", "display", "original_chapter",
    "existing_canonical_id", "preflight_lane", "preflight_reason",
    "ws65c_evidence_status", "ws65d_evidence_status", "ws65d_action",
    "ws65d_source_url", "ws65d_source_supported_sense",
    "ws65d_editorial_reason", "ws65d_suggested_chapter",
    "ws65d_suggested_tier", "ws65d_rights_status",
    "qa_editor_decision", "owner_decision",
]


def read_second_pass(root=ROOT):
    with (root / EVIDENCE_PATH).open(encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f, delimiter="\t")
        if reader.fieldnames != EVIDENCE_HEADERS:
            raise ValueError("WS65-D second-pass evidence column drift")
        result = {}
        for item in reader:
            key = (item["source_pool"], item["answer"])
            if key in result:
                raise ValueError(f"Duplicate second-pass record {key}")
            if key[0] not in {"WS64", "WS65"} or not re.fullmatch(r"[A-Z]{3,20}", key[1]):
                raise ValueError(f"Invalid source or answer key {key}")
            action = item["evidence_action"]
            if action not in ACTION_CODES:
                raise ValueError(f"Unrecognized second-pass action {key}: {action}")
            if action in ("REAFFIRM_MOVE", "RECONSIDER_MOVE") and not item["suggested_chapter"]:
                raise ValueError(f"Chapter move action requires explicit proposed chapter {key}")
            if item["suggested_tier"] and item["suggested_tier"] not in TIERS:
                raise ValueError(f"Bad suggested tier {key}")
            if not item["source_url"].startswith("https://"):
                raise ValueError(f"No verifiable source URL for {key}")
            if len(item["source_supported_sense"]) < 45 or len(item["editorial_reason"]) < 48:
                raise ValueError(f"Sense/rationale is too vague for {key}")
            if item["source_rights"] != "NOT_CLEARED":
                raise ValueError(f"Evidence cannot grant commercial source clearance for {key}")
            if item["qa_editor_decision"] or item["owner_decision"]:
                raise ValueError(f"Approval cannot be injected into unsigned source evidence {key}")
            result[key] = item
    if not result:
        raise ValueError("No WS65-D records")
    return result


def build(root=ROOT):
    prior_rows, prior_receipt = build_ws65c(root)
    second = read_second_pass(root)
    prior_keys = {(r["source_pool"], r["answer"]) for r in prior_rows}
    if len(prior_keys) != len(prior_rows):
        raise ValueError("Prior source keys are duplicated")
    missing = set(second) - prior_keys
    if missing:
        raise ValueError(f"Unrecognized second-pass source key: {sorted(missing)}")
    indexed = { (r["source_pool"], r["answer"]): r for r in prior_rows }
    for key, evidence in second.items():
        old = indexed[key]
        if old["source_evidence_status"] != "NOT_IN_THIS_EVIDENCE_BATCH":
            raise ValueError(f"Second-pass record must not overwrite prior evidence: {key}")
        if old["preflight_lane"] not in {"HOLD", "MOVE"}:
            raise ValueError(f"Second pass is for outstanding HOLD/MOVE items only: {key}")
        if old["preflight_lane"] == "MOVE" and evidence["evidence_action"] not in {"REAFFIRM_MOVE", "RECONSIDER_MOVE"}:
            raise ValueError(f"Move review must say reaffirm/reconsider: {key}")
        if old["preflight_lane"] == "HOLD" and evidence["evidence_action"] in {"REAFFIRM_MOVE", "RECONSIDER_MOVE"}:
            raise ValueError(f"Hold review cannot silently change original lane: {key}")

    rows = []
    counts = Counter()
    actions = Counter()
    by_source = Counter()
    reviewed_moves = []
    for old in prior_rows:
        key = (old["source_pool"], old["answer"])
        ev = second.get(key)
        if ev:
            counts[old["preflight_lane"]] += 1
            actions[ev["evidence_action"]] += 1
            by_source[key[0]] += 1
            if old["preflight_lane"] == "MOVE":
                reviewed_moves.append(key[1])
        if old["qa_editor_decision"] or old["owner_decision"]:
            raise ValueError(f"Unexpected approved row in prior source {key}")
        rows.append({
            "source_pool": old["source_pool"],
            "source_id": old["source_id"],
            "answer": old["answer"],
            "display": old["display"],
            "original_chapter": old["original_chapter"],
            "existing_canonical_id": old["existing_canonical_id"],
            "preflight_lane": old["preflight_lane"],
            "preflight_reason": old["preflight_reason"],
            "ws65c_evidence_status": old["source_evidence_status"],
            "ws65d_evidence_status": "SOURCE_REFERENCE_CHECKED" if ev else "NOT_IN_THIS_PASS",
            "ws65d_action": ev["evidence_action"] if ev else "",
            "ws65d_source_url": ev["source_url"] if ev else "",
            "ws65d_source_supported_sense": ev["source_supported_sense"] if ev else "",
            "ws65d_editorial_reason": ev["editorial_reason"] if ev else "",
            "ws65d_suggested_chapter": ev["suggested_chapter"] if ev else "",
            "ws65d_suggested_tier": ev["suggested_tier"] if ev else "",
            "ws65d_rights_status": ev["source_rights"] if ev else "NOT_CLEARED",
            "qa_editor_decision": "",
            "owner_decision": "",
        })

    old_lanes = prior_receipt["baseline_preflight_lanes_unchanged"]
    earlier = prior_receipt["source_checked_by_preflight_lane"]
    missing_per_lane = {
        lane: old_lanes.get(lane, 0) - earlier.get(lane, 0) - counts.get(lane, 0)
        for lane in old_lanes
    }
    if any(value < 0 for value in missing_per_lane.values()):
        raise ValueError("Impossible negative remaining evidence counts")
    receipt = {
        "format": "WS65D_INDEPENDENT_READONLY_SECOND_PASS_V1",
        "status": "UNSIGNED_SOURCE_EVIDENCE_AND_EDITORIAL_SUGGESTIONS_ONLY",
        "all_original_proposal_rows": len(rows),
        "prior_ws65c_evidence_rows": prior_receipt["source_checked_spelling_senses"],
        "new_ws65d_source_referenced_rows": len(second),
        "combined_evidence_rows": prior_receipt["source_checked_spelling_senses"] + len(second),
        "source_rows_without_individual_evidence": len(rows) - prior_receipt["source_checked_spelling_senses"] - len(second),
        "ws65d_by_original_lane": dict(sorted(counts.items())),
        "ws65d_actions": dict(sorted(actions.items())),
        "ws65d_by_source": dict(sorted(by_source.items())),
        "original_preflight_lanes": old_lanes,
        "remaining_unchecked_by_preflight_lane": dict(sorted(missing_per_lane.items())),
        "original_26_moves_all_have_source_references":
            (missing_per_lane.get("MOVE", 0) == 0),
        "move_actions_reviewed": sorted(reviewed_moves),
        "candidate_new_answer_spellings_not_approved":
            prior_receipt["possible_new_distinct_canonical_total_not_approved"],
        "evidence_file_sha256": hashlib.sha256((root / EVIDENCE_PATH).read_bytes()).hexdigest(),
        "prior_ws65c_evidence_sha256": prior_receipt["evidence_file_sha256"],
        "new_qa_editor_approvals": 0,
        "new_owner_public_approvals": 0,
        "new_playable_android_words": 0,
        "notes": [
            "These references support vocabulary senses and proposed chapter/tier judgments, not ratings from real players or formal publication decisions.",
            "CI checks the local evidence ledger and source identity; it cannot certify that linked external source pages remain online or satisfy commercial-use rights.",
            "RECONSIDER_MOVE explicitly corrects one earlier overgeneralized recommendation without editing that historical source or automatically moving a word.",
            "No existing V1-V5 puzzle, game/save snapshot, authoring pack, Android app or artwork was modified.",
        ],
    }
    if len(rows) != prior_receipt["full_source_rows"]:
        raise ValueError("New evidence overlay changed original candidate cardinality")
    return rows, receipt


def main(argv=None):
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--repo-root", type=Path, default=ROOT)
    p.add_argument("--out-dir", type=Path, default=Path("qa-ws65d-evidence"))
    a = p.parse_args(argv)
    try:
        rows, receipt = build(a.repo_root)
        a.out_dir.mkdir(parents=True, exist_ok=True)
        with (a.out_dir / "unsigned-second-pass-review.csv").open(
                "w", newline="", encoding="utf-8") as f:
            writer = csv.DictWriter(f, fieldnames=OUTPUT_FIELDS, lineterminator="\n")
            writer.writeheader()
            writer.writerows(rows)
        (a.out_dir / "second-pass-evidence-receipt.json").write_text(
            json.dumps(receipt, sort_keys=True, indent=2) + "\n", encoding="utf-8")
        (a.out_dir / "README.md").write_text(
            "# WS65-D unsigned second-pass evidence\n\n"
            f"Source proposals {receipt['all_original_proposal_rows']}, new evidence "
            f"{receipt['new_ws65d_source_referenced_rows']}, total evidence "
            f"{receipt['combined_evidence_rows']}, remaining without individual evidence "
            f"{receipt['source_rows_without_individual_evidence']}.\n\n"
            "All editor/owner decisions remain blank; rights NOT_CLEARED.\n",
            encoding="utf-8")
        print("WS65D_SECOND_PASS_OK:", receipt["new_ws65d_source_referenced_rows"],
              "new source-linked reviews;", receipt["combined_evidence_rows"],
              "combined evidence rows;", receipt["remaining_unchecked_by_preflight_lane"],
              "unchecked; 0 approvals")
        return 0
    except (ValueError, OSError, KeyError, csv.Error) as e:
        print("WS65D_SECOND_PASS_FAIL:", e, file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
