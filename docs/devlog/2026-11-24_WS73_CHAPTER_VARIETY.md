# November 24, 2026 — WS73 Chapter variety

The owner asked to continue the playable beta roadmap. This pass deepens Quiet Clearings Expert and Our Solar System Relaxed and lets the owner play three new pages in each chapter on Android. Work and verification are tracked in [draft #194](https://github.com/PlayerEden/GridMates-Word-Search/pull/194).

## Starting point

Signed v115 Home fit in [draft #193](https://github.com/PlayerEden/GridMates-Word-Search/pull/193) had 87 pages across Woodland, Space, and Garden. Its handoff commit was `30feffe9c42b2d78046050f9f0320335fdd728ee`. Its [verified v115 APK ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45329658676/artifacts/14891551505) remains the previous build. [WS72 pass record](2026-11-23_WS72_HOME_FIT.md)

## Playable pages

| Book | Chapter | Book pages | Board and targets |
|---|---|---|---|
| Woodland | Quiet Clearings · Expert | 43–45 | 20×20, 24 each |
| Space | Our Solar System · Relaxed | 25–27 | 12×12, 12 each |

The display catalog appends the difficulty-specific chapters after the existing pages. Woodland, Space, and Garden still occupy three shelf positions and show 45, 27, and 21 pages respectively. Each page uses a new edition and puzzle ID and a pinned frozen-board digest. The debug runtime copies eligible v115 progress into v116 journals on first use, with the previous journals available to v115.

## Vocabulary and generated results

| Chapter | Candidates in the three new pools | Distinct answers across the v115 set | Distinct answers across the v116 set | v115 adjacent repeats | v116 adjacent repeats | Repeat count from the last v115 preview page to the first v116 preview page |
|---|---:|---:|---:|---|---|---:|
| Quiet Clearings Expert | 90 | 54 of 72 | 72 of 72 | 5, 1 | 0, 0 | 0 |
| Our Solar System Relaxed | 72 | 33 of 36 | 36 of 36 | 0, 0 | 0, 0 | 0 |

The fixture generates all six boards with the existing V5 engine and checks that every target occurs once and scores through normal word-claim rules. It encodes and reopens each board and compares the new results to the actual pinned v115 preview boards. The new answer pools are disjoint within each chapter, with one pool per page. The first new pool in each chapter excludes the answers on the last v115 preview page.

The [candidate source table](../../core/src/test/resources/ws73-variety/page-pools.tsv) includes the word ID, answer, display spelling, theme context, and provenance per row. Quiet Clearings reuses 11 entries from the chapter preview, 27 from internal English QA and 3 from internal Fall QA, and adds 49 suggestions. The Solar System pools reuse 35 preview, 28 English QA and 1 Fall QA entries, and add 8 suggestions. These are QA word packs. The [frozen-board index](../../core/src/test/resources/ws73-variety/expected-boards.tsv) pins the six exact outputs.

## Build and verification

The initial evidence run at `081552c514c697e34389b5228323482f480aa1aa` produced the six digests from the actual generator. [Evidence workflow](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45426385236) · [Initial report](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45426385236/artifacts/14937255550)

The fixture-only [Android run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45426384476) was superseded by the application integration commit. The signed v116 source is `462792ed3a6e553a9a2eb72fb9d4be8b8bcb413c`. All three runs passed at that source.

- [Android build, unit tests, lint and signed emulator install-over](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45427261820)
- [Exact-board replay after digest pinning](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45427261805)
- [Debug build and release isolation](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45427261919)

The signed v115 installation created two Space bookmarks and saved a word in an old Solar System puzzle. After an in-place upgrade, the Android emulator reopened that same page with the saved word, verified the order and identity of all 87 existing pages, and started Woodland page 43 and Space page 25 from the chapter picker. A word traced on each new board persisted in the v116 session journal. The original v115 progress and session journals matched byte for byte after play. All 237 v115 packaged assets also matched byte for byte. The v116 APK adds six frozen snapshots and their index. The release package excludes both sets of preview assets and the preview code. [Test logs, screen captures and exact answers](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45427261820/artifacts/14938971052)

| APK field | Verified value |
|---|---|
| Package | `com.playereden.gridmates.wordsearch.qa` |
| Version code | `116` |
| Version name | `0.9.71-ws73-chapter-variety-qa` |
| Size in bytes | `34279387` |
| APK SHA-256 | `74cac36d85436f1e5cb59cd4087981064d7a1fff91773a6313868e2e129fbcf9` |
| QA certificate SHA-256 | `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419` |
| GitHub artifact | [Signed v116 APK ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45427261820/artifacts/14937207027) |

## Follow-through

The signed v116 build is ready for handset QA. The prior verified build is [signed v115](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45329658676/artifacts/14891551505). Continue from the v116 draft and audit the start, resume, completion, and return-to-Home flows in Quick Play and Daily Puzzle. The future beta pass includes handset art and readability review and two-phone Nearby play.
