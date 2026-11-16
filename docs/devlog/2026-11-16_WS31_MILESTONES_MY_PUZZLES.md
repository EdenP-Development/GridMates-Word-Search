# WS31 — Milestone Shields and Illustrated My Puzzles

**Date:** November 16, 2026
**Base:** WS30 v92 QA (draft PR #137) source `a8021b42badd1c4c38db609e6777173793f4b13a`.
**Status:** actual source committed in isolated stacked branch; both GitHub pipelines and real-device owner acceptance are separate gates.

## Why this pass
The real-device WS29 achievements showed clear cream/gold shield design but emoji-colored locked sun. WS30 fixed that with native vector glyphs. The user asked to continue making meaningful progress before another manual test; better to combine an existing milestone medal tier with a saved puzzle journal visual pass rather than make many one-line APK iterations.

Physical phone screenshots show Home, Collection portraits, illustrated Profile, walnut Library and working puzzles visually cohesive; **My Puzzles** is a working but relatively plain paper utility screen. Preserve functional behavior, especially automatic save, 24 paused slots and deletion confirmation.

## Existing-source milestone expansion (no achievement inflation)
- Reuse existing WS21 editable SVG master `gm_core_ws21_achievement_slot_forest.svg` in `render_ws29_achievement_medals.py`, verifying SHA-256 against the source master manifest and producing reproducible 192×192 `gm_ws29_achievement_forest.png` in **debug only**. Continue generating exact cream/gold masters and keeping production release free of unapproved candidates.
- Three **already existing** milestones—`twenty_puzzles` (Puzzle Keeper), `seven_dailies` (Seven Good Days), `all_themes` (Three Chapters)—show a live "MILESTONE" label, and their shield becomes forest green with gold detail upon earning. Unlocked everyday achievements use gold; locked continue to use cream/muted artwork. The four native monochrome WS30 motifs remain intact.
- Original **eight** achievement definitions, IDs, descriptions, target thresholds, progress math, page navigation and save data are unchanged.

## My Puzzles illustrated polish (native Compose)
- `GridMatesShell.kt` `SavedSoloScreen` keeps the same schema-5 SoloStore profile, active session, paused list, `onNewSolo`, `onResume`, `store.discardPausedSolo`, and explicit destructive confirmation.
- Replace plain title/actions and `PaperPanel` cards with a modest illustrated `Ws15SectionHeading` and source-independent native `GridMatesIllustratedFrame` containers: FEATURE frame around active game, MEDIUM frames around paused games, and an intentional empty-state frame when no paused puzzles exist.
- Each illustrated frame has **one interior Column** to avoid WS28's BoxScope overlay regression. Keep actual puzzle theme, board size, found word counts, elapsed minutes and real progress bar. Use a normal 48dp+ New Solo / Resume pill and explicit Remove confirmation.
- Maintain text and action semantics so the old `MyPuzzlesNavigationAndroidTest` still locates the saved journal and can resume an earlier paused run.
- Minor explanatory copy is shorter so more of the active puzzle appears on screen.

## Tests
- `test_ws31_milestones_saved_puzzles.py` reads the existing forest master SHA, renders all three shields and validates real PNG bytes, release isolation, original achievement targets, and actual SoloStore navigation/confirmation source.
- `MyPuzzlesNavigationAndroidTest` adds checks for active/pause panels and existing resume behavior; `CollectionSettingsPolishAndroidTest` checks the milestone label on page two. These are Android instrumentation **source tests compiled** in standard CI; their actual execution on device/emulator must not be claimed without evidence.
- Both Android QA and independent artwork/release workflows call WS31 source checks.
- QA versionCode **93**, `0.9.49-milestones-solo-journal-qa`, same test package/signer, no persistence schema change.

## Explicit non-goals
Do not adjust Home, physical Cabinet/Library or pilot 'TEST BOOK', puzzle gameplay, Daily/nearby networking, word fonts, portrait unlocks, original source IDs, analytics, monetization, or release-only resources. No faux achievements or inflated asset inventory.

## Acceptance
1. GitHub Android tests/lint/build, original source art SHA, and independent debug/release isolation pass on same final commit.
2. Actual signed v93 APK shares v92 signing certificate and QA application ID; independent APK member contents match.
3. On phone: Collection → Achievements → second page includes MILESTONE on Three Chapters; normal achievements remain clear and muted when locked; earned higher-tier milestones eventually get green shields. My Puzzles uses illustrated cards, buttons work, paused list and confirmation remain usable without overlapping labels. Large-font stress test still needed.
4. Keep PR draft and do not merge into production before owner visual acceptance. v92 remains known-good fallback. Never uninstall/downgrade test app to work around UI.
