# November 24, 2026 — WS75 Garden album and puzzle progression

The owner asked how Google Play puzzle games handle difficulty, achievements and unlocks, and asked to keep developing Word Search. The Garden album is the first implementation of the approved book-linked Collection direction. [Draft #196](https://github.com/PlayerEden/GridMates-Word-Search/pull/196) · [Book Collection decision](2026-11-24_WS74_BOOK_ALBUM_DIRECTION.md)

The starting source is `274d844e3e116aed8202ddc8e02fb97b48954f4f`. Signed v117 verified resuming and completing Daily and Quick Play, including the earlier Daily restore fix. [Previous Android QA](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45432313101) · [Signed v117 fallback](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45432313101/artifacts/14937444767)

## Google Play examples

| Game | Progression or play pattern | Word Search application |
| --- | --- | --- |
| Razzle Puzzles Word Search | Advertises advanced difficulty options, saved unfinished games, and best and average time statistics. [Google Play](https://play.google.com/store/apps/details?id=com.razzlepuzzles.wordsearch) | Keep Continue prominent and let difficulty choices set expectations. Use times as a personal record. |
| PlaySimple Word Search Explorer | Solves move players toward a new destination. The word puzzles become more demanding as progress builds, with hints available. [Google Play](https://play.google.com/store/apps/details?id=in.playsimple.wordsearch) | Put a visible next goal in each book and show what the last solve earned. |
| Wordscapes | Unlockable scenic backgrounds give progress a visible cosmetic payoff. [Google Play](https://play.google.com/store/apps/details?id=com.peoplefun.wordcross) | Garden uses themed stamps that look different from one another in the album. |
| Sudoku.com | Daily challenges award trophies, and seasonal events award medals. Difficulty choices and personal statistics support different play styles. [Google Play](https://play.google.com/store/apps/details?id=com.easybrain.sudoku.android) | Activity collections can later reward cumulative Daily and Quick Play milestones using existing records. |

## Garden in v118

Open Garden's album from its Library details, a completed Garden puzzle, or **Collection → Albums**. Both Collection and Library use the same earned state, with progress and the next stamp shown in the album.

| Stamp | Goal | Placement when played in book order |
| --- | --- | --- |
| First Bloom | Finish any Garden page | Page 1, Relaxed 12×12 with 12 words |
| New Shoots | Finish the Getting Started chapter | Page 2, after the Classic 15×15 with 16 words |
| Garden Visitors | Finish its three-page chapter | Pages 10–12, across 12×12, 15×15 and 20×20 |
| In Full Bloom | Finish the 21 pages of the Garden edition | Book completion |

The 21 qualifying page IDs are fixed for this preview edition. Album progress derives from existing page awards matched to Garden's book ID, puzzle ID and exact board hash. Eligible saved solves count on upgrade. Repeated page awards count once, and later edition pages can be added without expanding already-defined goals. The album reuses Word Search's emblem, chapter, bookmark and completion artwork as stamps. The four Sudoku portrait options remain in the art review.

Garden reaches a 20×20, 24-word Classic puzzle on page 3, directly after a 15×15 page. The first two stamps give a reward before that step. Phone playtesting can establish when players first ask for a hint or leave a board unfinished, which will inform how the next book introduces larger grids and harder placements.

## Progression balance

Book stamps mark completion within a theme. Existing Word Search achievements cover goals across modes, including one Daily finish, seven dated finishes, a hint-free Solo solve and completing all three Solo themes. Use those records when adding an activity collection so the same achievement can show progress wherever it appears.

For the first phone playtest, watch how long the first two Garden pages take, whether a new player uses hints and whether they continue to page 3. Use that information when pacing the next book across grid size, word count and placement directions. Show players one current goal in the album and keep the other milestones visible in its list.

## Build and verification

The signed QA package is `com.playereden.gridmates.wordsearch.qa`, version 118 (`0.9.73-ws75-garden-album-qa`). Both workflows passed at code `102c1162843d809c6ce505abf391f087f2eedb0f`, tree `bba73c8830e9d0dfcc1391fbf9107ae5739d4563`. [Android and core checks](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45440988172) · [Artwork and release isolation](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45440988111)

| Verification | Result |
| --- | --- |
| Signed install-over from v117 | The Solo and book journals matched byte for byte. The existing Garden completion unlocked First Bloom and the Woodland completion stayed with its book. |
| Garden solve and reward | Sixteen Android drag gestures solved page 2 and earned New Shoots. The album showed 2 of 4 stamps and named Garden Visitors as the next goal. |
| Recreation and readability | Android activity recreation restored the album route and earned state. The 320dp layout at 150% text passed. |
| Rules and assets | Unit coverage checks page identity, duplicate awards, and added edition pages. All 244 assets from signed v117 matched byte for byte. The release build passed the isolation check for the QA album. |

The first workflow pair stopped at an older three-tab layout check. The updated checks recognize Albums and continue to verify the existing portraits and font unlocks. Nine targeted Python checks passed before rebuilding. [First Android run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45440399560) · [First release run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45440399625)

The signed APK is 34,312,151 bytes. Its SHA-256 is `e7d015f82a504331e92acaeb0f604f6ed5b120f1436f3face20a4b9a54b75695`. The QA certificate SHA-256 is `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419`, matching v117. The evidence archive SHA-256 is `ba94def469c7da118235c958e996b1bfe224156359680d2f442a7bf97742ee93`. [Signed v118 ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45440988172/artifacts/14945160537) · [Android screenshots and logs](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45440988172/artifacts/14945315761) · [Signed v117 fallback](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45432313101/artifacts/14937444767)

Handset review remains open. Play Garden pages 1–3 to check the first solve, use of hints, reward visibility and willingness to start the 20×20 page. Follow with an activity collection grounded in existing Daily and Quick Play achievement records.
