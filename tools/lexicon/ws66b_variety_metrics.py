#!/usr/bin/env python3
"""WS66-B: independently reconcile measured Kotlin V5 multi-page trial outputs.

The data are mechanical test results from unapproved, unit-test-only inputs.
No authorization to publish new content or alter existing saved boards.
"""
import argparse
from collections import Counter, defaultdict
import csv
import hashlib
from itertools import combinations
import json
from pathlib import Path
import re
import sys

SCENARIOS = {
    ("Flower Borders", "BOOK_SHORT", "RELAXED"): 12,
    ("Flower Borders", "BOOK_CLASSIC", "CLASSIC"): 16,
    ("Flower Borders", "BOOK_BIG", "EXPERT"): 24,
    ("Our Solar System", "BOOK_CLASSIC", "CLASSIC"): 16,
    ("Our Solar System", "BOOK_BIG", "EXPERT"): 24,
    ("Quiet Clearings", "BOOK_SHORT", "RELAXED"): 12,
    ("Quiet Clearings", "BOOK_CLASSIC", "CLASSIC"): 16,
    ("Quiet Clearings", "BOOK_BIG", "EXPERT"): 24,
}
KNOWN_WORKING = {
    ("Flower Borders", "BOOK_SHORT", "RELAXED"),
    ("Our Solar System", "BOOK_CLASSIC", "CLASSIC"),
    ("Quiet Clearings", "BOOK_BIG", "EXPERT"),
}


def read_tab(path):
    with path.open(encoding="utf-8", newline="") as handle:
        return list(csv.DictReader(handle, delimiter="\t"))


def words(raw):
    if not raw:
        return set()
    data = raw.split(",")
    if len(data) != len(set(data)) or any(not re.fullmatch(r"[A-Z]{3,20}", x) for x in data):
        raise ValueError("Duplicate or invalid trial targets")
    return set(data)


def adjacent(rows):
    return sum(len(a & b) for a, b in zip(rows, rows[1:]))


