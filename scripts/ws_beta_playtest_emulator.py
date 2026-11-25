"""Verify the signed library and collection update with a full QA save."""

import re

import ws_beta_import_emulator as device


def export_playtest(name):
    return device.export_backup(f"/sdcard/Download/{name}", name)


def screenshot(name):
    png = device.adb("exec-out", "screencap", "-p")
    assert png.startswith(b"\x89PNG\r\n\x1a\n"), f"Missing screenshot {name}"
    (device.REPORT / f"{name}.png").write_bytes(png)


def open_library():
    device.tap(device.ui("Open menu"))
    device.tap(device.ui("Full Library"))
    for title in ("Woodland", "Space", "Garden"):
        device.ui(f"Open {title}")


def check_collection():
    device.tap(device.ui("Collection"))
    device.ui("Your Collection")
    rows = []
    for label in ("Portraits", "Fonts", "Achievements", "Albums"):
        node = device.ui(label)
        _, top, _, _ = map(int, re.findall(r"\d+", node.get("bounds", "")))
        rows.append(top)
    assert abs(rows[0] - rows[1]) <= 2 and abs(rows[2] - rows[3]) <= 2, rows
    assert rows[1] < rows[2], rows
    screenshot("collection-tabs")
    device.tap(device.ui("Albums"))
    device.tap(device.wait_for_node(
        lambda node: "Garden album" in node.get("text", "")
        or "Garden album" in node.get("content-desc", ""),
        "Garden album link",
    ))
    device.ui("First Bloom")
    device.ui("0 of 1 page")
    screenshot("collection-album")
    device.tap(device.ui("Back to albums"))
    device.tap(device.ui("Back to Home"))


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
    device.tap(device.ui("Back to Home"))
    open_library()
    device.ui("Library options")

    device.adb("install", "beta-v124/GridMates-WordSearch-Beta1-Owl-Icon-Playtest.apk")
    device.open_settings(device.BETA)
    device.open_restore_preview("1 completed book pages · 1 saved book pages.")
    device.tap(device.ui("Restore backup"))
    device.ui("Backup restored. Your saved progress is ready.")

    # Android's package manager checks that the new APK can update the old one.
    device.adb("install", "-r", "beta-playtest/GridMates-WordSearch-Beta1-Library-Polish-Playtest.apk")
    device.open_settings(device.BETA)
    updated = export_playtest("WordSearch-Beta1-Library-Updated.json")
    assert updated == source, "The signed update changed the profile or book saves"
    assert device.qa_journal_hashes() == qa_before, "The original v120 saves changed"
    print("PASS: The full save survived the signed update from code 124 to code 125.")

    device.tap(device.ui("Back to Home"))
    device.ui("Your puzzle books")
    device.ui("Play Woodland")
    screenshot("illustrated-home")

    open_library()
    assert not any(
        device.has_label(node, label)
        for node in device.hierarchy().iter("node")
        for label in ("Library options", "Previous Library artwork", "Preview options")
    )
    screenshot("illustrated-library")
    device.tap(device.ui("Back to Play"))

    check_collection()

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
        "PASS: Beta 1 code 124 updated to the signed code 125 Library polish playtest. "
        "The full portable profile, font, book bookmarks and page sessions matched after restart. "
        "Home and the illustrated Library loaded with Woodland, Space and Garden, "
        "and Woodland resumed its saved page with one word found. "
        "The artwork menu was visible in QA. The signed beta bookcase had only player controls. "
        "The four Collection tabs fit in two rows and Garden displayed the singular page count. "
        "The three original v120 journals matched byte for byte. "
        "The installed app icon was captured in Android Settings."
    )
    (device.REPORT / "result.txt").write_text(result + "\n")
    print(result)


if __name__ == "__main__":
    main()
