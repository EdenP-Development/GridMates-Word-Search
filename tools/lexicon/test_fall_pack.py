import unittest
from pathlib import Path

from export_snapshot import build_snapshot, canonical_bytes
from validate import EXPECTED_WORD_COLS, csv_rows


LEXICON = Path(__file__).resolve().parents[2] / "data/lexicon"
FALL = LEXICON / "authoring/fall-v1"


class FallPackTest(unittest.TestCase):
    def test_frozen_fall_pack_can_be_reproduced_from_qa_authoring_rows(self):
        snapshot = build_snapshot(FALL, "en-fall-v1", ["fall"])
        payload = snapshot["payload"]
        self.assertEqual(len(payload["entries"]), 150)
        for source in payload["source_notices"]:
            self.assertEqual(source["approval_scope"], "assistant-reviewed-qa-only")
            self.assertIs(source["owner_release_approved"], False)
        frozen = FALL / "snapshots/en-fall-v1.json"
        self.assertEqual(frozen.read_bytes(), canonical_bytes(snapshot) + b"\n")

    def test_existing_words_reuse_canonical_rows_and_new_ids_are_free(self):
        previous = csv_rows(LEXICON / "authoring/en-v1/words_candidate.csv", EXPECTED_WORD_COLS)
        previous_by_answer = {row["answer"]: row for row in previous}
        previous_ids = {row["word_id"] for row in previous}
        fall = csv_rows(FALL / "words_candidate.csv", EXPECTED_WORD_COLS)

        for word in fall:
            with self.subTest(answer=word["answer"]):
                if word["answer"] in previous_by_answer:
                    self.assertEqual(word, previous_by_answer[word["answer"]])
                else:
                    self.assertNotIn(word["word_id"], previous_ids)
