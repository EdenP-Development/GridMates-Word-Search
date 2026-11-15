# WS16CD — Execute v85 save, large-type and word-list tests on actual Android emulator

**Date:** November 15, 2026. **Owner direction:** Continue meaningful development and verification **before** another owner APK download or phone test. **Current draft:** [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), same feature branch, unmerged.

## Proven baseline and missing evidence

[WS16CC signed v85 dual-green receipt](2026-11-15_WS16CC_LARGE_TEXT_NEARBY_DAILY_V85.md) verified exact app source `f118ca76c9dd05d02a635af24bcc81109b9dd210`, Android [#43753383159](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43753383159) SUCCESS 81/81 and independent art [#43753383219](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43753383219) SUCCESS 82/82. Actual `app-debug.apk` bytes (SHA-256 `c7cc8c03ce24f2a61c68901992654db65cd7a568471048843f95189ba82a1689`) are same-signed, package `com.playereden.gridmates.wordsearch.qa`, versionCode 85, with all 360 entries identical to the independent art APK and all 219 asset bytes identical to v84.

**Prior limitation:** all latest Android instrumentation code *compiled*, but CI never executed it on an Android device. The risk particularly matters for `SoloStore` AtomicFile save, real Compose scroll behavior at 200%, and new continuous 16-word index.

## New actual-emulator test gate

- `scripts/ws16cd_targeted_emulator.sh`: install the **exact source-verified v85 app APK from GitHub**, then the newly built instrumentation APK. Run these actual five Android API35 tests by class:
  1. `SoloRewardRecordsJourneyAndroidTest` — interrupted unfinished Solo → disk flush → new store reopens → hint → finish → First Page/Rabbit → portrait equip → reopen persistent Records.
  2. `NearbyDailyLargeTextAndroidTest` (2 tests) — 320×640dp/200% Nearby three modes and Join, plus Daily Today action/journal accessibility.
  3. `ContinuousSoloWordIndexAndroidTest` (2 tests) — 15×15/16-word last target is independently scroll-reachable; short Daily remains paged.
- `.github/workflows/ws16cd-targeted-emulator.yml`: one-purpose, opt-in/manual or self-file-push-triggered API35 KVM emulator job, separate from normal speedy APK/art CI. Uses the existing upstream-SHA256-pinned Gradle, pinned portrait and OFL-font source dependencies, actual hash-checked [v85 APK artifact #14176978654](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43753383159/artifacts/14176978654), Android test APK and exact expected five-test success guard. Stores test output as an artifact even on failure.
- **Scope:** no production Kotlin changes, no generated assets/art permissions, no save schema, no app version bump, no network protocol changes, no newer owner APK or main-branch merge. The test package is freshly built; its **app under test remains the verified signed v85 artifact**, avoiding a silently different art variant.
- Script source [`adb9f8b265c905b5efd439126ab6a9626eb6ed4b`](https://github.com/PlayerEden/GridMates-Word-Search/commit/adb9f8b265c905b5efd439126ab6a9626eb6ed4b); workflow source [`7460365792ab9288d134a3997eb0de1fc5744e31`](https://github.com/PlayerEden/GridMates-Word-Search/commit/7460365792ab9288d134a3997eb0de1fc5744e31).

## Execution / acceptance

[Targeted emulator workflow #43754358738](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43754358738) **RUNNING at initial receipt**; no emulator pass is claimed before the actual job/steps and runner output confirm five tests. Adding the script to existing `scripts/**` changes also queued ordinary PR Android and art jobs; they are redundant for unchanged production app. Do not count those as emulated proof.

**Next exact action:** read #43754358738 status/job logs. If red, identify the real source failure vs test setup failure and repair appropriately. If green, document 5/5 actual instrumented outcomes separately from build-only 81/82 checks; no real-user/two-phone or physical touchscreen acceptance can be inferred. Keep v85 signed QA as automated fallback, no owner download yet, PR draft.

## FINAL actual Android emulator receipt

**[WS16CD dedicated workflow #43754358738](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43754358738): SUCCESS.** Actual GitHub Actions job `targeted-emulator` completed with no failed steps. Its `reactivecircus/android-emulator-runner@v2` successfully booted an Android API35 Google APIs x86_64 emulator (Pixel 4 profile), installed the **previously verified v85 signed app APK** (SHA256 checked), and installed a newly compiled Android instrumentation test package from the same source.

GitHub job log explicitly prints `INSTRUMENTATION_STATUS: numtests=5`, a successful `INSTRUMENTATION_STATUS_CODE: 0` for each test, and **`OK (5 tests)`**. Script's own five-test success guard passed. Instrumented test classes: one `SoloRewardRecordsJourneyAndroidTest`, two `NearbyDailyLargeTextAndroidTest`, and two `ContinuousSoloWordIndexAndroidTest`.

[Actual emulator output artifact #14177821852](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43754358738/artifacts/14177821852) contains captured instrumentation results. Runtime test execution (five) is distinct from 81/81 Android build steps and 82/82 independent artwork/release steps for v85.

**Verified on emulator:** AtomicFile unfinished Solo write/reopen, assisted completion, Rabbit achievement/portrait equip/persistent Records render, 320dp/200% Daily and Nearby primary controls, independent scrolling to last of 16 targets, and short Daily paging regression. These five controlled scenarios passed, but not all combinations of real UI behavior or phone devices.

**Still unverified:** owner-installed real handset, touch tracing/pinch ergonomics, physical 200% readability beyond test semantics, Wi-Fi network state transitions/Host/Join between two devices, persistence across a real v84→v85 update on the owner's data, and complete artwork acceptance. This run does not claim any of those. v85 remains the current verified **QA** package, PR #131 DRAFT, no owner download requested until they choose the bundled test.

**Next:** keep developing core polish only when evidence warrants it; the next *physical* acceptance should bundle v85 Nearby Wi-Fi, Daily, 16-word Solo scrolling and full saved game completion/rewards/Records. Keep the owner install-over path safe (do not uninstall or clear app data).
