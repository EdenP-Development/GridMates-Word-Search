# WS46 — Deterministic bonus-word opportunity and fairness audit

> **Quick status:** Audit code and independent CI data collection in a separate draft branch. Numerical results and both workflow conclusions pending until the exact test SHA finishes. No gameplay code, art, saves, or existing generated boards changed.

## Who, what, where, why
- **Owner request:** Continue helpful gameplay-focused development independently without disturbing ChatGPT Work's larger implementation. Previous user suggestion: award small additional points for legitimate off-list words found inside the puzzle.
- **Implementer:** Game Design chat, source-controlled GitHub branch. CI Kotlin core runner evaluates **actual V1/V2/V3/V5 generated boards**, not assistant-made hypothetical grids.
- **Player problem:** Are there meaningful extra-word opportunities on small, medium and big boards? Does a debug QA 670-word vocabulary make the system feel much more generous than production's narrower built-in vocabulary? Is the +15 reward and 12-word cap sensible in principle?
- **Out of scope:** No claim about *human discovery probability*—measuring available words on a board is not the same as observing players find them. No new word pack, generator change, revised scoring, automatic achievement or currency, network behavior, picture asset or release approval.

## Where we came from
- **Parent verified checkpoint:** [WS45 PR #157](https://github.com/PlayerEden/GridMates-Word-Search/pull/157), exact source `7f521113ebb570e2a313cac4d58c791e24b8d4a5`, stacked on WS44/WS43, QA v106.
- **Android QA:** [run #44652880670](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44652880670): 97 successful CI steps, 14 branch-specific skips; **18/18 Android 35 instrumented tests executed successfully**, including actual bonus-word discovery, unique count, restart, backup/restore and completion scoring. [Native evidence ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44652880670/artifacts/14592626091).
- **Independent art/release QA:** [#44652880657](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44652880657): 93/93 success. Both APKs contained 372/372 identical ZIP members, QA versionCode 106, preserved signature SHA256 `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419`.
- **Owner phone approval:** v105/v106 not confirmed by owner. This is data QA only; no need to reinstall anything.

## Exact WS46 audit implementation
- `core/src/test/kotlin/com/playereden/gridmates/wordsearch/core/BonusWordOpportunityAuditTest.kt` generates **339 deterministic real boards**, using unchanged game engines:
  - **216 V5 / QA 670-answer reviewed pack**: 24 seeds per each of 3 sizes (12×12/15×15/20×20) and 3 challenge levels (Relaxed/Classic/Expert).
  - **72 legacy V1 8×8** boards across Woodland/Space/Garden, 24 seeds per theme (Daily-shaped, not actual calendar dates).
  - **36 V2 15×15 Classic** boards across 3 themes, 12 seeds per theme.
  - **15 V3 25×25 Classic** boards across 3 themes, 5 seeds per theme.
- For each real board, trace up to 16 letters in all **eight valid straight-line directions**, reject listed target answers, and cross-check actual finds against `BonusWordRules.select`.
- Compare potential **QA runtime vocabulary** (reviewed 670-word pack + three legacy word lists) against **release fallback** (legacy built-in themed word lists only). Release editorial review is not assumed or granted.
- Produce one grouped, per-lexicon `TSV`: `group,lexicon,boards,targets,pct_zero,pct_1to3,pct_4to11,pct_12plus,mean,min,median,p90,max,pct_4letter,potential_bonus_points_mean_capped,distinct_words_seen,most_frequent_words`.
- Group by actual source generator, board, challenge, theme; output 2 rows per group; source guard verifies real generation and CI artifact wiring. Both independent CI workflows save `core/build/ws46-bonus-opportunities.tsv` into a GitHub artifact for future Work inspection.
- A handcrafted 6×6 **test fixture** validates that scan matches real claim logic; all other metrics come from genuine unchanged source-backed generated boards. No synthetic board counts are presented as user gameplay data.

## Interpretation and decisions
- The puzzle's **potential** bonus words may include short 4-letter words hidden by the normal target placement and filler; a human might discover fewer or none.
- A board can have zero extra words and still be perfectly playable: the bonus feature is optional. Do **not** rewrite old deterministic board layouts or guarantee a word on every saved board.
- Compare small 8×8/Daily and larger 20×20/25×25 groups. A 12-bonus cap can matter for big boards; no forced quota or rebalancing is approved until the report exists.
- If the QA lexicon dramatically outperforms fallback, prioritize more **editorially reviewed release vocabulary**, not runtime auto-approval of the 670 QA words.
- Future Work can use measured distributions to decide whether to adjust cap, scoring per difficulty, minimum word length, or add hint copy; no changes authorized from this experiment alone.

## Build and QA receipt
- **Current test branch:** `feature/ws46-bonus-opportunity-audit-2026-11-19`, stacked on WS45. Work's active WS43 and default `main` untouched.
- **Build:** Both existing pipelines run core tests and export report; CI source and artifact verification pending. No new QA version number required; **runtime gameplay and signed v106 package unchanged in source**.
- **Fallback:** WS45 v106 fully CI + Android emulator verified; WS42 v104 earlier emulator-verified baseline, still awaiting owner handset approval.

## Outcome and next steps
1. Read both exact-source CI results; download and compare independently generated TSVs, inspect outliers and summarize actual counts. If source test fails, fix and rerun only on this isolated draft.
2. Add a second dated **analysis report with results** and concise pass/fail/limitations to PR and Work issue #140; do not imply physical owner testing.
3. Work can adopt scoring opportunity conclusions whenever ready, while this lab avoids source collisions.

**New-chat one-liner:** Continue from WS46 isolated bonus opportunity draft, verify SHA and both QA TSV artifacts, summarize 339 actual generated boards vs two lexicons; do not change Work's development branch.
