# WS51 — Three distinct and progressively harder Birdsong manuscripts

> **Status at authoring:** Isolated source + Kotlin regression + Python source guards committed. Exact-source CI and actual V5 board artifact output pending. No runtime gameplay, installed APK or release approval.

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
