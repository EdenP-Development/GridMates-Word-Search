#!/usr/bin/env python3
"""Validate every GitHub-backed editable WS21 asset source and catalog link.

Only GitHub SVGs + manifest are available to this tool. Full PNG raster, alpha,
and ZIP CRC verification must be run separately against the external archive.
"""
from pathlib import Path
from collections import Counter
import hashlib
import json
import re
import xml.etree.ElementTree as ET

ROOT=Path(__file__).resolve().parent
DOC=json.loads((ROOT/"manifest.json").read_text(encoding="utf-8"))
assert DOC["schema_version"]==1
assert DOC["pack_id"]=="gm_ws21_covers_seasons_core_reserve"
assert DOC["status"]=="candidate_not_shipped"
COUNT={"illustrated_rewards":16,"theme_book_covers":72,"theme_medallions":24,"core_ui_templates":24,
       "total":136,"editable_svgs":120,"reference_only_boards":2}
assert DOC["counts"]==COUNT
assert len(DOC["sprites"])==136
assert len(DOC["source_sheets"])==3
all_ids=[x["id"] for x in DOC["sprites"]]
assert len(all_ids)==len(set(all_ids))
source_paths=[]
families=Counter()
themes=Counter()
covers_by_theme={}
ui_by_template={}
for item in DOC["sprites"]:
    assert item["status"]=="candidate"
    assert item["has_alpha"] is True
    assert re.fullmatch("[0-9a-f]{64}",item["sha256"])
    assert re.fullmatch(r"gm_[a-z0-9_]+",item["id"])
    assert item["width_px"]>0 and item["height_px"]>0
    assert item["path"].startswith("sprites/") and item["path"].endswith(".png")
    families[item["family"]]+=1
    source=item["source"]
    if source and source.endswith(".svg"):
        assert source.startswith("svg/"),source
        src=ROOT/source
        assert src.is_file(),source
        source_paths.append(source)
        content=src.read_bytes()
        assert hashlib.sha256(content).hexdigest()==item["details"]["editable_svg_sha256"],item["id"]
        assert b"<script" not in content and b"<foreignObject" not in content
        assert b"<image" not in content and b"xlink:href" not in content
        root=ET.fromstring(content)
        assert root.tag=="{http://www.w3.org/2000/svg}svg"
        assert root.attrib.get("viewBox")
    else:
        assert item["family"]=="illustrated_rewards", item["id"]
        box=item["details"]["source_box_xyxy"]
        assert len(box)==4
        x0,y0,x1,y1=box
        assert 0<=x0<x1<=1266 and 0<=y0<y1<=1242
        assert source=="sources/ws21_illustrated_reward_seasonal_atlas_rgb.png"
    if item["family"]=="book_covers":
        d=item["details"]; theme=d["theme"]; variant=d["variant"]
        assert variant in ("standard","collector","pocket")
        covers_by_theme.setdefault(theme,set()).add(variant)
    if item["family"]=="medallions":
        themes[item["details"]["theme"]]+=1
    if item["family"]=="ui_templates":
        d=item["details"]
        assert d["tone"] in ("forest","cream","gold")
        ui_by_template.setdefault(d["template"],set()).add(d["tone"])
assert families==Counter({"illustrated_rewards":16,"book_covers":72,"medallions":24,"ui_templates":24})
assert len(covers_by_theme)==24 and all(v=={"standard","collector","pocket"} for v in covers_by_theme.values())
assert set(covers_by_theme)==set(themes) and all(v==1 for v in themes.values())
assert len(ui_by_template)==8 and all(v=={"forest","cream","gold"} for v in ui_by_template.values())
assert len(source_paths)==120 and len(set(source_paths))==120
actual_sources={p.relative_to(ROOT).as_posix() for p in (ROOT/"svg").rglob("*.svg")}
assert actual_sources==set(source_paths), "Missing or unregistered SVG source"
assert all(re.fullmatch("[0-9a-f]{64}",item["sha256"]) for item in DOC["source_sheets"])
assert all(x["role"] in ("illustration_atlas_rgb_cleanup_needed","reference_only_baked_text_not_sprite") for x in DOC["source_sheets"])
print("PASS WS21: 136 named candidates, 120 real and SHA-matched editable SVGs,")
print("24 themes x 3 cover styles, 24 medallions, 8 Core template families x 3 tones, 16 illustrated reward metadata records.")
print("External PNG archive and actual Android visual acceptance are NOT validated here.")
