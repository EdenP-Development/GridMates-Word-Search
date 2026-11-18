# WS41 — Mixed Quick Play and book content

Status: implementation and verification in progress. No v103 APK is verified yet.

Owner authorized the recovered content/replayability roadmap. Codex is implementing the first assistant-reviewed QA-only English packs, mixed size selection, portable frozen boards, append-only book chapters and idempotent profile rewards. This is not human editorial signoff or production content/art approval.

Starting source: WS40 PR #151 head bb90af0a2948e8b21879e6e47cbd81d4ef0e94a8. Its 26 authoring tests passed in run 44198852876. Latest playable fallback is v102, tested source 8ce68b0d0564ce11c30c0b18dcb208d7b9a1b6f4, standard run 44192668122, artifact 14377072810, APK SHA256 3468befffa01b5e6b1f098389d775519274d95557ce00eac9d8b8514e9335d37. Owner physical review remains open.

## Implementation

- 670 distinct curated QA answers / 702 topic assignments across Woodland (159), Space (169), Garden (174) and Everyday (200). Frozen JSON plus strict checksummed runtime projections. Root recovered fixtures unchanged.
- New V5 pack-aware generator; original V1/V2/V3 outputs and IDs preserved. V4 lab remains separate. Mixed sets balance four topics, avoid substring/palindrome conflicts, and verify unique physical answer paths.
- Small 12×12, Medium 15×15 (recommended), Large 20×20 chooser and same-size Play Another, with optional Change Size. Custom settings remain independent.
- Schema 6 carries complete checksummed V5 boards. Older schema 1–5 reconstruction stays supported; book reward ledger travels with the profile and whole-game backups.
- QA books append six chapters each, preserving the original nine page identities/recipes, for 63 pages across three 21-page volumes. All new vocabulary/catalog content remains debug only.
- Completed book pages grant general puzzle-count portrait/font/achievement rewards once. They do not invent old elapsed-time/hint records or Solo-only achievements.
- All existing game artwork retained. No Sudoku changes, PR merges, user data reset or release publication.

## Verification / build

Pending core preflight, literal new V5 fixtures, full Android and independent builds, real emulator migration/backup/input tests, and native review. No successful new APK claimed. Physical two-phone gate remains open.

## Next exact action

Compile and exercise V5 generation, vocabulary variety, frozen save corruption checks and book awards; capture fixed golden hashes. Then complete Android schema/backup/UI tests, build signed v103 and inspect actual navigation/screenshots before delivery.
