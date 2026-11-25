# November 24, 2026 — First Word Search beta candidate and save-import rehearsal

> **Status:** Signed Beta 1 and Android 35 save-import rehearsal passed. Handset review is open.

## Starting checkpoint

The owner asked to resume the first signed Word Search Android beta after the previous chat reached its limit. GitHub held [draft #199](https://github.com/EdenP-Development/GridMates-Word-Search/pull/199) at `3072c7a6392558192b43aa7c50e50ea07fc6af27`, based on Woodland QA v120. The dedicated beta key passed [Actions run 45496175839, attempt 2](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45496175839). [Prior entry](2026-11-24_EDENP_WORD_SEARCH_BETA_SIGNING_VERIFIED.md)

## Candidate

- The release configuration builds version `0.9.75-beta.1` (Android version code `121`) under `com.playereden.gridmates.wordsearch.beta`. Its signer is the dedicated itch.io beta certificate pinned in the Actions workflow.
- The restore preview identifies which parts of a QA backup carry over. The beta imports the profile, earned rewards, Solo and Daily progress, Race and Duel records, and board font. QA book pages use a separate catalog and stay available in QA. The recovery journal records the confirmed scope before applying any changes.
- The Android rehearsal uses the archived, hash-checked QA v120 APK and the signed release candidate side by side. Settings exports a test save from QA, previews it in beta, cancels once, restores it and checks the complete portable profile after restarting. Checks include the equipped font and the three QA journal checksums.

## Build and follow-through

- [First build run 45534794555](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45534794555) at `d3bdee142be2d79f0be3fc7f3d9ae6c17b73be9d` stopped at SDK setup because `sdkmanager` was outside PATH. The workflow now finds it under the runner's Android SDK.
- [Run 45534953388](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45534953388) at `53f676f243b915f5c8008be93344027f20e0f4fd` compiled the APK, verified its package and dedicated certificate, and excluded 239 QA assets. [Signing and emulator evidence](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45534953388/artifacts/14987465112) records APK SHA-256 `1d6c58d94dbf323b7de2317cfa4f4e526cfbfc4e96120c0a8e1560a132ef11a8`. The exact installed v120 passed the save fixture test. Android's first-use full-screen prompt blocked opening the Home menu. [Android QA](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45534959161) and [release isolation](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45534959134) passed at this source.
- [Run 45536006825](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45536006825) at `67fee12f699c8150ca73a49bcce58b85606414a6` signed the candidate and reached v120's Settings save action. The test waited for a confirmation below the visible part of Settings. [Evidence](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45536006825/artifacts/14986448890) records APK SHA-256 `b98261865bfc594b9658541190073a5b82a6e98a9c05ee9db0079ae68d1dc03a`. The rehearsal now verifies the exported file and its checksum. The beta's confirmation appears next to its backup buttons.

- [Run 45537205869](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45537205869) at `612fb4affaed7bc57946c4bb24f935cbbc79eb7c` signed the APK, exported and verified a save through v120 Settings, and displayed the beta import preview. Keeping the current progress returned to an empty beta profile. The restore dialog exposed its confirmation as a text child, so the test now uses the visible action label after the dialog opens. [Evidence](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45537205869/artifacts/14987582930) records APK SHA-256 `a2f50dd1fc2ae1a023b7dda4b273747bf88b9ecfcc26b66c291dbf5ac930b423`. [Android QA](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45537213091) and [release isolation](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45537213010) passed.

### Verified Beta 1

[Run 45538637429](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45538637429) passed at `06f03af17835192bd189936508608a50d4bfb92c`. [Android QA](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45538645122) and [release isolation](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45538645068) also passed at that source.

| Check | Verified value |
| --- | --- |
| Android identity | `com.playereden.gridmates.wordsearch.beta` |
| Version | `0.9.75-beta.1` (code `121`) |
| Dedicated certificate SHA-256 | `c29c0c593ac99ae81b5a5aaf5fca37bbed3bbd419e8f08287296de6681babba3` |
| APK size and SHA-256 | 9,571,166 bytes · `b43fabba58bbefe0ab042bf6bf8b708dde874c2b5cdc70d4ea96d002ca6cfdc3` |
| Release isolation | Non-debuggable APK. QA-only art and book preview assets and classes were excluded. |
| Android 35 | Settings exported from the archived v120 APK. Beta showed the import scope, accepted cancel, and restored the full portable profile and board font. The result matched after a process restart. All three v120 journal checksums still matched. |
| Android backup regressions | Seven tests passed, including recovery-journal replay and preservation of book saves. |

[Signed APK](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45538637429/artifacts/14988966094) · [Android rehearsal](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45538637429) · [Verification files](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45538637429/artifacts/14988223062)

Signed QA v120 is the fallback. Its APK SHA-256 is `0650e7fc6fb6198e22b20b8f6360c7379fe668a263b4eebb9d522f787b286a09`. [QA v120 ZIP](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45491802328/artifacts/14967073059)

Continue from draft #199. The next distribution check is handset acceptance of the signed Beta 1 APK. Public catalog approval and itch.io publication are open. Space's album continues from [gameplay draft #198](https://github.com/EdenP-Development/GridMates-Word-Search/pull/198).
