# GridMates Word Search — Vocabulary editorial & puzzle-quality standard (WS63 draft)

**Scope:** internal review rules for a large, long-lived en-US puzzle library. This does **not** grant approval to ship any new content. The owner must accept/revise the standards and separately review a release manifest.

## 1. A word's five independent checks

| Domain | Required evidence | Gate |
| --- | --- | --- |
| **Actual English answer** | Normalized A–Z token, real en-US spelling/accepted regional variant, meaningful ordinary definition in the proposed context, not a hallucinated concatenation | Technical **and** human; unresolved -> HOLD |
| **Correct category** | Exactly why `answer` belongs in proposed topic/subtopic/chapter, ideally with short sense note; common vs unusual vs specialist usage distinguished | Human thematic review; ambiguity -> HOLD, not filler |
| **Understandable to target players** | Human display phrase (spaced/hyphenated where natural), familiarity tier within category, no obscure jargon forced on Relaxed players, non-distressing age-appropriate usage | Human review and actual player reading |
| **Source/provenance and rights** | Original selection vs licensed dictionary, exact source/release/checksum/notice, commercial-use review, stable source ID and human reviewer record | Hard licensing gate; uncertified imports -> quarantine |
| **Actual playable puzzle** | Pack/board/tier eligibility, no page-local target overlaps, exact-once valid path, suitable grid size, readable word list, repeat/spacing tests, persistent board identity | Automated generator + Android QA; not a lexical ban |

A term can **pass spelling** and **fail chapter fit**. It can be valid for Expert but unhelpful in Relaxed. A concept can be suitable for the global library and unsuitable for a particular page. Always record the *reason* rather than simply treating a word as good/bad.

## 2. Three review stages — never confuse them

**INTAKE candidate:** a proposed answer with traceable evidence and proposed display, tier, topics. Automated checks may reject invalid syntax and collisions; dictionary evidence by itself does not approve meaning, suitability or source redistribution. Keep all unknowns in quarantine.

**INTERNAL QA reviewed:** a source-authorized editor/assistant verifies spelling, sense, topic, tier and mechanics for *debug builds*. Existing English v1 records mark 670 as `review_status=approved` **within `assistant-reviewed-qa-only` scope**. That is **not** owner signoff.

**OWNER-APPROVED PUBLIC:** require explicit owner review of final versioned pack/manifest and any relevant licensing notices, suitable player-device QA and no frozen-puzzle migration. Keep public authorization separate from the existing `review_status`; never silently change QA records to say owner approved. When disagreement occurs, use HOLD and revision history.

Editorial worksheet should expose these fields without demanding all of them from casual players: `word_id`, `answer`, `display`, `locale`, `sense/context`, `topic_id`, `subtopic_id`, `tier`, `source_id/release/rights`, `why_fits`, `ambiguity_flags`, `generation_compatibility`, `qa_review_by/on`, `owner_review_by/on`, `decision` and `revision`. The new worksheet must not destroy existing CSV schema.

## 3. Familiarity and board fit

| Tier | Reader expectation | Examples of suitability |
| --- | --- | --- |
| Familiar | Ordinary player can recognize/read it in the stated category without an encyclopedia | FOX in Woodland; MOON in Space; ROSE in Garden |
| Intermediate | Understandable with topic context, perhaps specialized or compound | RIVERBANK in Waterside; CONSTELLATION in Space |
| Advanced | Distinct specialist/technical/historical language used intentionally in challenging chapter | A carefully verified meteorological or navigation term, never a made-up or merely long word |

**Length ≠ difficulty.** `AXE` isn't automatically easy for every language learner; `SUNFLOWER` isn't automatically advanced because it is long. Research terms need actual editorial adjudication, not a frequency model presented as truth.

V5 mechanical eligibility (existing engine): answer must be **3–20 uppercase A–Z**, shorter than board side (12/15/20), **nonpalindromic** for its current all-directed unique-occurrence check, and eligible by tier (Relaxed familiar; Classic familiar/intermediate; Expert all). Board-local conflicts exclude substring-containing target pairs together, e.g. `RIVER` vs `RIVERBANK` for one page. Neither word should be deleted from the canonical library globally.

**Display separate from grid:** `MILKYWAY` can show *Milky Way*. Exact display normalization must reconstruct the answer. For terms with variant punctuation, verify the intended readable label and avoid indistinguishable answers after A–Z normalization. Maintain en-US dialect boundary; don't introduce other locales silently.

## 4. Authoring a chapter rather than a bag of words

A book chapter's title promises a theme: **Birdsong** should be about birds and birdlife, **By the Stream** should be about streams/banks/water ecology, **Quiet Clearings** should evoke glades, light and meadow vegetation. Multiple plausible themes may share valid words, but repeated generic FOX, WOOD, TREE and RIVER cannot be the majority everywhere merely to meet a quota.

