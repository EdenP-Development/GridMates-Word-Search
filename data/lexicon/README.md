# Word-library authoring

Recovered in WS40 from the separate [WS10B2 branch](https://github.com/PlayerEden/GridMates-Word-Search/tree/fb023af6c31323961760439ff1f62b0226ec57a2).
This directory is an authoring workspace. It is not included in Android assets.

| Inventory | Meaning |
| --- | --- |
| 40 canonical draft words, 76 draft assignments | Small schema/example catalog |
| 33 proposed topic families | Planning metadata, not 33 playable books |
| 563 historic research suggestions | 532 review rows plus 31 holds |
| 36,213 pinned-source spelling roots | Reproducible quarantine, not shipped words |
| 0 approved playable words in this new catalog | Existing V1/V2/V3 APK word pools are separate and remain playable |

The goal remains 10,000 distinct approved playable answers over time. Count a
canonical word once, even when it belongs to more than one theme. Review each
word's spelling/display/provenance and each topic/subtopic/familiarity assignment
separately. Holds and rejections remain traceable. Do not automatically promote
dictionary matches, candidate rows or research proposals.

Run all authoring tests and validate the draft catalog:

```bash
python3 -m unittest discover -s tools/lexicon -p 'test_*.py' -v
python3 tools/lexicon/validate.py --data-dir data/lexicon
```

The current WS40 workflow reproduces source quarantine and the three review
sheets. The intake release, archive checksum and exact notice are pinned in
`intake/esdb_us_2026_02_25.json` and `licenses/`. External source rights remain
distinct from permission to inspect/review candidates.

After real editorial decisions are signed in both CSVs and the topic is active,
an immutable content snapshot can be produced:

```bash
python3 tools/lexicon/export_snapshot.py \
  --data-dir data/lexicon --pack-id en-woodland-v1 \
  --topics woodland --output-dir /tmp/gridmates-reviewed-packs
```

The unchanged draft catalog deliberately fails this export. The exporter writes
only already-approved words and assignments, includes source notices and a
canonical SHA-256, and refuses to overwrite an existing pack ID with different
bytes. It never edits approval flags or enables a pack in the app.

Next implement the first reviewed editorial batch, then a separately versioned
Android loader/generator with frozen fixtures, portable saves and LAN content
compatibility. Keep V1/V2/V3 IDs and Daily reconstruction stable. Follow the
[current product roadmap](../../docs/CURRENT_PRODUCT_ROADMAP.md) rather than old
branch-specific "next task" text.
