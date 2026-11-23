# WS51 — Birdsong: three progressively harder, distinct-word page manuscripts

> **Stage:** **Verified QA prototype (not published).** Exact-source V5 generation and BookCatalog checks passed in both independent Android/Art pipelines; all three board digests are recorded in the WS51 journal. The 23 new words remain unapproved editorial candidates.

## Player goal and design decision
The owner wants collectible, curated chapter puzzles with permanent boards after review, not general themed randomness masquerading as chapter themes. WS50 proved a 29-word species-only Birdsong chapter can build exact frozen pages, but its first page drew KINGFISHER, CHICKADEE, NUTHATCH, WARBLER and ORIOLE and shared **6** words with the second page and **11** with the third. This does not feel like good difficulty progression.

**WS51 authoring rule:** use a hand-curated target list **per page**, then let the **existing V5 generator** place those words. Generate each page from a separate source-hashed QA pack/ID; the puzzle's exact rows, paths, words and board SHA become frozen candidates for editorial review. There is **no** generator change, no automatic reroll on opening pages and no planted bonus words.

## The three new draft pages

| New QA page | Grid | Words | V5 direction rules | Target average length | Editorial style |
|---|---:|---:|---|---:|---|
| 1. Forest Friends | 12×12 | 12 | Relaxed; across/down | 4.58 letters | Common bird names plus short everyday birdlife terms |
| 2. Feathers and Flight | 15×15 | 16 | Classic; some reverse/diagonal | 5.94 letters | Familiar species and bird behaviors |
| 3. Birdsong Challenge | 20×20 | 24 | Expert; all eight directions | 8.50 letters | Longer species and bird ecology/life-stage vocabulary |

The **52 target words are distinct by editorial design**—there is no deliberate repeated answer across any page. The first page uses ROBIN, CROW, WREN, HAWK, FINCH, BEAK, WING, NEST, EGGS, CHIRP, FLOCK and FEATHER. The second uses familiar medium-length birds and related terms. The third deliberately introduces longer species names and birdlife terms. The actual V5 engine and book-catalog checks must run before claiming three valid grids.

## Source/provenance and review gates
- [Canonical QA manuscript](../data/lexicon/authoring/chapter-recipes-v1/birdsong-ws51-qa-manuscript.tsv) is a frozen input recipe, mirrored byte-for-byte under `core/src/test/resources/chapter-recipes/`.
- **29** terms are identical to already existing `en-woodland-v1` QA bird entries, including stable `gm...` IDs. These are **assistant QA-reviewed only** and not public-release-approved.
- **23** new birdlife terms, `ws51b...` IDs, are **assistant-generated proposals with `ws51_editorial_candidate_unapproved` provenance**. These are not copied from a third-party dictionary, not imported into any normal QA/runtime/release pack and **not owner-approved**. Semantic theme-fit and word familiarity require editorial/owner review.
- Exact **12 / 16 / 24**, 52 unique words, no within-page substring conflicts, and increasing mean target length are validated by separate Python and Kotlin source checks. New V5 QA page pack IDs and SHA-256 digests are used, keeping the older WS50 candidate IDs and all 63 original QA book pages unmodified.
- Kotlin makes all three actual new boards, generates each twice to prove deterministic hashes, checks **all target IDs/answers** match their authored page list, verifies zero cross-page repeats, runs BookCatalog/BookPageSession validation, then emits `core/build/ws51-birdsong-diverse-frozen-pages.tsv`. Both independent workflows upload byte-comparable results.

## Important publication/persistence safeguards
These are **test-only manuscripts, not playable BookQaPilot content**. Do not alter `app/src/debug/.../BookQaPilot.kt`, release BookQaPilot, `BookCatalog.requireCompatibleUpgrade`, published/QA page IDs or live saves. Work retains ownership of integration, artwork, device testing, edition naming, owner signoff and broad chapter QA. Any new published edition needs versioned content IDs, separate approved pack manifests and migration checks. Frozen QA prototype hashes are not permission to ship.

## Exact next milestone
Verify both WS51 pipeline conclusions and compare the TSV hashes/word lists. If V5 cannot place a particular curated list under the unmodified 80-attempt constraint, revise **this draft's list or seed/version** and rerun—do not weaken placement quality controls or change the generator. Then audit actual placement balance/repetition and ask Work to consider a new chapter-edition prototype. Other 17 chapter plans remain WS50 research only.

[WS50 verified baseline](devlog/2026-11-19_WS50_CHAPTER_RECIPE_BIRDSONG_LAB.md) · [WS51 detailed pass log](devlog/2026-11-20_WS51_BIRDSONG_DIVERSITY_QA.md) · [Work handoff](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
