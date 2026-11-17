# November 17, 2026 — WS34 phone screenshot polish

The owner shared handset screenshots after receiving the [signed v96 QA build](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44026675539/artifacts/14304230230). The [v96 receipt](2026-11-16_WS33B_LIBRARY_LARGE_TEXT.md) records the Woodland journey, the 200% text checks for the Library, and the last verified APK. WS34 starts from `de69ca3d6b8e523ac28a4ee812a3e39c7db10903` in [draft PR #144](https://github.com/PlayerEden/GridMates-Word-Search/pull/144).

## Changes

- The Solo toolbar now gives the full row to Select and the zoom buttons. The gesture instructions appear below the board, where they have room. The continuous word list shows a slim scrollbar whenever there are additional rows to scroll through.
- Collection keeps Back and the tab strip in view while the cards scroll. A tab change opens its content at the top. The title can wrap at large system text sizes.
- The portrait grid now uses “1 puzzle to unlock” for a single remaining completion. The Android gallery regression covers this copy and checks that Back and the tabs stay visible after scrolling.

## Build and follow-through

The v97 QA build and on-device checks are pending. Use the verified v96 APK for comparison. The next milestone is portable backup and restore for book completions, bookmarks, and unfinished pages.
