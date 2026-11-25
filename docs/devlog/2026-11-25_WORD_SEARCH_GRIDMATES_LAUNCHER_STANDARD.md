# November 25, 2026 — Word Search adopts the GridMates launcher standard

The owner pointed out the new GridMates master design in the EdenP hub. The launcher standard uses illustrated Sudoku Beta 3 as the reference. The assistant audited the installed beta resources, re-exported the selected Word Search owl master, and verified signed build 126. [GridMates launcher icon standard v1.0](https://github.com/EdenP-Development/Eden-Development-Standards/blob/9821e84330057c7744ff0f84ae3e85d380445c57/branding/gridmates/app-icons/v1.0/README.md)

## Starting point

[Draft #199](https://github.com/EdenP-Development/GridMates-Word-Search/pull/199) started this pass at `9ff464a50a636cca049659ab4182c88ee8c62fbf`. Signed build 125 had already passed the Library and Collection checks and the update from 124. The earlier owl export filled 256 × 256 px at (88, 88) and used the darker `#08351F` surround. [Build 125 record](2026-11-25_WORD_SEARCH_HANDSET_LIBRARY_AND_COLLECTION.md) · [Signed build 125 ZIP](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45572831566/artifacts/15002893342)

## Launcher export

| Setting | Build 126 |
| --- | --- |
| Surround | `#214F3F` from the existing Word Search launcher color. APK resource table reports opaque `#ff214f3f`. |
| Adaptive canvas | 108 × 108 dp, 432 × 432 px in the 4× density resource |
| Artwork footprint and offset | 58 × 58 dp, 232 × 232 px from (100, 100) through (331, 331). Local alpha measurement and a decode of the packaged raster both matched these bounds. |
| Original owl master | 1254 × 1254 PNG, SHA-256 `2137925563cc56762c187759a9eb62de1c2536b83acc9d0a09e93c97de2895c2` |
| Foreground export | Lanczos resize and transparent padding, stored as lossless WebP. SHA-256 `924b6a1e00c5e677431f11a7f9fcb102fee5f07ab5b8c814438c46e1a1970299`. [Pinned manifest](https://github.com/EdenP-Development/GridMates-Word-Search/blob/96d03402ee8249a9dfd62304d334ad370925caf9/design/visual-system/masters/ws-launcher-owl-v1/manifest.json) |
| Themed and startup resources | The beta packages its Word Search owl monochrome vector. The Android starting theme uses the separately configured native startup mark and paper color. |

The locally supplied illustrated Sudoku master matched the reference SHA-256 from the hub. Both artworks were previewed at 48 px using the runtime green and simulated circle, rounded-square and squircle masks. The owl, gold frame and FIND diagonal stayed recognizable. A themed preview rendered the Word Search monochrome vector at the same size. Android Settings showed the installed color icon after the signed upgrade.

## Build and verification

| Check | Result |
| --- | --- |
| Tested source | [`96d03402ee8249a9dfd62304d334ad370925caf9`](https://github.com/EdenP-Development/GridMates-Word-Search/commit/96d03402ee8249a9dfd62304d334ad370925caf9) |
| Signed APK | [Build 126 APK ZIP](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45577040557/artifacts/15004778367) · `GridMates-WordSearch-Beta1-GridMates-Icon-Playtest.apk` · 30,357,255 bytes |
| Package and version | `com.playereden.gridmates.wordsearch.beta` · Android code 126 · `0.9.75-beta.1-gridmates-icon-playtest` |
| APK SHA-256 | `99fcbbfd9101e730251dd3c1ce443dcbef1d6e62e07e8f5ebe3401782baf2479` |
| Signer SHA-256 | `c29c0c593ac99ae81b5a5aaf5fca37bbed3bbd419e8f08287296de6681babba3` |
| Build and art | Android unit tests, lint, package checks, exact raster and resource color, all 239 illustrated preview assets, and release isolation passed. [Android run](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45577040557) · [Art Lab check](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45577049986) |
| Device | Android 35 verified the signed 125 → 126 update, the full portable test save, the original QA journal hashes, the installed icon, illustrated Library and Collection, and the resumed Woodland puzzle. [Screenshots and test report](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45577040557/artifacts/15005144036) |

The emulator script can close the exact stalled Pixel Launcher dialog seen in previous archived QA runs and retry opening Settings once. Local checks exercised normal launch, that recovery, and propagation of a Word Search error. The current Android run passed on its first attempt.

## Handoff

Compare build 126 beside the illustrated Sudoku icon on the owner's handset. The physical launcher shape and themed appearance remain open for review. Signed build 125 is the preceding verified beta and QA v120 is the separate QA build. Use Android version code 127 or higher for the next update to this beta package. [Draft #199](https://github.com/EdenP-Development/GridMates-Word-Search/pull/199) · [Build 125 ZIP](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45572831566/artifacts/15002893342)
