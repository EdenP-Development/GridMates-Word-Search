# November 17, 2026 — WS36 matching covers and bindings

The owner confirmed the v98 Library on the handset and preferred the rounded bindings. Their screenshots showed the previous painted binding edges on the Home covers. This pass starts from [v98 draft PR #146](https://github.com/PlayerEden/GridMates-Word-Search/pull/146) at `e697fa5bb784b29bede72d0f5f26eef598dfad47`. The [v98 receipt](2026-11-17_WS35_LIBRARY_BINDING_DEPTH.md) and [signed v98 APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44136465891/artifacts/14352522134) are the comparison points.

## Artwork and routing

The three color and emblem pairs use the separate WS35 spine sprites and the [WS36 cover projection manifest](../../design/visual-system/ws36-matched-books.json). The exporter projects the visible binding around the angled edge of each existing WS16AE front. It checks the original alpha silhouette and the entire front face pixel for pixel. The original illustrated owl, ringed planet, flowers, and lettering appear at the same coordinates. Each derived cover is versioned separately.

Home, the cover browser, book previews, and the illustrated list resolve the current edition through `BookArt`. The playable Library bindings read the same theme paths as the illustrated list. The candidate asset files are debug-only. Book IDs, UI actions, and progress storage follow the existing routes.

## v99 build and verification

Generated and visually reviewed all three projected covers at phone scale. Signed Android QA and independent release-art checks are pending.

## Follow-through

Review all three matching book pairs on a handset. The next feature remains portable backup and restore for completed, bookmarked, and unfinished book pages.
