# November 23, 2026 — WS68 beta integration

The owner asked to continue building toward a polished Word Search beta. WS68 puts the accumulated runtime, art pipeline, editorial QA and native regression tests on one draft against `main`. [Integration comparison](https://github.com/PlayerEden/GridMates-Word-Search/compare/main...integration/ws68-v111-beta-2026-11-23)

## Starting point

Signed v111 passed its grouped-book and Home interaction checks on Android. The starting trees are `main` at `9043cf4c942da598b5841cc39c361c99abd9ef26` and WS67 at `11088874768f325dc35ff6c992fe1679f62a3f6f`. The [previous pass](2026-11-23_WS67_HOME_CONTROLS_AND_BOOK_GROUPING.md) records the changes. The signed [WS67 v111 APK ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45273861695/artifacts/14866208722) is the current handset fallback, SHA-256 `b2b966ee60d529d7f45fdcaf6db96f17326eafa62abb298401abcc26ab20bf0f`.

## Changes

- Reconciled six overlapping paths. The newer game and QA files drive the draft, with main's manual artifact maintenance and three-day routine APK retention for work targeting main. Earlier plans remain available through the original main history and the current roadmap.
- The integration CI starts with signed v108, seeds a Woodland bookmark and session, and installs signed v110 and the v111 candidate in sequence. The emulated v111 player then resumes the Solar save from Space. The existing native checks also cover dragging and hints in the two newer chapters.
- The Android run compares the six frozen preview boards and their index to the signed v110 build. Release QA checks that those seven entries and the preview class are excluded from the release package.
- The same emulator runs real TCP host/guest tests for Co-op, Shared Board Race, and Speed Duel. Physical Wi-Fi and discovery between two phones require their own playtest.

## Build receipt and next pass

The draft records the exact integration commit, workflow conclusions, APK checksum and artifacts once CI finishes. Start the next design pass with the visible back panel seam and cropped shelf brackets. Verify the shelf at compact and standard portrait widths, then resume the editorial work on Quiet Clearings Expert and Our Solar System Familiar. Handset review and public vocabulary approval remain open.
