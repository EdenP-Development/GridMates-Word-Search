# WS54 — Stream-theme manuscript and six-chapter Woodland variety audit

> **Stage:** **VERIFIED TECHNICAL QA ONLY.** Both independent Android pipelines passed at exact source `1655d03cda06afd173d1e61e04fd8624f2bfc6ef`, and all three internal artifact reports match byte-for-byte. These pages remain research-only and release-unapproved.

## Exact-source verification — both independent Android workflows green

**Executed code + CI source SHA:** `1655d03cda06afd173d1e61e04fd8624f2bfc6ef`, [standard Android/core workflow #44764877945](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44764877945) **SUCCESS**; [independent art/debug/release #44764877878](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44764877878) **SUCCESS**. Both independently passed WS54 Python source guards, core Kotlin generated/re-generated actual 3 V5 puzzles, exact 52 target IDs/word lists, new frozen `BookCatalog`/`BookPageSessionRules` digest identity, plus their standard Android/lint and art/release quality gates. **No owner phone test and no new playable chapter integration occurred**.

**Real downloaded report archives:**
- [Android/core research artifact #14643341480](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44764877945/artifacts/14643341480)
- [Independent art/release artifact #14644841080](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44764877878/artifacts/14644841080)

Their **three TSV payloads are byte-for-byte identical across the independently run workflows**. Outer ZIP hashes differ only because of metadata; each internal file was independently checked:

| Verified file | Bytes | SHA-256 |
| --- | ---: | --- |
| `ws54-stream-frozen-pages.tsv` | 2,014 | `1eba0201810c38fb4e35f4a488db244f74d447e6543d59386c642786b8707a87` |
| `ws54-bookwide-overlap-delta.tsv` | 512 | `570a05baa7bca2115ccec33be164afbb10d9a8796a6adead24bec5d984b8907a` |
| `ws54-bookwide-word-frequency.tsv` | 9,439 | `9c0edba05566ee42401ae40c5be92162d49a7fa9688a3de98c396eaa3752b78b` |

**Generated and frozen WS54 stream QA board SHA-256 values (all answer sets exact, IDs distinct):**

| Page | Board, targets and difficulty | Frozen board SHA-256 |
| --- | --- | --- |
| Water's Edge | 12×12 / 12, Relaxed | `7ac77588ce4bcda95d63df36c1859cce09cd937769ee7e3bb435d8d921d46ef6` |
| Life Along the Bank | 15×15 / 16, Classic | `033024feb4832f1c338931e7daf2d5bc605808166924eb92730c6fc0e7efb412` |
| River Currents | 20×20 / 24, Expert | `f3b0d08857383164ed9d6ed9507b2744c67c6f8f849e9a6c059b1612a95a292a` |

**Extra independent report inspection:** Read the actual grid rows; confirmed each grid is exactly 12×12, 15×15, 20×20 respectively. For each of 52 target answers, scanned all eight possible straight directions; every answer appears at **exactly one** path, with no missing or multiply placed target strings. This is a private manual verification of the archived grids, not a change to the test suite.

**Measured book-wide result, from the actual matched report content:** Six QA manuscript chapters total **312 target slots**, **168→202 distinct answers**, repeated appearances **144→110**, pairwise intersections **204→139**. The remaining 202-word research source includes **146 original Woodland QA answers** and **56 not-release-approved editorial candidates** (23 WS51 Birdsong and 33 WS54 stream). Of those 202, **120 occur in only one chapter**, **55 in two chapters**, **26 in three**, and **WILLOW in four**. Highest remaining chapter-pair overlap is **26**: *Under the Canopy*↔*Quiet Clearings*; *Under the Canopy*↔*Forest Discoveries* remains **25**. This is not a release-ready wordpack and the manuscript's new words still need human/owner review.

**Decision:** **Technical QA verified; editorial/release approval withheld.** Next: review and approve (or revise) the proposed 33 streamlife terms for semantic quality and exact display names (e.g. WATERLILY / RIVERSTONE may warrant spaced display labels); consider *Forest Discoveries* chapter-level vocabulary distinction, then use new explicitly versioned publication IDs with upgrade/backup regression and owner physical-device acceptance. Never silently change any saved old page, artwork, source pack or bonus-word algorithm.

## Why this is different from a generic bonus-word pass
The owner wants actual collectible, curated book chapters. [WS52](2026-11-20_WS52_WOODLAND_FIVE_CHAPTER_QA.md) technically generated 15 Woodland grids, and [WS53](2026-11-20_WS53_QUIET_CLEARINGS_EDITORIAL_DIVERSITY.md) reduced one near-duplicate pair. A six-chapter view shows stream Chapter 6 still looks like an indiscriminate Woodland list; answers like badger, hedgehog, and porcupine dilute the waterside idea and repeat other chapters. **Fix content quality and chapter identities first**, keeping incidental bonus words unplanted and Quick Play random.

## Isolated implementation
- Baseline parent WS53 documented head `5850661809e2e03796adf8cba2a0dde1d8937333`; its actual source `4c11c0b15c9fe9feab6b0275292d9ec43da2a7d7` passed [Android/core #44758251802](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44758251802) and [independent art/release #44758251892](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44758251892).
- [WS54 stream word manuscript](../../data/lexicon/authoring/chapter-recipes-v1/woodland-ws54-stream-manuscript.tsv): **52 distinct source-authored targets across three pages**, with 19 exactly reused original QA words/IDs and **33 original assistant-proposed streamlife terms marked `ws54_streamlife_candidate_unapproved`**. Three fresh source/version-hashed QA-only V5 page packs, no production/game data changes.
- New `StreamChapterBookwideQualityLabTest.kt`: original source word provenance, page difficulty/substring guard, actual three frozen V5 board generation + independent regeneration, exact ID/answer match, BookCatalog/BookPageSession reconstruction and **six-chapter** overlap/frequency reports.
- Independent `test_ws54_stream_bookwide_quality.py`: canonical original QA terms unchanged, 33 proposed terms do not accidentally enter original source, old WS51/52/53 comparison preserved, two Android CI upload contracts.
- No QA Book catalog edits, no API/release migration, no APK for installing this chapter, no target engine or bonus rule changes.

## Word-list study before actual board build

| Comparison | Prior six-chapter QA study | WS54 proposed stream chapter |
| --- | ---: | ---: |
| Distinct answers in six researched chapters | 168 | **202** |
| Total source targets | 312 | 312 |
| Sum of pairwise chapter overlaps | 204 | **139** |
| Shared *By the Stream*↔*Forest Discoveries* | 30 | **10** |
| Shared *By the Stream*↔*Along the Trail* | 29 | **10** |
| Shared *By the Stream*↔*Quiet Clearings* | 21 | **3** |
| Maximum chapter pair overlap | 30 | 26 |

**Editorial limitations:** 202 distinct answers includes 56 unapproved original WS51/WS54 proposals (23+33), not production-ready licensed text. Some compound words need owner reading. Under the Canopy/Quiet Clearings and Under the Canopy/Forest Discoveries still overlap 26 and 25 words respectively. The data-only study should produce verified V5 boards and reports before calling this a technical completion.

## Follow-up after independently verified CI
- Exact source, both verified workflows, three generated grids and TSV hashes are recorded above.
- Owner handset and actual gameplay/editorial approval: **not performed**; no new edition published.

[Full source/editorial policy](../WOODLAND_BOOKWIDE_EDITORIAL_PLAN_WS54.md) · [Work handoff](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
