# WS41 — Mixed Quick Play, reviewed QA packs and book rewards

**Verified signed v103 QA.** Actual Android native play and 24 instrumented tests passed. Owner handset, editorial release review and two-physical-phone acceptance remain open. This is a QA build, not a public release.

## Who, why and prior checkpoint

Owner authorized the recovered roadmap, larger vocabulary, replayability and cohesive game presentation. Work by Codex in `feature/ws41-mixed-quick-play-2026-11-17`, [draft PR #152](https://github.com/PlayerEden/GridMates-Word-Search/pull/152), stacked on WS40 #151. Started at `bb90af0a2948e8b21879e6e47cbd81d4ef0e94a8`. WS40 recovered exact WS10 authoring tools and passed 26 tests; it did not change the APK. Previous playable fallback is v102, source `8ce68b0d0564ce11c30c0b18dcb208d7b9a1b6f4`, run 44192668122/artifact 14377072810, SHA256 `3468befffa01b5e6b1f098389d775519274d95557ce00eac9d8b8514e9335d37`.

## Actual implementation

- 670 distinct English QA answers / 702 many-to-many topic assignments: Woodland 159, Space 169, Garden 174, Everyday 200. Original editorial selection is explicitly `assistant-reviewed-qa-only`, with owner release approval false. It is not a human-reviewed production dictionary. Frozen JSON, exact checksummed TSV projections and source/reviewer attribution are committed. The 17 root recovery fixtures stay byte-identical. See [authoring guide](../../data/lexicon/authoring/en-v1/README.md).
- V5 pack-aware generator with Small 12×12/12 words, Medium 15×15/16 recommended, Large 20×20/24. Mixed sets balance the four groups, exclude palindrome/substring target conflicts per puzzle, use bounded generation and verify one physical occurrence per target. Original V1/V2/V3 and Daily recipes/IDs are unchanged; V4 lab stays separate.
- Home→Quick Play→size starts immediately. Completion offers same-size Play Another and optional Change Size. Existing Custom settings and the 24-slot pause journal remain separate. Legacy themed Custom and Nearby keep their V2/V3 content; this pass activates the new packs for Quick Play and QA books only.
- Schema 6 saves complete checksummed V5 boards, targets, display labels and paths. Old schemas 1–5 remain supported. Pack updates cannot change a started board. Full backup/restore carries snapshots, paused games and the book reward ledger.
- Each existing QA volume appends six three-page chapters. Original nine page IDs/recipes remain; three books now have 21 pages/8 chapters each, 63 pages total. No fake future-theme books. QA IDs remain reserved and cannot become release IDs.
- Completed book pages grant general profile/portrait/font/achievement progress once, including historical completed pages on return Home. No elapsed time, hints, Solo-only achievements or records are invented. Practice/replay cannot duplicate rewards.
- Mixed games identify as Quick Play on Home, saves, records and sharing. Display labels retain word spaces. Shared warm paper/emerald/gold controls and all 228 art assets are reused. Home now has a GridMates/actions row and a full-width game-title row.

## Exact build and package receipt

| Item | Verified value |
| --- | --- |
| Compiled/tested source | `01eb02e4e5460feaab131236e4da7b9d8eb5c2e1` |
| Standard Android QA | [44236302499](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44236302499) SUCCESS |
| Independent debug/release QA | [44236302498](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44236302498) SUCCESS |
| Signed v103 artifact | [14397980688](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44236302499/artifacts/14397980688) |
| Independent artifact | [14397766228](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44236302498/artifacts/14397766228) |
| Native/device test evidence | [14398235699](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44236302499/artifacts/14398235699) |
| APK size | 34,191,002 bytes |
| APK SHA256 | `19949e41b1ed9165781c7a39b89857c9996bd5e27cfe25227dc5afabcfe25359` |
| Package/version | `com.playereden.gridmates.wordsearch.qa` / 103 / `0.9.59-ws41-qa` |
| QA signer SHA256 | `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419` |
| Reproducibility | All 372 extracted entries match independent APK |
| Artwork | All 228 assets match v102 byte-for-byte |

Downloaded artifact ZIP digests/CRC, actual APK manifest and signing certificate were checked. Both workflows run apksigner validation against the pinned QA certificate. All five new QA pack IDs/data projections are absent from release DEX. Later receipt-only commits do not change the tested APK.

## Executed verification

- Authoring/recovery QA passed 26 tests and all exact root source hashes. Final recovery workflow [44236302642](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44236302642) SUCCESS.
- New core preflight [44233330672](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44233330672) passed 24 tests across V5 packs, saved-board checks, V2/V3 goldens and book compatibility. Artifact 14395499130 SHA256 `4050860cd80661df132b4db9d864eb99245b7ca43dfa2df8c9fb958b775706fe`. The final header correction does not change core/data packs; both final full builds also execute core tests. New V5 literal fixture hashes are pinned, never recomputed as expected values.
- Across 100 consecutive Medium Classic boards: 608 distinct words, adjacent mean overlap 0.364, 100/100 boards with targets in every quadrant, six Classic directions, mean length 6.243, 345 short (≤4) and 224 long (≥9) targets, mean 11.62 subtopics. Classic crossings mean 0.02/max 1; Expert prefers crossings. These are measured QA distributions, not a claim that every content decision is final.
- Mixed generator tests cover all three sizes/challenges and five seeds, reverse claims, exact snapshot round trips and corruption rejection. All 63 actual QA book pages validate; original nine V2 IDs/hashes are checked.
- Actual API35 native review installed v103 over v102 with prior save bytes unchanged, opened Medium through Home, traced all 16 words using Android input, restarted/resumed after the first word with the exact frozen board preserved, completed, played another Medium and reached backup settings. Clean native screenshots are in the evidence artifact.
- **24 Android tests passed in 18.894 seconds.** Includes real 15×15 V5 and 20×20 V2 finger traces, chooser at 200% text, all sizes, retained Custom settings, 24 paused games/full-library protection, legacy migrations, frozen corruption protection, full backup/book awards/font restoration, interrupted restores at three checkpoints and actual IO failure recovery. Header layout checks at 284/324/360dp and 100/200% text verify the full name and both 48dp actions remain usable.

## Failures and corrections

Initial preflight source `e8038de798b8ad8b1d2f3910b37d9096f8ea523f` passed; new V5 fingerprints were then frozen. Intermediate `d71b6117d88dde4816acf83b0a064075fd152994` builds were superseded/cancelled while finishing checks. Source `539543d125945e83698627fa63f6f66bf8ad5fb0` passed both builds and all 23 initial Android tests (run 44233338654; native evidence 14395939693), but screenshots exposed an ellipsized Home title. First correction `898629afca29fe43651bd32686cb1450b2a2a59d` still failed the new overflow check at 360dp/100%: run 44234679091, 23/24 passing, evidence 14397407112. Final source gives the game title a full-width row with bounded weighted text; all 24 checks and final screenshot review pass. No defective intermediate APK was delivered to the owner.

## Open gates, fallback and next exact task

Owner physical review of v103 and two-phone LAN gestures/claims/reconnect/Race/Duel remain unverified. Existing issue #53 is an old observation, not proof of a current failure/pass. Human vocabulary/editorial and production art/content/signing promotion remain release gates. Larger content, future languages, new themed Custom/LAN packs and store publishing are not claimed complete. No draft stack was merged, user data reset, production release published or Sudoku source modified.

v103 is now the current automated/native-tested build; retain v102 above as the previous tested fallback. v100 remains the prior owner-reviewed visual checkpoint until owner confirmation.

**Next exact content task:** curate the next 100–250-word batch for the Fall family using stable canonical IDs and many-to-many topics, before making it a playable book. Continue toward 10,000 distinct approved words / 30-plus topics. Use the current roadmap for separate Custom/LAN versioning and physical acceptance work.
