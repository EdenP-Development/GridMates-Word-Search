# November 17, 2026 — WS35 Library binding depth

The owner said the Library looked flat and the spines looked pasted onto the screen. This pass uses the [v97 build and handoff](2026-11-17_WS34_PHONE_SCREENSHOT_POLISH.md) in [draft PR #145](https://github.com/PlayerEden/GridMates-Word-Search/pull/145) as its starting point. The parent is `fc387ca49d607d2eba574f010e10bdfcdcc2f516`. The [signed v97 APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44120147813/artifacts/14345181818) is the verified comparison build.

## Artwork and layout

- Six new, independently cropped bindings use rounded leather and cloth with raised bands. Woodland, Space, and Garden have individual gold emblems. Three several-size cloth volumes serve as shelf decoration.
- The source atlas was generated for this game. The original RGBA PNG is kept byte for byte as numbered parts under [masters/ws35](../../design/visual-system/masters/ws35). The [manifest](../../design/visual-system/ws35-library-bindings.json) pins its hash, crop columns, and output pixel hashes.
- The cabinet's walnut finish is darker, the shelf recesses add depth, and each binding has a cast shadow where it meets the wood. The three real books are split two above and one below on the two-shelf view.
- Live Compose titles and touch cells open the original catalog entries. Decorative books occupy spare space without clicks. Pressing a book lifts its face, and the shelf pager retains access to all catalog entries.

## v98 build and verification

Android and artwork builds are pending. The six extracted sprites passed the local source and opacity checks. The existing portable book backup milestone follows the Library review.
