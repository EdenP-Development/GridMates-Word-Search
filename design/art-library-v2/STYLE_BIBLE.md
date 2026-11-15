# GridMates: Word Search — Ornamental Art Library v2

**Date:** 2026-11-15. **Status:** Owner-liked visual reference and preproduction asset candidates; NOT automatically approved for packaging or screen replacement.

## Why this exists

The current app uses quiet nostalgic paperback art. The user additionally likes four transparent, illustrated source sheets containing antique parchment panels, green and gold buttons, Woodland/Ocean/Bug badges, shelf decoration, and menu medallions; an expansion sheet of 12 decorative book spines and two reference boards were generated in the same design session. The app must reuse rather than redraw equivalent borders/icons on every screen. These are **optional enhanced visual variants**, not a silent override of `design/asset-pipeline/paperback-v1-tokens.json`.

**Compatibility note:** Older paperback-v1 prototypes explicitly disallow ornate gold and embossed leather. Preserve those prototype constraints for legacy/core gameplay; ornamental v2 applies only to explicitly selected UI and Library/Collection/reward surfaces. The puzzle board must prioritize glyphs, selected-word clarity, and low-latency gestures. Do not bake decorative graphics over interactive board cells.

## Reproduction recipe

- **Paper:** ivory parchment with fine natural grain, warm diffused edges; not bright white.
- **Metal:** muted antique gold trim, small embossed points, no neon glitter or excessive shadows.
- **Plantwork:** olive/forest-green laurel or oak sprigs, delicate occasional bows; repeat a single coherent motif.
- **Leather and wood:** restrained forest-green, olive, brown, deep blue/teal, lightly shaded vintage book spines and shelf structures.
- **Illustration:** painterly watercolor shading, rounded bevels and tactile surface depth; outline weight and soft highlights consistent across a family.
- **Lighting:** warm top-left, gentle lower-right shadows; no harsh specular streaks.
- **Icons:** single central recognizable symbol, framing consistent within one family; separate shape and semantic label.
- **Exports:** real RGBA alpha (not a checkered/painted background). Keep each isolated sprite fully inside its crop with 8–16 px bleed, no intersecting edges, no labels/text/®/™ in live bitmaps. Production labels remain native Compose text.
- **Source sheets:** keep exact source images + version, generation prompt, curator notes, crop coordinates, and SHA-256. The source PNG and crop rectangle reproduce the *pixel-exact* sprite. Re-generation with a style reference is only *approximately* reproducible.
- **Resolution:** master at 2–4× target display size; UI renderer should scale down, not up. Optimize to WebP/PNG at build time after QA; never ship multiple nearly identical sizes blindly.

### Accent tokens (candidates, not overrides)

| Token | Hex | Use |
|---|---|---|
| Paper ivory | `#F5E7D0` | Illustrated panel interior |
| Parchment shadow | `#E6D1AB` | Soft paper edge |
| Forest leather | `#254632` | Strong primary book/button |
| Laurel olive | `#657447` | Sprigs, inactive motif |
| Antique gold | `#AD8134` | Frames, rules, medals |
| Gold highlight | `#E3C377` | Thin bright edge only |
| Deep ink | `#27382C` | Native text |
| Ocean accent | `#2E6870` | Optional marine theme |
| Night blue | `#233F57` | Optional space/winter theme |
| Rustic brown | `#855333` | Wood and autumn/book accent |

These complement, but do not change, the shipped `GridMatesTheme.kt` colors or previous design token files.

## Shared component contract

| Family | Initial reserve | Example future use | Boundary |
|---|---|---|---|
| Paper panels & gold frames | 12 shared UI pieces | Home/Play/Daily/Nearby/Records/Collection cards | Native readable content, accessible hitboxes, scalable corners |
| Theme medallions | 12 | Library categories, collectible achievements, custom themes | No unlock-state mapping until curated |
| Shelf & decor | 12 | Library shelves, bookmarks, plaques, backgrounds | Do not obscure book spines or navigation |
| Menu icons | 12 | Home/Play/Daily/Library/Nearby/Records/Profile/Collection/Gifts/Settings/Hint/Back | Preserve labels and semantics, don't replace a clearer native icon |
| Additional book spines | 12 | Blank shelf books, optional cosmetic theme spines | Avoid new book IDs/progress logic; use source registry |

## Novel future reserve ideas

**Books:** narrow/wide blank spines, 2–3 thicknesses, same-height band systems, theme-coordinated mini-spines, completion-glow variants, summer/winter/desert/night accents.

**Panels:** three sizes of blank cards, shared 9-slice corner sets for landscape/portrait, button normal/pressed/disabled/selected, thin tab rails, badge backgrounds, modal headers, score plaques, question/help pages.

**Collection:** empty slot, locked seal, completed medal, claim ribbon, duplicate recycle symbol, weekly gift frame, portrait border and profile top bar.

**Gameplay:** subtle paper-edge dividers, selected-word seal, progress track, nonblocking reward flourish. Keep puzzle glyph and touch layer native, not painted into art.

**Seasonal/theme:** Ocean, Bugs, Garden, Winter, Farm, Desert, Night Sky, Space; each theme uses the *same shared global framing system* with only 1–2 accent colors and a distinct small central motif.

## Approval gates

1. **Candidate:** generated sheet isolated and indexed; no Android wiring.
2. **Technical review:** alpha/fringe, frame scaling/9-slice possibility, color contrast, crop dimensions, duplicate check, licensing/origin, file size.
3. **Preview:** show side-by-side actual 320dp, 360dp, narrow landscape, fontScale 1.5–2.0, normal/high contrast.
4. **Owner approval:** compare *existing* and new visuals on physical Android screenshot(s); preserve existing visual if the candidate does not improve usability.
5. **Integrate:** only approved candidates get stable resource IDs, fallback, Android tests and source-matched APK checks. Keep the asset manifest updated.

**Do not treat the owner liking the sheets as approval of every candidate on every screen.** Make polished, functional screens with cohesive reuse, not decorative overreach.

## Archive, storage and import

The first session produced a self-contained `GridMates_Word_Search_Visual_Library_v1.zip` with seven original sheets, 60 isolated PNG sprites, a machine-readable manifest with hashes and crop rectangles, a deterministic `tools/rebuild_sprites.py`, and a contact sheet. Canonical extra backup: ChatGPT Library `/Game Design/GridMates Word Search/GridMates_Visual_Library_v1.zip`, ZIP SHA256 `68fac0f6a40c7abbb3b75096692e74f016368218ffb2e8085e60a12f7636966e`.

**Connector limitation:** the GitHub connector writes UTF-8 source files, not the 28.7-MB binary image archive directly. The **index/style recipes/import script are in this GitHub branch**, while high-resolution PNG bytes are in the persistent Library archive and chat artifact. Do not claim that the PNG binaries are currently present in GitHub. Before Android integration or handoff to another developer, import those exact binary files into the repo (via normal git/LFS or the import script), commit and verify against their SHA-256s; then CI can ship them. Avoid hotlinking transient chat-generated file URLs.

**Repo integration scope:** `design/art-library-v2/` is preproduction only; do not change `app/src`, Gradle versionCode, approved wallpaper or art-shipment hashes as part of adding this guide.
