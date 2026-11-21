# WS56 — Open glade and woodland floor chapter, six-chapter diversity

**Final WS56 technical status: VERIFIED; EDITORIAL PUBLICATION NOT APPROVED.** Both independent Android pipelines passed exact source `22b40df40b842d81bd7b4b5f675fbe91b244c1a0`, and three report TSV payloads were independently downloaded and matched byte-for-byte.

## Source-verified QA result — two Android pipelines, three actual V5 boards

**Tested exact source:** `22b40df40b842d81bd7b4b5f675fbe91b244c1a0` on [draft PR #169](https://github.com/PlayerEden/GridMates-Word-Search/pull/169). [Standard Android/core #44962609725](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44962609725) **SUCCESS**, [independent art/debug/release #44962609720](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44962609720) **SUCCESS**. Both performed WS56 Python source/old-library isolation and Kotlin V5 real-board generation + exact-target reproduction/catalog checks, then passed their Android/core/app or debug/release/art gates. Initial code attempt on the same branch failed on a Python dictionary-construction error, fixed before this **exact-source** successful rerun; don't confuse old failed/cancelled attempts with the verified final result.

**Two independent downloaded research ZIPs:** [standard artifact #14730232892](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44962609725/artifacts/14730232892) and [art/release artifact #14730257818](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44962609720/artifacts/14730257818). Both archives contain the **same three TSV payloads byte-for-byte**, as independently downloaded and checked in-container:

| Report | Bytes | SHA-256 |
| --- | ---: | --- |
| `ws56-clearings-frozen-pages.tsv` | 2,069 | `edaf5b2e248fbdbc2935aff4233d557f95fefd2d5b146cbb107774faccdf8803` |
| `ws56-bookwide-overlap-delta.tsv` | 464 | `56e5a584f28238d3c349f8ab196c68bc11c63db80a1437c6b59e300a8a4b6734` |
| `ws56-bookwide-word-frequency.tsv` | 11,282 | `04bdd08df23d8eaab60efda341b08ee557d1a81c093e0a1824f1ab50beb5f835` |

**Actual source-hashed frozen QA candidate puzzle boards:**

| QA page | Grid & required words | Challenge | Board SHA-256 |
| --- | --- | --- | --- |
| Open Glade | 12×12, 12 targets | Relaxed | `370388cd8c69a9b74c6061a3aff22dcbf5d6c946d3f16504d6bca4ebd00e085e` |
| Sunlit Spaces | 15×15, 16 targets | Classic | `799cd2e96e3afce7cf1628c1e3fcfc95e75919e71612895b640b15cd4fdc22bd` |
| Woodland Stillness | 20×20, 24 targets | Expert | `b022dd28b1d4b953075e989b216e27c5bc75622c5168a9a47809d05416f315fa` |

**Independent letter-grid inspection:** Every exported row matches its expected width (12/15/20). Independently scanning all eight straight directions for each of the 52 target answers found **exactly one actual path per required word**, no missing or duplicated selectable target paths. This is separate from the V5 exact-target, seeded repeatability and `BookCatalog` regression.

**Verified six-chapter bookwide manuscript outcome:** 312 required target appearances in Chapters 3–8. Distinct answers **233→264**, repeat appearances **79→48**, total chapter-pair intersections **82→49**, maximum pair overlap **26→14**. Ch7 Quiet Clearings intersections: ↔Ch3 Along Trail **18→14**, ↔Ch4 Under Canopy **26→0**, ↔Ch5 Birdsong **1→0**, ↔Ch6 By Stream **3→1**, ↔Ch8 Forest Discoveries **0→0**. Full frequency list has **264 rows**, with **122 answers explicitly tagged UNAPPROVED** (WS51 23, WS54 33, WS55 30, WS56 36). All 16 canonical reused terms remain source-faithful. **No release or owner editorial signoff**; many flora words remain awaiting editorial review for readability and tone.

**No app/user impact:** The 63 original QA playable book pages/IDs, saved progress, actual runtime generator/bonus dictionary, Quick Play/Daily, artwork, Nearby, prior frozen chapter manuscripts, release vocabulary, and Work/main were not changed. This is a draft test-only alternate edition *proposal*. A future playable QA edition requires explicit **new versioned IDs**, save-migration regression and owner physical-device testing.

- WS56 builds from **verified WS55 [PR #167](https://github.com/PlayerEden/GridMates-Word-Search/pull/167)** (original source `59004f28bc7a0bcafcd15e876278e56ed1464215` passed [Android #44923963483](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44923963483) and [independent art/release #44923963465](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44923963465)). The unmerged duplicate WS55 PR #168 was closed, not a baseline.
- New research [source manuscript](../../data/lexicon/authoring/chapter-recipes-v1/woodland-ws56-clearings-manuscript.tsv), exact test mirror, **52 unique required words** in 12/16/24 authored pages *Open Glade*, *Sunlit Spaces*, *Woodland Stillness*. **16 exact canonical Woodland QA words** and **36 assistant-proposed `ws56_clearings_candidate_unapproved`** entries; 4.92/7.38/9.17 letter averages.
- Six-chapter proposed vocabulary count **233→264** distinct in 312 slots; sum 15 chapter-pair overlaps **82→49** and max pair **26→14**. Under Canopy/Quiet Clearings **26→0**, Along Trail/Clearings **18→14**, Stream/Clearings **3→1**. Source-only comparison; do not imply installed game.
- Test-only Kotlin `QuietClearingsBookwideLabTest` checks original ID/label/tier, 36 explicit unapproved terms, 3 genuine V5 board generations each repeated, exact 52 targets and BookCatalog frozen reconstruction; writes 3 inspectable reports. Python source/provenance/old-release-guard plus two CI artifact paths.
- **Two independent Android CI pipelines passed and exact three report checksums/board hashes were verified** (details above); no production game/lexicon/code, 63 existing QA page IDs/saves, previous QA books/manuscripts, artwork, scoring, LAN or Work branch changed.
- Next editorial check: word meaning, title fit, possible overly garden-centric flora, readable compound displays, actual mobile puzzle layouts and a new versioned edition gate.

[WS56 design & safeguards](../WOODLAND_QUIET_CLEARINGS_WS56.md) · [Work coordination](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
