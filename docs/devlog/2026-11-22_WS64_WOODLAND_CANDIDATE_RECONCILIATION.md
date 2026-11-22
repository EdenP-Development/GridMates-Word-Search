# WS64 — Unapproved Woodland manuscript → unsigned editorial queue

> **VERIFIED — DRAFT PR #177; unsigned owner/editor worksheet built and independently inspected.** Executed source `8517a8ab991fd3485fd63846077965b7d04f3c08`. [WS64 #45080015449](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45080015449) and [original WS40 integrity #45080015518](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45080015518) both **SUCCESS**. No new owner approvals, word packs, production APK or changes to old saves.

## Actual WS64 exact-source test and review artifact receipt

- **First run failure was source-tooling only:** [WS64 initial run #45079881313](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45079881313) and [WS40 initial run #45079881324](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45079881324) failed because the new parser used Python's nonexistent `utf8-sig` codec rather than `utf-8-sig`. Existing legacy lexical tests passed; no corpus edit or game defect was demonstrated. Fixed only the reader's encoding name in commit `8517a8ab991fd3485fd63846077965b7d04f3c08` and reran both workflows.
- **Corrected source/code SHA:** `8517a8ab991fd3485fd63846077965b7d04f3c08`. [Dedicated WS64 QA #45080015449](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45080015449) **SUCCESS**, new WS64 5/5 native Python tests and previous WS63 6/6 both passed, exact 122/28/94 source invariants and blank owner review fields. [Independent historic WS40 source/rights/quarantine #45080015518](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45080015518) **SUCCESS**, including exact pinned 36,213 spelling-root quarantine and 532 review/31 holds with zero automatic approvals.
- **[Exact archived unsigned review artifact #14777225148](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45080015449/artifacts/14777225148)** independently downloaded, ZIP integrity verified; ZIP SHA256 **`b0403133d5e1f749541f00fbf0d2c47df9ab483d09e246752e95be6335ecaf96`**. Includes a **122-row 29,887-byte `woodland-proposals-review.csv`**, `source-reconciliation.json`, README and manifest. Manifest hashes independently matched extracted CSV **`405234416003fd5c342bb64882af51a0cb6a45bba57e5c5c67a7d02c2f8a1faf`**, JSON **`165a9e3dd4b04edebb3501e86135e22a778a2c9f5de818b7b9369c56492a3bc6`**, README **`452ca8beefd6cc7eccb8ca75660fcc81851e3fab338bafd9abf5d23d164479dc`**.
- **Actual row counts:** 122 source proposals, 122 unique proposed answers and 122 unique proposal IDs. Exactly **28** match canonical internal QA answers (source-specific `ws...` IDs must not create new canonical rows), leaving **94 potentially novel**. Breakdown: **Birdsong 23 total, 6 existing, 17 candidate-new; Stream 33/3/30; Forest Discoveries 30/4/26; Quiet Clearings 36/15/21.**
- **Editorial review remains open:** all 122 rows have **blank `owner_decision`, `owner_reviewer`, `owner_reviewed_on` and `editorial_notes`**; source provenance/old candidate IDs intact. Flags `NOT_12X12_FIT=4`, `NOT_RELAXED_TIER=69`, and `REUSE_EXISTING_CANONICAL_ID=28`; these describe proposed tiers/technical filtering and are **not failed dictionary entries** or publishing verdicts.
- **Safety:** No older English/Fall CSVs or source TSVs mutated, no ownership approval added, no Android build or version change, no old 63+18 frozen QA page IDs/board hashes, saves, V5 generation, Daily, Nearby, original assets or release signing touched. Physical user phone/beta and 2-phone Wi-Fi QA remain separate open gates.

**Next exact content work:** owner/editor adjudicates the existing 28 canonical reuses and 94 candidate-new terms (with external rights/sense/tier checks), then dedicated Solar System and Flower Borders **candidate-only** research, no direct imports; new versioned QA edition and actual frozen V5 book page tests follow only when content is approved.
 No owner approval, content activation, APK, words newly shipped, app source mutation, or signed Android install. This is a reproducible editorial intake step following verified WS63 vocabulary quality audit.

## Starting verified baseline

[WS63 DRAFT PR #176](https://github.com/PlayerEden/GridMates-Word-Search/pull/176) introduced full 10K-word staged roadmap, quality standard, Grid Hunt research crosswalk and read-only QA tool. On exact commit `0f50e814e1dc5e667930011d9b5be862d4e7eb2b`, [WS63 #45079091823](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45079091823) and [WS40 historical lexicon recovery #45079091757](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45079091757) both SUCCESS; 6/6 WS63 tests; independently inspected [quality artifact #14776092886](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45079091823/artifacts/14776092886) confirms 670 English + 117 distinct new Fall = **787 unique internal QA**, six active authoring topics and 18 proposed chapter pools. Birdsong has 29 eligible source words across 52 slots (at least 23 repeats); Our Solar System 44/52 (8), Flower Borders 44/52 (8). These are source-pool bounds, not played board results.

## New WS64 purpose

Prior Word Search chapters contain more editorial material than the first 670-word source, but many proposal IDs duplicate entries already in first English or Fall catalog. Source inspection found four manuscripts:
- WS51 Birdsong: 23 unapproved candidate rows.
- WS54 By the Stream: 33 unapproved rows.
- WS55 Forest Discoveries: 30 unapproved rows.
- WS56 Quiet Clearings: 36 unapproved rows.

**122 proposed rows, 122 distinct proposal answers, 28 already present as canonical answers in English/Fall QA, and 94 possible genuinely new canonical answers** pending exact CI proof. Naive import with new `ws..` IDs would duplicate words like WING, NEST, WATER, DRAGONFLY, MIST, DAISY and BUTTERFLY. Canonical original `gm..` ID must be reused in a future reviewed content edition; proposal ID retained as provenance, not promoted to public identity. **'28 reused' does not make those words owner-approved in their new chapters.**

## Code and safety

- `tools/lexicon/ws64_woodland_review_queue.py` reads four TSV manuscripts **without writing sources**, reuses `validate.normalized` and WS63/WS10 canonical integrity, enforces source count/tag/chapter and existing QA word identity, classifies exact `canonical_existing_word_id` vs `PROPOSED_NEW_CANONICAL_NEEDS_REVIEW`, adds simple V5 palindrome/board-size/tier warnings, leaves `owner_decision`, `owner_reviewer`, `owner_reviewed_on` and notes **blank**.
- `tools/lexicon/test_ws64_woodland_review_queue.py` verifies counts, known original ID mappings, duplicate hard fail and deterministic read-only behavior.
- `.github/workflows/ws64-woodland-review.yml` validates source identity, runs WS63+64 tests and uploads a hash-pinned 122-row human-review CSV + JSON and README; original WS40 lexicon integrity check may run on tool changes too.
- No approvals changed in `data/lexicon`, no new dictionary source imported, no versioned pack written, and no edit of production runtime, 63 historical + 18 frozen Woodland QA pages, saved game IDs, V5, Daily/Nearby, artwork, signed v108 app or public release assets. 122 source proposals are not all new, and 94 candidate words are **not yet approved**.

## Original planned pass contract (historical; superseded by verified receipt above)

- [New DRAFT PR #177](https://github.com/PlayerEden/GridMates-Word-Search/pull/177) stacked on WS63; exact tested source SHA and workflow IDs pending.
- Python tooling/report must show `122/28/94`, origin breakdown `23/33/30/36`, `0` new owner approvals.
- Every proposed answer appears once in review CSV with provenance and **blank owner reviewer/decision**. Existing canonical words link to current exact stable IDs; new candidates receive no fabricated global canonical ID.
- Reconciler's proposed source SHA manifest and output CSV/JSON must be reproducible; inspect archived actual reports before claiming success.
- Future on-device/app QA and owner decisions remain separate. WS60 signed v108 APK still current handset candidate, not replaced by a vocabulary worksheet.

## Next

WS64 owner-facing review/hold notes for these 122 candidates, then candidate-only Solar System and Flower Borders expansion based on actual source QA shortages. After real approval, new immutable authoring edition and generator/playtests—no silently modified existing puzzles. Preserve [WS63 master roadmap](../WS63_MASTER_VOCABULARY_EXPANSION_ROADMAP_2026-11-22.md), [editorial review standard](../WS63_EDITORIAL_AND_WORD_QUALITY_STANDARD_2026-11-22.md) and [WS63 first ranked queue](../WS63_FIRST_EDITORIAL_PRIORITY_QUEUE_2026-11-22.md).

**Handoff:** WS64 separate draft contains 122 existing Woodland manuscript candidates vs canonical English+Fall QA, likely 28 reuses/94 new. Check exact new GitHub CI and fix any actual source parsing discrepancies; do not touch packaged wordpacks or authorise release.
