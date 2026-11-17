#!/usr/bin/env python3
"""Build an immutable approved English pack for a future versioned loader.

Authoring only. No APK assets, enums, saved puzzles or approval flags are changed.
Both the canonical word and its topic assignment must already be approved.
"""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import sys
import tempfile

from validate import audit, csv_rows, EXPECTED_WORD_COLS, EXPECTED_TOPIC_COLS

PACK_ID = re.compile(r"en-[a-z][a-z0-9-]{2,60}-v[1-9][0-9]*\Z")


def canonical_bytes(value):
    return json.dumps(value, ensure_ascii=False, sort_keys=True,
                      separators=(",", ":")).encode("utf8")


def build_snapshot(base: Path, pack_id: str, topic_ids: list[str]) -> dict:
    audit(base)  # Validate the complete authoring graph before selecting anything.
    if not PACK_ID.fullmatch(pack_id):
        raise ValueError("Use an explicit versioned English pack ID, e.g. en-woodland-v1")
    if not topic_ids or len(topic_ids) != len(set(topic_ids)):
        raise ValueError("Choose one or more distinct topics")
    all_topics = {t["id"]: t for t in json.loads((base / "topics.json").read_text())["topics"]}
    if any(t not in all_topics or all_topics[t]["status"] != "active" for t in topic_ids):
        raise ValueError("Every requested topic must exist and be active")
    words = {r["word_id"]: r for r in csv_rows(base / "words_candidate.csv", EXPECTED_WORD_COLS)}
    assignments = csv_rows(base / "word_topics_candidate.csv", EXPECTED_TOPIC_COLS)
    selected = {}
    for row in assignments:
        if row["topic_id"] not in topic_ids or row["review_status"] != "approved":
            continue
        word = words[row["word_id"]]
        if word["review_status"] != "approved":
            raise ValueError("An approved assignment requires an approved word")
        if word["word_id"] not in selected:
            selected[word["word_id"]] = {
                key: word[key] for key in ("word_id", "answer", "display", "locale",
                                          "source_id", "reviewed_by", "reviewed_on")
            }
            selected[word["word_id"]]["assignments"] = []
        selected[word["word_id"]]["assignments"].append({
            key: row[key] for key in ("topic_id", "subtopic_id", "tier",
                                     "reviewed_by", "reviewed_on")
        })
    if not selected:
        raise ValueError("No approved playable words for the requested topics")
    for word in selected.values():
        word["assignments"].sort(key=lambda a: (a["topic_id"], a["subtopic_id"], a["tier"]))
    used_sources = {w["source_id"] for w in selected.values()}
    sources = json.loads((base / "sources.json").read_text())["sources"]
    payload = {
        "format": "gridmates-word-pack-snapshot", "schema_version": 1,
        "pack_id": pack_id, "locale": "en-US",
        "topics": [all_topics[t] for t in sorted(topic_ids)],
        "entries": [selected[k] for k in sorted(selected)],
        "source_notices": sorted((s for s in sources if s["id"] in used_sources),
                                 key=lambda s: s["id"]),
    }
    return {"payload": payload, "sha256": hashlib.sha256(canonical_bytes(payload)).hexdigest()}


def write_immutable(snapshot: dict, output_dir: Path) -> Path:
    payload = snapshot["payload"]
    pack_id = payload["pack_id"]
    if not PACK_ID.fullmatch(pack_id):
        raise ValueError("Unsafe or unversioned pack ID")
    if snapshot["sha256"] != hashlib.sha256(canonical_bytes(payload)).hexdigest():
        raise ValueError("Snapshot checksum mismatch")
    content = canonical_bytes(snapshot) + b"\n"
    output_dir.mkdir(parents=True, exist_ok=True)
    destination = output_dir / (pack_id + ".json")
    # Install a complete file atomically and never overwrite a published name.
    temporary = None
    try:
        with tempfile.NamedTemporaryFile(dir=output_dir, prefix=".pack-", delete=False) as f:
            temporary = Path(f.name)
            f.write(content)
            f.flush()
            os.fsync(f.fileno())
        try:
            os.link(temporary, destination)
        except FileExistsError:
            if destination.is_symlink() or destination.read_bytes() != content:
                raise ValueError("Pack ID already has different bytes; use a new content version")
        return destination
    finally:
        if temporary is not None:
            temporary.unlink(missing_ok=True)


def main():
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--data-dir", type=Path, default=Path("data/lexicon"))
    p.add_argument("--pack-id", required=True)
    p.add_argument("--topics", nargs="+", required=True)
    p.add_argument("--output-dir", type=Path, required=True)
    args = p.parse_args()
    try:
        snapshot = build_snapshot(args.data_dir, args.pack_id, args.topics)
        destination = write_immutable(snapshot, args.output_dir)
    except (ValueError, KeyError, OSError) as exc:
        print(f"FAIL snapshot: {exc}", file=sys.stderr)
        return 1
    print(json.dumps({"path": str(destination), "pack_id": args.pack_id,
                      "sha256": snapshot["sha256"],
                      "approved_unique": len(snapshot["payload"]["entries"]),
                      "android_activated": False}, sort_keys=True))
    return 0


if __name__ == "__main__":
    sys.exit(main())
