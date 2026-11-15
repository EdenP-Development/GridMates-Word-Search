# WS18 — GridMates master asset system, A production plan + B abundant inventory

**Date:** 2026-11-15. **Status:** A and B implemented in GitHub as versioned *planning and source* artifacts. No Android runtime replacement, new image generation in this pass, APK bump, game mechanics change, or source PNG upload.

## Owner's decisive direction

Prior screens are experimental proofs of possibility. Most existing visual assets are disposable/replaceable placeholders; owner is comfortable with a broad overhaul to achieve shared cohesion. Reuse the **same GridMates Core** in Word Search and Sudoku, add **Word Search-specific bookcase and book assets**, and use **theme adapters** for categories. Owner strongly prefers **an expansive stockpile of more assets than immediately needed**, including likely-to-be-unused future ideas, so future games and volumes can draw from the reserve. Reuse, consistency and discoverability are critical; do not package every speculative file inside the APK.

## What was committed

- [Master Asset System hub](../../design/master-asset-system/README.md)
- [A: `PRODUCTION_CHECKLIST.md`](../../design/master-asset-system/PRODUCTION_CHECKLIST.md): 7 phased milestones M0–M6, concrete unchecked work, evidence gates, cross-game sharing/release boundaries, artwork state transitions, folder architecture, versioning and source provenance.
- [B: `MASTER_ASSET_INVENTORY.json`](../../design/master-asset-system/MASTER_ASSET_INVENTORY.json): **2,142 unique named master slots**. Counts:
  - GridMates **Core 653**: panels/buttons/tabs/chips, universally usable semantic icons, badging, status/progress, typography/layout primitives, backgrounds/textures and low-motion feedback.
  - Word Search **275**: covers, book spines (including material/width recipes and 12 prior candidate crops), library/shelves, Home, Collection, native gameplay utility and result art.
  - Interchangeable **Theme 854**: **29 themes** with 10 motifs +19 shared-template slots each, plus 12 prior WS17 theme badge candidates.
  - Game adapters **171**: Sudoku, Crossword and optional future puzzle formats. These future titles are *inventory thought starters*, not announcements.
  - Reserve **189**: optional premium/seasonal/unused visuals, future panels, icons, books and awards.
  - Statuses: **1,239 `planned`**, **843 `reserve`**, **60 `candidate`**. **0 `approved` / `integrated`** in this *new* inventory; this does **not** contradict already-shipped assets tracked separately.
  - Priorities: P0 **440**, P1 **488**, P2 **344**, P3 **27**, P4 **843**. Names/status and family are explicit in every row. Some entries are **native UI state/layout recipes**, not standalone PNG requests.
- [B: `INVENTORY_GUIDE.md`](../../design/master-asset-system/INVENTORY_GUIDE.md): 29 theme names, exact-ID examples, scope and status definitions, suggested first 13 production batches, scripted filtering/CSV export examples, and strict warnings against treating planned inventory as generated files.
- `inventory_validator.py`: standard-library validation for 2,142 unique/sorted legal IDs, theme pack completeness, scope/prefix/priority/status, exact SHA-backed link between WS18's 60 prepared candidate entries and the prior WS17 `RESERVE_INDEX.json`. On-demand CSV export supports any scope/priority/status filter.
- [`ws18-master-asset-inventory-qa.yml`](../../.github/workflows/ws18-master-asset-inventory-qa.yml): source-only metadata CI + full/selected CSV artifacts.
- Previously written `design/art-library-v2/STYLE_BIBLE.md` and `README.md` now explicitly acknowledge the **owner-authorized holistic overhaul**, while keeping former paperback-v1 prototype limits as historical rather than forbidding ornamentation. Every individual asset still requires actual placement review.

## Direct verification and deliverables

[**WS18 master inventory QA #43772016259**](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43772016259) **SUCCESS**. [**CSV artifact #14184641876**](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43772016259/artifacts/14184641876) contains full 2,142-row inventory and targeted Core P0, initial themes P1 and WS17 prepared candidate subsets. Those exports are useful to assign actual work to future chats.

The previous WS17 [original source image archive](../../design/art-library-v2/README.md) remains a different asset: **60 actual isolated alpha PNG candidates** from four owner-liked sheets + one book spine expansion, with 7 source/reference sheets. Full exact source ZIP SHA256 `68fac0f6a40c7abbb3b75096692e74f016368218ffb2e8085e60a12f7636966e`, preserved in ChatGPT Library `/Game Design/GridMates Word Search/GridMates_Visual_Library_v1.zip`. **Those binary PNGs have NOT been uploaded to the GitHub repo** via the GitHub connector; only metadata, four editable SVG prototypes and import script reside there. The WS18 master catalog references all 60 via unique `ws17:` identifiers rather than pretending they are new PNG creations. New shared Core illustrations in the 2,142-slot inventory remain **planned**, not generated.

## Game layout context from owner's screenshots

- **Home:** cozy parchment background, shelf/book cover carousel, gold-outlined action cards, modes, Continue Solo and page indicators. Needs shared cards/buttons/icons without more scroll density.
- **Library:** ornate wooden bookcase with two shelves, 3-page navigation, carved trim/ivy, themed and filler book spines. Library may remain visually immersive as a unique Word Search location.
- **Collection:** pagination, portraits, fonts, achievements, equipped/locked states; strong target for Core tabs and theme medallion overlays.
- **Profile:** generic parchment card and sections; ideal cross-game Core reuse.
- **Puzzle:** must stay clean, high-contrast, fingertip-usable; minimal external decoration; preserve letter grid/zoom/16-word scrolling.
- **Sudoku:** different gameplay grid/notes/number fonts, so Core reusable cards/avatars yes, bookcase no.

## Next executable production steps (in order)

1. Collect an asset-by-asset **existing artwork audit** by actual Android source path and screenshots; keep/rework/retire mapping. Replace placeholders deliberately rather than refactoring gameplay.
2. Prototype **Core v1 scalable panel + green/gold action + tabs/icon semantic set** and render side-by-side Word Search / Sudoku states including 200% text.
3. Reuse the archived 60 source candidates and existing four editable SVGs when relevant; import selected binary PNG originals into GitHub using normal git/LFS and the strict existing hash checker, not transient URLs.
4. Replace the **Collection and Profile** visual components first, then filler **Library spines/shelf decorations**; refine Home next and touch-sensitive gameplay last.
5. Build out future art in large coherent *families*, checking source/alpha/dedupe at each batch; keep unused illustrations in reserve instead of Android package.
6. On actual app changes: Android build/regression and independent art check, hash, correct QA signing/install-over, screenshot QA, physical owner approval before claiming release.

**Source-of-truth boundaries:** This is a Word Search draft PR's staging home for the multi-game asset plan; Sudoku's separate `PlayerEden/SudokuTogether` main is **not modified** and no independent shared-asset repo has been silently created. [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) remains **DRAFT**. Prior **v86** signed QA/6-of-6 API35 emulator remains known-good gameplay baseline; no APK change or new Android acceptance occurred in this pass.
