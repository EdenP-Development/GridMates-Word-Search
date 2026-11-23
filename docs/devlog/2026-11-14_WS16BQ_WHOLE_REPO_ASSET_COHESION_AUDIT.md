# 2026-11-14 — WS16BQ whole-repository and asset-cohesion audit

> **Quick status: AUDIT COMPLETE; documentation-only; NO new APK or runtime changes.** User requested a broad sweep of GitHub/design/materials to avoid forgotten assets and incoherent screen styling.

## Who, what, where, why
- **Owner evidence:** concept Home screenshot with warmer parchment grain, stronger illustration atmosphere and intentional book-specific themes; wants shared asset reuse and identification of missing/forgotten art/design work.
- **Review performed by:** ChatGPT using connected GitHub live repository, tree, design/art registers, issue/PR metadata and representative actual Kotlin callers. User supplied recent v75 device screenshots. Not an emulator/device execution.
- **Where:** `feature/ws14b-style-anchor-and-art-direction-2026-11-08`, open draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), existing WS14/WS16 design docs and Compose app.
- **Prior state:** [WS16BP v75 QA report](2026-11-14_WS16BP_LIBRARY_FUNCTIONAL_NAVIGATION_QA.md) on compiled source `6f0ccaa46840006d13918e81fcb32a767a8ad94b`, Android 71/71 and independent artwork/release 72/72 green; user phone visuals supporting Home, Library, opened Woodland and one found word/hint but not full save reopening.

## Actual work and findings
- Compiled [whole-repository audit](../audits/2026-11-14_WHOLE_REPO_ASSET_AND_VISUAL_COHESION_AUDIT.md) with explicit evidence, reusable assets, incomplete art, stale registries, screen coverage, risks and prioritized next pass.
- Active head at start `0c15a1a667a725b85240d0eee553840fcb9fb63a`. GitHub tree complete: 742 files; main 19 files. 86 open PRs and 45 open issues. Draft stacked chain remains unmerged.
- Confirmed style bible and master game plan already exist, plus shared parchment/frames, Home decor, physical Library assets, three theme paper tints, book factory, QA art and reports.
- Found WS14 central register empty; approved-assets ledger named by issue #108 absent on active head; older asset statuses contradict later QA integrations; high-resolution original WS16AB master is not in repo according to its own manifest. All are **investigation/reconciliation items**, not assumptions of data loss.
- WS16G 20-role register: 8 rendered QA-art roles, 6 integrated action roles, 5 pending painterly master/export, 1 pending composite; 0 recorded owner/release flags there, even though later actual UI layouts have owner acceptance.
- Identified older `README`, START_HERE, journal and handoff status headers lag latest v75, current selected source must be checked live.
- New concept's stronger paper texture should be optional Home-specific enhancement; avoid forcing decorative backgrounds into every game/screen.

## Verification
- No source code/art changes, APK compilation, CI trigger, checksum revalidation, full device QA or automated tests attempted in this audit. Documentation-only update marked `[skip ci]` by commit.
- Current last passing APK remains v75, [Android #43635464423](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43635464423), [art #43635464422](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43635464422). QA source original is v75 commit `6f0ccaa...`, later docs-only commits are not new APK builds.

## Open blockers and next exact move
- Need canonical asset crosswalk; dedicated source/rights/owner approval; possible missing full-res original; two-phone Nearby and app-wide accessibility/performance/real save recovery gates. Issue #128 and #120 remain open.
- Keep PR #131 DRAFT, no merges, no new publishing approval; protect v74/v75 physical Library design and all saves.
- **Next:** reconcile live importer/raster names with the role registry and owner art statuses, then make a one-screen optional texture QA A/B + compact preview modal polish. Preserve reuse-first principle.
