# 2026-11-16 — WS32 illustrated My Puzzles

The current pass starts from [verified WS31/v93 PR #139](https://github.com/PlayerEden/GridMates-Word-Search/pull/139). [The baseline audit](2026-11-16_WS31_BASELINE_AUDIT_AND_WS32_PLAN.md) records the signed APK checks, branch history, and remaining Library work. v93 from #139 remains the fallback.

## Saved-puzzle screen

The first visual change brings the illustrated saved-puzzle journal from [PR #138](https://github.com/PlayerEden/GridMates-Word-Search/pull/138) onto the #139 code. The active puzzle gets a parchment frame, live word count and progress bar, and the shared green Continue or Review action. Paused puzzles get matching frames and distinct Resume and Remove actions. The empty state explains where an unfinished puzzle will appear.

The port uses the existing `GridMatesIllustratedFrame`, `GridMatesIllustratedPill`, and journal glyph. The patch keeps the v93 `SoloStore` and its existing routes and confirmation dialog. The navigation regression checks the active and paused cards, then resumes one of the saved games. Version code 94 distinguishes the preview from both prior v93 variants.

## Build and QA

The v94 runtime commit is `bbbee51bacc8ad6242fd4291cbc70e1d7689bc97` in [draft PR #142](https://github.com/PlayerEden/GridMates-Word-Search/pull/142). The [Android QA build](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43987637538) and [artwork and release isolation build](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43987637260) succeeded. The [exact v94 QA ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43987637538/artifacts/14286536417) contains the `com.playereden.gridmates.wordsearch.qa` APK with version code 94, name `0.9.50-ws32-qa`, and SHA-256 `0b8953e63a5c0cf38b127d76081c104e5c495ae443a0a827d6d0b3095870e539`. Its signer SHA-256 is `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419`.

The [#139 upgrade](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43989770069) and the [alternate #138 upgrade job](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43989138455/job/133724925241) passed against the exact v94 APK. Both Android 15 emulators created an active and paused Solo puzzle and a Daily entry on v93, verified the original install timestamp and byte-for-byte save on v94, and reopened the illustrated journal. At 320dp and 200% text, Keep puzzle preserved the save and Resume swapped the correct Solo puzzles while retaining the Daily entry.

[Before-and-after screens from #139](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43989770069/artifacts/14288015670) · [Screens from #138](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43989138455/artifacts/14286324169)

The [QA draft #143](https://github.com/PlayerEden/GridMates-Word-Search/pull/143) records the test harness. Its first attempts exposed two Home navigation layouts and a Pixel Launcher dialog. The passing checks handle the menu and direct links, and the #139 rerun handles the Android launcher dialog. Physical handset, Nearby play, and book backups remain open.

## Artwork to use next

| Area | Direction | Existing source |
| --- | --- | --- |
| Bookcase arrivals | Pair cover and spine art with the first reviewed playable catalog. Keep the walnut shelving and use readable titles and progress labels. | [Illustrated UI crosswalk](../../design/visual-system/ws16br-runtime-asset-crosswalk.json) |
| Collections and book milestones | Use a few Woodland, Ocean, Garden and Space motifs as theme cues. Pair crest frames with a clear, earned milestone. | [16 illustrated motifs and 24 editable frames](../../design/art-library-v2/ws22/README.md) |
| Chapter and end-of-book states | Add a matched chapter marker or bookmark only where the selected book lacks one. Use the parchment, brass and forest-green style already in the app. | [GridMates visual identity](../GRIDMATES_SHARED_VISUAL_IDENTITY_AND_MOTION_2026-11-09.md) |

The release catalog and permission review for production artwork are still open. The first public Library needs playable books, tested save and resume, and a backup that includes book progress. Continue WS32 with the book-page save/reopen and completion tests, then the portable backup. Keep Word Search progress local to Word Search.
