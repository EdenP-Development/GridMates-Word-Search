# WS53 — Distinguish Quiet Clearings from Along the Trail without changing old puzzles

> **Status at authoring:** QA-only versioned editorial proposal and Kotlin board-generation regression committed. Exact-source CI **pending**, and nothing is playable or release approved.

## Goal and why it matters
The owner prefers real collectible books with distinctive, deliberately chosen chapter word lists and permanent page grids, while Quick Play remains random and natural incidental bonus words are never planted. The source-backed [WS52 manuscript audit](devlog/2026-11-20_WS52_WOODLAND_FIVE_CHAPTER_QA.md) found an editorial blocker: Woodland Chapter 3 **Along the Trail** and Chapter 7 **Quiet Clearings** shared **48 of their 52** required words because both were scoped to the same two categories, `habitats,trails`. The five-chapter combined book study had 260 required slots but only 124 distinct words. The generator had passed; the manuscript did not pass editorial distinctiveness.

WS53 introduces **one additional separately versioned, source-only Chapter 7 candidate** and leaves all WS52 pages and their hashes unchanged. It is an alternative *proposal*, not a replacement of any played page.

## Revised chapter identity

- **Chapter stays:** *Quiet Clearings*, not another general hiking or stream book.
- **Page 1:** *Moss and Fern* — 12×12, 12 common short answers (4.50 average letters), Relaxed across/down.
- **Page 2:** *Grove and Canopy* — 15×15, 16 intermediate-length words (5.94 average), Classic.
- **Page 3:** *Woodland Whispers* — 20×20, 24 longer words (8.21 average), Expert.
- **New source categories:** `habitats` (primary) and `trees`, replacing the proposed `habitats,trails` category pair **for this new WS53 candidate only**. This keeps woodland nature, moss, glades, trees, undergrowth, lichen and canopy central, avoiding another 52-word hiking/footprint chapter.
- All **52 required targets** come from the same original **159-entry Woodland QA word pack**, with existing stable word IDs and display spellings. **No new words, no outside dictionary and no release permission**. Each page has at least 5/6/9 primary habitat words, no within-page substring conflicts, 52 words unique across the chapter, and increasing word length/direction complexity.
- The original WS50 chapter map is intentionally **not edited**. The WS53 TSV is the standalone proposal for a future *new* QA/production edition after owner review, and is **not** the new runtime source of truth.

## Before/after editorial targets, source-manuscript comparisons (not human-play metrics)

| Comparison | Original WS52 Quiet Clearings | Proposed WS53 |
| --- | ---: | ---: |
| Targets shared with Along the Trail | 48 | **18** |
| Targets shared with Under the Canopy | 0 | 26 |
| Targets shared with By the Stream | 33 | **21** |
| Targets shared with Forest Discoveries | 12 | 20 |
| Unique words across the five chapters | 124 | **129** |
| Duplicate required targets within Chapter 7 | 0 | **0** |

A critical tradeoff is explicit: changing to trees reduces the egregious overlap with the Trail chapter, but naturally increases overlap with the tree-heavy *Under the Canopy* chapter. The maximum new chapter-pair overlap stays below 30 of 52, and the total five-chapter distinct pool increases by five. This is a measured **incremental** improvement, not a claim of zero repetition across the book.

## Implemented technical QA
- Canonical [WS53 authored 52-row candidate](../data/lexicon/authoring/chapter-recipes-v1/quiet-clearings-ws53-revision.tsv) and byte-identical Kotlin fixture `core/src/test/resources/chapter-recipes/ws53-quiet-clearings-revision.tsv`. Exactly the same original Woodland word IDs and topic assignments.
- New `QuietClearingsDiversityLabTest.kt` checks original source provenance, count, theme fit, difficulty progression, uniqueness, and page selection conflicts; uses the **unmodified V5** to generate each of three distinct QA-only packs twice, checks exact authored targets, stable frozen board hashes and BookCatalog/BookPageSession identity. Reports 3 actual grids/hashes and direct before-after comparisons to the existing WS52 manifest.
- Independent Python `test_ws53_quiet_clearings.py` verifies the exact source mirror, canonical tags/word IDs, 52-word constraints, old map isolation, original-manuscript baseline and quantitative overlap goals. Both independent Android/core and art/release GitHub pipelines generate research TSV artifacts for comparison.
- No user-facing book content was installed, no version bump or production code change is part of WS53.

## Why this is still not ready to publish
The combined five-chapter set still uses only 129 distinct words across 260 target slots. A future polished book must consider **both** meaningful title fit and repetition between every pair, plus accurate difficulty, reading familiarity, and visually fair word placement. Other chapters 4, 6 and 8 were not altered; WS51 Birdsong remains separately test-only. The original 63 QA Library pages, their saved progress, unlocks, BookQaPilot, published lexicon, generator V1–V5, score/bonus, UI/art and Nearby remain untouched.

**Follow-on step:** verify the actual two GitHub CI results and byte-identical TSV reports. Then audit broader book-wide repetition and consider a *chapter-semantic* editorial vocabulary expansion (e.g. naturally clearings-related flora and forest environment) rather than indiscriminately adding a generic dictionary, while preserving historical page IDs and save compatibility. Require owner review and physical Android play before a new release edition.

[WS53 verification journal](devlog/2026-11-20_WS53_QUIET_CLEARINGS_EDITORIAL_DIVERSITY.md) · [Work handoff](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
