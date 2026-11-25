# November 24, 2026 — Illustrated Word Search beta playtest

> **Status:** Build and install-over rehearsal pending.

## Handset feedback and starting point

The owner installed Beta 1 and showed its placeholder Home bookshelf and plain puzzle board. Beta 1 code 121 used the release source set to verify signing and portable save import. That configuration loads the fallback catalog and artwork. The illustrated QA v120 build is the visual baseline and fallback. [Previous beta pass](2026-11-24_EDENP_WORD_SEARCH_BETA_CANDIDATE.md) · [QA v120](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45491802328/artifacts/14967073059)

## Playtest changes

[Draft #199](https://github.com/EdenP-Development/GridMates-Word-Search/pull/199) starts this pass at `b27a6d80f2d82c45494200d017995d5db26a6ca7`. The `betaPlaytest` Android build type uses the same preview code and assets as QA v120, with the dedicated beta package and signing certificate. Version `0.9.75-beta.1-playtest` uses Android version code 122 so it can update the installed code 121.

The standard Android workflow prepares the same v120 art and frozen puzzle pages for the unsigned playtest. A separate job signs it on the trusted branch. The regular release source set remains the gate for public art and editorial approval.

## Verification

The workflow will compare the packaged preview assets with the archived v120 APK. The emulator seeds the previous signed beta, installs the playtest over it and compares the portable progress. It then imports a complete v120 backup, including book bookmarks and unfinished pages, and checks the illustrated Home, Library and resumed Woodland page.

Build, evidence and handset review are pending. The next step is to verify the package and updates on Android before handing off the test APK. [Signing draft](https://github.com/EdenP-Development/GridMates-Word-Search/pull/199)
