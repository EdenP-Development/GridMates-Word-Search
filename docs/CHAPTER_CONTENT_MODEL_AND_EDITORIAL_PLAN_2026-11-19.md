# GridMates Word Search — chapter content model and editorial plan (WS50)

## Product decision (owner-directed)
Books should feel like *actual curated, collectible puzzle books*, not Quick Play with a chapter label. **Chapter names define a controlled, thematically relevant answer pool.** The game generator still lays out words fairly and reproducibly. The editorial team then reviews and permanently freezes each **exact page** (word list, grid, target paths, stable page ID, pack hash and board digest). Once an ID is published, reopening that page is the **same puzzle**, even after app updates.

Quick Play keeps new random puzzle generation from the global reviewed packs. Daily remains deterministic for a day and content version. Bonus words remain incidental dictionary-recognized discoveries; never add planted extras or make them required.

## Precise existing-app reality
- Current QA Library has 3 books × 8 chapters = 24 chapters and **63 pages**. The first two chapters per book contain the original three V2 pages; chapters 3–8 contain three V5 pages each.
- The existing V5 chapter pages use *the entire themed pack*, not title-specific subtopics. The editorial map below is **for future versioned pages**, not a silent content replacement.
- Regular future book-page sizes remain **12×12/12 targets**, **15×15/16**, and **20×20/24**. New chapters need stable page IDs and fixed board/answer data before publish.
- All 63 current QA pages, older save IDs, progress/awards, V1/V2/V3/V5 generation, artwork and Nearby remain untouched by WS50.
- **No production book content is currently release-authorized.** Current source reserves QA IDs forever and release `BookQaPilot` is disabled.

## Versioned chapter vocabulary map — proposed for editorial review
Authoritative authoring data: [18 planned chapters](../data/lexicon/authoring/chapter-recipes-v1/chapter-map.tsv), two or three related category scopes per chapter when a single category is too small. Single-category chapters are most distinctive. The primary subtopic is an editorial guide, not a hard selection quota in the current V5 generator. Candidate pools are drawn only from the existing topic-tagged, 670-word editorial QA edition. No external dictionary import.

| Book | Chapter | Chapter theme | Word categories |
| --- | --- | --- | --- |
| Woodland | 3 | Along the Trail | trails, habitats |
| Woodland | 4 | Under the Canopy | trees, birds, forest animals |
| Woodland | 5 | Birdsong | birds only |
| Woodland | 6 | By the Stream | habitats, forest animals |
| Woodland | 7 | Quiet Clearings | habitats, trails |
| Woodland | 8 | Forest Discoveries | forest animals, trees, habitats |
| Space | 3 | Night Sky | stars, observatories |
| Space | 4 | Our Solar System | solar system |
| Space | 5 | Ready for Launch | spacecraft, exploration |
| Space | 6 | Among the Stars | stars, solar system |
| Space | 7 | Mission Control | spacecraft, exploration, observatories |
| Space | 8 | Beyond the Horizon | exploration, stars, observatories |
| Garden | 3 | First Shoots | growing, plants |
| Garden | 4 | Flower Borders | flowers only |
| Garden | 5 | Garden Visitors | pollinators, flowers |
| Garden | 6 | Tools and Trellises | tools, growing |
| Garden | 7 | Growing Season | growing, plants, tools |
| Garden | 8 | A Flourishing Garden | flowers, pollinators, plants, growing |

## Editorial and gameplay rules
1. **Page contracts, not reseeding old pages.** For future content, choose a versioned, QA-reviewed chapter word pool, select target words, place them with deterministic V5, store exact puzzle board and target paths, and review the result before assigning publication IDs. Changing a list/pack/seed creates a **new** QA ID, never repurposes an old saved/published ID.
2. **Thematic integrity:** every required target must belong to at least one permitted chapter subtopic. Meaningful primary-topic emphasis may require a future explicit authoring selector. No hacky repeated topic IDs or production generator modifications.
3. **Progression:** page 1 = 12×12 Relaxed (12 targets), page 2 = 15×15 Classic (16), page 3 = 20×20 Classic/Expert (24), with clear increasing challenge and accessible directions. Page numbers and chapter titles stay readable.
4. **Repetition controls:** avoid duplicate target words *within* a page, quantify overlap between neighboring pages, and prefer diverse stems across chapters. The Birdsong pool currently has 29 bird answers for 52 target slots; some cross-page repetition is unavoidable without editorially growing that subtopic. Do not claim 3 pages have 52 unique bird words.
5. **Quality controls:** all target paths valid, hashes and recipe pack identity pin the board, no word-placement dead ends, content reviews (meaning/age appropriateness/typos), device readability and completion testing. Focus on playability rather than artificially injecting bonus words.
6. **Release gate:** the 670 QA vocabulary is assistant-reviewed only. Work must approve a separate production chapter manifest/asset release, preserve migration and save compatibility, and test real Android input. A green CI prototype does not publish any chapter.

## Concrete WS50 prototype
- Source-only `ChapterRecipeLabTest` audits each existing QA chapter name against the complete 18-chapter map and confirms source category coverage, including enough familiar terms for 12×12 and enough length-eligible candidates for 20×20.
- **Birdsong pilot:** filter the existing Woodland QA word pack to the **29 existing bird terms**, then use *unchanged* deterministic V5 generation to produce three **new QA-only pages** with 12, 16 and 24 targets. The pilot creates a **new scoped pack identity** derived from source hash and recipe and verifies exact reproducibility and catalog recognition.
- Report actual page word lists, rows, hashes, and adjacent target overlap in `core/build/ws50-birdsong-qa-preview.tsv`; output all 18 chapter-pool counts in `core/build/ws50-chapter-pool-coverage.tsv`. Both GitHub pipelines independently generate the same two TSV files for Work to inspect.
- These are **test-only frozen candidates**, not wired into the Library. Their GitHub preview IDs are never adopted as production IDs.

## Delivery order
**WS50:** confirm the map, build and test a three-page Birdsong prototype, collect outcomes and repetition metrics; keep Work's branch isolated.  
**WS51 candidate:** editorially increase shallow categories and add clear per-page word-set diversity if repeat rates are high; prototype other chapters on a separate versioned QA catalog.  
**Later Work integration:** promote approved new chapter manuscripts only as distinct versioned pages or fresh volumes (not replace existing progress), exercise Android save/upgrade and two-phone tests, obtain owner visual/content sign-off.

[Work coordination](https://github.com/PlayerEden/GridMates-Word-Search/issues/140) · [WS50 audit journal](devlog/2026-11-19_WS50_CHAPTER_RECIPE_BIRDSONG_LAB.md).
