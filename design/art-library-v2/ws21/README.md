# WS21 — Expanded GridMates collectible/seasonal/cover art bank

**2026-11-15 · Candidate-only art source (NOT a new Android build).**

This third reusable-art batch adds **136 actual transparent PNG candidates** and **120 corresponding independently editable SVG sources**: 16 painterly seasonal/reward illustrations; 72 blank, motif-driven Word Search book covers for 24 themes × 3 variants (standard, collector, pocket); 24 round theme medallions; and 24 GridMates Core UI panel/button/tab/profile/progress templates across forest, cream and gold treatments.

## What is *in GitHub*

- [manifest.json](manifest.json) — every one of 136 candidate image IDs, family, pixel dimensions, per-PNG SHA256 and source SVG SHA256 where applicable; no invented approved/shipped status.
- [build_gridmates_ws21.py](build_gridmates_ws21.py) — deterministic, portable SVG and bitmap build; uses Pillow, NumPy, OpenCV, CairoSVG. Its image assets must be available from the external source archive; it cannot generate missing painterly illustrations from nothing.
- [svg/covers/](svg/covers/) — **72** editable SVG covers, blank native-title zones, two gold/paper label panels per cover, themed medallion; no static title text.
- [svg/medallions/](svg/medallions/) — **24** reusable motif medallions.
- [svg/core_templates/](svg/core_templates/) — **24** scalable GridMates Core UI pieces.
- [validate_ws21.py](validate_ws21.py) — checks 120 actual GitHub SVG files, source hashes, 136 catalog records, 24 themes × 3 cover sizes, and the 8 × 3 Core states. **Does not validate the external PNG ZIP**.

## What is backed up outside GitHub

The **full verified archive** `GridMates_Asset_Expansion_WS21_FINAL.zip` is in ChatGPT Library at `/Game Design/GridMates Word Search/`. Its SHA-256 is `4e97d62b26f91b091f4bc08db8f3d37a79578dfe45140b74ba876ebff40f45c8`, size **17,519,880 bytes**. It contains all **136 transparent PNGs**, all 120 individual SVG files, the source illustration RGB atlas, two generated **reference-only** boards, four contact sheets, complete manifest and the builder. It passed ZIP CRC, all 136 PNG SHA checks and 120 SVG SHA checks. A 2.1MB Lite ZIP provides 136 smaller WebP previews and contact sheets; it is **not** the source-of-truth archive.

**Important honesty:** GitHub here contains SVG source files and metadata, but **not the 136 new PNG bitmaps or the original source/overview raster sheets**. Before actual Android integration, retrieve the verified full archive and inspect optimized outputs. Do not claim that a GitHub CI green check validates raster pixels that GitHub does not hold.

## Source illustration caveats

The 16 painterly reward graphics were segmented from a clean near-white RGB/checkerboard atlas. We applied color-aware transparent-background cleanup and restored four decorative shapes that crossed atlas-row boundaries. The extraction has deterministic crop rectangles in `manifest.json`; small outline/halo quality at 48–100 px remains a visual-approval gate. The two larger concept boards contain baked text and are **not runtime sprites**.

## Where to use them later

- **Collection / Profile:** laurel avatar rings, ribbon blanks, key/gift/trophy/medal and native status overlays.
- **Library / Home:** blank title-zone covers, theme icons matched to book categories and existing shelf/spine geometry; no hardcoded book IDs.
- **Daily / Nearby / Records / future Sudoku:** restrained Core tabs, progress tracks, buttons and frames, reusing one semantic component.
- **Seasonal:** Autumn, Winter, Spring and Summer illustrations as future category/reward accents (without changing puzzle logic).

**Boundary:** No Android code, APK, profile records, multiplayer protocol, save schema or existing title asset IDs were changed. Last fully verified signed gameplay QA remains v86, PR #131 is a draft, and none of the WS21 visuals have passed physical device approval.
