# WS16BU — Compact portrait gallery and accessible Profile stats

Date: November 14, 2026. Status: v79 implementation and QA source committed; owner phone acceptance pending. Draft PR #131 remains unmerged.

## Owner-reported evidence
Five screenshots of v78 showed the Woodland book details, Player Profile and three portions of Collection. The existing 4-per-page portrait gallery rendered as four vertically stacked oversized cards on a narrow content width. Page layout used screenWidthDp while the card layout used BoxWithConstraints.maxWidth, allowing incompatible decisions. Profile metrics wrapped one word onto a new line; Collection repeated its own name and profile banner.

## Exact changes
1. Commit e44c17b4d2bf2b68736c7e9953aed5badf5c4542: a single shell-level Your Collection heading and concise introduction; a smaller real editable identity card retaining Edit display name, Save/Cancel and Nearby privacy text.
2. Portrait layout now calculates both columns and page size from the same actual content width. At less than 520dp or fontScale >=1.3: one-column compact horizontal row cards, 3 portraits/page (4 pages for all 12). On wider screens: two columns and 4 portraits/page. Page selection is clamped when width changes.
3. Owl/other portraits remain original artwork. The equipped border, native unlock rules, progress bars and equipped/locked states remain. Available Fox or other unlocked portraits have a real min-48dp Equip button; Equipped and Locked are readable text, not low-contrast disabled button text.
4. Profile now displays completions and total words on separate short lines. No stat calculation or persistent data changed.
5. Commit 223e9f192ba29ad3947358c629be901bc6ef73c7 added new Python/source regression guards and revised 360dp Compose instrumentation source for Page 1 of 4 and Page 2 beginning with Bear; Android instrumented sources are compiled in GitHub, not executed on physical devices by default.
6. An earlier WS16BB test assumed Collection still owned a redundant StorybookHeading and direct illustrated PANEL. These outdated assertions caused failures in intermediate commits e44c17 and 223e9f. Commit f5069f65244e805f6f821d7a306bdff5215ee3c3 checks the new single shell header and actual reusable GridMatesInfoPanel instead. This is a documented test migration, not a release success claim.

## Build gate
Final candidate versionCode 79, versionName 0.9.35-compact-portrait-gallery-qa; source commit f5069f65244e805f6f821d7a306bdff5215ee3c3.
Android QA: https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43646063049
Independent art/release QA: https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43646063039
Check exact conclusions, APK integrity/signature and unchanged art before handing off. Prior verified v78 APK artifact: https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43643636791/artifacts/14133392125

## Owner on-device acceptance
- Collection first page: one main title, compact local-name card and earlier access to portraits; Owl, Fox and Rabbit should be on Page 1 of 4; Bear begins Page 2.
- Verify actual Fox equip, Owl/locked states, original 12 portraits, next-page/back and progress. Screen-width and large-text behavior need device QA (320dp, 200%, TalkBack).
- Check Profile stats and local display name; verify Fonts, Achievements, Records Solo/Race/Duel unchanged; actual save/reopen and Nearby two-device test still pending.
- No new image/font assets or changed Library cabinet, gameplay, stored puzzle formats, unlock thresholds or art rights/approval.
- Keep PR #131 DRAFT, do not uninstall the app, and do not merge or claim production approval from compiled CI.

## Final exact-source GitHub QA / signed APK receipt

