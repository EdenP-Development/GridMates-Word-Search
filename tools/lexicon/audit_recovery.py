#!/usr/bin/env python3
"""Verify exact recovery of the isolated WS10 authoring branch."""
import hashlib
import json
from pathlib import Path


def audit(root: Path):
    manifest = json.loads((root / "data/lexicon/recovery-source-v1.json").read_text())
    assert manifest["schema_version"] == 1
    assert manifest["source_commit"] == "fb023af6c31323961760439ff1f62b0226ec57a2"
    assert len(manifest["files"]) == 17
    seen = set()
    for record in manifest["files"]:
        path = Path(record["path"])
        assert not path.is_absolute() and ".." not in path.parts
        assert record["path"] not in seen
        seen.add(record["path"])
        data = (root / path).read_bytes()
        digest = hashlib.sha1(b"blob " + str(len(data)).encode() + b"\0" + data).hexdigest()
        assert digest == record["git_blob_sha1"], f"Recovered source changed: {path}"
        assert len(data) == record["size_bytes"], path
    return {"source_commit": manifest["source_commit"], "exact_hash_files": len(seen),
            "android_activated": False}


if __name__ == "__main__":
    print(json.dumps(audit(Path(__file__).resolve().parents[2]), sort_keys=True))
