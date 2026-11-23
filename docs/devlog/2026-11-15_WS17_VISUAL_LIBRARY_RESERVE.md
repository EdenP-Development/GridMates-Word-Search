# WS17 — GridMates reusable visual asset library and future-theme reserve

**Date:** 2026-11-15. **Owner request:** Pre-make cohesive visual assets while the approved style is fresh; use as many as useful across the game, keep re-generatable, place in GitHub for future work, and ask if live screenshots are needed to place them properly.

## Starting checkpoint

Previous verified release candidate: **v86 signed QA** in [WS16CE](2026-11-15_WS16CE_AUTO_WIFI_READINESS_V86.md); Android 81/81, independent art/release 82/82, API35 actual emulator 6/6, [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) still **DRAFT**. Do not overwrite user local progress, pack assets without review, merge main, or claim a new APK.

Style precedence: shipped quiet paperback `design/asset-pipeline/paperback-v1-tokens.json` remains intact. New, more ornate illustrated cream/gold/forest-green reference sheets are the user's preferred *candidate* direction and live in a separate `design/art-library-v2/` style system; reuse existing native buttons and puzzle controls until an explicit side-by-side review.

## Work completed in the ChatGPT asset workspace

- Four owner-shared **original transparent** source atlases validated as 1448×1086 RGBA: shared parchment/gold UI, Woodland/Ocean/Bug medallions, library shelf trim, and menu controls.
- One more 1536×1024 RGBA 12-book-spine expansion sheet generated with matching antique book/ribbon/laurel/ivory/gold palette. Two further 1536×1024 **painted reference boards** generated to plan future collections, tabs, badges, progress states, seasonal motifs, and coherent shelf layouts. The latter are NOT alpha-sprite sheets and contain printed labels; do not ship as UI bitmaps.
- All five transparent atlases isolated with careful crop boxes into **60 transparent lossless PNG candidates** (12 shared panels/UI, 12 themed badges, 12 shelf/book decor, 12 navigation icons, 12 extra book spines). Each sprite has stable family/name, source rectangle, output dimension and SHA-256 in `manifest.json`; the source sheets are embedded; rebuilding those exact crops was executed and passed. This is **pixel-exact cropping**, not a claim that future generative redraws will reproduce identical pixels.
- Contact-sheet review image generated with transparency checkerboard; final names verified against positions. Full pack ZIP `GridMates_Word_Search_Visual_Library_v1.zip`, **SHA-256 `68fac0f6a40c7abbb3b75096692e74f016368218ffb2e8085e60a12f7636966e`**, ~28.7MB. Smaller optional review ZIP `GridMates_Word_Search_Visual_Library_Lite_v1.zip`, ~2.0MB, contains 60 transparent WebP previews and contact sheet. Nothing integrated into APK.
- Persistent exact full archive uploaded to ChatGPT Library `/Game Design/GridMates Word Search/GridMates_Visual_Library_v1.zip`. This is a recoverable binary backup separate from the GitHub repository.

## GitHub work actually committed

- `design/art-library-v2/STYLE_BIBLE.md`: consistent texture/palette/icon/shape/lighting recipes, licensing and quality gates, future theme backlog; explicitly separates v2 from old prototype v1.
- `design/art-library-v2/RESERVE_INDEX.json`: **7 exact source SHA-256s**, category lists of all **60 named candidate PNGs**, reproducible archive digest, repo vs external binary status.
- `design/art-library-v2/import_visual_library.py`: isolated extraction only after ZIP SHA-256, seven sheet hashes, 60 sprite hashes, CRC, duplicate and path-safety checks. Does **not** modify `app/` or Gradle.
- `design/art-library-v2/vector-prototypes/`: **four real editable SVG source components now in GitHub**: parchment panel, blank decorated book spine, circular medallion background, gold divider. These are vector *approximations/templates*, not the exact PNG pixel art.
- `design/art-library-v2/README.md`: how to locate the archive and candidate screen map, with explicit binary handoff limit.
- Existing `design/asset-registry.json` gains a new **candidate-only** registry entry; original artwork IDs/statuses untouched.
- `.github/workflows/ws17-art-library-source-qa.yml`: independent, non-Android provenance QA for seven sheet hashes, 60 unique IDs, SVG XML validity, missing-binary/honest packaging status.

**Important GitHub limitation:** the installed GitHub connector does not accept local binary ZIP/PNG upload directly. The **metadata, style bible, importer, vector examples and QA workflow are committed in the draft PR branch**, but the 60 original high-resolution PNG bitmaps and seven PNG atlases are **not in the GitHub tree yet**. They are safely in the persistent ChatGPT Library and available through the chat's download link. A future normal git push/LFS import of that exact archive is needed before Android integration. Do not claim they are all committed as image files.

## Evidence / quality

[WS17 source-only QA #43763480523](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43763480523) **SUCCESS 5/5 steps**, covering manifest counts/IDs, source SHA-256 formatting, valid SVG XML, candidate-only asset registry and no runtime packaging claim. Earlier attempt #43763451210 failed because source QA accidentally rejected the standard SVG namespace URL; corrected to prohibit external images and active scripts, and successful rerun verified.

ZIP re-extraction SHA checks, sprite count and generated contact sheet were validated locally. **No runtime screen/UI, generated wallpaper, Android version, signing identity, asset ship catalog, or saved data were changed**. Any Android workflows triggered by adding a new workflow are redundant for unchanged app source and should not be interpreted as a newly approved art APK.

## Visual placement and optional screenshots

I can inspect current repo code, design tokens and shared frames independently; screenshots are **not required to stockpile and document** future art. Screenshots **are valuable before changing live UI** because a working screenshot shows actual layout, density, visual hierarchy and real-device crop/scroll behavior that source cannot fully prove. Best intake: (1) Home/Play, (2) Library shelf/book view, (3) Collection/unlocks, (4) one Solo 15×15 puzzle, optionally Daily/Nearby/Records later. Review screenshots without assuming they grant permission to swap every asset.

## Next implementation gate

When owner wants placement, start with a **single screen-to-component mapping and A/B preview** using the exact current Compose hierarchy, then selectively promote only 1–2 candidate families (e.g. Library blanks and common medallions). After that, code QA/Android layout + signed APK + real owner screenshot acceptance. Leave the untouched v86 APK as stable fallback until code changes earn a new verified build.

**One-line handoff:** On draft PR #131 read `design/art-library-v2/README.md`, `STYLE_BIBLE.md`, `RESERVE_INDEX.json`, and this journal; retrieve the binary ZIP from ChatGPT Library; stage with the SHA-checked import script; do not ship unreviewed art.
