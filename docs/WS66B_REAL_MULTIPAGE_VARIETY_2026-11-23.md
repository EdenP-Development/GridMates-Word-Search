# WS66-B — Real multi-page replay variety lab

**November 23, 2026 · draft/unmerged · Kotlin V5 test source set only · no APK or new published content**

The WS66-A milestone demonstrated that the real generator can produce nine valid standalone 12×12, 15×15 and 20×20 boards. A playable *book*, however, has another quality requirement: **consecutive pages should not feel like the same word list rearranged.** WS66-B measures this rather than assuming it.

## What is actually being tested

Using the existing, unapproved and **test-only** 89-candidate fixture from WS66-A, the unchanged Kotlin `WordSearchGeneratorV5` is run for **12 fixed seeds in each of eight scenarios (96 attempts)**:

| Chapter | 12×12 Relaxed | 15×15 Classic | 20×20 Expert |
|---|---|---|---|
| Flower Borders, 30 proposed words | Trial | Trial | Trial |
| Our Solar System, 33 proposed words | **Not trialed: only 9 Familiar; requires at least 12** | Trial | Trial |
| Quiet Clearings, 26 proposed words | Trial | Trial | Trial |

The first three successful seeds provide a simple baseline three-page run. A **test-only three-page seed selector** examines chronological combinations from all successfully generated boards and chooses a set that minimizes the total word overlap between adjacent pages, breaking ties in favor of more distinct answers across the three. This does **not** change the production generator, create new saved IDs or activate a scheduler in Android.

For each scenario the Kotlin test records **every failed seed**, all successful board SHA-256s, exact word lists, and the three candidate seed choices, if feasible. It validates their deterministic regeneration, full answer claim and completion through `WordSearchRules`, and frozen-board binary encode/decode. The separate Python `ws66b_variety_metrics.py` recomputes all aggregate and adjacent-overlap metrics from the **actual Kotlin output** and rejects fabricated or inconsistent numbers.

## Critical mathematical limitation

For any two pages using `T` words drawn from `E` eligible words, the minimum possible number of repeated words is **max(0, 2T − E)**. For example, the 26-word Quiet Clearings pool at Expert 24-word pages necessarily shares **at least 22 answers** between ANY pair of consecutive pages, even with perfect scheduling (more if mechanical restrictions shrink the effective pool). A near-total repeat there is a *content-pool shortage*, not necessarily a bad random seed or defective generator. This is exactly why the 10,000-word initiative matters.

The report also logs selected word reuse across Garden, Woodland and Space, rather than treating all book vocabulary as independent.

## Evidence and caveats

The GitHub Action archives:
- All **96 per-seed success/failure rows**, with hashed board content and exact answer lists.
- Per-scenario metrics, including eligible pool, lower-bound repeat floor, baseline vs selected 3-page adjacent repeats, and distinct answer coverage.
- The selected 3-page test-only recipes, a machine-checked JSON summary, concise human-readable table, checksums, and JUnit XML.
- Historical **WS66-A source-provenance guard** and **unchanged historic V1/V2/V3 golden puzzle checks**.

This is *mechanical and content-variety research* on unapproved candidate words. It does not measure actual screen readability, family-friendliness, chapter artwork, in-game reward systems, touch interaction, commercial vocabulary permissions, or genuine device bookmarks.

**No new editor/owner/public approval, no change to the 787 internal-QA English/Fall word baseline, no activation of any of the hypothetical 200 additional spellings, and no app/source, active pack, board ID, frozen 63+18 research pages or saved-game mutation.**

## Product-development next step

Use the real measured overlap and failures to prioritize larger usable pools, particularly **Quiet Clearings Expert** (24 targets from just 26 proposals) and **Solar System Relaxed** (only nine Familiar in this experimental subset). Once we have credible three-page pools, prepare a debug-only visual in-app QA preview using brand-new immutable page IDs and compatible saved data; do not promote scratch study seeds to existing published/frozen IDs.
