# WS16BZ — Continuous word index for larger Solo boards

**Date:** 2026-11-15. **Compiled source candidate:** [`dad0f62b6ec0767c13f08d7f160c0f72d25d851e`](https://github.com/PlayerEden/GridMates-Word-Search/commit/dad0f62b6ec0767c13f08d7f160c0f72d25d851e). [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) DRAFT. **State at source commit:** exact-source Android and art/release workflows running. No claim of APK/art/release approval until both green and binary inspected.

## Why
The owner's November 15 screenshots showed a 15×15, 16-word Solo board with just **two words per page, Page 1 of 8**. The word-list `rowsPerPage` budget was measured below a large stable board viewport, leaving only one row. For an older/nontechnical audience, changing pages eight times to inspect a small vocabulary creates unnecessary friction. The same `WordList` composable already supports `scrollAll=true` in Book gameplay.

## Small source change: no puzzle mechanic changes
- Add pure UI selector `scrollSoloWordIndex(daily, targetCount)`: only **non-Daily Solo boards containing at least 12 targets** opt into continuous word-list scroll. Current 15×15/16-word Solo now has one vertically scrollable target index rather than an eight-page button pager.
- Both **portrait** and **landscape** `WordSearchScreen` paths explicitly pass `scrollAll = continuousSoloIndex` to the existing reusable `WordList`. Only the words region scrolls. **The board is not scrolled, replaced, shrunk, or attached to the index gesture handler**; all zoom/tap/drag logic stays on `ZoomableWordGrid`. No new art or controls.
- The original `WordList` retains the exact `puzzle.targets` order and original `Remaining` filter, solved checkmarks/strikethrough, two-column normal width, one-column on very narrow/enlarged text, and scroll offset reset when puzzle/filter changes. Add Compose tags `word-list-scrollable` and `word-list-target-{id}` for repeatable instrumented reachability tests.
- **Daily** (including 8×8, six targets) and short Solo boards stay on existing paging. Book mode continues using its existing `scrollAll=true`; giant 105-word index and page-jump logic stay unchanged in their default path.
- Version 82 → **83**, `versionName=0.9.39-continuous-solo-words-qa`. No schema migration, target generation, daily rollover, completed results, rewards, multiplayer or save data changes.

## Test gates
- `SoloWordIndexModeTest.kt` local JUnit exercises 6/11/12/16/44 words, Daily preservation.
- `ContinuousSoloWordIndexAndroidTest.kt` Android UI test source: 360dp Woodland 15×15 confirms all 16 words, no eight-page pager, scroll to the actual last target; verifies no claim/completion mutation; independent 360dp Daily-mode `WordList` remains non-scroll-all. **Compiled under Android QA; does NOT prove executed on device/emulator**.
- `test_ws16bz_solo_words.py` source guard included in both standard Android and independent artwork workflows. Guard checks UI selector/calls, independent nested scroll semantics, all-target retention and unchanged board/selection/save paths.
- [Standard Android run #43690359217](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43690359217); [independent art/release run #43690359215](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43690359215). Final status **PENDING at writing**. Do not hand over an unverified v83 APK.
- Last fully verified APK remains v82 [standard signed GitHub artifact #14143313475](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43669520506/artifacts/14143313475). Pending exact package, manifest 83, signer and unchanged packaged art verification.

## Owner visual and functional acceptance still open
Test ordinary 15×15 Solo: scroll inside the word region from SQUIRREL to later targets without clicking Next; board remains fixed and drags still claim the right words; Remaining filter works and solved words are visibly crossed; leave/reopen saved puzzle and eventually finish to verify Rewards/Records. Recheck Daily 8×8 continues to function and still paginates; 320dp, 200% text and landscape scroll reachability. **No production signoff or new screenshots** from this change yet. Also still outstanding from WS16BY: no-Wi-Fi host UX clarity, Nearby/Daily card consistency, two-phone LAN proof and complete gameplay-to-reward roundtrip.
