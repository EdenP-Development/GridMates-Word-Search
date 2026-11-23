> **WS18 next direction:** The owner clarified that current visuals are mostly placeholders and a coherent replacement is desired. This candidate pack now feeds the larger [GridMates Master Asset System](../master-asset-system/README.md) (Core / Word Search / Themes / Reserve), not only a collection of little optional embellishments. The high-resolution PNGs remain externally archived, so placement and binary import are separate tasks.

# GridMates: Word Search — Art Library v2 reserve

**Candidate only; no runtime asset substitutions.**

This directory is the canonical GitHub **source of truth for the future ornamental design system**. The original user-approved transparent art sheets and separated lossless PNGs are preserved in the accompanying ZIP archive in the user's ChatGPT Library (Game Design / GridMates Word Search), not yet in GitHub's binary tree. The two artwork concept boards with printed titles/text must not be shipped as game sprites.

Read `STYLE_BIBLE.md` before making additional books, UI pieces, collections or themes. `RESERVE_INDEX.json` has SHA-256 provenance for seven source sheets and names for all 60 prepared sprites. `vector-prototypes/` contains four **real, editable SVG assets committed in this repository** (parchment card, blank book spine, medallion base, ornate divider) to support future scalable variants. These SVGs are *new reconstructions*, not pixel-perfect copies of the PNG sheets.

## First review package

- Master: `GridMates_Word_Search_Visual_Library_v1.zip` — 7 source/reference sheets, 60 separated transparent PNG sprites, SHA-256 manifest, source-crop replay tool and contact-sheet preview
- Persistent backup: ChatGPT Library `/Game Design/GridMates Word Search/GridMates_Visual_Library_v1.zip`
- Expected master ZIP SHA-256: `68fac0f6a40c7abbb3b75096692e74f016368218ffb2e8085e60a12f7636966e`
- Lightweight on-chat preview: `GridMates_Word_Search_Visual_Library_Lite_v1.zip` (~2 MB, 60 WebPs)
- `python3 design/art-library-v2/import_visual_library.py path/to/GridMates_Word_Search_Visual_Library_v1.zip` verifies the ZIP, source sheets and every sprite hash, then stages assets under `design/art-library-v2/staged/` — **never writes to `app/`**. Add only reviewed art to the Android source pipeline.

## Where images might go later

| Screen | Priority candidate(s) | Before enabling |
|---|---|---|
| Home/Play | Warm panel, primary forest button, native title/icons | Verify card spacing and first-screen clarity |
| Library | Blank spines, shelf trim, bookmarks, book plaque | Preserve spine widths, 2–3 shelf pagination, blank filler books |
| Collection | Woodland/Ocean/Bug medallions, round seal, ribbon | Preserve locked/unlocked rules and all portrait IDs |
| Daily | Blank parchment panel and calendar icon | Readability at 200% text, no status overlap |
| Nearby | Shared panel, two-player icon, Wi-Fi text unchanged | Keep Host/Join instantly recognizable; no false connectivity implication |
| Records | Gold rule, trophy seal, minimal stat plaque | No density regression or excessive scrolling |
| Solo gameplay | Thin book divider only if useful | Never diminish puzzle grid or word trace touch accuracy |

## Important limits

The legacy `paperback-v1` design tokens have a different, quieter style. Keep v1 as shipped until the owner approves a v2 screen comparison. The owner explicitly liked the reference pack but has not yet approved every placement. The current last verified APK remains QA v86; this library commit does not create or validate another APK.

See `STYLE_BIBLE.md` for expansion recipes, QA gates and accessibility limits.
