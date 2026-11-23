# WS16BQ — Whole-repository, asset and visual-cohesion audit

**Date:** November 14, 2026. **Scope:** `PlayerEden/GridMates-Word-Search` active development branch `feature/ws14b-style-anchor-and-art-direction-2026-11-08`, draft PR [#131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131). **Audited source head:** `0c15a1a667a725b85240d0eee553840fcb9fb63a`. **No Kotlin/art/build changes made by this audit.** Source/manifest/document review and a verified repository tree inventory; no new Android runtime/device tests or binary-by-binary revalidation.

## Owner request and immediate decision

Owner supplied a richly textured illustrative Home concept (warmer paper, more natural speckling/edge detail, leaf/mushroom framing and a richer shelf) and asks whether existing assets can be reused across all screens, what actually needs independent creation, and whether documentation/plans/art have been forgotten.

**Decision:** Freeze the visually accepted v74/v75 *Library cabinet geometry and button treatment*; do a **reuse-first asset and screen-state reconciliation**, not another whole-app restyle. The shared GridMates paper must stay calm enough for puzzles, but the owner now welcomes a modestly richer **Home** texture like the concept. Use a screen-specific adjustable enhancement rather than forcing dense scenery behind gameplay/other GridMates games.

The reference image is an art-direction reference, not an approved cuttable sprite atlas or production master.

## Repository baseline and governance

- Live repository: `main` currently has **19 files** / only docs+early foundation; active PR #131 has **742 files** (846 total git tree nodes), of which `design/` has **93** files, `docs/` **231**, `app/` **188**, `tools/` **82**, `.github/` **41**. No truncated tree response.
- GitHub currently reports **86 open PRs** and **45 open issues**. PR #131 itself is draft, stacked on [PR #130](https://github.com/PlayerEden/GridMates-Word-Search/pull/130), and metadata reports **675 commits / 396 changed files**. These are not 86 independent releases; many are sequential feature/draft branches.
- Source of truth: **current active branch + source inspection + latest actual CI/owner screenshots**, not the `main` README or old top-of-file milestone claims. `README.md`, `docs/START_HERE.md`, `docs/DEVELOPMENT_JOURNAL.md`, `docs/WS14_ACTIVE_HANDOFF_2026-11-09.md` still prominently open with earlier v73 or much older checkpoints. Add prominent current links without deleting historical records.
- [Latest v75 functional QA receipt](../devlog/2026-11-14_WS16BP_LIBRARY_FUNCTIONAL_NAVIGATION_QA.md): [Android #43635464423](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43635464423) **71/71** and [independent artwork/release #43635464422](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43635464422) **72/72** on code `6f0ccaa46840006d13918e81fcb32a767a8ad94b`. User phone screenshots support Home→Library→Book preview→Details→Page 1→found word→hint→notices, **not** persisted relaunch or full gameplay completion. QA APK v75 is an install-over candidate; PR remains draft.
- No automatic branch merges or removal of archive/draft branches. **P0 merge-consolidation planning** must map ancestry/QA checkpoints and preserve the known same-signer install-over/rollback path; deciding on final merge into `main` requires user approval.

## Original design documents already present

| Purpose | Real repository source | Assessment |
| --- | --- | --- |
| Core game/product decisions | [MASTER_GAME_DESIGN](../MASTER_GAME_DESIGN.md) | Extensive, but contains WS07/WS09-era status passages: decisions vs historical status must be distinguished |
| Overall UI/art bible | [WS14 Visual Style Bible](../../design/visual-system/WS14_VISUAL_STYLE_BIBLE_AND_SPRITE_ASSET_CONTRACT_2026-11-08.md) | Exists, still marked **proposal/unapproved**; update adoption/approval status rather than drafting an unrelated replacement |
| Cross-GridMates design | [Shared identity/motion contract](../GRIDMATES_SHARED_VISUAL_IDENTITY_AND_MOTION_2026-11-09.md) | “Quiet paper, living details”; almost-flat default with optional separate decoration; no `®` and no automatic Sudoku repo editing |
| Actual modular asset plan | [WS16E](../design/WS16E_MODULAR_GRIDMATES_UI_ASSET_PRODUCTION_PLAN_V1_2026-11-09.md) | Existing P0–P3 component/ownership checklist, some statuses superseded by code |
| Painterly role register | [WS16G Batch 1](../../design/visual-system/modular/ws16g-batch1-production-register.json) | **20 roles** (see status reconciliation below) |
| Shared frames & parchment | [modular source package](../../design/visual-system/modular/README.md), [WS16F register](../../design/visual-system/modular/ws16f-source-asset-register.json) | Actual reusable SVG source examples, native Compose analogs; not necessarily production-approved/pixel-identical imports |
| Book sizing/production | [WS16AD Book Factory](../WS16AD_BOOK_FACTORY_CONTRACT.md) | Fixed cover 480×640 / spine 128×640 source geometry; per-book art/rights/progress IDs protected |
| Library decisions | [WS16BI Library cohesion spec](../design/WS16BI_LIBRARY_COHESION_SPEC_2026-11-12.md) | Spine-first, meaningful owned IDs and silent decorative blanks; latest approved v74/v75 phone evidence supersedes early proposed layout |
| Asset transfer and approvals | [Art/GitHub Playbook](../ASSET_CREATION_GITHUB_INTEGRATION_PLAYBOOK.md) | Versioned source, hash, rights, QA-vs-release isolation and owner-specific approval all explicitly required |

**Not forgotten:** a design bible, multiple art-source registers, actual independent SVG masters, source-hashed imports, theme paper tints, book factory and a multi-screen production roadmap already exist. The problem is **canonical synchronization** and release approval, not absence of design thought.

## Audit of the 20-role original painterly register

Current JSON statuses: **8** SVG art roles rendered/QA-verified, **6** action-art roles QA-integrated, **5** painterly master+export pending, **1** composite recipe pending. **0/20 `owner_art_approval=true`, 0/20 `release_approval=true`** within this older register. Statuses may be stale relative to v75 visual acceptance; do not equate the register's zeros to owner disapproval of the entire current UI.

- **Existing rendered/QA art (reuse, don't regenerate blindly):** seven WS shelf parts plus backboard, and Continue/Daily/Together/Quick Play/Full Library/Collection artwork roles; see `design/visual-system/masters/ws16g-batch1/`, `ws16h-home/`, and `Ws16BActionArt.kt`.
- **Five pending painterly roles:** `GM_BG_PARCHMENT_MAIN_V1`, `GM_FRAME_PANEL_LIGHT_V1`, `GM_FRAME_PANEL_FEATURE_V1`, `GM_BTN_PRIMARY_V1`, and `GM_DIVIDER_SHORT_V1`. **These are not five absent UI elements.** Existing native `GridMatesParchmentBackground.kt`, `GridMatesIllustratedFrames.kt`, `Ws16IHomeDecor.kt` and green button painter already provide functional analogs. Compare current phone appearance before deciding any raster replacement.
- **One pending composition role:** `WS_BOOKCASE_ROW_SHELF_V1`. Later `WS16ATModularLibraryScene.kt` and `Ws16BIMountedCabinetShelf.kt` implement live shelves, so this looks like an **outdated register description**, not automatically a missing asset.
- **Existing five home-decoration PNG roles:** `ws16i/decor` top-left, top-right, bottom-left, bottom-right and divider, independently source-pinned, QA-only. Their production register still reads phone review pending despite later owner screenshots.
- **Current Home shelf:** the actual `ws16ab/painted-shelf-v1.webp` is source-hashed in GitHub. Its register explicitly says the **full-resolution original is a separate generated conversation asset, not included in GitHub**; preserve/find that master before promoting as canonical production art. Do not infer the optimized WebP is an editable master.

## Registry and design-debt findings

1. **Empty central WS14 art register:** `design/visual-system/ws14-art-register.json` currently contains `"assets":[]`, despite actual source sheets, QA-generated covers, book variants and later asset registers. Reconcile the central registry or explicitly demote it to a historical/legacy index.
2. **Absent central approval file at active head:** issue [#108](https://github.com/PlayerEden/GridMates-Word-Search/issues/108) points to `design/asset-pipeline/approved-assets.json`; that path is not present on the active head (there is `paperback-v1-tokens.json` in that folder). Determine whether approval was never implemented here or lives only on an older branch before declaring any art production approved. **Do not fabricate approvals.**
3. **Legacy six-entry registry is stale:** `design/asset-registry.json` marks old navigation icons, theme cards and feedback symbols planned/uncreated, while newer native icons, book artwork and QA screens exist. Reconcile IDs to actual runtime paths rather than deleting historical intent.
4. **Art provenance isn't the same as release approval:** `gm-shared-frame-source-register.json`, `ws16f-source-asset-register.json`, `ws16i-home-decor-production-register.json`, `ws16aa-art-register.json` mark various masters as unreviewed/QA-only; passing independent release isolation means test art was *excluded*, not that it was authorized for production.
5. **Naming and typography risk:** prior WS16AD notes flag baked-in unverified trademark `®` and inconsistent letters, while newer WS16AE/AF variants correct some art. Verify **current output pixels** on all three covers/spines; old notes alone cannot prove the problem persists today. Never add `®` unless registration can be verified.
6. **Historical handoffs are too long:** `WS14_ACTIVE_HANDOFF` is ~224K characters and starts at v73; `README`/START_HERE still prominently refer to 2026-11-07/09 APKs. Add a concise current index and treat old sections as historical. Preserve entire evidence history until consolidation is approved.
7. **Potentially forgotten work still tracked:** [#127](https://github.com/PlayerEden/GridMates-Word-Search/issues/127) whole-game screen cohesion, [#128](https://github.com/PlayerEden/GridMates-Word-Search/issues/128) asset and full-device release QA, [#120](https://github.com/PlayerEden/GridMates-Word-Search/issues/120) publishable growing books, [#17](https://github.com/PlayerEden/GridMates-Word-Search/issues/17) Sudoku parity and two-phone LAN acceptance remain open. Do not close them merely because the Home/Library screenshots look good.

## Reuse vs new asset allocation (working recommendation, not art approval)

| Area | Use now / keep | A targeted improvement only if visual QA warrants it |
| --- | --- | --- |
| Home shell | `GridMatesParchmentBackground`, existing light edge washes, native typography | **P1 first image task:** optional subtle textured parchment master/tile and optimized alpha edge wash, generated as independent material, not an entire home screenshot |
| Home decoration | `Ws16IHomeEdgeDecor` five source-hashed botanical/divider sprites | Fit/balance or enhance existing corners; don't redraw merely to mimic the concept |
| Buttons/cards | `GridMatesIllustratedFrames` MEDIUM/FEATURE/PANEL/PRIMARY_PILL/GREEN_ICON, `StorybookVisualKit` | Slight material depth; avoid baking labels in bitmaps or losing dynamic text/high-contrast states |
| Home featured book/shelf | WS16AB painted shelf + current Woodland/Space/Garden cover/spine imports | Source-master archival, edge cleanup and scalable contact shadow only if still demonstrated on phone |
| Full Library | Existing owner-liked v74/v75 high-res cabinet, shelves, spine model, green arrows, page dots | **Freeze current layout.** No extra decorative rails/cards |
| Book opening/modal | Native `BookcaseBookshelfScreen` AlertDialog, shared book cover | **P1 cohesion bridge:** more compact paperback-framed preview, reduce whitespace, retain accessible real buttons |
| Book details/chapters | `PuzzleBookDetailScreen`, genuine cover/emblem, real progress and chapter models | Reuse existing frame/divider and relevant theme motif, don't invent extra book images |
| Gameplay | `BookPageThemePaper` already tints Woodland/Space/Garden; existing `ws14k2` themed motifs | Light per-theme header or paper wash outside word grid; preserve crisp grid, touch traces and word-list contrast |
| Collection/profile/records | Native avatars/fonts/achievements, reusable `GridMatesIllustratedFrame` panel | Bring miscellaneous cards/empty/dialog states to one frame family; no replacement of the 12 original Woodland portraits |
| Daily/Nearby/results/settings/help | Existing functional UI routes and shared colors | **P2 screen-by-screen visual pass**, use native reusable primitives before designing new art |
| Future books/themes | WS16AD book factory, stable catalog and editorial format | New artwork **per approved theme** (cover, spine, limited motif), not a new global style for every book |

**Important boundary:** `GM_SHARED` paper, typography, frames, neutral icons and presentation can later be ported to Sudoku/Crossword by explicit separate approval. `WS_THEME` walnut library, current books, woods/flowers/planets should remain Word Search-specific. The latest concept permits richer Home detail, but shouldn't silently reverse the earlier “flat-first series default.”

## Existing screens and likely gaps to verify

| Screen/state | Source-backed state | Next audit gate |
| --- | --- | --- |
| Home/carousel | native real book carousel and artwork visible in user v75 screenshot | small-phone/large-font/fullscreen, real book tap/back, paper texture A/B |
| Immersive Library | accepted spine-first visual + real catalog bindings, v75 screenshots | keyboard/TalkBack, swipe/disabled arrows, 3/10/100 books, no false locked books |
| Pull-book dialog | functional preview and chapter action, phone screenshot | reduce large blank native dialog, preserve no-overlap and Back |
| Book detail/chapter | real page sizes/progress, screenshot | keep info architecture, unify panel look, save/reopen/completion |
| Word grid + hints/notices | found word and hint observed in v75 screenshot | touch, grid readability, persistent word/hint state, transient toast not covering necessary actions |
| Daily/solo/new puzzle/results | implementations exist in active Compose source | full visual snapshot and state matrix, avoid UI ornament over board |
| Nearby co-op/Race/Duel | implementation/test sources and issues exist | **two physical phones**, reconnection, host claim, stability; no blanket multiplayer approval |
| Collection/Portraits/Fonts/Achievements | real native UI and some reuse | consistent selected/locked states, readable larger text, preserve original art IDs |
| Profile/Records/Settings/Help | code and shared design patterns partly used | screenshots for empty/error/confirm/popup states, screen-level cohesion |
| Quick Play | explicitly labelled `Mixed-word boards · coming soon` and non-interactive in `Ws16BHomeActions.kt` | WS-17 gameplay/content work, **not** just a missing button asset |

## Prioritized next passes and definitions of done

**P0 — documentation + asset truth (no new APK)**

- Crosswalk each active artifact ID/source master/export/apk path/runtime caller into ONE canonical inventory with `scope=GM_SHARED|WS_THEME`, `status=PLANNED|SOURCE_CREATED|QA_INTEGRATED|OWNER_VISUAL_ACCEPTED|RELEASE_APPROVED`, source hash, rights and fallback. Keep the original registers, avoid duplicate approvals and explain deprecated historical IDs.
- Add current status links at the top of README/START_HERE/journal/handoff. Plan the huge stacked draft chain before merge. Reconcile outstanding issue statuses rather than closing blindly.
- Archive/locate missing full-resolution source originals (notably WS16AB shelf) and check current cover/spine raster pixels for typography, seams, alpha and `®`.

**P1 — one coherent visible upgrade (next user-review APK)**

- Optional **more textured Home parchment** compared to v75 baseline: warm cream fibers, minute gold/umber grain, very subtle edge warmth, existing botanical sprites retained. Build independent tile/background material; do NOT rasterize entire screen or place texture over letter-grid cells.
- Use the existing frames to make book preview/detail less plain; prioritize **tighter preview modal** and reuse of chapter/cover accents, not adding layers everywhere.
- A/B render same screens, preserve on-phone legibility, measure memory and verify users' saves. If no meaningful visual improvement, retain existing background.

**P2 — extend and enforce**

- Audit and normalize every secondary screen/state (Home, Collection, Profile, Daily, Nearby, Solo, Results, Records, Settings, Help, errors/dialogs/empty/loading). Prioritize screens with unstyled default cards and visual inconsistency.
- Canonical themed mini-kits for Woodland/Space/Garden: real cover/spine, one compact emblem/heading motif, subtle palette, optional stamped chapter/completion artwork only where actually visible.
- Make reusable component tokens and optional illustration behavior explicit, including disabled/focus/pressed/200% text/landscape.

**P0 beta/release gate (separate from visual polish)**

- Exact approved production asset/rights ledger, zero debug prototypes in release, persistent save/install-over, real Android instrumentation execution, performance, 320dp/200%/landscape/TalkBack and physical two-phone LAN, device-safe backups, source branch consolidation and rollback. Distinguish source compile/CI from device QA. Issue #128 lists these gates.
- The QA test volumes are not automatically editorially publishable books; issues #120 and WS10 content gates still apply.

## Current status and handoff

- **Visual direction:** owner likes v74/v75 walnut Full Library; freeze it.
- **Best next design:** a small texture-quality *Home* pass + paperback preview/dialog harmonization, after canonical asset crosswalk.
- **No new image was generated and no APK was built in this audit.** No changes to saves, books, LAN, Sudoku, test signing, art imports or runtime code.
- **Evidence limits:** audited repository tree, representative Kotlin callers, original design documents, a set of key JSON asset registers and open issue contracts; not a full manual pixel review of every source raster, not every screen tested on device, not a proof of 100% complete provenance.
- **Rollback:** v75 verified QA Android artifact [#14129808202](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43635464423/artifacts/14129808202), v74 earlier art/layout candidate [#14130100171](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43634068542/artifacts/14130100171). Do not uninstall/downgrade in a way that might discard saves.
- **Next exact action:** crosswalk the 20-role register and live runtime imports into a status-owned asset inventory, then prototype one **optional richer Home parchment tile** and one **compact book-preview frame** as a focused QA APK. Do not refactor gameplay or merge PR #131 as part of this task.
