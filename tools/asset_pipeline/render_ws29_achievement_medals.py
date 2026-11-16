#!/usr/bin/env python3
"""WS29: render existing original WS21 achievement shield masters into QA sprites.

Source-backed only; never ship candidate SVGs into release without approval.
"""
import argparse
import hashlib
import json
from pathlib import Path
import cairosvg

ROOT = Path(__file__).resolve().parents[2]
PACK = ROOT / "design/art-library-v2/ws21"
DEST = ROOT / "app/src/debug/res/drawable-nodpi"
PALETTES = ("gold", "cream", "forest")

def render(output_dir=DEST):
    manifest = json.loads((PACK / "manifest.json").read_text(encoding="utf-8"))
    output_dir.mkdir(parents=True, exist_ok=True)
    results = {}
    for tone in PALETTES:
        key = f"gm_core_ws21_achievement_slot_{tone}"
        matches = [e for e in manifest["sprites"] if e["id"] == key]
        if len(matches) != 1:
            raise ValueError(f"Expected exactly one WS21 original {key}")
        entry = matches[0]
        source = PACK / entry["source"]
        source_bytes = source.read_bytes()
        actual = hashlib.sha256(source_bytes).hexdigest()
        expected = entry["details"]["editable_svg_sha256"]
        if actual != expected:
            raise ValueError(f"WS21 source SHA mismatch: {key}: {actual} != {expected}")
        result_path = output_dir / f"gm_ws29_achievement_{tone}.png"
        cairosvg.svg2png(bytestring=source_bytes,
            write_to=str(result_path), output_width=192, output_height=192)
        rendered_bytes = result_path.read_bytes()
        if rendered_bytes[:8] != bytes([137, 80, 78, 71, 13, 10, 26, 10]):
            raise ValueError(f"Not a PNG: {result_path}")
        if len(rendered_bytes) < 5000:
            raise ValueError(f"Implausibly small shield output: {result_path}")
        results[tone] = {"source_sha256":actual,
                         "png_sha256":hashlib.sha256(rendered_bytes).hexdigest(),
                         "bytes":len(rendered_bytes)}
        if (ROOT / "app/src/release/res/drawable-nodpi" / result_path.name).exists():
            raise ValueError("Unapproved WS29 trial shield leaked into release resources")
    print(json.dumps(results, indent=2))
    return results

if __name__ == "__main__":
    parser=argparse.ArgumentParser()
    parser.add_argument("--output-dir",type=Path,default=DEST)
    args=parser.parse_args()
    render(args.output_dir)
