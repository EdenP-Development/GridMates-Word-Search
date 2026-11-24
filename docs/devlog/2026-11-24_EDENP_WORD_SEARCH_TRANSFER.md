# November 24, 2026 — Word Search transfer verification

The owner transferred GridMates: Word Search to EdenP Development. GitHub reports the same repository ID `1447522480` at the [new canonical repository](https://github.com/EdenP-Development/GridMates-Word-Search); the old address resolves to that same repository. This follows the [pre-transfer inventory](2026-11-24_EDENP_DEVELOPMENT_INTEGRATION.md).

| Check | Confirmed after the move |
| --- | --- |
| Default branch | `main` at `9043cf4c942da598b5841cc39c361c99abd9ef26` |
| Integration draft | [#189](https://github.com/EdenP-Development/GridMates-Word-Search/pull/189) at `dbe0448ac1f8f7a46e24ea204e2b387049bfe15b` |
| Garden draft | [#196](https://github.com/EdenP-Development/GridMates-Word-Search/pull/196) at `aabe55ce0d714c0dd6a0d88c39d191a6cd12bd3d` |
| Active v119 draft | [#197](https://github.com/EdenP-Development/GridMates-Word-Search/pull/197) was transferred at `ac6bf762e8adbe96182600d4141ca3c4b1b44bd5` |
| Woodland progress | [WS77 checkpoint](https://github.com/EdenP-Development/GridMates-Word-Search/commit/59030b660f47fd3abc6fba548206065764c9b327) on `feature/ws77-woodland-album-v120-2026-11-24` |
| Issue tracker and history | [Vocabulary #87](https://github.com/EdenP-Development/GridMates-Word-Search/issues/87) remains open. Original v119 [Android](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45446328318) and [release isolation](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45446328171) runs are accessible. |

The local Git remote and current development entry points use the canonical EdenP address. Workflow and helper references were updated to use the transferred repository; their source refs, artifact IDs and test coverage were retained. The production package configuration, game code, QA certificate and puzzle data were outside the transfer changes.

The signed fallback is v119, `com.playereden.gridmates.wordsearch.qa` version code `119`. The original v119 APK SHA-256 is `ae0a9a9834c48d1d94dcf4a188d7e5020c672393ba36bdcbd50026bb9e78a2b4`. The v118 install-over preserved the Solo and book journals. Public itch.io signing remains unconfigured. [Original v119 APK ZIP](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45446328318/artifacts/14946605616)

## Fresh Android verification

Both workflows passed at source `7df956c779d5047cd7bc7a5ad1d765b3873f81eb` on [draft #197](https://github.com/EdenP-Development/GridMates-Word-Search/pull/197). GitHub branch updates with the expected parent confirmed write access. The first Android attempt hit a Maven Central HTTP 429 during dependency retrieval. The retry passed at the same source.

- Android built the signed v119 QA APK, installed it over v118, and preserved both the Solo and book journals byte for byte. The emulator verified the Activity stamps, Garden page 3, and a 320dp large-text layout. [Android run](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45489605994) · [Emulator evidence](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45489605994/artifacts/14965819598)
- The rebuilt APK has the same application ID, version code, QA certificate and **244 byte-identical packaged assets** as the original v119. The rebuilt APK is 34,328,531 bytes with SHA-256 `0596dfa4c816ac766a0ec7f2fc95fb53f0c37787eab428f7657ed3098033b419`. The [APK ZIP](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45489605994/artifacts/14967370605) has SHA-256 `d129b65f5d6b7b0efb10fe473258ea93e0dea9c91dea5f4216dcda31bda53654`.
- Release isolation passed, including verification of the pinned artwork sources and that debug artwork is excluded from release. [Release isolation run](https://github.com/EdenP-Development/GridMates-Word-Search/actions/runs/45489606038)

Woodland resumes from [draft #198](https://github.com/EdenP-Development/GridMates-Word-Search/pull/198) on its own v120 test candidate. The draft stack is open for review. The public itch.io beta remains at pre-release planning.
