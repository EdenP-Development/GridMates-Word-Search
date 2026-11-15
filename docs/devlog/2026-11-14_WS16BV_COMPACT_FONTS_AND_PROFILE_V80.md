# WS16BV — Compact font cards, genuine lock states and Profile cleanup

**Date:** November 14, 2026. **State at first commit:** v80 source and regression gates implemented, exact-source Android/art QA and owner phone confirmation pending. **Branch:** `feature/ws14b-style-anchor-and-art-direction-2026-11-08`; [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) DRAFT.

## Owner evidence and scope
Six v79 handset screenshots showed a materially better Portraits gallery (Owl/Fox/Rabbit compact rows, collection progress), coherent Achievements, and Profile metrics no longer wrapping. But four **Board fonts** remained tall white cards with large vertically spaced gray Selected/Locked fake controls, separate License buttons and unnecessary scrolling. Profile also had the redundant section heading **Your profile** beneath the **Player profile** page title. The owner asked to continue the game refinement. This pass addresses only those concrete points. No new artwork or fonts.

## Exact source changes
1. [UI commit `13ddcadda71fa9bc5129db6ac4403908157657f2`](https://github.com/PlayerEden/GridMates-Word-Search/commit/13ddcadda71fa9bc5129db6ac4403908157657f2) revises `CollectionScreen.kt`'s `BoardFontCard`. Original five-letter sample `BoardFontPreview(item.font.id)` still renders actual face. Compact label and description follow; **Equipped** and **Locked** are readable labels, not inaccessible disabled action buttons. Equipped cards keep a 2dp green border. A real, enabled **Equip** control appears only when `item.unlocked && !item.equipped`, and the **License** button is always available for all four font IDs with 48dp minimum tap height.
2. Fonts use **one column on content width below 600dp** or fontScale >=1.3 to keep each five-letter sample readable; two columns on larger tablet widths at normal text scale. The existing panel backgrounds and font painter are reused, preserving actual puzzle glyph rendering.
3. `Ws16CProfileHub.kt` section title **Your profile** becomes **Explore**, avoiding duplicate title language. Routes and live data remain.
4. `app/build.gradle.kts` QA versionCode **80**, versionName `0.9.36-compact-font-collection-qa`. Existing application ID and test signing configuration remain.

## QA and preservation
- [Source/regression commit `4116737d83c554de363e2ce43770e83ecf9ed86d`](https://github.com/PlayerEden/GridMates-Word-Search/commit/4116737d83c554de363e2ce43770e83ecf9ed86d) adds `test_ws16bv_compact_fonts.py` to *both* GitHub workflows. The source tests verify original four font definitions (atkinson, plex, dmmono, patrick), bundled license lookup, actual Equip/License callbacks, readable states, 48dp controls and existing Profile destinations.
- `CollectionGalleryPagingAndroidTest.kt` now checks visible font cards, equipped/locked semantics and opens/closes the real Readable Print Open Font License dialog. This instrumentation source is **compiled** during CI; no claim it ran on a physical handset.
- Source/background art, portrait collection and paging, Achievements, Records, Home/Library/book screens, book/Solo/Daily/Nearby data and game save formats are unchanged; original font binaries/licensing also unchanged.
- Previous verified fallback: [v79 standard Android artifact #14134286557](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43646063049/artifacts/14134286557), with unchanged QA signing family. Do **not uninstall or clear data** for an install-over QA update.
- Required code verification: both standard Android and independent artwork/release workflows succeed at the **same source SHA `4116737...`**, APK integrity, art asset hashes unchanged v79→v80, same cert and correct version. Earlier workflows for just UI commit `13ddcadd...` are *not* sufficient to prove the added regression tests ran.
- Owner real-device visual: Fonts top and bottom cards, Readable Print vs Classic Typewriter and License action, 200% font/320dp layout, Profile Explore heading, portrait equip state and saved progress reopening. Records Solo/Race/Duel still need visual review. Rights/release signoff not implied.
- Keep [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) DRAFT. Only revise the font layout after on-phone evidence shows a specific problem.

## CI receipt
Pending exact-source runs and signed APK inspection. No APK link is published by this source log alone.
