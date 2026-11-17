# Word library, replayability and future themes

The owner wants a growing library of themed books, reliable selection mechanics,
and fresh word searches after the last book page. Fall, Jungle, Winter, Summer
and Castle are future themes. WS39 makes the existing Quick Play Home action
playable and closes the book-backup gap before expanding content.

## What is playable now

- Books have fixed, verifiable pages and persistent completion/bookmarks.
- Quick Play starts a fresh 12×12 Classic Solo puzzle, choosing a different theme
  from the active Solo board. Each board uses one theme. It is not a mixed pack.
- Custom offers 12, 15, 20, 25 and 30 cells per side, with independent Relaxed,
  Classic and Expert options. A fresh seed produces another layout.
- Quick Play shares normal Solo hints, pause/resume, records and unlocks.
  Started unfinished games are kept in My Puzzles. Its 24-game capacity is
  explicit and starting another game never silently discards one.
- The same saved version, theme, options and seed regenerate the same board.
  Daily and old Solo saves retain their original generators.

Random layouts provide continued play. Vocabulary remains finite and words will
repeat, especially on the smaller-board packs. This is the main long-term
content limitation, rather than a shortage of puzzle layouts.

## Current English vocabulary audit

Counts are unique entries in the existing source packs, not combined across
versions. A word shared by two themes is counted within each theme.

| Theme | Daily legacy V1 | Small/medium boards V2 | Large boards V3 |
| --- | ---: | ---: | ---: |
| Woodland | 14 | 36 | 81 |
| Space | 14 | 36 | 81 |
| Garden | 14 | 36 | 79 |

V2 entries are 3–9 letters in these packs, V3 entries are 5–14 letters. All are
uppercase A–Z tokens with no exact duplicate within a pack. Pack tiers are
editorial familiarity ratings. They are not measured reading-level ratings.
Compound words such as MILKYWAY currently appear without spaces. SPACE V3 has
both SOLSTICE and SOLSTICES. Substring exclusions avoid selecting such pairs on
one large board, but future packs should avoid using plural variants to pad
their vocabulary count.

The new `ReplayabilityAuditTest` independently counts every placed target across
all eight directions over 315 boards spanning three themes, five sizes, three
challenges and seven seeds, including zero and large positive seeds. It checks
reverse selection, duplicate claims, invalid selections, forward-only Relaxed
placement, and all-target completion. A separate 256-seed vocabulary sweep checks
that every V2/V3 entry is reachable, plus 16 distinct short layouts per theme.
Both audit tests passed in the WS39 Android QA build. The
[build receipt](devlog/2026-11-17_WS39_REPLAY_AND_BOOK_BACKUPS.md) links the exact
source, build, signed APK and actual Android tests.

## Next content version

Do not edit released V1/V2/V3 spelling, selection order, random calls or IDs.
They are part of existing saves. V4 is an isolated giant-board lab and is not
the content expansion path.

The next implementation should use explicit immutable pack IDs such as
`en-woodland-v1` and `en-fall-v1`, a versioned generator, and portable saves that
include that pack ID. Persist pack identity and seed before displaying a board.
Route old saves to their existing generators. Avoid using an enum's ordinal as
the identity of new content. Daily expansion needs an explicit date/content-version
boundary so historical Daily puzzles and their results remain unchanged.

Each pack should have a reviewed display name, normalized search token, stable
entry ID, familiarity tier and suitable board sizes. Keep friendly phrases in
the word list while searching their normalized letters. Validate normalization
collisions, spelling, duplicate/inflected forms, target length, sufficient words
per difficulty, and substring conflicts before packaging it. Start with at
least 150 distinct reviewed entries per new pack, including at least 60 familiar
entries that fit the short board. These are content targets for the next pass,
not counts already shipped.

A mixed Quick Play pack should reference reviewed existing entry IDs. It needs
its own immutable version and theme-aware presentation rather than combining
old enum pools at runtime and silently changing saved layouts. Every size and
challenge must pass generation, uniqueness, reverse-claim, timing, hints,
completion, restore and bounded-generation checks. Broader vocabulary and
mixed packs are the next development milestone after WS39. A later player-entered
word list needs a bounded, portable pack definition in its save and backup, so
it can reproduce the same puzzle on another phone. Custom currently means
choosing the built-in theme, size and challenge.

## Visual families for future books

| Theme | Binding family | Shared cover/spine motif |
| --- | --- | --- |
| Woodland, existing | Forest green and antique gold | Pine branch |
| Space, existing | Deep blue and gold | Ringed planet |
| Garden, existing | Olive green and gold | Flower stem |
| Fall, planned | Amber/rust and antique gold | Maple leaf |
| Jungle, planned | Deep teal/emerald and gold | Tropical leaf |
| Winter, planned | Icy blue and silver | Snowflake |
| Summer, planned | Turquoise and warm gold | Sun |
| Castle, planned | Burgundy and antique gold | Tower crest |

Use one approved binding material, motif and palette per book across the Home
cover, cabinet spine, book detail, page heading and completion reward. Covers
and spines are a matched deliverable. Maintain the shared warm paper,
Literata headings, Atkinson controls, native readable labels and green/gold
actions. New themes do not need their own navigation or gameplay code.

## WS39 backup behavior

The portable backup envelope contains the existing Solo/Daily/profile/results
JSON plus the existing checksummed book-progress and book-session blobs, and
the equipped board-font choice. It includes paused Solo, Quick Play, completed
book pages, bookmarks, and partial page words/hints/time. Device display and
sound preferences remain local preferences.

Preview validates all components, including catalog and exact page identities,
before confirmation can write any save. Unknown or unavailable books are
rejected rather than silently discarded. Legacy schema-1..5 Solo backups remain
supported and leave existing books untouched. New complete backups require a
WS39-compatible build with the corresponding books.

An atomic pending restore is replayed on startup after interruption. Play is
held until all components are saved. A durable committed marker prevents old
restore replay if cleanup is interrupted. Unreadable existing files remain
protected. Android tests cover round trips, older exports, checksum rejection,
death between file writes, storage failure, unavailable books and tombstones.