def analyze(directory):
    candidates = read_tab(directory / "all-seed-trials.tsv")
    selected = read_tab(directory / "three-page-selections.tsv")
    metrics = read_tab(directory / "per-scenario-metrics.tsv")

    if len(candidates) != 96 or len(metrics) != 8:
        raise ValueError("Missing per-seed evidence or scenarios")
    case_rows = defaultdict(list)
    selections = defaultdict(list)
    summaries = {}
    for row in candidates:
        key = (row["chapter"], row["board"], row["challenge"])
        if key not in SCENARIOS or row["status"] not in {"PASS", "FAIL"}:
            raise ValueError(f"Unexpected Kotlin trial case {key}")
        if row["status"] == "PASS":
            if not re.fullmatch("[0-9a-f]{64}", row["sha256"]) or row["failure"]:
                raise ValueError(f"Missing trial board digest {key}")
            if len(words(row["answers"])) != SCENARIOS[key]:
                raise ValueError(f"Wrong word count for {key}")
        elif row["sha256"] or row["answers"] or not row["failure"]:
            raise ValueError(f"Missing failure evidence {key}")
        case_rows[key].append(row)
    for key, rows in case_rows.items():
        seeds = [int(row["seed"]) for row in rows]
        if len(rows) != 12 or seeds != sorted(set(seeds)):
            raise ValueError(f"Wrong count or non-chronological trial seeds: {key}")
    if set(case_rows) != set(SCENARIOS):
        raise ValueError("At least one board scenario has no actual trials")

    for row in selected:
        key = (row["chapter"], row["board"], row["challenge"])
        if key not in SCENARIOS:
            raise ValueError(f"Unexpected selection {key}")
        selections[key].append(row)

    for row in metrics:
        key = (row["chapter"], row["board"], row["challenge"])
        if key not in SCENARIOS or key in summaries:
            raise ValueError(f"Duplicate or unknown metrics row {key}")
        pool = int(row["eligible_pool"])
        expected_floor = max(0, 2 * SCENARIOS[key] - pool)
        if (int(row["per_board_targets"]) != SCENARIOS[key] or
                int(row["unavoidable_pair_overlap_floor"]) != expected_floor):
            raise ValueError(f"Wrong constraints or mathematical floor: {key}")
        attempts = case_rows[key]
        passes = [x for x in attempts if x["status"] == "PASS"]
        if (int(row["attempts"]) != 12 or int(row["successes"]) != len(passes) or
                int(row["failures"]) != 12 - len(passes)):
            raise ValueError(f"Reported pass/fail counts do not match raw trials: {key}")
        chosen = selections[key]
        if len(passes) < 3:
            if chosen or row["selection_status"] != "NOT_ENOUGH_GENERATED_PAGES":
                raise ValueError(f"Blocked case falsely called page-ready: {key}")
            if any(row[f] != "-1" for f in (
                    "baseline_adjacent_repeats", "selected_adjacent_repeats",
                    "baseline_distinct_answers", "selected_distinct_answers")):
                raise ValueError(f"Blocked case has fake page metrics {key}")
        else:
            if row["selection_status"] != "THREE_VALID_TEST_ONLY_PAGES" or len(chosen) != 3:
                raise ValueError(f"Successful scenario lacks three test-only pages {key}")
            matches = {x["seed"]: x for x in passes}
            if [x["page"] for x in chosen] != ["1", "2", "3"]:
                raise ValueError(f"Bad page ordering {key}")
            if len({x["sha256"] for x in chosen}) != 3:
                raise ValueError(f"Duplicate selected board layout {key}")
            for pos, item in enumerate(chosen):
                original = matches.get(item["seed"])
                if original is None or original["sha256"] != item["sha256"]:
                    raise ValueError(f"Selected page does not match actual seed digest {key}")
                if words(item["answers"]) != words(original["answers"]):
                    raise ValueError(f"Selected page word list differs from actual grid {key}")
                prev = words(chosen[pos - 1]["answers"]) if pos else set()
                got = len(words(item["answers"]) & prev)
                if int(item["adjacent_repeat_from_previous"]) != got:
                    raise ValueError(f"Adjacent repetition was miscounted: {key}")
                if pos and got < expected_floor:
                    raise ValueError(f"Reported overlap below mathematical floor: {key}")
            actual = [words(x["answers"]) for x in chosen]
            baseline = [words(x["answers"]) for x in passes[:3]]
            if (int(row["baseline_adjacent_repeats"]) != adjacent(baseline)
                    or int(row["selected_adjacent_repeats"]) != adjacent(actual)
                    or int(row["baseline_distinct_answers"]) != len(set.union(*baseline))
                    or int(row["selected_distinct_answers"]) != len(set.union(*actual))):
                raise ValueError(f"Reported 3-page metrics mismatch {key}")
            possible = [
                (adjacent([words(a["answers"]), words(b["answers"]), words(c["answers"])]),
                 len(words(a["answers"]) | words(b["answers"]) | words(c["answers"])),
                 (a["seed"], b["seed"], c["seed"]))
                for a,b,c in combinations(passes, 3)
            ]
            optimal_repeats = min(x[0] for x in possible)
            optimal_coverage = max(x[1] for x in possible if x[0] == optimal_repeats)
            if (adjacent(actual) != optimal_repeats or
                    len(set.union(*actual)) != optimal_coverage):
                raise ValueError(f"Three-page schedule is not actually optimal: {key}")
            if adjacent(actual) > adjacent(baseline):
                raise ValueError(f"Experiment worsened page repetition: {key}")
        summaries[key] = row

    if set(summaries) != set(SCENARIOS):
        raise ValueError("Not every scenario has an independent metrics row")
    if not KNOWN_WORKING.issubset({
        k for k, r in summaries.items() if r["selection_status"] == "THREE_VALID_TEST_ONLY_PAGES"
    }):
        raise ValueError("One of three previously successful scenarios regressed")
    all_words_by_book = defaultdict(set)
    for key, rows in selections.items():
        for row in rows:
            all_words_by_book[key[0]].update(words(row["answers"]))
    book_names = sorted(all_words_by_book)
    cross = []
    for i, name in enumerate(book_names):
        for other in book_names[i+1:]:
            shared = sorted(all_words_by_book[name] & all_words_by_book[other])
            cross.append({"chapters": [name, other], "shared_answer_count": len(shared),
                          "shared_answers": shared})
    return {
        "format": "WS66B_ACTUAL_KOTLIN_V5_MULTIPAGE_VARIETY_V1",
        "status": "TEST_ONLY_EXPERIMENTAL_SEED_SELECTION_NOT_PUBLISHED",
        "scenarios": len(SCENARIOS),
        "total_actual_seed_trials": len(candidates),
        "passed_generator_trials": sum(x["status"] == "PASS" for x in candidates),
        "failed_generator_trials": sum(x["status"] == "FAIL" for x in candidates),
        "three_page_scenarios": sum(x["selection_status"] == "THREE_VALID_TEST_ONLY_PAGES"
                                    for x in summaries.values()),
        "blocked_scenarios": [
            {"chapter": k[0], "board": k[1], "challenge": k[2]}
            for k, x in sorted(summaries.items())
            if x["selection_status"] != "THREE_VALID_TEST_ONLY_PAGES"
        ],
        "three_page_test_only_pages": len(selected),
        "original_89_proposal_fixture_unchanged": True,
        "cross_chapter_selected_answer_reuse": cross,
        "cases": [dict(x) for _, x in sorted(summaries.items())],
        "input_sha256": {
            file: hashlib.sha256((directory / file).read_bytes()).hexdigest()
            for file in ("all-seed-trials.tsv", "three-page-selections.tsv",
                         "per-scenario-metrics.tsv")
        },
        "new_word_approvals": 0,
        "new_android_playable_words": 0,
        "warnings": [
            "Reported results reflect 12 deterministic seeds per scenario, not all possible seeds or human devices.",
            "Selected test pages are temporary choices within the generator experiment, NOT frozen published IDs.",
            "No player familiarity, age appropriateness, licensed vocabulary, UI, phone restore or physical touch QA occurs here.",
            "Small eligible pools impose unavoidable repetition. A perfect-looking board does not make a three-page book varied.",
        ],
    }


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--trial-dir", type=Path, default=Path("core/build/ws66b-variety"))
    parser.add_argument("--out-dir", type=Path, default=Path("qa-ws66b-variety"))
    args = parser.parse_args(argv)
    try:
        result = analyze(args.trial_dir)
        args.out_dir.mkdir(parents=True, exist_ok=True)
        (args.out_dir / "bookwide-variety-report.json").write_text(
            json.dumps(result, indent=2, sort_keys=True) + "\n", encoding="utf-8")
        heading = [
            "# WS66-B Actual V5 Multi-page Variety Trial (test only)",
            "",
            "Not editor approved, not released, not Android-activated. The three-page scheduling "
            "experiment chooses between already generated valid V5 grids; it does not change V5 itself.",
            "",
            "| Chapter | Board | Success/12 | Minimum pair overlap | Naive two-pair repeats | Selected repeats | Distinct chosen answers | Status |",
            "|---|---|---:|---:|---:|---:|---:|---|",
        ]
        for row in result["cases"]:
            heading.append(
                f"| {row['chapter']} | {row['board']} | {row['successes']}/12 "
                f"| {row['unavoidable_pair_overlap_floor']} "
                f"| {row['baseline_adjacent_repeats']} | {row['selected_adjacent_repeats']} "
                f"| {row['selected_distinct_answers']} | {row['selection_status']} |"
            )
        heading += [
            "",
            f"**{result['passed_generator_trials']}/96 generated successfully** across "
            f"eight intended scenarios. **{result['three_page_test_only_pages']}** total "
            "scratch pages selected only for this report.",
            "",
            "Never publish the trial file as a reviewed pack or replace an existing frozen page.",
        ]
        (args.out_dir / "bookwide-variety-summary.md").write_text(
            "\n".join(heading) + "\n", encoding="utf-8")
        print("WS66B_VERIFIED_TRIAL_METRICS:",
              result["passed_generator_trials"], "of 96 valid V5 seeds;",
              result["three_page_scenarios"], "of 8 three-page scenarios;",
              len(result["blocked_scenarios"]), "blocked; zero releases")
        return 0
    except (ValueError, OSError, KeyError, csv.Error) as exc:
        print("WS66B_TRIAL_METRICS_FAIL:", exc, file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
