# WS59B — Touch geometry diagnostics and independent landscape test

> **PENDING Android emulator execution.** A source fix or APK build is not a QA pass.

## Basis
- Latest actual WS59 Android run [#45036801446](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45036801446) failed after the 200% enlarged-text chapter navigation PASSED; 20×20 Expert swipe did not show the expected 1/24 progress label and the 150% landscape case was skipped.
- Same-source art/release [#45036801420](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45036801420) passed.
- Suspected test defect: `playViewport(width: Int, height: Int)` used unqualified `width/height` inside a nested touch injector; those lexical screen dimensions are not the actual rendered puzzle-grid dimensions at enlarged font size. **This is not proof of a production gameplay bug.**

## Changes
- Test only: calculate word-swipe endpoints using `this.width` and `this.height` of the *real touch injection scope*. Capture before/after emulator screenshots and emit `WS59_DIAG` evidence including frozen target, board size, touch geometry, saved found IDs and hints. Check disk found IDs **before** testing UI label semantics.
- Separate 200% Expert accessible precise-row/column picker interaction, targeting a frozen board word using the visible native controls (no direct store claim), then verify persisted found ID.
- Script: run portrait **3 tests** and landscape **1 test** as independent groups even when one fails; copy any existing screenshots and AndroidRuntime/diagnostic logs before the final status. Use actual `wm user-rotation` and composable device-orientation assertion.
- Unchanged: all production runtime Kotlin, original 63 QA pages, Woodland 18 frozen IDs/board hashes, user saves, release lexicon and 122 unapproved Woodland candidates, existing art, Daily, Quick Play, and Nearby. No merge or owner APK reinstall.

## Receipt to complete after CI
- Exact tested commit: **PENDING**.
- Android/core and art/release runs and outcome: **PENDING**.
- Expected emulator evidence: `OK (3 tests)` portrait; `OK (1 test)` landscape; saved words/hints and screenshot export. Do not claim physical phone accessibility or TalkBack coverage from emulator success.
- Rollback: verified WS57 signed v107 [APK artifact #14734405724](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44971601632/artifacts/14734405724), SHA-256 `304b389d53935ceb8a8857666caf00fbc15e3c51a440ab3cced07f1f684e82f9`.

Next: verify actual UI results before altering the real game. If an actual enlarged-text touch defect remains, use saved IDs and the before/after captures to establish the cause and change the smallest possible runtime portion in a separate reviewable milestone.
