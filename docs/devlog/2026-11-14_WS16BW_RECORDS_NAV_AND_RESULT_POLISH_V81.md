# WS16BW — Records & Statistics: navigation, accessibility, result density

**Date:** November 14, 2026. **Status at source commit:** v81 source and tests committed, standard Android and independent artwork/release CI running. Owner real-device screenshots/interaction acceptance **OPEN**. [Draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131) not merged.

## Context / audited source

The v80 Font and Portrait visual work passed code QA, with screenshots looking much better. The next unreviewed section is **Records**, accessible from Profile → Records & statistics. This was a source-backed UX audit rather than claiming the owner had supplied Records screenshots.

Inspected `app/src/main/java/com/playereden/gridmates/wordsearch/SoloRecordsScreen.kt`, its Android UI navigation test, older WS07F acceptance/92-test emulator evidence, and core Race/Duel journal rules. Found two concrete UI risks:

1. The shell already prints `Records`, but the screen added a decorative `Your puzzle journal` heading and a `Browse your results` section heading above the Solo/Race/Duel tabs. This delays navigation on phones and repeats hierarchy.
2. Race/Duel winner and partner score, and Solo theme/difficulty and hint classification, sat side-by-side in a `Row(Arrangement.SpaceBetween)`; long numbers, font scaling and translations can crowd/clash. Race/Duel metrics were always two columns, even at enlarged system text, unlike the adaptive Solo metric cards.

**Important existing design confirmed:** Only the active Solo/Race/Duel journal is rendered; Solo/Daily summary is only on Solo; `rememberSaveable` keeps independent tab/page positions; results are paged four at a time with no UI truncation, and the local Race/Duel journal rules deduplicate validated host results. No changes necessary to underlying privacy/data semantics or journal calculations.

## Implementation, code 81

- [UI change `21f186d3bb4b2b268322b096e7cc2c064aa2abee`](https://github.com/PlayerEden/GridMates-Word-Search/commit/21f186d3bb4b2b268322b096e7cc2c064aa2abee): remove redundant double heading inside Records; retain shell's actual **Records** title and a concise local-data introduction. Add stable Solo/Race/Duel tab tags for future instrumentation. Page tabs are earlier in scroll.
- Add shared `MatchSummaryMetrics` presentation: Race and Duel still show exact Played/Wins/Losses/Ties or Words found values, but use 2-column card rows at normal width and a 4-card vertical stack when content width <350dp or system font scale >=1.4. The previously adaptive Solo summary remains unchanged.
- For existing saved Solo, Race, Duel history cards, stack real headline and real secondary status/score onto separate lines rather than opposing aligned labels. Retain outcome, own/partner word totals, theme, board dimensions, challenge, date/time and Solo assisted/hint/elapsed values. All results still reuse the same cream/gold native PANEL frame; no new art.
- v80 → **v81** (versionName `0.9.37-records-journal-cohesion-qa`). Same QA package/test signer and saved profiles; no save schema/JSON migrations, mutating records, two-phone protocol or art/font changes.
- [Test commit `a7a0779ff527d9f7b991b8e565d98119369ff70a`](https://github.com/PlayerEden/GridMates-Word-Search/commit/a7a0779ff527d9f7b991b8e565d98119369ff70a): new `test_ws16bw_records_journal.py` checks one heading, all three tabs, independent page states, 4-per-page reachability, adaptive metrics and preserved record fields. Both CI workflows run it. `RecordsJournalNavigationAndroidTest.kt` updated to assert newer tabs/introduction and rendered Race/Duel metrics. Android instrumented test compilation alone does not demonstrate actual emulator/phone execution.
- [Test-only correction `247c173ec7ebd637d438dc81dfdc350c2351ff45`](https://github.com/PlayerEden/GridMates-Word-Search/commit/247c173ec7ebd637d438dc81dfdc350c2351ff45): the test previously asserted there were zero `SpaceBetween` layouts anywhere on the screen, incorrectly flagging the intentionally spread-out **Previous/Page/Next pager**. Corrected to assert that exactly three result-header groups stack vertically. This does not change runtime UI.
- Historical WS07F full API35 instrumentation [92/92 green](https://github.com/PlayerEden/GridMates-Word-Search/blob/main/docs/WS07F_RECORDS_BUILD_QA_2026-11-07.md) establishes past paging/navigation coverage, **not** execution of the new v81 test suite.

## Candidate CI and acceptance

- Correct exact code+test SHA: `247c173ec7ebd637d438dc81dfdc350c2351ff45`.
- [Android QA #43665962329](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43665962329) and [independent debug/release art QA #43665962331](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43665962331); conclusions **PENDING at writing**. Earlier partial-source workflows that were cancelled are not passing v81 evidence. Check SHA, final results, extracted APK, signing certificate and asset hashes before providing install-over instructions.
- Last independently verified APK was v80 [Android artifact #14139588867](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43660462285/artifacts/14139588867), package/signing unchanged. Do not uninstall/clear app data to update. Do not overwrite a newer installation with older code without backup.
- **Owner phone QA requested:** Profile → Records, Solo default and Daily count, Race and Duel empty states when no completed matches; page tabs should remain visible without an excessive header; new metric layout should be readable. If populated match history becomes available, check real long title/score and all pagers. Also verify returning to Profile, opening/closing and saved Solo puzzle/hints.
- Validate 320dp, 360dp, 200% system text, landscape/scroll reachability and TalkBack when feasible. The accepted Home/Library/Collection/Fonts designs are intentionally unchanged.

**Release status:** UI cohesion candidate only; not user approved and not production released. Keep PR #131 DRAFT and preserve existing game/rules. Next: physical screenshots of Records Solo, Race and Duel, then functional persistence/roundtrip focus instead of new decoration.
