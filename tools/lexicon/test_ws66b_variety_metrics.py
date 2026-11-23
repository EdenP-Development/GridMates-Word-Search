"""Exercise report validation with synthetic TSV rows, without generating grids."""

import csv
from pathlib import Path
from tempfile import TemporaryDirectory
import unittest

from ws65g_editorial_coverage import BOARD_SPECS, eligible
from ws66a_test_fixture_guard import FIXTURE, ROOT
from ws66b_variety_metrics import BOARD_PROFILE, SCENARIOS, analyze, read_tab


class VarietyMetricsTests(unittest.TestCase):
    def setUp(self):
        temp = TemporaryDirectory()
        self.addCleanup(temp.cleanup)
        self.directory = Path(temp.name)
        self.trials = []
        self.selections = []
        self.metrics = []
        fixture = read_tab(ROOT / FIXTURE)
        for index, ((chapter, board, challenge), targets) in enumerate(SCENARIOS.items()):
            size, tiers = BOARD_SPECS[BOARD_PROFILE[challenge]]
            pool = [row["answer"] for row in fixture if row["chapter"] == chapter
                    and eligible(row["answer"], row["tier"], size, tiers)]
            answers = ",".join(pool[:targets])
            case = {"chapter": chapter, "board": board, "challenge": challenge}
            for offset in range(3):
                seed = 660000 + index * 1000 + offset
                grid = dict(case, seed=seed, answers=answers, sha256=f"{seed:064x}")
                self.trials.append(dict(grid, status="PASS", failure=""))
                self.selections.append(dict(grid, page=offset + 1,
                                            adjacent_repeat_from_previous=targets if offset else 0))
            for offset in range(3, 12):
                self.trials.append(dict(case, seed=660000 + index * 1000 + offset,
                                        answers="", sha256="", status="FAIL", failure="FixtureFailure"))
            self.metrics.append(dict(
                case, eligible_pool=len(pool), per_board_targets=targets,
                unavoidable_pair_overlap_floor=max(0, 2 * targets - len(pool)),
                attempts=12, successes=3, failures=9, baseline_adjacent_repeats=2 * targets,
                selected_adjacent_repeats=2 * targets, baseline_distinct_answers=targets,
                selected_distinct_answers=targets, selection_status="THREE_VALID_TEST_ONLY_PAGES",
            ))

    def report(self):
        for filename, rows in (
            ("all-seed-trials.tsv", self.trials),
            ("three-page-selections.tsv", self.selections),
            ("per-scenario-metrics.tsv", self.metrics),
        ):
            with (self.directory / filename).open("w", encoding="utf-8", newline="") as handle:
                writer = csv.DictWriter(handle, fieldnames=rows[0].keys(), delimiter="\t")
                writer.writeheader()
                writer.writerows(rows)
        return analyze(self.directory)

    def test_accepts_consistent_evidence_with_recorded_failures(self):
        report = self.report()
        self.assertEqual(report["passed_generator_trials"], 24)
        self.assertEqual(report["failed_generator_trials"], 72)
        self.assertEqual(report["three_page_scenarios"], 8)

    def test_rejects_invented_pool_even_with_matching_floor(self):
        quiet_expert = next(row for row in self.metrics
                            if row["chapter"] == "Quiet Clearings" and row["challenge"] == "EXPERT")
        quiet_expert.update(eligible_pool=40, unavoidable_pair_overlap_floor=8)
        with self.assertRaisesRegex(ValueError, "eligible pool differs"):
            self.report()

    def test_rejects_reversed_pages_with_same_overlap(self):
        self.selections[0], self.selections[2] = self.selections[2], self.selections[0]
        for page, row in enumerate(self.selections[:3], start=1):
            row["page"] = page
            row["adjacent_repeat_from_previous"] = 12 if page > 1 else 0
        with self.assertRaisesRegex(ValueError, "seeds are not chronological"):
            self.report()

    def test_rejects_unknown_answer_even_when_metrics_match(self):
        for row in self.trials[:3] + self.selections[:3]:
            _, *remaining = row["answers"].split(",")
            row["answers"] = ",".join(["TESTWORD"] + remaining)
        with self.assertRaisesRegex(ValueError, "Targets outside the eligible fixture"):
            self.report()
