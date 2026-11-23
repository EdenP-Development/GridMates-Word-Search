# GridMates Master Asset System — A. Production checklist

**WS18 • 2026-11-15 • planning/asset architecture, not an Android visual release**

**Decision from owner:** Most currently shipped art is experimental and may be overhauled. Produce an abundant, extensible asset bank for GridMates Word Search, GridMates Sudoku and future GridMates games. Cohesion and reuse matter more than protecting placeholder artwork. Producing extras is desirable; *shipping everything by default is not*. Creative exploration is welcome in the reserve without introducing visual clutter, heavy APKs, or confusing menus.

**Status:** A = actionable plan; B = machine-readable inventory in `MASTER_ASSET_INVENTORY.json`. The existing 60 PNG candidates are in the externally backed-up WS17 visual library, not in GitHub as binary PNGs. Four editable prototype SVGs **are** in GitHub under `design/art-library-v2/vector-prototypes/`. This plan approves *architecture*, not individual artwork placements.

## 0. Roles and deployment boundaries

- **Core:** the universal GridMates brand grammar (paper, green/gold, friendly readable type, control shapes, icons, states, accessibility).
- **Word Search:** bookcases, spines, page motifs, book-related navigation, word-search ornaments, library backgrounds.
- **Theme:** interchangeable artwork motifs, covers, badges and accent tokens; no changed layout or feature logic.
- **Game adapters:** Sudoku (numbers, digit fonts, notes, grid-color palette), future Crossword, Word Search; keep game identity recognizable.
- **Reserve:** speculative and seasonal assets, alternates, unused concepts, future-game visuals; catalogued and recoverable, not shipped.
- **No uncontrolled coupling:** do not make Sudoku depend on Word Search runtime, package IDs, Git history, or proprietary portraits. A future standalone `GridMates-AssetKit` repo or versioned source package can house **Core** once vetted; do not create/overwrite another repo silently.
- **Human review:** owner signs off on a screen/use, not every slot in the catalog. Passing CI or generating a sheet does not approve it for packaging.

## 1. Decision register (locked for preproduction)

- [x] Master layers: `core`, `word_search`, `theme`, `game_adapter`, `reserve`.
- [x] Style anchor: warm cream/parchment, antique gold, deep forest green, slightly watercolor/storybook; typography is rendered natively, never AI-baked into dynamic UI.
- [x] Visual hierarchy: immersive Library > warm Home/Collection/Rewards > clean Profile/Records > restrained puzzle/gameplay.
- [x] Artwork overhaul is allowed; previous art is **baseline/legacy**, not a forever requirement. Preserve regressions and screenshots so changes can be judged.
- [x] Reuse first: one component master plus style/state tokens; avoid redundant independently drawn copies for every screen.
- [x] Build a substantial reserve without default APK packaging.
- [x] Prior WS17 60-candidate library is preserved by exact archive/hash reference.
- [ ] Promote an exact set of candidate UI masters to `approved` after in-context review.
- [ ] Confirm the final **GridMates Core v1** look via side-by-side Word Search and Sudoku examples before deploying across games.

## 2. Source and storage architecture

```text
design/master-asset-system/           # shared planning and registry (current repo)
  PRODUCTION_CHECKLIST.md             # this document
  MASTER_ASSET_INVENTORY.json         # B: canonical planned master IDs
  INVENTORY_GUIDE.md                  # definitions, count, inheritance, examples
  inventory_validator.py              # deterministic checks
design/art-library-v2/               # existing WS17 candidate art + importer + source prototypes
  STYLE_BIBLE.md
  RESERVE_INDEX.json
  vector-prototypes/
# Target layout when binary art is transferred and reviewed:
art-source/
  core/{frames,buttons,tabs,icons,badges,decor,profile,feedback,accessibility}/
  word_search/{books,spines,shelves,cover-slots,word-list,results}/
  theme/<theme_id>/{motifs,emblems,covers,spines,accents,rewards}/
  adapters/{sudoku,crossword,word_search}/
  reserve/{seasonal,experimental,alternates,unassigned}/
art-export/
  android/<family>/                  # ONLY selected, QA-approved optimized outputs
  web/<family>/                      # optional future consumer, not auto-generated for every prototype
```

**GitHub reality:** GitHub connector can create/update UTF-8 manifests and SVG text sources; it cannot take local high-resolution PNG bytes directly. WS17 original binary archive is backed up in ChatGPT Library `/Game Design/GridMates Word Search/GridMates_Visual_Library_v1.zip`; exact SHA256 `68fac0f6a40c7abbb3b75096692e74f016368218ffb2e8085e60a12f7636966e`. Transfer PNGs via a verified normal git/Git LFS upload, not an invented repo URL. All source art needs immutable file hash, prompt/artist attribution, transparent-background QA and rights metadata.

## 3. Milestones (A: what we actually do)

