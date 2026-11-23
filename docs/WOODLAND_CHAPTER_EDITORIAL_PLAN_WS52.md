# WS52 — Woodland chapter manuscripts: five more chapters, exact-source QA

> **Stage:** **15 real reproducible V5 boards verified in both independent QA workflows; editorial approval withheld** because of cross-chapter repetition and theme-fit issues. No playable content or release approval.

## WS52 measured editorial outcome
Both independent [Android/core #44670927912](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44670927912) and [art/release #44670927977](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44670927977) **passed** at `ee3c390fb62813273eff07be4da2e18259c09bf5`. All 15 frozen grids, pack IDs, SHA hashes and exact-target list digests are in the [verified WS52 journal](devlog/2026-11-20_WS52_WOODLAND_FIVE_CHAPTER_QA.md); both pipeline report files matched byte-for-byte.

The biggest **book-quality hold** is repetition across chapter boundaries: Chapter 3 and 7 share **48 of 52** words, Chapters 6 and 7 share 33, Chapters 6 and 8 share 30. The current Chapter 3 and 7 maps use precisely the same two subtopics. **Do not publish** or declare completed editorial design simply because V5 generates the pages. The next chapter-scope diversification must be a **new versioned editorial proposal**, not an in-place rewrite of old QA/playable pages or original WS50 results.

## Reason for the pass
The user chose **curated chapters and permanent, repeatable puzzle pages** instead of a randomly assembled list unrelated to a chapter title. WS50 scoped 18 chapter names to existing topic tags. WS51 actually produced a three-page Birdsong pilot, using 52 distinct targets across 12/16/24-word pages, with easy-to-hard progression; the 23 new Birdsong proposals were still not cleared for release.

This pass addresses the **five remaining Woodland QA chapter titles**. It does **not** edit the old 63 playable QA-book pages or their saved progress and rewards.

## Five chapters — 15 authored page recipes

| Existing Woodland chapter title | New test-only page themes (Relaxed → Classic → Expert) | Allowed source subtopics |
| --- | --- | --- |
| 3. Along the Trail | First Steps · Following the Path · Into the Wilderness | trails + habitats |
| 4. Under the Canopy | Trees Around Us · Branches and Neighbors · Canopy Secrets | trees + birds + forest animals |
| 6. By the Stream | Water's Edge · Creekside Wildlife · River Wilderness | habitats + forest animals |
| 7. Quiet Clearings | Little Clearing · Woodland Calm · Hidden Glades | habitats + trails |
| 8. Forest Discoveries | Animal Clues · Forest Life · Deep Woods Discoveries | forest animals + trees + habitats |

Every chapter has exactly **three new QA-only page manuscripts**: 12×12/12 target words, 15×15/16 targets, 20×20/24 targets. The page difficulty rules are **Relaxed / Classic / Expert** respectively. Pages use distinct versioned source-and-page-hash identities; the app's V5 generator is unmodified.

## Source-aware editorial construction
- Authoritative input: [260 target-word rows](../data/lexicon/authoring/chapter-recipes-v1/woodland-ws52-manuscript.tsv) and identical [read-only Kotlin fixture](../core/src/test/resources/chapter-recipes/ws52-woodland-manuscript.tsv).
- **All 260 page targets match original IDs, answer spellings and display names from the 159-answer Woodland QA wordpack**; 0 newly imported or auto-approved words. The original wordpack has not been modified.
- **52 distinct required target words per chapter**, with zero repeated target words *within that chapter*. Short familiar four-to-seven-letter words are favored in Page 1, and answer lengths increase on Pages 2 and 3; word ordering and target selection are **fixed by the manuscript**, not rerandomized by runtime browsing.
- Original topic metadata controls allowed subtopic: all words in each chapter are tagged to the specified permitted category; Page 1 contains at least 5 primary-category words, Page 2 at least 6, Page 3 at least 9. This verifies topic-tag **structural fit**; genuine artistic/semantic fit still needs a human editorial pass.
- **Book-wide vocabulary limitation:** 260 target slots use only **124 distinct answers across the five chapters** (although each individual chapter has 52 unique), because the subtopics overlap heavily. This is **not** a claim of book-wide uniqueness. Improving repetition across *chapters* needs more curated vocabulary and/or alternative chapter scope choices, not silently changing existing pages.
- The existing Birdsong WS51's 52 required answer slots are separately authored and not counted in the 260. All 63 preexisting QA BookQaPilot pages remain unchanged.

## Actual generator/compatibility checks implemented
- Kotlin `WoodlandChapterManuscriptTest` verifies the chapter-map join, source IDs/labels/topics, primary-category floor, word count, zero within-chapter duplicates, no within-page substring conflicts, page-size/length/difficulty progression.
- Constructs **15 separate, test-only V5 QA wordpacks**, one per page, with derived hashes and fixed QA IDs/seeds; calls the original unchanged V5 to place each exact list, reproduces same board at same seed, checks that every target ID/answer appears once. Builds a **new, test-only BookCatalog** for the five chapters and requires exact BookPageSessionRules reconstruction.
- Independent reports: `core/build/ws52-woodland-five-chapter-frozen-boards.tsv` (15 frozen page hashes, actual letter grids, target lists, metadata) and `core/build/ws52-woodland-chapter-quality.tsv` (15 primary-category/length/diversity metrics).
- Both Android/core and art/release workflows execute Python source/data/test-wiring guards and upload these exact two TSVs for byte comparisons. These are research artifacts; CI success is not a physical Android handset acceptance or final editorial review.

## Hard boundaries and next step
Do not modify `app/src/debug/java/.../BookQaPilot.kt`, its release counterpart, `BookCatalog.requireCompatibleUpgrade`, any historical page IDs/hashes, the app runtime generator, saved records/achievements, real art, Nearby protocol, bonus-word scoring or release vocabulary. The existing old 63 QA pages are still Work's branch authority.

**Next:** Confirm both independent CI outcomes and download/compare both TSVs; if any V5 page-specific QA manuscript fails placement, revise **only that new draft's word set/seed**, not V5 or historical pages. Then run a human editorial review of title fit, cross-chapter repetition (especially chapters 3 vs 7, 6 vs 8), and the frozen board aesthetics/legibility. Work can later integrate an approved **new versioned edition** and test Android migration and owner phone play.

[WS51 tested baseline](devlog/2026-11-20_WS51_BIRDSONG_DIVERSITY_QA.md) · [WS52 implementation journal](devlog/2026-11-20_WS52_WOODLAND_FIVE_CHAPTER_QA.md) · [Work handoff](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
