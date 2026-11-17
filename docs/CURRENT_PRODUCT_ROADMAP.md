# Current product roadmap — November 17, 2026

This is the working priority list. Historical design documents and issue
checkboxes supply context. A newer tested implementation or owner decision
supersedes an older "next task" paragraph. See the
[GitHub recovery audit](audits/2026-11-17_WS40_GITHUB_PLAN_RECOVERY.md) for the
source crosswalk and the [development journal](DEVELOPMENT_JOURNAL.md) for receipts.

## Current playable baseline

**v102 / WS39**, tested source `8ce68b0d0564ce11c30c0b18dcb208d7b9a1b6f4`,
[draft PR #150](https://github.com/PlayerEden/GridMates-Word-Search/pull/150).
The latest cumulative source is on its development branch, not `main`.
[v102 receipt](devlog/2026-11-17_WS39_REPLAY_AND_BOOK_BACKUPS.md).

- Solo/Custom supports 12/15/20/25/30 boards with independent challenge levels,
  24 paused games, hints, records and progress protection.
- Daily, original Woodland portraits, free font unlocks, profile and results work
  in the QA app. Nearby Co-op, Race and Duel have implemented source and tests.
- The three illustrated QA books have nine total test pages. This is not yet a
  full release catalog. Public builds still isolate QA book/art content.
- Quick Play starts a fresh **single-theme 12×12** Classic Solo board.
- Full backup/restore includes book progress and unfinished pages. Its restore
  recovery and signed install-over were tested in WS39.

## What still applies

| Priority | Work | Current state and completion target |
| --- | --- | --- |
| Next | **Large curated word library** | Recover WS10's separate authoring branch, then review 100–250-word batches. Long-term target remains **10,000 distinct approved playable words over 30-plus topics**. Current APK V2 pools have 36 words/theme and V3 has 81/81/79. Candidate/source counts do not count as shipped content. |
| Next | **Immutable packs and new content engine** | WS40 adds an approved-only snapshot exporter. Next needs actual reviewed content plus an offline loader/versioned generator, stable pack IDs and hashes, frozen fixtures, portable saves and content-compatible LAN messages. Preserve V1/V2/V3 and old Daily boards. |
| With new packs | **Consecutive-play quality benchmarks** | Recover the genre research's word-set overlap, subtopic/length mix, direction balance, crossings and target-region coverage checks across many consecutive seeds. The v102 solvability audit does not by itself prove long-term vocabulary freshness. Measure distributions and poor outliers before tuning quotas. |
| Then | **Complete mixed Quick Play** | Recover WS16B/WS17's intended Small 12×12 / Medium 15×15 / Large 20×20 selector, mixed-theme plus general vocabulary, and same-size Play Another. Medium is recommended. v102's usable short themed mode is a partial implementation of that broader design. |
| Then | **Publishable book catalog** | Replace the tiny test-only catalog with reviewed fixed pages, chapters and meaningful volume length. Add books from data and matched art, never fake playable future volumes. Maintain regular book sizes at 12/15/20. |
| Before public book release | **Book rewards/profile policy** | Current book completion writes its separate journal. Portrait unlocks count Solo + Daily. Define and test an idempotent book-to-profile reward path before promising that book pages earn those cosmetics. Do not silently alter old unlock IDs or grant QA-test rewards. |
| Parallel, beta gate | **Two physical phones** | Open [issue #53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53) records an old real-device marking failure. Later input/UI fixes and automated tests exist. Current host/guest drag, endpoint selection, claims, reconnect, Race and Duel still need physical acceptance. Do not infer either a current failure or a pass from the old issue alone. |
| Ongoing | **Cohesive book families and menus** | Keep the accepted parchment/green/gold look and matched cover/spine material. Use chapter/bookmark/seal motifs where useful. Fix concrete readability/input defects; optional decoration is secondary to playable content. |
| Before beta/release | **Regression and publishing** | Device/font/orientation/TalkBack/performance checks, complete progress migration, app/art/content promotion, production catalog/signing/AAB and store preparation remain. Keep release credentials and QA/prototype isolation intact. |

## Future themes and deliberately deferred ideas

| Idea | Disposition |
| --- | --- |
| **Fall, Jungle, Winter, Summer, Castle** | Current owner direction. Build coherent reviewed vocabulary first, then matched cover/spine/page families. Seasonal books can use existing season subtopics; Castle can relate to architecture/history while retaining its own book identity. New topic IDs may be added deliberately when needed. No new books are claimed shipped. |
| 33 existing proposed topic families | Keep as authoring metadata, not 33 fake selectable books. Their scope already covers ocean, animals, food, travel, music, history, games, seasons and more. |
| 40×40/50×50, later 75/100 Marathon | Preserve the isolated placement/quality lab. Optional future R&D after content supply, readable navigation, performance and save safety. Do not add giant sizes to regular books or enable them from an old "next" note. |
| Spanish / more languages | Keep the existing language plan. Locale-specific normalization, alphabets, display, saves and LAN need an explicit version. English A–Z data is not a Spanish implementation. |
| Ocean/Bugs portraits | Coordinated art ideas still make sense, but the parity-first plan deferred them behind core content and multiplayer acceptance. Keep the original twelve Woodland portraits. |
| More board fonts | Keep the current bundled, achievement-earned fonts. The recovered font research is a future intake shortlist, not approval to import or sell those fonts. Paid board-font unlocks are not the current design. |
| Co-op split word lists | Future optional mode after ordinary Co-op is accepted. Needs hidden-list and hint/accessibility privacy rules. |
| Stamps, weekly/daily gifts, rotating store, shared wallet | Open design choices, not a committed economy. Existing fixed achievements/fonts remain the useful baseline. No grind or attendance requirement is implied. |
| Ads / one-time Remove Ads | Later launch/business work after a clean beta. No implementation or store-policy readiness is implied by current QA. |
| Online accounts, internet multiplayer, persistent friends, cross-game inventory | Outside the current offline/LAN baseline. Do not accidentally expand scope from older speculative notes. |
| Full painted GridMates/Word Search wordmarks | The old WS16AE brief requested complete title artwork. Current code uses illustrated emblems plus accessible native lettering. Treat full wordmarks as an optional branding decision, not as already delivered by the emblems. |
| User-entered custom word lists | Current Custom chooses built-in options. A true word-list editor needs its own portable pack definition and generator/save validation. Revisit after the pack loader. |

## Superseded directions

- The old WS01 "NEXT" milestone table and v47/v56/v76 "latest APK" banners are
  historical. Current source and fallback come from the active handoff.
- Cover-first two-books-per-shelf prototypes and the proposed open shelf do not
  replace the later packed cabinet direction. Decorative blank spines remain
  noninteractive and never create book/save IDs.
- Legacy forced 145%/185% zoom and separate Find/Move controls were replaced by
  fit-first selection, pinch/two-finger navigation and precise selection tools.
- Old art placeholders and branch-specific SVG studies are references, not a
  reason to roll back the current matched bindings or regenerate approved art.
- An open historical issue or unmerged draft is not proof that its feature is
  absent from the cumulative QA app. Check current code and linked receipts.
- Older word-library documents sometimes express per-puzzle conflicts as global
  exclusions or propose a single theme per word. WS10's later model separates
  global words from many-to-many assignments. Keep substring/palindrome handling
  specific to the new generator's physical-path rules.

## Next exact implementation

Create a versioned authoring directory from the recovered schemas, keeping the
exact recovery fixture intact. Complete the first reviewed content batch from
the Woodland/Space/Garden sheets and a general vocabulary source, export packs, and
implement the new pack-aware mixed generator/save path. Then add the compact
three-size Quick Play chooser and Play Another loop. Run saved-board goldens,
novelty/solvability/length-tier tests, backup/upgrade tests and actual Android
play before calling that full WS17 milestone done.

WS40 itself recovers tooling and plans, adds snapshot safety, and reproduces
the editorial queues. It does not add approved words to the installed game or
create another APK. v102 remains the playable baseline during this pass.
