# WS62 — Real Android TCP host/guest mode integration QA

> **VERIFIED — three live TCP mode tests and both Android/art pipelines passed.** Exact executed code `b7fc886bb82833d133e8fe26b8d63033478e05b8`. This runs two independent host/guest runtime objects **within one Android emulator app process**, NOT two physical phones or separate Android devices. External router/NSD room discovery and real-user testing remain OPEN.

## Actual WS62 execution receipt (November 22, 2026)

- **Source SHA tested:** `b7fc886bb82833d133e8fe26b8d63033478e05b8`. Subsequent documentation commits do not alter the compiled/tested app. [DRAFT PR #175](https://github.com/PlayerEden/GridMates-Word-Search/pull/175) unmerged atop WS61.
- **Android/core and exact live socket QA:** [GitHub run #45068863442](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45068863442) **SUCCESS**. Native API35 emulator output exactly `OK (3 tests)`; separate instrumentation statuses 0 for all three `Ws62NearbyTcpIntegrationAndroidTest` methods. Android core/app unit/lint/debug APK build, instrument compile and branch-scoped full socket test all succeeded.
- **Independent art/debug/release:** [run #45068863479](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45068863479) **SUCCESS** on the same tested source. No new game art, rights clearance or release content changes.
- **Raw emulator evidence:** [workflow artifact #14772767012](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45068863442/artifacts/14772767012), ZIP independently downloaded, verified CRC, 19,129 bytes, SHA256 `ed81be0ff391b83aeb60bf76f7c9b2f76de84fd98e4efc6ab038d2143cdd1f60`. Includes all three instrument methods, six `WS62_NETWORK_QA` log lines (handshakes + mode success), AndroidRuntime evidence, install/version and emulator network diagnostics.
- **Verified real TCP behaviors:** **Co-op** live host/join Welcome/Accept/Joined, authoritative guest FOUND receipt, chat confirmed to both sockets once, host sees disconnect, fresh guest runtime rejoins using previously committed credential with words/chat preserved. **Race** host claims first word, guest duplicate attempt receives `ALREADY_FOUND` with no point, guest claims next word and scores, no early finish. **Speed Duel** guest and host Ready/GO, host finds target, guest receives aggregate host count but not host private word IDs, guest separately finds the same target, both recipients see their own found IDs and correct counts.
- **Network evidence caveat:** The shell's early `ip -4 addr show` capture in the ZIP lists loopback only, while the runtime's production `hostNetworkAvailable()` and actual host socket creation/join both succeeded inside Android instrumentation later. It does **not independently establish a physical Wi-Fi connection or external WLAN interface/NSD advertisement**. Do not overstate this test as two independent network namespaces/devices or router-level QA.
- **Built QA APK:** Standard workflow emitted [Android QA artifact #14772905916](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45068863442/artifacts/14772905916); internal binary SHA not independently rechecked and this **test-only** pass adds no production code or version bump. **No new owner install needed**. Last independently hashed owner-facing signed v108 remains [WS60 artifact #14771725725](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45065450259/artifacts/14771725725), known SHA256 `6a7d59e6fed8bd2524abd216027b3caf7445aa8afa79c7fa8f2f3600b65a3b3b`.
- **Safeguards:** No production runtime/manifest/LAN v7 changes, no profiles/saves, 63 legacy + 18 WS57 immutable QA page IDs/board hashes, images/assets, hint/bonus/progression, Daily/Quick Play, public release catalog or editorial data changed. **122 new Woodland words still unapproved**.

**Result:** WS62 *single-emulator live Android TCP host/guest transport* QA COMPLETE. **Separate physical two-phone acceptance still required:** independent app sandboxes/credentials, automatic Nearby NSD discovery vs manual-IP fallback, real shared Wi-Fi/hotspot/router isolation, actual touches at 200% font/landscape, reconnection on radio outage, host/guest resume/background, completed match result cards, and stable result journals. No user-phone or public release certification.

**Next exact step:** use [WS62 physical two-phone acceptance checklist](../WS62_TWO_PHONE_NEARBY_ACCEPTANCE_2026-11-22.md) on two Android handsets with the same verified signed WS60 v108 QA APK. Capture failures and fix only real observed defects; then continue visual cohesion and editorial release gates.


## Who, what, where, why

- **Owner:** requested continued GridMates: Word Search beta stabilization after WS61 signed APK install-over passed.
- **Work performed by:** ChatGPT assistant on connected GitHub; new isolated draft stacked on WS61, not merged into main/Work.
- **What we are verifying:** live Nearby transport beyond pure wire/session unit tests: actual host ServerSocket bind on emulated local Wi-Fi, manual guest socket join, Welcome / credential-commit / Accept / Joined, host-authoritative Co-op finds, receipt, chat, guest disconnect/rejoin, Shared Board Race first claimant ownership, and private Speed Duel ready/go + same-word independent finds. All three public Nearby modes are tested without faking socket payloads.
- **Why now:** existing Android tests mostly cover serializable packets, pure session behavior, navigation and simulated UI; physical two-phone acceptance remains open. Real TCP host/guest flows are a necessary intermediate reliability gate, but do not substitute for two handsets on shared Wi-Fi.
- **Starting verified source:** [WS61 PR #174](https://github.com/PlayerEden/GridMates-Word-Search/pull/174), exact executed `684d331387f6f5c10e84918086426ad7aeaa70dc`. Both [Android/core #45067303054](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45067303054) and [independent art/release #45067303073](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45067303073) SUCCESS, actual signed code107→108 install-over preserving six app-private files and post-upgrade decode.
- **Last independently verified player QA APK:** signed WS60 v108 [artifact #14771725725](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45065450259/artifacts/14771725725), SHA256 `6a7d59e6fed8bd2524abd216027b3caf7445aa8afa79c7fa8f2f3600b65a3b3b`. WS62 changes no player-facing runtime/version.

## Files and verification strategy

1. `Ws62NearbyTcpIntegrationAndroidTest.kt`: three independent actual Android instrumented scenarios. Each allocates distinct host and guest `NearbyCoopRuntime` instances in one process with real server/client TCP transport, asynchronous state, exact room ID, and deterministic timeout. Requires production `hostNetworkAvailable()` (site-local wlan/ap IPv4) and does not add a test-only runtime bypass.
   - **Co-op:** real welcome/accept/joined and packet version; guest finds a real generated word; host claim ledger and guest FOUND receipt; guest chat confirmed exactly once; guest runtime closes, host observes disconnect; new runtime rejoins using persisted room credential; found words/chat remain.
   - **Shared Board Race:** host first claims a target, guest attempts same target and receives `ALREADY_FOUND` without scoring; guest claims another and receives FOUND/one point; uncompleted match has no fabricated winner.
   - **Speed Duel:** both hosts/guests explicitly Ready; host-authoritative GO delivered; host finds word privately, guest sees host count only and not host target IDs; guest can separately find same word, both counts become one with recipient-scoped found sets; shared claims remain empty.
2. `scripts/ws62_nearby_live_tcp_emulator.sh`: install same-signer QA code108 and androidTest APK, capture actual emulator IPv4 interfaces and Wi-Fi diagnostics, run exactly the three named tests with hard `OK (3 tests)` check, preserve AndroidRuntime + WS62_NETWORK_QA logcat and failures as archived artifact.
3. `.github/workflows/ws01-android-qa.yml`: branch-scoped API35 Pixel 4 Android emulator stage, KVM setup and evidence upload, on top of normal core/app unit/lint/QA build and independent art/release safeguards.

## Safety, limitations and release status

- Intentionally **unchanged**: production `NearbyCoopRuntime`, LAN v7 packet rules, NSD service, Android manifest/permissions, UI, saved personal profile/book data, V5 generator, all original **63 + 18 immutable QA book pages and board SHA pins**, artifacts, image library, rewards, Daily/Quick Play, versionCode108, public vocabulary and monetization.
- The Android QA methods use ephemeral game sessions and a test-only room credential in app preferences on a disposable GitHub emulator. They **do not use two independent Android devices or simulate separate Wi-Fi adapters**, do not prove multicast DNS-SD room search, Wi-Fi AP isolation, NAT/multicast quirks, separate OS app sandbox credentials, Android 16 permission prompts, host background/kill, remote radio disconnect, or human touch latency.
- Manual address fallback is exercised, not NSD browsing. Co-op/Race/Duel game mechanics are exercised through real sockets, not the physical Android touch UI on two screens.
- LAN protocol remains unencrypted; physical tests should use a trusted Wi-Fi network. Do not promise operation on isolated guest Wi-Fi/cellular-only network.
- **122 Woodland candidate words remain unapproved and QA-only.** WS62 is not release approval.

## Original pre-CI checklist (historical; superseded by verified receipt above)

- **New draft PR / exact source SHA:** pending after branch creation.
- **Android/core and independent art/release run IDs, conclusions:** pending.
- **Expected Android evidence:** `OK (3 tests)`, one verified completion log per COOP/RACE/DUEL, no runtime errors. If emulator lacks a genuine site-local wlan/ap IPv4, classify as **emulator network environment blocker**, not success or a production gameplay bug; do not inject fake Wi-Fi into runtime.
- **APK:** no new player version intended. Actions may repackage the code108 QA artifact; the already independently hashed WS60 v108 remains phone-test baseline. No APK/install request for the user; do not downgrade/uninstall.
- **Still open:** two *physical* devices on the same Wi-Fi; NSD search, manual join, player-ready, Co-op chat/claim, Race ownership, Duel private scoring, disconnect/reconnect and on-phone UI; owner saved-game/large-font acceptance, TalkBack, book word editorial rights and Play Store publishing.

## Next exact action

Open WS62 DRAFT against WS61, check actual CI jobs and logcat. If host cannot open the real emulator WLAN socket, record the infrastructure failure and continue with a non-bypassing alternative. If the live socket mode cases pass, move next to an owner-friendly two-handset protocol checklist/build, not to a false claim of two-phone acceptance.

**New-chat one-liner:** WS62 actual Android TCP Nearby host/guest modes in one emulator, based on WS61. Check exact DRAFT PR and both workflows, fix only proven errors. Preserve code108 owner APK and 81 frozen QA pages; physical two-phone NSD remains unverified.
