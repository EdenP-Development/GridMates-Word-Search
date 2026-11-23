# November 23, 2026 — WS66 handoff and evidence review

## Current checkpoint

The owner asked what was ready to merge, what needed cleanup, and how the normal-chat vocabulary work held up. The review started at WS66-B `539e752e78638c774370b01d3b412189d2b47f68` and used the original trial archive and the exact stack in GitHub. All three checks for that starting head passed. [WS66-B draft #186](https://github.com/PlayerEden/GridMates-Word-Search/pull/186) · [Android](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45218939793) · [Lexicon](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45218939806) · [V5 variety](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45218940098)

| Checkpoint | Where it stands |
| --- | --- |
| Signed Android fallback | WS60 v108, code `0a546c2f99d0115eb4f9d30c00ef86598fe3b135`, APK SHA-256 `6a7d59e6fed8bd2524abd216027b3caf7445aa8afa79c7fa8f2f3600b65a3b3b`. Tracked Android and core runtime inputs match WS66-B. WS61 verified a signed v107-to-v108 upgrade and WS62 tested all three Nearby modes within one emulator. |
| Editorial candidate queue | 787 distinct internal-QA English/Fall words. Of 267 additional proposal records, 105 have term-specific source notes and 162 have authored player context. Up to 200 distinct spellings could be new. The new proposals still require editor review, rights review, and owner approval. |
| Work chapter preview | The previous session reported a signed v109 with six debug-only pages and five native Android checks. Its exact code was `6a758249bdaac5d073153a5972657d9e8209655e`, APK SHA-256 `cc803220271a23f5028b597b351c26f64d83216482a12aaa3b2e8adaf881dd7b`. The unsynced commit and handoff ZIP were unavailable in this session. Recover the original bundle based on the initial WS66-B commit before publishing v109. |

[v108 build and device receipt](2026-11-22_WS60_VERIFIED_ANDROID_QA.md) · [Signed v108 APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45065450259/artifacts/14771725725) · [WS65-G editorial crosswalk](../WS65G_PLAYER_CONTEXT_COMPLETE_AND_PAGE_RISKS_2026-11-23.md)

## Changes and checks

- The original WS66-B evidence archive passed ZIP and all six evidence-file hash checks. Reprocessing the **96 real V5 rows** reproduced the original report and summary byte-for-byte. All eight configurations had three mechanically valid pages.
- The old verifier accepted a misstated Quiet Clearings Expert pool, a reversed three-page sequence, and an answer absent from the fixture. Each test used modified copies of the original evidence. The verifier now derives eligible answers from the original fixture and checks actual target membership, the pool count, and ascending selection seeds. The same three modified artifacts now fail.
- All **78** local lexicon tests passed, including four new verifier tests. The historic recovery audit also passed. The game runtime, frozen boards, artwork, saves, and candidate contents were checked against the starting Git tree. This pass made no APK. The updated draft's [checks](https://github.com/PlayerEden/GridMates-Word-Search/pull/186/checks) carry the GitHub results.

WS66-B's generator trials exposed genuine player-facing differences. Quiet Clearings Expert shared 44 of 48 answer slots across two turns and had already reached the best possible result with 26 eligible words. Quiet Clearings Relaxed and Flower Borders Expert also reached their respective overlap floors. Those cases need deeper chapter pools before another seed study. The 96 boards answer mechanical questions. Player familiarity, consecutive-play feel, and touch ergonomics need first-hand play.

## Integration and beta order

The dry comparison from the current `main` `9043cf4c942da598b5841cc39c361c99abd9ef26` to the WS66-B starting tree covers **1,360 files**. A three-way rehearsal stopped on six conflicts, in the Android workflow, README, three navigation/design documents, and the historical font receipt. The existing draft chain remains open for review. Keep main's artifact policy and manual cleanup workflow when consolidating. The dedicated signed-upgrade and Nearby checks are keyed to historical branch names, so the integration candidate needs explicit execution of those checks.

1. Prepare one integration draft against main for the verified runtime and supporting QA. Resolve the workflow and documentation disagreements in that draft. Before merging, inspect the release packaging and run Android, save upgrade, frozen-board, and Nearby checks on its exact source.
2. Restore the v109 source from the original handoff. Compare v108 and v109 cold Home startup on the same handset under comparable conditions. The earlier software emulator took about 82 seconds to draw Home. Measure the handset before attributing a slowdown to the six new pages. Check a signed install-over with existing bookmarks and partial puzzles, real swipes and hints on the chapter screens, larger text, and Nearby using two phones. [Two-phone checklist](../WS62_TWO_PHONE_NEARBY_ACCEPTANCE_2026-11-22.md)
3. Get feedback from consecutive pages before extending the playable library. Use the existing unsigned editorial worksheet for approval and reuse decisions. Grow only the weakest chapter pools first and rerun the three-page trial on each revised pool. [Vocabulary master issue #87](https://github.com/PlayerEden/GridMates-Word-Search/issues/87)
