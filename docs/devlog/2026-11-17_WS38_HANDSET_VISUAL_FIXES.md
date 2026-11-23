# November 17, 2026 — WS38 handset visual fixes

The owner supplied screenshots from v100 showing the Home covers, the full cabinet, Collection, Together setup, Solo, a 30 × 30 Garden game, and a Space book page. The [v100 receipt](2026-11-17_WS37_VISUAL_COHESION.md) records the previous Android checks. This pass starts at `286e0a1adff7181acd36ebf50ab004e8bf26ff84` in [draft PR #148](https://github.com/PlayerEden/GridMates-Word-Search/pull/148). The signed v100 APK is the handset comparison build.

## Changes

The Garden PNG has transparent exterior pixels. The Home pager uses a bounded rectangular press indication around the book's transparent canvas. Its press treatment now scales the book subtly using the existing pager transform.

The word list measures native target labels before choosing columns. Long Garden words receive the full width, and their labels can wrap completely. A native layout regression covers PHOTOSYNTHESIS and GERMINATION at a phone width and enlarged text.

Book pages inherit the shared warm paper instead of a flat theme tint. The old tint helper and its experiment-specific test are retired. Together setup uses the same illustrated navigation, selected mode controls, and Host and Join buttons as the puzzle screens.

## Verification

The implemented source is [`cea3c5a6e5614eb223dd2ada4421a8fb31560d2b`](https://github.com/PlayerEden/GridMates-Word-Search/commit/cea3c5a6e5614eb223dd2ada4421a8fb31560d2b) in [draft PR #149](https://github.com/PlayerEden/GridMates-Word-Search/pull/149), stacked on #148.

| Check | Result |
| --- | --- |
| Android unit tests, lint, instrumentation compilation, and stable QA signing | [Passed](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44181424897) |
| Independent debug/release and artwork checks | [Passed](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44181424939) |
| Signed APK | [Standard archive](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44181424897/artifacts/14372562300), 34,125,470 bytes |
| Package/version | `com.playereden.gridmates.wordsearch.qa`, code 101, `0.9.57-ws38-qa` |
| APK SHA-256 | `2824444742e1b93129a8eb2d711075bd697c34065303b8e469e03f087f87853e` |
| Signing certificate SHA-256 | `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419` |
| Independent APK comparison | [Independent archive](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44181424939/artifacts/14371693597). All 372 extracted entries match. |
| Artwork comparison | All 228 packaged asset entries match v100 byte for byte. |
| Android install-over, cover press, book paper, and Together setup | [Verified before the fixture stop](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44183668628). The signed APK retained the v100 Solo hint. Home opened Garden and Space book pages, and Together mode choices and the Join finder remained reachable. [Actual screenshots](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44183668628/artifacts/14373078486). |
| Long Garden labels and enlarged text | [Passed on Android 15](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44186172695). A generated 30 × 30 Garden game displayed PHOTOSYNTHESIS in full at 125% text. At 200%, a target remained visible and the zoom, precision picker, board-map action, and Together mode actions stayed reachable. |
| Focused screenshots and logs | [Actual Android evidence](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44186172695/artifacts/14375495904). The emulator used 360 × 800 dp. |

The local eight source/integration checks passed. Local rendering of the separate historical SVG art family requires CairoSVG, which the normal CI pipeline supplies. The long-target instrumentation regression compiled with this build. Source artwork, game IDs, saves, and LAN behavior use the v100 implementations.

The first emulator attempt stopped in the book-selection helper after install-over passed. Its lookup treated an empty XML node as false; the helper now selects visible book nodes explicitly. The second run captured the cover, book-page, and Together checks, then rejected an incomplete synthetic schema-5 test save. The focused rerun supplies its required paused journal. That run verified the long target and exposed zero-size offscreen Home nodes in the automation lookup at 200% text. The helper now scrolls to visible controls, and the final focused run passed. These test-runner corrections use the same signed APK.

## Follow-through

The four screenshot issues have Android captures and the signed APK is available for owner review. Review v101 on the handset. Two-device Together testing and portable book backup remain subsequent work.
