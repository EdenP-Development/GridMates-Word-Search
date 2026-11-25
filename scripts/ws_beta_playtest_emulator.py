"""Install the icon playtest over the illustrated beta with a full QA save."""

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

    device.adb("install", "beta-v122/GridMates-WordSearch-Beta1-Playtest.apk")
    device.open_settings(device.BETA)
    device.open_restore_preview("1 completed book pages · 1 saved book pages.")
    device.tap(device.ui("Restore backup"))
    device.ui("Backup restored. Your saved progress is ready.")

    # Android's package manager checks that the new APK can update the old one.
    device.adb("install", "-r", "beta-playtest/GridMates-WordSearch-Beta1-Icon-Playtest.apk")
    device.open_settings(device.BETA)
    updated = export_playtest("WordSearch-Beta1-Icon-Updated.json")
    assert updated == source, "The signed update changed the profile or book saves"
    assert device.qa_journal_hashes() == qa_before, "The original v120 saves changed"
    print("PASS: The full save survived the signed update from code 122 to code 123.")

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

    device.adb("shell", "am", "start", "-W", "-a", "android.settings.APPLICATION_DETAILS_SETTINGS",
               "-d", f"package:{device.BETA}")
    device.wait_for_node(
        lambda node: node.get("package") == "com.android.settings" and device.has_label(node, "Word Search"),
        "Word Search app info in Android Settings"
    )
    screenshot("installed-app-icon")

    result = (
        "PASS: Illustrated Beta 1 code 122 updated to the signed code 123 icon playtest. "
        "The full portable profile, font, book bookmarks and page sessions matched after restart. "
        "Home and the illustrated Library loaded with Woodland, Space and Garden, "
        "and Woodland resumed its saved page with one word found. "
        "The three original v120 journals matched byte for byte. "
        "The installed app icon was captured in Android Settings."
    )
    (device.REPORT / "result.txt").write_text(result + "\n")
    print(result)


if __name__ == "__main__":
    main()
