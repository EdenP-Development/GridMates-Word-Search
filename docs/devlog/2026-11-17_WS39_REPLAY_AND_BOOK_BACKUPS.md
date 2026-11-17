# 2026-11-17 — WS39 replayability and book backups

> **Quick status:** BUILD PENDING. Android unit, generator audit, lint and actual
> emulator tests have not yet completed. No owner handset verification claimed.

## Who, what, where, why

- Owner approved the next progress/replay pass and requested future themes,
  mechanics/playability checks, continuing play after completing books, and
  verification of the word library.
- Work performed by Codex. Actual Android tests are executed by CI separately.
- Repository `PlayerEden/GridMates-Word-Search`, branch
  `feature/ws39-replay-and-book-backups-2026-11-17`, stacked on draft PR #149.

## Where we came from

- [Previous WS38 receipt](2026-11-17_WS38_HANDSET_VISUAL_FIXES.md).
- Starting remote head `2b97cb65f9883130c3d4814f701167db86f5236d`.
- v101 tested source `cea3c5a6e5614eb223dd2ada4421a8fb31560d2b`.
- v101 signed APK SHA-256
  `2824444742e1b93129a8eb2d711075bd697c34065303b8e469e03f087f87853e`.
- Prior standard build run 44181424897 and independent run 44181424939 passed.
- The owner's v100 screenshots remain the physical visual baseline. v101 has
  emulator evidence and remains available as the immediate source fallback.

## What changed in this pass

- Quick Play is a native clickable Home action. It starts a fresh 12×12 Classic
  board from a different existing theme and uses normal Solo saves, records,
  hints and rewards. It does not introduce a mixed-content generator.
- New whole-game backup envelope includes existing Solo/profile/Daily/results,
  equipped board font, book completions/bookmarks, and unfinished page sessions.
  Existing save schemas, codecs and generator outputs remain compatible.
- Restore validates all incoming components first. An atomic write-ahead file
  supports startup recovery across multiple save writes. A committed tombstone
  prevents completed restores being replayed over later play.
- Old Solo exports keep current books. Missing/unknown books or unreadable
  existing saves fail before a restore is staged. UTF-8 is strict and document
  input is bounded at 8 MB before allocation beyond the limit.
- New real Android tests cover full backup, legacy import, corrupt input,
  interrupted writes, storage errors, catalog mismatch, Quick Play persistence
  and pause capacity, Home routing, and existing physical pointer trace.
- New core sweep checks 315 boards and vocabulary reachability. The
  [word library and theme roadmap](../WORD_LIBRARY_AND_THEME_EXPANSION_2026-11-17.md)
  records actual counts, limits, expansion versioning, and matching art families.
- No new artwork. Pinned art, original portraits, frozen generators and LAN
  behavior remain part of the inherited verification contract.

## Build and QA receipt

- Version code 102, version name `0.9.58-ws39-qa`.
- Source SHA, PR, run IDs, artifacts and package hash are pending final build.
- Local checks completed: clean patch whitespace, shell syntax, workflow YAML,
  direct vocabulary counts and uniqueness from source.
- Android instrumentation is configured for this feature branch only, to avoid
  charging every later PR for a full emulator startup.
- Fallback v101 remains downloadable. Do not merge the draft stack or delete
  player data. Owner physical test and actual two-phone LAN remain open.

## Outcome and follow-through

- Implementation complete locally; build and actual Android evidence pending.
- Future themes and larger/mixed word packs are planned, not included in WS39.
- Next exact action is inspect CI/core sweep and ten Android tests, fix observed
  failures, verify the signed package and deliver v102 with the final receipt.
