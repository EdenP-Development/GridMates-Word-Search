# WS53 — Quiet Clearings chapter diversity without rewriting historical pages

> **Status:** **VERIFIED TECHNICAL QA — NOT RELEASE-APPROVED.** Both independent workflows passed at exact tested source `4c11c0b15c9fe9feab6b0275292d9ec43da2a7d7`, and their two generated TSV payloads are byte-for-byte identical.

## Verified execution — two independent pipelines, exact archived grids and measurements

**Exact executed source:** `4c11c0b15c9fe9feab6b0275292d9ec43da2a7d7`. Both [standard Android/core #44758251802](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44758251802) **SUCCESS** and [independent art/debug/release #44758251892](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44758251892) **SUCCESS**; the WS53 Python source/provenance guard passed in both, Kotlin generated and regenerated all three real unmodified-V5 QA frozen boards with all exactly authored 12/16/24 targets, and `BookCatalog` + `BookPageSessionRules` hash identity checks passed. Android/core also passed app/core unit tests, lint and QA APK packaging; art pipeline passed its separate build gates. These automated builds are not owner handset acceptance or release approval.

**Independently downloaded output reports:** [standard artifact #14640291491](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44758251802/artifacts/14640291491) and [independent art artifact #14640912053](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44758251892/artifacts/14640912053) each contain **two literally byte-identical TSV payloads**. Outer ZIP container SHA hashes differ due to archive metadata:
- `ws53-quiet-clearings-frozen-boards.tsv` — **2,032 bytes**, SHA-256 `046b48020f85836b852b65698dc320a600f3d3d97e56dc644aba6d041ca99be7`. All 3 real puzzle letter grids, original target IDs/words, exact per-page hashes, new QA-only pack identity and non-release marking.
- `ws53-woodland-overlap-delta.tsv` — **353 bytes**, SHA-256 `15a148f45b965064b64456b32dc1061fb8fbe4112af8aaba937d5089dda98e87`. Exact comparison of revised Chapter 7 against all four other WS52 Woodland QA chapter proposals and five-chapter total distinct vocabulary.

| New QA-only *Quiet Clearings* candidate | Grid / targets | Difficulty | Actual frozen board SHA-256 |
| --- | --- | --- | --- |
| Page 1 — Moss and Fern | 12×12 / 12 | Relaxed | `abd2c4ca565eef18f503f8cbdc81c6245ee6b4d7dc132dd48fa18a4ae087c4fb` |
| Page 2 — Grove and Canopy | 15×15 / 16 | Classic | `c281ca6ac250c6632622982fb7d25646eb1df4b01a0231f76c86a53f65d3c7f4` |
| Page 3 — Woodland Whispers | 20×20 / 24 | Expert | `5be40bc3ba9b497968c5c94443caaf2c133d2c22199d5cfa418ceda7e7b02ced` |

**Editorial results confirmed in each actual CI report:** The new QA-only Chapter 7 shares **18** full target words with *Along the Trail* (down from 48), **21** with *By the Stream* (down from 33), **26** with *Under the Canopy* (up from 0), and **20** with *Forest Discoveries* (up from 12). Five-chapter distinct answers increase **124→129**; no required target repeats within this three-page chapter. Including the separately tested WS51 Birdsong manuscript, all six researched Woodland Chapters 3–8 occupy **312 target slots with 168 distinct answers**; 23 of Birdsong's additions remain unapproved, so this is **not** a public-release dictionary count. This is improved structure, not a promise of zero whole-book repetition.

**Scope and decision:** No old WS50 mapping or WS51/52 manuscript changed, nor any of 63 playable QA Library pages/IDs, saved progress, release words, generator/runtime, art, profiles, Nearby or bonus scoring. The revised Quiet Clearings work is a **versioned editorial alternative**, never an in-place replacement. **Technical QA passed, editorial release remains on hold** pending human word/theme review, chapter-to-chapter repetition strategy, owner Android visual testing and a separately approved new edition.

## Why this pass exists
The user requested deliberate chapter word lists in collectible, permanently repeatable puzzle books. [WS52](2026-11-20_WS52_WOODLAND_FIVE_CHAPTER_QA.md) successfully generated 15 Woodland chapter QA puzzles, but the broader editorial audit found Chapter 3 *Along the Trail* and Chapter 7 *Quiet Clearings* overlap on **48/52** required words. That is not adequate product variety even though generator tests passed.

## Scope and concrete changes
- Starting parent `1583356d15b54a288090f2ea860d2204771549eb`, documented WS52 QA branch and [draft PR #164](https://github.com/PlayerEden/GridMates-Word-Search/pull/164). Exact WS52 tested Kotlin source `ee3c390fb62813273eff07be4da2e18259c09bf5`; [Android/core #44670927912](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44670927912) and [independent art/release #44670927977](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44670927977) passed.
- New versioned [Chapter 7 TSV proposal](../../data/lexicon/authoring/chapter-recipes-v1/quiet-clearings-ws53-revision.tsv) and byte-identical Kotlin test resource: **52 entirely different full answer identities within the chapter**; original stable IDs and spellings of 159 QA Woodland pack are retained (zero new words). Chapter label remains *Quiet Clearings*, but editorial scope becomes **habitats + trees**, not the previous habitats + trails. Original WS50 chapter map and old WS52 source rows are not edited.
- QA pages *Moss and Fern* (12 familiar words, 4.50 mean), *Grove and Canopy* (16, 5.94), *Woodland Whispers* (24, 8.21), with Relaxed/Classic/Expert progression. All existing QA words and titles need human review for semantic character, but these deliberate lists were not blindly generated from all available words.
- `QuietClearingsDiversityLabTest.kt` checks the proposed source and all historical WS52 scope metrics, then generates three actual QA-only V5 frozen boards, validating exact target IDs, board hashes and BookCatalog/BookPageSession; reports full grids and a per-chapter overlap-delta TSV.
- `test_ws53_quiet_clearings.py` ensures source pack identity, original IDs, no old-map mutation, new five-chapter unique count, and both CI workflow upload safeguards.
- No changes to production game engine, Android screens, BookQaPilot QA/release fixtures, original 63 QA playable Library pages or progress, existing WS51 Birdsong and WS52 research candidates, released dictionary, score/bonus, art or Nearby. No new APK requested.

## Editorial comparison from actual source lists — before new V5 board CI
- Ch3 ↔ Ch7: **48 → 18** shared target words.
- Ch6 ↔ Ch7: **33 → 21**.
- Ch4 ↔ Ch7: **0 → 26** (explicit new tree-category tradeoff).
- Ch8 ↔ Ch7: **12 → 20**.
- Whole five-chapter distinct answers: **124 → 129** from the same 159-entry Woodland QA source; chapter 7 still contains 52 unique words with none repeating across its three pages.

These numbers reflect editorial word lists, not playtest statistics or final book approval. The new pairwise maximum of 26 reduces the most extreme repetition but is still substantial. Three real new frozen puzzle boards must pass actual CI before declaring technical verification. No merge or release before owner editorial/visual review.

## Follow-up after verified technical QA
- **Source/Android/release automation:** Verified above, no failed steps.
- **Owner phone/emulator manuscript acceptance:** Not performed in WS53.
- **Next:** Have Work and the owner review theme fit, actual grids, chapter-wide repetition and explicit new-edition publication/upgrade boundaries before any promotion.

[WS53 plan](../QUIET_CLEARINGS_EDITORIAL_SCOPE_WS53.md) · [Work coordination](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
