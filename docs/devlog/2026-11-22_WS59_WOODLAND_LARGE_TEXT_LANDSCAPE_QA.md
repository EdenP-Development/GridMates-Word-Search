# WS59 — Woodland large-text, actual landscape and UI screenshots

> **Status: SOURCE AUTHORED; ANDROID CI/PHONE REVIEW PENDING.** The tests and emulator runner have been written on a new unmerged draft branch. A successful Android test compilation alone does not establish UX acceptance.

## Who, what, where, why

- **Owner:** requested continued refinement of GridMates: Word Search; no new phone screenshots or physical-gesture acceptance supplied yet.
- **Assistant implementation:** independent instrumented QA coverage, no production runtime or frozen-puzzle changes.
- **Goal:** investigate player accessibility/reachability on the **actual WS57 preview book**, especially 200% text and a physically rotated landscape display. These cases were not verified by WS58's normal-size 320dp portrait tests.
- **Branch:** `feature/ws59-woodland-large-text-landscape-qa-2026-11-22` stacked on [WS58 draft PR #171](https://github.com/PlayerEden/GridMates-Word-Search/pull/171), not main/Work. New draft PR required.

## Previously verified base and rollback

- **Start SHA:** `29a3d449b20f0e5ca1c7c7c1403d640a1331960b` (WS58 docs-only final HEAD after exact executed `c41eaf73d0da9befc42a3e58e86c6e09ef6edce0`).
- **WS58 Android/core:** [#45028233366](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45028233366) SUCCESS, **OK (3 tests)** actual emulator chapter picker and 12/20-cell finger gestures.
- **WS58 independent art/release:** [#45028233360](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45028233360) SUCCESS.
- **Safe independently hashed player-facing fallback:** previously executed **WS57 v107** [signed QA APK ZIP #14734405724](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44971601632/artifacts/14734405724), `app-debug.apk` SHA-256 `304b389d53935ceb8a8857666caf00fbc15e3c51a440ab3cced07f1f684e82f9`, package `com.playereden.gridmates.wordsearch.qa`. Do not uninstall or clear any owner's data.

## New WS59 source and intended executable checks

- `app/src/androidTest/java/com/playereden/gridmates/wordsearch/Ws59WoodlandTextAndLandscapeAndroidTest.kt` adds **3 true Compose instrumentation tests** against the WS57 QA `BookQaPilot.catalog(context)` and exact frozen V5 boards:
  1. At **320×640dp and 200% Compose font scale**, open six-chapter picker, scroll to last *Forest Discoveries*, select **Play** and **Bookmark** for that chapter's first page via accessible native controls.
  2. At **320×740dp and 200% text**, open the actual 20×20 Expert page, check Back/Puzzle tools/precision-selector access, ensure usable real grid geometry, tap Hint, finger-trace a real target, and independently reopen the saved target and hint.
  3. At **700×340dp and 150% text on a *physically rotated Android emulator***, exercise the actual 15×15 Classic page with the same native accessible actions, visible grid, Hint, and real target trace/save.
- Captures three emulator **PNG screenshots** alongside exact instrumentation and AndroidRuntime logs to support later visual human review. PNG output does **not** establish physical handset approval.
- `scripts/ws59_woodland_accessibility_emulator.sh` executes **2 portrait tests followed by 1 genuinely rotated landscape test** on one disposable API35 emulator, verifies `OK (2 tests)` and `OK (1 test)`, and exports screenshot evidence.
- `.github/workflows/ws01-android-qa.yml` adds isolated WS59 branch-scoped test preparation/Android emulator step and evidence upload. The pre-existing core/app unit/lint/build pipeline and independent art/release check continue.
- **No modifications to production game code, graphics, release assets, V5 generator/word placements, the 63 historical QA Library pages, 18 WS57 frozen page IDs/digests, reward/bonus logic, real AtomicFile save paths, backup/migration format, Quick Play, Daily, Nearby or Work/main.**
- Unique disposable WS59 session/progress filenames are deleted after tests; no owner data accessed.

## Build and QA receipt — awaiting actual runs

- **Test source commit:** to be set from the PR's exact executed head after CI; this text alone is not proof.
- **Both workflow conclusions and emulator report:** PENDING. No success claimed.
- **New APK:** build may produce a QA artifact; no install required because runtime source is unchanged. Do not call its signing/hash verified without inspecting actual receipt.
- **Fallback:** tested WS57 v107 above.
- **Open gates:** 200% font and full landscape native UI geometry, TalkBack speech/accessibility audit, full rotating-device behavior outside these selected screens, owner handset screenshots, 122 unapproved candidate words, and two-physical-phone Nearby regression.

## Next

Open a **draft WS59 pull request against WS58** to initiate independent CI. Inspect full emulator instrumentation and screenshots. If a real reachability or layout failure is reproduced, fix the smallest concrete defect on the separate draft without rewriting an existing saved puzzle. Only then decide whether a new player-facing QA APK is useful.

**New-chat handoff:** WS59 draft tests for 200% portrait / 150% actual landscape on 18-page Woodland QA, check the exact CI logs before claiming PASS; WS57 v107 remains safe owner APK.
