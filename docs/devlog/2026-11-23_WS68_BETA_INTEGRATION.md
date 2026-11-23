# November 23, 2026 — WS68 beta integration

The owner asked to continue building toward a polished Word Search beta. WS68 puts the accumulated runtime, art pipeline, editorial QA and native regression tests on one draft against `main`. [Integration draft #189](https://github.com/PlayerEden/GridMates-Word-Search/pull/189)

## Starting point

Signed v111 passed its grouped-book and Home interaction checks on Android. The starting trees are `main` at `9043cf4c942da598b5841cc39c361c99abd9ef26` and WS67 at `11088874768f325dc35ff6c992fe1679f62a3f6f`. The [previous pass](2026-11-23_WS67_HOME_CONTROLS_AND_BOOK_GROUPING.md) records the changes. The signed [WS67 v111 APK ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45273861695/artifacts/14866208722) is the current handset fallback, SHA-256 `b2b966ee60d529d7f45fdcaf6db96f17326eafa62abb298401abcc26ab20bf0f`.

## Changes

- Reconciled six overlapping paths. The newer game and QA files drive the draft, with main's manual artifact maintenance and three-day routine APK retention for work targeting main. Earlier plans remain available through the original main history and the current roadmap.
- The integration CI starts with signed v108, seeds a Woodland bookmark and session, and installs signed v110 and the v111 candidate in sequence. The emulated v111 player then resumes the Solar save from Space. The existing native checks also cover dragging and hints in the two newer chapters.
- The Android run compares the six frozen preview boards and their index to the signed v110 build. Release QA checks that those seven entries and the preview class are excluded from the release package.
- The same emulator runs real TCP host/guest tests for Co-op, Shared Board Race, and Speed Duel. Physical Wi-Fi and discovery between two phones require their own playtest.

## Build receipt and next pass

All 14 GitHub workflows passed on `dcacc8dcc184cc8383c8c32b4a3ac5f1c0db1ad0`, including the 96-board replay. The Android evidence contains 10 passing native tests, four unchanged journal files, hashes for the six frozen boards and index, and three screenshots. The old Woodland save decoded and the Solar System puzzle reopened from Space. Release isolation confirmed the preview code and boards were absent from release. [Android QA](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45286418490) · [Release isolation](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45286419274) · [Upgrade and Nearby evidence](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45286418490/artifacts/14871404188) · [Home, library and saved puzzle screenshots](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45286418490/artifacts/14871159640)

The signed candidate is `com.playereden.gridmates.wordsearch.qa`, version code 111, with 34,254,643 bytes and SHA-256 `38f6c260f334d1fa4c5c844a1302b67f44ca3f2f71d2451b9139c04530ae7b2b`. All 381 packaged files match the previously verified WS67 v111 APK byte for byte. [Integration APK ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45286418490/artifacts/14872471749)

Start the next design pass with the seam where the inner cabinet shadow meets the crown and the lower brackets that touch the base artwork. Verify the shelf at compact and standard portrait widths, then resume editorial work on Quiet Clearings Expert and Our Solar System Familiar. Handset review, physical two-phone discovery and play, and public vocabulary approval remain open.
