# WS57 — Six-chapter Woodland book, playable Android QA preview

> **Stage:** **Verified signed Android QA preview ready for user phone playtest.** Both independent Android pipelines passed and dedicated emulator executed all three book/save-regression tests. Published release catalog remains empty; owner physical phone QA and editorial/word authorization are not granted.

## What the user can actually play (once signed QA build passes)

An **additional** book, *Woodland - Editorial QA Preview* (QA volume 2), appears in the existing physical-style Library alongside the existing three QA books. Opening it uses the same real native book detail, chapter/page picker, V5 touch word grid, hints, pause/resume, bookmarks and saved-page journal as every other QA book. It has these intentionally authored page lists, previously tested independently:

| Chapter | 12×12 Relaxed | 15×15 Classic | 20×20 Expert |
| --- | --- | --- | --- |
| Along the Trail (3) | First Steps | Following the Path | Into the Wilderness |
| Under the Canopy (4) | Trees Around Us | Branches and Neighbors | Canopy Secrets |
| Birdsong (5) | Forest Friends | Feathers and Flight | Birdsong Challenge |
| By the Stream (6) | Water's Edge | Life Along the Bank | River Currents |
| Quiet Clearings (7) | Open Glade | Sunlit Spaces | Woodland Stillness |
| Forest Discoveries (8) | Woodland Neighbors | Signs Along the Path | Wildlife Field Notes |

Exactly **6 chapters × 3 pages = 18 QA pages**; every page uses exactly its previously authored 12, 16 or 24 words. The source QA reviews yielded **264 different required answers across 312 total slots**, with 49 pairwise chapter intersections. **122 terms remain UNAPPROVED** (23 WS51 Birdsong, 33 WS54 Stream, 30 WS55 Wildlife, 36 WS56 Clearings) and must not enter any released pack, automatic bonus dictionary, Daily or Quick Play.

## Safe build and save boundaries

- **Debug-only Kotlin** `Ws57WoodlandPreview.kt` constructs an *additional* QA volume from `app/src/debug/assets/ws57_woodland_manifest.tsv` (312 source-frozen rows) and 18 previously verified, pinned hashes in `ws57_woodland_expected_boards.tsv`. It reconstructs each V5 page **with the exact original chapter source pack recipe, word ID/order, page challenge and seed**, confirms every digest matches the historically verified WS51/52/54/55/56 board SHA-256, and refuses to show altered pages if even one hash drifts. This is NOT a random new word selection.
- Existing `BookQaPilot.catalog()` remains the **exact original three QA books, 63 pages**. New `BookQaPilot.catalog(context)` appends the optional QA preview while verifying `BookCatalog.requireCompatibleUpgrade`. The release `BookQaPilot` always returns **empty catalog**. The preview asset files are under debug source set and absent from release APK.
- Existing QA save files `gridmates_wordsearch_ws11_qa_progress_v1.dat` and `gridmates_wordsearch_ws11_qa_sessions_v1.dat` are NEVER overwritten by the preview. On first use, the app safely **copies** old compatible progress/session bytes forward into separate `gridmates_wordsearch_ws57_qa_progress_v1.dat` and `gridmates_wordsearch_ws57_qa_sessions_v1.dat`, where new preview records live. This preserves rollback to the old APK and ensures user old-save bytes stay identical; a corrupt/unknown old file fails closed rather than being erased. No schema migration or wiping.
- Preview completions **never award permanent Solo collection rewards**, because `QaBookshelfRuntime.syncBookRewards` explicitly filters the new QA preview book ID before calling SoloStore; other old QA book rewards are unchanged. No change to actual Solo, Daily, Nearby, V5 engine, art, or incidental natural bonus scoring.
- Preview is a research/test-only page set, not a *licensed, approved production book*. No new app account, no cloud dependency.

## Required tests and evidence

1. **Verified** Python source-integrity regression checks all 312 in-APK answers against the authoritative WS52/51/54/55/56 source manuscripts, preserving exact spelling, original IDs, display labels, tiers and provenance, 18 pinned SHA-256 values, and absence of release leakage.
2. Both independent Android/core and art/release CI paths compile real debug and release APKs. The art/release pipeline verifies zero debug-only assets/classes in release.
3. **Verified** Dedicated Android instrumentation executed **three tests on an emulator** and ensures the old catalog/63 original pages are bit-for-bit unchanged, each of 18 new V5 boards matches pinned SHA, new book has the expected six chapters, and saved progress/sessions from the old catalog reopen without loss under the expanded catalog; new preview bookmark/session survives a store restart.
4. Physical Android owner test: navigate Home → Full Library → *Woodland - Editorial QA Preview*, open chapters, play each difficulty, watch readability on 12×12 /15×15/20×20, test hint/word-trace, back/continue, rotate phone, force-stop/reopen. No 122 candidate words get approved merely because the gameplay works.

**Verified evidence:** [Full exact tested SHA, signed APK digest and emulator instrumentation results](devlog/2026-11-21_WS57_WOODLAND_PLAYABLE_QA.md). Signed debug build SHA-256 `304b389d53935ceb8a8857666caf00fbc15e3c51a440ab3cced07f1f684e82f9`. The source test branch is still draft/unmerged.

**Version:** signed install-over QA/debug APK application ID `com.playereden.gridmates.wordsearch.qa`, versionCode 107, versionName `0.9.63-ws57-woodland-book-preview-qa`. This is not the Google Play release package.

[WS57 verified execution log (pending source runs)](devlog/2026-11-21_WS57_WOODLAND_PLAYABLE_QA.md) · [GitHub Work handoff](https://github.com/PlayerEden/GridMates-Word-Search/issues/140).
