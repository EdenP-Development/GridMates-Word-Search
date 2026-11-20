# WS50 — Thematic chapter content plan and Birdsong three-page QA prototype

> **Status:** Source-authored prototype in isolated draft; Kotlin and independent CI **pending**. No release APK or playable-book mutation intended.

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
