# WS57 — Real six-chapter Woodland playable QA preview

**TECHNICAL QA VERIFIED, PHONE/EDITORIAL APPROVAL PENDING.** Both independent Android pipelines passed at exact source `a39edd37041786e9f9a7ffa2deaf03e8dabda95c`; the dedicated emulator actually executed 3 WS57 tests with no failures. A real signed QA APK was inspected and archived.

## Exact source tested — full independently verified Android acceptance

**SOURCE COMMIT SHA:** `a39edd37041786e9f9a7ffa2deaf03e8dabda95c` (the final source/test commit before documentation-only update). Two independent GitHub workflows **SUCCESS, zero failures**:
- [Standard Android/core QA #44971601632](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44971601632) — core/app unit tests, lint, instrumentation compile, signed debug APK, WS57 Python source/old-63-page isolation, **dedicated Android emulator actually executed all 3 WS57 tests**. Emulator report `OK (3 tests)`, runtime log has no crash.
- [Independent art/debug/release #44971601764](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44971601764) — source checks, debug + release variant build, reviewed art/release isolation all pass.

**Artifact receipts:**
- [Standard signed QA APK ZIP artifact #14734405724](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44971601632/artifacts/14734405724) — exact **34,230,327-byte** `app-debug.apk`, SHA-256 `304b389d53935ceb8a8857666caf00fbc15e3c51a440ab3cced07f1f684e82f9`. This is the exact build installed in the passing emulator and the user-facing Android download.
- [Independent art QA APK ZIP #14733469368](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44971601764/artifacts/14733469368) — same size but different APK byte hash `3532095ffc6d64f1c8b9fd24a6df953d10ebbb2881024d58c8c3967dcc822154` (do not claim byte-identical APKs: independent Gradle builds can differ due to ZIP/signing metadata). Both were built from the same tested source and contain both WS57 debug-only TSV assets.
- [Device-executed emulator report ZIP artifact #14733788952](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44971601632/artifacts/14733788952) — `instrumentation.txt` explicitly lists all three passing `Ws57PlayableWoodlandPreviewAndroidTest` cases, then `OK (3 tests)`. Its `android-runtime.txt` has no app crash.

**Exactly tested real Android behavior:**
1. All 18 new QA-only board hashes/word lists reconstruct from shipped assets and every page validates `BookPageSessionRules.puzzle`; six expected chapters, new 4th book, and old 63 exact QA page records pass `BookCatalog.requireCompatibleUpgrade`.
2. An existing old-book progress bookmark and a partially started old-book session reopen correctly under the expanded catalog without losing the original bytes.
3. New WS57 **one-time forward copy** reads the old WS11 AtomicFile QA progress/session journals, writes **separate WS57 files** `gridmates_wordsearch_ws57_qa_progress_v1.dat` and `gridmates_wordsearch_ws57_qa_sessions_v1.dat`, then correctly resumes a newly bookmarked and started preview page after a runtime restart. Byte-for-byte checks confirmed original WS11 journal files were **NEVER modified**. Unique disposable test filenames prevent touching user data.
4. Package verified by inspecting the actual tested APK ZIP: `assets/ws57_woodland_manifest.tsv` present with **312 authored rows**; `assets/ws57_woodland_expected_boards.tsv` present with **18 64-character hashes**. 264 distinct research answers, including 122 still-unapproved candidate terms; no bonus dictionary changes.

**Limits:** Dedicated emulator exercises actual catalog/page loading + save compatibility, but not every touchscreen UI interaction or physical device visual/ergonomic behavior. Owner must still install on Android and test opening Library → *Woodland - Editorial QA Preview*, picking chapters, finding words, using hints, completing a page, saving, force stopping and resuming, and assessing 12/15/20-wide phone readability. **Not approved for public release or editorial clearance.** Preview book scores never grant permanent Solo collection rewards.

## Delivered source changes
- `app/src/debug/assets/ws57_woodland_manifest.tsv`: 312 exact, source-compared answer rows, 18 pages, six chapters.
- `app/src/debug/assets/ws57_woodland_expected_boards.tsv`: 18 SHA-256 pins from previously verified WS51/52/54/55/56 V5 manuscript board reports.
- `app/src/debug/java/.../Ws57WoodlandPreview.kt`: recreates each exact original frozen board/pack/page recipe, rejects any SHA mismatches, exposes separate QA-only volume 2.
- `BookQaPilot.catalog()`: 63 previous QA pages preserved. New optional `catalog(context)` appends preview to standard debug catalog with compatibility guard. Release build still has no QA books, and all assets/class source debug-only.
- `QaBookshelfRuntime`: uses the appended debug catalog, shares unchanged old QA AtomicFile progress/session filenames, **filters preview book out of permanent Solo reward syncing**.
- `Ws57PlayableWoodlandPreviewAndroidTest`: emulator/physical Android tests check 18 actual V5 pages, original 63 pages equivalence, source-hash-digest identity and old-save progress/session reopening plus separate new preview progress after app/store restart.
- `test_ws57_playable_woodland_preview.py`: 312 rows/order/IDs exactly copied from original manuscript sources, 122 new proposals remain unapproved, debug/release and fixed-hash safeguards.
- CI: Android/core full regression plus branch-specific emulator run and report; independent art/release branch source guard, full release/no-asset QA. Actual CI receipts pending.

## Publication and device gates
The proposed 122 new candidate terms require editorial approval and display/meaning review. The six chapters are **now packaged in an independently verified signed QA/debug APK**, not publicly published. Physical phone touch, navigation, visual contrast and viewports still need user acceptance.

[Full QA edition design](../WOODLAND_PLAYABLE_PREVIEW_WS57.md) · [Work coordination](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
