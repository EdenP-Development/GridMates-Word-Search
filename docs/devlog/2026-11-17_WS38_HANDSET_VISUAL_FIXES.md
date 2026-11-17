# November 17, 2026 — WS38 handset visual fixes

The owner supplied screenshots from v100 showing the Home covers, the full cabinet, Collection, Together setup, Solo, a 30 × 30 Garden game, and a Space book page. The [v100 receipt](2026-11-17_WS37_VISUAL_COHESION.md) records the previous Android checks. This pass starts at `286e0a1adff7181acd36ebf50ab004e8bf26ff84` in [draft PR #148](https://github.com/PlayerEden/GridMates-Word-Search/pull/148). The signed v100 APK is the handset comparison build.

## Changes

The Garden PNG has transparent exterior pixels. The Home pager uses a bounded rectangular press indication around the book's transparent canvas. Its press treatment now scales the book subtly using the existing pager transform.

The word list measures native target labels before choosing columns. Long Garden words receive the full width, and their labels can wrap completely. A native layout regression covers PHOTOSYNTHESIS and GERMINATION at a phone width and enlarged text.

Book pages inherit the shared warm paper instead of a flat theme tint. The old tint helper and its experiment-specific test are retired. Together setup uses the same illustrated navigation, selected mode controls, and Host and Join buttons as the puzzle screens.

## Verification

Build and Android verification pending for v101. Source artwork, game IDs, saves, and LAN behavior use the v100 implementations.

## Follow-through

Review the four screenshot issues on Android, then supply the signed v101 APK. Two-device Together testing and portable book backup remain subsequent work.
