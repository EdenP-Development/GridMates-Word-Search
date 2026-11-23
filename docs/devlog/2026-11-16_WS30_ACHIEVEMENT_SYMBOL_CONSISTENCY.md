# WS30 — Actual v91 phone screenshot follow-up: reward symbol consistency

**Date:** November 16, 2026 local.  
**Base:** verified WS29 v91 source `7eb68a904e27a57c5645f9eab850b8983cfa8528`, draft PR #136.  
**Status:** v92 candidate isolated stacked branch; CI and owner physical validation are separate.

## Phone evidence
The user shared five new physical QA screenshots: achievements page 1, immersive Walnut Library, Woodland book details dialog, older-page Library shell, and Outer Space 8×8 Daily puzzle. Achievements show gold-edged cream shields correctly and live eight-milestone progress. The locked "Today Counts" displays a *bright yellow/orange Unicode sun* despite other locked symbols being gray. This is Android emoji presentation variation, not a WS21 shield source defect.

The Library screenshots are two intentionally selectable QA studies: modular/immersive designs and an older page-mounted cabinet. The "3 books" count appears only in an older study; the preferred modular case already removed the plaque. The pilot book is explicitly tagged "TEST BOOK"; it must not masquerade as final publication. No route or book content is changed in WS30.

## Exact fix
- In `CollectionScreen.kt`, preserve source-exact blank WS21 gold and cream achievement shields and original eight achievement IDs, titles, progress and remaining unlock copy.
- Replace `Text(motif)` with `Ws30AchievementSymbol`: a native Compose `Canvas` sized 28dp that draws four monochrome filled paths (star for puzzle counts, sun for dailies, sparkle for hint/Hard, flower for completing themes), using fixed earned gold ink `#875C1C` or muted locked ink `#798376`.
- Reuse original 66dp shield and existing UI/test tags. No emoji, no extra font, data or internet dependency.
- Add source guard and UI test tag for native medal motif. Standard Android and independent art pipeline run the guard. QA version code 92 (same package/signing, no persistence schema change).

## Boundaries
No edit to achievements metrics, local saves, books, puzzle board, Home, Collection portraits/fonts, player profile, full/legacy Library styling, or raw WS21 source assets. The gallery is still a QA study; owner must verify actual locked vs earned rendering at normal and enlarged text. No claims of on-device instrumentation execution without emulator/device evidence.
