# November 24, 2026 — Illustrated Word Search beta playtest

> **Status:** Signed APK and Android 35 upgrade rehearsal passed. Handset review is open.

## Handset feedback and starting point

The owner installed Beta 1 and showed its placeholder Home bookshelf and plain puzzle board. Beta 1 code 121 used the release source set to verify signing and portable save import. That configuration loads the fallback catalog and artwork. The illustrated QA v120 build is the visual baseline and fallback. [Previous beta pass](2026-11-24_EDENP_WORD_SEARCH_BETA_CANDIDATE.md) · [QA v120](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45491802328/artifacts/14967073059)

## Playtest changes

[Draft #199](https://github.com/EdenP-Development/GridMates-Word-Search/pull/199) starts this pass at `b27a6d80f2d82c45494200d017995d5db26a6ca7`. The `betaPlaytest` Android build type uses the same preview code and assets as QA v120, with the dedicated beta package and signing certificate. Version `0.9.75-beta.1-playtest` uses Android version code 122 so it can update the installed code 121.

The standard Android workflow prepares the same v120 art and frozen puzzle pages for the unsigned playtest. A separate job signs it on the trusted branch. The regular release source set remains the gate for public art and editorial approval.

## Verification

The signed playtest, standard Android QA, and Android 35 upgrade rehearsal passed at source `a25a99c06d1369ea284bfefefc36e34fc3d93821`. The separate release art isolation check also passed at that source. [Playtest workflow](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45545457282) · [Release art isolation](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45545463731)

| Item | Verified |
| --- | --- |
| Android identity | `com.playereden.gridmates.wordsearch.beta` |
| Version | `0.9.75-beta.1-playtest`, code `122` |
| Signer SHA-256 | `c29c0c593ac99ae81b5a5aaf5fca37bbed3bbd419e8f08287296de6681babba3` |
| APK size and SHA-256 | 30,267,030 bytes · `f5db0a080959f30112b268ed9fa38d9c6ae88284f39ba7e1c68e5bd0833eba35` |
| Preview assets | 239 packaged art and frozen puzzle files match QA v120 byte for byte |
| Install-over | The archived signed Beta 1 code 121 accepted the update. Its portable profile, puzzles and font matched after restart. |
| Full QA restore | The original v120 test backup imported and re-exported with the same profile, font, book completion, bookmark and unfinished page. All three original v120 journal hashes matched. |
| Visual check | Captures show the illustrated Home carousel, the Library with Woodland, Space and Garden, and the restored Woodland page with one word found. |

[Signed APK ZIP](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45545457282/artifacts/14990404723) · [Android run](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45545457282) · [Screenshots and verification](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45545457282/artifacts/14992145523)

Signed QA v120 remains the side-by-side fallback. The next check is handset review of the illustrated update. Public artwork and book catalog approval are open. Use a version code above 122 for any later APK intended to update this beta package. [Prior code 121 signing rehearsal](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45538637429/artifacts/14988966094)
