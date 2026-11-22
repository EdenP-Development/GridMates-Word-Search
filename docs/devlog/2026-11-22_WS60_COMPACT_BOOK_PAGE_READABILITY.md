# WS60 — Large-type book page readability and Android QA

> **Status: SOURCE AUTHORED — CI PENDING.** Gameplay UI changed but untested at authoring. Do NOT claim a new APK is install-ready or owner phone approved before exact source workflows pass.

## Who, what, where, why

- **Owner:** asked to continue holistic GridMates: Word Search refinement after WS59 accessibility validation.
- **Work performed:** ChatGPT assistant authored native Compose text layout changes and QA assertions on a new draft branch, based on actual WS59 emulator captures.
- **Observed issue:** At 320dp/200% text, long `Woodland - Editorial QA Preview` title was visually clipped as `Woodland…` and `0 of 24 words found` consumed two giant lines above the 20×20 grid. The game mechanically passed all WS59 drag, Hint and precise-selection checks; avoid pretending a touch-engine defect exists.
- **Branch:** `feature/ws60-book-page-compact-readability-2026-11-22` based on verified WS59 PR #172, separately draft, not merged to main/Work.
- **Previous verified exact source:** `89ffdc328a2219785ec2b7ec8083226ad22e7965`. [Android/core #45060619654](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45060619654) + [art/release #45060619645](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45060619645), SUCCESS; four real 200% portrait / native 150% landscape Android UI tests, with seven screenshots [#14769174434](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45060619654/artifacts/14769174434).

## Actual changes

- `app/src/main/java/.../BookPageGameplayScreen.kt`: at **>=150% font and narrow portrait**, show a legible `Page 18` title instead of a clipped long volume title; print `0 / 24 found` in the native progress panel instead of long two-line copy. Existing normal font and spacious landscape labels are unchanged. Grid geometry, 48dp controls, Hint, penalties and durable progress unchanged.
- `PuzzlePlayChrome.kt`: optional `titleSpokenLabel` and `spokenLabel` semantics on existing native Text. **TalkBack still has full volume title + frozen board title + page number, and `0 of 24 words found`**, despite compact visible wording; no changes to other callers. Spoken UI has not been physically TalkBack-reviewed.
- `Ws59WoodlandTextAndLandscapeAndroidTest.kt`: preserve four actual native UI tests; add explicit checks for compact 200% printed Page 18/0 of 24 + complete contentDescriptions, and updated 200% saved word count. Landscape 150% retains full existing copy. Disposable test store filenames never touch owner saves.
- `.github/workflows/ws01-android-qa.yml`: add WS60-specific API35 emulator runner using the existing physically rotated Android WS59 script and screenshots/log artifact. Default Android/core/lint and independent artwork/release pipelines remain in force.
- `app/build.gradle.kts`: debug/QA APK versionCode default **108**, versionName `0.9.64-ws60-book-page-readability`, same established QA-only signer/package for compatible install-over once verified.

**Intentionally unchanged:** all 63 historical and 18 WS57 frozen QA pages/board hashes/saved IDs, V5 generator, scoring, hint engine, 122 unapproved Woodland editorial words, original QA save-journal bytes, Collection/reward policy, existing assets, Nearby/Daily/Quick Play, release-only catalog/signing isolation, Work and main.

## QA receipt — PENDING

- **Code SHA:** to be recorded from exact PR workflow run (no build success claimed yet).
- **Android/core:** pending; must pass core+app/lint, genuine API35 emulator `OK (3 tests)` 320dp/200%-font portrait and `OK (1 test)` real Android landscape 700×340dp/150%-font, including durable found IDs, spoken label semantics, 48dp actions, and screenshot capture.
- **Art/release:** pending, no new artwork or asset exposure permitted.
- **APK:** new QA code108 signed ZIP pending exact verified Actions artifact; no checksum independently measured at authoring.
- **Fallback:** last separately hashed signed WS57 v107 [artifact #14734405724](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44971601632/artifacts/14734405724), SHA256 `304b389d53935ceb8a8857666caf00fbc15e3c51a440ab3cced07f1f684e82f9`. Do not uninstall or wipe data.
- **Owner phone & full TalkBack acceptance, 122 term editorial approval, physical two-phone Nearby regression, production publisher/catalog/Play Store** remain open.

## Next

Open WS60 DRAFT against WS59 and inspect exact source CI output, artifact and 200% before/after emulator screenshots. If tests fail, correct one observed issue rather than widening scope. If successful, provide *code108* test APK link with explicit safe install-over and physical acceptance checklist. Complete first owner phone UI pass next, then two-phone Nearby acceptance. Do not automatically release or merge.

**New-chat one-liner:** Continue WS60 from exact latest draft, evaluate 200% Page-number/progress compaction and spoken semantics in Android emulator, preserve 81 frozen QA pages and save data, use WS57 v107 fallback if new QA doesn't pass.
