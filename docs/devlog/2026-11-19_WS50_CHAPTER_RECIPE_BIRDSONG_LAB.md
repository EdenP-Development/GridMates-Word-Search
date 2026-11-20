# WS50 — Thematic chapter content plan and Birdsong three-page QA prototype

> **Status:** **VERIFIED TEST-ONLY PROTOTYPE.** Both independent GitHub workflows passed at exact test source `0ea6d03bd0e4ef1c4e6e9f4aa700b4d7f8cc2e83`; two TSV report payloads match byte-for-byte. No playable-book, runtime, art or saved-page mutation.

## Verified execution, full 18-chapter coverage and three inspectable Birdsong pages

- **Exact test source SHA:** `0ea6d03bd0e4ef1c4e6e9f4aa700b4d7f8cc2e83` (initial source commit `d60201278c41a5380cecb70ec3d773b6ecdce7a6` plus one Python source-guard literal correction). Full independent **Android/core CI [#44666632435](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44666632435) SUCCESS** and **art/debug/release CI [#44666632455](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44666632455) SUCCESS**. Kotlin tests including exact Birdsong frozen reconstruction and three WS50 Python contract checks passed in both.
- Standard research archive [artifact #14597944207](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44666632435/artifacts/14597944207); independent archive [artifact #14598131567](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44666632455/artifacts/14598131567). **Both TSV files byte-identical across the two jobs**; outer ZIP bytes differ because archive metadata differs.
- `ws50-chapter-pool-coverage.tsv`: **18/18** actual chapter names have source-backed pools of 29–145 candidate words and enough length/familiarity candidates for the three book sizes. **1,873 bytes**, SHA-256 `5170430492caf409fe51a0fc6d26c5b768a43f3652c06021f37fa7b11d4bec0c`. Note: counts refer to eligible source pool sizes and do **not** independently prove three balanced, distinct, finished pages for all 18 chapters.
- `ws50-birdsong-qa-preview.tsv`: **3 real V5 generated, validated and reproducible QA-only frozen candidates**, 2,024 bytes, SHA-256 `6acd497bd46de9c4b7696c4a3eb1cd5e208d840773fb7691a3ea001047970ae6`.

| Birdsong page | Board/target count | Difficulty | Fixed seed | Board SHA-256 |
| --- | --- | --- | --- | --- |
| 1. Forest Birdwatching | 12×12 / 12 | Relaxed | 5171 | `1a04687169ebe6a1370de90485b05f1dd6431ca0f4dfb1cf19474908aab3e702` |
| 2. Feathers and Flight | 15×15 / 16 | Classic | 5208 | `c0676216952ff41dfa33610b297c1e5a1f0e37ab419769dd327ea09c3db83968` |
| 3. Birdsong Challenge | 20×20 / 24 | Classic | 5245 | `ac7877ae63645bb52cb48ce98cfef849e63891a1626cdb7e211d7ef8977befec` |

The preview uses pack ID `en-woodland-birdsong-qa-v1`, whose source is **29 original Woodland/birds tagged QA words**. All 52 target slots are in the bird category; the three pages collectively use all 29 distinct available bird answers. Adjacent overlaps are **6 words** for Page 1→2 and **11 words** for Page 2→3. **52 slots from 29 words necessarily require repeated appearances**, so publisher/editor should extend familiar bird/birdlife word variety, select less repetitive lists and especially check Page 1 word familiarity before adopting a polished volume. These repeats are not a test failure but they are a deliberate editorial hold.

**No playable Library page was added, no existing 63 page IDs/word layouts changed, no production content was cleared or approved.** A green Android build is not a physical handset signoff. Runtime generation, save files, bonus-word scoring and release vocabulary remain unchanged; the QA research pack/seed/manifest identity belongs to the lab and must not be silently promoted as production.

**Next exact action:** Plan more familiar bird-related (not just bird-species) candidate vocabulary and a page-level target-selection/repetition budget with human editorial review. Then use a *new* QA content ID/new book volume instead of rewriting old QA progress. Other 17 mapped chapters still require individual board audit and user review before production.

## Who, what, where, why
- **Owner direction:** Library books should be curated chapter experiences, not randomly assorted theme words under unrelated chapter titles. Keep puzzle generation to place words, freeze approved pages, and maintain random Quick Play. "Let's plan it and get to it."
- **Implementer:** Parallel Game Design chat, repository source/QA only; separate from paused main Work branch.
- **Location:** 18 chapter recipe rows, three-page Birdsong offline Kotlin prototype, independent source guards and CI report artifacts.
- **Motivation:** Existing code labels Chapter 5 "Birdsong" but selects target words from a whole Woodland pack; chapter title does not control the word pool. Old pages/saves must not silently change.

## Prior tested baseline
- WS49 [draft PR #161](https://github.com/PlayerEden/GridMates-Word-Search/pull/161), parent head `ec33ba32a002970ad719a7d96b975952bc12634c`; executed test code `57632ca1e8b08ace516ae76e2e0a2ba13f89e970`.
- [WS49 standard Android/core #44663712025](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44663712025) and [independent art #44663712042](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44663712042) passed and exported four byte-identical TSVs. No production changes.
- The most recent scored native emulator gameplay is WS45 v106, 18/18 Android 35 tests; owner phone acceptance remains open. The general Book QA v104 baseline is older and retains 63 pages; don't infer published content.

## Exact content experiment
- New owner-facing blueprint: [chapter content editorial model](../CHAPTER_CONTENT_MODEL_AND_EDITORIAL_PLAN_2026-11-19.md). Specifies distinct chapter subset vs. Quick Play, immutable pages, board sizes, progression, review gates and migration rules.
- `data/lexicon/authoring/chapter-recipes-v1/chapter-map.tsv`: proposed subtopic scope for all 18 QA chapters 3–8 in Woodland, Space and Garden. `core/src/test/resources/chapter-recipes/ws50-chapter-map.tsv` is byte-identical.
- Kotlin test `ChapterRecipeLabTest.kt` checks map authenticity, all category scopes and pool size. For Birdsong, uses the **29 existing Woodland birds**, scopes a new QA-only hash and pack ID, and generates *exactly three* unchanged-V5 sample puzzles with 12/16/24 target words. Compares deterministic rebuilt boards, catalog digests and precise required target IDs; emits two TSVs with word lists and grids.
- Python `test_ws50_chapter_map.py` checks title parity against existing QA Book pilot, original three volume IDs/seed families, release QA isolation and both CI report upload paths.
- Existing game/app/core production source, QA Book catalog, art, lexicon approvals, Daily, Quick Play, Nearby protocol and persisted state unchanged.

## QA/build receipt and blockers
- Source base: branch `feature/ws50-chapter-recipe-and-birdsong-lab-2026-11-19` from WS49 documentation head, stacked draft only.
- **WS50 CI at creation:** Pending; don't mark as passed based on prior WS49 QA.
- **APK:** None deliberately released or installed; Android workflows may automatically produce test packages.
- **Repetition risk:** Birdsong has 29 source words but its three new pages collectively require 52 target slots. A substantial repeated-word share is mathematically unavoidable; the preview reports actual adjacent overlap. Future editorial expansion may be needed before publishing a three-page chapter. Source QA does not substitute for owner reading/playing it.
- **Fallback:** unchanged existing 63 QA book pages and their save IDs; current verified artifact chain remains WS49 (data-only) / WS45 (actual Android bonus QA).
- **Next exact action:** Verify both WS50 CI outcomes and the exact generated TSV pairs. Inspect Birdsong page overlap and topic fidelity before recommending new book content or promotion.
