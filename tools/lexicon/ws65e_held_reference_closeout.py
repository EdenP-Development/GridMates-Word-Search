#!/usr/bin/env python3
"""WS65-E: deterministic, read-only closeout of 31 remaining held *research queues*.

This resolves reference coverage, NOT actual editor/owner publication decisions.
Uncertainty regarding spelling/display or player familiarity stays explicit.
"""
import argparse
from collections import Counter
import csv
import hashlib
import json
from pathlib import Path
import re
import sys

from validate import normalized
from ws63_quality_audit import ROOT
from ws64_woodland_review_queue import load_canonical
from ws65d_second_pass import build as build_ws65d

INPUT = Path("data/lexicon/intake/ws65e-final-holds-evidence-v1.tsv")
FIELDS = [
    "source_pool", "answer", "evidence_scope", "source_url",
    "source_context", "recommendation", "suggested_chapter", "suggested_tier",
    "reader_note", "common_name_to_consider", "rights_status",
    "qa_editor_decision", "owner_decision",
]
ALLOWED_SCOPE = {"DIRECT_WORD_SENSE", "WORD_FORM_HOLD", "DISPLAY_REVIEW"}
TIERS = {"familiar", "intermediate", "advanced"}
OUTPUT_FIELDS = [
    "source_pool", "source_id", "answer", "display", "original_chapter",
    "existing_canonical_id", "original_preflight_lane", "ws65c_evidence_status",
    "ws65d_evidence_status", "ws65e_evidence_status", "ws65e_scope",
    "ws65e_source_url", "ws65e_source_context", "ws65e_recommendation",
    "ws65e_suggested_chapter", "ws65e_suggested_tier", "ws65e_reader_note",
    "ws65e_common_name_idea", "ws65e_idea_type",
    "ws65e_existing_alias_canonical_id", "ws65e_rights_status",
    "qa_editor_decision", "owner_decision",
]


def load_input(root=ROOT):
    with (root / INPUT).open(encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f, delimiter="\t")
        if reader.fieldnames != FIELDS:
            raise ValueError("WS65-E source evidence schema mismatch")
        rows = {}
        for r in reader:
            k = (r["source_pool"], r["answer"])
            if k in rows:
                raise ValueError(f"Duplicate WS65-E source answer {k}")
            if r["source_pool"] not in {"WS64", "WS65"}:
                raise ValueError(f"Unexpected source pool {k}")
            if not re.fullmatch(r"[A-Z]{3,20}", r["answer"]):
                raise ValueError(f"Invalid uppercase answer {k}")
            if r["evidence_scope"] not in ALLOWED_SCOPE:
                raise ValueError(f"Unknown evidence scope {k}")
            if not r["source_url"].startswith("https://"):
                raise ValueError(f"Missing source reference {k}")
            if len(r["source_context"]) < 55 or len(r["reader_note"]) < 55:
                raise ValueError(f"Insufficient source sense or reader context {k}")
            if not r["recommendation"] or not r["suggested_chapter"]:
                raise ValueError(f"Missing proposed disposition {k}")
            if r["suggested_tier"] not in TIERS:
                raise ValueError(f"Invalid suggested tier {k}")
            if r["rights_status"] != "NOT_CLEARED":
                raise ValueError(f"Evidence may not clear commercial rights {k}")
            if r["qa_editor_decision"] or r["owner_decision"]:
                raise ValueError(f"Evidence may not impersonate approvals {k}")
            if r["common_name_to_consider"]:
                alt = normalized(r["common_name_to_consider"])
                if not re.fullmatch(r"[A-Z]{3,20}", alt):
                    raise ValueError(f"Bad alternative proposal {k}")
            rows[k] = r
    if not rows:
        raise ValueError("Empty held-answer source ledger")
    return rows


