# November 23, 2026 — WS69 library polish

The owner asked to continue after the v111 integration. The v111 native library capture showed a rectangular dark edge across the wood and cut-off lower shelf brackets. The source is based on [draft #189](https://github.com/PlayerEden/GridMates-Word-Search/pull/189) at `dbe0448ac1f8f7a46e24ea204e2b387049bfe15b`. The previous integration passed 14 workflows. The [signed v111 APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45273861695/artifacts/14866208722) is the fallback. [Previous pass](2026-11-23_WS68_BETA_INTEGRATION.md)

## Changes

- The cabinet renderer uses the contiguous source shading across its crown, pilasters, backboard and base. The extra overlay on only the backboard caused the abrupt visible edge.
- The shelf stage reserves the painted bracket depth plus clearance above the base. Book positioning at the shelf contact plane and pager control positions stay aligned.
- The full library title moves below the controls when its measured width exceeds the space between them. At Home, the cover painter trims transparent rows below the image and aligns the visible cover with the shelf. Covers retain the same bottom pivot while swiping.
- A native Android check uses the actual bookcase and shelf raster bounds at 320dp and approximately 393dp. It also checks Home navigation and resumes a real Solar System save, then scrolls to a target in the word list.
- The QA build is version code 112. The app uses the existing source-pinned art, frozen boards and compatible QA signature.

## Build and follow-through

The first v112 candidate at `5219bd62c7b74ef75dfe67cf1ef95f936bfff3aa` passed the signed upgrade and bracket checks. Its 320dp captures revealed a truncated library title and a visible gap below the Home cover. The revised checks require the full title and book contact at both widths. [Initial Android run](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45290364448) · [Initial release isolation](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45290364419) · [Initial APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45290364448/artifacts/14874915241)

The revised candidate at `2d4e031f0bbfdfe5dd0a836c6eaa3eaf21063d87` passed release isolation and kept both journals byte-identical. At 393dp the Home cover met the shelf exactly and the full title was visible. The generic text-overflow assertion failed on that screenshot, so the validation now tests the last visible character and ellipsis directly and runs both widths independently. [Android retry result](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45292435824) · [Release isolation](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45292435920) · [Revised APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45292435824/artifacts/14875311802)

The final build at `fb7ffaabc6f085524dbd08e477ec2a88accc55b3` passed both workflows and all four native tests. It rendered all 12 title characters and a zero measured Home cover-to-shelf gap at both widths. The lower shelf raster clears the base by 5.45dp at 320dp and 5.82dp at 393dp. The two saved journals match v111 byte for byte. The same found word is visible on the resumed puzzle, and the word list reaches its final entry at both widths. [Android QA](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45294112307) · [Release isolation](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45294112941) · [Screenshots and upgrade evidence](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45294112307/artifacts/14875398966)

The signed QA APK uses `com.playereden.gridmates.wordsearch.qa`, version code 112, 34,254,647 bytes, and SHA-256 `92eecab6cc6423be41997b5ca347887677a150a851646d009b672da0ed967fc0`. All 237 packaged assets matched v111, including the frozen preview boards. [Signed v112 APK ZIP](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/45294112307/artifacts/14876041667)

Handset review is open. The next content pass is deeper tested pools for Quiet Clearings Expert and Our Solar System Familiar.
