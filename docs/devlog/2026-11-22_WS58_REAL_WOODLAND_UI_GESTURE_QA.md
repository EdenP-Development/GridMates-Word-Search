# WS58 — Real Woodland chapter navigation and finger-trace Android UI QA

> **FINAL AUTOMATED STATUS: VERIFIED — 3/3 REAL ANDROID COMPOSE TESTS PASS; OWNER PHONE ACCEPTANCE OPEN.** Executed source SHA `c41eaf73d0da9befc42a3e58e86c6e09ef6edce0`; both independent GitHub workflows completed SUCCESS. Docs-only receipts written afterward do not change executed source.

## Exact tested receipt — Nov 22, 2026

- **Standard Android/core workflow:** [#45028233366](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45028233366), **SUCCESS** at `c41eaf73d0da9befc42a3e58e86c6e09ef6edce0`. Existing core/app unit tests, lint, instrumentation compilation and Android QA APK passed; branch-scoped API35 emulator **actually executed** `Ws58WoodlandPlayerJourneyAndroidTest` with explicit final output `OK (3 tests)`, 32.296 seconds, and no failed tests. Verified directly in the job's published log.
- **Executed UI behaviors:** `twentyCellFrozenPreviewAcceptsRealDragHintAndRestart` (real 20×20 word trace, Hint and disk reload); `twelveCellFrozenPreviewAcceptsRealDragHintAndRestart` (12×12 equivalent); `sixChapterPickerScrollsToForestDiscoveriesInRealBookDetail` (actual scroll/jump to the sixth chapter on a 320dp book-detail viewport). Each passed `INSTRUMENTATION_STATUS_CODE: 0`.
- **Dedicated emulator instrumentation + AndroidRuntime report:** [artifact #14756053893](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45028233366/artifacts/14756053893), uploaded successfully. This artifact is **test evidence**, not an APK.
- **Standard QA APK artifact:** [#14756229876](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45028233366/artifacts/14756229876). An APK was built but **its internal binary SHA-256 was not independently rechecked** in this pass; no new build is required for owner testing because WS58 modifies **test harness only**.
- **Independent art/debug/release workflow:** [#45028233360](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45028233360), **SUCCESS**, exact same tested source SHA. No new release/content approval.
- **Precise limitation:** 320dp Compose test container is not a full device-density matrix, TalkBack test, complete 18-page manual playthrough, or owner physical handset acceptance. WS57 signed tested QA v107 remains recommended phone fallback. All 122 novel terms still **editorially unapproved** and the entire new book is QA-only.

This pass did not alter the signed WS57 gameplay, puzzle words, content hashes, saved IDs or production app runtime.

## Who, what, where, why

- **Owner direction:** Continue GridMates: Word Search development while physical-phone testing of the verified WS57 playable Woodland preview is pending.
- **Implemented by:** ChatGPT assistant through the connected GitHub repository; GitHub Actions will be the independent build/test executor after the draft PR starts.
- **Goal:** Close the specific gap between WS57's catalog/save-only emulator tests and the real player's book-navigation/gesture experience, without inventing phone results.
- **Where:** New isolated `feature/ws58-woodland-real-ui-qa-2026-11-22` branch, based on WS57 draft [PR #170](https://github.com/PlayerEden/GridMates-Word-Search/pull/170). No source changes to main or Work.

## Previous verified baseline and fallback

- **Starting WS57 head SHA:** `fb616efb9910852e2bfbe0924a0725d8a4ffd17c` (documentation-only after executed code).
- **WS57 exact tested code SHA:** `a39edd37041786e9f9a7ffa2deaf03e8dabda95c`.
- **WS57 verified Android/core:** [run #44971601632](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44971601632), SUCCESS.
- **WS57 independent release/art:** [run #44971601764](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44971601764), SUCCESS.
- **Known signed and emulator-installed QA APK, fallback:** [WS57 APK ZIP artifact #14734405724](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44971601632/artifacts/14734405724), `app-debug.apk`, v107 / `com.playereden.gridmates.wordsearch.qa`, SHA-256 `304b389d53935ceb8a8857666caf00fbc15e3c51a440ab3cced07f1f684e82f9`.
- **Owner phone acceptance:** NOT done. No permission to publish words, merge branch, change saved pages or claim an install.

## Actual changes in WS58

- `app/src/androidTest/java/com/playereden/gridmates/wordsearch/Ws58WoodlandPlayerJourneyAndroidTest.kt`: three NEW device-executable Compose UI tests on the exact WS57 versioned QA-only book and previously frozen boards.
  1. Show the actual detailed book with six chapters in a 320dp-width Compose viewport. Open the native chapter picker, scroll to final chapter and jump to *Forest Discoveries*. Require that the chapter is visible and no page-completion ledger was written.
  2. On actual 12×12 *First Steps* V5 page, tap real hint button, touch-swipe a word's on-grid coordinates and confirm its claimed ID/hint survive a fresh session-store instance.
  3. Same end-to-end interaction on 20×20 *Wildlife Field Notes* Expert page at 320dp.
- All tests inject the real `BookQaPilot.catalog(context)` and **unique disposable filenames**. No test uses actual profile or WS11/WS57 QA store filenames.
- `scripts/ws58_woodland_player_emulator.sh`: install debug app and androidTest APK, execute just the 3 new UI tests, retain instrumentation/AndroidRuntime reports, fail on missing or failed tests.
- `.github/workflows/ws01-android-qa.yml`: WS58 branch-scoped Android emulator execution and evidence artifact; all standing source/core/lint/build checks still run.
- No production Kotlin, V5 generator, word lists, SHA pins, old 63 QA pages, new 18 frozen WS57 pages, assets, saved-file schema, bonus recognition, UI layouts, Nearby or release catalogs were modified.

## Pre-execution plan (historical; superseded by verified receipt above)

- **Build triggered:** not at authoring time; draft PR opening should start both existing Android/core and art/release pipelines.
- **Expected signed version:** unchanged WS57 v107 and QA package; only androidTest and CI test harness changed.
- **Requested device execution:** 3 real Compose tests through dedicated API-35 Android emulator. *Source compilation does not prove gesture correctness.*
- **Actual source SHA tested:** PENDING.
- **GitHub Actions URLs / conclusions:** PENDING.
- **APK artifact and exact checksum:** use WS57 fallback above until new exact-source artifact is verified. A new APK from a different run may have a different ZIP byte digest despite no runtime source changes.
- **Blockers:** physical owner UX, TalkBack/text-scale/orientation, release/word signoff, two-phone Nearby acceptance remain separate.
- **Rollback:** existing WS57 signed APK and separate WS11/WS57 journal preservation. Never uninstall, erase app data, or overwrite historical page IDs.

## Outcome and next exact action

Open WS58 as a DRAFT PR **against WS57's exact branch**, inspect both actual CI runs, read the emulator `instrumentation.txt`, and repair any compile/interaction failure before stating green. If passing, treat the result as a navigation/touch regression improvement—not as owner handset visual approval. Follow up with owner device screenshots when available and then prioritize issues actually observed in the Library or gameplay.

**New-chat one-liner:** Continue WS58 draft on WS57 and check actual instrumented 320dp chapter-navigation + 12/20-board touch tests; keep the WS57 signed APK as fallback until real WS58 evidence is verified.
