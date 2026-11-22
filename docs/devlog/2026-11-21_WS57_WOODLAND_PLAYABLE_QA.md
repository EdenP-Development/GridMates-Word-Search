# WS57 — Real six-chapter Woodland playable QA preview

**Stage at authoring:** New source-pinned debug-only book exists on an isolated branch and compiles/executes through existing Library UI in source; actual Android CI and emulator acceptance **not yet independently verified**. Do not describe a successful APK, test execution or phone approval until those runs pass.

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
The proposed 122 new candidate terms require editorial approval and display/meaning review. The six chapters are not installed or publicly published yet, even though they are wired into QA source. Physical phone touch, navigation and viewports still need user acceptance.

[Full QA edition design](../WOODLAND_PLAYABLE_PREVIEW_WS57.md) · [Work coordination](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
