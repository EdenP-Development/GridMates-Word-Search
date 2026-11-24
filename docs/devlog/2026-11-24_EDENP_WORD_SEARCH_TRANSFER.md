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

Fresh Android QA and release isolation are pending on the transferred draft. The next development step is finishing the Woodland album from WS77 after the baseline passes.
