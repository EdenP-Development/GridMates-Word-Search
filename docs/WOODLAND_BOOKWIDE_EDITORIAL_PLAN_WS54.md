# WS54 — Woodland book-wide editorial quality: a genuine *By the Stream* chapter

> **Status:** **Three V5 frozen candidate boards verified; two independent Android/core + release/art QA pipelines succeeded.** Editorial and owner/public-release approval still withheld; all existing Library pages and saves untouched.

## Verified result and next quality hold

The executed source `1655d03cda06afd173d1e61e04fd8624f2bfc6ef` passed both [Android/core #44764877945](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44764877945) and [independent art/release #44764877878](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44764877878), and generated three identical-content QA puzzle report payloads across pipelines. [Exact board hashes, source checksums and full word-frequency findings](devlog/2026-11-20_WS54_STREAM_BOOKWIDE_EDITORIAL_QA.md) are available. The next owner/editor pass should prioritize 33 newly proposed stream terms and display spelling before any new edition, then remove the biggest remaining Chapter 4 ↔ 7/8 overlaps.

## Decision and source-based editorial problem
The user chose deliberately authored, permanent collectible puzzle-book chapters. Generator V5 can place and freeze a chosen target set, but the source QA Woodland book has only **159 existing words** and the earlier six-chapter research manuscripts contained **312 target slots / 168 unique answers**. That includes 23 previously proposed WS51 Birdsong terms which remain **editorial QA-only, not owner release approved**.

The [WS52 approved-in-technical-QA source report](devlog/2026-11-20_WS52_WOODLAND_FIVE_CHAPTER_QA.md) and the [WS53 Quiet Clearings revision](devlog/2026-11-20_WS53_QUIET_CLEARINGS_EDITORIAL_DIVERSITY.md) exposed obvious theme and repetition problems. The old *By the Stream* contained otherwise valid woodland answers such as MOOSE, BADGER, BOBCAT, SKUNK, HEDGEHOG, BLUEBERRY and UNDERGROWTH, because generator target pools were too broad. The chapter shared **30 words with Forest Discoveries**, **29 with Along the Trail**, and **21 with Quiet Clearings**, even though those should be different moods.

WS54 instead authors one **new, separate and expressly unapproved** Chapter 6 manuscript. A narrower stream-centered vocabulary makes the word search itself more meaningful, not just better at a numerical overlap score.

## Authored page contents

| Page | Title | Board / targets | Challenge | Avg target letters | Editorial subject |
| --- | --- | --- | --- | ---: | --- |
| 1 | Water's Edge | 12×12 / 12 | Relaxed | 4.42 | Familiar water features and common waterside animals |
| 2 | Life Along the Bank | 15×15 / 16 | Classic | 5.75 | Stream life, amphibians, waterside plants and movement |
| 3 | River Currents | 20×20 / 24 | Expert | 8.42 | Longer river-ecology, geology and flow words |

All **52 required answers** appear **once in this three-page manuscript**, without a same-page substring target collision. The 19 existing words retain their **original Woodland QA word ID, spelling, label and source subtopic/tier**. The 33 additional editor-suggested English streamlife words have `ws54s...` IDs and explicit `ws54_streamlife_candidate_unapproved` provenance; they are **not imported into a production wordpack, Daily/Quick Play, bonus dictionary, or any old QA page**. The 33 candidates must be reviewed for meaning, familiarity, regional fit, UI readability and release authorization by the owner/editor before use. Source manuscript [here](../data/lexicon/authoring/chapter-recipes-v1/woodland-ws54-stream-manuscript.tsv); identical isolated test fixture under `core/src/test/resources/chapter-recipes/ws54-stream-manuscript.tsv`.

## Honest source-manuscript before/after research

The baseline is the six-chapter research picture: WS52 Chapters 3,4,6,8, WS53 distinct proposed Chapter 7, and WS51 Birdsong Chapter 5. Replace **only** the QA comparison Chapter 6 with this new WS54 proposal; **do not change any of those original manuscripts or frozen page hashes**.

| Source-list metric | Earlier six-chapter set | With WS54 stream proposal |
| --- | ---: | ---: |
| Total required-word slots | 312 | 312 |
| Distinct answers across all six chapters | 168 | **202** |
| Word slots repeating an answer already used in another chapter | 144 | **110** |
| Sum of all 15 chapter-pair word intersections | 204 | **139** |
| Largest pairwise overlap | 30 | **26** |
| Ch6 ↔ Along the Trail (Ch3) | 29 | **10** |
| Ch6 ↔ Under the Canopy (Ch4) | 9 | **2** |
| Ch6 ↔ Birdsong (Ch5) | 1 | **0** |
| Ch6 ↔ Quiet Clearings (Ch7) | 21 | **3** |
| Ch6 ↔ Forest Discoveries (Ch8) | 30 | **10** |

Across five chapters excluding Birdsong, unique required words improve **129→163**. These are comparisons of authored *word identities*, not human gameplay, user score or bonus recognition. A whole-book distinct count of 202 **includes 23 WS51 and 33 WS54 editorial candidates NOT RELEASE APPROVED**. The worst remaining chapter overlap is **Under the Canopy vs Quiet Clearings: 26**; *Under the Canopy vs Forest Discoveries* still shares 25. Refining Chapter 8 is the logical next step—do not claim every chapter is editorially done.

## Genuine test and publication gates
- Kotlin `StreamChapterBookwideQualityLabTest` checks all 19 original IDs/tags, exact count and 33 explicitly unapproved proposals, page word length progression and target uniqueness, makes three separately versioned QA packs and generates each with **unchanged V5 twice**, asserts word IDs and answers and frozen `BookCatalog`/`BookPageSessionRules` digest reconstruction.
- Kotlin compares six full chapter lists and writes **three inspectable reports**: `core/build/ws54-stream-frozen-pages.tsv` (three exact letter grids, page IDs, seeds and SHA-256); `core/build/ws54-bookwide-overlap-delta.tsv` (repetition deltas); `core/build/ws54-bookwide-word-frequency.tsv` (each of the 202 answers, chapter appearance count, chapter IDs, and source/candidate provenance for editorial prioritization).
- Python `tools/asset_pipeline/test_ws54_stream_bookwide_quality.py` independently validates exact authoring mirror, original source IDs, new candidate status, original historical chapter baselines, numeric improvements and both CI upload contracts.
- Both independent standard Android/core and art/debug/release pipelines upload all three reports for byte comparison. **At creation CI is pending**, not presumed passed.
- No source changes whatsoever to original 63 QA Library pages, previously generated WS50/51/52/53 candidate manuscripts, `BookQaPilot` debug/release, runtime V5, any saved progress, score rules, natural bonus-word behavior, artwork, Nearby, released vocabulary, or Work/main branch. The WS54 page IDs are **entirely new and QA-only**.

## Editorial hold and follow-up
The prototype is not a player-ready public puzzle book. It relies on 33 new unapproved streamlife terms, and some (e.g., compound river terms) should be examined for readability and whether they actually belong in our accessible nostalgic paperback tone. The wider book still has 110 repeated target appearances and 26-word maximum chapter overlap. Before an actual versioned release edition: owner/editor approve the vocabulary, inspect actual board placements visually/on an Android phone, verify accessibility and profile/save migrations, then explicitly publish new stable IDs. **Do not silently replace existing played pages.**

**Next:** Verify true GitHub CI status and compare both independent artifacts; if a page fails V5 placement or target identity, change **only this manuscript's** test-only words/seeds. After successful QA, tackle Chapter 8 *Forest Discoveries* thematic identity and reduce overlap against Chapters 4 and 7.

[WS54 verification record](devlog/2026-11-20_WS54_STREAM_BOOKWIDE_EDITORIAL_QA.md) · [Work coordination](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
