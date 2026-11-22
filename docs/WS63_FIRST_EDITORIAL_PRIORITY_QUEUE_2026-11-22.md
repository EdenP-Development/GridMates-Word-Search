# WS63 — First evidence-backed Word Search vocabulary review queue

**Verified against [WS63 Actions #45079091823](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45079091823):** source `0f50e814e1dc5e667930011d9b5be862d4e7eb2b`; six tests passed. Original JSON/Markdown evidence: [artifact #14776092886](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45079091823/artifacts/14776092886). This is an editorial queue **derived from existing English v1 QA words and proposed chapter map**, NOT from 18 freshly generated and played boards.

## Honest reference inventory

- **670** unique first English QA words; Fall **150**, including **33** reused and **117** new: **787** unique combined internal-QA answers across six active authoring topic families. These are **NOT owner-approved production words**.
- Initial V5 topic pools: Woodland 159 (143 eligible at 12×12 Relaxed), Space 169 (144), Garden 174 (159), Everyday 200 (199), Fall/Seasons 150 (112). Fall/Seasons share the same 150 canon and are **not two independent 150-word libraries**.
- **18 proposed chapter pools** audited: six chapters each for Woodland, Space and Garden. **190** substring-related canonical pairs flagged for per-board review, not rejected or counted as 190 errors. No trivial `+S` plural pairs detected by the deliberately narrow heuristic; that is NOT a comprehensive morphological check.
- Word pool counts use approved-for-QA topic+subtopic assignment and V5 tier/length/palindrome eligibility, **not** board-generation success. No thematic quality, exact repeated page words or user satisfaction proven from the counts alone.

## First editorial triage, ordered by proven depth and observed thematic issues

| Priority | Edition/chapter | Existing distinct QA pool | Relaxed 12 eligible | Classic 15 eligible | Expert 20 eligible | Minimum reused slots across three 12/16/24-target pages | What the editor must do |
| --- | --- | ---: | ---: | ---: | ---: | ---: | --- |
| **P0** | Woodland 5 — Birdsong | 29 | 23 | 29 | 29 | **23** | Reconcile 23 existing **unapproved WS51 Birdsong terms** before inventing more; verify bird/birdlife definitions, reading tier and every target's fit |
| **P0** | Space 4 — Our Solar System | 44 | 39 | 44 | 44 | **8** | Curate more accurate, recognizable Solar-System-specific terms rather than padding with generic `STAR` and `SPACE`; verify technical terms and number/place names |
| **P0** | Garden 4 — Flower Borders | 44 | 36 | 44 | 44 | **8** | Expand genuinely recognizable flower/bloom/flower-part vocabulary and natural display labels; carefully distinguish common names, cultivars and botanical jargon |
| **P1** | Space 3 — Night Sky | 54 | 40 | 54 | 54 | 0 | Avoid overusing the same sky/light terms in several Space chapters; review observatory/sky terminology |
| **P1** | Woodland 3 — Along the Trail | 60 | 60 | 60 | 60 | 0 | More authentic trail/camp/track terms, not duplicate generic woodland filler; compare neighboring chapter target overlap |
| **P1** | Woodland 6 — By the Stream | 65 | 65 | 65 | 65 | 0 | Existing broad category pool is NOT proof of stream specificity; review existing **33 unapproved WS54 streamlife proposals** and water-specific manuscript separately |
| **P1** | Woodland 7 — Quiet Clearings | 60 | 60 | 60 | 60 | 0 | Existing broad `habitats,trails` pool can misrepresent clearings; review the WS56 moss/glade/meadow/sunlight manuscript and prior overlaps |
| **P1** | Garden 5 — Garden Visitors | 62 | 54 | 62 | 62 | 0 | Pollinator/flower pairs; distinguish insects from generic botanical words |
| **P2** | Woodland 8 — Forest Discoveries | 105 | 95 | 105 | 105 | 0 | Not quantitatively shallow, but audit repeated stems/other chapter overlap; inspect existing WS55 candidate manuscript |
| **P2** | Space 5/6/7/8 | 71/80/89/84 | 65/61/83/70 | 71/80/89/84 | 71/80/89/84 | 0 | Chapter-specific purpose and distinct target patterns rather than the same generic orbit/mission words |
| **P2** | Garden 3/6/7/8 | 83/70/112/145 | 76/63/105/130 | 83/70/112/145 | 83/70/112/145 | 0 | Flower, growing, visitors, tools, seasons and flourishing identities distinct; no pressure to use every term per page |
| **P2** | Woodland 4 — Under the Canopy | 99 | 83 | 99 | 99 | 0 | Review genuine tree/canopy/bird balance and spillover into other chapters |
| **P3** | Everyday / Fall v1 | 200 / 150 | 199 / 112 | 200 / 148 | 200 / 150 | N/A | Maintain everyday friendliness; Fall is authoring-only and must be reviewed against base English identities and proposed six subtopics |

**Limit of the minimum-repeat calculation:** 52 answer slots in each proposed three-page chapter; `max(0, 52 - distinct_QA_words_in_chapter_source_pool)` is a lower bound, **not** a measured page-overlap count and not a guarantee all 52 targets are placeable with the available words. It ignores nuanced length/tier/substring conflicts and must be followed by frozen V5 page generation.

## Existing work to reuse FIRST, rather than generating duplicates

- [WS51 Birdsong QA manuscript](../data/lexicon/authoring/chapter-recipes-v1/birdsong-ws51-qa-manuscript.tsv): three pages, **23 explicitly `ws51_editorial_candidate_unapproved` words** such as Beak, Wing, Nest, Chirp, Flock, Plumage, Fledgling. Verify their exact IDs, senses, tier, and chapter relevance; they must not be silently approved just because generated puzzles used them.
- [WS54 By the Stream QA manuscript](../data/lexicon/authoring/chapter-recipes-v1/woodland-ws54-stream-manuscript.tsv): **33 explicitly unapproved streamlife proposals** with water/plant/animal terms and separate approved-in-QA existing IDs. Keep stream chapter coherent.
- [WS55 Forest Discoveries manuscript](../data/lexicon/authoring/chapter-recipes-v1/woodland-ws55-wildlife-manuscript.tsv) and [WS56 Quiet Clearings manuscript](../data/lexicon/authoring/chapter-recipes-v1/woodland-ws56-clearings-manuscript.tsv): use earlier curated scope and source QA fixtures rather than discarding research.
- [WS10B2 recovered research intake](../data/lexicon/intake/v4_three_theme_research_seed_suggestions.csv) and ESDB quarantine: **research only, no owner approval**. Review license/source before using any externally derived records.
- [Existing English v1/Fall v1 authoring](../data/lexicon/authoring/en-v1/README.md) and [Fall README](../data/lexicon/authoring/fall-v1/README.md): canonical IDs and owner-release boundary.

## Next controlled work packages

1. **WS64-A — Review previously authored 122 Woodland candidates.** Produce a plain CSV/TSV editorial queue with original candidate ID/answer/display, source chapter/page, suggested familiarity, obvious morphological/substring interaction and an **empty owner decision**. Do not alter their source proposal files. Include exact source provenance and checksummed freeze.
2. **WS64-B — Expand genuine holes first:** create two new **candidate-only** research lists for Solar System and Flower Borders (target initially 50–100 high-quality suggestions per chapter, not approved count), plus optional Birdsong additions after processing its existing 23 proposals. Check every suggestion against English/Fall canonical IDs and other manuscripts; reuse IDs for repeated valid answers.
3. **WS65 — Editorial batch adjudication:** document meaning/context, en-US regional display, chapter fit, familiarity, age appropriateness, rights and holds/rejects. Prepare new versioned catalog **without mutating en-v1 or old QA pages**.
4. **WS65/66 QA:** generate three new 12/15/20 V5 trial pages, run exact-occurrence/path/hash/restore tests, measure adjacent-page and chapter-pair Jaccard/repeats, inspect a signed phone test. Freeze only after owner approves.

## How we'll judge success

**P0 initial objective:** have 52 **distinct, genuinely on-topic, player-readable** candidates for each three-page specialist chapter where possible, with more than 52 preferable for replay. A valid source-pool count alone is not enough, and any hypothetical new word counts **zero toward owner-release-approved** until signed off. No page changes, owner data reset or production publication in WS63.

**References:** [WS63 master roadmap](WS63_MASTER_VOCABULARY_EXPANSION_ROADMAP_2026-11-22.md), [editorial quality standard](WS63_EDITORIAL_AND_WORD_QUALITY_STANDARD_2026-11-22.md), [verified automated report](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45079091823/artifacts/14776092886).
