# WS65-D — Second-pass held vocabulary and all outstanding chapter moves

**Status:** draft editorial source research, no signed QA word approval, no owner/public release approval, no Android activation.

## Input
The WS63–WS65-C stack holds **267 historical candidate records**, not approved new words: Woodland 122 and Solar System/Garden 145. Before this milestone, WS65-C had **38** proposal-specific evidence entries and **229** proposals without one. Its original WS65-B triage was **79 HOLD / 26 MOVE / 162 PROMISING**. Some earlier "move" proposals were editorial guesses, not authoritative facts.

## New second pass
`data/lexicon/intake/ws65d-second-pass-evidence-v1.tsv` adds **36** independently source-referenced candidate reviews, exactly **30** previously unreviewed HOLD and **6** previously unreviewed MOVE rows. New second-pass details are joined via `(source_pool, answer)`, never by rewriting old manuscripts or existing canonical IDs. The combined unsigned ledger now has **74** proposal-specific evidence records. **193** remain without individual evidence, specifically **31 HOLD**, **0 MOVE**, **162 PROMISING**. No entry counts as newly approved vocabulary.

Five moves receive supporting vocabulary/context references for the suggested alternate chapter: TERRESTRIAL (more specific Planetary Science), BOUQUET (Flower Arranging), CAMERATRAP (Wildlife Research), NESTBUILDING (Birdsong), PICNIC (Outdoor Activities). The sixth, **SHORELINE, is deliberately *RECONSIDER_MOVE***: Penn State itself discusses a 'streambank shoreline interface' for stream repair. **By the Stream** is therefore not lexically wrong; the real editorial choice is whether STREAMBANK would be more natural in a cozy book. We do not erase the previous MOVE from WS65-B; we document this later correction.

## Science vocabulary
NASA sources validate BENNU and VESTA as asteroid names, REGOLITH as surface material, PERIHELION/APHELION/ECCENTRICITY as orbit terms, INCLINATION/RETROGRADE/PROGRADE as orbital-orientation terms, MAGNETOSPHERE as a magnetic-region term, and REVOLUTION as orbital motion. Science truth is not proof that a casual reader recognizes a technical answer. These remain candidate-only and are provisionally specialist/advanced when appropriate, with context-specific sense notes. Some are better in a separate **Planetary Science** book than in the approachable **Our Solar System** chapter.

References:
- https://science.nasa.gov/solar-system/asteroids/101955-bennu/facts/
- https://science.nasa.gov/mission/dawn/science/vesta/
- https://science.nasa.gov/solar-system/why-bennu-10-reasons/
- https://science.nasa.gov/solar-system/orbits-and-keplers-laws/
- https://science.nasa.gov/learn/basics-of-space-flight/glossary/
- https://science.nasa.gov/lunar-science/resources/glossary/
- https://science.nasa.gov/universe/glossary/

## Garden / Woodland
Twelve held flower names now have specific North Carolina Extension plant references, covering AGERATUM, CELOSIA, CLEOME, GOMPHRENA, PORTULACA, TORENIA, SCAEVOLA, GAZANIA, NEMESIA, ANGELONIA, HELIOTROPE, and GAILLARDIA. GAURA gets an explicit taxonomy-drift note (modern OENOTHERA), and VERONICA is tied to a flowering plant genus rather than the personal name. These are useful valid words but **not necessarily Familiar**. A future cozy book may prefer more readable common names like **Moss Rose, Globe Amaranth, Fan Flower, Summer Snapdragon, Blanket Flower, Speedwell**, each evaluated as its own potential canonical answer rather than silently replacing the historical draft.

Woodland SPRING is grounded in the USGS freshwater source sense (vs the season); DROPPINGS is real wildlife sign with a separate family-tone decision; CHAMOMILE, WOODSORREL and MEADOWSWEET are real botanical words with context/region caveats. Word-specific evidence does **not** complete the commercial license ledger or demonstrate an actual generated puzzle.

References:
- https://plants.ces.ncsu.edu/plants/
- https://www.usgs.gov/water-science-school/science/springs-and-water-cycle?page=1
- https://extension.umd.edu/resource/vegetable-seedlings-or-transplants-are-damaged-eaten-or-fall-over
- https://extension.psu.edu/backyard-stream-repair-faqs
- https://extension.psu.edu/spying-on-wildlife
- https://www.audubon.org/magazine/appreciate-these-magnificent-avian-nests-and-next-one-you-find-field
- https://dictionary.cambridge.org/us/dictionary/english/picnic
- https://www.oxfordlearnersdictionaries.com/us/definition/english/bouquet

## Constraints and next
- The GitHub workflow validates reproducible source-to-review joins, counters, written evidence, explicit rights=NOT_CLEARED, new-vs-existing ID stability, missing decisions and immutable old data. Its source URLs are not spidered or officially license-cleared by CI.
- `RECONSIDER_MOVE` reflects a quality correction, *not* approval or automatic reassignment.
- The **200** previously candidate-new word spellings across the 267 proposals remain hypothetical. No new QA/editor/owner approvals and no new playable Android answers.
- Before WS66 production-adjacent authoring: finish remaining 31 HOLD and 162 PROMISING cases, source/meaning/US English, rights, tier, owner decisions, then create *new* versioned pack and trial 12/15/20 boards. Preserve old 63+18 pages and saves. Only build an APK when actual gameplay/content is ready for owner device test.
