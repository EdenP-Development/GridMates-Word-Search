# November 23, 2026 · WS70 Home scrolling and library shelf order

**Status** · Android emulator checks passed for signed v113. Handset review is open.

## Starting point

The owner supplied v112 phone screenshots showing the Home book heading partly clipped during vertical scrolling and Garden on the second shelf while blanks occupied the first. The owner asked for Woodland, Space, and Garden together in catalog order and for a review of the visual style. The assistant implemented this pass in the Android project from draft [#190](https://github.com/PlayerEden/GridMates-Word-Search/pull/190) at `d4bcec21105338eaa469956c105089fcd08c508e`. [WS69 receipt](2026-11-23_WS69_LIBRARY_POLISH.md) · [Draft #191](https://github.com/PlayerEden/GridMates-Word-Search/pull/191)

## Implementation

- Home scrolls the Word Search title, ornament, book carousel, and mode controls together beneath the Profile and Menu toolbar. A thin scroll indicator and bottom spacing help players reach Custom and My Puzzles. The book carousel responds to horizontal swipes.
- The playable cabinet fills each shelf in catalog order. Each unlettered leather spine marks one future book position and has no tap action. New books take the next position, then continue to the following shelf. The archived cabinet retains its comparison layout.
- The debug build moves to version 113 using the existing QA package and signer. This pass reuses the existing art, catalog, frozen boards, and save identifiers.

## Art review

The book illustrations, leather spines, warm paper, forest-green type, and gold trim establish a recognizable paperback identity. The illustrated cabinet suits browsing and the simpler puzzle page suits sustained play. The flat Collection and Profile portraits feel least related to the richly illustrated covers. The painted robin and flat outlined animal portraits in the same Collection screen make that difference especially visible. A companion-art pass can retain recognizable silhouettes, names, and unlock identities while bringing texture and outlines closer to the book art.

Primary actions vary between the flat green fills in book detail and the gold-edged gradient in setup. Chapters has ornate framing on the dialog and every row, so numbers and page counts sit close to decorative corners. Review the primary controls and the clipped bottom edges in book detail, Collection, and the puzzle word list.

## Build and verification

- Tested source [`510ba7e36f63a5358ca9c0b96ff88bd9d8a3a768`](https://github.com/PlayerEden/GridMates-Word-Search/commit/510ba7e36f63a5358ca9c0b96ff88bd9d8a3a768). [Android QA passed](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45317919000) and [release isolation passed](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45317918805). Local contract checks passed 54 tests. Android QA ran core and app tests, lint, compilation, sign-off checks, and the emulator suite.
- At 320dp and 393dp, Android verified that horizontal swipes page the covers, vertical swipes move the title and book heading together, both toolbar actions retain their positions, and My Puzzles remains reachable. The three playable spines sit on the top shelf. A fixture proved the fourth and fifth books replace the next slots without shifting the first three. [Android captures and test reports](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45317919000/artifacts/14886502989)
- Signed install-over from v112 preserved both book journals byte for byte. Both emulator layouts reopened the saved Solar System page with one of twelve words found. All 237 APK assets matched v112 byte for byte, including art and frozen puzzles. [Android captures and test reports](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45317919000/artifacts/14886502989)
- APK package `com.playereden.gridmates.wordsearch.qa`, version code `113`, version name `0.9.68-ws70-home-library-qa`, 34,254,647 bytes. SHA-256 `fa870141851fce3c119b976c9c96c7364b8fb1dec978870a3e51f5da78962f1d`. Certificate SHA-256 `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419`. [Signed APK ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45317919000/artifacts/14886522521)
- Early CI checks flagged source assertions for the archived cabinet. The final source preserves those contracts. The first emulator run passed the install-over and book-growth fixture, and failed its first horizontal swipe assertion. The revised test swipes inside the carousel and checks Home and Full Library independently. [Earlier emulator run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45316855321)

## Follow-through

The signed v112 build is the fallback. Review the v113 Home gestures and shelf order on the owner's handset. The next visual pass can align the Collection and Profile illustrations, make primary buttons consistent across screens, and soften crowded chapter rows. [Signed v112 fallback](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45294112307/artifacts/14876041667) · [Latest handoff](../WS14_ACTIVE_HANDOFF_2026-11-09.md)
