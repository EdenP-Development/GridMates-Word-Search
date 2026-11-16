# WS29 — Illustrated achievement rewards, using WS21 sources

**Date:** November 16, 2026  
**Base:** WS28 v90 draft PR #135 `10721fb7120fe95e3e0ccd02bcb3f2e1af35028e` (user visually confirmed Profile nonoverlap on handset).  
**Status:** candidate source code in isolated stacked draft branch. CI and handset testing required.

## Physical phone feedback
Owner v90 screenshot confirms Player Profile's avatar/name/metrics and companions progress now stack properly without overlap; Collection 2x2 gallery, Home illustrated book, and walnut shelf are also rendering. New "My Puzzles" screen looks functional but stylistically simpler; defer it. Next previously agreed priority is collectible rewards and achievement presentation.

## WS29: compact functional visual upgrade
- Integrate the **actual existing SVG originals** from `design/art-library-v2/ws21/svg/core_templates/gm_core_ws21_achievement_slot_{gold,cream}.svg`, not flattened artwork reference sheets. Their exact source SHA-256s are validated against the existing `design/art-library-v2/ws21/manifest.json`. CairoSVG builds 192x192 debug-only PNGs, `gm_ws29_achievement_{gold,cream}.png`, reproducibly at CI time; no new binary commits or production assets.
- Update `AchievementCard` in `CollectionScreen.kt` to show a 66dp shield and **native live** motif beside title, description, earned status and progress. The motif is selected from existing achievement IDs (sun/daily, diamond/hints+hard, flower/themes, star/puzzles). Locked cards are faded and retain their exact progress/remaining calculation.
- Keep the current `GridMatesInfoPanel` and existing eight user achievements, counts, sorted order, pagination, original live Text labels and test tags. The earned status changes the medal appearance without modifying profile data.
- Eliminate a redundant explanatory sentence above cards to show more of the actual rewards on entry. All data stays local.
- Compile the original SVG shield and milestone regression in both GitHub workflows; extend Collection on-device UI source tests to assert First Page medal. **Instrumented test compiled is not proof of on-device execution.**
- QA versionCode **91** (`0.9.47-achievement-medals-qa`) uses the same existing QA test signer. v90 is the verified rollback reference.

## Preserved
No change to achievements' IDs, thresholds, descriptions, metric logic, save format, fonts/licenses, co-op, solo puzzle gameplay, Home, the walnut Library, Profile, the new portrait gallery, or release-approved resources. Release variant has native appearance fallback if debug candidates are unavailable. This is a visual study, not blanket approval of WS21 art.

## QA and phone gate
1. Complete source-SHA verification, actual PNG decoding, release isolation, all core/unit/lint and UI-test compilation at the exact final commit in two independent workflows.
2. Verify signed QA APK's package, v91 version, matching v90 certificate, and identical internal packaging across independent builds.
3. Phone: open Collection > Achievements; verify gold vs pale shields, First Page / Puzzle Regular / other eight milestones, titles, progress and pagination. Confirm lock/earned visuals remain distinguishable at 150%-200% font scale and small devices.
4. Keep draft; no main-branch or public-release merge until player screenshot acceptance.
