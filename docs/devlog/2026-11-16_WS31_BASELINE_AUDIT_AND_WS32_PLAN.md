# 2026-11-16 — WS31 baseline audit and WS32 Library milestone

Read the [Work handoff #140](https://github.com/PlayerEden/GridMates-Word-Search/issues/140), the contributor instructions, the development journal, the current source, and the style and asset pipeline references. This pass establishes the Word Search QA baseline and the work needed for the next consolidated Android candidate.

## Source and integration

- Continue from [draft PR #139](https://github.com/PlayerEden/GridMates-Word-Search/pull/139) at `77eb131ffd23fe60622bb0b76a87249d807884e9`. It includes the WS25–WS31 stack over #131 and four native engravings on the existing eight achievement definitions. The earlier pass and fallback are [WS31’s scope](2026-11-16_WS31_ACHIEVEMENT_MILESTONE_ENGRAVING.md) and [WS30/v92 #137](https://github.com/PlayerEden/GridMates-Word-Search/pull/137). The latest owner screenshots identified in the handoff are from v91.
- [Draft PR #138](https://github.com/PlayerEden/GridMates-Word-Search/pull/138) is a sibling off WS30, with different achievements styling and illustrated My Puzzles cards. It also built versionCode 93. Its standard APK is 30,725,451 bytes. The Library item named `GridMates_WordSearch_WS31_v93_QA.apk` has that size, while both #139 APKs are 30,711,505 bytes. Identify #139 by its pinned GitHub artifact and hash. Reserve a fresh version code above both builds for WS32.
- [Compared `main` and #139](https://github.com/PlayerEden/GridMates-Word-Search/compare/9043cf4c942da598b5841cc39c361c99abd9ef26...77eb131ffd23fe60622bb0b76a87249d807884e9). GitHub reports 1,735 commits on the cumulative side and 89 on the `main` side. The 19 paths on the `main` side include release and content references, the asset schema, and artifact retention and cleanup. Review those changes in one release integration draft, preserving the approved source and main-side policies. The audit uses #139 as its base. [Draft audit PR #141](https://github.com/PlayerEden/GridMates-Word-Search/pull/141)

## Exact archived APK evidence

| Build | Check | Download |
| --- | --- | --- |
| #139 Android QA | 88 successful workflow steps, including JVM unit tests, debug lint, Android instrumentation compilation, and a signed debug APK | [Android QA run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43963840406) · [v93 QA ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43963840406/artifacts/14274644351) |
| #139 independent art and release-isolation QA | 89 successful workflow steps, including debug and release assembly and candidate-art isolation checks | [Art QA run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43963840303) · [art QA ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43963840303/artifacts/14274144473) |
| v92 fallback | Same QA signer and versionCode 92 | [v92 QA ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43938489643/artifacts/14262602218) |

[Independent archive verification](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43971293408) downloaded and checked all three archive SHA-256 values. It confirmed `com.playereden.gridmates.wordsearch.qa`, v92 to v93, minSdk 26, targetSdk 35, and signer SHA-256 `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419`. The two #139 APKs have **363 matching internal ZIP member payloads**. The signed APK containers have these distinct digests.

| #139 APK | SHA-256 |
| --- | --- |
| Android QA | `714ea448ee729140aa1cf689b11718bbed19ed8af4431a3b9b34c97e691faf41` |
| Independent art QA | `cb40fd545c3b745a997f45230c8d3746ca38a76396c0522457baeeb319b9f8e5` |

[Android 15 emulator upgrade](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43984750517) passed using QA harness commit `3324f7ae30ac669ae7526f9a792e2b67fdbbf4e9`. Driving v92 through the Home menu produced an active Solo puzzle, one paused Solo puzzle, and today's Daily entry. The exact archived #139 v93 APK installed over the v92 copy. Android kept its original install timestamp and the schema-5 save matched byte for byte before and after opening My Puzzles on v93. The seeded save SHA-256 was `414cac1e5b3c91fc5f07ae715b18c04e9984c53740fb689cbdcd5a6089edefcd`.

Earlier attempts stopped at Android’s first-run fullscreen tip, a cold-launch timeout, and a compact Home selector that assumed My Puzzles was visible. The passing run dismissed the system tip, waited for visible controls, and used the Home menu. It covers that emulator's Solo and Daily save and journal reopen. QA book sidecars, alternate #138 updates, and physical devices still need their own test.

This pass uses the archived APKs. The original Android workflow compiles its instrumentation suite and verifies packaging. Physical handset results and two-phone Co-op, Race, and Duel acceptance are still open.

## V1 findings

| Priority | Area | Evidence and next action |
| --- | --- | --- |
| 1 | Playable Library in a public build | [Release BookQaPilot](../../app/src/release/java/com/playereden/gridmates/wordsearch/BookQaPilot.kt) disables the current catalog. [The QA Library runtime](../../app/src/main/java/com/playereden/gridmates/wordsearch/QaBookshelfRuntime.kt) uses pilot fixtures and dedicated save files. Review the first real catalog and the corresponding cover/spine art before enabling a production Library. |
| 2 | Save continuity | My Puzzles already preserves an active Solo puzzle and up to 24 paused ones. The illustrated cards and UI assertions in #138 can be ported separately from its medals. [Settings export](../../app/src/main/java/com/playereden/gridmates/wordsearch/SoloSettingsScreen.kt) covers the Solo profile and its local histories. [Book progress and page sessions](../../app/src/main/java/com/playereden/gridmates/wordsearch/QaBookshelfRuntime.kt) have their own files and are excluded from that export. Cover book reopen, restart, one-time completion, and portable progress before the release candidate. |
| 3 | Device and nearby coverage | Run the current flows at routine and large text sizes, including 320dp/200%, finger tracing, the accessible word picker, and 20×20 and larger boards already supported by the app. Run two matching physical phones through host, join, claim, disconnect, reconnect, and complete for each supported Nearby mode before public two-phone acceptance. The reported handset word-marking issue remains open. [Issue #53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53) |
| 4 | Public Android package | [Gradle](../../app/build.gradle.kts) configures a public test-only signer for `.qa`. Establish an owner-controlled production signing and update path, check release content, artwork provenance/permission, accessibility, and distribution setup before the approved beta. |
| Later | Quick Play | [The Home preview](../../app/src/main/java/com/playereden/gridmates/wordsearch/Ws16BHomeActions.kt) is explicitly QA-only and has no action. Defer mixed boards until the main play and save loops are solid. |

## Asset inventory

- [Master inventory](../../design/master-asset-system/MASTER_ASSET_INVENTORY.json) records 1,239 planned slots, 843 reserve slots, and 60 candidates. The 2,142 slots support reuse planning across GridMates.
- [WS22 assets](../../design/art-library-v2/ws22/README.md) contain 16 transparent Woodland, Ocean, Garden, and Space motifs and 24 editable prestige frames. The seven source shards rebuild the original atlas. [Binary source QA](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43795880694) passed.
- [WS21 source](../../design/art-library-v2/ws21/manifest.json) has 120 editable SVG templates and a manifest for 136 candidate renders. The full raster archive is `GridMates_Asset_Expansion_WS21_FINAL.zip`. Earlier WS17–WS20 also have candidate archives and editable source or manifests. Production selection requires documented source rights and owner review.
- [Runtime art map](../../design/visual-system/ws16br-runtime-asset-crosswalk.json) connects the painted Home and walnut Library, book geometry, motifs, and shared UI to their source paths. Keep the 12 stable Woodland portrait IDs and the native labels and puzzle letters. Current WS21 cream/gold medals render into debug. Review per-asset provenance and actual game-sized art for any production selection.

## WS32 — Library and save continuity

1. Base the implementation on #139 and port the My Puzzles framing and relevant regression assertions from #138. Preserve the paused-game limit, active/resume/delete behavior, confirmation, all eight achievement rules, and #139's native medal treatment.
2. Exercise real Solo, Daily and book paths on a disposable emulator. Confirm a saved board reopens after process restart, a book page survives pause/restart and awards completion exactly once, and views remain usable at 320dp with 200% text. Reuse the existing Solo and book store regressions and record the executed tests.
3. Design a versioned portable backup that covers compatible book progress and interrupted pages. Keep existing on-device formats and schema-1–5 import behavior. Validate the whole snapshot and catalog compatibility before writing. Test failed or interrupted imports explicitly before enabling the feature.
4. Produce one signed, source-pinned QA candidate after the above checks. Exercise install-over from both v93 variants with their saved data. Record the prior APK, first-install timestamps, result and rollback evidence. Use a separate QA export for recovery and retain the installed app data during upgrades.

The next implementation starts with the isolated My Puzzles port and executable save/reopen tests. Production catalog and art decisions follow inspection of that play loop. The public beta needs the owner's approval.
