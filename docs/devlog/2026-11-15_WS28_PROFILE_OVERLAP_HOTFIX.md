# WS28 — Physical v89 screenshot: Profile progress overlaps player identity

**Date:** 2026-11-15 local / 2026-11-16 UTC  
**Base:** WS27 v89 verified QA code `be5271ea7bcbb6f8b710cb35502c491d385a2925` (draft PR #134).  
**Status:** source + regression tests committed in isolated draft branch, CI and physical acceptance to be checked separately.

## User evidence

Five actual Android phone screenshots of WS27 v89. Collection on pages 1 and 2 shows a more efficient portrait/collection header, the same two-column collectible cards, their untouched earned/locked states and live next/previous page navigation. Edit display name opens an accessible native confirmation dialog with local-only privacy explanation. The **Profile summary screenshot shows "Companions collected 2 / 12", its green progress bar, and "0 of 8 achievements" painted across the player's name and owl image near the top of the same bordered panel.**

## Confirmed root cause

`GridMatesIllustratedFrame` supplies content inside a `BoxScope` using `Box { Canvas(matchParentSize()); content() }`. WS26 added `Row { avatar, name, stats }` and `Column { companion progress, achievements }` as two **direct sibling children** to the frame's Box. A Box aligns those siblings at top-left, so the content overlaps. It is a **layout code defect**, not missing assets, broken profile stats or font scaling.

## Narrow source fix

- `Ws16CProfileHub.kt`: wrap the two existing children in a **single `Column(Modifier.fillMaxWidth(), verticalArrangement=Arrangement.spacedBy(2.dp))`** inside the frame. Now Row identity (existing 76dp avatar, name and real words/puzzles) is followed by Column progression (existing collected count, progress bar, achievements), each with its original padding and data. The frame should size to the combined content.
- Tag actual UI sections as `ws28-profile-identity-row`, `ws28-profile-progress-section`, `ws28-profile-layout` to verify geometry. Preserve `ws16c-profile-summary` and original progression/metrics tags.
- New `ProfileOverlapAndroidTest.kt`: render 360dp Profile, compare bounding rectangles and assert identity.bottom <= progression.top, progression.bottom <= frame.bottom, and original metrics/progression still exist. **Build/compile alone does not mean this instrumented test ran on a physical device**.
- New Python `test_ws28_profile_overlap.py` guards correct frame parenting and preserved local progress/routes in both pipelines.
- QA test version code 90, name `0.9.46-profile-stacking-qa`, same QA app ID and signing key. No migration.

## Scope deliberately unchanged

No Collection UI modifications, no unlock conditions, portrait/font/achievement definition changes, saved data changes, Home, wooden Library, puzzle board, progress calculation, networking, release art, or Sudoku repository change.

## Gates

1. Both workflows pass at exact WS28 source commit.
2. Standard and independent signed QA APKs match internal packaged file lists and signing certificate matches verified v89 QA.
3. Phone verification: Profile shows avatar/name at top, progress underneath inside same frame with no text overlap; confirm large text. Collections paging and name dialog remain good.
4. Production merge / Play release / real-device approval still pending after source CI.

Rollback / prior verified: [v89 Android QA run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43807160803), [v89 artwork/release run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43807160800).
