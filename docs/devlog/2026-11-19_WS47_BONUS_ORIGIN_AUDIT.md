# WS47 — Physical bonus-word origins: fragments vs crossings vs independent routes

> **Owner clarification after this study (WS48, Nov 19):** The owner does **not** want to force or plant extra words. Prefer recognizing valid real words already occurring naturally on normal boards. The proposal below to create a generator with 1–3 deliberately placed optional words is **superseded, not approved**. Fragment-only, crossing and free-path classifications are for analysis and do not restrict existing +15 eligibility. See [WS48 policy and test](2026-11-19_WS48_NATURAL_BONUS_POLICY.md).\n\n> **Status:** Reproducible Kotlin study is prepared in an isolated draft stacked on WS46, numerical results/CI pending. No changes to the playable app, released vocabulary, score amounts, APK, saves or Work's active branches.

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

## Verified findings — both independent CI reports succeeded

**Executed Kotlin/Android test-source SHA:** `ae3ab61f6826a8535609736d0e910bf8126469f7`. All result quantities below were computed from the real `core/build/ws47-bonus-origin.tsv`, not from synthetic playthroughs or a dictionary simulation. No new APK or modified gameplay was necessary.

- [Standard Android/core run #44657363467](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44657363467): **SUCCESS**, 97 successful steps, 18 branch-specific skips, 0 failures. [Research artifact #14593079788](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44657363467/artifacts/14593079788).
- [Independent artwork/release #44657363518](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44657363518): **SUCCESS**, 96 successful steps, one conditional skip, 0 failures. [Independent research artifact #14593184428](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44657363518/artifacts/14593184428).
- Both workflows produced **byte-identical research files**: `ws47-bonus-origin.tsv` (5,628 bytes; SHA-256 `d5281e089265a295b811cb54d664a37e6ada66a7a25b71ee8fe4e3fbbea01887`) and the unchanged original `ws46-bonus-opportunities.tsv` (5,189 bytes; SHA-256 `3686f509174cc690fa3024bd3e12a9c1c9cf36cb8998cdcc68bd726531932a9e`).
- Newly added Kotlin coordinate fixture tests passed as part of `:core:test`. Existing actual Android instrumentation remains separately documented in WS45 as **18/18 executed**, not attributed to WS47.

### Exact grouped counts of eligible unique bonus-word/board opportunities

A word is counted once **per board**, whether physically present one or multiple times. Path category records the most independent path available.

| Board family | Lexicon | Boards | All side words | Inside-target fragment only | Crossing only | Free path available | Boards with a free path |
| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: |
| V5 12/15/20, all difficulties | QA 670 + built-ins | 216 | 635 | 416 | 192 | 27 | 22 |
| V5 12/15/20, all difficulties | Built-ins only | 216 | 145 | 98 | 44 | 3 | 2 |
| V1 8×8 Daily-shaped | QA 670 + built-ins | 72 | 23 | 17 | 6 | **0** | **0** |
| V1 8×8 Daily-shaped | Built-ins only | 72 | 9 | 9 | 0 | **0** | **0** |
| V2 15×15 | QA 670 + built-ins | 36 | 78 | 48 | 29 | 1 | 1 |
| V2 15×15 | Built-ins only | 36 | 22 | 19 | 3 | 0 | 0 |
| V3 25×25 | QA 670 + built-ins | 15 | 151 | 109 | 36 | 6 | 5 |
| V3 25×25 | Built-ins only | 15 | 49 | 45 | 3 | 1 | 1 |
| **All families** | **QA 670 + built-ins** | **339** | **887** | **590 (66.5%)** | **263 (29.7%)** | **34 (3.8%)** | **28 (8.3%)** |
| **All families** | **Built-ins only** | **339** | **225** | **171 (76.0%)** | **50 (22.2%)** | **4 (1.8%)** | **3 (0.9%)** |

**V5 size-only breakdown (QA lexicon):** 12×12 had **2 free-path words in 72 boards**, 15×15 had **7 in 72**, and 20×20 had **18 in 72**. Each represents a potential unique word, **not** player discovery.

**Concrete observed example:** In the 24 seeded Garden Daily-shaped boards, built-in `WOLF` appeared as an eligible side word on **nine** boards. Its placements were fragments along other listed target paths rather than an entirely independent word placement. This is expected to include `FLOWER` read backward, but the group summary reports word identity/geometry class rather than individual target-to-word witnesses; confirm exact paired target witnesses if needed before claiming every occurrence is `FLOWER`.

### Updated interpretation and recommendations

1. **Real independent bonus words are rare in current generated boards.** The previous WS46 conclusion that a broader dictionary would help is true for *any* recognized word, but insufficient if the product goal is finding genuinely separate incidental words. Even with 670 QA answers, only **3.8%** of all 887 candidate word/board opportunities have a wholly separate selectable path.
2. **Do not penalize or remove fragments retroactively.** WS44/WS45 saves and completion record scoring treat all validated extras equally. Changing them would silently recalculate historical scores and reduce already earned recognition. Current +15 is an optional pleasant surprise, not misleadingly a guaranteed independent discovery.
3. **A future versioned generator is the credible route to deliberate free bonuses.** For *newly generated boards only*, consider placing 1–3 untargeted, curated 4–7-letter words in grid space before final filler, recording them as **optional** discoveries outside the main target list; they must never count toward completion or achievement unlocks. Seed/version and snapshot identity must be stable, and older saved boards need no migration. Place via explicit consent/design and run real game QA; **not implemented or approved in WS47**.
4. **Editorial review still matters:** an approved release dictionary can recognize more incidental words and allow safe optional placement. Don't auto-publish the 670 debug QA words because this study passed.
5. **Optional player experience:** label these as bonus surprises, not a required goal, especially for 8×8 Daily boards where this sample has zero free paths.
6. **Scoring policy remains untouched:** +100 listed, +15 recognized extra (up to 12 unique), +100 complete, no hint/time deduction. Nearby Race/Duel and book pages remain separate systems.

**Handoff:** Detailed grouped records live in the two uploaded TSVs (per-group means, fragment-only share, crossing share, free path share, most frequent words). Work can examine, cherry-pick, change thresholds in a future draft and implement a new generator without affecting the sealed WS45 scoring candidate. Current branch is research-only.
