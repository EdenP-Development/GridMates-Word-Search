# WS60 verified Android QA — v108 readable book page

> **VERIFIED AUTOMATED TEST BUILD. Not owner/phone or Google Play approved.** Exact executed source `0a546c2f99d0115eb4f9d30c00ef86598fe3b135`; subsequent documentation-only commits do not alter the tested app.

## Source, tests and screenshots

- [Android/core workflow #45065450259](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45065450259): **SUCCESS**. Full source/core/app unit/lint/QA APK checks completed. API35 Android emulator executed `OK (3 tests)` in 320dp portrait at **200% text** and `OK (1 test)` in genuine Activity landscape at **150% text**.
- Portrait tests: real six-chapter navigation/Play/Bookmark, 20×20 Expert real touch drag with saved found-word and Hint, precise native row/column word selection with persisted result. WS60 also asserts compact printed **Page 18**, **0 / 24 found**, full spoken volume/board/page title and full spoken word-count text, plus native result after selection.
- Landscape test: 15×15 Classic actual finger trace, Hint and saved word, verifies real Activity and Compose orientation; original roomy landscape title/progress unchanged.
- [Independent art/debug/release workflow #45065450280](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45065450280): **SUCCESS** on same source SHA. No artwork added or release terms approved.
- [Seven emulator screenshots and instrumentation/logcat #14771332335](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45065450259/artifacts/14771332335): ZIP downloaded and inspected. At 200% portrait, previously clipped `Woodland…` now displays **Page 18**; `0 of 24 words found` no longer wraps two lines and now displays **0 / 24 found**. More word-list content remains visible below the unchanged 20×20 grid. Previous WS59 screenshot is a comparison reference. Immediate before/after swipe captures are byte-identical even when saved/UI assertions passed; they do not independently prove animated highlight timing.

## Exact signed QA package and in-place upgrade boundaries

- [GitHub Actions standard QA APK ZIP #14771725725](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45065450259/artifacts/14771725725), containing `app-debug.apk`, **34,230,331 bytes**, independently computed SHA-256 `6a7d59e6fed8bd2524abd216027b3caf7445aa8afa79c7fa8f2f3600b65a3b3b`. ZIP integrity checked.
- Binary AndroidManifest independently parsed from APK: **`com.playereden.gridmates.wordsearch.qa`**, **versionCode 108**, **versionName `0.9.64-ws60-book-page-readability-qa`**, compile SDK35. This is a non-store QA package.
- Android APK v2 signing certificate independently extracted from this and the [known-good WS57 v107 signed APK artifact #14734405724](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44971601632/artifacts/14734405724). **Identical QA-only certificate** in both: SHA-256 `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419`. WS57 original APK SHA-256 independently reconfirmed: `304b389d53935ceb8a8857666caf00fbc15e3c51a440ab3cced07f1f684e82f9`.
- Higher code and identical QA package+signer support an **install-over** for compatible QA installs, but **physical owner v107→v108 in-place upgrade and existing on-device save verification remain OPEN**. Never advise uninstalling or clearing storage.
- No production Play signing, public publishing or editor/rights clearance.

## Changed and untouched

**Changed in WS60:** only book-page text presentation at 150%+ portrait; optional spoken-content descriptions on existing shared title/progress components; Android UI test assertions; same-signer QA build version code/name 108; branch-scoped emulator step and docs. **Not changed:** real board grid coordinates, touch engine, V5 puzzle generation, original **63 QA + 18 immutable Woodland page IDs/digests**, owner save journals/migration, hints/scoring/rewards, existing illustrations, Daily/Quick Play/Nearby, released wordpacks. **122 Woodland editorial candidates remain unapproved**; QA preview is debug-only.

## Remaining gates and next action

Owner real handset visual test of Library, large-font chapter/page, 12/15/20 word traces, Hint feedback after delay, pause/resume and save migration; real TalkBack speech; two physical-phone Nearby host/join/Co-op/Race/Duel/reconnect; editorial review of 122 words and production store assets/signing/AAB/monetization. None are certified by emulator success.

**Next:** offer the verified code108 QA APK for optional install-over and collect concrete owner screenshots/interaction feedback. Then fix actual player-facing issues together rather than another speculative asset or vocabulary pass. Retain old WS57 v107 APK as separately hashed fallback.
