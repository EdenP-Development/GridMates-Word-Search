#!/usr/bin/env python3
"""Validate, filter, and export the GridMates MASTER_ASSET_INVENTORY.json.

This is asset planning metadata. It does not generate images, change games,
approve artwork, or move any files into Android runtime resources.
Usage:
  python3 design/master-asset-system/inventory_validator.py
  python3 design/master-asset-system/inventory_validator.py --scope core --priority P0 --list
  python3 design/master-asset-system/inventory_validator.py --scope theme --csv /tmp/theme_inventory.csv
"""
import argparse
import csv
import json
import re
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parent
JSON_PATH = ROOT / "MASTER_ASSET_INVENTORY.json"
WS17 = ROOT.parent / "art-library-v2" / "RESERVE_INDEX.json"
VALID_SCOPES = {"core", "word_search", "theme", "game_adapter", "reserve"}
VALID_STATUS = {"planned", "candidate", "approved", "integrated", "reserve", "deprecated"}
VALID_PRIORITIES = {"P0", "P1", "P2", "P3", "P4"}

def validate():
    doc = json.loads(JSON_PATH.read_text(encoding="utf-8"))
    prior = json.loads(WS17.read_text(encoding="utf-8"))
    rows = doc["assets"]
    assert doc["schema_version"] == 1
    assert len(rows) >= 2000, "Inventory accidentally shrank below the agreed abundant reserve"
    assert len({r["id"] for r in rows}) == len(rows), "Duplicate IDs"
    assert [r["id"] for r in rows] == sorted(r["id"] for r in rows), "IDs not sorted"
    expected_prefix = {
        "core": "gm_core_", "word_search": "gm_ws_",
        "theme": "gm_theme_", "game_adapter": "gm_adapter_",
        "reserve": "gm_reserve_",
    }
    for row in rows:
        assert re.fullmatch(r"gm_[a-z0-9_]+", row["id"]), row
        assert row["scope"] in VALID_SCOPES, row
        assert row["id"].startswith(expected_prefix[row["scope"]]), row
        assert row["priority"] in VALID_PRIORITIES, row
        assert row["status"] in VALID_STATUS, row
        assert row["render_type"] and row["family"] and row["name"], row
        assert isinstance(row["states"], list), row
        assert isinstance(row["target_games"], list) and row["target_games"], row
        assert isinstance(row["notes"], str), row
        assert row["status"] != "integrated", "Planning update cannot claim shipped assets"
    counts = {
        "total": len(rows),
        "by_scope": dict(Counter(r["scope"] for r in rows)),
        "by_status": dict(Counter(r["status"] for r in rows)),
        "by_priority": dict(Counter(r["priority"] for r in rows)),
        "theme_count": len(doc["theme_blueprint"]["theme_ids"]),
        "external_ws17_candidates": sum((r["source_asset_id"] or "").startswith("ws17:") for r in rows),
    }
    assert counts == doc["counts"], "Computed counts don't match committed counts"
    assert counts["external_ws17_candidates"] == prior["sprite_count"] == 60
    assert counts["theme_count"] >= 25
    keys = {r["source_asset_id"] for r in rows if r["source_asset_id"]}
    expected = {f"ws17:{family}.{sprite}"
                for family, sprites in prior["sprites_by_family"].items()
                for sprite in sprites}
    assert keys == expected, f"WS17 source keys differ: missing={expected-keys}, extra={keys-expected}"
    assert doc["source_archive"]["sha256"] == prior["binary_archive"]["sha256"]
    assert doc["source_archive"]["github_png_binaries"] is False
    assert doc["source_archive"]["prepared_png_candidates"] == 60
    for theme in doc["theme_blueprint"]["theme_ids"]:
        ids = [r["id"] for r in rows if r["scope"] == "theme" and r["family"] == theme
               and not r["source_asset_id"]]
        for component in doc["theme_blueprint"]["common_slots"]:
            assert f"gm_theme_{theme}_{component}" in ids, (theme, component)
        assert sum(x.startswith(f"gm_theme_{theme}_motif_") for x in ids) >= 8, theme
    return rows, counts

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--scope", choices=sorted(VALID_SCOPES))
    parser.add_argument("--priority", choices=sorted(VALID_PRIORITIES))
    parser.add_argument("--status", choices=sorted(VALID_STATUS))
    parser.add_argument("--list", action="store_true")
    parser.add_argument("--csv", type=Path, metavar="OUTPUT")
    args = parser.parse_args()
    rows, counts = validate()
    filtered = [r for r in rows
                if (not args.scope or r["scope"] == args.scope)
                and (not args.priority or r["priority"] == args.priority)
                and (not args.status or r["status"] == args.status)]
    print(f"PASS: {counts['total']} unique planned master slots, "
          f"{counts['theme_count']} theme packs, "
          f"{counts['external_ws17_candidates']} exact-source WS17 candidate links. "
          f"{len(filtered)} rows match the filter.")
    if args.list:
        for row in filtered:
            print(f"{row['id']}  ({row['priority']}, {row['status']}, {row['render_type']})")
    if args.csv:
        fields = ["id", "scope", "family", "name", "priority", "status",
                  "render_type", "target_games", "states", "source_asset_id", "notes"]
        args.csv.parent.mkdir(parents=True, exist_ok=True)
        with args.csv.open("w", newline="", encoding="utf-8") as stream:
            writer = csv.DictWriter(stream, fieldnames=fields)
            writer.writeheader()
            for row in filtered:
                flat = {**row, "target_games": ";".join(row["target_games"]),
                        "states": ";".join(row["states"])}
                writer.writerow(flat)
        print(f"CSV export written: {args.csv} ({len(filtered)} records)")

if __name__ == "__main__":
    main()
