# November 25, 2026 — Word Search handset review and Library polish

The owner's build 124 screenshots show the owl launcher in a handset folder, the illustrated Home and Library, the Woodland puzzle, all four Collection tabs, and the Woodland album. They also show the Library's artwork comparison menu, the lone Albums tab on a second row, and the label “0 of 1 pages.” The assistant used those three findings for build 125. Android emulator checks passed. Build 125 is ready for a handset comparison.

## Starting point

[Draft #199](https://github.com/EdenP-Development/GridMates-Word-Search/pull/199) started this pass at `6475148f693f9138fdd38ded123cbe2a3b95d6e1`. The owner had selected the owl artwork and tested the illustrated app on their phone. The signed build 124 had already passed the build 123 upgrade and save check in the emulator. [Owl launcher pass record](2026-11-25_WORD_SEARCH_OWL_LAUNCHER_PLAYTEST.md) · [Signed build 124 ZIP](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45566345002/artifacts/15000254577)

## Changes

- The non-debuggable beta presents the illustrated spine-first Library with its player controls. The debuggable QA Library has the artwork and bookcase comparison controls.
- Collection measures the tab labels at the current text scale. All four fit on one row where space permits. When they need to wrap, they occupy two balanced rows at the tested phone width. The labels keep their native text size and 48dp minimum touch height.
- Unfinished one-page album goals say “0 of 1 page.” Other page counts retain their plural form. The signed upgrade uses the existing beta identity and save format.

## Build and verification

| Item | Verified result |
| --- | --- |
| Source | [`3354764ea46ca61e4c43bc40f5fd814366812857`](https://github.com/EdenP-Development/GridMates-Word-Search/commit/3354764ea46ca61e4c43bc40f5fd814366812857) |
| APK | [Signed build 125 ZIP](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45572831566/artifacts/15002893342) · `GridMates-WordSearch-Beta1-Library-Polish-Playtest.apk` · 30,373,639 bytes |
| Package and version | `com.playereden.gridmates.wordsearch.beta` · `125` · `0.9.75-beta.1-library-polish-playtest` |
| APK SHA-256 | `2728ea8952a17e9157edf19d62142ddf8c43a551214219b5ddf601f34f686901` |
| Signing certificate SHA-256 | `c29c0c593ac99ae81b5a5aaf5fca37bbed3bbd419e8f08287296de6681babba3` |
| Runtime | Android 35 installed signed 125 over signed 124, matched the full test save after relaunch, reopened Woodland with one word found, and retained the three original QA journal hashes. |
| Library and Collection | The archived QA Library exposed its comparison menu. The beta opened the illustrated shelves with Woodland, Space, and Garden. The emulator captured the balanced Collection tabs and verified “0 of 1 page” in the Garden album. [Device screenshots and report](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45572831566/artifacts/15003871338) |
| Assets and release | The exact owl raster and all 239 illustrated preview assets matched the verified build inputs. Unit tests, lint and the release artwork isolation check passed. [Android build](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45572831566/attempts/2) · [Art Lab check](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45572840676) |

The initial source run stopped at the existing Collection check, which matched the FlowRow call formatting. Restoring that call formatting passed all 15 targeted Collection source checks and both workflows on the corrected source. [Initial source run](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45572525824) · [Corrected source](https://github.com/EdenP-Development/GridMates-Word-Search/commit/3354764ea46ca61e4c43bc40f5fd814366812857)

On the corrected source, the first device attempt stopped at a Pixel Launcher dialog while opening QA v120. The second device attempt passed and produced a byte-identical signed APK. [First device attempt](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45572831566/attempts/1) · [Passing device attempt](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45572831566/attempts/2)

## Handoff

Use build 125 for the next handset comparison of the Library and Collection. Build 124 is the verified previous beta and signed QA v120 is the separate QA build. Handset review of build 125 and public beta catalog approval are open. Use Android version code 126 or higher for a new update to this beta package. [Draft #199](https://github.com/EdenP-Development/GridMates-Word-Search/pull/199)
