# 2026-11-18 — WS43 Fall vocabulary for editorial QA

**Authoring checks passed.** The Fall pack is ready for page planning and owner editorial review.

## Starting point

The owner asked to continue after the Word Search status review. The current roadmap called for the next 100–250-word Fall batch. Codex worked from the WS42 handset branch at `7877f6d123701bf94ef54083d7909e31ec969985`. The prior verified APK when the pass began was [signed v103](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44236302499/artifacts/14397980688).

## Content

- [Fall authoring catalog](../../data/lexicon/authoring/fall-v1/README.md) has six 25-word groups covering foliage, crisp weather, harvest, wildlife, seasonal food and traditions. It includes 120 familiar and 30 intermediate answers.
- The 150 distinct answers add 117 canonical IDs and reuse 33 approved QA catalog rows exactly. Each answer has a Fall subtopic and a `seasons/autumn` assignment.
- The frozen `en-fall-v1` snapshot has content SHA-256 `08ed614190c0ec4b1071aac4ab518f1fa91d457aa5443632c0c39924350b621c`. This path is for authoring. The Android runtime reads the previously tested 670-word edition.
- 144 answers meet the 12-letter budget, 149 meet the 15-letter budget and all 150 meet the 20-letter budget.

## Verification

Source `5c279ae9fde53bb95718ae84f1e82807ea1e67cc` is in [draft PR #154](https://github.com/PlayerEden/GridMates-Word-Search/pull/154). [Lexicon CI](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44251550015) passed all 28 authoring tests, including byte-identical export and canonical ID checks. The same tests passed locally. The 17 recovered source fixtures matched their pinned hashes. The original QA export and its Kotlin and core-test projections reproduced byte for byte. The current signed Android build is [v104](2026-11-18_WS42_HANDSET_VERIFICATION.md).

## Next task

Plan Fall chapter and page recipes from the reviewed subtopics. Check repeated stems, directions and target coverage with the existing V5 generator at 12×12, 15×15 and 20×20 before introducing a playable QA volume. Matched cover and spine artwork and owner editorial review remain open.
