# WS52 — Five remaining Woodland chapters, 15 QA-only page manuscripts

> **Status at creation:** Source manuscripts and offline board-generation QA committed; actual Android/core and independent release-art CI **pending**. Do not claim any board hash or APK has passed yet.

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
