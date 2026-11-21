# WS55 — Forest Discoveries: wildlife observation chapter, full Woodland book QA

> **Stage:** **Both independent Android/core and art/release QA workflows succeeded with actual reproducible V5 boards.** Research-only; word/editorial and playable release signoff withheld. Historic Library pages/saves untouched.

## Verified WS55 research outcome

Both [Android/core #44923963483](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44923963483) and [independent art/release #44923963465](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44923963465) passed on exact source `59004f28bc7a0bcafcd15e876278e56ed1464215`. All three real frozen grids, complete target words and source identity were verified, and three independent report payloads matched byte-for-byte. [Complete WS55 board SHA/report checksums and frequency audit](devlog/2026-11-21_WS55_FOREST_DISCOVERIES_QA.md).

**Recommendation:** Finish the outstanding Chapter 4/7 overlap and review all 86 newly proposed words in a focused editorial pass; then build **one new playable QA versioned Woodland edition** and test on an actual phone. Continuing research indefinitely without owner device play is a weaker next use of effort.

## Why this milestone matters
The owner wants collectible chapter books with intentionally chosen answer lists and permanent reproducible page layouts, not randomly selected whole-theme words under misleading chapter titles. In verified WS54 [draft PR #166](https://github.com/PlayerEden/GridMates-Word-Search/pull/166), the themed stream chapter reduced bookwide duplication and passed true V5 board QA. Yet Chapter 8 *Forest Discoveries* continued to repeat a lot of generic canopy/tree/stream vocabulary from other chapters: 25 required words also appeared in *Under the Canopy*, 20 in *Quiet Clearings* and 10 in *By the Stream*. Source overlap reports identified those words precisely.

WS55 makes **one distinct versioned QA-only candidate** for Chapter 8 focused on forest animals, tracks, feeding signs, life stages and wildlife field observation, rather than another list of pinecones and forest features. Preserve the old WS52 Ch8 board identities and WS54's other five research manuscripts.

## Three new proposed pages
| Page | Title | Size/targets | Difficulty | Mean answer length | Editorial focus |
| --- | --- | --- | --- | ---: | --- |
| 1 | Woodland Neighbors | 12×12 / 12 | Relaxed | 4.50 | Familiar species and visible animal traits (PAWS, HOOF, CLAWS) |
| 2 | Signs Along the Path | 15×15 / 16 | Classic | 6.13 | Mammals and recognizable tracks, feeding marks, signs |
| 3 | Wildlife Field Notes | 20×20 / 24 | Expert | 9.54 | Species plus nocturnal behavior, camouflage, foraging and field observation |

**52 required answers, all unique within the three-page chapter.** The authored lists contain **22 existing canonical original Woodland QA forest-animal words** whose original IDs, labels and topic/tier are unchanged, plus **30 newly proposed wildlife-signs and ecology answers marked `ws55_wildlife_candidate_unapproved`** with separate QA-only IDs. These **are not released or owner-approved words**. Compound answer spellings are contiguous for puzzle play; display labels such as *Animal Tracks*, *Wildlife Signs*, *Food Chain*, *Camera Trap* and *Nest Building* use readable spaces while keeping target-letter identities exact. The owner/editor must still review concept difficulty, wording and visual display.

[Canonical WS55 manuscript](../data/lexicon/authoring/chapter-recipes-v1/woodland-ws55-wildlife-manuscript.tsv) has exact mirrored source under `core/src/test/resources/chapter-recipes/ws55-wildlife-manuscript.tsv`. No source/release pack is modified.

## Source-based full-book impact
Baseline is six earlier QA manuscripts: WS52 Chapters 3 and 4 and old 8, WS51 Birdsong Chapter 5, WS54 stream Chapter 6, and WS53 distinct Quiet Clearings Chapter 7. Only **this new, versioned Ch8 proposal** is swapped into *the analytical comparison*, not into any actual playable book or old test fixture.

| Editorial comparison | Verified WS54 manuscript set | Proposed WS55 set |
| --- | ---: | ---: |
| Total required answer appearances | 312 | 312 |
| Distinct required answers across six chapters | 202 | **233** |
| Repeated required appearances beyond first | 110 | **79** |
| Sum of all 15 chapter-pair intersections | 139 | **82** |
| Worst remaining pairwise intersection | 26 | **26** (Under the Canopy / Quiet Clearings; unchanged) |
| Forest Discoveries ↔ Along the Trail | 12 | **0** |
| Forest Discoveries ↔ Under the Canopy | 25 | **7** |
| Forest Discoveries ↔ Birdsong | 0 | **0** |
| Forest Discoveries ↔ By the Stream | 10 | **3** |
| Forest Discoveries ↔ Quiet Clearings | 20 | **0** |

All figures are *authored word-set comparisons*, not human playtest feedback or official release vocabulary. **233 unique words includes 86 unapproved proposed words**: 23 from WS51 Birdsong, 33 from WS54 Stream, 30 from WS55 wildlife, with 147 original QA entries from the 159-word Woodland pack. No bonus words are planted/guaranteed to manufacture rewards. Book-only words stay separate from Quick Play, Daily and bonus recognition.

## Technical checks and editorial release fence
- Kotlin `ForestDiscoveriesBookwideLabTest` checks source provenance, original canonical IDs/labels/topics, explicit unapproved status, page sizes, no missing/duplicate/nested selected targets and increasing mean answer length. It builds three *new, QA-only* page-specific content-hashed `ReviewedWordPack` objects, uses **unchanged V5** to generate each board twice with the same seed, asserts exact original authored target IDs/answers and frozen SHA-256 board digests, and runs BookCatalog/BookPageSession reconstruction.
- Kotlin emits three actual report TSVs: `ws55-forest-frozen-pages.tsv` (grids, target lists and hashes), `ws55-bookwide-overlap-delta.tsv` (before/after all comparison dimensions), and `ws55-bookwide-word-frequency.tsv` (every one of 233 distinct answers, chapter appearances and which of 86 proposed terms lacks release approval).
- Independent Python `test_ws55_forest_discoveries.py` checks all 52 source rows, 22/30 provenance split, exact mirror, historical manuscripts intact, strict numerical claims, old debug/release BookQaPilot fence and both independent CI report wiring paths.
- Two existing GitHub Android/core and art/debug/release workflows will independently execute source/core QA and upload the same reports. **CI pending at initial commit; no claim of successful generated boards until the outputs and job conclusions are actually inspected.**

Do NOT replace the original 63 debug QA Library pages, release BookQaPilot (disabled), saved records, previous WS51–54 test-only candidates, WordSearchV5 generator/puzzle hashes, users' scores/achievements, bonus-word dictionary, Quick Play, Daily, Nearby, UI, portraits, app version or active Work/main branches. No APK of this new chapter is installed. Future publication must be a separately authorized, fully frozen **new versioned edition** and explicitly tested with save backups and actual Android phone usability.

**Next after CI:** verify exact grid/word/path/frequency artifacts match between pipelines; review how 30 proposed terms read on older-user phone screens; then address the last **26-word overlap** between Under the Canopy and Quiet Clearings as a separate versioned content project rather than overwriting old saved pages. Prepare owner-visible chapter preview and visual audit before full Woodland release approval.

[WS55 verification journal](devlog/2026-11-21_WS55_FOREST_DISCOVERIES_QA.md) · [Work integration context](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
