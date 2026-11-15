# WS16CC — Large-type Nearby and Daily controls (v85 candidate)

**Date:** November 15, 2026. **Owner direction:** Continue improving GridMates: Word Search before requesting another APK download or physical test. **Repository:** `PlayerEden/GridMates-Word-Search` on draft [PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), branch `feature/ws14b-style-anchor-and-art-direction-2026-11-08`.

## Starting checkpoint and why

[v84 WS16CA/CB](2026-11-15_WS16CA_WS16CB_NEARBY_DAILY_ROUNDTRIP_V84.md) is the verified same-signed APK fallback: exact code `0aa6a2c71f0a75c586e3587c792f7fa3e4d017da`, [Android QA #43700246715](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43700246715) SUCCESS 81/81, [independent art QA #43700246722](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43700246722) SUCCESS 82/82. v84 introduced honest Wi-Fi-based hosting and shared parchment/gold Nearby and Daily panels; its real Android AtomicFile save/reopen test only compiled and has not executed. It would be premature to ask for another owner install without addressing likely 200%-font UI compression.

The new large-type risk is concrete in the source: Nearby used a single fixed row for all three mode chips; Daily placed today's puzzle text beside its Play/Continue/Review action, used a single row for each journal entry's Resume/Review, and placed three paging controls in one row. At 150–200% font scale on 320–360dp phones, these arrangements can clip or squeeze actions. This pass addresses layout without changing the content or data.

## Changes actually committed

- `NearbyCoopScreen.kt`: at Android fontScale >=1.5, Co-op/Race/Duel are individually tappable full-width rows, tagged `ws16cc-nearby-modes-stacked`; at ordinary text scale, the existing compact row remains, with horizontal scrolling as a safeguard. The shared `NearbyMatchChip` maintains identical selected state and labels in both layouts. Host safety gate, network runtime, Join, manual address entry, room options, modes and protocols stay intact.
- `DailyLandingScreen.kt`: at >=1.5 scale the date's theme/status and primary action stack, with a full-width Play today/Continue/Review button (`ws16cc-daily-today-stacked`). Journal heading, historical action rows and Previous/Next pagination also reflow vertically. At regular scale, original dense layouts remain; Daily puzzles, progress and dates are unchanged.
- `GridMatesInfoPanel.kt`: shared pure `shouldStackLobbyActions(fontScale)` selects the same breakpoint for Nearby and Daily.
- New executed-target unit source `LobbyLargeTextLayoutTest.kt` checks 1.0, 1.49, 1.5, 1.8 and 2.0 scales. New instrumentation source `NearbyDailyLargeTextAndroidTest.kt` renders both screens in 320×640dp with simulated 200% type, navigating to all three Nearby modes, Join and Daily primary action. Normal workflows **compile but do not execute** this instrumentation test; no real screenshot or gesture acceptance is implied.
- `app/build.gradle.kts`: QA `versionCode=85`, `versionName=0.9.41-large-text-lobbies-qa` (debug suffix), retaining the same application ID and QA signing configuration. There are **no artwork, media/font, JSON save schema, generator, reward, board gesture, network protocol or package identity changes**.

**Exact candidate code/test SHA:** [`f118ca76c9dd05d02a635af24bcc81109b9dd210`](https://github.com/PlayerEden/GridMates-Word-Search/commit/f118ca76c9dd05d02a635af24bcc81109b9dd210).

## Build and acceptance gates

- [Android QA #43753383159](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43753383159): **in progress at initial record**, no green claim.
- [Independent art/release #43753383219](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43753383219): **in progress at initial record**, no green claim.
- Do not hand over or ask the owner to install v85 until both exact-source runs succeed and real APK version, package, signer, asset contents and SHA-256 are checked. A workflow success does not establish physical 200% readability.
- **Signed v84 fallback:** [artifact #14156326281](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43700246715/artifacts/14156326281), APK SHA256 `cdd2070ead1ad494fbb87cbb89226c4e3c2c3b66f80e78239988646eda45b772`. The owner has not confirmed v84 installation; do not uninstall or clear progress. PR #131 stays draft, main untouched.
- Still OPEN: run actual Android instrumentation for AtomicFile Solo save→resume→finish→First Page/Rabbit→Records, physical 200% font screens, disconnected/reconnected Wi-Fi and actual two-phone LAN, v83 long word-list/board gesture interaction and all owner visual acceptance.

**Next exact action:** read both v85 CI conclusions; if red inspect real compiler/test logs and correct only the failure. If green, inspect the matched signed artifacts before marking v85 build verified. Continue batching meaningful progress until the owner asks for the combined APK/phone test.

## FINAL: v85 verified source-matched Android / independent artwork QA

**Exact compiled source/test commit:** `f118ca76c9dd05d02a635af24bcc81109b9dd210`. Documentation-only `[skip ci]` commits after this do not change the APK.

- [Android workflow #43753383159](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43753383159): **SUCCESS, 81/81 steps**, including the new executable JVM breakpoint test, Android instrumentation **compilation**, lint, APK, fonts/art and signing guards. [Standard signed QA APK ZIP artifact #14176978654](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43753383159/artifacts/14176978654), ZIP contains `app-debug.apk`.
- [Independent artwork and release-isolation #43753383219](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43753383219): **SUCCESS, 82/82 steps**. [Independent debug APK ZIP #14177302459](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43753383219/artifacts/14177302459).
- Both GitHub-hosted ZIP artifacts were downloaded independently, CRC-tested and inspected. Each APK is **30,507,416 bytes**; signed standard Android APK SHA-256 `c7cc8c03ce24f2a61c68901992654db65cd7a568471048843f95189ba82a1689`; independent artwork APK SHA-256 `cc4ddd5e103d3427fd6f11cabe263f692def633073e80b663172efb947ad05ac`. Whole APK envelope hashes differ, but **all 360/360 extracted ZIP entries are byte-identical**. Both ZIP/APK archives passed CRC/integrity.
- Compared exact standard v85 against v84 standard (the prior verified APK hash `cdd2070ead1ad494fbb87cbb89226c4e3c2c3b66f80e78239988646eda45b772`): the same **360 entry names**, the same **219/219 asset bytes**, zero additions or removals; only `classes4.dex` and `AndroidManifest.xml` differ as expected for the UI/version update.
- GitHub Actions' **actual `apksigner` output** confirms v85 QA certificate DER SHA-256 `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419`, identical to v84. The **actual `aapt dump badging`** reports package `com.playereden.gridmates.wordsearch.qa`, `versionCode=85`, `versionName=0.9.41-large-text-lobbies-qa`. Version and signer therefore support install-over from v84 without clearing the user's local progress.
- This verifies **build integrity, unit tests, instrumented source compilation, independent artwork isolation and package continuity**. It does **not** verify the actual on-device appearance of 200%-scaled controls, executed Android AtomicFile saved-game/Records tests, Wi-Fi connectivity, physical finger tracing, or two-phone play.

**Outcome:** v85 is a **dual-green signed QA APK** and the latest *automated* fallback, not an owner-installed or release-approved game. The user explicitly requested to continue before another APK download and test; none requested yet. PR #131 remains **DRAFT**, no merge. Next meaningful work: create/run a **targeted real-emulator instrumentation gate** for the new high-font accessibility and the existing v84 interrupted Solo→reopen→reward→Records flow, then combine with the eventual owner phone test. Do not repeatedly rebuild for docs-only changes.
