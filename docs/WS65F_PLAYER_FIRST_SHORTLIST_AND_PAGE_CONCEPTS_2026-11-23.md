# WS65-F — Player-first chapter shortlist and opening-page concepts
**November 23, 2026 · unsigned content research and UX mock word lists · not release approval**

## Why this stage exists
The WS63–WS65-E chain built a rigorous vocabulary-inventory system, but a gigantic dictionary alone does not make a delightful word-search game. WS65-F starts prioritizing *what players would enjoy seeing*: readable, natural words that evoke an identifiable chapter, then difficulty variation as they progress.

## Exactly what was prepared
- **80 source-reconciled draft candidates**, all from the 162 previously PROMISING entries lacking WS65-C/D/E individual evidence. Each includes a *fresh author-written sense* and *player-readability comment*, and a general topic/context reference—not an independent one-word dictionary verification. Split: **12 Birdsong, 12 By the Stream, 12 Quiet Clearings, 12 Forest Discoveries, 16 Our Solar System, 16 Flower Borders**.
- **3 example opening-page word lists, 12 slots each**, drawn only from these 80 words. These are **NOT generated or playable V5 boards**, and are not new frozen book pages or Android data. Static tests check page identity, ordering, word uniqueness, V5 palindrome/length/tier constraints and basic substring confusion, not grid placement or uniqueness in generated filler letters.
- **82 other PROMISING proposals** still await individual context treatment after this first batch. The previous 105 individually sourced HOLD/MOVE notes remain unchanged. The 80 new entries contain author-written player-fit notes and topic references. Editor decisions are still open.
- Tier caveats matter: the previous editorial suggestions for **Talon, Minnow, Tadpole, Mayfly, Comet, Asteroid, Meteorite, Meteoroid, Crater, Atmosphere, and Gas Giant** stay **Intermediate**, not automatically Relaxed/Familiar. All skill labels are tentative.

## Concept pages for editorial review, not released puzzles

### Woodland / Birdsong: “Feathers & Friends” — Classic 15 concept
BEAK · WING · NEST · EGGS · CHIRP · FLOCK · FEATHER · SONG · FLIGHT · PERCH · ROOST · TALON

The page should feel like a moment of birdwatching, with visible/recognizable objects and sounds. TALON is retained as an Intermediate stretch. Do not pair overly similar stem words on the same board without independent mechanical QA.

### Space / Our Solar System: “Our Celestial Neighborhood” — Classic 15 concept
MERCURY · VENUS · EARTH · MARS · JUPITER · SATURN · URANUS · NEPTUNE · PLUTO · COMET · ASTEROID · CRATER

PLUTO is explicitly a **dwarf planet**, not the ninth major planet. COMET, ASTEROID and CRATER remain Intermediate pending actual audience testing. This page emphasizes recognizable objects rather than orbital-mechanics jargon.

### Garden / Flower Borders: “A Colorful First Bloom” — Relaxed 12 concept
ZINNIA · MARIGOLD · PETUNIA · PANSY · IMPATIENS · BEGONIA · SNAPDRAGON · PEONY · IRIS · DAYLILY · CONEFLOWER · HOLLYHOCK

Use the player's everyday flower names before obscure Latin genera. Separate Calendula from common Marigold (Tagetes) concept overlap during the human page review. Keep sun/shade or toxicity issues in background research, not injected into a calming puzzle-word list.

## Supporting topic sources (context only)
- Cornell Lab of Ornithology, [Bird Academy bird-term glossary](https://academy.allaboutbirds.org/bird-academys-a-to-z-glossary-of-bird-terms/) — sound, anatomy, feathers and nesting context.
- NPS [Rivers and Streams](https://www.nps.gov/agfo/learn/nature/rivers.htm) — stream, bank, aquatic fauna, plants and habitat context.
- NPS [Forests](https://www.nps.gov/subjects/forests/index.htm) and [Animal Tracks](https://www.nps.gov/articles/000/animaltracks.htm) — wildlife signs and woodland outdoor context. They do not prove every selected word belongs to every woodland ecosystem.
- NASA [Solar System Facts](https://science.nasa.gov/solar-system/solar-system-facts/) — planets, dwarf planets, asteroids and comets.
- NC State Extension [Plant Toolbox](https://plants.ces.ncsu.edu/) — familiar ornamental garden plants, botanical synonyms and their distinct senses.

Each reference is general **context** for writing original editorial explanations. CI does not access external URLs, establish licensing to copy a dictionary, independently confirm a term's frequency/familiarity, or grant editor/owner approval. Owner release fields remain blank and rights stay NOT_CLEARED.

## Quality boundary and next work
The script `tools/lexicon/ws65f_player_first_shortlist.py` joins exact historical proposed IDs, denies overwritten tier cautions, exports the 80-row unsigned CSV, 36-slot word-list concepts, JSON metrics and checksums, and **never** touches previous frozen pages, English/Fall authoring, Android assets, save identities or generator seeds.

**WS65-G** should research the remaining 82 promising words and perform explicit human editor decisions on chapter fit and player friendliness, with licensing review. **WS66** may then attempt entirely new versioned QA-only V5 12×12, 15×15 and 20×20 boards, score actual adjacent-page repeats/valid placements and prepare a hands-on Android test **only after** the new pack/puzzles are ready. Do not suggest the three static word lists have been playtested.
