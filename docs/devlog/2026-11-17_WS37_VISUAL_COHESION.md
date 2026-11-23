# November 17, 2026 — WS37 puzzle flow visual cohesion

The owner approved a visual and asset overhaul to make GridMates Word Search feel like a finished mobile game. The v98 rounded bindings received handset approval. [WS36](2026-11-17_WS36_MATCHED_BOOK_BINDINGS.md) matched the three Home covers to those bindings. This pass starts at `ccdb8d5a3039e53fd9140935642f5c6b22ab042a` in [draft PR #147](https://github.com/PlayerEden/GridMates-Word-Search/pull/147).

## Screen and art decisions

Home, the full bookcase, and Collection already use the illustrated paper, wood, green, and gold treatment. The puzzle view had plain navigation, a detached hint counter, and a heavily constrained board. The shared title and progress panels carry the same UI style through Solo, Daily, book pages, and live Together games. Solo and Daily use a larger board at regular text sizes. Short screens and large text put precision and zoom controls in Options. The word list still scrolls independently of the board. Solo setup and the Daily action use the same illustrated buttons as Home.

The result journal uses the matching cover for the theme where QA art is enabled, along with saved time, hints, and a readable progress summary. It uses the available space for text at larger text sizes. The existing asset set, game entry points, timers, gesture handling, and save formats are carried into this build.

## Build and verification

The first source was `fa8f72a4830480024ad284aa1c7fd88aa007f25a` in [draft PR #148](https://github.com/PlayerEden/GridMates-Word-Search/pull/148), stacked on #147. The [initial emulator run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44157855931) confirmed install-over from v99 with a saved hint, independent Solo word scrolling, returning safely from setup, a complete Daily solve through touch input, and retained Daily completion after restarting. Its 320 × 640 dp check at 200% text failed because the word list had no room for a target.

The correction at [`ac1bb62783232e4ea5dff73bec037becc6f9be68`](https://github.com/PlayerEden/GridMates-Word-Search/commit/ac1bb62783232e4ea5dff73bec037becc6f9be68) reduces the title area during play, uses the existing compact filter at enlarged text, and presents dismissible hint notices above the list. The compact Android regression test now checks that a target is displayed. The corrected signed APK passed both build workflows and the Android emulator check.

| Check | Result |
| --- | --- |
| Unit tests, lint, instrumentation compilation, and Android QA signing | [Passed](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44176136860) |
| Independent debug and release builds with artwork isolation | [Passed](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44176137245) |
| Signed QA APK | [Standard archive](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44176136860/artifacts/14369903140), 34,125,470 bytes |
| Package and version | `com.playereden.gridmates.wordsearch.qa`, code 100, `0.9.56-ws37-qa` |
| APK SHA-256 | `c5957595f5a84a3140a19f60dd71bf52b13936a77ff6c06ff3998db7e6a3e07a` |
| QA certificate SHA-256 | `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419` |
| Independent APK | [Independent archive](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44176137245/artifacts/14368864338). Each of the 372 extracted entries matches the signed QA APK. |
| Packaged assets | All 228 asset entries match the v99 APK byte for byte, including all three cover and spine pairs. |
| Android runtime | [Passed on Android 15](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44177294910). The exact signed APK installed over v99 and retained a used hint. Solo scrolling, setup cancellation, a Daily solve through touch input, result review, and restart persistence passed. At 320 × 640 dp and 200% text the word list, dismissible hint notice, zoom tools, and precision action stayed reachable. |
| Actual Android screenshots | [Screenshots and device logs](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44177294910/artifacts/14371036928). Normal layout was exercised at 440 × 720 dp. |

The fallback for handset testing is [signed v98](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44136465891/artifacts/14352522134). The [v99 signed APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44142826177/artifacts/14354454242) is the matched-book reference.

## Follow-through

Review v100 on the owner's handset. Two-device Together runtime testing is pending. The next feature after handset review is portable backup and restore for completed, bookmarked, and unfinished book pages.
