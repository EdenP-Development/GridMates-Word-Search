# WS16BT — Collection, Profile and Records visual-cohesion pass

**Date:** November 14, 2026. **Status:** v78 code and source-compiled UI guard committed; exact-source CI/real-device acceptance should be checked separately. **Branch:** `feature/ws14b-style-anchor-and-art-direction-2026-11-08`, stacked [draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131). **Source/QA commit:** [`fec3f21025f686593289d7a125a841427c25128d`](https://github.com/PlayerEden/GridMates-Word-Search/commit/fec3f21025f686593289d7a125a841427c25128d).

## Why this pass

User's v77 phone images support a new original low-key Home parchment and shared cream/gold framed book-detail surfaces; remaining user-facing inconsistency lay in Collection, Profile and Records. Objective is to reuse the native `GridMatesIllustratedFrame` family rather than generate more image packs, avoid changing owner-liked walnut Library and active book/game content, and preserve clear equipped/locked/reward semantics.

## Findings before editing

- `Ws16CProfileHub.kt` **already** uses FEATURE/MEDIUM native frames for profile summary and navigation to Collection, Records and My Puzzles. Leave it alone; don't multiply ornamental layers.
- `CollectionScreen.kt` has an illustrated heading, but player identity, rewards progress and achievement cards were plain `PaperPanel`. Font and portrait cards use **functional selected/equipped borders**; don't replace those without a suitable state-driven alternative. Existing tabs, paged 12-portrait gallery, 8 achievements, font-license notices, local display-name/portrait/font choice are real functional surfaces.
- `SoloRecordsScreen.kt` showed plain Material cards for Daily summary, Solo/Race/Duel empty states and saved result rows, plus plain metric cards. Solo summary showed three narrow cards in one row regardless of 320dp phones and system font scale. Tabs and pager already isolate three record types and preserve all entries.

## Implemented code

1. **`GridMatesInfoPanel.kt`**: single native cream/gold PANEL wrapper with a real Compose `ColumnScope` and dynamic content. It paints ornaments but never flattens/replaces labels, progress, events or controls.
2. **Collection**: use `GridMatesInfoPanel` for exactly three existing panel roles: Profile (with editable local name), Collection progress, and each achievement. Add stable `ws16bt-* ` tags; **retain** portrait/font `Card` selection borders, locked/earned/equipped behavior, all original 12 portraits and font license access. No rewards model or persistence changes.
3. **Records**: apply existing `GridMatesIllustratedFrame(PANEL)` around all seven daily/empty/results record surfaces and the `MetricCard` building block. Use `BoxWithConstraints`: standard-width Solo stats retain three metrics in one row; phones narrower than 350dp or fontScale >=1.3 arrange two + one; fontScale >=1.8 stacks three. Real values, order, timestamps, paging and Race/Duel semantics unchanged.
4. **Profile**: no source edit. Add source guard preserving existing `ws16c-profile-*` destination semantics.
5. **Tests and CI**: new `test_ws16bt_secondary_cohesion.py` checks card reuse without losing edit/equip/record paging, and presence of narrow/large-font behavior; both standard Android and independent artwork/release CI run it. Updated Android instrumentation test **source** for Collections and Records to assert the new panels and meaningful text. **Compiling these test sources in GitHub is not executing UI tests on a phone/emulator.**
6. **Build:** versionCode **78**, versionName **0.9.34-collection-records-cohesion-qa**. No new art, font, theme, game/puzzle files or save migrations. Same Android QA package identity and signature configuration.

## Verification links

- [Android QA run #43643636791](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43643636791)
- [Independent debug/release art QA run #43643636797](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43643636797)
- Both must complete successfully against exact source `fec3f21025f686593289d7a125a841427c25128d`; do not infer success from triggered build or source checks alone.
- Known verified v77 Android rollback artifact: [#14132233044](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43641290560/artifacts/14132233044). Never uninstall to update and never downgrade/clear saved state without backup.

## Owner phone validation (P1)

1. **Profile:** open avatar, tap Collection, Back to Profile, Records, Back to Profile and My Puzzles. Confirm no missing/duplicate headers on 320dp and normal screens.
2. **Collection — Portraits:** profile card and collection progress framed; first and later portrait pages still reachable; equipped portrait highlighted, locked portraits readable; Edit display name opens and can be cancelled.
3. **Collection — Fonts and Achievements:** ensure selected/locked state and License still work; achievement page 2+ accessible, readable framed panels without long scroll traps.
4. **Records:** Solo metrics remain readable at phone width; switching Solo→Race→Duel still shows separate summaries and empty states/results; Previous/Next handles more than four entries and disabled states correctly; results remain accurate.
5. **Regression:** Home/Library wood/card art unchanged; book Play/Bookmark, puzzle MUSHROOM/hint persistence after relaunch, no artwork leaks into release. Portrait/font/record persistence, 200% font and TalkBack need execution not screenshots alone.

## Status and next exact move

No source change to production approval state. Keep [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) **draft** and the approved-feeling Library geometry frozen. On green exact-source CI, retrieve and validate signed v78 APK and independent art build, compare v77 art entries and signing cert, then send install-over v78 for real phone screenshots. Only consider visual changes actually supported by those screenshots. Do not initiate new image generation just to decorate functional screens.
