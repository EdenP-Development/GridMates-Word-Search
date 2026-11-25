# November 24, 2026 — First Word Search beta candidate and save-import rehearsal

> **Status:** Build and Android rehearsal pending.

## Starting checkpoint

The owner asked to resume the first signed Word Search Android beta after the previous chat reached its limit. GitHub held [draft #199](https://github.com/EdenP-Development/GridMates-Word-Search/pull/199) at `3072c7a6392558192b43aa7c50e50ea07fc6af27`, based on Woodland QA v120. The dedicated beta key passed [Actions run 45496175839, attempt 2](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45496175839). [Prior entry](2026-11-24_EDENP_WORD_SEARCH_BETA_SIGNING_VERIFIED.md)

## Candidate

- The release configuration builds version `0.9.75-beta.1` (Android version code `121`) under `com.playereden.gridmates.wordsearch.beta`. Its signer is the dedicated itch.io beta certificate pinned in the Actions workflow.
- The restore preview identifies which parts of a QA backup carry over. The beta imports the profile, earned rewards, Solo and Daily progress, Race and Duel records, and board font. QA book pages use a separate catalog and stay available in QA. The recovery journal records the confirmed scope before applying any changes.
- The Android rehearsal uses the published, hash-checked QA v120 APK and the signed release candidate side by side. Settings exports a test save from QA, previews it in beta, cancels once, restores it and checks the complete portable profile after restarting. Checks include the equipped font and the three QA journal checksums.

## Build and follow-through

The beta APK and emulator result are pending. Signed QA v120 is the fallback. Its APK SHA-256 is `0650e7fc6fb6198e22b20b8f6360c7379fe668a263b4eebb9d522f787b286a09`. [QA v120 ZIP](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45491802328/artifacts/14967073059)

Continue from draft #199. Verify the beta package, signer and Android rehearsal. Owner handset review and public catalog approval remain open. Space's album continues from [gameplay draft #198](https://github.com/EdenP-Development/GridMates-Word-Search/pull/198).
