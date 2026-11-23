#!/usr/bin/env python3
"""Reconstruct the WS35 master, import opaque book sprites, and verify APKs."""
import argparse
import hashlib
import io
import json
from pathlib import Path
from zipfile import ZipFile

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
MANIFEST = ROOT / "design/visual-system/ws35-library-bindings.json"
MASTER_DIR = ROOT / "design/visual-system/masters/ws35"
ASSETS = ROOT / "app/src/debug/assets"
ASSET_PREFIX = "ws35/spines/"
ALPHA_LUT = [max(0, min(255, round((value - 208) * 255 / (242 - 208))))
             for value in range(256)]


def sha(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def read_atlas(master: dict) -> Image.Image:
    data = b"".join((MASTER_DIR / part).read_bytes() for part in master["parts"])
    if sha(data) != master["sha256"]:
        raise ValueError("WS35 original atlas hash mismatch")
    atlas = Image.open(io.BytesIO(data))
    if (atlas.mode, atlas.size) != ("RGBA", tuple(master["dimensions"])):
        raise ValueError("Unexpected WS35 atlas format")
    return atlas


def isolate_binding(atlas: Image.Image, left: int, right: int) -> Image.Image:
    binding = atlas.crop((left, 0, right, atlas.height))
    # The original has partially opaque matte pixels in the gutters.
    # Keep the antialiased silhouette and make the leather cores opaque.
    alpha = binding.getchannel("A").point(ALPHA_LUT)
    bounds = alpha.getbbox()
    if bounds is None:
        raise ValueError("WS35 atlas crop contains no book")
    binding.putalpha(alpha)
    binding = binding.crop(bounds)
    # Leave two transparent pixels on the exported border for bitmap filtering.
    sprite = Image.new("RGBA", (binding.width + 4, binding.height + 4))
    sprite.paste(binding, (2, 2))
    return sprite


def import_bindings(manifest: dict) -> None:
    atlas = read_atlas(manifest["master"])
    for asset in manifest["bindings"]:
        sprite = isolate_binding(atlas, *asset["columns"])
        if (sprite.size != tuple(asset["dimensions"]) or
                sha(sprite.tobytes()) != asset["rgba_sha256"]):
            raise ValueError(f"WS35 sprite pixels differ from master mapping: {asset['id']}")
        alpha = sprite.getchannel("A")
        if min(alpha.crop((sprite.width // 3, sprite.height // 3,
                           sprite.width * 2 // 3, sprite.height * 2 // 3)).getextrema()) != 255:
            raise ValueError(f"WS35 leather core is not fully opaque: {asset['id']}")
        path = ASSETS / ASSET_PREFIX / (asset["name"] + ".png")
        path.parent.mkdir(parents=True, exist_ok=True)
        sprite.save(path, optimize=True)
        print(f"{asset['id']} {sprite.width}x{sprite.height} {sha(path.read_bytes())}")


def verify_apk(manifest: dict, debug: Path, release: Path | None) -> None:
    prefix = "assets/" + ASSET_PREFIX
    with ZipFile(debug) as apk:
        assets = {name for name in apk.namelist() if name.startswith(prefix)
                  and not name.endswith("/")}
        expected = {prefix + asset["name"] + ".png" for asset in manifest["bindings"]}
        if assets != expected:
            raise ValueError(f"WS35 debug asset mismatch: {assets ^ expected}")
        for entry in sorted(expected):
            source = ASSETS / entry.removeprefix("assets/")
            if apk.read(entry) != source.read_bytes():
                raise ValueError(f"WS35 packaged sprite differs: {entry}")
    if release is not None:
        with ZipFile(release) as apk:
            if any(name.startswith("assets/ws35/") for name in apk.namelist()):
                raise ValueError("WS35 candidate bindings escaped into the release APK")
    print(f"WS35 verified {len(expected)} source-matched debug bindings"
          + (" and release isolation" if release is not None else ""))


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--debug-apk", type=Path)
    parser.add_argument("--release-apk", type=Path)
    args = parser.parse_args()
    if args.release_apk and not args.debug_apk:
        parser.error("--release-apk needs --debug-apk")
    manifest = json.loads(MANIFEST.read_text())
    if args.debug_apk:
        verify_apk(manifest, args.debug_apk, args.release_apk)
    else:
        import_bindings(manifest)


if __name__ == "__main__":
    main()
