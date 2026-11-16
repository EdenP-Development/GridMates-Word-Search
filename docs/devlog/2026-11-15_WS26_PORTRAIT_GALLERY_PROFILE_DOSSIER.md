# WS26 — Real-device screenshot response: collectible gallery and player dossier

**Date:** 2026-11-15 local / 2026-11-16 UTC  
**Status:** source committed in draft PR branch; CI and owner phone review required before visual acceptance.  
**Base:** verified WS25 v87 draft source `d0090bee3787f7444eb2717789dfdce91c34a1db`, PR #132.  
**Previous verified APK:** WS25 v87 from [Android QA #43802827311](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43802827311), 82/82 steps; [Art/release #43802827292](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43802827292), 83/83; same stable QA signer as v86. Keep v87 as safe fallback.

## Actual owner screenshots received

Seven real-phone v87 screenshots: Home book carousel, one Woodland book puzzle with a found word, illustrated Library, Profile, Collection top, Collection page 4, and Fonts. Evidence supports on-phone display and navigation into those states, **not full save/restart/completion, two-phone LAN testing, or release approval**.

Visual assessment:
- Home book, parchment and buttons cohesive. Live book progress correctly displays an empty bar at 0/3, but empty track is faint.
- Gameplay has crisp, full-size word grid and complete twelve-word index; deliberately avoid ornate art inside the letter area.
- Walnut Library is the strongest distinct visual identity; freeze geometry and paging.
- Collection's illustrated tabs and real WS22 Robin QA art are rendering, but portrait rewards previously used one full-width row each even on ordinary phone widths.
- Profile summary is functional but visually sparse, despite meaningful local statistics already in profile.
- Fonts gallery is dense, but retains actual previews and licensing; defer aggressive typography changes pending phone evidence.

## Code changes — WS26 v88 candidate

- `CollectionScreen.kt`: when actual **content width >=320dp** and `fontScale <1.3`, show 2 portrait columns × 4 unlockables per page using a new `PortraitTile` with original portrait art, live unlock requirement and fraction, clear equipped/locked states and minimum 48dp Equip action. At narrower widths or large text, retain full-width original `PortraitCard` with 3 portraits per page. This follows the measurement-driven layout already used for Fonts. No sprites/tokens/offline data added.
- `Ws16CProfileHub.kt`: richer dossier information for companions collected and achievements earned, with a live progress track inside the same existing frame, plus three descriptive navigation cards that reuse native `Ws15SectionHeading` symbols. All old destinations and test tags survive.
- Existing 360dp Android gallery test now checks 4 portraits on page one / 3 pages and later Badger on page two, plus font/achievement navigation.
- WS16BU source guard reflects the new >=320dp threshold; new `test_ws26_gallery_profile.py` checks real models, UI layout/fallback, live equip links and 48dp target.
- Standard Android and independent art CI workflows run WS26 source guard; QA code **88**, versionName `0.9.44-portrait-gallery-profile-qa`, same signing and local save schema.

## No changes / approval boundaries

No changes to portrait IDs, unlock thresholds, achievements, font content/licenses, user identity serialization, game modes, puzzle boards, timer, local co-op, books or Library. Original portrait PNGs unchanged. No production assets approved by this feature. Existing v87 WS22 Robin debug-only art import persists unchanged. No Word Search `main` merge or Sudoku edits.

## Verification, acceptance and rollback

- Check both independent GitHub workflows on **the final exact WS26 PR commit**, distinguish source checks from actual Android emulator and phone runtime execution.
- After an installable signed v88 APK passes, verify unchanged package `com.playereden.gridmates.wordsearch.qa`, matching SHA-256 certificate with v87, and ZIP member equality across independent build artifacts.
- On phone: Collection first page should show Owl/Fox/Rabbit/Bear in **2×2**, next page begins Badger, all 12 reachable. Equip Fox then Owl, locked portrait progress intact, font and achievement pages unaffected. Profile shows companions progress and original routes. At Android large font should fall back to readable rows.
- On failure, keep v87 installed; **do not uninstall or downgrade** to avoid damaging local saves.

## Next decision

Use actual v88 screenshots before shifting visual treatments across other screens. Candidate future passes: compact font cards, achievement medals using reviewed SVG masters, further theme motifs and collectible categories. Do not add those merely to inflate asset counts.
