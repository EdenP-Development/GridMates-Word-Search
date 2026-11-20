# WS49 — Natural bonus opportunities from Fall QA vocabulary

> **Status:** **VERIFIED RESEARCH.** Both independent CI workflows succeeded at exact test SHA `57632ca1e8b08ace516ae76e2e0a2ba13f89e970`; all four generated TSV payloads match byte-for-byte. No APK runtime, dictionary, generator, save or game rule changed.

## Verified actual WS49 results — two independent CI artifacts

**Research code tested:** `57632ca1e8b08ace516ae76e2e0a2ba13f89e970`. [Standard Android/core run #44663712025](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44663712025) **SUCCESS**; [independent artwork/release run #44663712042](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44663712042) **SUCCESS**. Both ran the three WS49 source-contract tests, Kotlin/core tests and their existing build gates without failures. No WS49-specific Android emulator instrumentation or physical owner testing was performed; the older WS45 v106 Android 18/18 actual-test evidence is a separate checkpoint.

- Standard report ZIP [artifact #14597231420](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44663712025/artifacts/14597231420) and independent report ZIP [artifact #14597071925](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44663712042/artifacts/14597071925) each contain **four internally byte-identical TSV files**; ZIP container-level hashes are different due to archive metadata.
- Original WS46 `ws46-bonus-opportunities.tsv`: 5,189 bytes, SHA-256 `3686f509174cc690fa3024bd3e12a9c1c9cf36cb8998cdcc68bd726531932a9e`, identical to earlier WS46/47 proof.
- Original WS47 `ws47-bonus-origin.tsv`: 5,628 bytes, SHA-256 `d5281e089265a295b811cb54d664a37e6ada66a7a25b71ee8fe4e3fbbea01887`, unchanged.
- New `ws49-fall-natural-increment.tsv`: 1,343 bytes, SHA-256 `2ee2cafa3f087e21f712ea42aa9534681b40736dd1eae1e7b8302c7a0b45206e`.
- New `ws49-fall-candidate-frequencies.tsv`: 432 bytes, SHA-256 `e2cdef3c3a1ff218c5a418973205391f038a70163b56066122cb883251f1c2cb`.

**Exactly 339 actual, unmodified generated boards:** 887 baseline QA+built-in *distinct bonus-word/board opportunities* increased to **933**, gaining **46 opportunities (5.2%)** from 150 existing Fall QA words. **43/339 boards (12.7%)** gained any additional recognized word; **296/339** gained none. These are physical opportunities, *not player-observed finds*, and may coexist on the same board.

| Generator family | Boards | Before | Increment | Boards gaining | Fragment | Crossing | Independent |
| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| V5 12/15/20 | 216 | 635 | 32 | 31 | 23 | 9 | 0 |
| Daily-shaped V1 8x8 | 72 | 23 | 0 | 0 | 0 | 0 | 0 |
| Legacy V2 15x15 | 36 | 78 | 9 | 7 | 6 | 2 | 1 |
| Legacy V3 25x25 | 15 | 151 | 5 | 5 | 3 | 2 | 0 |
| **All** | **339** | **887** | **46** | **43** | **32** | **13** | **1** |

**Only eight distinct newly recognized Fall words** appeared across the 339 boards: CORN (30 boards; 27 fragment/3 crossing), CHAI (4), STEW (4), CLOVE (2), GOOSE (2), WOOL (2; one free path), AMBER (1), MIST (1). The 150 Fall answers have 33 overlaps with the current 670-answer mixed pack; 117 are new to the existing QA+built-in set, of which 116 satisfy 4–16 bonus length. Most new matches were short fragments; no independent new Fall match on V5 or 8x8 sampled boards. Every match is still one optional +15 when actually traced; no category has different payout.

**Decision:** The owner does not want planted bonus words. Do not expand or release the Fall QA pack merely for the bonus feature based on this modest incidental gain. Editorially explore broader **familiar 4–7-letter everyday vocabulary**, with explicit owner release approval and source/license checks, and validate it against unmodified boards before considering future dictionary versions. Do not claim any newly identified word is editor-cleared for public release, do not modify the generator, and do not retroactively change score history.

## Owner request and context
- **Decision:** The player wants the generator to produce ordinary word-search puzzles. Recognize valid extra words that arise **naturally**, not intentionally planted bonus words. WS48 [PR #160](https://github.com/PlayerEden/GridMates-Word-Search/pull/160) and [owner-policy record](2026-11-19_WS48_NATURAL_BONUS_POLICY.md) prohibit forced hidden-word quotas.
- **Last checkpoint:** WS48 Kotlin natural-discovery regression passed standard Android/core [CI #44662499054](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44662499054) at `c9d3e1f1000ec22f96e3df7f7dfb189dddac571a`. WS47's 339-board geometry comparison passed independently at `ae3ab61f6826a8535609736d0e910bf8126469f7`.
- **Stable actual Android-regression baseline:** WS45 v106, [18/18 native Android emulator tests](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44652880670). The owner's actual phone acceptance remains open.
- **What we want to learn:** Would adding an existing *editorially QA-reviewed but release-unapproved* Fall word batch increase incidental bonuses? By how much, and are its matches primarily fragments, crossings or unrelated paths?

## Corpus integrity and provenance
- Frozen [Fall QA authoring catalog](../../data/lexicon/authoring/fall-v1/README.md) has **150** answers, **33** already present in the mixed 670-word QA pack and **117** new compared with it. All are internal editorial QA, not owner release approved. This is about valid extra-word recognition, not new target selection or Fall book integration.
- Test-only `core/src/test/resources/research-packs/en-fall-v1-nonrelease-answers.txt` deterministically mirrors exactly those **150** answers and is checked against the canonical `data/lexicon/authoring/fall-v1/words_candidate.csv` by a Python source contract.
- Three Fall answers are only three letters (BAT, OAK, FOG), so the 4–16-letter side-word rule excludes them; other words may simply not physically appear.
- No external bulk wordlist is imported. No source word is granted release authorization; QA approval is an editorial stage, distinct from public release rights/owner confirmation.

## Exact trial method
1. Reuse WS46/47's **same 339 actual deterministic generated puzzles**: 216 V5 regular 12/15/20 across three difficulty tiers; 72 Daily-shaped legacy V1 8×8; 36 V2 15×15; 15 V3 25×25. No fresh generator behavior or extra placements.
2. Compare existing `qaDictionary` (670 mixed QA + built-in theme words) against `qaDictionary + Fall150`, respecting listed target text exclusion, actual straight-line 4–16 board coordinates and 8 legal directions.
3. Assert every originally eligible QA word retains exactly the same fragment/crossing/free path category and confirm new Fall words against the **real BonusWordRules.select contract**, not spelling alone.
4. Report `core/build/ws49-fall-natural-increment.tsv` (18 groups, base / net new opportunities, board gain rate, fragment/crossing/free breakdown) plus `core/build/ws49-fall-candidate-frequencies.tsv` (per-new-word board appearances and origin classification, **not an approved dictionary**).
5. Preserve byte-for-byte all existing WS46/47 report code paths and upload all four reports from each independent CI pipeline for comparison. Source guards check both workflow files and the frozen QA mirror.

## Scope, verification and rollback
- Work performed by: Game Design chat, test-source/CI/docs only.
- Isolated branch: `feature/ws49-fall-natural-bonus-coverage-2026-11-19`, stacked on WS48 PR #160; do not merge to `main` or modify Work's active WS43/Fall branch.
- Intentionally untouched: app production code; runtime lexicon; puzzle seeds, targets and saved boards; scoring (+100 target/+15 bonus/+100 completed, 12 cap); achievements and rewards; Android version; artwork; Nearby co-op/race/duel; release gating.
- **At creation:** exact WS49 code/test CI still to execute; no new handset approval. Android and art/release workflows are separate jobs, and success means actual tests passed on source, not proof the player found those words.
- **Fallback:** WS48 green source test / WS45 v106 previously emulator-tested gameplay / WS42 v104 general app source.
- **Next:** Download and compare **both** `WS49` numeric TSV pairs after CI completes; publish grouped actual numbers and carefully choose whether larger *owner-reviewed* dictionaries are worthwhile. Do not force generator bonus placements or automatically promote the Fall QA corpus.
