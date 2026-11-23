#!/usr/bin/env python3
"""WS25: import one hash-verified WS22 Collection accent into QA debug only.

Do not ship an unapproved candidate into the release resource tree.
"""
import argparse
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
PACK = ROOT / "design/art-library-v2/ws22"
SPRITE_ID = "gm_theme_woodland_robin_motif"
DEFAULT_OUTPUT = (ROOT / "app/src/debug/res/drawable-nodpi" /
                  "gm_ws25_collection_robin.png")


def import_accent(output: Path = DEFAULT_OUTPUT) -> str:
    manifest = json.loads((PACK / "manifest.json").read_text(encoding="utf-8"))
    matches = [e for e in manifest["entries"] if e["id"] == SPRITE_ID]
    if len(matches) != 1:
        raise ValueError("Expected exactly one WS22 robin source entry")
    entry = matches[0]
    source = PACK / entry["path"]
    data = source.read_bytes()
    expected = entry["sha256"]
    digest = hashlib.sha256(data).hexdigest()
    if digest != expected:
        raise ValueError(f"WS22 robin SHA-256 mismatch: {digest} != {expected}")
    if not data.startswith(b"\x89PNG\r\n\x1a\n"):
        raise ValueError("WS22 robin source is not a PNG")
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_bytes(data)
    if output.read_bytes() != data:
        raise ValueError("Debug drawable copy mismatch")
    if (ROOT / "app/src/release/res/drawable-nodpi" /
            "gm_ws25_collection_robin.png").exists():
        raise ValueError("Unapproved WS25 motif leaked into release resources")
    print(f"WS25 debug-only illustration imported: {output} (SHA-256 {digest})")
    return digest


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", type=Path, default=DEFAULT_OUTPUT)
    args = parser.parse_args()
    import_accent(args.output)


if __name__ == "__main__":
    main()
