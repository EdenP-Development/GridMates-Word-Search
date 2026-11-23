# WS65-B — Editorial preflight across Woodland, Solar System, and Flower Borders
**Draft review workbench — not a publisher or authoring release.**

## What is actually new

- A stable set of **133 candidate-specific preflight annotations** in `data/lexicon/intake/ws65b-editorial-annotations-v1.tsv`, authored for chapter suitability, nuanced terminology, anticipated reader familiarity, and tier suggestions.
- A **read-only** generator `tools/lexicon/ws65b_editorial_triage.py` combining the historic **122 WS64 Woodland proposals** and **145 WS65 specialist proposals**, creating one **267-row unsigned review CSV** and JSON receipt without editing either source, the canonical English/Fall editions, any frozen pages, or Android saves.
- Validation and GitHub workflow for the exact input identity, required context, source annotations and blank owner/reviewer signatures.

**Three strictly preliminary lanes**: `PROMISING` (prioritize for careful human review), `HOLD` (obtain lexical/meaning/familiarity/license evidence), `MOVE` (keep the answer in the global library if suitable but consider another named chapter). None of these is a positive QA approval, editor signoff, or owner/public approval. An unannotated source row receives only a transparent *group-default* preflight suggestion. Every proposal still needs individual substantive review.

## Important correction to the original 145-word intake

The original batch contained more than one kind of issue:

1. **COSMOS** has a legitimate ornamental flower sense but is also ordinary Space vocabulary; same spelling/canonical identity, new topic association needs sense review. Do not create duplicate canonical IDs because it has two meanings.
2. **Black-eyed Susan** and **Rudbeckia** are closely related botanical naming; they can be distinct spellings but can also represent highly overlapping puzzle concepts. Prefer natural early-page wording, audit repeated concepts, verify genus/species; never blindly double-count *concept novelty*.
3. **STAMEN, PISTIL, ANTHER, SEPAL** and other flower anatomy terms are legitimate botanical concepts, better suited to a focused botany chapter than filling a delightful Flower Borders page. Similar for garden-care vocabulary `DEADHEAD`, `ANNUAL`, `PERENNIAL` and `BIENNIAL`: probably better in Growing Season.
4. **ARIEL/MIRANDA/TITANIA/OBERON/UMBRIEL** are real Uranian moons but not obvious beginner words; maintain NASA context and keep them on Hold until reader-tier validation. **COMA** is comet terminology but collides with common medical usage, and **TROJAN** must mean Trojan asteroid.
5. **BLACK-EYED SUSAN, GREAT RED SPOT, VALLES MARINERIS** have precise display strings but normalized length makes them ineligible for smaller boards. Eligibility is a *per-board* constraint, not a deletion from the global library.
6. Woodland older chapter issues include **WATERLILY** in a flowing stream, **CAMERATRAP** in broad Forest Discoveries, and shade-preferring woodland-floor flowers in **Quiet Clearings**. These are suggested MOVE/HOLD opportunities, not thrown-away vocabulary.
7. Suggest a **different tier for some candidates**: specialist flower genera originally labeled familiar and rare celestial objects should not appear in default Relaxed solely because their first batch proposed that tier. Human decisions still required.

## What gets reviewed next (WS65-C)

A reviewer processes the generated unsigned CSV in order: `MOVE`/ `HOLD` first, then `PROMISING`, inspecting original manuscript/page, exact canonical word ID, intended displayed sense, supporting dictionary/science/botany reference and licensed source. Confirm or revise subtopic and familiarity tier. Record explicit `qa_editor_decision` and evidence **in a new signed edition**, never by editing historical sources or this unsigned receipt in place.

Only after a meaningful validated set exists should WS66 create a **versioned new snapshot** with actual 12/15/20 full-board placement, no-substring and target-once checks, page-level repeat measurements, Android/touch/restore QA, publisher permissions and owner approval. Original 63+18 frozen QA boards and legacy saves remain immutable. No new APK is needed in this preflight milestone.

### Source context references — for later independent verification

- NASA Solar System overview: https://science.nasa.gov/solar-system/solar-system-facts/
- NASA Solar System moons: https://science.nasa.gov/solar-system/moons/facts/
- NASA Uranus moons: https://science.nasa.gov/uranus/moons/facts/
- North Carolina Extension Rudbeckia and common names: https://plants.ces.ncsu.edu/plants/rudbeckia/
- North Carolina Extension horticultural ornamentals: https://content.ces.ncsu.edu/extension-gardener-handbook/10-herbaceous-ornamentals

**Important evidence boundary:** Links above support the general identity/relationship flags, but have not been checked word-by-word for all 267 proposals or used as bulk permission/rights evidence. Numeric results in the CI JSON receipt are not approved vocabulary counts. All prior PRs WS63 #176, WS64 #177, WS65-A #178 remain **draft**.
