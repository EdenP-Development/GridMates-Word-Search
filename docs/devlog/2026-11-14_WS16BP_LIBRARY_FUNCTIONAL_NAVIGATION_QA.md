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

## Owner phone screenshot batch — first-play route (November 14, v75)

Owner supplied **seven sequential real-phone screenshots** in ChatGPT covering Home, Library, Woodland physical-book preview, book details, Page 1 after a found word, a subsequent hint, and Game notices. Screenshots were inspected in the conversation; the image files are **not stored in this GitHub repository**.

### Observed directly (visual only; don't equate to entire end-to-end test pass)

1. **Home:** GridMates Word Search branding, featured Woodland cover, Full Library/Collection, Continue Solo, Daily/Together, Quick Play preview, Custom and My Puzzles all appear in the screenshot without visible overlap. Separate counters on Home: book Woodland `0/3 pages complete`; Solo save `0/16 words` — correct distinct progress concepts.
2. **Modular Library:** owned Woodland, Space and Garden spines present; green buttons and wood-only bottom pager render after WS16BO; disabled Previous appears visually distinct. Screenshot alone doesn't demonstrate successful 1→2→3 navigation in v75.
3. **Woodland physical preview:** modal displays the matching cover, `2 chapters · 0 of 3 pages completed`, TEST BOOK designation, Back to shelf and View pages. The working route is *suggested by the next screenshot*, not instrumented.
4. **Book details:** accessible Woodland book hero; Start solving and chapter 1 Getting Started / Page 1 (12×12 Relaxed) / Page 2 (15×15 Classic) cards shown. Second card is cut at the viewport bottom due to normal scrollable content; no clipping defect established.
5. **Puzzle playing:** Woodland Page 1 12×12 board, MUSHROOM highlighted vertically, word-list entry checked, count `1 of 12 words found` and `Words · 11 left`; elapsed time progresses between consecutive screenshots from 0m 5s to 0m 10s. This demonstrates in-session state and UI response, **not disk reload yet**.
6. **Hint feedback:** first hint toast `Hint: look for a 6-letter word.` appears. It is valid first-stage progressive guidance but may feel weak if the user expects an extremely capable Coach; future usability evaluation, not a bug in this pass.
7. **Game notices:** modal displays both `Hint: look for a 6-letter word.` and `✓ MUSHROOM found!` in expected recency order. In-session notice history is visually functioning.

### Low-risk polish observations for a subsequent dedicated UX pass

- The woodland preview is a relatively tall plain light dialog with substantial empty whitespace; visually less cohesive than the highly detailed immersive wooden cabinet. Preserve keyboard/screen-reader accessible native dialog behavior while considering a tighter book-preview layout.
- Book details appear functional and legible, but the plain cards/white dialog are a distinct visual style from the bookcase. Don't change unapproved art or core progress/navigation simply to harmonize appearance.
- The temporary success and hint toast sits over the bottom of the word-list area. It is transient, not a proven interaction blocker. Later consider safer positioning or interaction testing at large text sizes.
- TEST BOOK is intentionally QA-only and should not appear as editorial production content.

### Remains unverified; next owner test

- **Critical:** exit Page 1, reopen Woodland Page 1, confirm that MUSHROOM remains found, and if possible confirm hint stages persisted; also relaunch the app to verify disk persistence after process restart.
- **Critical:** finish a page and verify 1/3 completed in book details and Home, then verify Next page and replay behavior.
- **Navigation:** Page 1 ↔ Woodland details ↔ physical Library, including system Back; tap both sides of bookcase pager, swipe, and see disabled last-page Next. Nine-book return-to-page-two case still needs actual device/emulator execution.
- **Separate saves:** ensure the Home Continue Solo count (0/16) remains unaffected by book gameplay.

Outcome: **promising early real-phone functional evidence, no observed blocking regression, not full physical QA acceptance.** Keep v75 design and PR #131 DRAFT while the above checks remain open.
