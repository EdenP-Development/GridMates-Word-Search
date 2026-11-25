# November 25, 2026 — Illustrated Word Search owl launcher

The owner supplied their illustrated Sudoku fox badge to clarify the shared GridMates style and selected the new owl artwork after seeing the preview. The assistant integrated it into signed Beta 1 playtest build 124. Android 35 verified the update and the installed icon. The handset review is open.

## Starting point

The playtest continues [draft #199](https://github.com/EdenP-Development/GridMates-Word-Search/pull/199) on `ci/word-search-beta-signing` from `e2818e579fd57a78e375437b6e7372d5500fb939`. Code 123 was the signed illustrated beta with a flat FIND-grid launcher. Its save migration and package identity were already verified. [Previous icon receipt](2026-11-24_WORD_SEARCH_LAUNCHER_ICON_PLAYTEST.md) · [Signed code 123](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45551647183/artifacts/14993368921) · [Separate QA v120](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45491802328/artifacts/14967073059)

## Artwork and Android integration

The owl and gold FIND diagonal bring a Word Search motif to the same golden wood rim, deep forest green, and cream puzzle tiles as the Sudoku badge. The original 1254 × 1254 transparent generation is archived with the input reference checksum, prompt, and exact export hashes. [Original artwork](https://github.com/EdenP-Development/GridMates-Word-Search/blob/6564a7ada2bfe9f44f4984b1339bda198e13e45e/design/visual-system/masters/ws-launcher-owl-v1/original.png) · [Artwork manifest](https://github.com/EdenP-Development/GridMates-Word-Search/blob/6564a7ada2bfe9f44f4984b1339bda198e13e45e/design/visual-system/masters/ws-launcher-owl-v1/manifest.json)

The `betaPlaytest` source set uses a 432 × 432 lossless WebP layer centered on a forest-green adaptive background. It scales the original badge to 256 × 256 with Lanczos filtering and extends the layer with transparent padding. A separate vector renders the owl and diagonal grid in the Android monochrome theme. Local 48-pixel round and rounded-square previews kept the owl and the highlighted diagonal visible. Android Settings captured the installed round icon.

## Signed APK and checks

| Item | Result |
| --- | --- |
| Tested source | [`6564a7ada2bfe9f44f4984b1339bda198e13e45e`](https://github.com/EdenP-Development/GridMates-Word-Search/commit/6564a7ada2bfe9f44f4984b1339bda198e13e45e) |
| Android package and version | `com.playereden.gridmates.wordsearch.beta` · 124 · `0.9.75-beta.1-owl-icon-playtest` |
| APK | `GridMates-WordSearch-Beta1-Owl-Icon-Playtest.apk` · 30,373,639 bytes · [Signed APK ZIP](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45566345002/artifacts/15000254577) |
| APK SHA-256 | `c43462cc89ed2060ce33358e30d92c0fd0662ffdc40d847a38faf8099f392859` |
| Signer SHA-256 | `c29c0c593ac99ae81b5a5aaf5fca37bbed3bbd419e8f08287296de6681babba3` |
| Build | Android unit tests, lint, compile, the existing signature and package checks, both adaptive layers, and exact raster bytes passed. All 239 v120 preview assets matched the signed build. [Signed Android run](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45566345002/attempts/2) |
| Installed update | Android 35 installed 124 over 123 and matched the complete portable save after relaunch. The illustrated Home and Library loaded, the saved Woodland page resumed at one word found, and the three v120 journals matched their original byte hashes. [Screenshots and verification](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45566345002/artifacts/15001296317) |
| Release packaging | The ordinary release APK excludes the playtest-only owl resources. [Art Lab run](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45566352392) |

The first emulator attempt stopped at a Pixel Launcher system dialog before installing the new APK. Retrying the failed device job passed on the same source and produced a byte-identical signed APK. [First attempt](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45566345002/attempts/1) · [Passing attempt](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45566345002/attempts/2)

## Handoff

Use signed build 124 for the handset comparison with Sudoku. Code 123 is the verified pre-update beta reference, and QA v120 is the separate playable reference. The handset review and public beta catalog decisions are open. Future updates to the beta package use a version code above 124. [Draft #199](https://github.com/EdenP-Development/GridMates-Word-Search/pull/199)
