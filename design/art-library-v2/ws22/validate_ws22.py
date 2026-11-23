#!/usr/bin/env python3
"""Source-backed WS22 QA: verifies real GitHub PNGs and editable SVGs.

Run after restore_atlas.py. This is not Android gameplay QA or visual approval.
"""
from pathlib import Path
from collections import Counter
import json, hashlib, struct, zlib
import xml.etree.ElementTree as ET

root=Path(__file__).resolve().parent
data=json.loads((root/"manifest.json").read_text(encoding="utf-8"))
assert data["schema_version"]==1 and data["pack_id"]=="gridmates_ws22_theme_motifs_prestige"
assert data["status"]=="candidate_only"
assert data["counts"]=={"motif_pngs":16,"premium_frame_svgs":24,"total":40}
assert len(data["entries"])==40 and len({x["id"] for x in data["entries"]})==40
source=root/data["source_sheet"]["path"]
assert source.is_file(),"Run python3 restore_atlas.py first"
assert hashlib.sha256(source.read_bytes()).hexdigest()==data["source_sheet"]["sha256"]
assert source.read_bytes().startswith(b"\x89PNG\r\n\x1a\n")

shapes,tiers=Counter(),Counter()
png_count=svg_count=0
for entry in data["entries"]:
    path=root/entry["path"]
    assert path.is_file(),f"Missing checked-in asset: {entry['path']}"
    content=path.read_bytes()
    assert hashlib.sha256(content).hexdigest()==entry["sha256"],entry["id"]
    assert entry["status"]=="candidate_not_packaged"
    if entry["format"]=="PNG":
        png_count+=1
        assert entry["id"].startswith("gm_theme_")
        assert content.startswith(b"\x89PNG\r\n\x1a\n")
        length=struct.unpack(">I",content[8:12])[0]
        assert content[12:16]==b"IHDR" and length==13
        width,height,depth,colour=struct.unpack(">IIBB",content[16:26])
        assert [width,height]==entry["size_px"]
        assert depth==8 and colour==6,entry["id"] # RGBA
        assert entry["source_cell"][0] in range(4) and entry["source_cell"][1] in range(4)
        shapes[entry["family"]]+=1
    elif entry["format"]=="SVG":
        svg_count+=1
        assert entry["id"].startswith("gm_core_prestige_")
        node=ET.fromstring(content)
        assert node.tag=="{http://www.w3.org/2000/svg}svg"
        assert node.attrib.get("viewBox")=="0 0 256 256"
        for forbidden in (b"<script",b"<foreignObject",b"<image",b"xlink:href"):
            assert forbidden not in content
        tiers[entry["variant"]]+=1
    else:
        raise AssertionError(entry["id"])
assert png_count==16 and svg_count==24
assert shapes==Counter({"woodland":4,"ocean":4,"garden":4,"space":4})
assert tiers==Counter({"bronze":6,"silver":6,"gold":6,"emerald":6})
print(f"PASS WS22: actual {png_count} PNGs hashed, {svg_count} editable SVGs hashed,")
print("original source atlas restored and SHA256 verified, 4 theme sets and 4 prestige tiers verified.")
print("Artwork remains unshipped; physical small-scale clarity/alpha-edge QA and owner placement open.")
