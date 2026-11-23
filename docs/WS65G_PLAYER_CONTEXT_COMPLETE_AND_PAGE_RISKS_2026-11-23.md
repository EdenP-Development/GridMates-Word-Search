# WS65-G — All promising chapter candidates now have player-context notes
**November 23, 2026 | draft/unmerged | research-workbench only**

## What this milestone actually completes

WS65-F selected 80 of 162 previously unreviewed PROMISING source proposals. WS65-G adds **82** more hand-authored word senses and player-readability notes, with individual source proposal IDs and topic-context links. The resulting **267** total proposal occurrences are now *covered by some kind of editorial context*, but the strength of evidence differs dramatically:

| Evidence level | Proposal occurrences | This does **not** mean |
|---|---:|---|
| WS65-C/D/E term-specific source research notes | 105 | Rights clearance, human editor signatures, familiar to players, or public approval |
| WS65-F authored player-context glosses | 80 | Independent dictionary verification, source licensing, or actual playtesting |
| WS65-G authored player-context glosses | 82 | Independent dictionary verification, source licensing, or actual playtesting |
| **Total proposed source records with context** | **267** | **Not 267 approved words** |

The earlier **200 potentially new answer spellings relative to the English/Fall editions** remains a purely hypothetical set that includes all HOLD/MOVE and other unapproved candidates. Some WS64/WS65 source proposals share exactly the same normalized spelling and should reuse a single global canonical identity.

## Source-based, human-aware vocabulary quality

New editorial notes preserve the intent of each chapter:

- **Birdsong (8):** Hatch, Plumage, Migrate, Wingspan, Fledgling, Birdsong, Hatchling, Nestling. All are recognizably bird-related but multiple words are near-duplicate life stages or contain whole shorter answers.
- **By the Stream (17):** Current/Rapids/Meander, stream geography, insects, animals and scenery. `CURRENT` and `RUSHING` require the stream sense; `RIVERBED` and `STREAMBED` are near synonyms deserving separate pages or a deliberate educational comparison.
- **Forest Discoveries (12):** Camouflage, Hibernation, Nocturnal, feeding categories and wildlife observations. Predator is different from Scavenger; Herbivore/Carnivore/Omnivore can appear as a meaningful learning sequence but need suitable page pacing.
- **Quiet Clearings (14):** Pollen, Rustling, Warmth, Dandelion, Fireflies, Buttercup, etc. More sensory and scenic vocabulary. FOXGLOVE, PRIMROSE and POLLEN repeat answers also proposed in the Garden family; reuse one canonical word spelling, moderate repetition between collectible books.
- **Our Solar System (17):** named planetary moons, asteroid belts, dwarf/ice giant classes and iconic landmarks. `OORTCLOUD` is a hypothetical faraway region, not a spacecraft-photographed cloud. `VALLESMARINERIS` is a long advanced target, unlike approachable planetary page answers.
- **Flower Borders (14):** cultivated plants, blossom structures, and border terms. The overlapping terms are legitimate for multiple themes, but FLO​WERBORDER is 12 normalized letters and fails Relaxed-12 length policy. Distinct plant concepts are prioritized over padding with generic garden care.

Reference topics for independent follow-up:
- [Cornell Lab Bird Academy](https://academy.allaboutbirds.org/bird-academys-a-to-z-glossary-of-bird-terms/)
- [USGS Rivers, Streams, and Creeks](https://www.usgs.gov/special-topics/water-science-school/science/rivers-streams-and-creeks)
- [NPS Forests](https://www.nps.gov/subjects/forests/index.htm) and [Animal Tracks](https://www.nps.gov/articles/000/animaltracks.htm)
- [NASA Solar System Facts](https://science.nasa.gov/solar-system/solar-system-facts/), [NASA Moon Facts](https://science.nasa.gov/solar-system/moons/facts/) and [NASA Oort Cloud](https://science.nasa.gov/solar-system/oort-cloud/)
- [North Carolina Extension Gardener Plant Toolbox](https://plants.ces.ncsu.edu/)

All topical references are context for original descriptions, not copied third-party dictionary lists. CI checks our stored references and content integrity, not live external pages, commercial rights, or player familiarity.

## More valuable than one big word count: risk reporting

`tools/lexicon/ws65g_editorial_coverage.py` reads WS65-E and WS65-F *without modifying them* and verifies that the new 82 proposals exactly equal the rest of the promising queue. It emits:
- A source-identity-preserving unsigned review CSV, with per-word 12x12 / 15x15 / 20x20 **theoretical** word length/tier/palindrome eligibility.
- A JSON inventory of every answer spelling shared by different chapters, within-chapter substring pairs, and editorially curated semantic overlap groups.
- SHA256 checksums and regression proof that approvals remain zero, English/Fall authoring stays intact, original frozen 63+18 pages are unchanged, and no Android code or save IDs were modified.

A duplicated spelling or substring isn't inherently a bad word. The **page planner** should avoid placing `SONG` alongside `BIRDSONG`, `NEST` alongside `NESTLING`, and `WING` alongside `WINGSPAN` if it can cause ambiguous selection/target matches. Similarly, don't confuse semantic differences such as HATCHLING vs NESTLING or RIVERBED vs STREAMBED.

## Real next milestone — avoid an endless series of worksheets

**WS66-A** should move beyond research paperwork by creating a **separate QA-only, non-released experimental selection** with explicit negative/hold restrictions and *actual* fresh V5 12/15/20 placement trials, no mutations to existing page IDs or saves. Report real per-page answer placement, unique occurrence, cross-page repetition and failure cases; do not confuse theoretical fit with a generated board. Formal authoring pack activation/public release must still pass human editor signoff, source rights/asset ownership review and owner approval.

Current vocabulary totals are still **787 internal-QA unique English/Fall words**, **0 new public-release approvals from WS63–65-G**, and **0 new Android playable answers**. The source counts are not additive to approved totals.
