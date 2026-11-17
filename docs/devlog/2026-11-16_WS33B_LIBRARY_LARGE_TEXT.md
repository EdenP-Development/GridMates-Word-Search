# November 16, 2026 — WS33 Library heading and short-screen menu

The [v95 Woodland playthrough](2026-11-16_WS33_PACKED_LIBRARY.md) confirmed that partial finds and completed pages persist across app restarts. The [320dp and 200% text capture](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/44025258933/artifacts/14303017070) showed that all three volumes could be reached, while the cabinet title clipped the word Library and covered the tops of the first bindings. The same pass found that the Home menu lost lower actions at 260 × 450dp and 200% text.

The v96 QA candidate puts the enlarged title below the navigation buttons, lets it span the safe width, and measures its height before positioning the first shelf. The Home menu scrolls when the actions exceed the available height. The Android title test now checks layout overflow and title clearance in addition to book access. The standard Android emulator run focuses on the Woodland journey. The installed-app layout checks use a separate fresh emulator.

Build and installed-app checks are pending. Start from [draft PR #144](https://github.com/PlayerEden/GridMates-Word-Search/pull/144). The [signed v95 APK](https://github.com/PlayerEden/GridMates-Word-Search/actions/runs/43999350042/artifacts/14291891401) is the last verified Woodland checkpoint. Portable book backups follow the layout pass.