### M0 — Capture, inventory and anchor style (no app changes)
- [x] Collect actual Word Search Home, Library, Collection, Profile and gameplay screenshots; these reveal the legacy contrast between parchment and carved wood.
- [x] Preserve the old paperback-v1 token file. Version the more ornate direction in `design/art-library-v2/STYLE_BIBLE.md` rather than rewriting history.
- [x] Prepare and index 60 separated alpha candidates, 7 source/reference sheets, exact crop/rebuild recipe and 4 editable SVG examples (WS17).
- [x] Establish this production checklist and comprehensive planned inventory (WS18).
- [ ] Inventory both games' *current* UI graphics as `keep / replace / promote / retire` by path, used screen and stable runtime ID. **Do not mass-delete**.
- [ ] Assemble a two-game side-by-side style board (Word Search + Sudoku) proving Core visuals don't force library décor into Sudoku.

### M1 — GridMates Core v1 (foundation first)
- [ ] Finish token dictionary: primary neutrals/accent, paper/ink/green/gold, typography, focus and disabled contrast, elevations/shadows, ornament density, size/shape radii.
- [ ] Implement **one shared frame system** with corner/middle stretch or 9-slice rules; small, large, tall, landscape, portrait and scroll contexts.
- [ ] Implement button/segment/tab/chip families using shared state tokens (normal, pressed, focused, selected, disabled, loading, error where applicable).
- [ ] Build universal semantic icon family at 24/32/48dp; important icons remain comprehensible without gold framing and have content descriptions.
- [ ] Build generic status/tag/achievement/portrait/collection templates; game-specific motifs slot into the center.
- [ ] Build reusable progress/pagination/scroll affordances, hover is optional on touch, preserve Android back.
- [ ] Render sample *actual* Android Compose cards for Home, Profile and Settings; maintain keyboard/touch accessibility.
- [ ] Export only shared approved assets; keep a procedural/native fallback if an image is absent.
- **Exit gate:** cross-game prototype samples, 320dp/360dp/600dp widths, portrait and landscape, Android text scaling 100%/150%/200%, touch >=48dp, normal/high-contrast legibility, RTL/multiline resilience where practical; no new persistent state or navigation regression.

### M2 — Word Search visual identity
- [ ] Define reusable physical book geometry: cover 3:4, spine families 1–3 widths, consistent height/baseline, caps/trim/texture, cover label zones rendered natively.
- [ ] Prepare blank, locked, owned, selected and completed cosmetic variants without changing existing book IDs/unlock rules.
- [ ] Unify carved-wood Library as intentional **Word Search-specific set piece**; reuse its shelf, lamp/light, ivy/corner motifs and arrows rather than redrawing a new library for every shelf page.
- [ ] Map the seven user screenshots to the intended component variants: Home (light panel), Library (ornate books), Collection (medallion templates), Profile (core), gameplay (minimal).
- [ ] Handle tall/short books, empty shelves, filler books, page dots, smooth scroll and narrow screens; preserve book cover readability and spine-first browsing.
- [ ] Design a theme-extension template for covers, spines, shelf accents and medallions without layout changes.
- **Exit gate:** 2–3 shelf pages, first/last book navigable, no art clipping, alpha edge halos absent, no repeated fake title baked in, thumbnails legible.

### M3 — First real placement pass (replace placeholders deliberately)
- [ ] **Collection:** shared tabs, unlocked/equipped/locked state, portrait frame, achievement seal; avoid replacing existing approved animal portrait bitmaps.
- [ ] **Profile + Records:** same frames/icons/counters, not a new one-off theme; correct text hierarchy.
- [ ] **Library:** use 12 additional blank/filler spines; keep active books recognizable and the carved-room aesthetic.
- [ ] **Home:** harmonize feature cards, icons, shelf, compact controls without taking extra scroll space.
- [ ] **Game puzzle:** only light utility restyling outside the letter grid; preserve 16-word list interaction, zoom, tracing and hint targets.
- [ ] A/B capture current/new actual emulator screens; owner chooses acceptance for each promoted family.
- **Exit gate:** screenshot diff of five screens, baseline artifacts preserved, APK identity/version/signing safe, Android UI/regression tests plus physical owner visual check.

### M4 — Theme generator and expansion packs (reuse engine)
- [ ] Define schema `theme_id, motif, emblem, badge, cover, spine, section-ornament, palette, profile-accent, reward-accent`.
- [ ] Finish existing Woodland/Ocean/Garden/Bugs/Space primary motif pack; map names to game content intentionally (Garden and Bugs not automatically identical).
- [ ] Add long-range reserves (Winter, Farm, Desert, Autumn, Night Sky, Spooky, Spring, Summer, Mountains, Dinosaurs, Food, Weather, Travel, Music, Mythic, etc.).
- [ ] Generate a theme batch **using the same frame templates**; variation is primarily central illustration, surface accent, and motif.
- [ ] Record prompts, reference sheets, allowed/license provenance, intended games, SHA256, target density, crop and owner approval.
- [ ] Deduplicate palettes/emblems and reuse corner/metalwork; avoid multiplying PNG files for semantic button states if native tint/Compose covers them.
- [ ] Human review at actual ~48px badges and ~70–100px spine displays, not only 1024px hero illustrations.
- **Exit gate:** at least one theme round-trips from schema -> source -> optimized asset -> view without structural/UI changes.

