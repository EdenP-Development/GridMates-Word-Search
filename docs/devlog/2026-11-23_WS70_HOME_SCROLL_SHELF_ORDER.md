# November 23, 2026 · WS70 Home scrolling and library shelf order

**Status** · Implementation ready for Android QA. The signed v112 build remains the fallback.

## Starting point

The owner supplied v112 phone screenshots showing the Home book heading partly clipped during vertical scrolling and Garden on the second shelf while blank spines occupied the first. The owner asked for Woodland, Space, and Garden together in catalog order and for a review of the visual style. The assistant implemented this pass in the Android project from draft [#190](https://github.com/PlayerEden/GridMates-Word-Search/pull/190) at `d4bcec21105338eaa469956c105089fcd08c508e`.

The previous pass verified the signed v111-to-v112 upgrade, bookshelf geometry, and the saved Solar System page. [WS69 receipt](2026-11-23_WS69_LIBRARY_POLISH.md) · [Signed v112 fallback](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45294112307/artifacts/14876041667)

## Implementation

- Home scrolls the Word Search title, ornament, book carousel, and mode controls together. Profile and Menu stay in the toolbar. A green scroll indicator follows the vertical viewport, with room below Custom and My Puzzles at the end. The book carousel keeps its horizontal swipe.
- The playable cabinet fills each shelf in catalog order. Each unlettered leather spine marks one future book position and has no tap action. New books take the next position, then continue to the following shelf. The alternate archived cabinet keeps its comparison layout.
- The debug build moves to version 113 using the existing QA package and signer. This display pass reuses the existing art, catalog, frozen boards, and save identifiers.

## Art review

The book illustrations, leather spines, warm paper, forest-green type, and gold trim establish a recognizable paperback identity. The illustrated cabinet suits browsing and the simpler puzzle page suits sustained play. The flat Collection and Profile portraits feel least related to the richly illustrated covers. The painted robin and flat outlined animal portraits in the same Collection screen make that difference especially visible. A companion-art pass can retain recognizable silhouettes, names, and unlock identities while bringing texture and outlines closer to the book art.

Primary actions also vary between flat green fills in the book detail and a gold-edged gradient in setup. Chapters has ornate framing on both the dialog and every row, so numbers and page counts sit close to decorative corners. Review control treatments and clipped bottom edges on book detail, Collection, and the puzzle word list together.

## Verification and follow-through

The signed v113 build, native 320dp and 393dp gestures, save upgrade, and placeholder replacement are pending CI. Start from the signed v112 APK if the candidate is unavailable. Review real handset scrolling and order after emulator QA.
