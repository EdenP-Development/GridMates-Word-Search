# Current product roadmap — November 17, 2026

This is the working priority list. Historical design documents and issue
checkboxes supply context. A newer tested implementation or owner decision
supersedes an older "next task" paragraph. See the
[GitHub recovery audit](audits/2026-11-17_WS40_GITHUB_PLAN_RECOVERY.md) for the
source crosswalk and the [development journal](DEVELOPMENT_JOURNAL.md) for receipts.

## Current playable baseline

**v103 / WS41 QA**, tested source `01eb02e4e5460feaab131236e4da7b9d8eb5c2e1`,
[draft PR #152](https://github.com/PlayerEden/GridMates-Word-Search/pull/152).
Latest cumulative source is on its feature branch, not `main`.
[Build/native/Android receipt](devlog/2026-11-17_WS41_MIXED_PLAY_AND_BOOK_CONTENT.md) · [signed APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44236302499/artifacts/14397980688).

- Mixed Quick Play offers Small 12×12 / Medium 15×15 recommended / Large 20×20 and same-size Play Another. It uses 670 distinct QA answers over four groups.
- Three QA books now have 21 pages each/63 total; original nine page IDs/recipes stay stable. This remains a QA catalog requiring editorial/production promotion.
- Schema 6 stores exact V5 boards plus idempotent book-to-profile rewards. Full backups, interrupted restore recovery and v102 install-over passed actual Android checks.
- Legacy Solo/Custom 12/15/20/25/30, Daily, original portraits/fonts and Nearby remain. Custom/Nearby use their legacy themed content; V5 is active in Quick Play/books.
- Both builds, 24 actual Android tests and native finger playthrough passed. All 228 art assets match v102. Owner phone/two-phone acceptance is still open; v102 is the prior tested fallback.

## What still applies

| Priority | Work | Current state and completion target |
| --- | --- | --- |
| Next | **Large curated word library** | WS40 tooling recovery and the first 670-word assistant-reviewed QA batch are complete. Continue 100–250-word batches toward **10,000 distinct approved words / 30-plus topics**, with human/editorial release review. Candidate/source counts do not count as shipped words. |
| QA implemented | **Immutable packs and new content engine** | V5 strict pack loader, SHA-pinned exports, literal fixtures and portable frozen boards work for Quick Play/books. V1/V2/V3/Daily stay frozen. Add explicit mode/pack identity before activating new themed Custom or content-compatible LAN; Nearby currently retains its existing protocol/content. |
| QA measured | **Consecutive-play quality benchmarks** | 100 Medium boards used 608 unique words; adjacent overlap 0.364; 100/100 covered all quadrants; word-length/subtopic/direction/crossing metrics recorded in WS41. Continue outlier and full-book editorial review as packs grow. |
| QA complete | **Complete mixed Quick Play** | Small 12×12 / Medium 15×15 recommended / Large 20×20 selector, four-group vocabulary, same-size Play Another and optional Change Size work through actual Android input and restart. Owner review pending. |
| QA expanded; release work remains | **Publishable book catalog** | Three 21-page QA volumes now preserve the original nine pages and append 54. Complete editorial review/fixed release manifests and matched art promotion before publishing; never promote reserved QA IDs. Regular books remain 12/15/20. |
| QA implemented | **Book rewards/profile policy** | Idempotent book-page ledger grants general profile/portrait/font/count-achievement progress once and travels in full backups. No invented historical hints/time, Solo-only achievements or production approvals. |
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

Curate the next100–250-word Fall batch in the versioned authoring catalog, preserving canonical IDs and many-to-many assignments. Verify spelling/display/theme fit and export under a new immutable identity before activating any future book. The10k goal remains ongoing;670 QA words are the first edition, not its completion.

Before using new packs in Custom or Nearby, distinguish play mode from pack identity in portable saves/results and version the LAN content contract. Keep old boards and protocol behavior. Owner physical v103 review and the two-phone acceptance gate remain parallel work. Production content/art/AAB/store promotion remains separate from a successful QA build.
