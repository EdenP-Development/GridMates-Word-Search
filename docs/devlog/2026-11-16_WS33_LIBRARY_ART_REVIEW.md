# WS33 Library art review

The owner asked whether the existing art family was sufficient and raised concerns about the Library bookcase. This pass compares the carved cabinet with an open wood shelf on warm paper. The comparison uses the same three bindings on both sides and puts a real My Puzzles screen beside them.

## Starting point

The review starts at draft [PR #142](https://github.com/PlayerEden/GridMates-Word-Search/pull/142), head `59fc485018ab18b217c9ea2eb66ee116f1e49b5f`. Its tested runtime is `bbbee51bacc8ad6242fd4291cbc70e1d7689bc97`. [WS32 receipt](2026-11-16_WS32_ILLUSTRATED_MY_PUZZLES.md).

The sculpted cabinet was an intentional immersive choice. Its near full-screen orange wood, illuminated crown, brass carving, and decorative second shelf compete with the real bindings on the first shelf. My Puzzles shows the existing warm parchment, Literata heading, Atkinson controls, and fine gold and emerald used elsewhere in GridMates.

## Art use and proposed layout

| Role | Existing source | Use in this pass |
| --- | --- | --- |
| Book identity | v94 `ws16bf/spines/{woodland,space,garden}.png` and `ws16ae/covers/woodland.png` | Keep the spine art and use the cover to identify the selected volume. Full titles and live progress belong in native text. |
| Shelf | v94 `ws16g/shelf/top.png` | Shallow wood ledge beneath the bindings. Show catalog titles first and page through further volumes as the catalog grows. |
| Shared surface and controls | `GridMatesParchmentBackground`, `GridMatesIllustratedFrames`, `GridMatesTheme`, `ws16h/actions/full-library.png` | Paper, fine brass, and emerald actions link Library to Home and My Puzzles. The review scene approximates the Compose surface. |
| Collection and rewards | [WS22 motif and frame inventory](../../design/art-library-v2/ws22/README.md) | Reserve illustrated motifs for moments such as a book opening or earned reward. WS22 already contains 16 theme motifs and 24 frames, enough for the next integration pass. |

Keep the existing cabinet available while assessing the draft. The proposed shelf shows only real browseable books with a separate cover and page count for the book in focus. More books can use the existing swipe and button pattern where several pages are needed.

## Evidence

Inspected the [v94 My Puzzles Android capture](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43989770069/artifacts/14288015670) and verified the [v94 QA APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43987637538/artifacts/14286536417) used to extract the candidate assets. APK SHA-256 is `0b8953e63a5c0cf38b127d76081c104e5c495ae443a0a827d6d0b3095870e539`. The carved cabinet panel is a reconstruction with the packaged cabinet layers, shelf, and books. The open shelf panel is a visual layout study using those bindings, the app fonts, and independent artwork.

Reviewed the rendered 1794 × 1473 comparison for readability, source art, and shelf contact. The PNG SHA-256 is `6d5adaebc7d660aeac0ece3a6d218d07794f974b3e53f27aca2408805eaaf472`. This branch contains development records only. Android verification and the two v93 upgrade results come from WS32.

## Follow-through

Use v94 as the verified Android baseline. Take Woodland through the open shelf, book details, play, pause, process restart, resume, and page completion with the actual book progress store. Verify the shelf at compact widths and with large type and verify both UI variants use the same catalog and save IDs. That pass can establish the first complete book experience and inform the portable book backup work.
