#!/usr/bin/env python3
"""Generate debug-only Kotlin packs and exact core-test fixtures from frozen exports."""
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
TITLES = {"everyday": "Everyday", "woodland": "Woodland", "space": "Space",
          "garden": "Garden", "cozy-mixed": "Quick Play"}


def main():
    pack_rows = []
    for path in sorted((ROOT / "data/lexicon/packs/qa-v1").glob("*.json")):
        snapshot = json.loads(path.read_text())
        payload = snapshot["payload"]
        canonical = json.dumps(payload, sort_keys=True, ensure_ascii=False,
                               separators=(",", ":")).encode()
        assert hashlib.sha256(canonical).hexdigest() == snapshot["sha256"]
        assert all(s.get("approval_scope") == "assistant-reviewed-qa-only"
                   and not s.get("owner_release_approved") for s in payload["source_notices"])
        pack_id = payload["pack_id"]
        title = TITLES[pack_id.removeprefix("en-").removesuffix("-v1")]
        lines = ["GRIDMATES_WORD_PACK_V1", pack_id, snapshot["sha256"], title,
                 ",".join(t["id"] for t in payload["topics"])]
        for entry in payload["entries"]:
            assignments = "|".join(":".join(a[k] for k in ("topic_id", "subtopic_id", "tier"))
                                   for a in entry["assignments"])
            lines.append("\t".join([entry["word_id"], entry["answer"], entry["display"], assignments]))
        body = "\n".join(lines)
        digest = hashlib.sha256(body.encode()).hexdigest()
        text = body + "\nSHA256\t" + digest
        assert len(text.encode()) < 60_000 and '"""' not in text and '$' not in text
        resource = ROOT / "core/src/test/resources/reviewed-packs" / (pack_id + ".tsv")
        resource.parent.mkdir(parents=True, exist_ok=True)
        resource.write_text(text + "\n")
        pack_rows.append((pack_id, text, digest, len(payload["entries"])))
    code = '''package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.ReviewedWordPack
import com.playereden.gridmates.wordsearch.core.ReviewedWordPackCodec
import com.playereden.gridmates.wordsearch.core.WordTheme

/** Generated original QA content. Assistant-reviewed; owner release approval pending.
 * This file exists only in the debug source set. Reproduce with render_qa_runtime.py. */
internal object QuickPlayContent {
    private val packs: Map<String, ReviewedWordPack> by lazy {
        mapOf(
'''
    code += ",\n".join('            "' + pack_id + '" to ReviewedWordPackCodec.decode("""' + text + '""")'
                        for pack_id, text, _, _ in pack_rows)
    code += '''
        )
    }
    val mixed: ReviewedWordPack? get() = packs["en-cozy-mixed-v1"]
    fun themed(theme: WordTheme): ReviewedWordPack? = packs["en-" + theme.name.lowercase() + "-v1"]
}
'''
    destination = ROOT / "app/src/debug/java/com/playereden/gridmates/wordsearch/QuickPlayContent.kt"
    destination.write_text(code)
    manifest = {"scope": "assistant-reviewed-qa-only", "owner_release_approved": False,
                "packs": [{"id": p, "runtime_sha256": h, "entries": n} for p, _, h, n in pack_rows],
                "kotlin_sha256": hashlib.sha256(code.encode()).hexdigest()}
    (ROOT / "data/lexicon/authoring/en-v1/runtime-manifest.json").write_text(json.dumps(manifest, indent=2) + "\n")
    print(json.dumps(manifest, sort_keys=True))


if __name__ == "__main__":
    main()
