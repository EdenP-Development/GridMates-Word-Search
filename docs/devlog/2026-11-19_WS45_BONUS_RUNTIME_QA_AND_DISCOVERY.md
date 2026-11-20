# November 19, 2026 — WS45 optional side-word journal and executed Android scoring QA

> **Quick status:** Candidate branch, full Kotlin/Android/art CI and emulator results pending. Previous WS44 v105 is a verified build, not physically owner-approved.

## Who, what, where, why
- **Owner request:** Continue meaningful, independent gameplay work while ChatGPT Work develops the broader GridMates: Word Search project. Preserve Work's branches and leave full artistic direction to Work.
- **Implementation:** Game Design chat; GitHub Actions runner executes Kotlin unit, lint, Android instrumented tests and package validation.
- **Objective:** Make WS44's +15 unlisted real-word rewards discoverable and **actually run** core persistence and bonus-word Android tests, rather than only compile the instrumentation.
- **Scope:** Only optional native UI journal, added emulator runner and QA workflow; no visual asset changes, multiplayer scoring, puzzle-generation changes or release content approvals.

## Checkpoint
- Parent: **WS44** draft [PR #156](https://github.com/PlayerEden/GridMates-Word-Search/pull/156), exact cumulative source `9c7eddea7967a61f1d353f1466dcc9a0328dd636`, stacked on WS43's cumulative branch.
- Prior proof: [standard Android v105 run #44649467539](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44649467539) **93 successful steps, 0 failures, 14 optional skips**. [Independent artwork/release run #44649467353](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44649467353) **92 successful, 0 failures**. Two APKs held matching 372 package entries, v105, same QA package and signing certificate as v104. The new `SideWordScoringAndroidTest` had been **compiled, not executed**.
- Earlier safe fallback: **v104** on WS42 physically unreviewed by owner, but passed actual emulator/saved 20×20 install-over and 31 Android tests; v105 built and signer-verified but bonus-word device execution was pending.
- Independent vocabulary/replay audit: [issue #155](https://github.com/PlayerEden/GridMates-Word-Search/issues/155). This pass does not mutate the Fall pack.

## Changes in WS45
1. **`WordSearchScreen.kt`:** Add an optional `Options → Extra words (N/12)` text-only journal dialog. It displays actual saved words, count, and derived bonus points, plus the explanation that only curated off-list 4–16-letter straight-line words qualify. Does **not** overlay the letter board, require another tap during gameplay, or change target completion, achievements, hint policy, score calculation, local store schema, or any asset.
2. **`scripts/ws45_bonus_emulator.sh`:** Install the signed debug APK and Android instrumentation APK onto Android 35 emulator; actually execute `SideWordScoringAndroidTest`, `QuickPlayAndroidTest`, `SoloPersistenceTest`, `DailyPersistenceTest`, `SoloSchemaGuardsAndroidTest`, `GameBackupAndroidTest`. Fail on non-success, `FAILURES!!!`, missing `OK (N tests)`, or Android process failure. Save instrumentation output and AndroidRuntime logs as an artifact.
3. **`.github/workflows/ws01-android-qa.yml`:** Branch-conditional Android emulator on this isolated WS45 branch with KVM, full executed suites and result artifact. Other branch behavior unchanged.
4. **`tools/asset_pipeline/test_ws45_bonus_journal.py`:** Regression source checks for real journal and CI emulator contract; both existing independent workflows execute it.
5. **`app/build.gradle.kts`:** versionCode **106**, versionName **0.9.62-ws45-bonus-journal-qa** for QA install-over. Same QA signer and unchanged production release.

## Tests and acceptance gates
- **Automated CI at exact tested final SHA:** Android Kotlin, lint, instrumentation compilation, signed debug APK, source checks, debug/release art separation. Independently run the native Android instrumentation suite **and verify the actual test count and output**.
- **APK:** Must confirm package `com.playereden.gridmates.wordsearch.qa`, versionCode 106, v105/v104-matching QA cert and identical packaged contents across independent artifact builds before providing an install-ready claim.
- **Device acceptance:** owner hasn't tried v105/v106. Actual on-phone usability, words found, backup/reopen, accessibility font scales and balancing still need review.
- **Risks:** Some old/QA word packs are debug-only, not production licensed/approved. Bonus words do not count for Nearby games or Book pages. The Quick Play real-word discovery search in instrumented tests may expose gaps; treat failure as a bug to investigate, not a reason to silently skip.
- **Rollback:** v105 verified build remains safe; do not uninstall or wipe saves. v104 has previous real emulator receipt.

## Work isolation and next step
- Branch: `feature/ws45-bonus-android-reliability-2026-11-19`, stacked **only** on WS44 PR #156; remains a draft PR and never merged to main. Work's active WS43 / WS42 branches are untouched.
- **Next action:** inspect both CI run conclusions and, if emulator fails, repair the actual scoring implementation/tests rather than accept compile-only coverage. Attach source SHA, run/test counts and artifact receipts to the PR and original Work issue #140.
- **Then:** optionally investigate a replayability scoring balance and general/family dictionary expansion under release-editorial review; not part of this feature build.
