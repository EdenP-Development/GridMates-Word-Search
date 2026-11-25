"""Install the signed playtest over Beta 1 and rehearse the full QA backup."""

import ws_beta_import_emulator as device


def export_playtest(name):
    return device.export_backup(f"/sdcard/Download/{name}", name)


def screenshot(name):
    png = device.adb("exec-out", "screencap", "-p")
    assert png.startswith(b"\x89PNG\r\n\x1a\n"), f"Missing screenshot {name}"
    (device.REPORT / f"{name}.png").write_bytes(png)


def main():
    device.REPORT.mkdir(exist_ok=True)
    device.adb("shell", "settings", "put", "system", "font_scale", "1.0")
    device.adb("install", str(device.QA_DIR / "app-debug.apk"))
    device.adb("install", "-t", "beta-playtest-input/app-debug-androidTest.apk")
    device.instrument("WsBetaV120BackupSeedAndroidTest", 1)

    device.open_settings(device.QA)
    source = device.export_backup(device.QA_BACKUP)
    assert source["books"]["progress"] and source["books"]["sessions"]
    assert source["boardFont"] == "plex" and len(source["solo"]["bookAwards"]) == 1
    qa_before = device.qa_journal_hashes()

    device.adb("install", "beta-v121/GridMates-WordSearch-Beta1.apk")
    device.open_settings(device.BETA)
    device.open_restore_preview()
    device.tap(device.ui("Restore backup"))
    device.ui("Backup restored. Your saved progress is ready.")

    # Android's package manager checks that the new APK can update the old one.
    device.adb("install", "-r", "beta-playtest/GridMates-WordSearch-Beta1-Playtest.apk")
    device.open_settings(device.BETA)
    updated = export_playtest("WordSearch-Beta1-Updated.json")
    assert updated["solo"] == source["solo"], "Beta progress changed during the signed update"
    assert updated["boardFont"] == source["boardFont"], "Beta board font changed during the signed update"
    print("PASS: Beta 1 code 121 updated to code 122 and kept the full portable profile.")

    device.open_restore_preview("1 completed book pages · 1 saved book pages.")
    device.tap(device.ui("Restore backup"))
    device.ui("Backup restored. Your saved progress is ready.")
    device.open_settings(device.BETA)
    restored = export_playtest("WordSearch-Playtest-RoundTrip.json")
    assert restored == source, "Full v120 backup differs after restoring and restarting"
    assert device.qa_journal_hashes() == qa_before, "The original v120 saves changed"
    print("PASS: The complete v120 backup, including book bookmarks and sessions, survived restart.")

    device.tap(device.ui("Back to Home"))
    device.ui("Your puzzle books")
    device.ui("Play Woodland")
    screenshot("illustrated-home")

    device.tap(device.ui("Open menu"))
    device.tap(device.ui("Full Library"))
    for title in ("Woodland", "Space", "Garden"):
        device.ui(f"Open {title}")
    screenshot("illustrated-library")
    device.tap(device.ui("Back to Play"))

    device.tap(device.ui("Play Woodland"))
    device.ui("1 of 16 words found")
    screenshot("restored-woodland-page")

    result = (
        "PASS: Beta 1 code 121 updated in place to the signed code 122 playtest. "
        "The v120 portable profile, font, book bookmarks and page sessions matched after restart. "
        "Home and the illustrated Library loaded with Woodland, Space and Garden, "
        "and Woodland resumed its saved page with one word found. "
        "The three original v120 journals matched byte for byte."
    )
    (device.REPORT / "result.txt").write_text(result + "\n")
    print(result)


if __name__ == "__main__":
    main()
