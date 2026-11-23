# WS48 — Natural bonus discoveries, no planted-word requirement

> **Status:** Isolated test-and-design pass. CI pending for this exact change; previous WS47 Android and independent art runs passed. No new gameplay build version, release permission or owner-phone acceptance claimed.

## Who / why
- **Owner direction (Nov 19):** Extra valid words should be recognized if they arise naturally in an ordinary generated word search. Do **not** force bonus words into the generator or require a quota.
- **Implementer:** Game Design chat, independently of the paused Work chat.
- **Scope:** Test-source Kotlin regression and written implementation contract for an existing scoring candidate. No runtime behavior edited.
- **Problem discovered by WS47:** Most naturally occurring extra words are placed-target fragments or crossings, not completely independent routes. Independent-only scoring would disappoint players and contradict the owner's preference.

## Starting checkpoint and safe fallback
- **Starting isolated parent:** WS47 [draft PR #159](https://github.com/PlayerEden/GridMates-Word-Search/pull/159), latest head `5e7393dbb39d8394529522039cf590b2ba83f94c`, whose last executed geometry-test SHA was `ae3ab61f6826a8535609736d0e910bf8126469f7`.
- **Earlier exact-source evidence:** [WS47 Android/core #44657363467](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44657363467) and [independent artwork #44657363518](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44657363518) succeeded and produced byte-identical opportunity/geometry TSVs.
- **Gameplay fallback:** WS45 v106 QA [18/18 executed Android tests](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44652880670), physical owner approval pending. WS42 v104 is the earlier verified general game baseline. Never erase a local save or silently auto-merge this stack.

## Player-facing policy (clarifies, does not yet alter runtime)
1. Generate ordinary target word-search puzzles **as before**. No mandatory hidden words, reserved bonus cells, deliberate extra-word placements, quotas, forced regeneration, or guarantee of a bonus on every board.
2. When the user traces a legal straight-line path of 4–16 letters in any of eight directions, recognize it as an optional extra **if and only if** the word belongs to an editor-approved active lexicon, is not a required target text, has not already been recorded in this puzzle, and the 12-unique-bonus cap is not reached. Mere chance letter strings are not automatically valid English words.
3. Accept valid target subword fragments (forward/reverse), incidental crossings and fully separate paths **equally**. Geometry classifications in WS47 are **research metadata, not eligibility tiers**; no route-dependent point reduction.
4. Retain `+100` required word, `+15` unique approved extra, `+100` completion, and no hint/time penalty. Extras never fulfill target requirements, count as achievements, grant currency or alter Nearby Race/Duel/book-page authority.
5. Old boards, save IDs, score history, schema, and completed records remain stable. A 0-bonus board is legitimate. Do not present bonus discovery as required or promised.
6. QA's 670-word mixed pack is **not** a release-approved bonus dictionary. Prefer editorially expanding release vocabulary and inspecting natural paths; no raw 36k candidate auto-approval.

## Actual repository change
- New `core/src/test/kotlin/com/playereden/gridmates/wordsearch/core/NaturalBonusDiscoveryPolicyTest.kt` constructs a six-by-six fixture containing only one deliberately placed target (FLOWER). It checks naturally readable FLOW, backwards WOLF, crossing ROSE and unrelated DOGS; verifies target exclusion, lexicon gate, duplicate prevention, 12-word cap, and unchanged exact score arithmetic.
- This test is added to the normal Kotlin `:core:test` discovery path with no new workflow definition or Android version code. The previously existing WS47 geometry test and WS46 full 339-board scan remain unchanged.
- No app/core **production source**, generators, released packs, saves, art, or multiplayer touched. Work branch and `main` untouched.

## QA, blockers, and next action
- **New WS48 tested commit and workflow result:** Pending until GitHub Actions runs on this one-change branch. Do not borrow WS47 green status as proof for this new test.
- **APK:** None requested or released in this pass; a CI debug package, if built automatically, does not imply owner installation.
- **Next single task:** Verify WS48 source SHA and both workflow conclusions; read the Kotlin test outcome. Then inventory eligible *naturally occurring* words by board size against **editorially reviewed** lexicon candidates—without adding bonus placement to generators.
- **Work handoff:** [Issue #140](https://github.com/PlayerEden/GridMates-Word-Search/issues/140); review this policy after WS47 and before deciding whether WS44/45 scoring should be integrated.
