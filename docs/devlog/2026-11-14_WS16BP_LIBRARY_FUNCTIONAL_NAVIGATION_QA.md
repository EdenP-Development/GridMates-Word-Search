# WS16BP — Functional navigation, return-to-shelf continuity, and v75 dual QA

Date: 2026-11-14. Status: **coded and dual-green, owner physical functional QA pending**. Active PR: [#131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) remains DRAFT.

## Owner direction

Three real phone screenshots of WS16BO v74 showed the first, second, and third Library bookcases, with the rectangular pager backplate removed. The visual direction was accepted for the next stage: freeze the walnut/ivy cabinet layout and validate actual Library functions rather than repeatedly polishing decorative pieces.

Owner requested a functionality pass around: actual books, chapter/page access, save-reopen behavior, Back navigation, and bookcase paging. Do not modify artwork, invented ownership, content, local-save schemas or monetization in this pass.

## Audited core behavior (code and existing test coverage)

- `BookLibraryFlow.kt` connects catalog-backed spines -> physical book preview -> real book details -> chapter/page -> gameplay -> book details or Home on Back as intended for shelf-open versus direct Home play.
- `BookLibraryStore.kt` stores independently from Solo using `AtomicFile`; unknown/corrupt progress is preserved and writes blocked.
- `BookPageSessionStore.kt` writes found-word claims/hints before acknowledging success, maintains elapsed-time checkpoints, and can recover finished sessions into the completed book journal after an interrupted transition.
- Existing Android instrumentation source tests cover book selection/bookmark/page navigation, session persistence, interruption recovery, unreadable data protection, and real/future shelf semantics. **The standard GitHub workflow compiles Android instrumented sources but does NOT execute device/emulator UI tests.** Physical verification still needed.

## Specific problem fixed: returning from a book resets shelf position

The modular cabinet previously used a fresh `rememberPagerState(pageCount={pages})` when the shelf component was re-entered. After inspecting a book on a later real bookcase page, the shelf could re-enter at page 1 even though the owner had not navigated there.

- `BookLibraryFlow.kt`: keep a per-catalog, in-memory `cabinetPage` across Library -> Book -> Page -> Book -> Library routes.
- `PuzzleBookshelfScreen.kt` and `BookcaseBookshelfScreen.kt`: pass `initialCabinetPage` and `onCabinetPageChanged` to the actual modular bookcase.
- `WS16ATModularLibraryScene.kt`: initialize pager at the saved shelf page, clamped to `0..pages-1` when viewport/catalog capacity changes, and report live page position using `LaunchedEffect`.
- No changed puzzle IDs, save files, owned-book actions, future decorative content, swipe logic, arrow enabled state, page dots, shelf spacing, cabinet art, or physical touch targets.
- `QaActualBookcaseAndroidTest.kt`: new source-compiled Compose regression that presents nine real books (so page two contains a real volume), opens the ninth volume, returns to the shelf, and checks the retained second-page position and enabled Previous arrow. This test requires a device/emulator to execute; CI only compiles it.
- `test_ws16bn_green_layout.py`: executable source guard checks correct routing and clamped pager initialization.

Source implementation [`6f0ccaa46840006d13918e81fcb32a767a8ad94b`](https://github.com/PlayerEden/GridMates-Word-Search/commit/6f0ccaa46840006d13918e81fcb32a767a8ad94b). QA versionCode **75**, versionName **0.9.31-library-return-continuity-qa**; same package and signing configuration as v74 for install-over updates.

## Exact-source GitHub verification

- [Android QA #43635464423](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43635464423): **SUCCESS, 71/71 steps**, original source `6f0ccaa...`. [GitHub artifact #14129808202](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43635464423/artifacts/14129808202). Artifact ZIP SHA256 `468bbc7361c339bf2b28a3c9d4a68bf9796bf5af7d192c6ce809f38d9d15efcb`. Extracted `app-debug.apk`: 30,095,757 bytes, SHA256 `d7edb75b690e7817527e223a3149c627706c4880b68e0fb79a2ff5958f5437fa`.
- [Independent art/release QA #43635464422](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43635464422): **SUCCESS, 72/72 steps**. [Artifact #14129788596](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43635464422/artifacts/14129788596). ZIP SHA256 `fe082bc39650bed9f2f9f316fdca2e82860ec99fec7fe06f28a1e4cbd1d3a775`.
- Both downloaded ZIPs passed their integrity tests. The two independently built QA APKs contain **358/358 identical uncompressed entries**; external APK bytes differ due to archive/signing envelopes.
- Compared standard v75 APK to verified owner v74: all **217/217 packaged `assets/` bytes exactly match**. Only `AndroidManifest.xml` and `classes4.dex` contents differ, consistent with version + code-only work.
- Same QA signing identity was intended/preserved through existing signing configuration. Owner should **install directly over v74 without uninstalling**, then confirm local progress is preserved.

## Outstanding real-phone acceptance

1. First Library shelf: choose Woodland, Space and Garden, see real preview, open book details and chapter pages, then Back to Library; no accidental Home exits.
2. Open one unfinished page; use one hint and find a real word; Back, reopen, and verify saved word/hint state. Fully complete a page and confirm completion is reflected in the book journal.
3. Navigate 1 -> 2 -> 3 -> 2 -> 1, including swipes and arrow buttons, verify both terminal disabled states and read-only blank future bindings.
4. The new return-to-shelf page restoration is covered for 9-book scenario in Android instrumentation source, but current three-book QA catalog has only one real-ownership page; device execution or a later expanded real catalog is still needed to observe that scenario on phone.
5. QA release art and visual design remain pending owner signoff; keep PR #131 DRAFT and preserve rollback v74 artifact.

No claim of device test execution or production release approval is made.
