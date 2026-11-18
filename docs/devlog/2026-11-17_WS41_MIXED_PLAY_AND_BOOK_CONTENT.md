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

First source `539543d125945e83698627fa63f6f66bf8ad5fb0` passed core preflight (24 tests, run 44233330672), full standard Android run 44233338654 and independent run 44233338683. Actual native v102→v103 install-over and 16 finger traces/restart/resume/Another passed, plus 23 Android tests (15.178s). Artifact 14395939693 preserves that evidence. Screenshot inspection found Home's game title ellipsized at 360dp. Reclaiming the unnecessary narrow-screen inset, allowing a second title line, and adding actual 320/360dp 100/200% text-layout checks before delivery. A corrected signed build and 24-test emulator rerun are pending. Physical two-phone gate remains open.

## Next exact action

Compile and exercise V5 generation, vocabulary variety, frozen save corruption checks and book awards; capture fixed golden hashes. Then complete Android schema/backup/UI tests, build signed v103 and inspect actual navigation/screenshots before delivery.
