"""Approved-only, deterministic and immutable authoring-pack boundaries."""
import csv
import json
from pathlib import Path
import shutil
import tempfile
import unittest

from export_snapshot import build_snapshot, write_immutable

SOURCE = Path(__file__).resolve().parents[2] / "data" / "lexicon"


class SnapshotTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.base = Path(self.temp.name) / "lexicon"
        shutil.copytree(SOURCE, self.base)
        self.output = Path(self.temp.name) / "snapshots"

    def tearDown(self):
        self.temp.cleanup()

    def edit_csv(self, name, edit):
        path = self.base / name
        with path.open(newline="") as f:
            reader = csv.DictReader(f)
            headers, rows = reader.fieldnames, list(reader)
        edit(rows)
        with path.open("w", newline="") as f:
            writer = csv.DictWriter(f, fieldnames=headers)
            writer.writeheader()
            writer.writerows(rows)

    def approve_fixture(self):
        # Synthetic test-only approval; never write it back to source data.
        topics_path = self.base / "topics.json"
        topics = json.loads(topics_path.read_text())
        next(t for t in topics["topics"] if t["id"] == "woodland")["status"] = "active"
        topics_path.write_text(json.dumps(topics))
        def approve(rows):
            for row in rows:
                if row["word_id"] in {"gm000001", "gm000002"} and row.get("topic_id", "woodland") == "woodland":
                    row.update(review_status="approved", reviewed_by="Test fixture editor",
                               reviewed_on="2026-11-17")
        self.edit_csv("words_candidate.csv", approve)
        self.edit_csv("word_topics_candidate.csv", approve)

    def test_unreviewed_repository_cannot_export_a_playable_pack(self):
        with self.assertRaisesRegex(ValueError, "active"):
            build_snapshot(self.base, "en-woodland-v1", ["woodland"])
        path = self.base / "topics.json"
        topics = json.loads(path.read_text())
        next(t for t in topics["topics"] if t["id"] == "woodland")["status"] = "active"
        path.write_text(json.dumps(topics))
        with self.assertRaisesRegex(ValueError, "No approved"):
            build_snapshot(self.base, "en-woodland-v1", ["woodland"])
        self.assertFalse(self.output.exists())

    def test_only_approved_word_and_approved_assignment_are_selected(self):
        self.approve_fixture()
        pack = build_snapshot(self.base, "en-woodland-v1", ["woodland"])
        self.assertEqual([w["answer"] for w in pack["payload"]["entries"]], ["FOREST", "RABBIT"])
        self.assertEqual(len(pack["payload"]["source_notices"]), 1)
        self.assertTrue(all(a["topic_id"] == "woodland" for w in pack["payload"]["entries"]
                            for a in w["assignments"]))

    def test_row_order_and_unrelated_draft_edits_do_not_change_pack_bytes(self):
        self.approve_fixture()
        first = build_snapshot(self.base, "en-woodland-v1", ["woodland"])
        def change(rows):
            rows.reverse()
            next(r for r in rows if r["word_id"] == "gm000003")["editor_notes"] = "Still draft."
        self.edit_csv("words_candidate.csv", change)
        self.edit_csv("word_topics_candidate.csv", lambda rows: rows.reverse())
        self.assertEqual(first, build_snapshot(self.base, "en-woodland-v1", ["woodland"]))

    def test_existing_pack_is_idempotent_and_cannot_be_overwritten(self):
        self.approve_fixture()
        pack = build_snapshot(self.base, "en-woodland-v1", ["woodland"])
        path = write_immutable(pack, self.output)
        original = path.read_bytes()
        self.assertEqual(path, write_immutable(pack, self.output))
        self.edit_csv("words_candidate.csv", lambda rows: rows[0].update(display="FOREST"))
        changed = build_snapshot(self.base, "en-woodland-v1", ["woodland"])
        with self.assertRaisesRegex(ValueError, "different bytes"):
            write_immutable(changed, self.output)
        self.assertEqual(original, path.read_bytes())
        self.assertEqual(list(self.output.glob(".pack-*")), [])

    def test_invalid_or_duplicate_topics_and_pack_ids_are_rejected(self):
        self.approve_fixture()
        for pack_id, topics in [("../escape", ["woodland"]), ("en-woodland-v1", ["missing"]),
                                ("en-woodland-v1", ["woodland", "woodland"])]:
            with self.assertRaises(ValueError):
                build_snapshot(self.base, pack_id, topics)

    def test_unlicensed_word_is_rejected(self):
        self.approve_fixture()
        self.edit_csv("words_candidate.csv", lambda rows: rows[0].update(source_id="scowl-research"))
        with self.assertRaisesRegex(ValueError, "uncleared"):
            build_snapshot(self.base, "en-woodland-v1", ["woodland"])

    def test_checksum_mismatch_does_not_write_output(self):
        self.approve_fixture()
        pack = build_snapshot(self.base, "en-woodland-v1", ["woodland"])
        pack["sha256"] = "0" * 64
        with self.assertRaisesRegex(ValueError, "checksum"):
            write_immutable(pack, self.output)
        self.assertFalse(self.output.exists())
