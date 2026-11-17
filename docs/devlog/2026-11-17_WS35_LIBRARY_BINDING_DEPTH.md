# November 17, 2026 — WS35 Library binding depth

The owner said the Library looked flat and the spines looked pasted onto the screen. This pass uses the [v97 build and handoff](2026-11-17_WS34_PHONE_SCREENSHOT_POLISH.md) in [draft PR #145](https://github.com/PlayerEden/GridMates-Word-Search/pull/145) as its starting point. The parent is `fc387ca49d607d2eba574f010e10bdfcdcc2f516`. The [signed v97 APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44120147813/artifacts/14345181818) is the verified comparison build.

## Artwork and layout

- Six new, independently cropped bindings use rounded leather and cloth with raised bands. Woodland, Space, and Garden have individual gold emblems. The three cloth variants fill spare shelf space at different heights.
- The source atlas was generated for this game. The original RGBA PNG is kept byte for byte as numbered parts under [masters/ws35](../../design/visual-system/masters/ws35). The [manifest](../../design/visual-system/ws35-library-bindings.json) pins its hash, crop columns, and output pixel hashes.
- The cabinet's walnut finish is darker, the shelf recesses add depth, and each binding has a cast shadow where it meets the wood. The three real books are split two above and one below on the two-shelf view.
- Live Compose titles and touch cells open the original catalog entries. Decorative books occupy spare space without clicks. Pressing a book lifts its face, and the shelf pager retains access to all catalog entries.

## v98 build and verification

The package was built from [`8e8c15f11295d382ccb770838891eb766336aa79`](https://github.com/PlayerEden/GridMates-Word-Search/commit/8e8c15f11295d382ccb770838891eb766336aa79) in [draft PR #146](https://github.com/PlayerEden/GridMates-Word-Search/pull/146). The source pipeline passed 29 local cabinet checks. The six binding rasters have clear perimeter alpha and a verified opaque core.

| Check | Result |
| --- | --- |
| Android unit tests, lint, instrumentation compilation, and signing | [Passed](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44136465891) |
| Independent art build and release isolation | [Passed](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44136465842) |
| Signed debug APK | [QA artifact](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44136465891/artifacts/14352522134) contains `app-debug.apk`, 32,442,580 bytes |
| Package and version | `com.playereden.gridmates.wordsearch.qa`, code 98, `0.9.54-ws35-qa` |
| APK SHA-256 | `06d9f7686017153e52725e3f94cb96531de0577b623d5119841a3017bcbbdefb` |
| Stable QA signing certificate SHA-256 | `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419` |
| Packaged art | All 219 previous assets match v97. The 6 imported WS35 spines match their local exports. |
| Independent package | All 369 APK entries match the standard build. The independent package SHA-256 is `213f5dcf88fc120fee82989fc02ff53653a2de213c9227d80fec75cc26af5433`. [Independent QA ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44136465842/artifacts/14352093174). |

Android 15 accepted the exact signed v98 APK over v97 in the [second](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44138223788) and [third](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44139001057) runs. The [first comparison](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44137368152) lost the emulator while opening the v97 cabinet. The second timed out capturing the open v98 Library. The smaller framebuffer and Vulkan-off retry lost ADB before confirming the v98 Library controls. The signed APK, artwork, and install-over are verified. The displayed Library, opening Garden, and compact viewport paging need an on-device check.

## Follow-through

The revised Library is available in v98 for handset review. The next feature is portable backup and restore for completed book pages, bookmarks, and unfinished pages.

## Source generation prompt

```text
Use case: stylized-concept. A production-ready transparent game sprite atlas for a cozy illustrated paperback puzzle game's interactive walnut bookcase. Six isolated upright hardcover BOOK SPINES, shown straight from the spine side with just a very thin shadowed glimpse of the right cover board for thickness. Each is a complete slender upright leather-bound object, width around 19–21 percent of its height. Arrange the six at even horizontal intervals in ONE horizontal row across a wide image, all at one baseline, all fully inside the image, separated by wide completely transparent gutters. The spines never touch. Match the gentle hand-painted gouache and clean storybook ink finish of a premium cozy puzzle game. These should look like rounded physical book bindings. Upper-left soft light, fine tactile leather grain and subtle worn corners, raised stitching and gentle warm highlights, a darker rounded curve along the right edge, believable cap and board edge, aged gold tooling used sparingly. Use solid opaque books with clean anti-aliased silhouettes on actual transparent alpha. No cast shadows outside the books.

From left to right: 1 deep pine-green leather with a small antique gold embossed traditional fir sprig in the upper quarter; 2 faded midnight blue embossed leather with a small gold ringed planet in the upper quarter; 3 warm olive green leather with a small delicate gold five-petal blossom in the upper quarter; 4 plain warm tobacco cloth; 5 plain mellow auburn cloth; 6 plain slate-purple bookcloth. The three plain spines are as physically solid and dimensional as the featured books, use different restrained simple raised horizontal cord arrangements, and have no centered emblem. All six have a broad quiet uninterrupted central face from the midline down to the lower fifth for game text added later. Only fine low-contrast edge tooling and one or two raised bands near the ends. No text, letters, numbers, wordmarks, badges, logos, frames around the sprite sheet, title panels, book covers facing the camera, cabinet, shelves, floor, or background objects. Avoid straight flat paper labels, oversized icons, repeated gold rectangles, glossy chrome, hard studio lighting, bright yellow accents, detached highlights. Keep realistic readable texture and distinguish the physical bindings at 36–48dp wide.
```
