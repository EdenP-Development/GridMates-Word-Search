# WS27 — Collection first-screen density after physical v88 screenshots

**Date:** November 15, 2026 (Kentucky local); GitHub UTC November 16.  
**Status:** actual Android source changes isolated in draft branch; QA / owner approval not presumed.  
**Base:** WS26 v88 verified source `917947db55827d885897829da52f24327b9395a0`, draft PR #133; no merge into main.

## Physical v88 screenshots and decision

The owner shared real-phone images for the illustrated Home, walnut Library and the new 2-column Collection portrait gallery. The gallery cards successfully render Owl/Fox in two columns, with unlocked/equipped states correctly represented. But the first actual rewards appear very low on the first Collection viewport: full-width introductory copy, illustrated tabs, a large duplicate profile/name-edit card, two-line Woodland heading, another instructional paragraph and pager all precede the portraits.

Keep the rich walnut bookcase and functioning Home/puzzle board untouched. This release focuses on bringing **rewards into view sooner** rather than increasing screen ornamentation.

## Source changes

- Compact ordinary-font Collection identity: profile portrait (36dp), local player name, equipped label, and live **Edit display name** action in one horizontal row. Maintains a 48dp button, original profile store update, save schema and profile info panel/test tag.
- At `fontScale >= 1.3f` retain the older stacked identity+edit layout to avoid squeezing the CTA or name under accessibility font scaling.
- Shorten gallery heading from `Woodland companions` to `Woodland`, while retaining the live `X of 12 companions unlocked` subtitle, optional exact-source WS22 Robin and artwork-missing message.
- Remove the **redundant** explanatory paragraph above the gallery, retaining informative text below the gallery and individual reward unlock requirements.
- Do not change the approved 2-column/3-page normal layout, large-text list fallback, 12 original portrait IDs, unlock thresholds, equip behavior, achievements, fonts, main Profile, Home, walnut Library, puzzle input, co-op or privacy.
- Add `test_ws27_collection_density.py` and one on-device Compose test assertion for the 360dp compact identity; both Android and independent Art QA call the source guard.
- QA code **89** (v88→v89) to preserve install-over candidate compatibility with stable test signer. No user should uninstall to test.

## Validation gates and evidence limits

1. Exact source source/layout and profile-edit tests pass.
2. Full Android QA (Gradle compilation, unit tests, AndroidTest compilation, signed package) and independently assembled art/release isolation build pass on exact final commit.
3. Compare standard and independent APK contents, versionCode 89, v88 signer and WS22 Robin SHA; provide installable artifact only after checks.
4. Real phone QA: Collection initial viewport now shows more of Owl and Fox; edit name remains accessible; next pages, equip, locks and fonts/achievements unchanged; 150-200% font fallback.
5. Owner actual physical approval and two-phone LAN are separate, not implied by CI.

Rollback reference: [v88 Android QA #43805246621](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43805246621) and [ArtQA #43805246619](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43805246619). Do not downgrade or clear saves without a backup.
