# WS22 — GridMates reusable theme motifs and prestige frames

**Status:** 40 actual candidate assets produced, source-verified, **all present in GitHub** on the [draft PR #131 branch](https://github.com/PlayerEden/GridMates-Word-Search/pull/131). No Android UI changes, no build/version bump, no production art approval.

## The assets

- **16 original transparent illustrated PNGs:** Woodland (badger, robin, squirrel, stag); Ocean (seahorse, jellyfish, lighthouse, pearl clam); Garden (hummingbird, watering can, sunflower, mantis); Space (astronaut helmet, telescope, comet, spiral galaxy). Every crop is stored in [`sprites/motifs/`](sprites/motifs/) with its original SHA-256 and dimensions in `manifest.json`.
- **24 editable SVG prestige frames:** six standard slots (round, shield, portrait, ribbon, starburst, book crest), each in bronze, silver, gold and emerald variants, stored under [`svg/premium_frames/`](svg/premium_frames/). Shared GridMates Core art, usable for Word Search/Sudoku/future games; labels and achievement status remain native UI.
- **Exact original transparent PNG source sheet:** its 3,485,649 byte master source exceeded the GitHub connector's single-request limit, so it is preserved **losslessly as seven GitHub binary shards** under [`source_parts/`](source_parts/), with SHA-256s in `source_parts/manifest.json`. Run `python3 design/art-library-v2/ws22/restore_atlas.py` after checkout to recreate `sources/ws22_master_motifs_alpha.png` **byte for byte**. The finished source SHA-256 is `9de514db9f09ac9fa09fe5fa5a4b0a3c0a0f98ca9dbda6df8abf6ff3e5e7a70e`.
- **`rebuild_ws22.py`** stores reproducible sprite-cropping, alpha-edge decontamination, vector source construction, previews, and a deterministic ZIP export. Requires Pillow, NumPy, SciPy and CairoSVG (for SVG preview only). From GitHub checkout: restore atlas, then `python3 design/art-library-v2/ws22/rebuild_ws22.py` to regenerate the art pack; the script treats its containing folder as the artwork source root.
- **`manifest.json`** records 40 IDs, exact bitmap and SVG bytes, dimensions and source crops; **`validate_ws22.py`** validates the real checked-in PNGs, SVGs and reassembled original atlas.

## Independent backup and reproduction

The complete archive (including original, 16 PNGs, 24 SVGs, preview images, manifest and builder) is `GridMates_Asset_Expansion_WS22.zip`, 6,316,370 bytes with SHA-256 **`cadbfac63005a280dcbeee9ca6ecc4bef13c552308edc2b073c59c47aa12f2c8`**. It is preserved separately in ChatGPT Library as `/Game Design/GridMates Word Search/GridMates_Asset_Expansion_WS22_FULL.zip`.

The ZIP passed CRC and exact-match repeat builds twice from the working folder and once from an independently extracted copy. **Unlike earlier packs WS17–WS21, all of WS22's actual image sprite bytes are now committed to GitHub**, as well as the exact source image via sharded binary blobs. Earlier packs still require a similar source-binary import for complete GitHub-only backups.

## Placement / release policy

Keep these as candidates for theme book covers, profile rewards, achievements, and high-value cosmetics. Nothing should be treated as a game reward entitlement or a cosmetic store offer without separate product design. Preview at 48/72/96px on 320/360dp screens; test contrast and 150–200% text scaling where controls are adjacent. The more realistic painted motifs and intentionally simpler vector frames can coexist; the latter should never cover native text or touch zones.

[Source CI workflow](../../../.github/workflows/ws22-actual-binary-art-qa.yml) reconstructs the original source and checks actual PNG + SVG hashes; a pass is **not** a physical phone visual review or gameplay test.