def build(root=ROOT):
    old_rows, old_receipt = build_ws65d(root)
    source = load_input(root)
    canonical = load_canonical(root)
    baseline = {(r["source_pool"], r["answer"]): r for r in old_rows}
    if len(baseline) != len(old_rows):
        raise ValueError("Prior source inventory not identity unique")
    unhandled_holds = {
        k for k, r in baseline.items()
        if r["preflight_lane"] == "HOLD"
        and r["ws65c_evidence_status"] == "NOT_IN_THIS_EVIDENCE_BATCH"
        and r["ws65d_evidence_status"] == "NOT_IN_THIS_PASS"
    }
    if set(source) != unhandled_holds:
        missing = sorted(unhandled_holds - set(source))
        extra = sorted(set(source) - unhandled_holds)
        raise ValueError(f"WS65-E must cover only the outstanding HOLD entries; missing={missing}, extraneous={extra}")

    output = []
    scopes, alias_counts, origins = Counter(), Counter(), Counter()
    specialist_tiers = Counter()
    alias_rows = []
    for orig in old_rows:
        k = (orig["source_pool"], orig["answer"])
        evidence = source.get(k)
        if orig["owner_decision"] or orig["qa_editor_decision"]:
            raise ValueError(f"Earlier source contained approval {k}")
        alias = evidence["common_name_to_consider"] if evidence else ""
        norm_alt = normalized(alias) if alias else ""
        alias_type = ("SAME_NORMALIZED_DISPLAY" if norm_alt == orig["answer"] else
                      "SEPARATE_CANONICAL_CANDIDATE_NOT_ADDED") if alias else ""
        match = canonical.get(norm_alt, {}) if alias else {}
        if evidence:
            scopes[evidence["evidence_scope"]] += 1
            origins[k[0]] += 1
            specialist_tiers[evidence["suggested_tier"]] += 1
        if alias:
            alias_counts[alias_type] += 1
            alias_rows.append({
                "original_answer": orig["answer"],
                "source_pool": k[0],
                "proposed_reader_friendly_label": alias,
                "normalized_answer": norm_alt,
                "same_existing_answer": norm_alt == orig["answer"],
                "canonical_existing_id_if_any": match.get("word_id", ""),
                "idea_classification": alias_type,
                "approval": "NONE",
            })
        output.append({
            "source_pool": orig["source_pool"],
            "source_id": orig["source_id"],
            "answer": orig["answer"], "display": orig["display"],
            "original_chapter": orig["original_chapter"],
            "existing_canonical_id": orig["existing_canonical_id"],
            "original_preflight_lane": orig["preflight_lane"],
            "ws65c_evidence_status": orig["ws65c_evidence_status"],
            "ws65d_evidence_status": orig["ws65d_evidence_status"],
            "ws65e_evidence_status": "INDIVIDUAL_SOURCE_CONTEXT_NOT_RELEASE_APPROVAL"
                if evidence else "NOT_IN_THIS_PASS",
            "ws65e_scope": evidence["evidence_scope"] if evidence else "",
            "ws65e_source_url": evidence["source_url"] if evidence else "",
            "ws65e_source_context": evidence["source_context"] if evidence else "",
            "ws65e_recommendation": evidence["recommendation"] if evidence else "",
            "ws65e_suggested_chapter": evidence["suggested_chapter"] if evidence else "",
            "ws65e_suggested_tier": evidence["suggested_tier"] if evidence else "",
            "ws65e_reader_note": evidence["reader_note"] if evidence else "",
            "ws65e_common_name_idea": alias,
            "ws65e_idea_type": alias_type,
            "ws65e_existing_alias_canonical_id": match.get("word_id", ""),
            "ws65e_rights_status": evidence["rights_status"] if evidence else "NOT_CLEARED",
            "qa_editor_decision": "", "owner_decision": "",
        })

    if len(output) != old_receipt["all_original_proposal_rows"]:
        raise ValueError("Source proposals cardinality changed")
    previous = old_receipt["combined_evidence_rows"]
    total = previous + len(source)
    remaining = len(output) - total
    counts_by_lane = dict(old_receipt["remaining_unchecked_by_preflight_lane"])
    counts_by_lane["HOLD"] -= len(source)
    if any(x < 0 for x in counts_by_lane.values()):
        raise ValueError("Unrealistic evidence coverage counters")
    if sum(counts_by_lane.values()) != remaining:
        raise ValueError("Remaining evidence count mismatch")
    r = {
        "format": "WS65E_COMPLETE_HELD_REFERENCE_QUEUE_V1",
        "status": "RESEARCH_QUEUE_REFERENCES_COMPLETE_PUBLIC_APPROVALS_ZERO",
        "all_source_proposals_unchanged": len(output),
        "previous_source_referenced_rows": previous,
        "new_held_rows_with_source_context": len(source),
        "cumulative_research_references": total,
        "still_without_individual_reference": remaining,
        "remaining_unreferenced_by_preflight_lane": dict(sorted(counts_by_lane.items())),
        "new_evidence_scope": dict(sorted(scopes.items())),
        "new_evidence_origin": dict(sorted(origins.items())),
        "suggested_tiers_unapproved": dict(sorted(specialist_tiers.items())),
        "friendly_label_ideas": len(alias_rows),
        "friendly_label_ideas_by_type": dict(sorted(alias_counts.items())),
        "candidate_new_spellings_not_reviewed_or_approved":
            old_receipt["candidate_new_answer_spellings_not_approved"],
        "original_ws65d_file_sha256": old_receipt["evidence_file_sha256"],
        "ws65e_file_sha256": hashlib.sha256((root / INPUT).read_bytes()).hexdigest(),
        "new_editor_approved_words": 0,
        "new_owner_public_approvals": 0,
        "new_android_activated_words": 0,
        "warnings": [
            "All initial held terms have an individual context reference; this DOES NOT mean they all pass vocabulary suitability, correct display, rights or player familiarity.",
            "RIVERSTONE has a word-form hold, and WILDLIFESIGNS still requires an explicit readable display decision.",
            "Friendly labels with a different normalized answer are only *ideas*, not added candidate words, aliases in Android or catalog approvals.",
            "CI validates the checked-in source ledger, identities and data invariants, not that every external reference remains online or grants rights.",
            "The remaining 162 PROMISING terms have not received individual source review; none of these records changes old frozen pages or saved games.",
        ],
    }
    return output, alias_rows, r


