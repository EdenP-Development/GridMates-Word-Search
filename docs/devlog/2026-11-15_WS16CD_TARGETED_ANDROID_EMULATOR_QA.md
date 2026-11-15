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
