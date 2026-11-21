# WS55 — Distinct Forest Discoveries: 3 frozen QA wildlife puzzles and bookwide audit

> **At source commit:** Isolated manuscript, source integrity test and actual V5 frozen-board Kotlin regression authored. Both independent GitHub workflows and actual board artifact hashes **pending**. No playable Library chapter or release permission.

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
- Source parent: verified WS54 [draft PR #166](https://github.com/PlayerEden/GridMates-Word-Search/pull/166), documented head `47214658a2cdbc803a82947bda79a62b80649edf`. Earlier source `1655d03cda06afd173d1e61e04fd8624f2bfc6ef` passed independent [Android/core #44764877945](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44764877945) and [art/release #44764877878](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44764877878).
- **WS55 exact test SHA/build conclusion:** pending. Do not use previously passed WS54 QA as proof of WS55 board generation.
- **No physical phone/owner content approval:** not performed. No new installable edition is being produced for the new manuscript.
- **Next:** read actual CI conclusions and inspect both independent frozen-board/overlap/frequency artifacts. If any 20×20 list cannot be placed with unchanged V5, revise only the new WS55 QA manuscript/seed and generate a *new* test-source digest. Then editorially review the remaining Chapter 4↔7 overlap and 30 proposed wildlife words.

[Full editorial and technical gate plan](../WOODLAND_FOREST_DISCOVERIES_EDITORIAL_PLAN_WS55.md) · [Work coordination](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
