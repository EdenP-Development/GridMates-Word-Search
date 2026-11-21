# WS56 — Quiet Clearings: distinctive, peaceful woodland floor

**Stage:** New versioned QA-only chapter manuscript and automated validation ready; **two independent CI executions pending**. No playable changes or release approval.

## Why the chapter needs a second pass

After the previously verified [WS55 draft PR #167](https://github.com/PlayerEden/GridMates-Word-Search/pull/167), the six researched Woodland Chapters 3–8 have **312 target slots**, **233 distinct answers** and **82 total pairwise chapter intersections**. Their worst remaining overlap is **26 of 52 required words** between Chapter 4 *Under the Canopy* and Chapter 7 *Quiet Clearings*. All 26 are trees or tree parts, so the current clearings theme feels too much like a second canopy book.

WS56 creates a **new independent Chapter 7 proposal** about opening sunlight, moss, ground cover, meadow plants and the calm forest floor—not species of trees. It deliberately does not alter previously generated WS53 boards or the permanent 63 QA Library pages.

## Three authored chapter pages

| Page | QA puzzle | Difficulty | Editorial focus |
| --- | --- | --- | --- |
| **Open Glade** | 12×12, 12 targets | Relaxed | Familiar moss, fern, meadow, shade, petals and flowers; mean 4.92 letters |
| **Sunlit Spaces** | 15×15, 16 targets | Classic | Sunbeams, butterflies, light and clearing textures; mean 7.38 letters |
| **Woodland Stillness** | 20×20, 24 targets | Expert | Woodland floor plants, ground cover, meadow blooms, longer word-search vocabulary; mean 9.17 letters |

The 52 words are selected explicitly in the [WS56 canonical authoring manuscript](../data/lexicon/authoring/chapter-recipes-v1/woodland-ws56-clearings-manuscript.tsv), with identical fixture in `core/src/test/resources/chapter-recipes/ws56-clearings-manuscript.tsv`. Exactly **16** words match IDs, spelling, display and tier of the existing 159-answer Woodland QA source pack; the **36 new terms** are marked `ws56_clearings_candidate_unapproved`, not imported into any playable/bonus/release lexicon. Standardized display labels include **Wood Sorrel**, **Wild Ginger**, **Wood Anemone**. No repeated required word across the 3-page chapter, no same-page target-substring collisions, and mean answer lengths grow with page difficulty. Botany/accessible-vocabulary judgment remains a human editorial review step, not a claim of factual regional flora certification.

## Source-manuscript whole-book improvement

Baseline: WS52 Ch3–4, WS51 Birdsong Ch5, WS54 Stream Ch6, WS53 older Clearings Ch7 and verified WS55 Wildlife Ch8. Substitute this new candidate **only in a standalone research comparison**; do not change old IDs or boards.

| Metric | Before WS56 | WS56 candidate |
| --- | ---: | ---: |
| Required words across 6 chapters | 312 | 312 |
| Distinct answers | 233 | **264** |
| Repeated appearances beyond first | 79 | **48** |
| Sum of all 15 chapter-pair overlaps | 82 | **49** |
| Largest overlapping chapter pair | 26 | **14** |
| Quiet Clearings ↔ Under the Canopy | 26 | **0** |
| Quiet Clearings ↔ Along the Trail | 18 | **14** |
| Quiet Clearings ↔ Birdsong | 1 | **0** |
| Quiet Clearings ↔ By the Stream | 3 | **1** |
| Quiet Clearings ↔ Forest Discoveries | 0 | **0** |

**262? No: 264 is a research manuscript count, not approved public words.** The 264 unique answers include **122 unapproved editorial proposals** (23 WS51 Birdsong + 33 WS54 Stream + 30 WS55 Forest Discoveries + 36 WS56 Clearings), assuming the baseline provenance is unchanged, and need actual editorial approval before publishing. The existing canon/release wordpack is not expanded.

## Real V5 technical gates

- Kotlin `QuietClearingsBookwideLabTest` validates canonical source IDs, candidate provenance, display labels and progression; constructs 3 independent versioned QA-only page packs; runs **unmodified V5 twice** per page, validates exact 12/16/24 required target IDs and words, hashes the exact boards, and tests standalone `BookCatalog` + `BookPageSessionRules` frozen reconstruction.
- Writes three reports: `ws56-clearings-frozen-pages.tsv` (grids/target lists/hashes), `ws56-bookwide-overlap-delta.tsv` (before/after six-book metrics), `ws56-bookwide-word-frequency.tsv` (all 264 answers and chapter provenance).
- Python `test_ws56_quiet_clearings.py` verifies byte-identical manuscript fixture, all source IDs and unapproved tags, difficulty constraints, exact chapter overlaps, release isolation and both CI report upload contracts.
- Both independent Android/core and art/release workflows run the validation and export the three TSVs. **Technical verification remains pending until both runs and outputs are checked.**

## Safe publication boundary

This is **only** test and authoring code and documentation on a stacked draft branch, not the real playable catalog. Do not touch the original 63 QA Library pages/IDs/saves, earlier WS50–55 frozen QA evidence, user progress/bonuses, release vocabulary, V5 generator, Quick Play/Daily, UI/art, Nearby, or Work/main. When ready, an owner-reviewed new *versioned edition* must use new IDs and pass Android accessibility, backup/upgrade, save and on-device play QA.

[WS56 verification journal](devlog/2026-11-21_WS56_CLEARINGS_BOOKWIDE_QA.md) · [Work handoff](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
