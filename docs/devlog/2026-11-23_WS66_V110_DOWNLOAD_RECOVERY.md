# November 23, 2026 — WS66 chapter preview download recovery

The owner could not download the earlier v109 handoff ZIP. The original branch, bundle, and APK were unavailable. The replacement starts from the reviewed WS66-B source `598b7fca17a52be6eec5894476c3cb4786514e29`. Its four checks passed and the v108 signed APK is the fallback. [WS66-B draft](https://github.com/PlayerEden/GridMates-Word-Search/pull/186) · [Signed v108 APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45065450259/artifacts/14771725725)

## Replacement build

- The signed debug candidate uses versionCode **110**. It adds three Relaxed pages in Our Solar System and three Expert pages in Quiet Clearings, using the existing cover art for their themes.
- The 47 and 65 word pools revive the same two chapter concepts with fresh curation from the existing WS66-A test fixture and internal English and Fall QA inventory. All rows carry source provenance. The [pool TSV](../../core/src/test/resources/ws66-preview/chapter-pools.tsv) is pinned to SHA-256 `314b07e3e4a36f81ec3bf27f013d3b01d995ba94b7da47efea6613691b8b3c11`. Public editorial decisions remain open.
- The build runs 32 V5 trials per chapter, selects three chronological boards per chapter with low adjacent overlap, and packages six exact binary snapshots. Android reads the snapshots and board hashes directly. The QA variant uses new volume and page IDs.
- v110 copies known compatible v108/WS57 book progress and partial sessions into new journals. Each older journal is preserved. The earlier v109 run used a separate, unavailable catalog, so this replacement carries forward the 81 v108 pages and establishes its own six preview pages. The editorial books keep QA-specific progress outside permanent Solo rewards.

## Verification and delivery

Source is tracked on `feature/ws66-chapter-preview-v110-2026-11-23` as a draft off WS66-B. The branch-specific Android workflow checks the signer, debug/release packaging, a real signed v108 to v110 install-over, and native swipe, hint, and session reopen on both book sizes. The draft description records the exact build and test results. Use the signed v110 QA APK to test the chapters once the checks pass. Physical launch timing and two-phone Nearby remain open.
