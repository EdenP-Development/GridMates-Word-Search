# November 24, 2026 — Word Search beta signing setup

The owner asked to set up organization signing for Word Search. The [Word Search repository](https://github.com/EdenP-Development/GridMates-Word-Search) is now managed by EdenP Development and has a verified persistent QA identity. This pass prepares its first itch.io signing identity. GitHub shows no Word Search releases. The shared registry had no previous public beta certificate.

## Signing identity

| Field | Value |
| --- | --- |
| Planned itch.io application ID | `com.playereden.gridmates.wordsearch.beta` |
| Keystore format and alias | PKCS12 · `gridmates-word-search-beta` |
| Certificate SHA-256 | `c29c0c593ac99ae81b5a5aaf5fca37bbed3bbd419e8f08287296de6681babba3` |
| Keystore secret | `GRIDMATES_WORD_SEARCH_BETA_KEYSTORE_BASE64` |
| Password secret | `GRIDMATES_WORD_SEARCH_BETA_KEYSTORE_PASSWORD` |

The key was generated separately for this project. The password also unlocks the PKCS12 key entry. Only the organization owner can enter the two prepared values in [EdenP's Actions settings](https://github.com/organizations/EdenP-Development/settings/secrets/actions). Use **Selected repositories** and select **GridMates-Word-Search** for both secrets. Save the original encrypted keystore in the owner’s encrypted backup and keep its password in a password manager. Keep a second independent encrypted recovery copy. [EdenP signing standards](https://github.com/EdenP-Development/Eden-Development-Standards/blob/main/SIGNING_KEY_MANAGEMENT.md)

## Verification

- The preflight is on [draft #199](https://github.com/EdenP-Development/GridMates-Word-Search/pull/199) at source `768a179b1d9da1be061a938d116eeeff0a9f201e`, based on [verified v120 draft #198](https://github.com/EdenP-Development/GridMates-Word-Search/blob/b95fc54107d32c00ee6d52baf27ae700c6333705/CONTINUE_HERE.md). It runs on a push to the dedicated `ci/word-search-beta-signing` branch with an empty repository token permission set. The job opens the PKCS12, checks the exact public certificate, and verifies a signed request against that certificate. Temporary files are removed at the end of the job.
- The identical workflow shell passed locally with the prepared key. It rejected missing credentials, malformed Base64, an incorrect password, a mismatched certificate and a matching certificate without the private key.
- The [first organization preflight](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45496175839) reached that check and stopped because the two organization secrets were missing or unavailable to this repository. Local proof is complete. GitHub proof and owner backup confirmation are pending.
- The job signs a verification request. A signed beta APK, confirmed Android identity and save-import rehearsal are later release work.

The latest playable build is [signed QA v120](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45491802328/artifacts/14967073059) under `com.playereden.gridmates.wordsearch.qa`. The planned beta package can install alongside it when a beta APK is prepared.

After the owner saves both values, rerun the preflight and record the GitHub-verified fingerprint in the shared registry. The next game milestone is Space’s album, using draft #198 as the gameplay base.
