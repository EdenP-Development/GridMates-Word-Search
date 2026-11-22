# Current product roadmap — November 18, 2026

**Latest WS62 Nearby transport milestone — November 22:** [WS62 exact-source single-emulator live TCP QA](devlog/2026-11-22_WS62_LIVE_NEARBY_TCP_MODES_QA.md) **PASSED**, [Android/core #45068863442](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45068863442) and [art/release #45068863479](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45068863479) both SUCCESS at executed SHA `b7fc886bb82833d133e8fe26b8d63033478e05b8`. Three actual API35 Android TCP host/guest integration tests passed Co-op (join/claim/chat/rejoin), Shared Board Race (first claimant/duplicate rejection) and Speed Duel (both Ready/GO and private found ledgers). **Important limit:** two independent runtime objects in ONE emulator/app process, NOT two separate phones, automatic NSD/mDNS discovery or real router Wi-Fi; [physical two-phone acceptance checklist](WS62_TWO_PHONE_NEARBY_ACCEPTANCE_2026-11-22.md) is the next functional gate. Test-only change, no new app version; last independently hashed WS60 signed v108 APK remains owner candidate. All original 63 + 18 frozen QA boards/saves/art preserved, 122 Woodland candidate words unapproved, public publishing/TalkBack/owner device acceptance open.


**Latest WS61 signed upgrade acceptance — November 22:** [Exact v107→v108 Android emulator evidence, raw saved-file hashes and 2/2 native store tests](devlog/2026-11-22_WS61_SIGNED_V107_V108_INSTALL_OVER_QA.md). At code `684d331387f6f5c10e84918086426ad7aeaa70dc`, [Android/core #45067303054](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45067303054) and [art/release #45067303073](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45067303073) both **SUCCESS**. The actual signed old QA v107 runtime created legacy and new Woodland bookmarks, partial words/hints/time plus Solo/feedback settings; `adb install -r` to signed code108 preserved **all six private save/pref files exactly**, then the new runtime re-decoded all state. This is verified *automated emulator* compatibility and is NOT an owner-device upgrade or launch approval. **WS60 v108** remains the latest independently hashed player-facing QA APK; WS61 modifies instrumentation/CI only, no artwork, 81 QA page hashes/IDs/saves, engine or release content. Next owner real phone UX/save acceptance, two physical phones Nearby, 122 candidate Woodland word editorial approval, store/monetization.


**Live November 22 WS60 verified QA update:** [WS60 exact source, signed v108 APK verification and remaining gates](devlog/2026-11-22_WS60_VERIFIED_ANDROID_QA.md). New readable portrait 150–200% book page shows short Page number / single-line progress while full word/puzzle names remain available to Android accessibility services. At `0a546c2f99d0115eb4f9d30c00ef86598fe3b135` both [Android/core #45065450259](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45065450259) and [independent art/release #45065450280](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45065450280) **SUCCESS**; real Android UI emulator 4/4 tests at 200% portrait and 150% Activity landscape. Verified QA v108 `com.playereden.gridmates.wordsearch.qa` APK SHA `6a7d59e6fed8bd2524abd216027b3caf7445aa8afa79c7fa8f2f3600b65a3b3b`, same APK v2 QA signer as verified v107, [artifact #14771725725](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45065450259/artifacts/14771725725). This is the new **source-built/independently hashed QA phone-test candidate**, replacing historical v104/v107 "latest" banners; **NOT owner phone install confirmed, not published or merged**. Original **63+18 immutable QA pages** and all saves unchanged; 122 Woodland editorial candidates unapproved, two-phone Nearby, physical font/Touch/ TalkBack and final release readiness remain open. Next owner phone QA on v108 without uninstall; then real two-phone LAN and release editorial/publisher checks.


**Live development checkpoint — November 22 (supersedes historical WS42 baseline below):** [WS57 six-chapter playable Woodland QA preview](WOODLAND_PLAYABLE_PREVIEW_WS57.md) v107 added a fourth debug-only Library book with **18 new QA pages** to the existing **63** (81 total), preserving old save bytes and frozen board hashes. [WS58](devlog/2026-11-22_WS58_REAL_WOODLAND_UI_GESTURE_QA.md) verified normal-size 12×12 and 20×20 native input. [WS59](devlog/2026-11-22_WS59C_LANDSCAPE_ACTIVITY_ORIENTATION.md) then verified **four actual emulator UI tests** at 200% font portrait and 150% native landscape; exact code `89ffdc328a2219785ec2b7ec8083226ad22e7965`, [Android/core success #45060619654](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45060619654) and [independent art/release success #45060619645](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45060619645). **No player-facing build/runtime change after WS57; last hashed signed owner QA APK is WS57 v107.** All changes remain stacked DRAFT, not merged into main/Work. **Primary next:** phone-first readability/UX and real two-phone Nearby acceptance; 122 Woodland editorial words still unapproved, TalkBack and release work remain open. This live checkpoint takes precedence over the v104 snapshot preserved below for historical context.


This is the working priority list. Historical design documents and issue
checkboxes supply context. A newer tested implementation or owner decision
supersedes an older "next task" paragraph. See the
[GitHub recovery audit](audits/2026-11-17_WS40_GITHUB_PLAN_RECOVERY.md) for the
source crosswalk and the [development journal](DEVELOPMENT_JOURNAL.md) for receipts.

## Current playable baseline

**v104 / WS42 QA**, tested source `f2304c5d81a57cc1a29197277702d92645460c96` on the feature branch in [draft PR #153](https://github.com/PlayerEden/GridMates-Word-Search/pull/153).
[Build and Android receipt](devlog/2026-11-18_WS42_HANDSET_VERIFICATION.md) · [signed APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44252800362/artifacts/14404433051).

- Mixed Quick Play offers Small 12×12 / Medium 15×15 recommended / Large 20×20 and same-size Play Another. It uses 670 distinct QA answers over four groups.
- Three QA books now have 21 pages each/63 total; original nine page IDs/recipes stay stable. This remains a QA catalog requiring editorial/production promotion.
- Schema 6 stores exact V5 boards plus idempotent book-to-profile rewards. Full backups, interrupted restore recovery and v102 install-over passed actual Android checks.
- Legacy Solo/Custom 12/15/20/25/30, Daily, original portraits/fonts and Nearby remain. Custom/Nearby use their legacy themed content; V5 is active in Quick Play/books.
- v104 adds pinned Home controls, a chapter picker, better first book hints and shorter large-board help. The v103 upgrade preserved the exact 20×20 board. Native Garden chapter and hint checks, all 31 Android tests and independent build and release isolation passed. All 228 art assets match v103. Owner handset and two-phone acceptance are open. v103 is the verified fallback.

## What still applies

| Priority | Work | Current state and completion target |
| --- | --- | --- |
| Next | **Large curated word library** | The initial 670 QA words are playable. [WS43](https://github.com/PlayerEden/GridMates-Word-Search/pull/154) prepared 150 Fall words for editorial QA, including 117 new canonical IDs. Continue with small reviewed batches toward **10,000 distinct approved words across 30-plus topics**. |
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

Plan page recipes for the first Fall volume using the versioned [150-word Fall authoring pack](https://github.com/PlayerEden/GridMates-Word-Search/pull/154). Check 12×12, 15×15 and 20×20 placement, repeated stems, difficulty and target coverage before integrating a playable QA volume and matched artwork.

Before using new packs in Custom or Nearby, distinguish play mode from pack identity in portable saves and results and version LAN content compatibility. Preserve existing boards and support the current LAN behavior during that migration. Owner v104 review and two-phone acceptance remain parallel work. Production content, artwork, app bundle and store promotion remain open.
