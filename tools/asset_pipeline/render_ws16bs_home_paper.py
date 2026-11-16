#!/usr/bin/env python3
"""Original WS16BS Home watercolor artwork -> QA-only reproducible PNG."""
import hashlib, json, struct
from pathlib import Path
import xml.etree.ElementTree as ET
import cairosvg

ROOT=Path(__file__).resolve().parents[2]
REG=ROOT/"design/visual-system/ws16bs-art-register.json"
OUT=ROOT/"app/src/debug/assets/ws16bs"

def git_oid(raw):
    return hashlib.sha1(b"blob "+str(len(raw)).encode()+b"\0"+raw).hexdigest()

def main():
    reg=json.loads(REG.read_text())
    assert reg["debug_only"] and not reg["release_approved"]
    raw=(ROOT/reg["source"]["path"]).read_bytes()
    assert git_oid(raw)==reg["source"]["git_blob_sha1"],"Original WS16BS art changed"
    tree=ET.fromstring(raw)
    forbidden={"image","text","foreignObject","script","use"}
    assert all(n.tag.rsplit("}",1)[-1] not in forbidden for n in tree.iter())
    e=reg["export"]
    png=cairosvg.svg2png(bytestring=raw,output_width=e["width"],output_height=e["height"])
    assert png[:8]==b"\x89PNG\r\n\x1a\n"
    assert struct.unpack(">II",png[16:24])==(640,640)
    OUT.mkdir(parents=True,exist_ok=True)
    (OUT/e["filename"]).write_bytes(png)
    receipt={"debug_only":True,"release_approved":False,
             "source_sha256":hashlib.sha256(raw).hexdigest(),
             "export_sha256":hashlib.sha256(png).hexdigest(),
             "filename":e["filename"],"width":640,"height":640}
    (OUT/"manifest.json").write_text(json.dumps(receipt,indent=2,sort_keys=True)+"\n")
    print("WS16BS source-hash-pinned QA artwork:",e["filename"])

if __name__=="__main__":
    main()