Every regular new chapter plans **three fixed puzzles**: 12×12/12 answers, 15×15/16, 20×20/24, or **52 answer slots** in total. Proposed chapter pools should prioritize:
- Enough unique familiar words at short lengths for the first page, beyond exactly 12.
- Natural increasing lexical variety rather than a sudden forced specialist list.
- A broad enough set that the same core words needn't recur on all three pages; report minimum forced repetition from `max(0, 52 - chapter_distinct_eligible)`, and actual overlap after generation.
- At least two or three coherent *subtopics* when one subtopic cannot supply the full range, or deliberately use a smaller chapter rather than fake terms.
- Exact frozen page IDs, targets, board hashes and proven saves once a page is activated; no repurposing old pages.

**No invented "fun score":** separately show hard validity, editorial notes, unique terms/page, overlaps across adjacent pages, length/tier distribution and actual human reaction. Generate 100+ seeds in candidate engines for variety **without** claiming those are 100 approved player puzzles or all pages.

## 5. Source/rights and safety policies

- Treat the pinned 36,213 ESDB/SCOWL spelling-root quarantine as **possible spellings**, not a licensed production word pack. Maintain release pin, source checksum, author/license/notice ledger and any commercial obligations. Bulk redistributing derived source data is a separate rights decision.
- ENABLE-style word game lists and `wordfreq` analysis may inform review but require their exact source/license/data provenance before using or redistributing. `wordfreq` code and its frequency data are **not necessarily the same license**. Inspect current upstream terms before any production intake.
- Human sense/context checks prevent fabricated taxonomy, inappropriate sexual/violent slang in everyday family packs, slurs in casual random play, obscure proper nouns, copyright/trademark confusions and regional mismatch. A substring match to an offensive sequence does not automatically make the whole innocent word inappropriate; review context.
- Never copy long copyrighted descriptions, definitions, illustrations, articles or complete themed word compilations wholesale. Original human/editorial selection, natural spelling and your own descriptions are preferred.

## 6. Review queue prioritization

**Priority A:** the *actual* shallow or repetitive chapters. Start with Birdsong, By the Stream, Quiet Clearings, Forest Discoveries, plus existing Space/Garden specialty chapters. For each, examine pool eligibility for each size/tier and the 52-slot lower bound. Explicitly record whether 122 prior Woodland proposals have been checked and what still needs owner signoff.

**Priority B:** newly authored but not activated Fall/Seasons, Everyday and related overlaps. Treat identical canonical IDs as reuse, not new word count.

**Priority C:** next major families Ocean, Insects, Weather, Animals, Geography/Navigation and Food. Do **not** add books until words and category sense are trustworthy. Matched covers/spines/chapters follow content signoff, not the other way around.

**Priority D:** wide-topic and specialized 30+ family coverage, with deliberate high-quality advanced pools rather than putting obscure entries into all Relaxed puzzles.

## 7. Evidence recorded per proposed batch

- Immutable input snapshot ID and SHA; exact changed candidates, new vs reused IDs, source rights, editor choices, holds/rejections with rationale, QA scope and owner/public scope.
- Hard validation outcome, per-topic/per-subtopic/tier counts; 12/15/20 nonpalindrome eligible counts; page-local substring/duplicate collision checks. A warning is **not** a pass; a soft recommendation is **not** a blocker without owner agreement.
- Each proposed three-page book chapter's actual answer lists, display labels, board/target hashes, target positions, actual cross-page repetition and Android screenshot; preserve old ones.
- Versioned CI commit, Android APK signing/upgrade, family/older-device playtest, observed touch issues and release decision. **No public approval inferred from CI green.**

## 8. What passes this WS63 milestone

1. A tested **read-only audit** can reproduce unique QA vocabulary counts across editions, identity reuse, tier/board eligibility and the 18 proposed chapter-pool gaps.
2. Every numeric table says **QA authoring**, **owner/public approval**, or **released playable**; it never mixes them.
3. The review process here is ready for human approval in later WS64–WS66 work; new words remain untouched until that explicit step.
4. Existing 63 historical + 18 WS57 frozen QA pages, legacy generators, Daily, Solo/Library state, Nearby and QA signer/version stay exactly as before.

**References:** [WS63 master roadmap](WS63_MASTER_VOCABULARY_EXPANSION_ROADMAP_2026-11-22.md), [existing WS10 validator](../tools/lexicon/validate.py), [existing exporter](../tools/lexicon/export_snapshot.py), [Grid Hunt GM03](https://github.com/PlayerEden/GridMates-Grid-Hunt/blob/main/docs/research/GM03_PUZZLE_QUALITY_AND_FAIRNESS_RESEARCH.md), [Grid Hunt GM08](https://github.com/PlayerEden/GridMates-Grid-Hunt/blob/main/docs/research/GM08_PLAYTEST_EVIDENCE_AND_PROTOTYPE_DECISIONS.md).
