# WS63–WS72 — GridMates: Word Search, the Great Vocabulary Expansion

**Program baseline:** November 22, 2026 · **State:** owner-requested roadmap; WS63 audit implementation started on isolated draft. Proposals are not approved release promises.

## Product vision

**Good puzzles. Good company. Endless interesting words.** Build a durable local-first collection of originally curated, coherent puzzle books with an expansive offline vocabulary. Players choose an inviting book or tap Quick Play and immediately get a fair, readable word search. We want **10,000+ DISTINCT, OWNER-APPROVED, PLAYABLE en-US answers across 30+ useful topic families** eventually—not 10,000 copied rows, inflected variants counted twice, or arbitrary text from a spelling list.

Four independent axes determine success: **technical validity** (a legal reconstructable puzzle), **editorial quality** (real words in the right chapter), **player experience** (readable and worth another round), and **release rights/approval** (legal notices and explicit owner sign-off). Never treat one axis as proof of the others. This adapts [Grid Hunt GM03 quality/fairness research](https://github.com/PlayerEden/GridMates-Grid-Hunt/blob/main/docs/research/GM03_PUZZLE_QUALITY_AND_FAIRNESS_RESEARCH.md) and [GM08 evidence categories](https://github.com/PlayerEden/GridMates-Grid-Hunt/blob/main/docs/research/GM08_PLAYTEST_EVIDENCE_AND_PROTOTYPE_DECISIONS.md); those documents are research proposals, not Word Search tests.

## Source-grounded starting inventory (different denominators)

- First English v1 authoring: **678 candidate canonical words, 670 assistant-reviewed/QA-approved distinct words, 702 approved topic assignments**, and **four active QA topics**: Woodland, Space, Garden and Everyday. The approval scope is `assistant-reviewed-qa-only`; **not owner/public-release approval**.
- Fall v1 authoring: **150 QA-reviewed words** with **117 new canonical IDs and 33 reused existing IDs**, and active Fall/Seasons authoring topic assignments. It is a separate research/authoring snapshot and **not enabled as a Fall player book**. Cross-edition compatibility must be independently validated before assuming counts are additive.
- Original WS10 root fixture: **40 draft words, 76 draft topic links, zero approved**; kept frozen for recovery-proof tests. Do not mistake this fixture for the later English v1 catalog.
- Exactly **36,213 spelling-root candidates** from a pinned source are held in an ESDB/SCOWL quarantine/research pipeline, not manually approved, not all appropriate for children, and not included in Android. Historical 563 theme suggestions produced 532 review rows + 31 holds.
- The English topic registry has **33 named families** (four active). Additional Fall authoring activates Fall and Seasons separately; a proposed name is not a published book.
- The first QA English mixed pack contains **670 distinct answers**, but ordinary board eligibility depends on pack/topic, size, challenge, length, palindrome restrictions and target containment. It is NOT a 670-word personal book or a 10K-word active app dictionary.
- A separate **18-page WS57 Woodland QA preview** exists alongside 63 historical QA book pages. Draft six-chapter Woodland manuscripts require **312 total answer slots**, currently cite **122 additional terms requiring owner/editorial approval**; no silent promotion to release.
- Current generator V5 creates 12×12 (12 targets), 15×15 (16), 20×20 (24). Frozen old 63+18 QA pages, older V1/V2/V3 generators, Daily and personal saves must not change; experimental V4 40×40/50×50 giant boards do not dictate this program's priority.
- **Live functional foundation:** signed WS60 QA code108 verified; WS61 signed v107→v108 real emulator install-over preserves six app-private save files; WS62 same-emulator real TCP Co-op/Race/Duel 3/3. **Physical owner upgrade and actual two-phone Wi-Fi/NSD remain open**. All are separate from vocabulary release approval.

## Definition of each number

| Metric | Rule | May be claimed as shipped? |
| --- | --- | --- |
| Quarantine root | Dictionary/source token held without review | No |
| Candidate unique | Canonical en-US answer with candidate provenance | No |
| QA reviewed unique | Word manually/assistant examined for internal research | No |
| QA approved in catalog | Approved word AND approved link in active QA topic; owner signoff missing | No |
| Owner approved unique | Explicit separately recorded editor/owner production review plus legal source clearance | Only when wired into versioned public pack |
| Playable eligible per board | QA/owner approved **AND** allowed topic/tier/length/normalization/generator rules | Only in the exact player build |
| Released unique | Actually packaged into an owner-approved, signed released pack | Yes, per build/manifest |
| Theme assignments | Many-to-many category references (one word may appear in several topics) | Never add to unique count |
| Book pages | Frozen exact puzzle pages with immutable IDs and digests | Never infer from word rows |

**Do not label QA-authoring `review_status=approved` as owner publishing consent.** Existing validators use that status for a different, narrower review scope. Production gate requires an explicit separate owner sign-off and license clearance, not a changed boolean inferred from CI green.

## Program streams running together

**A. Library and editorial:** source/provenance intake, normalized canonical IDs, reviewed regional spelling, definitions/context, display phrases, offensive/inappropriate surprises, family-friendliness, chapter fit, familiarity band, subtopic coverage, plagiarism/trademark and source obligations. Track ambiguous cases as holds, never quietly delete. Preserve original row provenance.

**B. Puzzles and variety:** immutable pack hashes, topic/subtopic selection, actual board eligibility, exact-once answer occurrence, no board-local substring conflicts, direction/crossing balance, suitable word lengths, adjacent page repetition and deterministic restore. Diagnostic review doesn't mean the generator can place any desired set. Change content via **new** pack/content/board IDs only; don't rewrite an issued puzzle.

**C. Bookmaking and presentation:** consistent themed book titles, cover/spine/detail/page/completion motifs, meaningful chapters and subtopics, readable display phrases separate from A–Z answer tokens, sensible beginner/intermediate/expert chapter flow, no intimidating menus. Freeze **12/16/24** answer pages after QA. Player expectations are set by book title and page labels.

**D. Actual player/game QA:** short fresh Quick Play, age-diverse phone playtests, 150–200% fonts, screen-reader, physical touch, profile/backup/restart, real LAN, return play and absence of repetition. Record **technical, editorial, visual, flow and accessibility** issues separately, drawing on Grid Hunt GM08. No fabricated universal fun/quality score.

**E. Ownership and distribution:** provenance ledger, commercial redistribution rights, source notices, immutable release manifest, offline package size, app identity/signature, signed APK and AAB/Play checks, monetization light-ads/remove-ads planned separately; release approval requires actual owner acceptance.

## Milestones and delivery gates (source/test driven, not calendar promises)

| Stage | Purpose | Deliverable | Must pass before claiming complete |
| --- | --- | --- | --- |
| **WS63 — Now** | Establish the truth before more imports | This roadmap + review rubric + Grid Hunt crosswalk; stdlib read-only QA report and unit tests for English/Fall/chapter coverage; GitHub evidence artifact | Existing WS10 integrity validator passes, no ID conflict, transparent 670/150/union figures, deterministic report, no content mutation. **Does not ship words.** |
| **WS64 — Editorial review bench** | Make hundreds/thousands of candidates manageable | Reviewer worksheet by topic/subtopic, source proof, plain-English definition/context, familiarity assessment, explainable reject/hold/approve, per-editor signoff & audit trail | No review decision can be inferred by importing a word list; owner/public release authorization distinct from QA status; tests reject forged review and unsafe source |
| **WS65 — Deepen our strong base** | Fix repetitive shallow chapters first | Distinct new candidate rounds for Birds, By the Stream, Quiet Clearings, Under the Canopy, Forest Discoveries; Space and Garden chapter-specific depth; Everyday/ Fall coverage; targeted editorial review | No generic off-theme padding, chapter pool and generated 3-page overlap report, exact board screenshots, human word and screen review; never edit existing 81 QA page IDs |
| **WS66 — 1,000** | First genuinely usable expanded catalog | New owner-reviewed unique count target **1,000**, explicit approved topic links and frozen new QA-only content packs; candidate surplus retained | 1,000 is not counted until owner/editor signed and generator/test build consumes the exact new version; no source-rights gaps; Android performance/replay and backup tests pass |
| **WS67 — New subject families** | Expand choices with real depth | Ocean Life, Birds/Insects, Weather, Geography/Navigation, Food, Animals, Fantasy via Mythology/Castles (Castle may be new subtopic/book), or other approved topics. Establish first 30+ roadmap slots and stable taxonomy | Each launched substantial family has enough familiar content for 12×12 and enough distinct words for multi-page play, accurate topic labels, quality review and visual identity |
| **WS68 — Author beautiful books** | Convert catalog into collectible experiences | Topic→chapter recipe pool→six distinctive named chapters→three frozen reviewed pages/chapter, plus matched cover+spine+detail+reward where practical | All exact board paths/hashes frozen; 12/16/24 pages pass generation/touch/a11y/restore; chapter promises match word lists; reward idempotency and book save migrations |
| **WS69 — 3,000/5,000** | Broad serious library | Incremental true owner-approved **3K then 5K** en-US canon, expanded chapter books and Quick/Custom variety | Multiple distinct same-theme 100+ seed runs, within-book overlap/human QA, bounded generation time/memory, source/notice auditing, no old puzzle drift |
| **WS70 — 10,000+** | Durable breadth across 30+ families | Verifiable 10K+ owner-approved canonical answers, with deep familiar and advanced pools, exact manifests and inspectable rights records | Honest deduped **shipped, playable** eligible counts per release; cannot substitute quarantined roots; offline APK/AAB and signing/reconstruction checks |
| **WS71 — Optional quality improvements** | Make the huge library matter | Smarter no-repeat selection, subtopic mixing, adjustable Custom focus, repeatability-preserving history/recency selection, optionally giant-board research | Replay seeds and saved board IDs still reconstruct identically; player choice respected; no invisible content edits; measure before adding complexity |
| **WS72 — Public release gate** | Honest commercial-quality beta | Human vocabulary/visual approval, rights/age/accessibility, signed release, store compliance, two-phone Nearby, privacy and practical analytics/monetization choices | **Owner** gives explicit release decision only after exact APK, content and sources are reviewed. No automatic merge or store publication. |

Milestones can be split into smaller reviewable PRs. The 1K→3K→5K→10K numbers are **goals**, not measured existing approvals or dates. A good specialty chapter may legitimately contain 45 meaningful terms rather than a forced 200; it should visibly disclose unavoidable repeats or use different chapter structure.

## Topic rollout and editorial depth plan

**Existing first:** Woodland / Birds / Woodland water / quiet meadows, Space / observatories / solar system / spacecraft, Garden / botany / pollinators / tools, Everyday / home objects / activities, plus the prepared Fall/Seasons draft.

**High-priority next:** Ocean & Marine Life, Insects & Pollinators, Animals & Pets, Weather, Geography (add map/terrain/navigation subtopics without claiming real land-nav training), Food & Cooking, Birds, Flowers & Botany, Nature & Outdoors, Travel.

**Later broad families already present in the 33-topic registry:** Music, Art & Design, Books & Literature, History, Science, Technology, Vehicles, Sports, Jobs, School, Holidays, Home, Language & Words, Architecture, Clothing & Fabrics, Mythology & Folklore, Games & Recreation, Fruits & Vegetables, and others. Castle is currently a proposed visual/book direction, not a verified active `castle` canonical topic; decide to organize under Mythology/Architecture or create a new reviewed topic.

**Proposed soft depth benchmarks:** substantial topic may aim **250–700 meaningful approved answers** distributed across useful subtopics; long-term high-traffic families eventually **300–500+ familiar-eligible** for giant boards if giant mode becomes real. For the current 24-target 20×20 page, a pool of 24 is technically enough for *one* specific no-conflict board but not good replay diversity. Begin with an **optional warning** below twice the per-board target count (12/16/24 → 24/32/48 eligible); refine thresholds with measured boards and player feedback, never present them as established human optimal values.

**Book-vs-Quick distinction:** books are carefully authored, chapter-specific fixed puzzles; Quick Play uses broad reviewed packs and fresh seeds (with exact generator/version/pack hash persisted). Daily is fixed to an edition/date. Custom can choose topic/subtopic/size/challenge later without giant choice menus or false Random behavior.

## Baseline hard checks and honest soft metrics

**Hard gates, block publication:**
- en-US A–Z normalized canonical answer, exact display normalization, unique immutable ID and answer across editions; stable source and topic references; no unsafe/noncommercial/unknown license source promoted.
- Scoped positive signed review by authorized editor and explicit owner production approval; no QA-only review repurposed.
- Board generator accepts the selected tier/length and places each word exactly once along validated coordinates; no board-local duplicate/substring/collision failure; historical golden pages and save IDs remain exact.
- Same pack version+seed → same selected answers, path/board hashes and replay; archive/backups restore old content; no network content mismatch.

**Soft signals, not automatic word rejection:**
- Full-page theme relevance, real familiar meanings, regional/spelling variants, clarity for older players, repetition, stems/plurals, word length mix, directional/crossing patterns, unexpected filler side words, player wish to play again.
- Distinct words per chapter, pairwise chapter overlap (absolute counts, Jaccard), 12/15/20 candidate eligibility, adjacent 100-seed board word-set Jaccard and direction distribution. A canonical word appearing in several sensible topics is fine; don't count it twice toward 10K.
- Palindromes e.g. `RADAR` remain lexically valid but are **V5-ineligible** because its exact eight-direction occurrence rule forbids reverse-equivalent targets; never globally delete them. `RIVER` and `RIVERBANK` are both valid in library but **must not be selected on same incompatible board** by current generator. Plural/stem pairs are editorial review prompts, not global bans.
- A word is not rejected just because a source dictionary doesn't recognize a regional proper term; hold for definition/source/human adjudication.

**No invented '92/100 quality score'.** Publish inspectable raw metrics with pass/warn/needs-review and actual human notes.

## First 30-day WORK SEQUENCE (order of work, NOT elapsed-time commitment)

1. **WS63 deliverable today:** read source manifests; commit this program/rubric and Grid Hunt mapping; implement read-only `tools/lexicon/ws63_quality_audit.py` and tests, GitHub workflow/report artifact; inspect honest English vs Fall union, all 18 proposed chapter eligibility and soft flags. No APK required.
2. Create an **editorial review queue** ordered by worst chapter pool depth and obvious thematic mismatch. Initial groups: Woodland Birds, stream, clearings, forest discoveries; Space stars; Garden flowers/pollinators; Fall overlap. Each queue row must expose answer, human display, proposed chapter, familiarity, provenance, review rationale, duplication and owner-review status.
3. Prepare **small, controlled 100–250-candidate batches** per topic as source-grounded additions, NOT immediate "approved" uploads. Cross-check rights/definitions and select only accurate, commonly understandable terms appropriate for the chapter. Keep raw/quarantined suggestions separately.
4. Independently review candidates, then create **new versioned authoring snapshot**; generate three frozen trial pages per chapter and check real QA Android finger tracing, readable words and save migration. Do not change previous content.
5. Review a signed debug APK on an actual handset and use observations to revise wording, length and variety before continuing to further categories. In parallel, physical two-phone Nearby gate stays open and unrelated to editorial approval.

## Ownership / completion checklist

- [ ] WS63 machine report and unit tests executed on the exact source; both normal and independent art/release CI conclusions recorded (not just written).
- [ ] Honest proposed-topic vs QA-active vs owner-approved vs shipped vocabulary dashboard.
- [ ] Editorial review procedure with separate manual approval and evidence of source rights.
- [ ] First focused deep-chapter candidate batches with QA and held/rejected rationale; no arbitrary filler or copied dictionaries.
- [ ] New pack snapshots IDs/checksums and 12/15/20 full-board generation/restore tests when approved; old saves intact.
- [ ] Low-repetition actual book pages; readable on phone; optional real player usability review; verified app signing.
- [ ] Physical real two-phone Nearby, TalkBack, legal and store acceptance before public publishing.

## Canonical references

- [Recovered WS10 roadmap](WS10_TEN_THOUSAND_WORD_LEXICON_ROADMAP_2026-11-08.md)
- [Historical vocabulary quality audit](WORD_LIBRARY_STATUS_2026-11-08.md)
- [Live word library README](../data/lexicon/README.md) and [en-v1 QA status](../data/lexicon/authoring/en-v1/README.md)
- [Chapter-specific 18 recipe map and immutable QA rule](CHAPTER_CONTENT_MODEL_AND_EDITORIAL_PLAN_2026-11-19.md)
- [Grid Hunt GM03 quality](https://github.com/PlayerEden/GridMates-Grid-Hunt/blob/main/docs/research/GM03_PUZZLE_QUALITY_AND_FAIRNESS_RESEARCH.md), [GM05 variety](https://github.com/PlayerEden/GridMates-Grid-Hunt/blob/main/docs/research/GM05_CHAPTER_PROGRESSION_SPACED_PRACTICE_AND_TRANSFER.md), [GM07 Quick vs Custom](https://github.com/PlayerEden/GridMates-Grid-Hunt/blob/main/docs/research/GM07_QUICK_AND_CUSTOM_HUNT_PLAYER_CHOICE_RESEARCH.md), [GM08 observed play](https://github.com/PlayerEden/GridMates-Grid-Hunt/blob/main/docs/research/GM08_PLAYTEST_EVIDENCE_AND_PROTOTYPE_DECISIONS.md)
- [WS62 active Nearby handoff](WS14_ACTIVE_HANDOFF_2026-11-09.md) (WS63 adds content audit, not a functional app release)

**Current end-state of this document:** plan + executable read-only audit + tests. Nothing newly owner approved, shipped, installed or public.
