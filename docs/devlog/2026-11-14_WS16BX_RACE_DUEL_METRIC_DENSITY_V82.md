# WS16BX — Race/Duel summary metric density v82

**Date:** November 14, 2026. **Source:** [`46150195801981a49b8cb6d26aa6eb14af4e32f7`](https://github.com/PlayerEden/GridMates-Word-Search/commit/46150195801981a49b8cb6d26aa6eb14af4e32f7), still under draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131). Android and independent art/release QA are running; owner visual/functional approval is **OPEN**.

## Evidence and problem

After signed v81, owner supplied five real phone screenshots in the Game Design conversation: Profile, Records Solo, Race, Duel, and My Puzzles. Profile and Solo look coherent. Solo uses 2 + 1 cards, but Race and Duel show four tall full-width cards before the empty-state panel. The source-defined `MatchSummaryMetrics` breakpoint (`maxWidth < 350.dp`) measures **inside** the shell's horizontal padding: an ordinary ~320dp-wide phone supplies roughly 288dp of content and triggers a four-row stack. This is a layout-density issue, **not a records/counter bug**. My Puzzles' plain card is a separate future visual-cohesion question; intentionally excluded from this pass.

## Scope

- v81 → **v82**: `versionCode=82`, `versionName=0.9.38-match-metrics-density-qa`.
- Added a pure breakpoint function `stackMatchSummaryMetrics(availableWidthDp: Float, fontScale: Float)`, using **width <280dp OR fontScale >=1.8** to fall back to one vertical column; on typical 320dp and 360dp phones with shell margins, Race and Duel use **two 2-card rows**.
- Added semantic Compose tags for the Race/Duel rows and stacked layout. Kept existing cream/gold illustrated metric cards, values, order, spacing, text sizes, actual stats and labels.
- Preserved all Solo/Race/Duel local records, independent `rememberSaveable` paging, 4 history results per page, saved-outcome data, Daily journal and book/play navigation. The previous Solo 2+1 or accessible stack layout is unchanged.
- No new images/fonts/licenses, signatures, app identity, storage schema, reward rules, or gameplay changes.

## Regression suite

- Existing v81 `test_ws16bw_records_journal.py` updated for new breakpoint implementation without weakening its journal preservation checks.
- `test_ws16bx_metric_density.py` tests the new source layout and retained score/journal fields.
- `RecordsMetricBreakpointTest.kt` tests content-width edge cases (288dp and 328dp two-column, 260dp stacked, 1.8/2.0 font scales stacked) as a **pure Kotlin/JUnit app unit test**, runnable in standard Android CI `:app:testDebugUnitTest`.
- `RecordsJournalNavigationAndroidTest.kt` source checks two actual Race and Duel rows and absence of fallback at 360dp layout. Android CI compiles these instrumented tests; execution on a real handset/emulator is not implied.
- Both existing standard Android and independent artwork/release CI paths run the new guard test.

## Exact-source CI pending

- Standard Android QA: [#43669520506](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43669520506).
- Independent artwork/release QA: [#43669520500](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43669520500).
- Do **not** provide unverified v82 APK success claims until both runs complete and APK package, signer, unchanged 219 bundled assets and manifest version 82 are checked. Previous verified signed v81: [Android artifact #14142450673](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43665962329/artifacts/14142450673).
- Install new QA version over old **without uninstalling** if signature/package remain identical; preserve local progress. Do not merge draft PR or promote debug-only art as production.

## Owner v82 phone acceptance

Confirm Race and Duel now display **Played/Wins**, **Losses/Ties** or **Losses/Words found** as a comfortable 2×2 grid before the empty state. Confirm Solo still shows Completed/Words found + With hints. Test high accessibility text and narrow screens if possible. Once approved, shift priority to functional roundtrip: start Solo, find words, leave/relaunch, resume, finish, unlock Rabbit and inspect Profile/Collection/Records, including the actual hint count. Complete Nearby Race/Duel tests when two devices are available. No fake match records for production screens.
