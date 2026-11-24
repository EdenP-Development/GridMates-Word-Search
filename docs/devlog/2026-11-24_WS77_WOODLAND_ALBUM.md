# November 24, 2026 — Woodland album

Signed QA v120 gives Woodland its own five-stamp album. Collection, book details and the puzzle result open the same album. Progress comes from existing book journals across the original volume, the later Woodland chapters and both Quiet Clearings expansions. The six Birdsong pages count together.

| Stamp | Requirement |
| --- | --- |
| First Footsteps | Finish any Woodland page |
| Trailhead | Finish the Getting Started chapter |
| Dawn Chorus | Finish all six Birdsong pages |
| Deep in the Woods | Finish 30 Woodland pages |
| Woodland Wanderer | Finish all 45 Woodland pages |

The album uses the existing woodland emblems, bookmark and robin. The geometric evergreen badges and painted robin should be compared at stamp size when the collections art is reviewed for cohesion.

## Android verification

- GitHub Android QA and release isolation passed at `9b47e8e8aee4d8d94e8fcab3e5b461d6f7b31107`. [Android run](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45491802328) · [Release isolation](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45491802212)
- The emulator seeded activity and book progress in the original signed v119 APK, including eight completed pages across the four Woodland editions and one found word on page 2. Installing over it with v120 kept the Solo, book and page-session journal bytes identical.
- Actual touch input finished the original page 2 and an appended 20×20 Birdsong page. Both earned their stamps, and restarting the activity retained 3 of 5 stamps and 10 completed Woodland pages. The appended preview page earned Woodland album credit while keeping the existing eligibility rules for profile awards.
- At 320dp and 150% text, the emulator opened Woodland, Garden and Activity and reached the last Woodland stamp. Six screenshots and three passing instrumentation logs were verified in the downloaded archive. [Emulator evidence](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45491802328/artifacts/14966817843)
- All 244 packaged assets matched v119 byte for byte. The content packs and frozen puzzle boards are the same in Git.

## Signed QA APK

| Field | Verified value |
| --- | --- |
| Channel and application ID | Persistent QA · `com.playereden.gridmates.wordsearch.qa` |
| Version | `0.9.75-ws77-woodland-album-qa` · code `120` |
| Certificate SHA-256 | `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419` |
| APK size and SHA-256 | 34,328,535 bytes · `0650e7fc6fb6198e22b20b8f6360c7379fe668a263b4eebb9d522f787b286a09` |
| APK ZIP SHA-256 | `65737e98526cf504e3776d2c27cfdd5eaeec1176064b860c7a73b26bc1aaa177` |
| Evidence ZIP SHA-256 | `c4c85dadc7a0028a1d1c8c7a74e6868784a24eca1cf9db3bb4392bb276963438` |

[Signed v120 APK ZIP](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45491802328/artifacts/14967073059) · [Original signed v119](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45446328318/artifacts/14946605616) · [Draft #198](https://github.com/EdenP-Development/GridMates-Word-Search/pull/198) · [EdenP transfer record](https://github.com/EdenP-Development/GridMates-Word-Search/blob/77f5620e22946deb3059ed4cbbb00cb94a63f2d0/docs/devlog/2026-11-24_EDENP_WORD_SEARCH_TRANSFER.md)

Phone acceptance is open. The next book album is Space, with a save-upgrade test that counts the original and appended Solar System pages.
