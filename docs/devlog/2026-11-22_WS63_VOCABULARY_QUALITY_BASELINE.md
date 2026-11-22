# WS63 — Vocabulary roadmap and executable QA baseline

> **VERIFIED SOURCE-BASED WS63 QA — DRAFT PR #176.** Both independent [WS63 quality #45079091823](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45079091823) and [original WS40 lexical recovery #45079091757](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45079091757) **SUCCESS**, exact tested source `0f50e814e1dc5e667930011d9b5be862d4e7eb2b`. Six new WS63 tests passed. **No signed new APK, words promoted, owner/public approval, existing game/save modification or actual player study.**

## Actual verified baseline, inspected from independent CI artifact

- [Original WS63 QA ZIP artifact #14776092886](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45079091823/artifacts/14776092886), downloaded, ZIP contents independently inspected and SHA-256 verified. ZIP **`f9d8e877086cc541fd5e296a0db11a900ce9d53c37559a47bd6c13bef8e70df7`**; report JSON **`3fe8550c114b82e05c172f1fa550cae0f3ba1d042f219877a34cd6e2ec640ad5`**, Markdown **`eb90032fd24efefc90e4d91d28cb13f2067ef081b401fa716ea98d59ed768909`**. Artifact hash manifest matched both extracted files.
- CI log: `Ran 6 tests in 0.151s` → `OK`; `WS63 QUALITY_AUDIT_OK: 787 unique QA English+Fall; 18 proposed chapter pools; zero newly owner-approved words`; separate source validator assertions also passed. Original WS40 recovery/intake QA succeeded unchanged (not a new dictionary production import).
- **English v1 QA:** 670 unique; Woodland 159 (143 eligible in V5 12×12 Relaxed), Space 169 (144), Garden 174 (159), Everyday 200 (199). Numbers denote approved-for-internal-QA source assignments, **NOT public release**. Fall v1 has 150 QA approved with 112 eligible 12×12 Relaxed, 148 Classic 15×15 and 150 Expert 20×20; 33 overlap English, 117 new: **787** distinct union. English active topics 4; Fall+Seasons add two authoring topics, **six** total.
- **18 proposed chapter source pools** (not actual frozen board measurement): critical **Woodland Birdsong** 29 distinct, V5 Relaxed 12 eligible 23, Classic/Expert 29, **minimum 23 unavoidable repeated slots** among 52 three-page answer slots; **Space Our Solar System** 44 distinct, 39/44/44 board eligible, **at least 8 reused slots**; **Garden Flower Borders** 44 distinct, 36/44/44 eligible, **at least 8 reused slots**. Next shallower **Night Sky** 54 distinct; **By the Stream** 65 **but old category is too broad**, not proof of chapter quality; Quiet Clearings 60 with broad category overlap concerns. All 18 complete results stored in report.
- **190 substring-related QA canonical pairs** flagged for per-board/expert editorial review, not 190 invalid words. Zero simple plus-S plural pairs under a narrowly scoped heuristic, **not** proof no morphological variants or problems exist.
- **Source-grounded next queue:** [WS63 first editorial priorities](../WS63_FIRST_EDITORIAL_PRIORITY_QUEUE_2026-11-22.md). Review existing **23 unapproved WS51 Birdsong**, **33 unapproved WS54 Stream**, and WS55/WS56 manuscripts before inventing replacements. Create dedicated high-quality candidate-only Solar System and Flower Borders lists; no owner-release authorization. Do not silently change current 81 frozen QA pages or WordSearch V5.
- **Unchanged:** all original 63 historical + 18 WS57 frozen QA pages, QA v108 APK, saved games and generator versions, Daily, Quick Play, Nearby, images, bonus and reward system, root lexicon fixture, original SCOWL quarantine, 122 Woodland editorial candidates, production release rights. This is content analysis, not a physical Android or two-phone test.

**Next exact development milestone:** WS64-A versioned reviewer worksheet for existing unapproved Woodland proposals and WS64-B new Solar System / Flower Borders candidate intake (source and duplicate checks, no automatic approvals). Then WS65 new QA-only versioned pack + actual V5 board generation and editor/owner reviews. Keep WS63 PR draft/unmerged.

## Owner directive

Expand GridMates: Word Search vocabulary **GOOD, VAST, ALL THE WORDS**, without the awkward filler, duplicate roots, generic off-theme book chapters or misrepresented production approvals. The user explicitly requested a comprehensive roadmap and actual development start and suggested reusing research from new [GridMates: Grid Hunt](https://github.com/PlayerEden/GridMates-Grid-Hunt). Keep Word Search's own game/source, don't import Grid Hunt's map engine.

## Prior verified starting point

- WS60 signed QA code108 [Android/core #45065450259](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45065450259) and [art/release #45065450280](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45065450280) SUCCESS (200% readable book page, portrait/landscape 4/4).
- WS61 [Android #45067303054](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45067303054), [art/release #45067303073](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45067303073) SUCCESS, real same-signed code107→108 emulator install-over preserved 6/6 raw private save files and decoded original puzzle progress.
- WS62 [Android #45068863442](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45068863442), [art/release #45068863479](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45068863479) SUCCESS, 3/3 actual TCP Co-op/Race/Duel modes between two runtimes in ONE emulator. **Two real Wi-Fi phones remain unverified**.
- First English QA vocabulary 670 internal-QA-approved words, 702 assignments, four active topics, separate Fall v1 150 words with 117 newly distinct IDs and 33 reused. This was **not** owner/public release approval; root WS10 recovery fixture still 40 draft words and 0 approved; ESDB quarantine 36,213 roots unreviewed.
- 63 original + 18 new frozen QA book pages are save-sensitive; new Woodland manuscripts have 122 candidate terms not publicly approved. No QA-only words secretly become production content.

## Work authored in WS63

- [Master roadmap](../WS63_MASTER_VOCABULARY_EXPANSION_ROADMAP_2026-11-22.md): staged WS63–WS72 1K/3K/5K/10K editorial, bookmaking, engine/versioning, real UX and signing.
- [Editorial rulebook](../WS63_EDITORIAL_AND_WORD_QUALITY_STANDARD_2026-11-22.md): en-US sense/fit/familiarity/source rights, canonical ID vs assignments, per-game eligibility, required explicit owner public approval.
- [Grid Hunt crosswalk](../WS63_GRID_HUNT_RESEARCH_TO_WORD_SEARCH_CROSSWALK_2026-11-22.md): research-only GM03 hard vs soft QA, GM05 spaced variety, GM06 optional story, GM07 Quick/Custom, GM08 observation categories, provenance without copying mapping engine.
- `tools/lexicon/ws63_quality_audit.py`: pure Python stdlib, **read-only**, first runs existing `validate.audit` on en-v1 and fall-v1 and rejects cross-edition canonical ID/answer drift. Reports QA exact distinct union and reuse, per-active-topic tier and V5 12/15/20 eligibility, 18 existing chapter recipe pools, unavoidable 52-slot reuse lower bounds, plural/substring soft review flags, file-level SHA256 and unequivocal 0 new public approvals.
- `tools/lexicon/test_ws63_quality_audit.py`: typed seed cases (palindrome, tier/length), cross-edition fatal ID/answer conflicts, legitimate substring/plural soft flags, exact expected English 670/Fall 150/117 novel/787 QA union and 18 chapter coverage, determinism/read-only output.
- `.github/workflows/ws63-lexicon-quality.yml`: isolated PR-triggered Python QA (no new external dictionary downloads), original WS10 recovery provenance audit, root+en+Fall schema/source validation, six explicit WS63 cases, JSON+Markdown report + SHA artifact. The older WS40 workflow can also run since `tools/lexicon` changed.
- **No edit to:** candidate CSV values/approval states, any packaged wordpack, Android Kotlin, game modes, save schema, IDs/hashes/seed/frozen pages, assets or public release configuration.

## Original pre-run contract (historical; superseded by verified receipt above)

1. WS63 GitHub action for same source SHA successful; existing WS40 lexical recovery workflow and normal application/art checks unaffected. Run targeted tests and archive `qa-ws63-quality/quality-baseline.json` and `.md`.
2. Verify genuine source metrics: **English 670**, **Fall 150**, **Fall new 117**, **union 787**, **six active authoring topic families across these two editions**, **18 proposed chapter recipes**. 787 is **QA unique union**, NOT owner approved or currently playable in one version. If data differs, investigate source instead of silently changing numbers.
3. Audit shows exactly how shallow chapters are and which proposed topic/size/tier can form a board. Treat lack-of-depth as soft editorial priority, **not a generator success claim**. No proof of UI or actual player enjoyment from this report.
4. Source QA report records no newly owner-approved entries; no public release, merge, phone install or background task created.

## Next action after successful WS63 exact-source CI

Start **WS64** with human-readable review queues from *actual lowest-depth chapters*, then carefully propose 100–250 candidate additions to birds/streams/clearings and other current books, preserving existing IDs/saves and tracking source+license. Follow with actual V5 board placement and book QA, actual handset visual review, then move to new Ocean/Insects/Navigation and 30+ topical coverage. Publish only after owner editorial/rights signoff; no automatic approval of quarantined dictionary roots.

## New-chat handoff line

[WS63 DRAFT PR #176](https://github.com/PlayerEden/GridMates-Word-Search/pull/176) contains the full 10K-word roadmap, editorial rubric, Grid Hunt crosswalk and read-only en-v1/Fall audit are written on `feature/ws63-lexicon-quality-roadmap-2026-11-22`, stacked on WS62 verified DRAFT #175. **Find WS63 draft PR and its exact CI status**. Verify 670/150/117/787 counts, 18 recipe coverage, then record exact status and pick WS64 chapter deficits. WS60 v108 is still last independent owner phone QA APK; no new installation needed; 122 Woodland candidates remain unapproved.
