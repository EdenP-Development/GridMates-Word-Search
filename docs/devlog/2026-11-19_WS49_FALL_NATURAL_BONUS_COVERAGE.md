# WS49 — Natural bonus opportunities from Fall QA vocabulary

> **Status:** New isolated test-only comparison for the same 339 generated boards. Source, independent CI report outputs and numerical gain remain **pending exact-source CI**. No APK version, runtime dictionary or game rule changed.

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
