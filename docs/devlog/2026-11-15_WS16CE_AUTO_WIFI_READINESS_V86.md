# WS16CE — Nearby automatically follows Wi-Fi availability (v86 QA)

> **Quick status (Nov 15): IMPLEMENTED, CI RUNNING.** No owner phone approval or two-phone LAN verification is implied.

## Who, what, where, why

- **Owner:** Continue GridMates: Word Search development before the next combined APK download and phone test.
- **Assistant:** Changed Nearby setup and added regression tests on [draft PR #131](https://github.com/PlayerEden/GridMates-Word-Search/pull/131), `feature/ws14b-style-anchor-and-art-direction-2026-11-08`. No changes to `main`.
- **Problem:** v84 added a correct check for local Wi-Fi/hotspot IP availability, but the screen only refreshed on entry, the Host tap, or manual "Check Wi-Fi again." If the user toggled Wi-Fi while keeping Nearby open, Host stayed visibly disabled (or enabled) until rechecked. Android connectivity event callbacks also do not guarantee that a hotspot reports as an ordinary Wi-Fi internet network or that an IPv4 address is assigned immediately on connection.

## Previous baseline

- [WS16CD v85 real Android emulator receipt](2026-11-15_WS16CD_TARGETED_ANDROID_EMULATOR_QA.md): five targeted instrumented tests **PASS**, [emulator #43754358738](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43754358738) `OK (5 tests)`.
- v85 source `f118ca76c9dd05d02a635af24bcc81109b9dd210` passed [Android QA #43753383159](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43753383159) 81/81 and [independent artwork #43753383219](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43753383219) 82/82.
- Signed v85 [artifact #14176978654](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43753383159/artifacts/14176978654), APK SHA256 `c7cc8c03ce24f2a61c68901992654db65cd7a568471048843f95189ba82a1689`, `com.playereden.gridmates.wordsearch.qa`, stable v2 QA signing certificate SHA256 `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419`. Owner physical install and full real-device acceptance still OPEN.

## Implementation

- `NearbyCoopScreen.kt`: Keep the **same exact** `runtime.hostNetworkAvailable()` (wlan/ap IPv4 interface) predicate as the authoritative `runtime.host()` worker, instead of substituting OS internet/cellular status. While the player is in the Nearby setup lobby, a cancellation-safe Compose effect refreshes readiness every **2 seconds**, running the interface enumeration on `Dispatchers.IO` to avoid blocking touch/UI. A lifecycle observer stops that check when the host activity is backgrounded; opening a hosted room also stops lobby polling. Returning/resuming automatically begins a fresh check. The old manual Check Wi-Fi again button remains available for an immediate retry, and the Host click still rechecks before starting the server. A failed interface lookup fails closed, rather than leaving Host falsely enabled.
- Warning copy says the connection is checked automatically. **Join, manual address, room discovery and retries remain independent** of the Host readiness flag; offline Solo/Daily, Wi-Fi protocol v7, data and art are untouched.
- `NearbyHostReadinessTest.kt`: executable JVM test covers false/true and probe exceptions.
- `NearbyAutoWifiRefreshAndroidTest.kt`: one Android UI instrumentation test uses an **injected deterministic readiness predicate** and an actual 360dp Nearby screen; it asserts Host disabled initially, automatically enabled after simulated Wi-Fi connection, disabled again on loss, and Join available throughout. The injection only controls the UI hint/gate; real runtime.host() continues checking the actual local interface and sockets independently. **Simulated predicate is not a real two-phone Wi-Fi connection test.**
- `app/build.gradle.kts`: `versionCode=86`, versionName `0.9.42-nearby-auto-wifi-qa`; stable QA package/signing configuration retained.

**Exact source/test candidate:** [`88ff4d75e6127904a544a82c67e2ed43077c8db3`](https://github.com/PlayerEden/GridMates-Word-Search/commit/88ff4d75e6127904a544a82c67e2ed43077c8db3). No new artwork or asset binaries.

## Current QA and handoff

- [Standard Android QA #43756099283](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43756099283) — RUNNING at initial record. Unit tests, lint, signed APK build and **compile** of UI instrumented tests are required.
- [Independent art/release QA #43756099257](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43756099257) — RUNNING at initial record. Requires separate debug/release build and art isolation.
- **Do not call v86 verified or provide an APK yet:** check both exact-SHA conclusions, actual manifest version, signer and package, source-matched APK archive contents, unchanged packaged artwork, checksum and install-over compatibility against v85.
- Dedicated actual emulator execution of **new Wi-Fi-auto-refresh instrumentation**, ideally alongside the established five v85 regression tests, is the next gate. A green ordinary QA flow only **compiles** Android UI tests.
- **Fallback:** v85 signed dual-green, whose actual on-owner-phone install is still not confirmed. Do not uninstall or clear user data. PR #131 remains DRAFT; no merge, no production art approval.

**New chat:** Read this pass, `docs/DEVELOPMENT_JOURNAL.md` and live PR #131; finish exact v86 builds, execute real emulator readiness transitions, and record results before the next combined owner phone test.

## CI correction — legacy assertion, no change to gameplay logic

First exact-source v86 QA [Android #43756099283](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43756099283) and [art/release #43756099257](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43756099257) were both **FAILED before Kotlin compile** at the same pre-existing Python source test `test_ws16ca_nearby_daily.py`. Its single assertion expected the literal old manual-only `hostNetworkReady = runtime.hostNetworkAvailable()` text. The implementation intentionally now goes through `hostProbe` and `safeHostNetworkAvailable` on a cancellable IO polling effect. The exact runtime `hostNetworkAvailable` and authoritative `host()` predicates are unchanged.

The test was updated to assert the new probe binds to the runtime, two safe direct rechecks, background/cancellation behavior and `Dispatchers.IO`/2-second refresh. This is a **source-test correction only**, not a hidden behavioral change. Corrected exact source/test [`f82406e63fbf553ec348b9f252dfb0bc22f59070`](https://github.com/PlayerEden/GridMates-Word-Search/commit/f82406e63fbf553ec348b9f252dfb0bc22f59070). Re-run workflows: [Android #43756258304](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43756258304), [independent art #43756258300](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43756258300) both **pending at log time**. Earlier failures must not be counted as build proof.

## Second build correction — lifecycle source import (Kotlin compilation)

Corrected test-source commit `f82406e` cleared the original WS16CA Python safeguard in both workflows, but the independent [art QA #43756258300](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43756258300) then **FAILED** compiling Kotlin because `androidx.lifecycle.ViewTreeLifecycleOwner` was not available on the app's current classpath. This was a genuine source compilation error, not a proven runtime Wi-Fi defect. The other job [Android #43756258304](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43756258304) was still processing when the correction was committed; it cannot validate the updated source.

Changed only the lifecycle lookup in Nearby to walk the existing Android Activity context chain (`ContextWrapper.baseContext`) and find a `LifecycleOwner` (bounded to 8 wrappers), with the same foreground-safe observer and polling behavior. No added Gradle dependency. Corrected code [`3aa7d9f1dbf2281796726cb00cb78d52169318da`](https://github.com/PlayerEden/GridMates-Word-Search/commit/3aa7d9f1dbf2281796726cb00cb78d52169318da). New exact-source runs [Android #43756581784](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43756581784) and [art/release #43756581778](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43756581778): **running at initial record**. Must inspect both final conclusions; earlier failures are NOT evidence of a validated v86 APK.
