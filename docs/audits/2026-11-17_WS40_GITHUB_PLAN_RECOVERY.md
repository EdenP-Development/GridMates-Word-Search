# WS40 — GitHub plan recovery and applicability audit

## Main finding

The WS10 word-library initiative was on a separate branch forked from `main`,
outside the cumulative gameplay/art stack. Its schema, source importer,
validation and review-queue tools were missing from v102's source tree.
Recovering that work is more useful than building a second parallel lexicon
system. The long-term goal was already 10,000 curated distinct playable words
over 30-plus themes. That goal is still relevant to the owner's latest request.

## Scope and evidence

Inspected the live repository, all 179 returned branch heads, all 104 open draft
PRs across two pages, 46 issue search records, and the default branch tree. The
current PR base chain links 87 PRs; 17 PRs sit outside that chain. Each of those
17 was checked for changed paths present/missing in the current tree. This is
a branch/path inventory, not proof that every off-chain patch should merge.

Starting current head is `3eb481424d622c6c067326393934ffac6060ef14`,
[PR #150](https://github.com/PlayerEden/GridMates-Word-Search/pull/150).
`main` is `9043cf4c942da598b5841cc39c361c99abd9ef26`; it contains historical
planning/CI files, not the current Android application. See the
[machine-readable inventory](ws40-github-inventory.json).

Read/reconciled the master design, development roadmap, current handoff and
journal/WS39 receipt, WS13 book product roadmap, WS14 full-app visual plan,
WS16AD forward/factory plans, WS16B Quick Play brief, WS10 roadmap and B2 receipt,
and relevant open issues. Current source checks covered generators, Quick Play
routing, catalog/session stores, book completion and profile unlock accounting,
native branding, Nearby and remaining future/placeholder references.

## Recovered branch

| Source | What was there | Current action |
| --- | --- | --- |
| [PR #86](https://github.com/PlayerEden/GridMates-Word-Search/pull/86) | Canonical words and many-to-many topic assignments, 33 proposed topics, source ledger, validator | Recover exact original data/tools/docs |
| [PR #88](https://github.com/PlayerEden/GridMates-Word-Search/pull/88) | Pinned ESDB/SCOWL intake and exact notice, reproducible 36,213 spelling-root quarantine | Recover importer, input pin and notice; retain unreviewed status |
| [PR #89](https://github.com/PlayerEden/GridMates-Word-Search/pull/89) | 563 research rows, 532 themed review rows, 31 holds, source crosswalk and tests | Recover research inputs/queue generator; reproduce under current CI |

Selected source commit is `fb023af6c31323961760439ff1f62b0226ec57a2`.
All **17 recovered files** match original Git blob hashes and byte counts.
[Recovery manifest](../../data/lexicon/recovery-source-v1.json) records each.
The three old branch-specific workflows were replaced by one current workflow;
old application files, Gradle settings and historical README snapshots were
not merged from that branch.

The original authoring catalog has **40 draft canonical words, 76 draft topic
assignments, 33 proposed topics and zero approved playable words**. These counts
are distinct from the existing playable V1/V2/V3 Kotlin word pools.
The 532 review rows are not 532 approved unique words. Dictionary spelling-root
matches do not approve display, theme or familiarity.

## What still applies / what moved on

### References also recovered from `main`

Seven research/reference documents were present only on default `main`, absent
from the active source tree. Restored exact bytes and recorded original blob
hashes in [the research recovery ledger](ws40-main-research-recovery.json):
the [genre design study](../WORD_SEARCH_GENRE_MASTER_RESEARCH_2026-11-08.md),
[word-library inventory](../WORD_LIBRARY_STATUS_2026-11-08.md),
[competitor UX study](../COMPETITOR_WORD_SEARCH_UX_RESEARCH_2026-11-06.md),
[Spanish plan](../MULTILINGUAL_SPANISH_RESEARCH_AND_DEV_PLAN.md),
[parity-first plan](../PARITY_FIRST_QUALITY_PLAN.md),
[font intake research](../FONT_LIBRARY_LICENSE_RESEARCH_2026-11-06.md), and
[artifact retention note](../CI_ACTIONS_ARTIFACT_RETENTION.md).

These are dated historical research, not fresh verification of external claims,
licenses, policies or artifact lifetimes. Their "current" version numbers and
statuses do not override this roadmap. No cleanup workflow, font candidates or
obsolete asset schema was enabled. Use the active build's actual retention and
save a milestone before its artifact expires.

The still-useful gameplay work is measuring word-set overlap over repeated
boards, subtopic/length variety, direction distribution, crossings and target
coverage across regions. It complements the existing solvability/claim tests.
Familiarity remains editorial rather than a measured corpus score. Keep the
global lexicon separate from per-puzzle substring/palindrome compatibility.

| Plan or issue | Assessment against current code | Disposition |
| --- | --- | --- |
| [#18](https://github.com/PlayerEden/GridMates-Word-Search/issues/18), [#87](https://github.com/PlayerEden/GridMates-Word-Search/issues/87), WS10 | Bulk content and canonical IDs were real prior work but absent from the cumulative tree | Recover now, then editorial batches and a new content engine |
| WS16B/WS17 mixed Quick Play | v102 starts a single-theme short board. The old brief also specifies three sizes, general vocabulary, mixed themes and same-size Play Another | Retain as the next gameplay/content milestone; partial, not fully complete |
| [#90](https://github.com/PlayerEden/GridMates-Word-Search/issues/90), [#120](https://github.com/PlayerEden/GridMates-Word-Search/issues/120) | Book flow exists, but only three QA volumes/nine test pages | Grow the actual publishable catalog after reviewed packs |
| WS11C / book backup | Original manual integration gap was closed in WS39 | Complete in current QA; retain migration/recovery regression |
| Book completion and rewards | `BookPageGameplayScreen`/session finalization write the book journal. `WoodlandCollection.completedCount` uses Solo + Daily, not book pages | Release work remains to define the reward bridge and idempotency |
| [#53](https://github.com/PlayerEden/GridMates-Word-Search/issues/53) | Open, no later issue comments. It records a Nov 7 two-phone failure. Later source includes input/UI fixes, but current physical acceptance is not recorded | Keep as an actual hardware acceptance gate, without claiming v102 reproduces the old defect |
| [#17](https://github.com/PlayerEden/GridMates-Word-Search/issues/17), parity-first | Much save/input/UI automation is now complete; physical LAN and broader beta checks remain | Retain core quality priority; don't equate open checkboxes with absent code |
| [#19](https://github.com/PlayerEden/GridMates-Word-Search/issues/19), Spanish | Detailed future plan exists. Current generator is English A–Z | Preserve locale seams and defer language implementation |
| [#16](https://github.com/PlayerEden/GridMates-Word-Search/issues/16), Ocean/Bugs | Coherent sample art request exists; parity plan paused broader collection expansion | Preserve as later content, not a required current reskin |
| WS09F giant-board lab | Current core has isolated V4 placement, tier and quality research | Preserve lab; 40/50/75/100 are not ordinary book requirements |
| WS13 cover-first shelf / open-shelf concept | Later owner direction favors the packed bookcase and matched bindings | Historical alternative, not the current implementation target |
| WS16AE complete title art | Current `Ws16MBrandEmblem` explicitly combines emblems with native text | Optional branding item, not a completed full wordmark asset |
| WS12 early art studies off-chain | Several source/test/docs paths were not copied, while their later rendering/visual descendants exist | Don't reimport obsolete whole art branches over current work |
| PRs #141/#143 device runners | Off-chain QA evidence workflows, not missing gameplay features | Keep linked as test history; current tests supersede their baseline claims |
| PR #138 old WS31 variation | Alternative milestone/My Puzzles pass; current #139 and WS32 supersede it | No blind merge; avoid undoing later medal/journal work |
| Gifts/stamps/store/split-lists/online ideas | Master design explicitly treats them as proposals or deferred choices | Keep visible but off the critical path |
| Old `WS01 NEXT`, `latest v47/v56/v76` banners | Contradicted by v102 source and receipts | Replace landing guidance with current roadmap; retain history collapsed |

## Work completed in WS40

- Recovered the exact missing authoring foundation and its tests.
- Added an approved-only immutable snapshot exporter. It validates the complete
  word/topic graph, requires existing approvals and active topics, carries source
  notices/review attribution, hashes canonical pack bytes and refuses a changed
  pack under an existing output ID. Row order and unrelated draft edits do not
  change a snapshot. No approval is fabricated.
- Added seven meaningful snapshot tests to the nineteen recovered tests.
- Added a consolidated CI reproduction of pinned-source intake and the three
  review queues. [CI 44198852876](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44198852876) passed all 26 tests, exact recovery hashes and the 36,213/532/31 reproduction, including rejection of unreviewed export. [Review artifact](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44198852876/artifacts/14380421759).
- Established [one current roadmap](../CURRENT_PRODUCT_ROADMAP.md) and redirected
  contributor/landing documents to it while keeping historical checkpoints.
- Kept app code, shipped words, saved generator outputs, assets and v102 intact.
  No new APK is needed for an authoring/docs-only pass.

## Follow-through

Keep the root recovery fixture immutable. Create a new versioned authoring
workspace using its four schema/data files and stable IDs. The next deliverable
is the first genuinely reviewed content batch and frozen
pack files, followed by the pack-aware generator/save path and full mixed Quick
Play. The book catalog and rewards need another deliberate pass after that.
Two-phone acceptance can proceed alongside content work when hardware is
available. Existing issue/PR states were not bulk-closed or merged during this
audit. The [WS40 pass receipt](../devlog/2026-11-17_WS40_PLAN_RECOVERY_AND_LEXICON.md)
tracks source, CI, fallback and the next exact task.
