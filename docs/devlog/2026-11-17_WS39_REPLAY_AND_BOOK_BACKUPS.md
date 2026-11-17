# 2026-11-17 — WS39 replayability and book backups

> **Quick status:** PASSED AUTOMATED. Core/app tests, lint, independent package
> verification and all ten actual Android emulator tests passed. Owner handset
> review is pending. Separate native navigation, screenshot and install-over
> checks also passed using the exact signed APK.

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
- Tested source [`8ce68b0d0564ce11c30c0b18dcb208d7b9a1b6f4`](https://github.com/PlayerEden/GridMates-Word-Search/commit/8ce68b0d0564ce11c30c0b18dcb208d7b9a1b6f4), [draft PR #150](https://github.com/PlayerEden/GridMates-Word-Search/pull/150), stacked on #149.
- [Standard Android QA run 44192668122](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44192668122) **SUCCESS**, 94 successful steps and three intentionally skipped WS33-only steps. Core/app unit tests, lint, instrumentation compilation, signed APK checks and the targeted emulator all ran.
- [Independent build/art/release-isolation run 44192668153](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44192668153) **SUCCESS**.
- [Signed artifact 14377072810](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44192668122/artifacts/14377072810): 34,141,854-byte APK, SHA-256 `3468befffa01b5e6b1f098389d775519274d95557ce00eac9d8b8514e9335d37`.
- Actual binary manifest package `com.playereden.gridmates.wordsearch.qa`, code 102, name `0.9.58-ws39-qa`. Extracted v2 signing certificate SHA-256 `75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419`, matching v101. CI separately verified signatures.
- All 372 extracted package entries agree with the independently built APK. All 228 asset entries are byte-for-byte unchanged from v101. ZIP CRC and outer artifact ZIP digests passed.
- Core audit covers 315 generated boards. Vocabulary reachability sweep covers 256 seeds per challenge/theme; 16 short layouts per theme are distinct. Frozen generator golden tests also ran.
- [Actual Android evidence 14377802333](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44192668122/artifacts/14377802333) contains `OK (10 tests)` in 27.15 seconds on API35. Six backup tests cover complete roundtrip, old Solo import, two levels of checksum rejection, interrupted writes at three checkpoints, a real IO obstruction and retry, absent catalogs and protected unreadable files. Two Quick Play store tests cover reopens, awards, archiving and 24-game capacity. The Home routing test and existing real 20×20 pointer-drag/hint persistence test also passed.
- The instrumentation screenshots were covered by Android's initial fullscreen notice and are not accepted as visual evidence. A QA-only runner now reuses the exact signed v102 APK, suppresses/dismisses that notice and captures real native Home → Quick Play → Settings navigation. It also installs v102 over v101 and checks old Solo save bytes. [Native run 44194406102](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44194406102), QA source `ef7c9f9228ea2826fd1a4afbe1373abd02111f35`, **SUCCESS**. [Native evidence artifact 14376884803](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44194406102/artifacts/14376884803) contains clean Home, generated Garden puzzle and backup Settings captures, plus the passed receipt. v102 installed over v101 and preserved the prior Solo JSON byte-for-byte. Native taps reached the square 12×12 board and both backup actions at 360dp, 100% font. No APK rebuild was performed.
- Local checks completed: clean patch whitespace, shell syntax, workflow YAML,
  direct vocabulary counts and uniqueness from source.
- Android instrumentation is configured for this feature branch only, to avoid
  charging every later PR for a full emulator startup.
- Fallback v101 remains downloadable. Do not merge the draft stack or delete
  player data. Owner physical test and actual two-phone LAN remain open.

## Outcome and follow-through

- Quick Play, complete book backup/restore and the word-library/mechanics audit are implemented and passed the checks above.
- Future themes and larger/mixed word packs are planned, not included in WS39.
- Next exact development action is implement immutable expanded English word packs and a versioned mixed Quick Play option before adding new themed books. Owner can review the v102 install-over, Quick Play and backup/restore on a handset. Future Fall/Jungle/Winter/Summer/Castle books should follow the content and matched-art contract linked above.
