#!/usr/bin/env python3
"""Project the WS35 binding onto each existing illustrated front's visible hinge."""
import argparse
import json
from pathlib import Path
from zipfile import ZipFile

from PIL import Image

from ws35_library_bindings import ASSETS, MANIFEST as BINDING_MANIFEST, ROOT, sha

MANIFEST = ROOT / "design/visual-system/ws36-matched-books.json"
COVER_PREFIX = "ws36/covers/"
COVER_SIZE = (480, 640)
SPINE_LEFT = 12
SPINE_TOP = 65
SPINE_BOTTOM = 560


def wrap_binding(cover: Image.Image, spine: Image.Image, hinge: int) -> Image.Image:
    # Use the actual WS35 leather, tooling and emblem. Inset the cutout's
    # rounded outer pixels before projecting the leather around the front board.
    width = hinge - SPINE_LEFT
    texture = spine.crop((10, 15, spine.width - 10, spine.height - 15))
    texture = texture.convert("RGB").resize((width, texture.height), Image.Resampling.LANCZOS)
    wrapped = cover.copy()
    old, new = cover.load(), wrapped.load()
    for col in range(width):
        face = (col + .5) / width
        top = round(92 - 25 * face)
        bottom = round(568 - 12 * face)
        strip = texture.crop((col, 0, col + 1, texture.height))
        samples = strip.resize((1, bottom - top), Image.Resampling.LANCZOS).load()
        # A darker turn around the left edge and a short transition into the
        # original raised hinge. The existing page block is below the wrap.
        light = .78 + .16 * face
        for y in range(SPINE_TOP, SPINE_BOTTOM):
            x = SPINE_LEFT + col
            red, green, blue, alpha = old[x, y]
            if not alpha:
                continue
            mix = min(1.0, (width - col) / 3, (SPINE_BOTTOM - y) / 5)
            leather = samples[0, max(0, min(bottom - top - 1, y - top))]
            rgb = tuple(round(a * (1 - mix) + b * light * mix)
                        for a, b in zip((red, green, blue), leather))
            new[x, y] = (*rgb, alpha)
    return wrapped


def render(entry: dict, binding_hashes: dict) -> Image.Image:
    name, hinge = entry["theme"], entry["hinge"]
    with Image.open(ASSETS / "ws16ae/covers" / (name + ".png")) as source, \
         Image.open(ASSETS / "ws35/spines" / (name + ".png")) as binding:
        if source.mode != "RGBA" or source.size != COVER_SIZE:
            raise ValueError(f"Unexpected WS16AE cover format for {name}")
        if sha(source.tobytes()) != entry["source_cover_rgba_sha256"]:
            raise ValueError(f"WS16AE source cover pixels changed for {name}")
        if sha(binding.tobytes()) != binding_hashes[entry["binding_id"]]:
            raise ValueError(f"WS35 binding pixels changed for {name}")
        result = wrap_binding(source, binding, hinge)
        if result.getchannel("A").tobytes() != source.getchannel("A").tobytes():
            raise ValueError(f"Original book silhouette changed for {name}")
        face = (hinge, 0, source.width, source.height)
        if result.crop(face).tobytes() != source.crop(face).tobytes():
            raise ValueError(f"Front illustration or title changed for {name}")
        return result


def import_covers(manifest: dict) -> None:
    bindings = json.loads(BINDING_MANIFEST.read_text())
    binding_hashes = {item["id"]: item["rgba_sha256"] for item in bindings["bindings"]}
    for entry in manifest["covers"]:
        result = render(entry, binding_hashes)
        if sha(result.tobytes()) != entry["cover_rgba_sha256"]:
            raise ValueError(f"WS36 projected cover differs for {entry['theme']}")
        path = ASSETS / COVER_PREFIX / (entry["theme"] + ".png")
        path.parent.mkdir(parents=True, exist_ok=True)
        result.save(path, optimize=True)
        print(f"{entry['id']} {sha(path.read_bytes())} with {entry['binding_id']}")


def verify_apk(manifest: dict, debug: Path, release: Path | None) -> None:
    cover_prefix = "assets/" + COVER_PREFIX
    cover_paths = {cover_prefix + item["theme"] + ".png" for item in manifest["covers"]}
    spine_paths = {"assets/ws35/spines/" + item["theme"] + ".png" for item in manifest["covers"]}
    with ZipFile(debug) as apk:
        actual = {name for name in apk.namelist() if name.startswith(cover_prefix)
                  and not name.endswith("/")}
        if actual != cover_paths:
            raise ValueError(f"WS36 packaged cover mismatch: {actual ^ cover_paths}")
        for entry in sorted(cover_paths | spine_paths):
            source = ASSETS / entry.removeprefix("assets/")
            if apk.read(entry) != source.read_bytes():
                raise ValueError(f"WS36 packaged book art differs: {entry}")
    if release is not None:
        with ZipFile(release) as apk:
            if any(name.startswith("assets/ws36/") for name in apk.namelist()):
                raise ValueError("WS36 candidate covers escaped into the release APK")
    print("WS36 verified three source-matched cover/spine pairs"
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
        import_covers(manifest)


if __name__ == "__main__":
    main()
