# WS59C — Android landscape Activity orientation acceptance

> **Status: NEW SOURCE AUTHORED, CI PENDING.** No claim of landscape success until actual instrumented test passes.

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
