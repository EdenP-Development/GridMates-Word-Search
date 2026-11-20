# WS44 Quality Lab — Score and hidden-side-word prototype (v105 QA candidate)

**Date:** 2026-11-19. **Starting point:** exact WS43 PR #154 cumulative head `4abdeb4a9a2645a0bba58e241bc29f7a864eaa77`, current verified v104 APK source `f2304c5d81a57cc1a29197277702d92645460c96`. **Status:** isolated draft PR candidate; CI and device acceptance must be recorded separately.

Owner direction: parallel Game Design chat may implement and document meaningful functional ideas, provided no Work branch is overwritten, no unapproved release/merge is made, and Work can inspect/cherry-pick/improve the changes. This first pass implements concrete scoring and rewards for genuine unlisted words using the existing board, not an external dictionary or fabricated bonus targets.

## Rules (proposed QA balancing, not final)

| Event | Points |
| --- | ---: |
| Each **listed target** found | +100 |
| Each unique eligible **unlisted side word** traced | +15 |
| Completed puzzle | +100 |
| Using hints / elapsed time | No points deduction |
| Max side-word discoveries | 12 distinct words per puzzle |

The side word must appear as an actual straight line of **4–16 contiguous board letters** (forward, backward, horizontal, vertical, diagonal), be present in an explicitly curated allowlist, and **not be any word already in the target list**. Re-tracing a previously discovered side word earns nothing. Bonus discoveries do not change target progress, finish a board, grant portrait/achievement unlocks, credit currency, or change nearby Race/Duel outcomes. Claims after board completion cannot generate extras. Users can ignore the feature without losing anything.

In **QA/debug**, eligible bonus words come from the 670-answer mixed pack already used in Quick Play plus the original three built-in theme lists. In release, that QA pack is excluded; only the original built-in words can supply extras until editorial approval. The internal 670 are **not owner-approved public dictionary entries**. No network, account, ads, or cash.

## Source integration

- New pure `core/.../BonusWordRules.kt` validates traced letters and whitelist matching, returns FOUND/ALREADY_FOUND/FULL/INVALID, verifies saved side discoveries against actual board occurrence and targets, and calculates points deterministically from counts.
- `SoloRun.bonusWords` and `SoloResult.bonusWordCount` are optional additive fields. Score is **derived**, not a mutable wallet. Old completed results get a consistent 100-per-target plus completion base by calculation; their side-word count defaults to zero. No achievement or save IDs changed.
- `SoloStore` appends optional `sideWords` to **existing schema 6** active/paused/Daily saved runs and optional `extraWords` to completion records. Old JSON encoding remains free of new empty keys. Malformed, duplicate, missing-on-board or target-text extras are rejected in import validation, preserving existing corrupted-save safety. Atomic save-before-confirmation and rollback are used for side finds.
- In `WordSearchScreen` **only** an INVALID listed-target selection triggers a side-word attempt. This preserves existing listed target, repeated target, drag/tap, hint and co-op claim semantics. Show live score in existing progress detail, a small +15 notification and final completion points/extra-word count.
- Show score in Solo/Quick Play Records and in local text share. Daily completed panels show its live score/bonuses, while legacy daily journal behavior is unchanged.
- **Book page practice/progress and Nearby Co-op/Race/Duel are deliberately out of scope**. They have different session stores and multiplayer authority; never add client-side bonus claims to a competitive protocol without an authenticated/versioned design. Future Work may unify scores across those after user acceptance.

## Regression gates

1. `BonusWordRulesTest` includes valid on-board side word, listed target denial, invalid/non-straight, reversed-unrecognized, duplicate, cap, point math, and malicious saved-word validation.
2. `SideWordScoringAndroidTest` creates real frozen V5 Quick Play boards, locates a genuine curated unlisted word, asserts target count unchanged, replay denial, exact atomic restart and full backup restore, corrupt import rejection, final points/record and denial after completion. **Compilation in CI is not physical Android execution**; run the instrumented test where supported.
3. Both Android and independent art/release workflows include `test_ws44_side_word_scoring.py` source contract. Standard Gradle runs core/app unit tests, lint, Android test compilation and signed APK. Art pipeline must confirm QA dictionary stays isolated from release.
4. Validate test APK package `com.playereden.gridmates.wordsearch.qa`, **versionCode 105**, exact v104/v103 signing certificate, install-over save retention, independent artifact equality. No stored game or production release is modified.
5. Owner physically review after Work integrates: bonus find feedback, scoring clarity, long-list/short-screen fit and actual save/reopen. Still pending.

## Work handoff and open questions

- Source branch: `feature/ws44-side-word-bonus-score-2026-11-19`, **stacked on WS43** and not the default `main`; never force-push or mass-merge a currently active Work branch.
- [Independent vocabulary review issue #155](https://github.com/PlayerEden/GridMates-Word-Search/issues/155) remains an advisory task: V5 12x12 eligibility prose mismatch, no per-Fall-subtopic quota, 24 containment pairs, and long-term repetition measurements. **Do not alter frozen V5 recipes or active book IDs to address them in this unrelated scoring pass.**
- Balance questions: points per longer/harder target, score comparison across sizes, hint penalties vs accessibility, if/when score influences profile achievements, whether side words should be colored on the board, and whether Books or Nearby should earn scoring. Default is noncompetitive, tiny bonus.
- This draft is not author approval of any QA dictionary or release art. Keep PR open/draft for Work to inspect, test, cherry-pick and improve.
