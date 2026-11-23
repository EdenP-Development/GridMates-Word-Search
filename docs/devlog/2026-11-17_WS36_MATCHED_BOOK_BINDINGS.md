# November 17, 2026 — WS36 matching covers and bindings

The owner confirmed the v98 Library on the handset and preferred the rounded bindings. Their screenshots showed the previous painted binding edges on the Home covers. This pass starts from [v98 draft PR #146](https://github.com/PlayerEden/GridMates-Word-Search/pull/146) at `e697fa5bb784b29bede72d0f5f26eef598dfad47`. The [v98 receipt](2026-11-17_WS35_LIBRARY_BINDING_DEPTH.md) and [signed v98 APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44136465891/artifacts/14352522134) are the comparison points.

## Artwork and routing

The three color and emblem pairs use the separate WS35 spine sprites and the [WS36 cover projection manifest](../../design/visual-system/ws36-matched-books.json). The exporter projects the visible binding around the angled edge of each existing WS16AE front. It checks the original alpha silhouette and the entire front face pixel for pixel. The original illustrated owl, ringed planet, flowers, and lettering appear at the same coordinates. Each derived cover is versioned separately.

Home, the cover browser, book previews, and the illustrated list resolve the current edition through `BookArt`. The playable Library bindings read the same theme paths as the illustrated list. The candidate asset files are debug-only. Book IDs, UI actions, and progress storage follow the existing routes.

## v99 build and verification

Built from [`faa55580f69e5cef36716db6b14c9712cbc79c24`](https://github.com/PlayerEden/GridMates-Word-Search/commit/faa55580f69e5cef36716db6b14c9712cbc79c24) in [draft PR #147](https://github.com/PlayerEden/GridMates-Word-Search/pull/147).

| Check | Result |
| --- | --- |
| Android unit tests, lint, instrumentation compilation, and signing | [Passed](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44142826177) |
| Independent debug and release builds with artwork isolation | [Passed](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44142826053) |
| Signed QA APK | [Standard archive](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44142826177/artifacts/14354454242) contains `app-debug.apk`, 34,109,082 bytes |
| Package and version | `com.playereden.gridmates.wordsearch.qa`, code 99, `0.9.55-ws36-qa` |
| APK SHA-256 | `e3a05ddb1a1092dbe70b428582feb68fe5ec31dccd5784db7de8fa6da23f374a` |
| QA certificate SHA-256 | `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419` |
| Independent package | [Independent archive](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44142826053/artifacts/14354399255), APK SHA-256 `11dff2797852264a5b157bae003758a12d121f5e9ab6d5a5567a9d738322b0e1`. All 372 APK entries match the standard build. |
| Art inventory | All 225 v98 asset files match byte for byte. Three new covers match their source outputs. All three cover and binding pairs were verified in debug, and the new covers were absent from release. |

Local generation from the pinned masters passed six existing cover and face-out tests and the new cover projection checks. Each projected cover keeps the original 480 × 640 silhouette and the exact original pixels to the right of its hinge. The visual comparison pairs the packaged covers and bare spine artwork at equal height. Android runtime and handset review of v99 are pending.

## Follow-through

Review all three matching book pairs on a handset. The next feature remains portable backup and restore for completed, bookmarked, and unfinished book pages.