### M5 — Cross-game adoption: GridMates Sudoku and future titles
- [ ] Compare with **current** Sudoku `PlayerEden/SudokuTogether` main/QA, don't assume earlier Word Search baseline matches its native layout.
- [ ] Extract a portable, versioned **Core** pack manifest and semantic tokens; keep ownership/licensing stable.
- [ ] Test common Home/Profile/Records/Daily icons, statuses, buttons and banners on Sudoku; don't transplant Word Search's wooden room.
- [ ] Keep Sudoku digit glyph fonts, pencil-note visual language, grid strokes and co-op ownership colors Sudoku-specific.
- [ ] Allow future Crossword, Nonogram, Minesweeper, etc. to consume core styles without inheriting unnecessary Word Search-specific assets.
- [ ] Version Core independently (`gm-core/1.x`), with explicit integration adapters and changelog; do not silently update all games.
- **Exit gate:** Core assets render/read accessibly in two different games with same semantic names, no invalid cross-references.

### M6 — Reserve warehouse / unlimited generation with discipline
- [ ] Maintain themes, icons, decals, panels, UX states, seasonal, educational, and alternate concepts in **reserve** even if unused.
- [ ] Generate extras in thematic groups (not random unmatched one-offs) and make thumbnail contact sheets with asset name labels *outside* source sprites.
- [ ] Keep immutable originals and lighter previews; record version/artist/generated flag, origin, edit prompt, license/permissions and checksum.
- [ ] Use tags, reuse score and actual-use notes. Never overwrite the originals when upscaling/cleaning.
- [ ] No need to generate every variant into a bitmap: a core master + state styling can represent dozens of UI appearances.
- [ ] Archive retired/experiments to prevent deletion of potentially useful future designs.
- [ ] Maintain **separate source-bank size vs per-game shipped footprint** budgets and do not inflate the APK just because candidates exist.

## 4. Required metadata / naming

Each *inventory slot* has:
`id`, `scope`, `family`, `variant`, `priority`, `render_type`, `target_games`, `status`, `states`, `source_asset_id`, `notes`.

**Machine ID prefixes:** `gm_core_` universal; `gm_ws_` Word Search; `gm_theme_<theme>_` theme; `gm_adapter_<game>_` per-game; `gm_reserve_` future. Lowercase snake case, stable across renames, no actual file extension baked into ID. Files should use same stem; variants use separate data, not guessed filenames. Runtime strings always native/localizable; game-specific art never becomes a core dependency.

Allowed slot lifecycle: `planned` -> `candidate` -> `approved` -> `integrated`, with `reserve` or `deprecated` as alternate states. **An inventory row is not proof an image has been generated.** Existing WS17 art remains candidate externally archived and is never silently presented as shipped. Existing production art state lives separately in `design/asset-registry.json`.

## 5. CI/QA rules and risk controls

- **Metadata QA:** schema, exact unique IDs, valid scopes/status/priority, theme completeness, source provenance, casing, deterministic sort/count, no unapproved packaging.
- **Art QA:** pixel alpha, canvas bleed, semantic duplicates, crop boxes, color difference, compression, PNG/WebP output, resizing, baseline file hashes.
- **UI QA:** shared component geometry, 320/360/600dp, accessibility fontScale 1/1.5/2, screen reader semantics, 48dp targets, landscape.
- **Regression:** offline gameplay, saved profiles, hints/rewards, unlocked portraits, no UI touch intercept, Android install-over, multiplayer host/join unaffected.
- **Release QA:** new exact APK signer/package/version, independent art/release, asset inventory diff and screenshots; preserve last known-good QA APK and owner local data.
- **No premature claims:** planning files are source-only; passing a checklist validator does not verify generated art, APK size, user playtest, or any other game.
- **Versioning:** change the Core contract only when consumers explicitly opt in; nonbreaking icon additions minor version, style redesign major, transparent export fixes patch.
- **Owner acceptance:** visual overhaul is permitted globally but promotion to live UI is explicit per family or screen after A/B preview; allow fast rollback.

## 6. Cross-game handoff order

```text
Asset idea
  ↓
Inventory slot + shared/group scope
  ↓
Source / prompt / rights + reference palette
  ↓
Candidate PNG/SVG + hash + preview
  ↓
Technical inspection (alpha, scaling, dedupe, size)
  ↓
Side-by-side live screen mock-up
  ↓
Owner approval
  ↓
Game-adapter integration + automated QA
  ↓
Signed APK / physical acceptance
  ↓
Integrated master registry + changelog
```

**First practical implementation slice after this documentation:** GridMates Core card/button/icon masters + Word Search Collection tabs/badges + Library blank spines; compare current Home/Collection/Profile/Library screenshots before replacing art. No new APK is required for this planning step.
