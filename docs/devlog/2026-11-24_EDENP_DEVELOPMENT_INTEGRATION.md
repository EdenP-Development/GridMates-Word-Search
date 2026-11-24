# November 24, 2026 — EdenP Development integration

The owner asked to connect GridMates: Word Search to the shared EdenP development standards while preserving the active game and its distribution identities. The [shared standards](https://github.com/EdenP-Development/Eden-Development-Standards) are maintained in the organization. This receipt records the pre-transfer state. See the [completed transfer and verification](2026-11-24_EDENP_WORD_SEARCH_TRANSFER.md) for the current repository address and checks.

## Repository and development

| Item | Verified state |
| --- | --- |
| Word Search repository | [PlayerEden/GridMates-Word-Search](https://github.com/PlayerEden/GridMates-Word-Search), repository ID `1447522480`. The owner's [transfer settings](https://github.com/PlayerEden/GridMates-Word-Search/settings) can move it to `EdenP-Development`. |
| Default branch | `main` at `9043cf4c942da598b5841cc39c361c99abd9ef26`; cumulative development is in the draft stack. |
| Active development | [WS76 draft #197](https://github.com/PlayerEden/GridMates-Word-Search/pull/197), stacked on [Garden #196](https://github.com/PlayerEden/GridMates-Word-Search/pull/196) through [integration #189](https://github.com/PlayerEden/GridMates-Word-Search/pull/189). Prior checkpoint is `182c0f7238d2610eb4efd1a5afba184e6bcbafdf`. |
| Woodland work in progress | [WS77 checkpoint](https://github.com/PlayerEden/GridMates-Word-Search/commit/59030b660f47fd3abc6fba548206065764c9b327) stores the album goals, grouped-edition progress rules and initial wiring. Continue the Collection route, stamp display and upgrade tests from `feature/ws77-woodland-album-v120-2026-11-24`. This checkpoint has no APK. |
| Existing verification | 63 workflow files include Android lint/unit tests, focused emulator journeys, signed install-over, immutable puzzle QA, asset provenance and debug/release isolation. [Latest Android pass](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45446328318) · [Release isolation](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45446328171) |
| Relevant open work | [Two-phone Nearby #53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53), [vocabulary expansion #87](https://github.com/PlayerEden/GridMates-Word-Search/issues/87), [bookshelf #90](https://github.com/PlayerEden/GridMates-Word-Search/issues/90), [asset pipeline #91](https://github.com/PlayerEden/GridMates-Word-Search/issues/91) and [release acceptance #121](https://github.com/PlayerEden/GridMates-Word-Search/issues/121). |

Word Search has 93 book pages across Woodland, Space and Garden. The latest QA adds Activity and Garden albums, restores local progress and previews the size and word count of the next page. Woodland is the current development step. See the [product roadmap](../CURRENT_PRODUCT_ROADMAP.md), [master game design](../MASTER_GAME_DESIGN.md) and [visual identity](../WS14_FULL_APP_VISUAL_IDENTITY_AND_UI_PRODUCTION_ROADMAP_2026-11-08.md).

The Sudoku repository moved into EdenP Development. The Word Search workflow checkouts now address `EdenP-Development/SudokuTogether` at the same verified commit `975e31a1e8cbfebad1fbbc7602b3340c357da7ef`. This preserves the source of the 12 existing Woodland portraits and the asset hash checks. [Pinned source](https://github.com/EdenP-Development/SudokuTogether/commit/975e31a1e8cbfebad1fbbc7602b3340c357da7ef)

## Android identities and recovery

| Channel | Package and signing |
| --- | --- |
| Persistent QA | `com.playereden.gridmates.wordsearch.qa`, signed with the existing public test-only key. Certificate SHA-256 `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419` matches both the trackable QA key and Android CI. |
| itch.io public beta | First public beta signing identity and distribution package are pending. The history reviewed includes internal QA artifacts. GitHub lists zero releases. |
| Production variant | `com.playereden.gridmates.wordsearch`, unsigned by default. Its configured code `108` is a build setting. Public distribution is unverified. |
| Disposable preview | A separate signed disposable channel is unconfigured. Persistent QA APKs use the established QA package and signer. |

Signed QA v119 is version `0.9.74-ws76-activity-album-qa`, code `119`. It was built from `f0a7152b43d8fb69ecd629e34b0b1a789a35c9cd` and its APK SHA-256 is `ae0a9a9834c48d1d94dcf4a188d7e5020c672393ba36bdcbd50026bb9e78a2b4`. The emulator verified that installing it over signed v118 preserved the Solo and book journals byte for byte. The signed v119 APK is the current fallback. [APK ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45446328318/artifacts/14946605616) · [Android evidence](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45446328318/artifacts/14947400389)

The active workflows reference no private Word Search signing inputs. Organization secret inventory requires settings access. The Sudoku credential preflight is a working pattern for a project's own certificate. A future Word Search beta should establish its own stable identity for itch.io, use dedicated organization secrets restricted to this repository, and verify the private-key operation and final APK signer before distribution. Google Play is a later release decision with its own progression policy. [EdenP signing and custody](https://github.com/EdenP-Development/Eden-Development-Standards/blob/main/SIGNING_KEY_MANAGEMENT.md) · [Sudoku preflight](https://github.com/EdenP-Development/SudokuTogether/actions/runs/45479430913)

Word Search has versioned local saves and a manual backup flow with validation, preview and restore recovery. The code includes Wi-Fi and network access for Nearby play. Review the manifest, network paths and backup/sharing behavior when drafting a public disclosure. [Save standards](https://github.com/EdenP-Development/Eden-Development-Standards/blob/main/SAVE_PRESERVATION.md) · [Release policy](https://github.com/EdenP-Development/Eden-Development-Standards/blob/main/RELEASE_POLICY.md)

## Transfer verification

The first step is a transfer of the existing repository to `EdenP-Development`. After the move, compare the repository ID, branch SHAs, issue and PR numbers, and archived artifact access. Check write access with a lease-protected docs update, change the active Word Search links and local Git remote to the confirmed owner, and run Android QA and release isolation from the transferred repository. Then resume Woodland from its recorded checkpoint.

This pass updates project documentation, registry inventory and pinned checkout addresses. Signed v119 remains the latest verified build. A new Android build, post-transfer CI and public beta signing are pending.
