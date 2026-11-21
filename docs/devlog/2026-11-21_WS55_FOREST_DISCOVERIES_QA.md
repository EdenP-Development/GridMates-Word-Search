# WS55 — Distinct Forest Discoveries: 3 frozen QA wildlife puzzles and bookwide audit

> **Verified technical QA — release/editorial approval still withheld.** Both independently run Android workflows passed at exact source `59004f28bc7a0bcafcd15e876278e56ed1464215`; full report payloads match byte-for-byte. No playable Library chapter or release permission.

## Verified execution — two successful Android QA workflows and byte-identical evidence

**Exact tested source:** `59004f28bc7a0bcafcd15e876278e56ed1464215`. Both independent workflows **SUCCESS** with no failed steps:
- [Standard Android/core run #44923963483](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44923963483): Python source/origin/old page isolation, Kotlin actual V5 frozen-page unit tests, core/app tests, lint and Android QA APK; all passed.
- [Independent art/debug/release run #44923963465](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44923963465): independent Kotlin source/board QA and debug/release art isolation and build gates; all passed.
- [Standard WS55 artifact #14715022908](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44923963483/artifacts/14715022908) and [independent WS55 artifact #14714533326](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44923963465/artifacts/14714533326) were **both downloaded**. Each contains **the same three byte-identical TSV payloads**. ZIP container checksums differ due to metadata, not report data.

| Output file | Bytes | SHA-256 |
| --- | ---: | --- |
| `ws55-forest-frozen-pages.tsv` | 2,076 | `8f7e83ec5dace1b80b145936fa3894abeae1613592c880e3ff23fbaea933ff8c` |
| `ws55-bookwide-overlap-delta.tsv` | 483 | `e1dce26c905dec737e623a9fd209307693849e18c0d16233bb1da868a934e267` |
| `ws55-bookwide-word-frequency.tsv` | 10,166 | `0935bb5b6c77a2fd1882afc02d516c22ecf599e4670d51299299304be88d6aeb` |

**Frozen new test-only Forest Discoveries board IDs and hashes:**

| QA page | Size/required words | V5 challenge | Frozen board SHA-256 |
| --- | --- | --- | --- |
| Woodland Neighbors | 12×12 / 12 | Relaxed | `1c5ebf0f9a1468c4f6d1bec8f5b61f3eecdc650ad827ccfb501c273c1ff742b2` |
| Signs Along the Path | 15×15 / 16 | Classic | `87bff0d7edcb9f5c7bec7f059ebaa73f6f8035c32f8502a9b3334c10042835e1` |
| Wildlife Field Notes | 20×20 / 24 | Expert | `93e8fe67538522fe4b9b0ac1631ed2d6610000c4f2bc6720d0d8969b27da0520` |

All three pages generated and reproduced with exactly the authored word ID sets. `BookCatalog`/`BookPageSessionRules` verified frozen identity. **Independent manual all-eight-direction scan** of the actual letter grids: every one of 52 required answers occurs on exactly **one** straight path within its respective 12/15/20-sized square board; no missing or multiply selectable required words. These pages remain test-only and have not been placed in the playable app.

**Book-wide verified word-frequency result:** six Woodland Chapters 3–8 research manuscripts have **312 required answer slots**, **233 unique answers**, **79 repeated appearances** beyond first use, total chapter-pair word intersections **82**, maximum overlap **26 (Under the Canopy vs Quiet Clearings)**. Forest Discoveries intersections: Ch3 Along Trail **12→0**, Ch4 Under Canopy **25→7**, Ch5 Birdsong **0→0**, Ch6 Stream **10→3**, Ch7 Quiet Clearings **20→0**. Frequency report confirms **147 original QA-source terms** and **86 new still-unapproved proposals** (WS51 Birdsong 23; WS54 Stream 33; WS55 wildlife 30). No answer appears in more than **3** researched chapters. Existing original QA words also require owner editorial/release review.

**Editorial hold remains:** Word semantics/familiarity, correct readable compound display labels, user-visible layout and Android touch/contrast require owner/editor acceptance. The source-approved-in-QA word list is **not production-published**. No changes to old WS50–54 QA prototype files, original 63 playable QA Library pages/IDs, saved progress, V5 runtime, scoring/bonus policy, released lexicon, app art, Nearby or Work/main. For phone review, Work should build a **new explicit versioned QA volume** with separate page IDs and save/upgrade regression; do not silently replace any existing ID or call this new release complete.

## Owner goal and current issue
The user wants real curated paperback-style chapter puzzles, with word lists meaningful to chapter titles and permanently fixed after owner editorial approval. Earlier QA versions used broad Woodland words across every chapter. WS54 technically verified a stream-focused manuscript and improved six-chapter distinctness to 202 words among 312 required target appearances. Despite that, old *Forest Discoveries* still shared **25 words with Under the Canopy** and **20 with Quiet Clearings**, often reusing tree/habitat vocabulary instead of wildlife subjects.

## Implemented separately
- New [52-row exact target manuscript](../../data/lexicon/authoring/chapter-recipes-v1/woodland-ws55-wildlife-manuscript.tsv) mirrored byte-for-byte under Kotlin test resources. Chapters 3–7 and all earlier WS52 research pages unchanged.
- Page 1 *Woodland Neighbors* (12 targets, 12×12 Relaxed), Page 2 *Signs Along the Path* (16, 15×15 Classic), Page 3 *Wildlife Field Notes* (24, 20×20 Expert). Increasing word length 4.50/6.13/9.54 and direction complexity. No same-page nested target and 52 unique target answers across all pages.
- **22** original canonical, assistant-QA-reviewed Woodland forest-animal words keep their IDs, display labels, assignments; **30 new wildlife and animal-sign entries** explicitly carry `ws55_wildlife_candidate_unapproved`. No new word added to main QA/release dictionary or incidental bonus recognition.
- `ForestDiscoveriesBookwideLabTest.kt` runs original V5 on three separate word-ID/sha-pinned QA-only packs and validates exact target list/IDs, repeatability, catalog restoration and historic six-chapter source audit, exporting frozen grids, overlap metrics and per-word source-provenance frequency.
- `test_ws55_forest_discoveries.py` independently guards the canonical source/label alignment, 22/30 split, 52 distinct, actual replay/code verification instrumentation, debug/release isolation and two CI upload paths.
- Both existing Android/core and art/release workflows run WS55 checks. No additional workflow, signing/Android version change, UI/assets, old saves or core engine edits.

## Actual source-list comparison (before CI)
| Source-only measure | WS54 | WS55 proposed |
| --- | ---: | ---: |
| Six-chapter distinct required answers | 202 | **233** |
| Total target appearances | 312 | 312 |
| Pairwise chapter intersections | 139 | **82** |
| Ch8 ↔ Under Canopy | 25 | **7** |
| Ch8 ↔ Quiet Clearings | 20 | **0** |
| Ch8 ↔ By the Stream | 10 | **3** |
| Ch8 ↔ Along Trail | 12 | **0** |
| Largest remaining chapter pair | 26 | **26**, unchanged Ch4 ↔ Ch7 |

All 233 are researched vocabulary, **not an approved production pack**; 86 entries are newly authored in WS51/54/55 and require owner/editor review. A higher unique-word count is only useful when new words are meaningful, correctly spelled, readable, age-appropriate and legally usable; none is automatically approved.

## Execution and release guard

The exact executed source SHA, both completed CI statuses, two independent artifact IDs and checked digests are recorded above. All three frozen grids passed target placement and deterministic reconstruction. The remaining open gate is **editorial approval and a physical Android phone review**, not another generator test.


- Source parent: verified WS54 [draft PR #166](https://github.com/PlayerEden/GridMates-Word-Search/pull/166), documented head `47214658a2cdbc803a82947bda79a62b80649edf`. Earlier source `1655d03cda06afd173d1e61e04fd8624f2bfc6ef` passed independent [Android/core #44764877945](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44764877945) and [art/release #44764877878](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44764877878).
- **WS55 exact test SHA/build conclusion:** source `59004f28bc7a0bcafcd15e876278e56ed1464215` passed both independent Android workflows (#44923963483 and #44923963465), with byte-identical frozen-board, overlap and frequency TSVs. Source-level WS55, Kotlin V5 frozen generation and BookCatalog restoration all passed.
- **No physical phone/owner content approval:** not performed. No new installable edition is being produced for the new manuscript.
- **Next:** review the remaining Chapter 4↔7 overlap and the 30 proposed wildlife words with the owner, then prepare a separately versioned playable Woodland QA edition for Android device and save-upgrade acceptance without rewriting existing page IDs.

[Full editorial and technical gate plan](../WOODLAND_FOREST_DISCOVERIES_EDITORIAL_PLAN_WS55.md) · [Work coordination](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
