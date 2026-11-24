# WS-16D — Collection Home shortcut and future themed unlock gallery

**Date:** November 9, 2026. **Status:** Home shortcut implemented for QA; larger Collection progression redesign proposed, NOT implemented. **PR:** #131 stays draft.

## Owner motivation and direction
After reviewing the actual WS-16C Home and Profile screenshots, the owner identified Collection as a major reason people keep playing—unlocks, artwork, theme exploration and personal expression. It should not be buried inside Profile. Proposed home arrangement places a dedicated **Collection** action alongside **Full shelf** directly beneath the illustrated book carousel, without reintroducing the bottom navigation, shrinking the covers or squeezing a third button into the top-right header.

## First implementation (WS-16D UI only)
- Two equal, small original illustrated action surfaces, Full shelf + Collection. On narrow viewports or 150%+ fonts they stack, with 56dp minimum action height.
- Home Collection opens the existing working CollectionScreen and returns Home with the current WS-16C Back behavior. Profile still links to Collection too. Existing Full Shelf route and test tag are preserved.
- A framed gallery picture is an original SVG master under design/visual-system/masters/ws16d-collection-gallery-shortcut.svg, with a matching lightweight native Canvas icon. Native text is not baked into image files.
- Collection stays available even if no editorially approved production books are installed. No placeholder reward system or fake unlocks.
- Current source touchpoints: Ws16DLibraryShortcuts.kt, Ws16HomeScreen.kt, GridMatesShell.kt and Ws16CarouselHomeAndroidTest.kt.

## Existing progress architecture verified in GitHub
- core CollectionProgress: WoodlandCollection has 12 animal portraits with stable IDs. Unlocks currently depend on Solo + Daily completion counts, from free starters up to 30 completions. WordSearchAchievements also derive from current player records.
- BoardFontCollection: font unlocks from general achievement IDs.
- CollectionScreen: Portraits, Fonts, Achievements and local player customization already work; not yet a per-book thematic gallery.
- BookCatalog and BookProgress: unique stable book/chapter/page IDs and completed-page records are separate from Solo/Daily. Production BookCatalog is currently empty. Woodland, Space and Garden illustrated QA books are test books, not an approved source of production rewards.

## Book albums and activity collections

The owner confirmed this direction on November 24. Each playable book gets a matching collection album. Its items are earned by completing milestones within that book. The Library's book details can show album progress and open the same album available in Collection.

| Collection | How players earn its items | Examples |
| --- | --- | --- |
| Book album | Complete distinct pages, chapters and the required pages of the linked book | Garden rewards earned by solving Garden pages and chapters. Woodland and Space get their own albums as they are built out. |
| Activity collection | Complete a specific challenge anywhere it applies, using Word Search's own game records | Finish a set of Daily puzzles, solve Quick Play at several board sizes, or complete selected puzzles without hints. |

Show what each item requires and give players a clear next goal. The Garden pilot can demonstrate early, chapter and book-completion rewards. Its portrait shortlist is subject to the latest Sudoku art review and a visual comparison at Word Search's avatar and card sizes. Choose the final reward milestones and portrait assignments during the pilot.

Bind the album to a stable book ID and each collectible to a stable item ID. Use existing verified book progress and the qualifying pages defined by the book's edition. Count and grant a milestone once and credit earlier eligible completions when loading saves. Use the original IDs and earning rules for the existing general Woodland portraits and board fonts. Prototype with QA book data and use approved book progress for production rewards. Use native labels for the requirements, progress and equip controls.

## Phasing
1. WS-16D: Collection Home access and responsive art, tested on Android.
2. Later: gallery information architecture and original album previews, owner screenshot review.
3. Later: tested book-specific unlock rules, legacy compatibility, local persistence and backup migration.
4. Later: meaningful earned notifications and a modest real New unlocks indicator on Home when the underlying event/history tracking exists.

No new reward types, money, gacha, unlocking resets or release art promotion in WS-16D. PR remains draft pending user phone test.
