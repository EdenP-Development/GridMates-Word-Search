# WS55 — Forest Discoveries: wildlife signs, not a second tree chapter

**Status:** test-only proposed manuscript and source/Kotlin QA wiring; independent CI not yet verified at writing. This is not a new playable chapter, a release dictionary approval, or an existing page replacement.

## Editorial reason
The earlier *Forest Discoveries* research manuscript contained **52 required answers** but reused 25 answers from *Under the Canopy* and 20 from *Quiet Clearings*. It included many unrelated tree names and stream terms. The owner wants a distinctive puzzle-game chapter whose words match its name, not an encyclopedia or an indiscriminate topic pool.

**New QA-only proposed chapter 8**, each page exactly authored (not V5 randomly selected):
1. **Tracks and Trails**, 12×12 / 12 Relaxed: approachable woodland animals and first tracks, e.g. BEAR, DEER, WOLF, FOX, TRACK, PAW.
2. **Signs of Wildlife**, 15×15 / 16 Classic: animals and visible traces, e.g. RACCOON, FOOTPRINT, SCRATCH, ANTLER, HOOFPRINT, PELT.
3. **Hidden in the Woods**, 20×20 / 24 Expert: longer woodland wildlife behavior and field signs, e.g. CAMOUFLAGE, NOCTURNAL, FORAGING, GNAWING, CLAWMARKS, TRAILCAMERA.

There are **52 unique targets**, 26 copied with original canonical Woodland QA word IDs/display labels/topic tiers and **26 newly proposed editorial words** with explicit `ws55_wildlife_signs_candidate_unapproved` provenance. All 26 new words need human review for spelling, appropriate tone, accessibility and whether compound words should display with spaces. The prototype is an ordinary word-search game, **not** a military training simulator or a real field guide.

## Measured manuscript comparison
Baseline = WS52 chapters 3,4 and old 8, WS51 Birdsong chapter 5, WS54 new stream chapter 6, WS53 revised Quiet Clearings chapter 7. Replace **only the research comparison Chapter 8**, never any frozen page in existing Library.

| Word-list metric | Before WS55 | Proposed WS55 |
| --- | ---: | ---: |
| Required target slots across six chapters | 312 | 312 |
| Unique answers | 202 | **231** |
| Repeated word appearances | 110 | **81** |
| Sum of 15 chapter-pair overlaps | 139 | **86** |
| Maximum chapter-pair overlap | 26 | 26 |
| Ch8 vs Along the Trail | 12 | **4** |
| Ch8 vs Under the Canopy | 25 | **7** |
| Ch8 vs Birdsong | 0 | 1 |
| Ch8 vs By the Stream | 10 | **1** |
| Ch8 vs Quiet Clearings | 20 | **1** |

**Important:** The new 231-word research set contains new unapproved editorial vocabulary from WS51, WS54 and WS55; it is **not** the existing approved release lexicon. Remaining major overlap **Under the Canopy ↔ Quiet Clearings: 26** and **Along the Trail ↔ Quiet Clearings: 18**; those are future chapter-editorial work, not WS55 defects.

## Technical and owner gates
- Canonical [manuscript](../data/lexicon/authoring/chapter-recipes-v1/woodland-ws55-discoveries-manuscript.tsv) and exact mirrored test fixture.
- Independent Python source guard validates 26 original IDs/tags, 26 clearly unapproved candidates, 12/16/24 distinct words, increasing mean lengths and exact whole-book overlap baseline.
- Kotlin `ForestDiscoveriesWildlifeLabTest` generates and re-generates each **real V5** board with distinct versioned test-only packs, checks every exact required word ID/answer, BookCatalog digest and page-session reconstruction; emits exact grid rows/hashes, overlap deltas, and a 231-answer provenance-frequency list.
- Both independent Android/core and art/release QA pipelines run checks and upload three reports. **Actual CI result pending**, not presumed successful.
- Keep all existing 63 playable QA Library pages and save IDs, released packs, V5 generator, bonus recognition, Quick Play/Daily, artwork, Nearby and Work/main untouched.
- Before a player-ready release: confirm both CI outcomes and report parity, owner review all proposed words, physical-phone grid/readability testing, explicit new edition IDs and saved-progress upgrade regression. No silent rewrite of existing played pages.

[WS55 development journal](devlog/2026-11-21_WS55_FOREST_DISCOVERIES_EDITORIAL_QA.md) · [Work coordination](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
