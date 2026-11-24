# November 24, 2026 — WS74 Quick Play and Daily Puzzle

The owner asked to continue the player journey checks, then look at Sudoku and other games for useful Word Search additions. This pass covers Home → puzzle → resume → finish → Home, plus a source review of Sudoku and Grid Hunt. [Draft #195](https://github.com/PlayerEden/GridMates-Word-Search/pull/195)

## Starting point

Signed v116 in [draft #194](https://github.com/PlayerEden/GridMates-Word-Search/pull/194) added six frozen pages and passed the signed upgrade, replay and release-isolation checks. The v116 handoff was `0db6024e22cd86fa7a56f0e13a2ebeee365ed715`. [Previous pass](2026-11-24_WS73_CHAPTER_VARIETY.md) · [Verified v116 fallback](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45427261820/artifacts/14937207027)

## Player-facing change

The Daily route was saved across Activity recreation and its selected date was held in memory. On recreation, the store selected today's date. An older Daily could open today's board or show an empty game screen if today's entry did not exist. v117 saves the selected date in Android's Activity state and restores the corresponding existing entry before composing the gameplay screen. That keeps date selection and the Daily route together while reusing the saved puzzle and progress.

Today's card now offers **Continue** once a journal entry exists. That gives a player who used a clue or opened the puzzle before finding a word the correct way back in. Quick Play uses its existing size chooser, Home resume, result and same-size replay paths. The Android tests drive those routes and look up the actual saved boards for every trace.

## Cross-game assets and interaction review

Reviewed the exact current source of [Sudoku's integrated RC3 draft #126](https://github.com/PlayerEden/SudokuTogether/pull/126), commit [86c703e](https://github.com/PlayerEden/SudokuTogether/commit/86c703ec9a4c264ffa920be3f6a752abd3c2fdd1). That tree has 43 actual portrait images across Woodland (17), Garden (9), Ocean (11), and Farm (6). All 12 Woodland PNGs that Word Search already uses match the hashes in its pinned import manifest. The Sudoku art bible gives per-sprite comparisons and size targets for the next family. [Sudoku Collection art bible](https://github.com/PlayerEden/SudokuTogether/blob/86c703ec9a4c264ffa920be3f6a752abd3c2fdd1/docs/GRIDMATES_COLLECTION_ART_BIBLE.md)

| First Garden candidates | Source PNG dimensions | Source PNG SHA-256 |
|---|---:|---|
| `garden_honeybee.png` | 384×384 | `bed0dfa2f32faa7758de0b51612226eec8b53046305a10a13969e7ea4cadf8b0` |
| `garden_ladybug.png` | 384×384 | `8c28931c1f663495862c8990b271b418c89b9815cec94e711bd489daa990a067` |
| `garden_butterfly.png` | 384×384 | `05ac29ddfe5754a4ab21d74cbb6be415ceb6c9617b9ec9c5f26d2d7de3ecff65` |
| `garden_sunflower.png` | 512×512 | `0901959b820159a27f361556a9bb7cdd4cd1d5009370b85f95b630fb1f1769d7` |

These four transparent sprites have clear silhouettes and consistent ink lines. Their source PNGs total 521,107 bytes. They are the first picks for a small Garden album since the matching illustrated book already exists. Check them at avatar and card sizes in Word Search. The Firefly source has an uneven lower glow and similar wings and proportions to Honeybee. Review it separately on-device. Raccoon is a close match for a later Woodland addition. The Ocean set has enough existing art to support a later ocean-themed volume and album. [Pinned Sudoku sprite directory](https://github.com/PlayerEden/SudokuTogether/tree/86c703ec9a4c264ffa920be3f6a752abd3c2fdd1/app/src/main/res/drawable-nodpi)

Sudoku's **family filters, tap-to-preview and next-unlock card** make the collection browsable as it grows. Word Search can adopt a small Woodland/Garden selector, one goal with the exact earning condition, and a compact earned shelf in Achievements. Use Word Search's existing game records to calculate progress. The portrait catalog and its upgrade checks need to retain the 12 original IDs, unlocks, equipped choice and backup behavior. Score Garden items against actual Word Search milestones and verify local equips and peer fallback in Together. [Sudoku Collection code](https://github.com/PlayerEden/SudokuTogether/blob/86c703ec9a4c264ffa920be3f6a752abd3c2fdd1/app/src/main/java/com/playereden/sudokutogether/CosmeticUi.kt#L111) · [Sudoku achievement album](https://github.com/PlayerEden/SudokuTogether/blob/86c703ec9a4c264ffa920be3f6a752abd3c2fdd1/app/src/main/java/com/playereden/sudokutogether/JournalScreen.kt#L345) · [Word Search themed gallery direction](../design/WS16D_COLLECTION_HOME_SHORTCUT_AND_THEMED_GALLERY_ROADMAP_2026-11-09.md)

Grid Hunt's actual Explorer's Academy offers optional practice with feedback and a second independent attempt. For Word Search, a small **Try a word** exercise under How to Play could show a straight swipe, then let the player try a diagonal or backward word. Quick Play can stay immediately reachable. [Grid Hunt Academy screen](https://github.com/PlayerEden/GridMates-Grid-Hunt/blob/388482934e3c24374cfa4a6e0f1d3335f63065f8/app/src/main/java/com/playereden/gridmates/gridhunt/ExplorerAcademyScreen.kt)

## Build and verification

Signed v117 was built from [`be79a6b28cf2fcc84b456c52d651c3f8f476b660`](https://github.com/PlayerEden/GridMates-Word-Search/commit/be79a6b28cf2fcc84b456c52d651c3f8f476b660). The [Android build, unit tests, lint and emulator run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45432313101) and [release isolation](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45432312872) both passed.

| Emulator check | Observed result |
|---|---|
| Signed v116 reproduction | A November 5 Daily with one found word opened normally. Activity recreation then showed “No Daily puzzle selected.” |
| Signed v116 → v117 install-over | Solo and Daily journal bytes matched immediately after upgrade. |
| Earlier Daily on v117 | The same dated grid reopened with the found word after Activity recreation. The second word scored on that exact board and persisted to November 5. |
| Today's Daily on v117 | Used a clue with zero words found, returned to the journal, resumed with the clue, solved and reviewed on the same dated board. One completion was recorded. |
| Quick Play on v117 | Started a 12×12, traced a word, returned Home, resumed the same board, recreated the activity, finished, opened the size chooser, and launched a different 12×12 from Play another puzzle. The result and resume path both persisted. |
| APK contents | All 244 packaged entries under `assets/` matched v116 byte for byte. |

[Logs, screenshots and signing receipt](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45432313101/artifacts/14940036985)

| APK field | Verified value |
|---|---|
| Package | `com.playereden.gridmates.wordsearch.qa` |
| Version code | `117` |
| Version name | `0.9.72-ws74-solo-daily-qa` |
| Size in bytes | `34279383` |
| APK SHA-256 | `714e250ba0f0c10d64c37fab555bb4d8471a43e997d8deca8165e0de4cabe4d6` |
| QA certificate SHA-256 | `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419` |
| GitHub artifact | [Signed v117 APK ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45432313101/artifacts/14937444767) |

## Follow-through

Handset QA is open. [Signed v116](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45427261820/artifacts/14937207027) is the previous verified build. Next, build an install-over Garden Collection preview from the four pinned Sudoku sprites and review the family selector, earned requirements, exact portrait rendering and profile persistence.
