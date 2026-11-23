#!/usr/bin/env python3
"""WS20 preproduction source QA. Does not need external PNG archive or Android.

Checks that the 72 editable spine SVGs in GitHub exactly match the
source SHA-256s in the full 88-sprite manifest. The PNG artwork remains
externally archived and cannot be validated without that separate ZIP.
"""
import hashlib
import json
import re
import xml.etree.ElementTree as ET
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parent
DOC = json.loads((ROOT / "manifest.json").read_text(encoding="utf-8"))
assert DOC["schema_version"] == 1
assert DOC["pack_id"] == "gm_ws20_asset_expansion"
assert DOC["status"] == "candidate_not_shipped"

sprites = DOC["sprites"]
assert len(sprites) == 88
assert len({x["id"] for x in sprites}) == 88
assert DOC["counts"] == {
    "collection_reward_sprites": 16,
    "theme_spine_sprites": 72,
    "total_candidate_sprites": 88,
    "themes": 24,
}
for sprite in sprites:
    assert re.fullmatch(r"gm_(?:core|ws)_[a-z0-9_]+", sprite["id"]), sprite["id"]
    assert re.fullmatch(r"[0-9a-f]{64}", sprite["sha256"]), sprite["id"]
    assert sprite["status"] == "candidate"
    assert sprite["alpha"] is True
    assert all(x > 0 for x in sprite["px"])
    assert sprite["path"].startswith("sprites/") and sprite["path"].endswith(".png")
for src in DOC["source_sheets"]:
    assert re.fullmatch(r"[0-9a-f]{64}", src["sha256"])
    assert src["role"] in ("exact_source_sprite_sheet", "reference_only_opaque_board_not_sprite")
assert len(DOC["source_sheets"]) == 3

spines = [x for x in sprites if x["family"] == "word_search_spines"]
rewards = [x for x in sprites if x["family"] == "core_rewards"]
assert len(spines) == 72 and len(rewards) == 16
expected_variants = {"narrow", "regular", "collector"}
by_theme = {}
used_files = set()
for x in spines:
    theme, variant = x["theme"], x["width"]
    assert variant in expected_variants
    by_theme.setdefault(theme, set()).add(variant)
    expected_id = f"gm_ws_spine_{theme}_{variant}"
    assert x["id"] == expected_id
    source = ROOT / x["source"]
    assert x["source"].startswith("svg/spines/") and source.suffix == ".svg"
    assert x["source"] not in used_files
    used_files.add(x["source"])
    assert source.is_file(), f"Missing committed SVG: {source}"
    content = source.read_bytes()
    actual_sha = hashlib.sha256(content).hexdigest()
    assert actual_sha == x["source_sha256"], (x["id"], actual_sha, x["source_sha256"])
    root = ET.fromstring(content)
    assert root.tag == "{http://www.w3.org/2000/svg}svg"
    assert root.attrib.get("viewBox")
    assert b"<script" not in content and b"<foreignObject" not in content
    assert b"<image" not in content and b"xlink:href" not in content

assert len(by_theme) == 24
assert all(v == expected_variants for v in by_theme.values())
disk_svgs = {p.relative_to(ROOT).as_posix() for p in (ROOT / "svg/spines").glob("*.svg")}
assert disk_svgs == used_files, "Unindexed SVG sources found"

for x in rewards:
    assert x["source"] == "source/collection_reward_atlas_alpha.png"
    assert len(x["source_crop_xyxy"]) == 4
    a,b,c,d=x["source_crop_xyxy"]
    assert 0 <= a < c <= 1536 and 0 <= b < d <= 1024
    assert [c-a,d-b] == x["px"]

assert any("72 book spines" in note for note in DOC["notes"]), "Stale asset-count note"
print(f"PASS: {len(spines)} SVG book spines from {len(by_theme)} themes,"
      f" 16 archived reward sprites indexed, 88 unique asset IDs.")
print("External PNG and ZIP verification is separate; no APK resources were changed.")
