#!/usr/bin/env python3
"""WS16BS: verify debug-only original watercolor PNG and no art in release."""
import hashlib,json,sys,zipfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
dir_=ROOT/"app/src/debug/assets/ws16bs"
receipt=json.loads((dir_/"manifest.json").read_text())
expected={
  "assets/ws16bs/"+receipt["filename"]:receipt["export_sha256"],
  "assets/ws16bs/manifest.json":hashlib.sha256((dir_/"manifest.json").read_bytes()).hexdigest()
}
with zipfile.ZipFile(sys.argv[1]) as apk:
    actual={n for n in apk.namelist() if n.startswith("assets/ws16bs/")}
    assert actual==set(expected),(sorted(actual),sorted(expected))
    for name,sha in expected.items():
        assert hashlib.sha256(apk.read(name)).hexdigest()==sha,name
if len(sys.argv)>2:
    with zipfile.ZipFile(sys.argv[2]) as release:
        assert not any(n.startswith("assets/ws16bs/") for n in release.namelist()),"WS16BS QA artwork leaked into release"
print("WS16BS source-pinned Home art verified, release isolated:",len(sys.argv)>2)
