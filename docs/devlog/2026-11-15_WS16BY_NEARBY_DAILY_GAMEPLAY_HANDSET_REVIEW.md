# WS16BY — Nearby, Daily and actual puzzle phone screenshots: design/behavior audit

**Date:** November 15, 2026. **Input:** Eight owner-supplied Android screenshots in the Game Design chat, showing Nearby Play Co-op/Race/Duel mode selection, a no-local-Wi-Fi host warning, a scrolled Nearby footer, the Daily Word Search landing, an 8×8 Daily puzzle, and a 15×15 Solo puzzle. These screenshots are not uploaded to the GitHub repository. **Review only:** no runtime/source change, new APK, end-to-end test or owner signoff. Current verified APK remains signed v82 / WS16BX, source `46150195801981a49b8cb6d26aa6eb14af4e32f7` and draft PR #131.

## Observed positives
- Nearby Co-op, Race and Duel selectors update their descriptions and Host labels coherently. Prominent Join and Host actions, 15×15 room-options summary, and optional board appearance keep the primary flow easy to discover.
- Daily landing clearly shows today as **2026-11-15**, today's Woodland six-word challenge, an obvious Play today action, and a comprehensible Daily Journal empty state. The date is a screenshot observation, not proof that device-day rollover persists correctly.
- Daily 8×8 grid is legible at the photographed phone size. Solo 15×15 grid fits horizontally, with visible zoom/fit and precise selection affordances. The in-progress screens shown have **0/6** and **0/16** words found; they do not prove selection, saved history, hints or completions.
- No confirmed in-screen elements physically overlapping one another in the photographed views. Text cut at the bottom of a screenshot may be ordinary scroll clipping; the Nearby container uses a vertical scroll and more text exists below the fold.

## Ranked issues and grounded source audit

### P1: High-friction word index for 15×15 Solo
The actual screenshot displays **two word targets on Page 1 of 8**, despite 16 targets. `WordSearchScreen.kt` `WordList` at approximately lines 591–613 calculates `rowsPerPage` from `maxHeight - reservedControls` and `rowBudget`, then `perPage = columns * rowsPerPage`. On a tall 15×15 board, the fixed board viewport leaves only one two-column word row beneath it. Paging eight times is excessive for a relaxing puzzle aimed at older and less-tech users. The existing Book mode already calls `WordList(scrollAll = true)` and scrolls its index independently: investigate carefully reusing a scrollable word index for Solo/Daily, or a small expandable word drawer, **without losing easy access to the board or making board-pinch gestures scroll the page**. Keep word order, checkmarks, Remaining filter, list semantics, and 200% text reachability. At daily 8×8, 4 of 6 targets appear on Page 1 of 2 and may be acceptable; don't automatically impose an oversized list there. UX regression tests must prove all 16 words can be reached without eight page changes.

### P1/P2: No-Wi-Fi host-state clarity, not a proven network failure
Screenshot reports `Connect to a local Wi-Fi network to host a game.` above the same green `Host Shared Board Race` action. Source `NearbyCoopScreen.kt` Host button is currently `enabled = !state.busy`, regardless of the error; `NearbyCoopRuntime.host()` properly checks `localWifiIp()` and catches an IOException with the exact message. This shows graceful error handling, but leaves a visually enabled primary action likely to repeat the same error when Wi-Fi is still absent. Consider readable network prerequisite/retry affordance that updates when connectivity changes, and do not disable forever based on a stale error. Preserve manual-address fallback, hotspot-compatible LAN behavior, and access to Join when it actually makes sense. Test disconnected, joined trusted Wi-Fi, guest-isolation and two-phone host/join; these screenshots **do not** verify real pairing or successful gameplay.

### P2: Nearby/Daily visual continuity
Nearby lobby/recovery/room-option cards and Daily Today card use ordinary `Card(...)` with the photographed **lavender-gray surface**, unlike the cream/gold framed panels already standardized on Library, Collection, Profile, Records. Source: `NearbyCoopScreen.kt` and `DailyLandingScreen.kt`. Reuse the shared `GridMatesInfoPanel` or `GridMatesIllustratedFrame(PANEL)` only where a framed panel improves orientation, especially **Host a nearby game** and **Today's puzzle**. Keep real text/buttons dynamic, keep the center uncluttered, no new artwork necessary. Avoid adding heavy gold frames around every gameplay element; puzzle grids should remain high-contrast and practical.
- Nearby mode descriptions vary from one short line (Co-op/Race) to two lines (Duel), naturally shifting buttons; minor visual stability opportunity only, not an overlap failure.
- The room-options and appearance controls are TextButtons, not just informational labels, as confirmed by code, but may look more like tappable disclosure rows if desired.
- The Daily screen's simpler empty state is understandable; focus on a coherent Today card rather than filling blank space with decoration.

### P2: In-puzzle controls and typography
The screenshots' `Trace words · pinch to` guidance wraps beside `Select / − / Fit / +`, making the toolbar visually busy. The 15×15 board font is small when fitted; the zoom/precise picker is valuable and must remain. Simplify instruction/toolbar hierarchy or use short accessible icon/copy, with 48dp targets and font-scale QA, without disturbing swipe/drag selection coordinates. In particular, don't replace functional word grid with an ornamental art asset; apply theme with lightweight headers, paper padding, and meaningful feedback.

## Next execution recommendation
1. WS16BZ candidate: optimize **Solo 15×15 word-list access** first, with targeted actual-height tests and preserve 8×8 behavior and gesture isolation. Optionally combine clear no-Wi-Fi state guidance if small and independently testable; avoid sweeping gameplay/nearby rewrites.
2. Follow with one focused shared-panel style pass for Nearby host and Daily Today if needed; don't extend the very large PR #131 merely for decorative flourishes without measurable UX benefit.
3. Gate with both Android and independent art QA; test one phone 8×8/15×15, landscape, text at 1.0/1.3/2.0 and 320dp; then two-phone nearby Co-op/Race/Duel and saved progress/history. Keep PR #131 DRAFT. No new APK is claimed by this review.

**User approval remains open:** These images alone cannot verify tapping, word selection, Hint behavior, network discovery, daily rollover, local save/relaunch or completed results.
