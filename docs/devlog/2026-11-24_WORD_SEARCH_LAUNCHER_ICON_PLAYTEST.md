# November 24, 2026 — Word Search launcher icon for the GridMates family

> **Status:** Source updated. Signed build and Android launcher review pending.

## Owner request and starting point

The owner asked for a stronger Word Search launcher icon, then asked to match the current Sudoku Beta 3 icon. The signed illustrated Word Search code 122 is the Android-verified fallback. This pass starts from draft #199 at `c344e4a65190cdda443c8926e9172d95ae46a655`. [Illustrated playtest record](2026-11-24_WORD_SEARCH_ILLUSTRATED_BETA_PLAYTEST.md) · [Prior APK ZIP](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45545457282/artifacts/14990404723)

## Design

The first illustration used a parchment letter grid and a gold diagonal through FIND. The family version uses the Sudoku Beta 3 launcher’s exact rounded frame paths and the same forest green, cream and gold colors. Native vector letter paths and a 4×4 grid give the Word Search design its own silhouette. A separate monochrome vector supports Android themed icons. The Sudoku reference comes from the Beta 3 source verified by the completed Visual 3 workflow. [Pinned Sudoku foreground](https://github.com/EdenP-Development/SudokuTogether/blob/fb9e4afd91d6aa58fac642d932e9f5620c7c60af/app/src/main/res/drawable/ic_launcher_foreground.xml) · [Sudoku Beta 3 run](https://github.com/EdenP-Development/SudokuTogether/actions/runs/45547418369)

The icon is an adaptive Android launcher resource in the `betaPlaytest` source set for visual review. The shared GridMates startup pilot remains in Sudoku. Code 123 uses the existing Word Search beta package and signing certificate.

## Verification

The signed workflow checks both icon layers in the APK, upgrades the previous illustrated APK with a complete QA save, and captures the installed icon in Android Settings. The release workflow checks that the candidate layers stay out of the release APK. Handset visual acceptance is open. [Playtest draft #199](https://github.com/EdenP-Development/GridMates-Word-Search/pull/199)
