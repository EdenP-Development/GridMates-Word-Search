# WS47 — Physical bonus-word origins: fragments vs crossings vs independent routes

> **Status:** Reproducible Kotlin study is prepared in an isolated draft stacked on WS46, numerical results/CI pending. No changes to the playable app, released vocabulary, score amounts, APK, saves or Work's active branches.

## Why this pass / owner request
The owner proposed rewarding valid extra words outside the target list (+15) and asked this Game Design chat to keep implementing helpful work in parallel with Work. The previous [WS46 339-board opportunity audit](2026-11-19_WS46_BONUS_OPPORTUNITY_AUDIT.md) established that the available bonus vocabulary, especially on 8×8 and under built-in-only release fallback, is sparse. It did **not** determine how much of the available list is naturally derived from a listed target's letters. WS47 answers that question without changing existing V1/V2/V3/V5 board generation.

## Baseline and rollback
- Starting exact SHA: `5eb67d81662f3ea79d8a6f28af5b39177ef5aaa0`, [WS46 draft PR #158](https://github.com/PlayerEden/GridMates-Word-Search/pull/158), stacked on WS45/WS44 and WS43.
- Existing [Android WS46 #44655441868](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44655441868) and [independent art #44655441713](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44655441713) both succeeded. Their 339-board original opportunity TSVs were byte-identical.
- Last verified gameplay/scoring candidate is WS45 **v106**, [Android CI + 18/18 executed Android tests #44652880670](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44652880670); owner phone test still outstanding. No v107 build is planned for this audit.

## Exact analysis semantics
A **candidate** is a unique 4–16-letter word from the same curated QA 670+three-theme built-in dictionary used in WS44, physically present as a selectable straight line in any of eight directions, and not itself a listed target word. The smaller **built-in-only release fallback** is analyzed independently.

For every eligible word we scan every physical occurrence, then assign the **least target-dependent available route**:
- **Fragment only:** Every discovered path is a contiguous *forward or reversed subpath of an actual placed target*, e.g., `WOLF` backward within an actual `FLOWER` placement, or `FLOW` within `FLOWER`.
- **Crossing only:** At least one path is not such a subpath but intersects one or more grid cells occupied by any target; none avoids target-letter cells entirely.
- **Free path available:** At least one occurrence uses **zero target-letter cells**. This takes priority, even if the same word is also embedded within a target elsewhere.
The three classifications partition all **distinct eligible words** exactly once; they are potential geometric paths, **not what the player actually traced** or an assertion of dictionary quality. Not all target-substring relationships correspond to player-visible bonus selections; we verify the actual physical coordinates and compare eligible sets to the proven WS46 `BonusWordRules` test.

## New source/test/CI work
- `core/src/test/kotlin/com/playereden/gridmates/wordsearch/core/SideWordOriginStudy.kt`: test-source-only coordinate classifier; **never ships in APK**.
- `core/src/test/kotlin/com/playereden/gridmates/wordsearch/core/SideWordOriginStudyTest.kt`: controlled coordinate-grid cases proving real forward/reverse fragments, single crossing and a fully separate path; also tests repeated independent occurrence outweighing embedded occurrence.
- Existing `BonusWordOpportunityAuditTest.kt` enhanced to classify the **same 339 real seeded generated boards** from WS46: 216 V5 12/15/20 all three difficulties; 72 legacy Daily-shaped V1 8×8; 36 V2 15×15; 15 V3 25×25. No second full board generator sweep and no changes to the original WS46 TSV columns.
- New **`core/build/ws47-bonus-origin.tsv`** with 36 category rows across the original 18 board groups × two vocabularies. Includes per-board mean free/crossing/fragment words, % of boards without a free word, percentage of available words in each category, 4-letter fragment share and top fragment entries.
- `tools/asset_pipeline/test_ws47_bonus_origin.py`: scope and source/CI guards.
- Android QA and independent art/release workflows run the test and independently upload both old WS46 TSV and new WS47 TSV for byte-comparison; no Work branch changed and no new APK version needed.

## Acceptance / follow-up
1. Confirm exact source SHA on both successful CI runs. **Inspect actual TSV payloads**, not workflow success alone, and compare byte-for-byte independently generated reports.
2. Report the proportion of free/crossing/fragment-only opportunities on 8×8, 12×12, 15×15 and 20×20 V5 and on older 25×25. **No human discovery claim.**
3. Decide whether substring-only words should be allowed, awarded fewer points, or visibly recognized in an optional help panel, but **do not change existing scoring without owner/Work acceptance**. Preserve existing v106 scores and saved bonus words.
4. Keep release QA vocabulary unapproved until an editorial process explicitly promotes it. No changes to nearby multiplayer or book pages.

**New-chat continuation:** Inspect WS47 draft head + two CI report artifacts, identify where recognition comes from fragments/crossings/free letter routes, document numbers in [PR](https://github.com/PlayerEden/GridMates-Word-Search/pulls), update Work handoff issue #140, and only then discuss a gameplay adjustment.
