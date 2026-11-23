# WS66-A — Actual V5 trial boards from unsigned editorial vocabulary

**November 23, 2026 — Scratch Kotlin JUnit only; NO APK, book, QA runtime activation, or public release.**

## Why

WS65-F presented **12-word opening-page concepts** for Birdsong, Our Solar System, and Flower Borders. Those were editorial sample lists, not valid complete Classic puzzle manifests: the existing V5 generator's real targets are **12 on 12x12 Relaxed**, **16 on 15x15 Classic**, and **24 on 20x20 Expert**. WS66-A now tests **actual Kotlin V5 placements** on three *new* scratch-only candidate pools with the correct required target counts, without changing production content.

The test-only resource `core/src/test/resources/ws66a/qa-only-v5-candidate-pools.tsv` contains **89 source-reconciled proposal occurrences**:
- **Flower Borders:** 30 words, 29 suggested Familiar. Test **12x12 Relaxed**.
- **Our Solar System:** 33 words, only **9 suggested Familiar**. Test **15x15 Classic**. A fully new 12x12 Relaxed page would be at least **3 familiar words short** if limited to these 33 unapproved proposals, and likely needs more than three once semantic and mechanics review is applied.
- **Quiet Clearings:** 26 words, 15 suggested Familiar. Test **20x20 Expert**.

Every record reuses the existing English/Fall QA canonical identity where one exists, otherwise preserves the source author's provisional proposal ID. No new globally approved word IDs are invented. The Python `ws66a_test_fixture_guard.py` joins against the WS65-E/F/G histories and rejects any drift in spelling, display, theme, tier, source provenance, or count.

## Actual Kotlin generator tests

`core/src/test/kotlin/com/playereden/gridmates/wordsearch/core/Ws66aV5ScratchTrialTest.kt` constructs valid test-local `ReviewedWordPack` data with the existing `ReviewedWordPackCodec`. It invokes **the unchanged production V5 generator** with the actual board size/challenge presets and three fixed seeds. For every successful board it checks:

- Correct size and target count, and deterministic regeneration from the exact same seed/pack identity.
- Full `FrozenWordSearchCodec` binary encode/decode round trip, so a complete real board restores exactly.
- Every answer path claims as `FOUND` using the normal `WordSearchRules`, and completing every target satisfies `WordSearchRules.complete`.
- An immutable digest of all board letters and target positions via `BookBoardDigest.sha256`.
- Printed/archived `WS66A` evidence with per-seed PASS/FAIL, exact SHA256, answer list and difficulty.

The bounded generator can still fail to place some seeds. The lab records a failure instead of pretending a grid worked; each case must have at least one real successful board or CI fails. Unsuccessful cases should guide future content/pool expansion, not forced retconning of old puzzles.

## Non-activation constraints

- New fixture exists **only in core/src/test/resources**; Kotlin runner exists **only under core/src/test**; no `app/src/main`, `app/src/debug`, published book catalog, recovery pack, active lexicon or save file is modified.
- The tested source proposals are still **NOT OWNER APPROVED** or cleared for redistribution. Being able to generate a puzzle does not make them publication-ready. This does not change the baseline 787 distinct internal-QA English/Fall spellings or authorize 200 potential-new spellings.
- The JUnit trial is not an Android touchscreen, accessibility, orientation, real-device, bookmark or visual QA pass. It tests the **actual grid generator** and frozen snapshot compatibility only.
- Original 63+18 frozen puzzles and previous Android QA apps remain unchanged.

## Next
Use actual placement failure and repeat counts to refine a **reviewed** chapter selection, add at least three familiar Solar System candidates (ideally several more for replay), plan new book/page IDs and owner review workflow, then test the candidate UI in a debug-only APK before public release.
