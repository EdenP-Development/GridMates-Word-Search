**WS22 fully backed up in GitHub:** [16 original theme motif PNGs, 24 prestige-frame SVGs, seven lossless source shards and reconstruction instructions](../art-library-v2/ws22/README.md); [real binary source QA passed](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43795880694). Earlier WS17–WS21 original image archives remain in ChatGPT Library for future GitHub binary backfill.

**Latest candidate artwork — WS21:** [136 candidate illustrations/covers/medallions/Core templates and 120 editable GitHub SVGs](../art-library-v2/ws21/README.md), source-verified by [GitHub QA #43793231439](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43793231439); original PNGs archived separately, not packaged in the app.\n\n# GridMates Master Asset System (WS18, 2026-11-15)

This is the working cross-game asset architecture, established after the owner clarified that most early Word Search visuals were exploratory **placeholders** and visual overhaul is permitted. Favor a coherent future GridMates family over protecting old one-off artwork. The reserve should be expansive; **the shipped asset payload stays curated**.

- [**A — Ordered production checklist**](PRODUCTION_CHECKLIST.md): source intake, core system, Word Search library, replacing placeholders, theme recipes, Sudoku adoption, future reserve, device QA, approval.
- [**B — Complete 2,142-slot master inventory**](MASTER_ASSET_INVENTORY.json): every planned/candidate/reserved master ID with scope, family, priority, format, status and source link where known.
- [**B — Human-readable inventory guide**](INVENTORY_GUIDE.md): family totals, first 13 batches, 29 theme names, representative master IDs and CSV commands.
- [**Inventory validator/exporter**](inventory_validator.py): checks the entire catalog, its 60 original WS17 sprite source links, states/IDs/theme coverage, and exports filtered or full CSV worklists.
- [**Prior WS17 art library**](../art-library-v2/README.md): 60 actual prepared candidate PNGs archived in ChatGPT Library; 4 editable scalable SVG prototypes present in GitHub, with style recipe and provenance.

## Current evidence

[WS18 inventory QA #43772016259](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43772016259) **SUCCESS**; canonical CSV bundle [artifact #14184641876](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43772016259/artifacts/14184641876) contains the complete 2,142-row inventory, Core P0 shortlist, first-five-themes P1, and the WS17 60 candidates.

There are **2,142 distinct master inventory slots**, not 2,142 new PNGs. Counts: **653 core, 275 Word Search, 854 theme, 171 game adapters, 189 reserve**. Source status: **1,239 planned, 843 reserve, 60 generated candidates** (the original images are in the external WS17 archive and not imported into GitHub as binary files). No art is automatically inserted into APKs by this index.

## Cross-game guardrails

The GridMates **Core** is generic parchment/forest/gold interfaces, status indicators, icons and accessible behavior. Word Search gets the library room/books/shelves. Sudoku gets its own grid, numbers, notes, player colors. Future games can reuse the Core without inheriting irrelevant Word Search-specific assets.

No Android code, save schema, artwork resources, gameplay state, release signer or main branch changed in WS18. The last verified Android **v86 QA** still provides the rollback baseline; [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) remains DRAFT. These are planning and production-source assets, not a new installed build.

## Ready to do next

Implement *the smallest useful slice of the larger warehouse*: scalable Core panel and button masters, common semantic icons, and a Collection/Library first-pass art replacement preview. Use the owner's actual Home/Library/Collection/Profile/Puzzle screenshots as layout references, keep accessible puzzle interactions native and review any full-screen overhaul before packaging.
