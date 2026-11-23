# WS51 — Three distinct and progressively harder Birdsong manuscripts

> **Status:** **VERIFIED TEST-ONLY MANUSCRIPT.** Two independent pipelines passed at the exact tested source commit. All three real frozen boards were generated and report bytes matched exactly; no runtime or playable book content changed.

## Verified WS51 source, frozen-board data and independent QA evidence

- **Exact source that actually ran:** `866713fe4b62e0bcf1266275e901f15a5f288270`. [Android/core workflow #44668581064](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44668581064) and [independent art/release workflow #44668581056](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44668581056) both **SUCCESS**. The source-contract checks, actual V5 three-puzzle generation, deterministic reconstruction, exact authored target match, zero cross-page repeats and BookCatalog/BookPageSession validation all passed. Both compiled QA packages, but **no new live Library chapter or native phone acceptance was performed**.
- [Android report artifact #14599437182](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44668581064/artifacts/14599437182) and [independent report artifact #14598912881](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44668581056/artifacts/14598912881) each contain the exact **same 2,029-byte TSV**, SHA-256 `a320f56d91f333db07a483a18edd9718a0d5fb8ec51664c86a6250177d543ec5`; ZIP container hashes differ because of container metadata. Both have 3 complete, real generated grids, the word lists, versioned pack IDs and immutable board digests.

| New Birdsong QA page | Size/targets | Directions | Mean target length | Generated board SHA-256 |
| --- | --- | --- | ---: | --- |
| 1. Forest Friends | 12×12 / 12 | RELAXED | 4.58 | `4ce671f364442084f8d32f4ac83e8d4f8473a21704da3defccfb922f473c17fd` |
| 2. Feathers and Flight | 15×15 / 16 | CLASSIC | 5.94 | `4bdaf533d73fb6d1b0571a32a4a7509089c06c6fe801c2a5828d5cae3a2ed36e` |
| 3. Birdsong Challenge | 20×20 / 24 | EXPERT | 8.50 | `fee1fe81947b567d45e781ae98f0b49c567490700e235e4b36f8474d53be7d35` |

### Before/after — exact editorial improvement, not player metrics
- The former WS50 52-slot proposal had only **29 unique words**, adjacent-page repeats **6** then **11**, and average required length **7.17, 7.56, 6.96** from Pages 1–3. The new WS51 52-slot proposal contains **52 unique words**, adjacent repeats **0/0**, and average lengths **4.58, 5.94, 8.50**, a genuine easy-to-hard progression in length and direction difficulty.
- All **29** original Woodland bird answers and canonical IDs survive verbatim; **23** newly proposed birdlife terms are explicitly **`ws51_editorial_candidate_unapproved`**, with no claim of human editorial clearance or license-based release permission.
- The original **63 QA book pages, all saved page IDs, runtime generator, app/art assets, Daily/Quick Play, score/bonuses, Nearby and release catalog are untouched**. These 3 boards are isolated *QA test-only candidates* with their own new pack IDs and page identities, not additions to the playable Library.
- **Before production:** owner editorial word review (meaning, regional fit, maturity), visual/device playtest for three page difficulties and board readability, plus explicit new book/edition manifest + compatibility tests. No in-place replacement of any existing page.

## Who and what
- **Owner goal:** Make named book chapters intentionally curated and permanent while leaving random Quick Play intact.
- **Reason for this pass:** WS50's technically valid Birdsong pages had unnecessarily advanced birds on the first page and repeated 6 and 11 answers between adjacent pages. Only 29 original bird names were available for 52 target slots.
- **Change owner:** Game Design chat, test-only branch stacked on [WS50 PR #162](https://github.com/PlayerEden/GridMates-Word-Search/pull/162). Work remains paused/separate.
- **Source base:** WS50 documented head `b102353bae017b1aa86360ec66734e4241e6e076`; tested WS50 source `0ea6d03bd0e4ef1c4e6e9f4aa700b4d7f8cc2e83` passed [standard #44666632435](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44666632435) and [independent #44666632455](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44666632455).

## Implementation
- New [WS51 editorial manuscript](../../data/lexicon/authoring/chapter-recipes-v1/birdsong-ws51-qa-manuscript.tsv) plus exact mirrored Kotlin test resource: 3 pages 12/16/24, 52 unique answers, split across 29 original stable-ID QA bird words and **23 original assistant-proposed birdlife candidate terms** explicitly marked **unapproved**.
- Page 1 limits required targets to 4–7 familiar letters; Page 2 mixes medium familiar terms; Page 3 uses longer words and EXPERT all-eight-direction rules. The authored page list has zero repeated targets, no within-page substring conflict, and average lengths 4.58/5.94/8.50.
- New source-only `BirdsongDiverseManuscriptTest.kt` constructs one distinct QA-only V5 pack for each page, stamps IDs and pack SHA, generates real grids twice, checks exact target lists and all 52 unique appearances, validates with `BookCatalog` and reconstructs via `BookPageSessionRules`, writes one inspectable TSV of frozen grids and hashes.
- Separate `test_ws51_birdsong_manuscript.py` verifies input source integrity, old 29 stable IDs, 23 unapproved new entries, QA/release isolation and both report upload routes.
- Both Android/core and art/release GitHub workflows run source guards and publish the data-only Word Search manuscript artifact. No extra CI workflow or Android app version bump.

## Scope and evidence caveat
- **Intentionally untouched:** All 63 existing QA pages, BookQaPilot debug/release, app/core *production* Kotlin, QA/release packs, saved records, profile rewards, artwork, Nearby, V1–V5 generator behavior and unlisted-bonus scoring.
- **No user-phone test** or owner reading of the new proposed words, thus the new page/word edition cannot be promoted to release automatically.
- **Open at writing:** exact-source CI, board generation success, native Android acceptance, independent TSV report hash comparison, potential placement collisions. Previous WS50 is green baseline; source definitions alone do not prove V5 generation succeeds.
- **Next:** Review concrete CI and table output; if V5 placement fails, fix the **draft** recipe/seed, not any existing generator. Document hashes and actual page difficulty before Work integration.

[Full WS51 design policy](../BIRDSONG_CHAPTER_EDITORIAL_PLAN_WS51.md) · [Work coordination](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
