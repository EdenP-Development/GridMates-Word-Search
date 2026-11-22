# WS59C — Android landscape Activity orientation acceptance

> **VERIFIED ANDROID EMULATOR — ALL FOUR ACTUAL UI TESTS PASSED.** At exact tested source `89ffdc328a2219785ec2b7ec8083226ad22e7965`, [Android/core run #45060619654](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45060619654) and [independent art/release #45060619645](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45060619645) both **SUCCESS**. Emulator logs `OK (3 tests)` portrait and `OK (1 test)` native landscape.

## Observed, not inferred

At exact tested source `765f9c271385a32d0607d4b9ce7322591161f836`:
- [Android/core run #45059275782](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45059275782) **FAILED** in WS59B instrumented landscape test. All **three actual 200%-font portrait tests PASSED** (`OK (3 tests)`): six-chapter navigation with Play/Bookmark, real 20×20 Expert finger trace with disk found-IDs and progress label, and real native precise-picker row/column selection with persisted found word. The earlier WS59 20×20 failure was caused by unqualified outer screen `width/height` in test injection, corrected to `TouchInjectionScope.this.width/height` rather than changing production touch code.
- **Landscape was separately executed**, but the `LocalConfiguration.current.orientation` check failed: the Compose host Activity was still in portrait after shell `wm user-rotation lock 1`. The 15×15 grid was not exercised in landscape; **not a puzzle failure**.
- Independent [art/debug/release #45059275779](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45059275779) **SUCCESS**. [Android test ZIP #14769495562](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45059275782/artifacts/14769495562) contains executable test reports and before/after screenshots.

## Exact WS59C test changes and policy

- Switch test host from `createComposeRule()` to `createAndroidComposeRule<ComponentActivity>()` so the Activity is addressable by the test. Immediately before landscape `setContent`, explicitly request `ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE`, allow Android to update/recreate the activity, and wait for a genuine **Activity resources Configuration.ORIENTATION_LANDSCAPE**, while retaining the separate **LocalConfiguration.current** check inside the actual Compose content.
- Capture the system rotation setting in the emulator-run artifact for troubleshooting. Never substitute a fake wide container for device/Activity orientation, and never disable the real configuration check just to turn CI green.
- The portrait, Expert and precise-picker assertions remain unchanged from their verified WS59B run.
- **Unchanged game:** production Kotlin, all original 63 QA pages and 18 immutable Woodland page SHA pins, real user progress/saves, bonus policy, assets, release files, Quick Play, Daily, Nearby, public vocabulary (122 Woodland candidates unapproved), and Work/main. WS59 remains unmerged DRAFT PR #172. No APK owner reinstall needed.

## Verification receipt

- Target source commit: **PENDING**, to record after single combined commit.
- Android/core and art/release latest workflow results: **PENDING**.
- Actual 150% portrait/landscape QA outcome: **PENDING**. Success requires the previously passing 200%-font portrait 3/3 **plus** a real 15×15 native landscape gesture, hint and persisted found words (`OK (1 test)`) and screenshot evidence.
- Last independently hashed signed owner-phone fallback: [WS57 v107 APK #14734405724](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44971601632/artifacts/14734405724), SHA-256 `304b389d53935ceb8a8857666caf00fbc15e3c51a440ab3cced07f1f684e82f9`.

Next: inspect the actual emulator logs and artifacts. If orientation still fails, do not alter frozen content; diagnose ActivityScenario/manifest behavior, or explicitly mark landscape acceptance open.


## WS59C completed exact execution receipt

- The earlier first WS59C draft had a test-code compile error because the Activity orientation setup landed in the precise-picker test. It was moved strictly into `playViewport` before final tested source; **no production game code touched**.
- **Verified executed source:** `89ffdc328a2219785ec2b7ec8083226ad22e7965`. [Android/core #45060619654](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45060619654) **SUCCESS**; [independent art/release #45060619645](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45060619645) **SUCCESS**.
- API35 emulator: **3 of 3** portrait 320dp / 200% font tests PASS — chapter navigation + Play/Bookmark, 20×20 native drag with persisted word `ws55f013`/Hint and updated progress semantics, separate row/column precise picker with saved `ws55f023`. **1 of 1** landscape 700×340dp / 150% font test PASS: real Activity orientation confirmed `LANDSCAPE` in `WS59_DIAG`; 15×15 native By the Stream Classic swipe finds and persists `ws54s009` with Hint, actual touch area `546×546px`.
- **Actual instrument/screenshot artifact:** [#14769174434](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45060619654/artifacts/14769174434), 7 PNG screenshots + system rotation state + portrait/landscape test output + diagnostic log. **Standard QA APK artifact:** [#14768669102](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45060619654/artifacts/14768669102); internal APK binary hash NOT independently rechecked. No need to reinstall because this pass changed only test harness and docs.
- Verified negative boundaries: no release publication or editorial approval, no owner physical-phone/TalkBack or two-phone LAN acceptance. WS57 v107 remains safe independently hashed APK; WS59 is DRAFT, no main/Work merge.
- **Next:** phone UX audit and two-phone LAN acceptance. Do not modify verified frozen puzzle boards to solve a diagnostic-only issue.
