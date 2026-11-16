# WS20 — Extra GridMates assets (source-bank candidates only)

**Created:** 2026-11-15. **Source and QA status:** 88 transparent PNG candidates verified in the separately backed-up ZIP; **72 editable themed book-spine SVG masters committed to GitHub**. 16 Collection/Reward sprite PNGs and the original painterly reward sheet remain inside the external backup, not the Android project.

## What is actually available

- `manifest.json`: 88 unique asset IDs, source file paths, image dimensions, SHA-256 hashes, crop rectangles (for 16 badges), and 24-theme/three-width spine metadata.
- `build_gridmates_ws20.py`: portable rebuilding tool. It needs the **unpacked WS20 source archive** (source transparent rewards atlas, 2 reference boards, optional font/Pillow/numpy/cairosvg dependencies); it does not pull missing source bytes from GitHub.
- `svg/spines/`: **72 source-of-truth vector books** (24 themes × narrow / regular / collector), GitHub-committed and independently editable.
- `validate_ws20.py`: local/CI verification of all 72 committed source SVGs and manifest source hashes; full PNG/ZIP validation happens separately using the archive.
- `GridMates_Asset_Expansion_WS20_verified.zip`: full external 16.5 MB pack. SHA-256 **`6c508feeb8fcc00b067cdee55f719718664af9d2fe27391ff2cc47297f623498`**. Saved in ChatGPT Library under `/Game Design/GridMates Word Search/`. It also contains the 88 transparent candidate PNGs, source rewards sheet, 2 reference-only painted boards, source generator, preview contact sheets and all the source SVGs.

### Style and production boundaries

The assets follow the GridMates parchment/forest-green/gold storybook direction and complement [Core asset system](../../master-asset-system/README.md). They are **candidate artwork, not automatic in-game replacements**; saved user profiles, book identity, unlock logic and board touch targets are unchanged.

The reward source sheet is a collection of illustrated designs on a cleaned transparent atlas. Its 16 individual art pieces may require later cleanup at actual 48–120 px UI scale; SVG-spines are better for precise resizing. The two broad layout design boards contain raster text and are for **reference only**, never UI bitmaps.

Text like theme names, book titles, reward statuses and button labels stays native to Android so it can be localized and scaled without degrading accessibility. `gm_ws_spine_woodland_regular.svg` is an editable pattern; it is not tied to an existing Woodland book ID or save file.

### Verification

Run `python3 design/art-library-v2/ws20/validate_ws20.py` after updating any SVG source or manifest. Source QA cannot prove that external PNGs were uploaded; they are intentionally **not** in the repo or APK. For the complete backed-up pack, confirm ZIP CRC, per-sprite SHA-256, alpha channels and source files, then compare contact sheets visually.

**Previous verified Android baseline:** v86 QA (unchanged by WS20 artwork work). [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) remains DRAFT. Promote and review assets in real Word Search/Sudoku screen contexts before including optimized bitmaps in an APK.
