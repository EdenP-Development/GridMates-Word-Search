# November 23, 2026 — Home controls and library grouping

The owner tested v110 and supplied phone screenshots of Home, the library, and a Space puzzle. The full Solo status card responded to a tap, a horizontal glint crossed the green controls, and three editorial editions occupied extra positions on Home and in the library. The fallback is the signed v110 APK from `1b821e833234a775fb66a600e2d050b1411ba209`. [v110 draft and Android receipts](https://github.com/PlayerEden/GridMates-Word-Search/pull/187)

## WS67 v111 changes

- The green New or Play pill is the sole action on the Solo status card. The shared green painter uses its existing gradient and curved gold borders with the decorative horizontal strokes removed.
- Home and the shelf present Woodland, Space, and Garden. The library groups matching editorial chapters with the original titles, including Our Solar System under Space and Quiet Clearings under Woodland. Displayed totals are 42 Woodland pages, 24 Space pages, and 21 Garden pages.
- Each of the 87 pages still opens via its original book ID, page ID, and board digest. Both journals keep their v110 formats and paths. Existing bookmarks are visible inside the book. Selecting a new bookmark explicitly sets the marker for that displayed book. Game and reward rules still use the original page owner.

## Verification and continuing work

The QA draft targets the tested v110 branch. At `199a04d0cf8ef3cce3f48608ded707b6b04f0e7b`, Android QA passed one v110 seed test and two v111 UI tests. The same-signer upgrade kept both v110 journals byte-identical, and the Space book resumed the saved Solar System page with its found word. Release isolation also passed. The signed v111 APK SHA-256 is `b2b966ee60d529d7f45fdcaf6db96f17326eafa62abb298401abcc26ab20bf0f`. [Android QA](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45273861695) · [Release isolation](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45273861732) · [v111 APK ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45273861695/artifacts/14866208722)

Android's first-run full-screen tip obscured the initial screen captures. The evidence follow-up captures the app window after confirming each screen is visible and requires all three PNGs to be exported. The exact capture result will be recorded on the [WS67 draft](https://github.com/PlayerEden/GridMates-Word-Search/pull/188).

Carry the requested full visual and asset audit forward as a separate pass. Cover clipping at the Home scroll boundaries and across puzzle word lists, artwork crop and safe areas, asset reuse versus accidental duplication, and consistency of color, outlines, typography, and illustration style across Home, library, gameplay, and secondary screens.