Compiled candidate SHA f5069f65244e805f6f821d7a306bdff5215ee3c3. Subsequent documentation-only commits are NOT separate APK builds.
- Android QA run https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43646063049: SUCCESS, 76/76 steps.
- Independent artwork/release QA run https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43646063039: SUCCESS, 77/77 steps. Both runs used the same compiled source and regression suite, including the updated historical WS16BB guard.
- Standard signed QA artifact ZIP https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43646063049/artifacts/14134286557 : SHA256 c59d8f813976bb9159d32e3baf6addedc8154d24fc6076fe2bb2dab39ae4b6c7. Embedded app-debug.apk 30,491,036 bytes, SHA256 f1807a7b14700731a13193e54006cee8a5859a6da2186797650023f26e029f6a.
- Independent debug art artifact https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43646063039/artifacts/14134272311 : ZIP SHA256 110f6937cc1e1f7547cbe5457fae67476597c9148e39476c90c6fe403fafcb5a.
- Downloaded artifact archives passed ZIP integrity checks. Compared the two independently built v79 APKs: 360/360 unpacked entries byte-identical; the outer APK ZIP files may differ due to packing/signature metadata.
- Compared signed standard v79 with verified v78: 219/219 packaged assets unchanged; only AndroidManifest.xml and classes4.dex bytes differ. No new imagery or production art promoted.
- Parsed actual Android APK Signature Scheme v2 signer certificates from v78 and v79; identical SHA256 75cc3c896247d71fbd51f7a3ed41f90c8218a8a5a70df8c0f6b494b5cc23d419. Same QA signing configuration; install directly over v78 WITHOUT uninstalling or clearing local data.
- This verifies build integrity, source checks, reproducible art/release isolation and Android test-source compilation. Android instrumentation tests were not executed on a handset/emulator by these two CI runs; owner must inspect actual portrait density, equip behavior, font scaling, scroll/pager, real save persistence, Fonts/Achievements/Records and other functional gates.
- PR #131 remains DRAFT, with no merge/release approval.


## Owner v79 real-phone visual review — November 14, 2026

**Evidence:** Six owner-supplied Android screenshots in the current game-design conversation: Collection/Portraits top, Portraits scrolled to all three first-page cards and Collection progress, Fonts top, Fonts lower cards, Achievements top, and Player profile. The screenshots were reviewed directly in the conversation, not uploaded to GitHub. This is a visual QA assessment; it does **not** prove buttons were tapped, that the selected portrait/font persisted, or that device accessibility and save tests passed.

- **Portraits — marked visually improved, final owner approval still open:** Page 1 of 4 renders Owl, Fox, Rabbit as compact left-portrait/right-status rows instead of tall mostly empty cards. Owl has a clear green equipped outline and Equipped label; Fox has an enabled-looking large Equip control; Rabbit is muted with an explicit one-puzzle unlock requirement and a short progress bar. The lower image shows the Collection progress frame and next Rabbit unlock message intact. No obvious clipping or overlap in these views. Preserve portrait art, unlock order, 12 items, profile display-name edit, paging and collection progress. **The Fox button has not been proven tapped/persisted by images**; page 2 not shown.
- **Fonts — strongest next usability issue:** Four font previews are useful and distinct, but each old `BoardFontCard` has a large white vertical footprint with oversized space for Selected/Locked and License controls. Current Locked and Selected labels appear very light/low contrast. At phone width, one tall card per row requires excessive scrolling. Recommend WS16BV compact font card layout: keep sample letters readable and large, show font name and earned/equipped/locked status beside or below the sample, use genuine readable text for unavailable/selected state, an enabled **Equip** action only when allowed, and a visible but more compact **License** control for every font (do not remove bundled OFL access). Preserve state/equipped border and four actual font definitions, license ID/file paths, native font preview, unlock achievement names, 48dp interactive targets. No new font files or decorative imagery required.
- **Achievements — retain design:** The existing cream/gold progress cards (First Page, Today Counts, Sharp Eyes visible) are legible, with descriptions and bars; Page 1 of 2 navigation appears clear. No reason for further artwork changes. Only first page and top portion photographed; page 2 and actual earned state still untested.
- **Player profile — improvement observed:** The two metric lines `0 puzzles completed` and `0 words found` no longer orphan a single word. Live owl portrait, feature frame, and three navigation choices are coherent. A minor duplicate heading remains: `Player profile` at the top, followed by `Your profile` above the menu; may rename the latter to `Explore` or remove it, without changing navigation. Large blank paper below the short menu is not an overlap defect and does not warrant decorative content.
- **Not established by these screenshots:** Actual Fox equip/save/relaunch, font Equip/License actions, later portrait/achievement pages, completed-state correctness, Records Solo/Race/Duel layout, small-phone and 200% font tests, TalkBack, gameplay hint/session persistence, two-phone Nearby, release-art approval. PR #131 stays DRAFT.
- **Recommended next narrow pass WS16BV:** compact **Fonts** layout only, possibly a small Profile section-heading rename; keep Portraits, Achievements, Home, book details and wood Library frozen unless physical tests identify a real regression. Run source tests for selected/locked/license semantics, compile Android instrumentation source, exact-source standard Android and independent art workflows, byte/signer comparisons, then owner screenshots of Fonts and actual license action. No source change or APK created from this documentation-only review.
