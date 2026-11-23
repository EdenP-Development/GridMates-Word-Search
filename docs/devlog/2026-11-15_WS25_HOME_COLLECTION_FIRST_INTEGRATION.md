# WS25 — First actual Home + Collection illustrated integration

**Date:** 2026-11-15 (Kentucky local)  
**Status:** implemented on dedicated draft branch; **CI, APK and owner phone approval pending**.  
**Source branch:** `feature/ws25-home-collection-visual-integration-2026-11-15`  
**Base:** `28ad2e8ad8039db181f9d919bc1706f2ec4d78db` (latest active WS16 branch, draft PR #131).  
**Previous safe signed QA:** v86 `3aa7d9f1dbf2281796726cb00cb78d52169318da`, [Android 81/81](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43756581784), [independent art 82/82](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43756581778), [signed QA APK artifact](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43756581784/artifacts/14178243637). Keep this as a known-good reference; do not instruct downgrade/reinstall without a save backup.

## Why this pass

Owner authorized moving from WS20–24 visual asset creation into **actual existing Android Home and Collection integration**, using current app screenshots as reference and keeping functionality. The project already has `GridMatesIllustratedFrame`, full walnut Library art, WS16B carousel Home, local profiles and original 12 Woodland portraits. The existing visual system should be reused, not a wholesale new architecture.

## Source changes

- `Ws16HomeScreen.kt`: live native `PaperProgress(count, pages)` below focused book progress, with `ws25-home-book-progress` hook. Cover focus/click handlers and progress calculations preserved. No artwork baked into count.
- `CollectionScreen.kt`: replace flat stock FilterChips with real illustrated green/gold frame tabs, 48dp min hit size, live text and accessibility-selected semantics; warmer paper / soft shadow in existing portrait and typeface cards. All existing pagination, lock conditions, equip actions, font licensing, store mutations and stable test tags preserved.
- Same `CollectionScreen.kt`: new optional 58dp **decorative** `WoodlandCollectionAccent()` next to Woodland gallery heading at standard font scale, without misrepresenting it as a new unlockable portrait; production fallback simply hides the art. No interaction.
- `tools/asset_pipeline/import_ws25_collection_accent.py`: copies one exact-SHA WS22 **Robin** PNG from the actual binary artwork already present in GitHub `design/art-library-v2/ws22/` into `app/src/debug/res/drawable-nodpi/gm_ws25_collection_robin.png`, and checks SHA, PNG signature, byte-for-byte copy and release-path absence. Original source remains candidate, NOT release-approved.
- `test_ws25_home_collection.py`: source-preservation, real sprite SHA and debug import tests.
- Two CI workflow updates invoke importer/guards before compilation. New QA `versionCode=87`, versionName `0.9.43-home-collection-visual-qa`; same package, signer and store schema.

## No changes / explicit boundaries

No new gameplay modes, content generation, multiplayer transport, save format, point economy, portrait eligibility, gameplay board input, official release graphics or Sudoku repository changes. WS23 and WS24 remain **Library candidate archives**, not directly installed as APK resources in this pass. WS22 Robin exists as full original binary in the development branch. A passing release-art isolation test would mean its candidate sprite was excluded from release, **not** production approved.

## Quality gates

1. **Source/import:** both Python guards and WS16BU/BV collection legacy guards pass; importer exact-hash works.
2. **Android:** full standard Android CI green, including lint and Compose test source compilation; independently assembled release art isolated.
3. **APK:** QA package code 87 and same test signer, no forced downgrade or uninstall, checksum and artifact recorded.
4. **Owner visual acceptance:** 320/360dp normal + 150–200% text, Collection tab states, side-by-side Home focused cover and actual book page counts; 58dp accent at real handset scale.
5. **Regression / still open:** gallery next pages, changing portrait/font, reading font licenses, opening focused book and saving/reopening play, real touch and same-Wi-Fi two-phone QA. Compose test compilation alone is not actual physical-device execution.

## Next safe action

Inspect **exact-source** two GitHub workflow statuses and output artifacts from this draft PR. **No APK is claimed here until successful source-matched verification.** Use a side-by-side owner phone screenshot review before any wider WS24/WS23 import, main-branch merge or release approval. Continue follow-on Collections preview and Profile polish without altering the accepted v74/v75 Library cabinet.
