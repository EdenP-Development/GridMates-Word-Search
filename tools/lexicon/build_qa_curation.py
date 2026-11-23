#!/usr/bin/env python3
"""Reproduce the WS41 assistant-reviewed QA catalog without editing WS10's fixture.

The curation sheet is the editorial input. No dictionary rows are auto-approved.
All exports are explicitly QA-only and remain absent from release builds.
"""
import csv
import json
from pathlib import Path
from collections import Counter

from validate import normalized, audit
from export_snapshot import build_snapshot, write_immutable

ROOT = Path(__file__).resolve().parents[2]
BASE = ROOT / "data/lexicon"
WORK = BASE / "authoring/en-v1"
REVIEWER = "Codex QA editorial pass"
DATE = "2026-11-17"


def read_csv(path):
    with path.open(newline="", encoding="utf8") as f:
        reader = csv.DictReader(f)
        return reader.fieldnames, list(reader)


def write_csv(path, fields, rows):
    with path.open("w", newline="", encoding="utf8") as f:
        w = csv.DictWriter(f, fieldnames=fields, lineterminator="\n")
        w.writeheader()
        w.writerows(rows)


def main():
    fields, original = read_csv(BASE / "words_candidate.csv")
    topic_fields, links = read_csv(BASE / "word_topics_candidate.csv")
    words = {r["answer"]: dict(r) for r in original}
    assignments = {(r["word_id"], r["topic_id"], r["subtopic_id"]): dict(r) for r in links}
    topics = json.loads((BASE / "topics.json").read_text())
    active = {"woodland", "space", "garden", "everyday"}
    for t in topics["topics"]:
        if t["id"] in active:
            t["status"] = "active"
    sources = json.loads((BASE / "sources.json").read_text())
    sources["status_note"] = "Original QA vocabulary selection; no bulk dictionary imported."
    sources["sources"].append({
        "id": "gridmates-qa-english", "label": "WS41 original QA word selection",
        "type": "internal", "license": "project-maintained-word-selection",
        "rights_review": "internal-existing",
        "attribution": "Original thematic selection and display labels by Codex for GridMates.",
        "source_url": None, "approval_scope": "assistant-reviewed-qa-only",
        "owner_release_approved": False,
    })
    next_id = max(int(r["word_id"][2:]) for r in original) + 1
    seen = set()
    for line in (WORK / "curation.tsv").read_text().splitlines():
        if not line or line.startswith("#"):
            continue
        topic, subtopic, tier, labels = line.split("|", 3)
        assert topic in active
        for label in labels.split(";"):
            answer = normalized(label)
            key = (answer, topic, subtopic)
            assert key not in seen, key
            seen.add(key)
            if answer not in words:
                words[answer] = {"word_id": f"gm{next_id:06d}", "answer": answer}
                next_id += 1
            word = words[answer]
            note = "Original curated QA selection; spelling/display, theme and familiarity reviewed by Codex. Owner release review pending."
            word.update(display=label, locale="en-US", review_status="approved",
                        source_id="gridmates-qa-english", editor_notes=note,
                        reviewed_by=REVIEWER, reviewed_on=DATE)
            assignment = {"word_id": word["word_id"], "topic_id": topic,
                          "subtopic_id": subtopic, "tier": tier,
                          "review_status": "approved", "editor_notes": note,
                          "reviewed_by": REVIEWER, "reviewed_on": DATE}
            assignments[word["word_id"], topic, subtopic] = assignment
    WORK.mkdir(parents=True, exist_ok=True)
    write_csv(WORK / "words_candidate.csv", fields,
              sorted(words.values(), key=lambda r: r["word_id"]))
    write_csv(WORK / "word_topics_candidate.csv", topic_fields,
              sorted(assignments.values(), key=lambda r: (r["word_id"], r["topic_id"], r["subtopic_id"])))
    (WORK / "topics.json").write_text(json.dumps(topics, indent=2) + "\n")
    (WORK / "sources.json").write_text(json.dumps(sources, indent=2) + "\n")
    report = audit(WORK)
    packs = []
    for name, wanted in [(t, [t]) for t in sorted(active)] + [("cozy-mixed", sorted(active))]:
        pack_id = f"en-{name}-v1"
        snapshot = build_snapshot(WORK, pack_id, wanted)
        path = write_immutable(snapshot, BASE / "packs/qa-v1")
        packs.append({"pack_id": pack_id, "entries": len(snapshot["payload"]["entries"]),
                      "sha256": snapshot["sha256"], "path": str(path.relative_to(ROOT))})
    report.update(approval_scope="assistant-reviewed-qa-only", owner_release_approved=False,
                  packs=packs, curated_assignments=len(seen))
    (WORK / "review-report.json").write_text(json.dumps(report, indent=2) + "\n")
    print(json.dumps({"unique_qa_words": report["global_approved_playable_unique"],
                      "assignments": len(seen), "packs": packs}, sort_keys=True))


if __name__ == "__main__":
    main()
