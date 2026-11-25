# November 24, 2026 — Word Search launcher icon for the GridMates family

> **Status:** Signed Android build and update checks passed. Handset icon review is open.

## Owner request and starting point

The owner asked for a stronger Word Search launcher icon, then asked to match the current Sudoku Beta 3 icon. The signed illustrated Word Search code 122 is the Android-verified fallback. This pass starts from draft #199 at `c344e4a65190cdda443c8926e9172d95ae46a655`. [Illustrated playtest record](2026-11-24_WORD_SEARCH_ILLUSTRATED_BETA_PLAYTEST.md) · [Prior APK ZIP](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45545457282/artifacts/14990404723)

## Design

The first illustration used a parchment letter grid and a gold diagonal through FIND. The family version uses the Sudoku Beta 3 launcher’s exact rounded frame paths and the same forest green, cream and gold colors. Native vector letter paths and a 4×4 grid give the Word Search design its own silhouette. A separate monochrome vector supports Android themed icons. The Sudoku reference comes from the Beta 3 source verified by the completed Visual 3 workflow. [Pinned Sudoku foreground](https://github.com/EdenP-Development/SudokuTogether/blob/fb9e4afd91d6aa58fac642d932e9f5620c7c60af/app/src/main/res/drawable/ic_launcher_foreground.xml) · [Sudoku Beta 3 run](https://github.com/EdenP-Development/SudokuTogether/actions/runs/45547418369)

The icon is an adaptive Android launcher resource in the `betaPlaytest` source set for visual review. The shared GridMates startup pilot remains in Sudoku. Code 123 uses the existing Word Search beta package and signing certificate.

## Verification

Source `d4d8cef88ce7a67a0489f21258a14d1c620bfa4a` passed standard Android QA and the signed emulator upgrade. Release isolation passed with both candidate vector resources outside the ordinary release APK. [Android run](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45551647183) · [Release isolation](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45551653360)

| Check | Verified result |
| --- | --- |
| Package and version | `com.playereden.gridmates.wordsearch.beta`, Android code 123, `0.9.75-beta.1-icon-playtest` |
| Signer SHA-256 | `c29c0c593ac99ae81b5a5aaf5fca37bbed3bbd419e8f08287296de6681babba3` |
| APK | 30,271,238 bytes · SHA-256 `09ecf1f0ce860083d4d8e5eb1e84ae13a86c954277c5bdea00c7c74faef54f64` |
| Launcher | Color and monochrome layers present. The Android 35 App info capture shows FIND in the round icon. The two foreground resources were also compared at launcher size using the same round crop. |
| Install-over and game | Android updated the exact signed code 122 with a full v120 backup. Profile, font, book bookmarks and the unfinished Woodland page matched after restart. The original QA journals retained their checksums. All 239 v120 preview assets matched. |

[Signed APK ZIP](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45551647183/artifacts/14993368921) · [Android verification](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45551647183) · [Installed icon screenshot and tests](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45551647183/artifacts/14994691847)

Code 122 is the previously verified illustrated build. The signed QA v120 app is available alongside the beta. Check the launcher on a handset before approving final art. A later beta APK must use a version code above 123. [Draft #199](https://github.com/EdenP-Development/GridMates-Word-Search/pull/199)
