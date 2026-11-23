# WS65-A — Specialist chapter candidate intake (draft; no approvals)

**Source:** curated editorial proposals based on project needs; no external word list copied. External links below are context references, not bulk-content license grants or word-by-word independent verification. No existing authoring editions, frozen pages, saves, or Android builds changed.

## Contents and honest scope

- **145** hand-selected candidate records, one answer per row; all **UNREVIEWED_CANDIDATE** and owner/editor decisions BLANK.
- Source baseline: WS64 branch `feature/ws64-woodland-proposal-reconciliation-2026-11-22`. Reuse identities from English/Fall catalogs wherever the answer already exists. If a prior unapproved Woodland proposal has the same answer, that historical origin is flagged too.
- Novel vs English/Fall **means candidate-new, not verified, approved, suitable for release, or playable**. The reference pages provide concept context; an editor still needs to verify named-moon, flower, regional, pronunciation/familiarity and ambiguous meanings.
- Machine flags: palindrome, 12×12 length incompatibility, non-familiar tier, prior Woodland reuse, ambiguous meaning and specialty chapter fit. The flags are review triggers, not global bans.

| Chapter | Proposed rows | Existing English/Fall identity reuse | Not in English/Fall | Prior Woodland draft overlap | Relaxed 12×12 suggested (not played) |
| --- | ---: | ---: | ---: | ---: | ---: |
| Our Solar System | 72 | 15 | 57 | 0 | 17 |
| Flower Borders | 73 | 24 | 49 | 4 | 27 |

**DO NOT add** the candidate counts to the 787 English/Fall QA unique answers or the 10K approved counter. An existing QA answer can be a new proposed chapter assignment; it is not a new canonical word.

## Human editorial gates before WS65-B or WS66

1. Verify each `why_it_fits` against the specific word and chapter, not just its group. In Space, moons and dwarf planets belong in Our Solar System, while spacecraft terms belong elsewhere; named landforms and technical orbit terms may be advanced-only. In Flower Borders, prioritize familiar *plants in a flower border*; move pure flower anatomy to an educational botany chapter if it dilutes the garden experience.
2. For every ambiguous or region-dependent word, record a one-sentence sense and keep HOLD if unresolved. Examples: `COMA` is comet anatomy in this context, `ARIEL` may be a moon or a personal name, `BLACK-EYED SUSAN` and `RUDBECKIA` can refer to overlapping flower names. Never treat them as multiple concepts automatically.
3. Use `canonical_existing_word_id` if present; do not assign final new IDs before exact global deduplication and signoff. Keep zero owner approvals in this manuscript. Review spellings/US forms with high-confidence sources and record content-rights notices before any public export.
4. Run 12/15/20 full placement, word-list readability, collision and repeat tests only after approved draft selection. Preserve all existing V5 and historical frozen pages.

## Editorial reference material (no text/database copying)

- Solar-System worlds: https://science.nasa.gov/solar-system/planets/
- Natural satellites: https://science.nasa.gov/solar-system/moons/facts/
- Solar System overview and small bodies: https://science.nasa.gov/solar-system/solar-system-facts/
- Planetary science: https://science.nasa.gov/planetary-science/focus-areas/
- University of Kentucky annual/perennial flowers: https://publications.mgcafe.uky.edu/sites/publications.ca.uky.edu/files/ho102.pdf
- University of Maryland perennials: https://www.extension.umd.edu/resource/perennials-selection-planting-and-care
- NC State Extension gardener glossary: https://content.ces.ncsu.edu/extension-gardener-handbook/glossary

**Files:** `data/lexicon/intake/ws65-specialist-chapter-candidates-v1.tsv` holds original selection + exact lexical reconciliation at the WS64 snapshot. Follow-up WS65-B should add a reproducible read-only QA report/test over this file and then perform manual decisions; do not overwrite the original historical manuscripts.
