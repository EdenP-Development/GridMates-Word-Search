#!/usr/bin/env python3
"""WS66-A strict guard: the Kotlin test fixture cannot invent or publish words."""
import argparse
from collections import Counter
import csv
import hashlib
import json
from pathlib import Path
import sys

from ws63_quality_audit import ROOT
from ws65e_held_reference_closeout import build as build_ws65e
from ws65f_player_first_shortlist import review as build_ws65f
from ws65g_editorial_coverage import build as build_ws65g

FIXTURE = Path("core/src/test/resources/ws66a/qa-only-v5-candidate-pools.tsv")
COLUMNS = [
    "chapter", "theme", "topic", "subtopic", "word_id", "answer",
    "display", "tier", "source_pool", "review_stage",
]
CONFIG = {
    "Flower Borders": ("GARDEN", "garden", "flowers", 30),
    "Our Solar System": ("SPACE", "space", "solar-system", 33),
    "Quiet Clearings": ("WOODLAND", "woodland", "habitats", 26),
}

def verify(root=ROOT):
    old, _, _ = build_ws65e(root)
    first, _, _ = build_ws65f(root)
    second, _ = build_ws65g(root)
    originals = {(v["source_pool"], v["answer"]): v for v in old}
    expected = {}
    for stage, entries in (("WS65F", first), ("WS65G", second)):
        for row in entries:
            chapter = row["chapter"]
            if chapter not in CONFIG:
                continue
            key = (chapter, row["answer"])
            if key in expected:
                raise ValueError(f"Repeated chapter word {key}")
            origin = originals[(row["source_pool"], row["answer"])]
            theme, topic, subtopic, _ = CONFIG[chapter]
            expected[key] = {
                "chapter": chapter,
                "theme": theme, "topic": topic, "subtopic": subtopic,
                "word_id": origin["existing_canonical_id"] or origin["source_id"],
                "answer": row["answer"], "display": origin["display"],
                "tier": (row["provisional_reader_tier"] if stage == "WS65F"
                         else row["prior_suggested_tier"]),
                "source_pool": row["source_pool"], "review_stage": stage,
            }
    with (root / FIXTURE).open(encoding="utf-8", newline="") as f:
        reader = csv.DictReader(f, delimiter="\t")
        if reader.fieldnames != COLUMNS:
            raise ValueError("Trial fixture columns changed")
        actual = {}
        for line in reader:
            key = (line["chapter"], line["answer"])
            if key in actual:
                raise ValueError(f"Duplicate scratch word {key}")
            actual[key] = line
    if actual != expected:
        missing = set(expected) - set(actual)
        excess = set(actual) - set(expected)
        drift = [k for k in set(expected) & set(actual) if expected[k] != actual[k]]
        raise ValueError(
            f"Fixture drift: missing={sorted(missing)}, extras={sorted(excess)}, changed={sorted(drift)}"
        )

    by_chapter = Counter(row["chapter"] for row in actual.values())
    familiar = Counter(row["chapter"] for row in actual.values() if row["tier"] == "familiar")
    for name, (_, _, _, count) in CONFIG.items():
        if by_chapter[name] != count:
            raise ValueError(f"Chapter {name} original source count changed")
    if familiar["Our Solar System"] >= 12:
        raise ValueError("Original Solar familiar shortage unexpectedly changed")

    return {
        "format": "WS66A_ISOLATED_KOTLIN_V5_TRIAL_FIXTURE_V1",
        "stage": "TEST_ONLY_NO_PACK_OR_APP_ACTIVATION",
        "test_fixture_proposal_occurrences": len(actual),
        "chapters": dict(sorted(by_chapter.items())),
        "familiar_by_chapter": dict(sorted(familiar.items())),
        "solar_relaxed12_familiar_shortage_at_least": 12 - familiar["Our Solar System"],
        "fixture_sha256": hashlib.sha256((root / FIXTURE).read_bytes()).hexdigest(),
        "new_owner_public_approvals": 0,
        "new_android_playable_answers": 0,
        "warnings": [
            "These source candidates are not release-approved or commercially cleared.",
            "Actual placement is tested by Kotlin V5 scratch-only JUnit; this Python guard checks source identity, not real boards.",
            "Space has too few familiar-only entries to fill a 12x12 Relaxed page from this draft subset.",
            "No old V5 frozen page, active pack, Android runtime or save key changes.",
        ],
    }

def main(argv=None):
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--repo-root", type=Path, default=ROOT)
    p.add_argument("--out-dir", type=Path, default=Path("qa-ws66a-v5-trial"))
    a = p.parse_args(argv)
    try:
        receipt = verify(a.repo_root)
        a.out_dir.mkdir(parents=True, exist_ok=True)
        (a.out_dir / "trial-fixture-integrity.json").write_text(
            json.dumps(receipt, sort_keys=True, indent=2) + "\n", encoding="utf-8")
        print("WS66A_SOURCE_FIXTURE_OK:", receipt["test_fixture_proposal_occurrences"],
              "source-reconciled test-only candidates, zero approvals")
        return 0
    except (KeyError, ValueError, OSError, csv.Error) as exc:
        print("WS66A_SOURCE_FIXTURE_FAIL:", exc, file=sys.stderr)
        return 1

if __name__ == "__main__":
    raise SystemExit(main())
