# WS56 — Open glade and woodland floor chapter, six-chapter diversity

**At authoring:** New 52-word Chapter 7 QA manuscript, Kotlin V5 frozen-board generator tests and independent Python source checks committed. **Both GitHub CI runs pending**; no claim of verified board hashes yet.

- WS56 builds from **verified WS55 [PR #167](https://github.com/PlayerEden/GridMates-Word-Search/pull/167)** (original source `59004f28bc7a0bcafcd15e876278e56ed1464215` passed [Android #44923963483](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44923963483) and [independent art/release #44923963465](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44923963465)). The unmerged duplicate WS55 PR #168 was closed, not a baseline.
- New research [source manuscript](../../data/lexicon/authoring/chapter-recipes-v1/woodland-ws56-clearings-manuscript.tsv), exact test mirror, **52 unique required words** in 12/16/24 authored pages *Open Glade*, *Sunlit Spaces*, *Woodland Stillness*. **16 exact canonical Woodland QA words** and **36 assistant-proposed `ws56_clearings_candidate_unapproved`** entries; 4.92/7.38/9.17 letter averages.
- Six-chapter proposed vocabulary count **233→264** distinct in 312 slots; sum 15 chapter-pair overlaps **82→49** and max pair **26→14**. Under Canopy/Quiet Clearings **26→0**, Along Trail/Clearings **18→14**, Stream/Clearings **3→1**. Source-only comparison; do not imply installed game.
- Test-only Kotlin `QuietClearingsBookwideLabTest` checks original ID/label/tier, 36 explicit unapproved terms, 3 genuine V5 board generations each repeated, exact 52 targets and BookCatalog frozen reconstruction; writes 3 inspectable reports. Python source/provenance/old-release-guard plus two CI artifact paths.
- **Technical CI status to be filled after actual runs**; no production game/lexicon/code, 63 existing QA page IDs/saves, previous QA books/manuscripts, artwork, scoring, LAN or Work branch changed.
- Next editorial check: word meaning, title fit, possible overly garden-centric flora, readable compound displays, actual mobile puzzle layouts and a new versioned edition gate.

[WS56 design & safeguards](../WOODLAND_QUIET_CLEARINGS_WS56.md) · [Work coordination](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
