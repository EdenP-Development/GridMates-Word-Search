# WS65-C — Source-checked lexical senses and chapter quality (draft; unsigned)

**Scope:** extend [WS65-B editorial preflight](WS65B_EDITORIAL_PREFLIGHT_2026-11-22.md) with **38 original factual source-evidence notes** and one reproducible **267-row evidence overlay**, without changing original proposals, QA catalogs, frozen book pages, saved games, or Android code.

## What is and is not established

- A factual source can establish that `CERES` names a dwarf planet, `ARIEL` names a moon of Uranus, `COSMOS` names a flower genus, or `STAMEN` names a flower part. **This does not establish whether the player finds the word friendly, how to rate its difficulty, whether the planned chapter is enjoyable, permission to redistribute a third-party list, or whether it should ship.**
- **38** proposal occurrences have specific, source-linked lexical facts and editorial implications. **229** other source rows remain without a WS65-C individual evidence record. Previous WS65-B preliminary labels remain intact: **162 PROMISING, 79 HOLD, 26 MOVE**. No count should be described as newly approved.
- The `data/lexicon/intake/ws65c-source-evidence-v1.tsv` input records `source_pool/answer`, factual interpretation, direct reference URL, editorial recommendation, a *proposed* destination and familiarity tier, and explicit `rights_status=NOT_CLEARED`. Author/owner decision columns are always blank.
- The `tools/lexicon/ws65c_source_evidence.py` script joins the 38 notes against the exact 267 source rows, enforces old IDs, aborts on missing references or attempted signoffs, and emits an evidence CSV, JSON receipt, SHA-256 file, and source-documentation. **GitHub CI does not browse these source links; references were examined during manual editorial preparation**, and must be re-checked at an actual formal signoff.

## Concrete corrected judgments

### Solar System

- **NASA names Ceres, Eris, Haumea and Makemake as recognized dwarf planets.** They are real astronomical names, but default Relaxed suitability is not established. See [NASA's planet overview](https://science.nasa.gov/solar-system/planets/).
- **NASA lists Miranda, Ariel, Umbriel, Titania and Oberon among Uranus's major moons.** Their multiple literary-name meanings, topic specificity and familiarity favor review as specialist/advanced answers, not broad random puzzles. [NASA Uranus moons](https://science.nasa.gov/uranus/moons/facts/).
- **COMA, NUCLEUS, DUST TAIL and ION TAIL** are comet-part concepts: preserve the full sense when editing chapters; `COMA` in particular is misleading without context. [NASA Space Place](https://spaceplace.nasa.gov/comets/en/).
- **TROJAN** is an asteroid category, not the historical city/person sense; stay on HOLD for standalone beginner play, even when valid for astronomy. [NASA Asteroid Facts](https://science.nasa.gov/solar-system/asteroids/facts/).

### Garden / nature

- **STAMEN, ANTHER, FILAMENT, PISTIL, STIGMA, STYLE, OVARY, SEPAL** are documented flower structures, but the source confirms meaning—not the decision that they belong in an entertaining `Flower Borders` book. Suggest **Flower Anatomy / Botany** as a more coherent separate chapter. [NC State botany guide](https://content.ces.ncsu.edu/extension-gardener-handbook/3-botany).
- **COSMOS** really denotes a garden flower genus as well as an astronomical term. Reuse its existing global canonical spelling rather than creating another word ID. [NC State Cosmos](https://plants.ces.ncsu.edu/plants/cosmos/).
- **RUDBECKIA** is a genus; `Black-eyed Susan` is a common name for certain related species, notably `Rudbeckia hirta`. These are *not globally identical spellings*, and not every Rudbeckia is a Black-eyed Susan; treat them as concept-overlap candidates and avoid one-page apparent duplicate plants. [NC State genus](https://plants.ces.ncsu.edu/plants/rudbeckia/), [species](https://plants.ces.ncsu.edu/plants/rudbeckia-hirta/).
- **NICOTIANA** is a legitimate ornamental flower genus; Extension warns several flowering-tobacco forms are toxic. Hold for familiarity/age-appropriate chapter tone rather than making unsupported claims that the word is invented. [NC State flowering tobacco](https://plants.ces.ncsu.edu/plants/nicotiana-x-sanderi/).
- **BLOODROOT, TRILLIUM, WILD GINGER** are shade woodland perennials documented by Iowa State Extension; **WOOD ANEMONE** is a European woodland spring flower in the Woodland Trust's reference, so en-US audience/regional context deserves specific checking. [Iowa woodland flowers](https://yardandgarden.extension.iastate.edu/faq/what-native-woodland-perennials-bloom-spring), [Woodland Trust](https://www.woodlandtrust.org.uk/trees-woods-and-wildlife/plants/wild-flowers/wood-anemone/).
- **WATER LILY** belongs comfortably in pond / still-water content rather than implying it is characteristic of a fast-flowing woodland stream. [Penn State aquatic-plant guide](https://extension.psu.edu/physical-control-of-aquatic-plants).
- **BIRDHOUSE, BIRDBATH, FEEDER** fit garden bird care and watching. [Maryland Extension wildlife guide](https://extension.umd.edu/resource/attracting-wildlife-your-garden).
- **ANNUAL, PERENNIAL, BIENNIAL** are life-cycle labels, and **DEADHEAD** is garden upkeep: recommend `Growing Season` instead of using them to pad a book of blooming flowers. [Minnesota Extension life cycles](https://extension.umn.edu/agriculture/crop-production/forages/legume-life-cycles-and-characteristics) and [deadheading](https://extension.umn.edu/garden-and-home/yard-and-garden/gardening-in-minnesota/growing-geraniums-in-minnesota).

## Release and approval safeguards

This is a **source-referenced editorial workbench, not a signed QA, owner or production approval**. All 267 source proposals remain as originally written; no word was added to the English/Fall approved-in-QA counts, 1K goal, pack manifest or APK. No word's rights status was cleared. A source citation **does not** permit copying an external reference's whole list, description or illustrations.

**Next:** verify the remaining **61 HOLD**, **6 MOVE**, and **162 PROMISING** source proposals lacking individual WS65-C evidence, using the exact source-coverage totals from the CI receipt. After full word-sense, locale, familiarity and licensing checks, a separate signed decision sheet can feed a future WS66 versioned QA pack and 12×12 / 15×15 / 20×20 generated trial boards. Leave source names, old pages and original saves immutable until explicitly authorized.
