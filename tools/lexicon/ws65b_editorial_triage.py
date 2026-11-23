#!/usr/bin/env python3
"""WS65-B: reproducible, read-only editorial *preflight* across two draft pools.

Not a human/editorial decision, source-rights clearance, vocabulary import,
puzzle generation, Android release, owner approval, or a change to old boards.
"""
import argparse
from collections import Counter
import csv
import hashlib
import json
from pathlib import Path
import sys

from ws63_quality_audit import ROOT
from ws64_woodland_review_queue import build as woodland_build
from ws65_specialist_review_audit import load_rows as load_ws65_rows, audit as audit_ws65

ANNOTATIONS = Path("data/lexicon/intake/ws65b-editorial-annotations-v1.tsv")
ANNOTATION_HEADERS = (
    "source_pool", "answer", "preflight_lane", "preflight_reason",
    "suggested_tier", "suggested_chapter", "context_reference",
)
LANES = {"PROMISING", "HOLD", "MOVE"}
TIERS = {"familiar", "intermediate", "advanced"}
REVIEW_HEADERS = (
    "source_pool", "source_id", "answer", "display", "original_chapter",
    "original_page", "original_tier", "content_group",
    "original_source_status", "existing_canonical_id", "prior_woodland_reference",
    "preflight_lane", "reason", "suggested_tier", "suggested_chapter",
    "context_reference", "mechanics_flags", "needs_tier_change",
    "qa_editor_decision", "qa_editor_reviewer", "owner_decision",
    "owner_reviewer", "owner_reviewed_on",
)
DEFAULTS = {
    "worlds": ("PROMISING", "Solar-System object/class; verify definition and reader familiarity, not automatic acceptance.", ""),
    "moons": ("HOLD", "Named satellite is not automatically familiar; verify exact planet, spelling and chapter sense.", ""),
    "small-bodies": ("HOLD", "Verify object/class and distinguish familiar spelling from specialist use.", ""),
    "orbital-features": ("HOLD", "Planetary science may be valid but often belongs in an advanced companion chapter.", ""),
    "annual-and-bedding": ("PROMISING", "Flower-border candidate; confirm ornamental use, display, US spelling and reader familiarity.", ""),
    "perennial-border": ("HOLD", "Verify plant/common name and whether the proposed tier suits relaxed readers.", ""),
    "flower-structure": ("MOVE", "Technical flower anatomy or garden-care term usually fits an educational chapter better.", "Flower Anatomy / Botany"),
}
WOODLAND_DEFAULT = (
    "PROMISING", "Original Woodland manuscript proposal; assess its exact bird/stream/forest/clearing sense and source rights.", ""
)


def read_annotations(root=ROOT):
    with (root / ANNOTATIONS).open(encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f, delimiter="\t")
        if reader.fieldnames != list(ANNOTATION_HEADERS):
            raise ValueError("WS65-B annotation schema changed")
        result = {}
        for item in reader:
            key = (item["source_pool"], item["answer"])
            if key in result:
                raise ValueError(f"Duplicate preflight annotation {key}")
            if item["source_pool"] not in {"WS64", "WS65"}:
                raise ValueError(f"Unknown source pool {key}")
            if item["preflight_lane"] not in LANES:
                raise ValueError(f"Unknown preflight lane {key}")
            if item["suggested_tier"] and item["suggested_tier"] not in TIERS:
                raise ValueError(f"Invalid recommended tier {key}")
            if not item["preflight_reason"] or len(item["preflight_reason"]) < 16:
                raise ValueError(f"Missing meaningful rationale {key}")
            if item["preflight_lane"] == "MOVE" and not item["suggested_chapter"]:
                raise ValueError(f"Unspecified target chapter {key}")
            if item["context_reference"] and not item["context_reference"].startswith("https://"):
                raise ValueError(f"Bad reference URL {key}")
            result[key] = item
    if not result:
        raise ValueError("No human-authored preflight notes")
    return result


