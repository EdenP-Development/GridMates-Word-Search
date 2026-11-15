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
