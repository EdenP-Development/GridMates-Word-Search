# November 23, 2026 — Home controls and library grouping

The owner tested v110 and supplied phone screenshots of Home, the library, and a Space puzzle. The full Solo status card responded to a tap, a horizontal glint crossed the green controls, and three editorial editions occupied extra positions on Home and in the library. The fallback is the signed v110 APK from `1b821e833234a775fb66a600e2d050b1411ba209`. [v110 draft and Android receipts](https://github.com/PlayerEden/GridMates-Word-Search/pull/187)

## WS67 v111 changes

- The green New or Play pill is the sole action on the Solo status card. The shared green painter uses its existing gradient and curved gold borders with the decorative horizontal strokes removed.
- Home and the shelf present Woodland, Space, and Garden. The library groups matching editorial chapters with the original titles, including Our Solar System under Space and Quiet Clearings under Woodland. Displayed totals are 42 Woodland pages, 24 Space pages, and 21 Garden pages.
- Each of the 87 pages still opens via its original book ID, page ID, and board digest. Both journals keep their v110 formats and paths. Existing bookmarks are visible inside the book. Selecting a new bookmark explicitly sets the marker for that displayed book. Game and reward rules still use the original page owner.

## Verification and continuing work

The QA draft targets the tested v110 branch. The Android run checks the package and signer, seeds real v110 bookmarks and a found word, installs v111 over it, compares the saved bytes, and exercises the native Home and Space routes. The draft records the tested commit, APK, and visual evidence.

Carry the requested full visual and asset audit forward as a separate pass. Cover clipping at the Home scroll boundaries and across puzzle word lists, artwork crop and safe areas, asset reuse versus accidental duplication, and consistency of color, outlines, typography, and illustration style across Home, library, gameplay, and secondary screens.
