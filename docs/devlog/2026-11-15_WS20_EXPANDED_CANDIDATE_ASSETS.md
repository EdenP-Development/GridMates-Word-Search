# WS20 — GridMates Collection/Rewards + themed spine art reserve

**Date:** 2026-11-15. **Status:** Source asset batch complete and GitHub source QA green; **candidate-only**, not added to APK.  
**Context:** Owner requested continued abundance of cohesive, reusable GridMates assets while artwork style was fresh, with future reuse across Word Search and Sudoku. A temporary ChatGPT Android UI error was shown after the archive upload; this was an app/interface error, not evidence of corrupt source files.

## What exists (not merely planned)

**88 verified RGBA transparent PNG candidates:** 16 illustrated Collection/Profile/Reward sprites (oval avatar frame, neutral player/achievement plaques, lock/unlock/equipped medals, ribbon/crest/trophy/achievement pieces, gold progress and decorative accents) + **72 book spine PNGs, based on 72 editable SVGs**, representing **24 themes x 3 variants** (narrow, regular, collector). Each has stable ID, file path, exact-source SHA-256 hash, bitmap dimensions and source provenance/crop data in `design/art-library-v2/ws20/manifest.json`. Two text-bearing generated overview boards are **reference-only**, not usable runtime sprites.

The 72 spine SVGs are truly **committed as independent editable files** to [`design/art-library-v2/ws20/svg/spines/`](../../design/art-library-v2/ws20/svg/spines/). The portable [rebuild script](../../design/art-library-v2/ws20/build_gridmates_ws20.py) and [full manifest](../../design/art-library-v2/ws20/manifest.json) are committed too. The SVGs are versioned individually so future themes can reuse their geometry/medallion frame instead of redrawing book art. All labels and gameplay status should be native UI, not AI-baked lettering.

## Verified external backup

**Full source archive:** `GridMates_Asset_Expansion_WS20_verified.zip`, 16,495,773 bytes, SHA-256 `6c508feeb8fcc00b067cdee55f719718664af9d2fe27391ff2cc47297f623498`; ChatGPT Library path `/Game Design/GridMates Word Search/GridMates_Asset_Expansion_WS20_verified.zip`. The same verified archive is also an on-chat download file. The archive contains 168 members: 88 PNG candidates, 72 SVG source files, one original Collection/Reward sheet, 2 reference-only boards, rebuild script, manifest, README and 2 preview contact sheets. Full ZIP CRC passed, all 88 PNG bytes agree with manifest SHA-256 and have transparent RGBA alpha, 72 SVG files agree with source SHA-256, and all three source/reference sheet hashes were validated.

**Correction:** an older builder note erroneously described 48 spine variants; final corrected script/manifest/archive consistently say **72**. An earlier Library file of the same name may be the pre-correction ZIP; use only the `_verified.zip` copy and its checksum above. The ChatGPT UI's generic error does not establish a GitHub or archive failure.

## GitHub evidence

- [WS20 source and art readme](../../design/art-library-v2/ws20/README.md)
- [Full 88-item manifest](../../design/art-library-v2/ws20/manifest.json)
- [Portable build source](../../design/art-library-v2/ws20/build_gridmates_ws20.py)
- [Independent source validator](../../design/art-library-v2/ws20/validate_ws20.py)
- [**72 GitHub-backed editable spine SVGs**](../../design/art-library-v2/ws20/svg/spines/)
- `design/asset-registry.json` registers all 88 as externally archived candidate artwork, with SVG sources in-repo and no Android package impact.
- [**WS20 independent GitHub source QA #43779274618**](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43779274618): **SUCCESS**. The job exercised validation of 24 themes, all 72 actual committed SVG source hashes and 88 entries/crops. Note: it does **not** run or verify the external binary PNG ZIP; that was separately checked locally.
- PR [#131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) remains **draft** and unmerged. All changes are on its feature branch, not main.

## Known boundaries and next work

No change to Android app source, Gradle build, saved puzzle/book/achievement IDs, user profile state, artwork packaged in the signed APK, LAN/network protocol or gameplay logic. The verified gameplay fallback remains **signed QA v86**, and the user has not yet tested WS20 art in an APK. Do not claim physical visual/readability acceptance.

These candidate assets should first be reviewed at 48–120px in actual Collection/Profile and Word Search Library, with crop/halo, text-size and 320/360dp checks. Use Core frames and native state controls for shared Sudoku usage; reserve Word Search books/shelves for Word Search. Only individually reviewed assets should be optimized/copied into an Android build; leave the rest in the reusable art bank.
