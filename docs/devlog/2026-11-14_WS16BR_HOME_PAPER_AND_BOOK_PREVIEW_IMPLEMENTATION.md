# WS16BR — Reusable art crosswalk, richer Home paper, compact book preview

**Date:** 2026-11-14. **Status:** code committed, exact-source GitHub QA and owner phone acceptance pending. **Branch:** `feature/ws14b-style-anchor-and-art-direction-2026-11-08`; [draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131).

## Who / why / prior checkpoint
- Owner's concept and subsequent instruction: reconcile what already exists, add a subtle illustrated-paper finish to Home, and reduce the mismatch between the polished walnut cabinet and the relatively plain, oversized pulled-book dialog. Preserve game data, original art, and existing v74/v75 cabinet layout.
- Work performed by ChatGPT through connected GitHub; not a user or emulator hardware test.
- Starting head: audited docs-only `8ac504d2a0ccc753dd3313806bb8065dd87b419a`; last fully verified QA package **v75** (Android [#43635464423](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43635464423), independent art [#43635464422](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43635464422)), built on `6f0ccaa46840006d13918e81fcb32a767a8ad94b`. User v75 first-play phone screenshots show Library, one found word, hint and notices—not save/restart/completion acceptance.

## Changes delivered
1. **New working asset crosswalk:** `design/visual-system/ws16br-runtime-asset-crosswalk.json` maps each of the 20 original WS16G painterly roles to present Kotlin runtime sources, retains original register statuses and approvals, plus existing Home decor, paper-tooth, book art and cabinet families. **This ledger is explicitly NOT production art approval.** No new sprites, image generation or asset-license assertions.
2. **Home-only richer parchment:** `GridMatesParchmentBackground.kt` introduces `GridMatesPaperFinish.QUIET` (default) and `STORYBOOK_HOME`. `GridMatesShell.kt` uses richer warmth, edge pigment and deterministic extra grain *only* when `page == Page.PLAY`. Reuses existing `ws16aa/parchment.png` plus `Ws16IHomeEdgeDecor`; no images stretched or overlaid on the puzzle board, no runtime network/download, no animation.
3. **Compact pulled-book preview:** `BookcaseBookshelfScreen.kt` replaces the oversized default AlertDialog with a native Compose Dialog containing existing `GridMatesIllustratedFrame(PANEL)` and green `GridMatesIllustratedPill` (View pages), with the real art, live chapter/page counts, optional QA TEST BOOK flag and Back to shelf. Resizes cover to **128×171dp**, keeps 48dp minimum button heights, stacks actions for Android fontScale >= 1.30, retains Back/outside dismissal and real selected book callback. The full wooden cabinet, shelves, arrow pager and test-book IDs are unchanged.
4. **Tests and workflow:** `test_ws16br_asset_cohesion.py` checks the 20-role crosswalk, that original approval fields remain untouched, source paths, Home-only opt-in and dialog callbacks/controls. Both [Android workflow](../../.github/workflows/ws01-android-qa.yml) and [art/release isolation workflow](../../.github/workflows/ws12g-qa-art-lab.yml) invoke it. Existing Android Compose library book-preview test already checks the live selector, cover and View pages route, but **CI compilation alone does not mean it executed on a phone/emulator**.
5. **QA version candidate:** `versionCode=76`, `versionName=0.9.32-paper-and-preview-cohesion-qa`; same QA app ID and publicly committed test-only certificate. Not a production release.

## Code and build receipt
- [Initial v76 UI commit](https://github.com/PlayerEden/GridMates-Word-Search/commit/b978f5d32d325a7cd83361d45ad8ad66b8c5860a)
- [Source crosswalk + tests + workflow commit](https://github.com/PlayerEden/GridMates-Word-Search/commit/c256befbd48a17fc47eba04b852f12c7b3c52b91)
- **Important:** GitHub builds triggered by the initial code commit `b978f5d` may predate the subsequent test-workflow changes. Only treat v76 as fully source-verified after a run of the final candidate code/source-test head, with Android and independent art workflows matching the same SHA. Record follow-up exact run/artifact hashes and signer after passing; no APK claimed as delivered yet.
- No save format, catalog/editorial content, Nearby/LAN protocol, profile unlocks, original twelve Woodland portraits, release artwork or Sudoku changes.

## Remaining gates and owner acceptance
- CI must compile Kotlin and UI tests, validate crosswalk, run full Android unit/lint, independently check release/test-art isolation and same-source APK contents.
- Check v76 Home on a phone against the v75 image: richer paper visible but still quiet behind text/cards; foliage/books unchanged. Check Woodland preview at normal and large font, View pages, Back to shelf and dismiss gesture. Check Library pages 1–3 identical to v75; confirm save/reopen separately.
- Determine whether texture quality genuinely needs a **separately generated/mastered raster tile**; don't produce one merely because the 2026-11-09 role register says “pending.” Source art and commercial rights would need proper review.
- Keep draft PR #131 open; don't merge branches or reset app data. Safe fallback is the signed v75 artifact [#14129808202](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43635464423/artifacts/14129808202). Avoid downgrades that might affect saves.

## Next exact action
Verify the final candidate head's two GitHub runs and Android APK; if green, provide the owner the direct APK and GitHub ZIP for a focused Home + book-preview A/B test. Then continue the canonical asset approval ledger and secondary screen cohesion pass.
