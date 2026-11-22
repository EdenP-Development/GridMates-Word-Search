# WS59 — Woodland large-text, actual landscape and UI screenshots

> **FINAL AUTOMATED STATUS: VERIFIED.** Later WS59B and WS59C fixes passed four actual Android UI tests at source `89ffdc328a2219785ec2b7ec8083226ad22e7965`; [Android/core #45060619654](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45060619654) and [art/release #45060619645](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45060619645) both SUCCESS. The initial failure below is retained as a historical incident, **not** the current milestone result. [WS59B root-cause correction](2026-11-22_WS59B_EXPERT_TOUCH_DIAGNOSTIC.md) · [WS59C final receipt](2026-11-22_WS59C_LANDSCAPE_ACTIVITY_ORIENTATION.md).

> **HISTORICAL WS59 FIRST RUN FAILURE — NOW SUPERSEDED BY VERIFIED WS59B/WS59C.** Latest exact tested code `0c05a0da74007ba03fba7f4b7d21ee5ed1293cb8`. Android/core [#45036801446](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45036801446) failed during the dedicated UI emulator test; independent art/release [#45036801420](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45036801420) **SUCCESS**. No owner phone acceptance or public editorial clearance.

## WS59 actual Android results, November 22

**Sequence of source-verified QA blockers:**
1. The initial test source did not compile because `DpRect` lacks the assumed `.width`/`.height`; code corrected to subtract edge coordinates (test-only).
2. The first corrected emulator attempt failed to connect ADB during boot; a targeted rerun **booted successfully** and installed both APKs, but stopped at unsupported `cmd window set-user-rotation`.
3. The runner was corrected to AOSP-supported `adb shell wm user-rotation lock 0/1` and a separate Compose `LocalConfiguration` landscape check. Both production and instrumentation APK compiled successfully at final executed commit `0c05a0da74007ba03fba7f4b7d21ee5ed1293cb8`.
4. The [Android/core run #45036801446](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45036801446) **actually executed native instrumentation** on the Android API-35 emulator:
   - `at200PercentTextAllSixChaptersAndLastPageActionsStayReachable`: **PASS**, six-chapter picker scroll/jump to Forest Discoveries, Play and Bookmark controls reachable at 320dp/200% text.
   - `at200PercentTextExpertPortraitRetainsHintToolsAndGrid`: **FAIL** at test line 181. The actual frozen 20×20 V5 page, Back/Puzzle Tools, precise picker text, minimum grid geometry and persisted Hint assertions succeeded. After a simulated finger drag, the Compose semantics tree did not expose `1 of 24 words found`. **Root cause NOT established**: current test checks progress text before saved `foundIds`; cannot yet separate gesture rejection from inaccessible/unchanged progress text.
   - `at150PercentTextClassicLandscapeRetainsHintToolsAndGrid`: **NOT RUN** because script fails fast after portrait test. No landscape pass claim.
   - [Actual instrumentation and runtime artifact #14760148447](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45036801446/artifacts/14760148447); signed QA APK build artifact [#14759353947](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45036801446/artifacts/14759353947), **internal APK hash NOT rechecked**, no reinstall required.
5. [Independent art/release run #45036801420](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45036801420) completed **SUCCESS** from the identical tested source, zero production-release approval implied.

**Safeguards kept intact:** No production Kotlin, 63 original QA page identities, 18 WS57 puzzle IDs/SHA pins, generator, any previous save, hint policy, bonus recognition, release lexicon, image asset, Daily/Quick Play, Nearby or Work/main modified. All 122 candidate words remain unapproved.

**Next SINGLE corrective task:** Improve WS59 test diagnostics and failure screenshot retention in one combined commit: read/print saved `foundIds` directly after simulated drag *before* UI text assertion, capture test screenshots before/after gesture, export artifacts on failure, and run portrait and landscape groups independently so the 150% landscape case is actually checked. Add a separate row/column precise-picker accessibility test to establish fallback without silently exempting a failed finger trace. Only then repair a **demonstrated** UI touch issue or test geometry; avoid replacing frozen content, speculative aesthetics, and repeated blind full Actions rebuilds. Owner handset acceptance and 122 editorial approvals still open.



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

## Original pre-execution checklist (historical; superseded by exact receipt above)

- **Test source commit:** to be set from the PR's exact executed head after CI; this text alone is not proof.
- **Both workflow conclusions and emulator report:** PENDING. No success claimed.
- **New APK:** build may produce a QA artifact; no install required because runtime source is unchanged. Do not call its signing/hash verified without inspecting actual receipt.
- **Fallback:** tested WS57 v107 above.
- **Open gates:** 200% font and full landscape native UI geometry, TalkBack speech/accessibility audit, full rotating-device behavior outside these selected screens, owner handset screenshots, 122 unapproved candidate words, and two-physical-phone Nearby regression.

## Next

Open a **draft WS59 pull request against WS58** to initiate independent CI. Inspect full emulator instrumentation and screenshots. If a real reachability or layout failure is reproduced, fix the smallest concrete defect on the separate draft without rewriting an existing saved puzzle. Only then decide whether a new player-facing QA APK is useful.

**New-chat handoff:** WS59 draft tests for 200% portrait / 150% actual landscape on 18-page Woodland QA, check the exact CI logs before claiming PASS; WS57 v107 remains safe owner APK.
