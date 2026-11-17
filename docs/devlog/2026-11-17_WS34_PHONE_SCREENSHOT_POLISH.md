# November 17, 2026 — WS34 phone screenshot polish

## Owner feedback and starting point

The owner shared seven handset screenshots of the QA app. The Solo screenshot showed the duplicate touch instruction clipped beside the zoom buttons and the word list extending below its visible rows. The scrolled Collection screenshot showed the title and Back above the viewport and “1 puzzles to unlock” on Rabbit.

The pass started at `de69ca3d6b8e523ac28a4ee812a3e39c7db10903` in [draft PR #144](https://github.com/PlayerEden/GridMates-Word-Search/pull/144). The [v96 receipt](2026-11-16_WS33B_LIBRARY_LARGE_TEXT.md) records the Woodland playthrough, 200% Library checks, and the [signed v96 QA fallback](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44026675539/artifacts/14304230230).

## Changes

- The Solo toolbar gives Select and the zoom buttons the full row. The gesture directions appear below the board. The continuous Solo and Book word lists show a proportional scrollbar when they have more rows.
- Collection keeps its title, Back, and tab strip in view while the cards scroll. Each tab opens at the top. The title can wrap at larger system text sizes.
- The portrait grid says “1 puzzle to unlock” when one completion remains. The gallery regression checks the wording and the fixed navigation.

## v97 build and verification

The signed build came from [`e89855264e85ef4a1486a1b3060a893a7301b45c`](https://github.com/PlayerEden/GridMates-Word-Search/commit/e89855264e85ef4a1486a1b3060a893a7301b45c) in [draft PR #145](https://github.com/PlayerEden/GridMates-Word-Search/pull/145).

| Check | Result |
| --- | --- |
| Android unit tests, lint, instrumentation compilation, and signing | [Passed](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44120147813) |
| Independent debug and release build, source artwork, and release isolation | [Passed](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44120147972) |
| APK content | All 363 entries match between the independent and standard builds. All 219 bundled assets match v96 byte for byte. |
| Signed APK | [Standard build ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44120147813/artifacts/14345181818) contains `app-debug.apk`, 30,727,877 bytes |
| Package and version | `com.playereden.gridmates.wordsearch.qa`, 97, `0.9.53-ws34-qa` |
| APK SHA-256 | `311cd5d5879912bc6062a744490a947d93ee17a235ef64075f8ea2d8bb5aa156` |
| QA signing certificate SHA-256 | `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419` |

The initial [Android](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44119342733) and [artwork](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44119342650) runs stopped at a source assertion that expected the Collection title in the prior shell. The correction verifies the same title in the Collection header and updates the Android test selectors for the puzzle toolbar. The corrected source passed the 24 targeted checks across seven Python suites.

The [Android 15 emulator run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44122930889) passed with the exact signed v97 APK installed over v96 at 440 × 720 dp. It confirmed the two-column Rabbit wording, stable Back and tab positions while the gallery scrolled, the Fonts tab opening at the top, the complete Solo gesture instructions with the control row visible, and more words scrolling into view while the board stayed fixed. [Android captures](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44122930889/artifacts/14345947593).

The early emulators at 360 and 400 dp used the one-column gallery after fullscreen gesture insets. A later 440 dp trial verified Collection, then stopped on a test assumption about the disabled state in the UIAutomator dump. The final check verified the toolbar labels and the independent list scroll. Handset verification of v97 is pending.

## Follow-through

The next task is portable backup and restore for completed book pages, bookmarks, and unfinished pages. Start from draft PR #145 and use the v96 receipt for the book playthrough and the QA catalog context.
