# WS21 — GridMates asset-production expansion: book covers, seasonal rewards, and Core templates

**Date:** 2026-11-15 · **Status:** Source artwork delivered, manifest and SVG sources committed, independent source QA GREEN. **Not an APK update or owner-approved UI placement.**

## Owner direction and starting point

The owner explicitly prefers an abundant, cohesive, reusable visual warehouse for Word Search, Sudoku and other GridMates projects rather than adding one-off placeholders to app screens. Previous source candidates: WS17 60 sprites, WS19 14 Core illustrations + vector masters, WS20 88 sprites with 72 editable book spines, and WS18 2,142 planned asset slots. The owner asked simply to continue creating useful artwork and reserve assets.

The previous verified gameplay APK is still **QA v86** (Android 81/81, independent art 82/82, real Android API35 emulator 6/6); PR [#131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) remains a draft, main untouched.

## What was actually created

**136 candidate transparent PNG sprites** grouped as:

- **16 illustrated seasonal / rewards:** blank round/shield laurel frames, blank parchment banner, open-book crest, autumn acorn/leaves, winter snow/pine, spring flowers, summer sunflower/bee, wax-sealed letter, gold trophy cup, gold leaf/acorn key, green/gold gift box, round medal, star wreath, green bookmark, square parchment frame.
- **72 themed Word Search book covers:** 24 themes × three editable variants `standard`, `collector`, and `pocket`. Theme names: Woodland, Ocean, Bugs, Garden, Space, Winter, Farm, Spooky, Desert, Night Sky, Autumn, Spring, Summer, Mountains, Dinosaurs, Food, Weather, Travel, Music, Fairy Tales, Arctic, City, Underwater, Tropical. Each cover is blank where a book title/theme name belongs, so Compose/native text can supply it, and no new game IDs are hard-coded.
- **24 round theme medallions:** common framed parchment/gold shape, motif and theme accents.
- **24 universal GridMates Core templates:** 8 families × three tones (forest, cream, gold): header plaque, menu card, tab, button, portrait frame, progress track, achievement slot, pagination button.

**120 SVG source files** for the editable covers, medallions and Core templates have been individually committed to [`design/art-library-v2/ws21/svg/`](../../design/art-library-v2/ws21/svg/). GitHub also holds the [portable, deterministic rebuild script](../../design/art-library-v2/ws21/build_gridmates_ws21.py), [136-sprite manifest](../../design/art-library-v2/ws21/manifest.json), [source validator](../../design/art-library-v2/ws21/validate_ws21.py), [README](../../design/art-library-v2/ws21/README.md) and one additive record in `design/asset-registry.json`.

### Important accuracy about generated files

Two new AI-generated full-screen layout/concept boards contain **baked words, labels and examples** and are saved as reference-only images, not game-ready sprites. The 16 painterly illustrations were cleaned from a generated RGB atlas with baked near-white checkerboard: color-key alpha cleanup was necessary. Four illustrations crossed rigid grid-cell borders, so the export was corrected to include the missing bleed; stray adjacent-cell fragments were removed. All 16 are now technically RGBA/alpha and visually coherent in the preview contact sheet, but fine hairline halos/small-screen quality remain a manual review item before release.

The 120 vector source shapes are purposefully cleaner and more scalable than the painterly atlas. They do not claim pixel-identical style to the AI-drawn pieces. Rendering can combine them with approved illustrated theme motifs and the global color system.

## Exact archive / reproducibility receipt

Full **`GridMates_Asset_Expansion_WS21.zip`**, **17,519,880 bytes**, **SHA256 `4e97d62b26f91b091f4bc08db8f3d37a79578dfe45140b74ba876ebff40f45c8`**. The full archive was verified by ZIP CRC and exact SHA-256 of **all 136 PNG sprites** plus **all 120 linked SVG source files**. After fixing the builder to use its unpacked source directory and fixed timestamps, running the builder from either the working copy or `tools/` yielded the **same byte-for-byte ZIP SHA-256**.

The verified full archive is backed up in ChatGPT Library at `/Game Design/GridMates Word Search/GridMates_Asset_Expansion_WS21_FINAL.zip`. A smaller ~2.12MB `GridMates_Asset_Expansion_WS21_Lite.zip` with 136 transparent WebP previews, contact sheets and manifest is separately backed up there at `.../GridMates_Asset_Expansion_WS21_Lite_FINAL.zip`. The source sheets and all 136 PNG binaries are **not uploaded to GitHub** through the GitHub connector; the ZIP is the binary source of truth, and the SVGs are the GitHub-editable source of truth.

## Source CI and usage boundary

[**WS21 GitHub source-only QA #43793231439**](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43793231439) returned **SUCCESS**. It checks all 120 committed SVG hash matches and 136 manifest entries, including 24-theme/three-cover completeness, Core 8×3 variants, SVG syntax and no external image/script links. This is *not* the same as verifying 136 PNG binaries from the external ZIP or visual acceptance on a real phone; those were evaluated separately as noted.

No user game save, achievement ID, unlocked book, asset packaged in the Android APK, Gradle version, network protocol, gameplay code or Sudoku repository was touched. Some generic Android/art build workflows were retriggered by source commits; they are not evidence of a changed APK, and WS21 is not an Android test build.

## Next meaningful art work

Stockpile well-framed individual theme motifs (especially new future categories beyond these 24), improve the more ornate `collector` vs `standard` distinction, and develop a small tested 9-slice/nine-patch panel export system. Then prototype a **Collection/Profile A/B** screen using just two shared Core components and one reward frame (not every candidate simultaneously). Review on 320–360dp, 150–200% font and actual device before integrating any image into a new signed QA build. Preserve all source candidates whether or not used.
