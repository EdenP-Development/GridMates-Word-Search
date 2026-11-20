# WS52 — Five remaining Woodland chapters, 15 QA-only page manuscripts

> **Status:** **VERIFIED TECHNICAL QA / EDITORIAL HOLD.** Both independent Android + art/release workflows succeeded at exact source `ee3c390fb62813273eff07be4da2e18259c09bf5`, with byte-identical output reports. Significant cross-chapter repetition remains unapproved for publishing.

## Verified execution — 15 real exact-target QA puzzles and two identical reports

**Exactly tested GitHub source:** `ee3c390fb62813273eff07be4da2e18259c09bf5`. [Standard Android/core #44670927912](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44670927912) **SUCCESS**; [independent art/debug/release #44670927977](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44670927977) **SUCCESS**. The WS52 Python source guard passed in both, Kotlin source+unit tests generated the actual 15 V5 boards and regenerated each identically, all targets and IDs matched the 260-row authored manifest, BookCatalog/BookPageSession exact-hash checks passed. Standard Android/core also compiled QA APK, app tests and lint; independent debug/release and artwork isolation passed. These are automated checks only, not actual owner handset review or a new public book.

**Two independent research ZIPs:** [standard artifact #14599722700](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44670927912/artifacts/14599722700); [independent artifact #14600530944](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44670927977/artifacts/14600530944). Both archives contain the **same two byte-identical TSV payloads**, verified by downloading and comparing their content, not merely trusting CI success:

- `ws52-woodland-five-chapter-frozen-boards.tsv`, **10,024 bytes**, SHA-256 `3a67ea509861e67fc21ecbc50b44351036cc64d9cbea764fe65c73c543e0b14e` — all **15 actual letter grids**, their 260 targets, exact board hashes, page IDs/seeds, pack hashes and QA-only marking.
- `ws52-woodland-chapter-quality.tsv`, **1,515 bytes**, SHA-256 `ec5d69b4f5ef370c658e1b53b436d96a5e815e78a26d36e525013e7ae4515144` — page counts, mean target length, primary-topic counts and within-chapter uniqueness.

**Exact frozen board SHA-256 evidence:**

| Chapter | 12×12 / 12 Relaxed | 15×15 / 16 Classic | 20×20 / 24 Expert |
| --- | --- | --- | --- |
| Along the Trail (3) | `3d147a2d6a37c12d7d390fd3aaa44c70751665752e792deeec5e30c22643cead` | `5b97ad21253c9ead6037cf06167db54b2aba288681851ba19559483b3e9db22c` | `8d7404af8759dc447b5f2e5d860c70987d36934b5991f5985618ac8d4648cc5d` |
| Under the Canopy (4) | `fbc6a7794af59ffd61344b1567e66b216579e9aa013b1abfe1f90ee20d29a2a1` | `df9263b26921a527f7bb9eba20793e0edd4b1ab4b25a652a95ea59f82f5c6ba8` | `88006e67bc64db3e279fe4b6e9d6630dc0fc49bc7656d524a21e46e569eafbeb` |
| By the Stream (6) | `f307e544eba8f043971e3a22fe4649b836e41dac4cd1fe2c20ecb9908438cf8e` | `ead8a0611a63bd0842005ff86177e967fc95456f1386fe68dd02fc4df0a5a702` | `1ed6626b3bbeeb846958a03d061aeaa60ca370da72f7b1ec032c59194051b6a4` |
| Quiet Clearings (7) | `deadd98501fcb4409368b3fdb83eb7662410258d523e8b2d7fc9b70773441719` | `0227652c086320b0cad24b1a43ff03700a9eefa776592baad137f4d61dddc314` | `59bb845482ae6e4eee5b6217fa68ffb596bfc6fa5b7c00fc07f15763573158ed` |
| Forest Discoveries (8) | `43ee7216eff318fbb3015f98b1ced83da1e75bd8e922dd0713d06cb289c99928` | `8996cd4e19bb1fa132143738672dee5ec692f79cf11272a08fb409aeb8df0eb4` | `f30be3b7355cb55bb0e6a5a815450a07dcccd198c2d01d1da94d044d9cde3fab` |

## Quantitative editorial issue — validated repetition across chapter boundaries

The five manuscripts contain **260 target appearances**, **52 unique within each chapter**, but only **124 distinct answers overall**. Of those 124 answers, **86 occur in multiple chapters** and 136 of the 260 appearances repeat an answer already used in a different chapter. Page-by-page within-chapter repetition remains **zero**.

| Chapter pair | Words appearing in both |
| --- | ---: |
| Along the Trail ↔ Quiet Clearings (3 ↔ 7) | **48 of 52** |
| By the Stream ↔ Quiet Clearings (6 ↔ 7) | 33 |
| By the Stream ↔ Forest Discoveries (6 ↔ 8) | 30 |
| Along the Trail ↔ By the Stream (3 ↔ 6) | 29 |
| Under the Canopy ↔ Forest Discoveries (4 ↔ 8) | 25 |
| Along the Trail ↔ Under the Canopy (3 ↔ 4) | 0 |
| Under the Canopy ↔ Quiet Clearings (4 ↔ 7) | 0 |

**Decision:** The 15-board **generator and persistence QA passes**, but the book-content editorial gate is **NOT cleared**. The current Chapter 3 and Chapter 7 source scopes are identical (`trails,habitats`), so extreme overlap is unsurprising and should not be sold as polished variety. Review alternative distinct new chapter scopes (e.g. more trees/forest-detail vocabulary for Quiet Clearings), then grow the carefully reviewed source vocabulary if necessary. Do not silently rewrite original saved pages or automatically release any QA candidate.

**Scope proof:** Test-only fixture, Kotlin test, Python guard, docs and CI. No runtime generator/code, existing 63 QA book pages/IDs or saves, app artwork, LAN/Nearby, release packs, bonuses or profiles changed. QA build success does **not** mean these 15 pages are in the player's currently installed app. Owner physical QA and new versioned book edition integration remain separate future steps.

## Reason and owner goal
The owner chose permanent, deliberately curated book chapters and reusable random Quick Play. [WS50](2026-11-19_WS50_CHAPTER_RECIPE_BIRDSONG_LAB.md) identified named chapters but the old QA game currently selects words from a whole theme's pack. [WS51](2026-11-20_WS51_BIRDSONG_DIVERSITY_QA.md) proved manually authored 52 distinct bird-related targets can create frozen, valid three-page V5 chapters; 23 new Birdsong terms remain unapproved for release.

WS52 authors the other **five Woodland chapters** (3 Along the Trail, 4 Under the Canopy, 6 By the Stream, 7 Quiet Clearings, 8 Forest Discoveries), retaining Birdsong Chapter 5 and all historic Chapter 1–2 QA content.

## Exact files and scope
- [WS52 content blueprint](../WOODLAND_CHAPTER_EDITORIAL_PLAN_WS52.md).
- `data/lexicon/authoring/chapter-recipes-v1/woodland-ws52-manuscript.tsv`, mirrored byte-for-byte to `core/src/test/resources/chapter-recipes/ws52-woodland-manuscript.tsv`: **260 exact QA target assignments**, 52 unique per chapter, **124 unique answers across chapters**. Only existing original 159 Woodland QA source word IDs, spellings and topic assignments; no new words or release approvals.
- `WoodlandChapterManuscriptTest.kt`: source validation plus 15 deterministic V5 candidate page boards, each generated twice with immutable page-specific pack hashes/IDs, strict exact target list and physical board/catalog checks. Reports two inspectable TSVs.
- `test_ws52_woodland_manuscript.py`: exact authoring mirror, 5×(12/16/24) and progression, original canonical ID/tag checks, app/debug+release isolation, CI workflow guards.
- Two independent CI workflows run WS52 checks and upload full actual puzzles and small QA metrics. No production code or app version changed.

## Editorial findings before CI
- Each chapter's first page has familiar 4–7 letter answers, subsequent pages have longer targets. Primary-category quotas per page **5/6/9** are satisfied by the authored lists; additional allowed subtopics fill the remainder.
- Zero within-chapter target repetition (52 distinct per chapter). **Cross-chapter repetition is substantial**: the 260 required slots share 124 answers. This cannot be erased without richer vetted vocabulary or different chapter design; document as an editorial gate before selling physical-looking books.
- Some broad tags may create weaker thematic matches (e.g. stream-adjacent wildlife and clearing-adjacent wetlands). Category validation is not a substitute for owner reading and editing each page's answers.
- The WS51 Birdsong 52 separate birdlife targets were not modified or promoted. All 63 legacy QA Book pages, game saves, rewards, artwork, release vocabulary and LAN protocol remain unchanged.

## QA and completion gates
- Starting parent: WS51 [draft PR #163](https://github.com/PlayerEden/GridMates-Word-Search/pull/163), documented head `f0a832a3194f4514a37f5af21d36fd9373c2dbaa`; independently executed WS51 source `866713fe4b62e0bcf1266275e901f15a5f288270` successfully completed [Android/core #44668581064](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44668581064) and [art/release #44668581056](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44668581056).
- **WS52 source execution:** Pending at creation. If CI fails, do not overstate readiness; modify only WS52 test-only draft manuscript to satisfy existing game rules.
- **Verification required:** both independent workflow conclusions + compare actual generated 15-board report hashes. Source-level metadata checks alone do not prove successful puzzle generation.
- **Rollback:** WS51 approved-in-QA *test-only* Birdsong and existing 63 original QA book pages; no merge or device acceptance occurred.

Next Work task: check semantic theme quality and cross-chapter repetition, then consider additional source-vetted vocabulary or scope refinement, and test new *versioned* book editions on Android without altering prior saved IDs. The user does not need to install an APK at this research stage.