def main(argv=None):
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--repo-root", type=Path, default=ROOT)
    p.add_argument("--out-dir", type=Path, default=Path("qa-ws65e-final-holds"))
    a = p.parse_args(argv)
    try:
        rows, aliases, receipt = build(a.repo_root)
        a.out_dir.mkdir(parents=True, exist_ok=True)
        with (a.out_dir / "full-unsigned-candidate-ledger.csv").open(
                "w", newline="", encoding="utf-8") as f:
            writer = csv.DictWriter(f, fieldnames=OUTPUT_FIELDS, lineterminator="\n")
            writer.writeheader()
            writer.writerows(rows)
        fields = list(aliases[0]) if aliases else [
            "original_answer", "source_pool", "proposed_reader_friendly_label",
            "normalized_answer", "same_existing_answer", "canonical_existing_id_if_any",
            "idea_classification", "approval"
        ]
        with (a.out_dir / "friendly-wording-ideas.csv").open(
                "w", newline="", encoding="utf-8") as f:
            writer = csv.DictWriter(f, fieldnames=fields, lineterminator="\n")
            writer.writeheader()
            writer.writerows(aliases)
        (a.out_dir / "research-receipt.json").write_text(
            json.dumps(receipt, sort_keys=True, indent=2) + "\n", encoding="utf-8")
        (a.out_dir / "README.md").write_text(
            "# WS65-E — held-word source-reference completion; NOT RELEASE APPROVAL\n\n"
            f"{receipt['new_held_rows_with_source_context']} new contextual reviews; "
            f"{receipt['cumulative_research_references']} total linked source entries; "
            f"{receipt['still_without_individual_reference']} promising entries still unchecked.\n\n"
            "No human approvals, source rights signoff, changed catalogs, game packs or APKs.\n",
            encoding="utf-8")
        print("WS65E_RESEARCH_OK:", receipt["new_held_rows_with_source_context"],
              "held terms now referenced;", receipt["cumulative_research_references"],
              "total refs;", receipt["still_without_individual_reference"],
              "unresearched; zero approvals")
        return 0
    except (ValueError, KeyError, OSError, csv.Error) as e:
        print("WS65E_RESEARCH_FAIL:", e, file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
