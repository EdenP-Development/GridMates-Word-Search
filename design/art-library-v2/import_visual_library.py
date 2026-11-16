#!/usr/bin/env python3
"""Import the owner-reviewed ChatGPT art reserve without changing Android assets.

The art binary ZIP is stored in ChatGPT Library, not auto-imported into GitHub.
This tool verifies the exact package, then stages isolated candidate sprites.
Usage:
  python3 design/art-library-v2/import_visual_library.py ./GridMates_Word_Search_Visual_Library_v1.zip
  python3 design/art-library-v2/import_visual_library.py ./pack.zip --dest ./review-candidates
"""
import argparse
import hashlib
import json
from pathlib import Path, PurePosixPath
from zipfile import ZipFile, BadZipFile

EXPECTED_ZIP_SHA256 = "68fac0f6a40c7abbb3b75096692e74f016368218ffb2e8085e60a12f7636966e"
INDEX = Path(__file__).resolve().parent / "RESERVE_INDEX.json"

def sha256(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()

def verify_and_extract(archive: Path, dest: Path) -> None:
    data = archive.read_bytes()
    observed = sha256(data)
    if observed != EXPECTED_ZIP_SHA256:
        raise ValueError(f"Unrecognized ZIP SHA256: {observed}")
    index = json.loads(INDEX.read_text(encoding="utf-8"))
    dest = dest.resolve()
    with ZipFile(archive, "r") as z:
        bad = z.testzip()
        if bad:
            raise ValueError(f"Corrupted archive member: {bad}")
        names = z.namelist()
        for name in names:
            p = PurePosixPath(name)
            if p.is_absolute() or ".." in p.parts:
                raise ValueError(f"Unsafe archive member: {name}")
            if not (p.parts[0] in ("sources", "sprites", "previews", "tools") or
                    name in ("manifest.json", "README.md")):
                raise ValueError(f"Unexpected member: {name}")
        if len(names) != len(set(names)):
            raise ValueError("Duplicate archive member")
        manifest = json.loads(z.read("manifest.json"))
        sprites = manifest["sprites"]
        if len(sprites) != 60 or manifest["sprite_count"] != 60:
            raise ValueError("Unexpected sprite count")
        for sheet in index["sheets"]:
            name = "sources/" + sheet["source_file"]
            if name not in names or sha256(z.read(name)) != sheet["sha256"]:
                raise ValueError(f"Source sheet mismatch: {name}")
        for entry in sprites:
            member = entry["path"]
            if member not in names or sha256(z.read(member)) != entry["sha256"]:
                raise ValueError(f"Invalid candidate sprite: {member}")
            if member.startswith(("app/", "android/")):
                raise ValueError("App source must never be overridden by archive")
        if dest.exists() and any(dest.iterdir()):
            raise ValueError(f"Destination is not empty: {dest}")
        dest.mkdir(parents=True, exist_ok=True)
        for name in names:
            if name.endswith("/"):
                continue
            out = dest / name
            out.parent.mkdir(parents=True, exist_ok=True)
            out.write_bytes(z.read(name))
    print(f"Verified and staged {len(sprites)} candidate PNGs under {dest}")
    print("No game source, release resources, unlock IDs or gameplay were changed.")
    print("Review previews/contact_sheet.jpg before choosing Android assets.")

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("archive", type=Path)
    parser.add_argument("--dest", type=Path,
        default=Path(__file__).resolve().parent / "staged")
    args = parser.parse_args()
    verify_and_extract(args.archive, args.dest)
