# 2026-11-17 — Home controls, book chapters, and puzzle feedback

**Status** · v104 build pending. v103 remains the verified APK.

## Who, what, where, and why

The owner shared ten handset screenshots after installing v103. The screens show the 20×20 Quick Play board, 21-page volumes, the book page hint history, profile, collection, and matching covers and spines. The Home controls and upper foliage compete for space when the page scrolls. The initial book hint gives the length of an already-listed word, and the notices dialog uses a different surface from the book and shelf.

Codex is making a focused UI pass in `feature/ws42-handset-polish-2026-11-17` and will check the update through the normal app entry points.

## Starting point

[WS41 receipt](2026-11-17_WS41_MIXED_PLAY_AND_BOOK_CONTENT.md) · [draft PR #152](https://github.com/PlayerEden/GridMates-Word-Search/pull/152). Starting GitHub head `a1f7237b1da893df01b5c5a666357640fea1b7ae`, with v103 tested source `01eb02e4e5460feaab131236e4da7b9d8eb5c2e1`. Standard and independent builds passed, with 24 Android tests and a full mixed Quick Play run. [Signed v103 baseline](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44236302499/artifacts/14397980688), SHA-256 `19949e41b1ed9165781c7a39b89857c9996bd5e27cfe25227dc5afabcfe25359`. The photos establish which screens the owner reached. Full handset playthrough and physical two-phone testing remain open.

## Changes

- The Home header stays visible while the modes scroll. Shorter upper sprigs reclaim clear paper around the row with Profile and Menu.
- The book detail header keeps Back and Chapters accessible. The picker shows each chapter’s page range and completed count, and selecting it scrolls the existing list to that chapter.
- Game notices uses the shared paper frame with a scrollable history and a fixed Close action. The first hint names the word and the row where it starts. The second adds its starting column, followed by an on-grid guide at the third step.
- Large Solo and Quick Play boards use a shorter first-play tip. The full gestures are in Options → How to select words. Word-found feedback clears after it has been shown.

## Build and QA

Build pending. The v104 check installs over a signed v103 containing an actual 20×20 save, checks save bytes and the frozen board, and exercises Home scrolling, Quick Play help and the Garden chapter jump and notices. Existing device tests plus 200% text scenarios cover the chapter picker and notice history. The previous signed v103 is the fallback.

## Next task

Review the v104 Android evidence and send the verified build for handset review. Continue the 100–250-word Fall authoring batch once this pass is verified.
