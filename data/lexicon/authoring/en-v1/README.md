# First English QA vocabulary edition

This working catalog is an original Codex editorial selection for owner review, not a human-reviewed production dictionary. The source notice explicitly records `assistant-reviewed-qa-only` and `owner_release_approved: false`. It remains in the debug APK only. The unchanged root lexicon is the exact historical WS10 recovery fixture.

| Group | Answers |
| --- | ---: |
| Woodland | 159 |
| Space | 169 |
| Garden | 174 |
| Everyday | 200 |
| Distinct answers in the mixed pack | 670 |

There are 702 topic assignments because one canonical word can belong to several groups. Those assignments must not be counted as 702 distinct words. Familiar/intermediate tags are editorial QA judgments. Subtopics are authoring groupings, not player-facing scientific classifications. Owner vocabulary review is still required, including spelling/display preferences, theme fit, familiarity, regional variants and repetition in complete book playthroughs.

## Reproduction and compatibility

Run `python3 tools/lexicon/build_qa_curation.py`, then `python3 tools/lexicon/render_qa_runtime.py`. The builder reuses stable canonical IDs from the recovered fixture and exports approved-in-this-QA-scope entries only. Frozen JSON lives in `data/lexicon/packs/qa-v1`; runtime fixtures are checked into the debug source and core test resources. Each projection and source payload has a SHA256.

An exported pack identity cannot be overwritten with different bytes. Corrected/future content receives a new pack ID/version. Never repurpose an old word, puzzle or page identity. The V5 seed recipe includes the full pack hash; portable schema-6 saves carry the exact board, targets, display labels and paths rather than requiring a later installed pack.

V1/V2/V3, existing Daily recipes and legacy IDs remain frozen. V4 is the separate giant-board research engine. V5 is limited to the regular 12/15/20 presets. QA book expansions append pages and preserve their original first nine recipes. QA IDs must never be promoted into production catalog identities.

## Continuing toward the larger library

The long-term target is 10,000 distinct approved answers across 30-plus topics. Continue in reviewed 100–250-word batches. Fall, Jungle, Winter, Summer and Castle are upcoming topic/book candidates, not active books. Curate each family before commissioning matched covers/spines. Re-run content integrity, frozen fixtures, consecutive-play vocabulary/subtopic/length/direction/coverage metrics, portable save tests and actual input checks for each engine/content edition.