def build(root=ROOT):
    # Both previous milestones perform their own strict canonical reconciliation.
    woodland, woodland_report = woodland_build(root)
    specialist = load_ws65_rows(root)
    ws65_report = audit_ws65(specialist, root)
    notes = read_annotations(root)

    source = []
    for item in woodland:
        if item["owner_decision"] or item["owner_reviewer"] or item["owner_reviewed_on"]:
            raise ValueError("Woodland source already contains unauthorized owner decision")
        source.append({
            "source_pool": "WS64", "source_id": item["candidate_id"],
            "answer": item["answer"], "display": item["display"],
            "original_chapter": item["source_chapter"],
            "original_page": item["source_page"], "original_tier": item["proposed_tier"],
            "content_group": "woodland-manuscript",
            "original_source_status": item["source_provenance"],
            "existing_canonical_id": item["canonical_existing_word_id"],
            "prior_woodland_reference": item["source_manuscript"],
            "mechanics_flags": item["mechanics_flags"],
        })
    for item in specialist:
        if item["owner_decision"] or item["editor_decision"]:
            raise ValueError("Specialist source already contains review decisions")
        source.append({
            "source_pool": "WS65", "source_id": item["candidate_id"],
            "answer": item["answer"], "display": item["display"],
            "original_chapter": item["chapter_title"],
            "original_page": "", "original_tier": item["proposed_tier"],
            "content_group": item["content_group"],
            "original_source_status": item["review_status"],
            "existing_canonical_id": item["canonical_existing_word_id"],
            "prior_woodland_reference": item["prior_woodland_manuscript"],
            "mechanics_flags": item["mechanics_flags"],
        })

    unique_keys = {(x["source_pool"], x["answer"]) for x in source}
    if len(unique_keys) != len(source):
        raise ValueError("Source proposals lost uniqueness within pools")
    unmatched = set(notes) - unique_keys
    if unmatched:
        raise ValueError(f"Preflight annotation references missing source answers: {sorted(unmatched)}")

    output = []
    lanes = Counter()
    chapter_lanes = Counter()
    tier_changes = Counter()
    source_lanes = Counter()
    new_possible = set()
    for item in source:
        key = (item["source_pool"], item["answer"])
        if item["source_pool"] == "WS64":
            default = WOODLAND_DEFAULT
        else:
            default = DEFAULTS[item["content_group"]]
        note = notes.get(key)
        lane = note["preflight_lane"] if note else default[0]
        reason = note["preflight_reason"] if note else default[1]
        target = note["suggested_chapter"] if note else default[2]
        tier = note["suggested_tier"] if note and note["suggested_tier"] else item["original_tier"]
        link = note["context_reference"] if note else ""
        needs_tier_change = tier != item["original_tier"]
        out = {
            **item,
            "preflight_lane": lane, "reason": reason,
            "suggested_tier": tier, "suggested_chapter": target,
            "context_reference": link,
            "needs_tier_change": "YES" if needs_tier_change else "",
            "qa_editor_decision": "", "qa_editor_reviewer": "",
            "owner_decision": "", "owner_reviewer": "", "owner_reviewed_on": "",
        }
        output.append(out)
        lanes[lane] += 1
        source_lanes[f"{item['source_pool']}:{lane}"] += 1
        chapter_lanes[f"{item['original_chapter']}:{lane}"] += 1
        if needs_tier_change:
            tier_changes[item["source_pool"]] += 1
        if not item["existing_canonical_id"]:
            new_possible.add(item["answer"])

    receipt = {
        "format": "WS65B_READ_ONLY_EDITORIAL_PREFLIGHT_V1",
        "status": "UNSIGNED_EDITORIAL_SUGGESTIONS_NO_QA_EDITOR_OR_OWNER_APPROVAL",
        "woodland_drafts": len(woodland),
        "specialist_drafts": len(specialist),
        "total_rows": len(output),
        "distinct_answer_spellings_across_two_pools": len({x["answer"] for x in output}),
        "candidate_new_vs_en_and_fall_distinct_combined_not_approved": len(new_possible),
        "ws64_candidate_new": woodland_report["potential_new_canonical_after_review"],
        "ws65_candidate_new": ws65_report["not_in_english_or_fall_catalog"],
        "review_annotations": len(notes),
        "preflight_lanes": dict(sorted(lanes.items())),
        "preflight_lanes_by_source": dict(sorted(source_lanes.items())),
        "preflight_lanes_by_chapter": dict(sorted(chapter_lanes.items())),
        "tier_changes_suggested_by_pool": dict(sorted(tier_changes.items())),
        "annotation_input_sha256": hashlib.sha256((root / ANNOTATIONS).read_bytes()).hexdigest(),
        "ws65_input_sha256": ws65_report["input_sha256"],
        "original_manuscript_sha256": woodland_report["source_sha256"],
        "new_qa_editor_approvals": 0,
        "new_owner_public_approvals": 0,
        "new_android_activated_words": 0,
        "warnings": [
            "Preflight lanes are unsigned editorial suggestions; PROMISING does not mean verified English, correct tier, fit, rights, approval or playable.",
            "HOLD means seek evidence, not automatic rejection; MOVE means suggest another chapter without changing canonical identity.",
            "No word-specific source/license adjudication, page generation, Android test or owner review has occurred in this milestone.",
            "The potential-new number includes every unscreened HOLD or MOVE and cannot be added to the approved dictionary.",
        ],
    }
    return output, receipt


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repo-root", type=Path, default=ROOT)
    parser.add_argument("--out-dir", type=Path, default=Path("qa-ws65b-editorial"))
    args = parser.parse_args(argv)
    try:
        rows, receipt = build(args.repo_root)
        args.out_dir.mkdir(parents=True, exist_ok=True)
        with (args.out_dir / "unsigned-editorial-triage.csv").open(
                "w", encoding="utf-8", newline="") as f:
            writer = csv.DictWriter(f, fieldnames=REVIEW_HEADERS, lineterminator="\n")
            writer.writeheader()
            writer.writerows(rows)
        (args.out_dir / "editorial-preflight-receipt.json").write_text(
            json.dumps(receipt, indent=2, sort_keys=True) + "\n", encoding="utf-8")
        (args.out_dir / "README.md").write_text(
            "# WS65-B — editorial preflight only\n\n"
            f"**{receipt['total_rows']}** historical/specialist draft proposals, "
            f"**{receipt['review_annotations']}** candidate-specific preliminary notes.\n\n"
            "PROMISING/HOLD/MOVE are **NOT APPROVAL**. Every reviewer and owner decision "
            "is blank. Keep the historic sources, catalogs and frozen game saves untouched.\n",
            encoding="utf-8")
        print("WS65B_PREFLIGHT_OK:", receipt["total_rows"],
              "source rows;", receipt["review_annotations"],
              "curated notes;", receipt["preflight_lanes"],
              "0 approvals; no game change")
        return 0
    except (ValueError, KeyError, OSError, csv.Error) as exc:
        print("WS65B_PREFLIGHT_FAIL:", exc, file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
