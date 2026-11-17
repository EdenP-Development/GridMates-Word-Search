# 2026-11-17 — WS40 GitHub plan recovery and word-library authoring

> **Quick status:** PASSED LOCAL AND GITHUB QA. All 26 authoring tests and the
> exact-source intake/review reproduction passed. No APK is built because Android
> code/assets and active word pools are unchanged.

## Who, what, where, why

- Owner asked to continue and deep-dive GitHub for forgotten plans, ideas and
  roadmaps, retaining those that still apply.
- Work performed by Codex. Reconcile actual source and later decisions before
  reviving old tasks, especially the ongoing word-library/replayability work.
- Branch `feature/ws40-plan-recovery-and-lexicon-2026-11-17`, stacked on WS39.

## Where we came from

- [WS39 receipt](2026-11-17_WS39_REPLAY_AND_BOOK_BACKUPS.md),
  [draft PR #150](https://github.com/PlayerEden/GridMates-Word-Search/pull/150).
- Remote starting head `3eb481424d622c6c067326393934ffac6060ef14`.
- Latest playable v102 source `8ce68b0d0564ce11c30c0b18dcb208d7b9a1b6f4`,
  [signed APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44192668122/artifacts/14377072810),
  SHA-256 `3468befffa01b5e6b1f098389d775519274d95557ce00eac9d8b8514e9335d37`.
- WS39 standard/independent builds, 315-board audit, ten Android tests and native
  install-over/navigation passed. Owner physical review and two-phone gate open.

## What changed in this pass

- Inventoried 179 live branch heads, 104 open draft PRs, 46 issue search records,
  the current 87-PR linked base chain and changed-file presence for 17 side PRs.
  Read current/historical designs and relevant source. No issue bulk-closing,
  PR merging or other app changes.
- Found WS10's isolated authoring branch had never joined the cumulative app
  source. Recovered 17 data/tool/doc files exactly from
  `fb023af6c31323961760439ff1f62b0226ec57a2`, with original Git blob and byte-size
  verification. Preserved source notice, candidate review states and holds.
- Recovered the 33-topic plan, 40 draft canonical words/76 associations,
  563 original research rows and reproducible pinned intake/review tooling.
  Source quarantine and proposal counts are not approved playable vocabulary.
- Added immutable approved-only pack export, canonical SHA-256, source/reviewer
  attribution, deterministic ordering and refusal to replace changed pack IDs.
  Seven new snapshot tests join nineteen recovered tests.
- Consolidated historical branch-specific CI into one current authoring/test/
  quarantine/review reproduction workflow. No Android rebuild needed.
- Added [current roadmap](../CURRENT_PRODUCT_ROADMAP.md),
  [recovery audit](../audits/2026-11-17_WS40_GITHUB_PLAN_RECOVERY.md), inventory
  and recovery manifest. Landing docs now point to current status and preserve
  old checkpoint text in collapsed history.
- Identified still-relevant mixed Quick Play/size/Play Another, actual catalog
  depth, book-to-profile rewards and physical two-phone acceptance. Classified
  future themes, languages, Marathon, extra portraits/economy and old visual
  alternatives without automatically enabling them.
- Restored seven exact-hash historical research references that existed only on
  `main`, including the Spanish/parity, word-inventory, genre/competitor, font
  intake and artifact-retention documents. Recorded their source in a separate
  recovery ledger. No old cleanup workflow or font import was enabled. Added
  the still-relevant consecutive-play freshness/layout benchmarks to the roadmap.

## Build and QA receipt

- **APK build:** none. App/core/assets/build/signing files unchanged, so v102
  remains the latest playable build and immediate tested fallback.
- **Local executed:** 26/26 Python authoring/snapshot tests, exact original
  hashes for all 17 recovered files, canonical catalog validation (zero approved
  new-pipeline words), workflow syntax and patch whitespace checks.
- **Tested source:** `71e37d9d61816bc8135afa60fdaa1d8c3624de23`, [draft PR #151](https://github.com/PlayerEden/GridMates-Word-Search/pull/151), stacked on #150. Later receipt/readme changes are documentation only.
- **GitHub CI:** [run 44198852876](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44198852876) **SUCCESS**, all eleven job steps successful. The explicit log reports 26 tests passing, all 17 recovered source hashes matching, and exact pinned source archive verification.
- **Reproduced content:** 36,213 unreviewed source candidates, Woodland 185 / Space 183 / Garden 164 review rows (532 total), 31 holds, and 0 automatically approved words/assignments. 176 proposals are flagged for no direct spelling-root match; that flag is not a rejection or approval.
- **Expected negative check:** exporting the unchanged proposed/unreviewed root catalog failed before creating an output directory. The CI step verifying that rejection passed.
- **Review artifact:** [14380421759](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44198852876/artifacts/14380421759), `GridMates-WS40-Recovered-Lexicon-Review`, 455,686-byte ZIP, SHA-256 `c155610a2b901aaafe5a74ea3ed477dde48f8a4c289ebcedbe35024cc6668c11`. Downloaded ZIP digest/CRC and its actual queue report verified.
- Root recovered files remain an exact historical negative-test fixture. Next editorial work goes into a new versioned authoring directory with the same schemas/stable IDs, passed explicitly via `--data-dir`, rather than editing the pinned recovery fixture.
- **Failures corrected:** the first recovery transfer exceeded the process
  argument-size limit before any write. Recovery was split into bounded batches
  and all original blob hashes then matched. No application build was retried.
- **Physical acceptance:** unchanged. v100 owner screenshots remain the visual
  baseline. v102 has automated/native emulator evidence, not owner phone signoff.

## Outcome and follow-through

- Missing word-library work is recovered into the active development branch;
  current plan and historical context are separated and traceable.
- No new theme, approved word pack, mixed generator or production catalog is
  claimed in this pass.
- **Next exact task:** create `data/lexicon/authoring/en-v1` from the four schema/data files, retaining stable IDs and the pinned root fixture. Review the first 100–250-word editorial batch from the
  recovered theme sheets plus general vocabulary, export immutable packs, then
  implement a new pack-aware generator/save identity and full mixed Quick Play.
  Keep old puzzle reconstruction and Daily stable. Catalog growth and an
  idempotent book reward bridge follow; hardware LAN acceptance remains parallel.
