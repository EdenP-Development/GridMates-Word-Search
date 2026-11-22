# WS62 — Real Android TCP host/guest mode integration QA

> **Status: IMPLEMENTED, ANDROID EMULATOR/ART CI PENDING.** This deliberately runs two independent host/guest runtime objects **within one Android emulator app process** over real TCP sockets. It is NOT a two-physical-phone test, separate emulator networking or external router/discovery acceptance.

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

## Build/QA receipt (pending actual Actions run)

- **New draft PR / exact source SHA:** pending after branch creation.
- **Android/core and independent art/release run IDs, conclusions:** pending.
- **Expected Android evidence:** `OK (3 tests)`, one verified completion log per COOP/RACE/DUEL, no runtime errors. If emulator lacks a genuine site-local wlan/ap IPv4, classify as **emulator network environment blocker**, not success or a production gameplay bug; do not inject fake Wi-Fi into runtime.
- **APK:** no new player version intended. Actions may repackage the code108 QA artifact; the already independently hashed WS60 v108 remains phone-test baseline. No APK/install request for the user; do not downgrade/uninstall.
- **Still open:** two *physical* devices on the same Wi-Fi; NSD search, manual join, player-ready, Co-op chat/claim, Race ownership, Duel private scoring, disconnect/reconnect and on-phone UI; owner saved-game/large-font acceptance, TalkBack, book word editorial rights and Play Store publishing.

## Next exact action

Open WS62 DRAFT against WS61, check actual CI jobs and logcat. If host cannot open the real emulator WLAN socket, record the infrastructure failure and continue with a non-bypassing alternative. If the live socket mode cases pass, move next to an owner-friendly two-handset protocol checklist/build, not to a false claim of two-phone acceptance.

**New-chat one-liner:** WS62 actual Android TCP Nearby host/guest modes in one emulator, based on WS61. Check exact DRAFT PR and both workflows, fix only proven errors. Preserve code108 owner APK and 81 frozen QA pages; physical two-phone NSD remains unverified.
