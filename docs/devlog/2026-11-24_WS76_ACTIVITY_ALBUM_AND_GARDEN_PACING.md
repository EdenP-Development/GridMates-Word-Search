# November 24, 2026 — WS76 Activity album and Garden pacing

The owner asked to continue developing a polished Android puzzle game with book collections, achievements and balanced progression. This pass adds an album for play outside the books and makes the next book board visible before players start it. [Draft #197](https://github.com/PlayerEden/GridMates-Word-Search/pull/197)

The starting head was `aabe55ce0d714c0dd6a0d88c39d191a6cd12bd3d`. Signed v118 had verified the book-linked Garden album, including old progress credit, touch input and the 320dp large-text layout. It is the signed fallback for this pass. [WS75 record](2026-11-24_WS75_GARDEN_ALBUM_AND_PUZZLE_PROGRESS.md) · [Signed v118](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45440988172/artifacts/14945160537)

## Activity album

Open **Collection → Albums → Activity album** or tap the album on a completed Daily or Quick Play result. Back from a result-linked album returns to that result. Garden still shows the same progress from Collection and its Library route.

| Stamp | Requirement | Existing record |
| --- | --- | --- |
| Today Counts | Finish one Daily puzzle | Today Counts achievement |
| Quick Start | Finish one Quick Play puzzle | Completed mixed Quick Play record in the Solo journal |
| All Sizes | Finish Small, Medium and Large Quick Play boards | Distinct 12×12, 15×15 and 20×20 mixed Quick Play board sizes |
| Seven Good Days | Finish seven dated puzzles at your own pace | Seven Good Days achievement |

Daily stamps use the same progress as their achievements. The Quick Play stamps recognize prior eligible finishes. Standard Solo puzzles, book pages and repeated Quick Play sizes add no new sizes. Album progress derives from the existing saves. The result highlights a newly earned stamp on a saved completion and opens the album. Going back to a completed result shows the current total.

Collection tabs wrap on narrow or large-text screens. The album uses the existing calendar illustration and achievement artwork. Activity album access is enabled where mixed Quick Play is available.

## Garden difficulty

Book results show the next board's grid size, challenge and word count beneath the existing action. The preview draws on the existing book page definitions. Garden’s third page has 400 cells and 24 words after a 225-cell, 16-word page. That is about 78% more cells and 50% more targets in one step. Page 4 returns to 12×12 Relaxed after the first larger challenge.

## Build and verification

Android and release isolation passed at code `f0a7152b43d8fb69ecd629e34b0b1a789a35c9cd`, tree `0aa4b91c0b5c2e77610cc6cf01d3c45e91de4999`. The first workflow pair stopped before compilation at a Collection header assertion in the cabinet shelf test. The follow-up updated that assertion and the medal guard for the new navigation and shared badge rendering. Fifty-six related local checks passed. The two asset-rendering checks that require CairoSVG ran in the GitHub workflows. [Android QA](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45446328318) · [Artwork and release isolation](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45446328171) · [Initial Android run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45446086818) · [Initial release run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45446086935)

| Android check | Result |
| --- | --- |
| v118 upgrade to v119 | The pre-upgrade app saved six nonconsecutive Daily finishes, Small and Large Quick Play, and Garden page 1. Both the Solo and book journals matched byte for byte after install-over. Garden credited First Bloom, and Activity credited Today Counts and Quick Start. |
| Daily and Quick Play | Android touch input completed the seventh dated puzzle and a Medium Quick Play board, earning Seven Good Days and All Sizes on the results. The app opened the album, survived activity recreation, and returned to the same result. |
| Garden | Touch input finished pages 2 and 3. The test saw the new preview, used a hint on page 3, left and resumed that board with one word saved, then finished the remaining words. The next preview showed a 12×12 Relaxed board. |
| Accessibility layout | Both albums and all tabs were visible at 320dp and 150% text. The last activity stamp remained reachable. |
| Assets | All 244 packaged assets matched the signed v118 files byte for byte. |

The Android run measured these layouts from the same Garden catalog used by the app. Backward counts use upward or leftward placements in the selected direction set.

| Garden page | Grid | Cells | Targets | Diagonal | Backward |
| --- | --- | --- | --- | --- | --- |
| 1 | 12×12 | 144 | 12 | 0 | 0 |
| 2 | 15×15 | 225 | 16 | 1 | 6 |
| 3 | 20×20 | 400 | 24 | 2 | 11 |

Reading the preview and using the controls passed in the emulator. Time to solve, hint use by new players and comfort with the 20×20 grid still need a handset playthrough.

The APK is package `com.playereden.gridmates.wordsearch.qa`, version 119 (`0.9.74-ws76-activity-album-qa`). Its size is 34,328,531 bytes, and its SHA-256 is `ae0a9a9834c48d1d94dcf4a188d7e5020c672393ba36bdcbd50026bb9e78a2b4`. The signing certificate SHA-256 matches v118 at `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419`. The evidence ZIP SHA-256 is `d9006df8965657931a29890f6545ff5e4d3e8140b0fd389ec75e2f24eb32cbf6`. [Signed v119 APK ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45446328318/artifacts/14946605616) · [Android screenshots and logs](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45446328318/artifacts/14947400389) · [Signed v118 fallback](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45440988172/artifacts/14945160537)

## Next pass

Prototype Woodland’s own album using verified Woodland page completions, chapter goals and the existing book artwork. Check its opening board ramp and choose milestones that suit its longer catalog. Garden’s handset pacing review remains open.
