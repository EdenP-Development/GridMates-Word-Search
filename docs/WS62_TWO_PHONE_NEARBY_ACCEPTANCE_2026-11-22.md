# WS62 — Two real Android phones: Nearby multiplayer acceptance checklist

> **OWNER DEVICE QA NOT YET PERFORMED.** The Android source run [#45068863442](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45068863442) passed three **same-emulator live TCP** modes, NOT this independent two-phone suite. This checklist is for the next physical milestone and preserves the simple GridMates play experience.

## Setup without disrupting saves

- [ ] Both phones run the **same verified signed WS60 QA v108** from [Android artifact #14771725725](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45065450259/artifacts/14771725725), or a later explicitly verified identical-version QA release. Package `com.playereden.gridmates.wordsearch.qa`; do not uninstall, downgrade or clear storage on an existing test phone. Verify each player's Solo/Library progress remains.
- [ ] Both phones use the **same trusted local Wi-Fi or supported hotspot**, not isolated guest Wi-Fi, VPN-only internet or cellular-only. LAN packets are not end-to-end encrypted: avoid untrusted/shared public networks during testing.
- [ ] Note each phone's Android major version, whether it is host/guest, network type (home Wi-Fi/hotspot), and any permission dialog shown. Do not publicly share the room PIN, private local address, or the player's personal profile details in screenshots.

## 1 — Co-op: easiest host/join first

- [ ] Phone A: **Play Together → Host Co-op game** with a default board and optional room code left off for the first attempt. Room visibly opens, host has an address and port.
- [ ] Phone B: **Join nearby game**; confirm room appears in the discovered list with Co-op identity and correct host name. Refresh once; stale duplicates must not appear.
- [ ] If discovery fails: **Join manually instead** and enter the address/port displayed by Phone A. Note that success via manual join is **not** proof that automatic NSD discovery works.
- [ ] Both phones show the **identical letter grid and word list**, same solved/remaining count and no unexplained missing puzzle.
- [ ] Have B trace one real word. Both phones highlight/count **one** host-confirmed word; duplicate selection must not count twice. Have A trace a second.
- [ ] Send a chat message from B, verify it appears once on both devices and its delivered state clears. Reply from A.
- [ ] B temporarily turns off Wi-Fi (not app data), then restores it and uses **Reconnect**. Confirm the host's authoritative word claims and chat history remain. Host disconnecting is a separate expected room-ending condition, not a promise of offline hosting.
- [ ] Background/return the guest app and rotate the screen; ensure obvious controls remain accessible and no old completed claim duplicates.

## 2 — Shared Board Race

- [ ] Phone A hosts **Host Shared Board Race**, B joins. Both share the same board and can see live scores.
- [ ] A claims a target first; B traces that same target and gets the already-found feedback, with **no stolen or double-awarded point**.
- [ ] B finds a different word and earns one point. Confirm both score views agree, neither phone silently ends the game while words remain.
- [ ] Optionally attempt near-simultaneous touches to the same remaining word; host may choose either by arrival order, but **only one** may score.
- [ ] Finish the board and compare winner/tie messaging and saved Race result/journal on each phone. Exit/reopen and confirm no duplicate result was added.

## 3 — Speed Duel

- [ ] Phone A hosts **Host Speed Duel**, B joins; board and hidden word list are not playable before both sides press **Ready**.
- [ ] A taps **I'm ready** and B verifies the board still does not start. Then B taps **I'm ready**; both see GO/start.
- [ ] A finds a target. B sees A's **count**, not A's secret target identity/path. B can find the **same** word privately and both personal counters increase correctly.
- [ ] Finish a Duel or enough of the board to validate per-seat progress, results, and on-device Duel journal. Reopen without duplicate results.
- [ ] Temporarily interrupt B's connection and reconnect; any completed private finds should remain per player, and Duel must not reveal opponent word IDs.

## 4 — Comfort, failure handling, and evidence

- [ ] Check normal portrait, landscape and large Android font setting (150–200%) on both devices. Host, Join, Ready, Reconnect, chat and puzzle-grid gestures must be reachable without accidental taps.
- [ ] If a host goes away, guests see a clear disconnect state rather than a fake successful finish. Wrong room code, wrong port, or old version should provide understandable errors, never silently enter a mismatched match.
- [ ] Return to Solo / Library and verify original bookmarks, half-finished pages, profile and cosmetic preferences are still intact.
- [ ] Record **mode, exact tap sequence, expected vs actual result, device role, Android version and screenshot(s)** for each defect. Do not send private codes or tokens. This is player-test evidence, not a request to upload full device logs.

## Automated evidence vs human acceptance

**Already independently verified:** [WS62 PR #175](https://github.com/PlayerEden/GridMates-Word-Search/pull/175) passes same-emulator live Co-op/Race/Duel TCP integration; [emulator logs #14772767012](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45068863442/artifacts/14772767012). Signed code107→108 upgrade preserving six real saves passed in [WS61 Android run #45067303054](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45067303054). None proves physical two-device Wi-Fi/mDNS or user handset playability.

**Still open:** Owner's physical two-phone run, TalkBack, 122 candidate Woodland terms' editorial approval, production signing/AAB, legal/content review and monetization. This checklist is not a public release gate approval. Keep all DRAFT PRs unmerged and production release isolated.
