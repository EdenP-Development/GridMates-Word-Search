# WS64 — Existing Woodland manuscript inventory and unsigned owner-review queue

> **Status: code/doc review workflow authored; GitHub CI pending.** The exact four historical manuscripts were inspected. They contain **122 unique unapproved candidate answers** but **28 of those already exist as canonical words in the QA English/Fall source**. Only **94** are potentially new canonical entries. None is newly owner-approved, shipped or installed. The queue-builder result must be verified by actual GitHub Actions before calling this milestone tested.

## Why this matters

GridMates Word Search already has real research behind Woodland chapters. Blindly importing all 122 draft records would create up to 28 duplicate canonical answers under proposal-specific IDs instead of reusing the existing `gm...` IDs. That would inflate "new word" progress and risk inconsistent saved puzzle references. Source provenance, word sense, publisher rights and human editorial approval must be resolved **before** any future pack or board is made.

## Exact candidate manuscripts

| Existing source manuscript | Chapter | Unapproved proposals in source | Existing approved-for-internal-QA answers reused | Candidate-only novel potential |
| --- | --- | ---: | ---: | ---: |
| [WS51 Birdsong](../data/lexicon/authoring/chapter-recipes-v1/birdsong-ws51-qa-manuscript.tsv) | Birdsong | 23 | To be confirmed by CI | To be confirmed |
| [WS54 By the Stream](../data/lexicon/authoring/chapter-recipes-v1/woodland-ws54-stream-manuscript.tsv) | By the Stream | 33 | To be confirmed by CI | To be confirmed |
| [WS55 Forest Discoveries](../data/lexicon/authoring/chapter-recipes-v1/woodland-ws55-wildlife-manuscript.tsv) | Forest Discoveries | 30 | To be confirmed by CI | To be confirmed |
| [WS56 Quiet Clearings](../data/lexicon/authoring/chapter-recipes-v1/woodland-ws56-clearings-manuscript.tsv) | Quiet Clearings | 36 | To be confirmed by CI | To be confirmed |
| **Total** | | **122** | **28** | **94 potential** |

The full 52-row manuscript for each chapter includes many *already-existing* internal-QA words in addition to these newly suggested records. The above 122 includes **only rows whose exact provenance tag says candidate/unapproved**, not every answer used by a candidate page.

### Verified by initial source inspection (awaiting automated audit)

Selected original IDs which **must be reconciled** with the canonical word ID before any future import:
- Birdsong proposal `ws51b002` WING → English QA `gm000453`; `ws51b003` NEST → `gm000136`; `ws51b007` FEATHER → `gm000153`.
- By the Stream proposal `ws54s001` WATER → `gm000461`; `ws54s020` DRAGONFLY → `gm000449`.
- Forest Discoveries proposal `ws55f005` ANTLERS → Fall `gm000738`; `ws55f017` HIBERNATION → Fall `gm000737`.
- Quiet Clearings proposal `ws56q001` MIST → Fall `gm000695`; `ws56q002` DAISY → English `gm000345`; `ws56q008` BUTTERFLY → English `gm000027`.

**None of these existing QA approvals is permission to ship or add the word to a new public chapter.** This is canonical ID reuse, not owner content or word placement approval.

## Implementation in this isolated WS64 branch

- `tools/lexicon/ws64_woodland_review_queue.py`: reads the four unchanged TSV manuscripts; calls existing WS10/WS63 source+identity validators; enforces exact provenance tags and original existing-word IDs; detects canonical duplicate answers; emits a deterministic **122-row unsigned** CSV with proposed sense/classification fields and full manuscript/page/provenance, plus canonical QA match ID/edition and V5 length/tier/palindrome warning flags. Both owner decision and reviewer/date remain **blank**.
- `tools/lexicon/test_ws64_woodland_review_queue.py`: unit tests actual 122 proposals, 28 canonical reuses, 94 novel potential, exact per-chapter counts, blank approvals, real existing ID examples, duplicate hard fails and read-only determinism.
- `.github/workflows/ws64-woodland-review.yml`: branch-specific Python CI, existing WS10 schema/rights validation, WS63+WS64 tests, archived CSV/JSON/README and SHA256 receipts. The existing WS40 integrity workflow also runs for changes under `tools/lexicon`.
- No candidate source manuscripts, catalog CSVs, release packs, Android Kotlin, APK/version, QA book IDs/digests, saved files or rights metadata are edited by this script. It cannot approve or add any word to the game.

## Next genuine editorial review

1. **Review the 28 reused canonical answers** for correct topic and chapter use. Reuse the stable `gm...` identity in a future **new edition**, never duplicate or mutate existing content. Some are canonically in another QA topic such as Fall/Garden, but their chapter assignment is still a separate editorial choice.
2. **Review the 94 potential new words** for accurate American English spelling, everyday context, difficulty, inappropriate ambiguity, and rights. Keep proposal-specific `ws...` IDs as provenance links only until assigning legitimate new global canonical IDs. Avoid approving all 94 simply to inflate total.
3. **Build future distinct candidate queues** for our objectively shallow *Our Solar System* (44-source-word chapter, minimum 8 repeats/52 slots) and *Flower Borders* (44, minimum 8). Both need accurate, understandable topic depth, no forced advanced jargon. Birdsong 29-source-word pool is shallow but already has 23 unapproved authored suggestions; process them first.
4. Once the owner/editor actually approves revised content, create a **new immutable versioned catalog/pack** and exact V5 chapter pages. Run generator, word-finding, readability, score, restore, signature and real handset QA. Do not repurpose any of the existing 63+18 QA page IDs.
5. Maintain [WS63 master expansion roadmap](WS63_MASTER_VOCABULARY_EXPANSION_ROADMAP_2026-11-22.md) and [word/rights review standard](WS63_EDITORIAL_AND_WORD_QUALITY_STANDARD_2026-11-22.md). The long-term 10K+ owner-approved goal remains unchanged.

**Not validated by this milestone:** final English word sense, editorial suitability, whether a 20×20 page is generator-placeable from a selected list, actual human interest, source rights for a new external corpus, physical owner-phone acceptance or public production release.
