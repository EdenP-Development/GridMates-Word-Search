# GridMates Master Asset System — B. Inventory guide

**Canonical full list:** [`MASTER_ASSET_INVENTORY.json`](MASTER_ASSET_INVENTORY.json)

This is a deliberately **oversized art-production and reuse inventory**, not a warehouse of 2,142 finished bitmaps or 2,142 features. It includes concepts, component/token slots, overlays, and assets designed for future games that may never be built. Its role is to prevent repeated art requests, inconsistent naming, and losing good alternatives.

## At a glance — 2,142 named slots

| Scope | Slots | Owner intent | Included in current Word Search APK? |
|---|---:|---|---|
| **GridMates Core** | **653** | Shared design components, glyphs, panels, interactions, profiles, status and motion | New slots **no** |
| **Word Search** | **275** | Book covers/spines, shelves, Collection, Home, puzzle-light utility | New slots **no** |
| **Theme packs** | **854** | 29 swappable thematic motif sets and base-template overlays | New slots **no** |
| **Game adapters** | **171** | Sudoku, Crossword and hypothetical future games | New slots **no** |
| **Reserve warehouse** | **189** | Experimental, premium, seasonal, future-game and unused concepts | New slots **no** |
| **Total** | **2,142** | Unique ID + priority + future-use metadata | **Planning only** |

**Production status:** 1,239 `planned`, 843 `reserve`, **60 `candidate` from the prior WS17 source pack**, zero approved/integrated *in this new inventory*. This says nothing about how many graphics the current app already uses: the current shipped artwork is tracked in `design/asset-registry.json` and APK audits.

**Priorities:** P0 foundation **440**, P1 first integration **488**, P2 secondary **344**, P3 optional polish **27**, P4 long-range reserve **843**. **Priority P0 does not mean paint 440 independent PNG files**: most represent icons, native controls, or shared state and layout templates.

## 29 theme families

Woodland, Ocean, Bugs, Garden, Space, Winter, Farm, Spooky, Desert, Night Sky, Autumn, Spring, Summer, Mountains, Dinosaurs, Food, Weather, Travel, Music, Fairy Tales, Forest Night, Tropical, Arctic, City, Underwater, Crafts, History, Animals, Science.

Each has **10 named central motifs and 19 generic visual template slots** (badges, book-cover accents, spines, category marks, ribbons, portrait rings, reward seals, etc.). Reuse the same global frame: illustration and 1–2 theme accent colors change, not the entire menu hierarchy. The 12 original candidate badges from the WS17 source atlas are registered in addition to the 29 templates; hence 854 theme-scope slots overall.

The first cross-theme production wave should be **Woodland/Ocean/Bugs/Garden/Space**; reserve the remaining concepts for future expansions. Game adapter slots such as Nonogram, Mahjong and Minesweeper are **ideas for reuse only**, not announcements or commitments to build those games.

## What an inventory slot means

Examples from the actual master JSON:

| Exact ID | Intent | Render strategy |
|---|---|---|
| `gm_core_panel_hero` | Shared app feature card | Native content + scalable nine-slice trim |
| `gm_core_button_primary` | Global green action | Native Compose states, no separate pressed PNG required |
| `gm_core_icon_navigation_home` | Universally recognizable Home glyph | Vector with native accessible description |
| `gm_core_progress_pagination_dots` | Shared page navigation | Native selected/inactive/accessible states |
| `gm_ws_spine_blank_forest_regular` | Book spine color/size recipe | Reusable spine geometry composited with theme emblem |
| `gm_ws_shelf_plank_ornate` | Bookcase wood | Reusable raster part, not full-screen art redraw |
| `gm_ws_collection_portrait_ring` | Collection framing | Reuse approved portrait bitmaps; no new animal identity |
| `gm_ws_puzzle_ui_words_remaining_filter` | Compact puzzle word filter | Native touch target, restrained ornament |
| `gm_theme_ocean_motif_seahorse` | Ocean artwork | Motif layered into badge/book/crest templates |
| `gm_theme_space_badge_round` | Space pack base | Shared badge frame + space motif |
| `gm_adapter_sudoku_grid_major` | Sudoku grid stroke | Game-native vector, not bookshelf styling |
| `gm_reserve_future_badge_rare_finder` | Optional future reward look | Stockpile without feature commitment |

### Naming and ownership

`gm_core_*` = reusable across GridMates games; `gm_ws_*` = Word Search-only; `gm_theme_<id>_*` = detachable visual theme; `gm_adapter_<game>_*` = gameplay-specific styling; `gm_reserve_*` = speculative/unused.

**Keep the individual icon's semantic meaning stable** even if its rendered artwork changes. For example, a locked portrait still requires native locked/unlocked logic; adding a new gold seal should not modify the save schema.

### States and composites

