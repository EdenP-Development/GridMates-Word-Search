# November 24, 2026 — Word Search beta key verified in GitHub Actions

The owner added the two Word Search organization secrets. The first run had stopped at the missing-secrets check. The retry at the same source passed using the dedicated itch.io beta signer. [Signing setup](2026-11-24_EDENP_WORD_SEARCH_BETA_SIGNING.md) · [Signing draft #199](https://github.com/EdenP-Development/GridMates-Word-Search/pull/199)

| Verification | Result |
| --- | --- |
| GitHub Actions | [Run 45496175839, attempt 2](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45496175839) passed |
| Source | `768a179b1d9da1be061a938d116eeeff0a9f201e` on `ci/word-search-beta-signing` |
| Signing operation | Opened the PKCS12 and signed a verification request |
| Public certificate SHA-256 | `c29c0c593ac99ae81b5a5aaf5fca37bbed3bbd419e8f08287296de6681babba3` |
| Planned beta package | `com.playereden.gridmates.wordsearch.beta` |

GitHub verified the request signature with the key from the certificate pinned in the workflow. The result establishes that Word Search can use both organization secrets. Owner backup confirmation remains open.

## APK and game handoff

This was a signing-key check. [Signed QA v120](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45491802328/artifacts/14967073059) is the latest verified playable build. Its SHA-256 is `0650e7fc6fb6198e22b20b8f6360c7379fe668a263b4eebb9d522f787b286a09`.

For the first itch.io candidate, build a separate beta APK and verify its package, version, and APK signature against this fingerprint. Rehearse portable save import before distribution. Space’s album continues from [gameplay draft #198](https://github.com/EdenP-Development/GridMates-Word-Search/pull/198).