A row such as `gm_core_button_primary` can have `default, pressed, focused, selected, disabled, loading` states. They are implementation states, **not separate required raster files**. If states can be supplied via native rendering/tint/alpha/focus rings, do so. A theme `badge_round` plus a theme `motif` can generate many combinations from two source masters rather than drawing a wholly new badge each time.

### Candidate vs planned vs reserve

- `planned`: named production target, *nothing generated or verified by this row*.
- `candidate`: actual prepared visual exists externally, but not approved for shipping.
- `approved`: accepted source artwork and intended screen context verified.
- `integrated`: shipped/QA-verified in a specific named app/version, recorded with path/hash.
- `reserve`: potential future use, outside present shipping/feature plans.
- `deprecated`: previous/experimental art archived, not deleted.

The 60 `ws17:` source links in the inventory refer to the archived **60 prepared transparent PNG sprites**. Their origin/source atlas and reproducible crop data remain in `design/art-library-v2/RESERVE_INDEX.json`. Full PNG archive lives in ChatGPT Library `/Game Design/GridMates Word Search/GridMates_Visual_Library_v1.zip` (SHA256 `68fac0f6a40c7abbb3b75096692e74f016368218ffb2e8085e60a12f7636966e`). **Those PNG binaries have not been committed to GitHub.** Four editable SVG prototypes *are* committed in `design/art-library-v2/vector-prototypes/`.

## Browsing/creating worklists

The validator uses only Python's standard library, does not modify the app, and supports filtering + spreadsheet-friendly export:

```bash
# Validate ALL naming, scope, source link and count invariants
python3 design/master-asset-system/inventory_validator.py

# Show the core foundation (P0) in a readable shortlist
python3 design/master-asset-system/inventory_validator.py --scope core --priority P0 --list

# List all prepared candidates; there should be exactly 60
python3 design/master-asset-system/inventory_validator.py --status candidate --list

# Generate an editable CSV for planning/batch assignment without changing source JSON
python3 design/master-asset-system/inventory_validator.py --scope theme --priority P1 --csv /tmp/gridmates_theme_p1.csv
```

**The canonical inventory is JSON**. CSV is a derived planning view. The GitHub CI source check should guard duplicate IDs, expected counts, theme coverage, WS17 links, and scope/status claims. Human approval is still separate.

## Recommended production batches

| Pass | Batch | First useful family | Practical screen / consumer |
|---|---|---|---|
| **01** | Core tokens + scalable panels | `gm_core_panel_*`, `gm_core_layout_*` | Home, Profile, Collection, Sudoku shell |
| **02** | Core button/state system | `gm_core_button_*`, `gm_core_tab_*` | All menus, focus/disabled behavior |
| **03** | Universal icon set | `gm_core_icon_navigation_*`, `gm_core_icon_utility_*` | Same glyph meaning across games |
| **04** | Player/status/progress | `gm_core_avatar_frame_*`, `gm_core_status_*`, `gm_core_progress_*` | Both games' profiles/results |
| **05** | Word Search blank spines | `gm_ws_spine_*` + WS17 extra art | Library 2–3 shelves and filler books |
| **06** | Library room pieces | `gm_ws_shelf_*`, `gm_ws_library_prop_*` | Carved-wood library |
| **07** | Collection status visuals | `gm_ws_collection_*`, Core badges | Portraits, fonts, achievements |
| **08** | Home + Daily cohesion | `gm_ws_home_*`, shared Core cards | Landing, resume, Daily, Nearby |
| **09** | Gameplay utility polish | `gm_ws_puzzle_ui_*` | Outside the letter grid only |
| **10** | Original five themes | Woodland/Ocean/Bugs/Garden/Space | New books and themed medallions |
| **11** | First seasonal reserve wave | Winter/Farm/Spooky/Desert/Night Sky | Later content releases |
| **12** | Sudoku Core adoption demo | `gm_adapter_sudoku_*` | Test same Core in second game |
| **13+** | Expand reserve by coherent batch | Remaining 19 theme packs + 189 reserve items | Later games/releases, never automatic APK payload |

**Production rule:** generate as much source art as useful, but ship only a compact curated subset. Original high-res + masks/source files may stay in the archive; Android packages contain optimized, visually reviewed versions.

## Safe first implementation scope

The user is comfortable replacing placeholder/experimental visuals, so we can confidently design the overhaul. However, **do not replace the puzzle grid, native text, data model, portrait identity IDs, saved progress or multiplayer protocol just to match new ornamental art**. First create a side-by-side Word Search/Sudoku Core sample; then apply new UI to Collection/Profile and Library, with Home following. The core remains warm and restrained, the Library remains a distinctive Word Search world.

See [A. Production checklist](PRODUCTION_CHECKLIST.md) for ordered milestones and exit gates, and `design/art-library-v2/STYLE_BIBLE.md` for the current reference style/palette.
