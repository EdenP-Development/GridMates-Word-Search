"""Check a signed v103 upgrade, Home, Quick Play and a real chapter jump."""
import json
import os
from pathlib import Path

import ws41_phone_review as ui

PACKAGE = ui.PACKAGE
QUICK_PLAY = "Quick Play. Choose a size for a fresh puzzle"
LIBRARY = "Open Full Library puzzle books"
SAVE = "files/gridmates_wordsearch_solo_v1.json"
ui.XML = "/sdcard/ws42-ui.xml"
ui.EVIDENCE = Path("qa-ws42-native")


def tap(label):
    ui.tap(ui.find(ui.await_screen(label), label))


def read_save():
    return ui.adb("exec-out", "run-as", PACKAGE, "cat", SAVE)


def main():
    ui.EVIDENCE.mkdir(exist_ok=True)
    ui.adb("shell", "wm", "size", "744x1696")
    ui.adb("shell", "wm", "density", "320")
    ui.adb("shell", "settings", "put", "system", "font_scale", "1.0")
    ui.adb("shell", "settings", "put", "secure", "immersive_mode_confirmations", "confirmed")
    ui.adb("shell", "wm", "dismiss-keyguard")
    assert b"Success" in ui.adb("install", "qa-baseline/app-debug.apk", timeout=90)
    assert b"versionCode=103 " in ui.adb("shell", "dumpsys", "package", PACKAGE)
    ui.launch()
    ui.open_home_action(QUICK_PLAY)
    tap("Large · 20×20")
    ui.await_screen("0 / 24 found")
    tap("Home")
    ui.adb("shell", "am", "force-stop", PACKAGE)
    before = read_save()
    assert json.loads(before)["active"]["frozen"]
    assert b"Success" in ui.adb(
        "install", "-r", "app/build/outputs/apk/debug/app-debug.apk", timeout=90
    )
    assert b"versionCode=104 " in ui.adb("shell", "dumpsys", "package", PACKAGE)
    assert before == read_save(), "An install-over must preserve the v103 save bytes"

    ui.launch()
    roots = ui.await_screen("Open menu", "Open player profile", "Word Search")
    initial_menu = ui.bounds(ui.find(roots, "Open menu"))
    ui.capture("home.png")
    ui.scroll_to("My Puzzles")
    roots = ui.await_screen("Open menu", "Open player profile", "My Puzzles")
    assert initial_menu == ui.bounds(ui.find(roots, "Open menu"))
    ui.capture("home-scrolled.png")
    tap("Open menu")
    ui.await_screen("Settings & backup")
    ui.adb("shell", "input", "keyevent", "BACK")

    ui.open_home_action(ui.SOLO)
    grid = "Word-search grid, 20 by 20. Drag or tap letters, or choose precise selection."
    ui.check_grid(ui.await_screen("0 / 24 found", grid), grid)
    assert json.loads(read_save())["active"]["frozen"] == json.loads(before)["active"]["frozen"]
    ui.await_screen("Drag a word or tap its first and last letters.")
    ui.capture("quick-play-large.png")
    tap("Options")
    tap("How to select words")
    ui.await_screen(ui.HELP)
    tap("Close")
    tap("Home")

    ui.open_home_action(LIBRARY)
    tap("Open Garden")
    tap("View pages")
    ui.await_screen("Chapters", "0 of 21 pages completed")
    ui.capture("book-detail.png")
    tap("Chapters")
    ui.capture("chapters.png")
    ui.tap(ui.scroll_to("8. A Flourishing Garden"))
    ui.await_screen("Page 19 · Not started")
    ui.capture("chapter-jump.png")
    ui.tap(ui.scroll_to("Play page 19"))
    ui.await_screen("Page 19", "Hint")
    tap("Hint")
    tap("Notice history")
    ui.await_screen("Game notices", predicate=lambda root: any(
        "Hint: Look for " in node.get("text", "") and "near row" in node.get("text", "")
        for node in root.iter("node")
    ))
    ui.capture("game-notices.png")
    tap("Close")
    ui.await_screen("Page 19")
    (ui.EVIDENCE / "receipt.json").write_text(json.dumps({
        "source": os.environ.get("WS42_SOURCE"),
        "upgrade": "v103 to v104, save bytes unchanged",
        "quick_play": "20x20 frozen grid resumed with fitted board and gesture help",
        "home": "Open menu bounds stable after scrolling to My Puzzles",
        "book": "Garden chapter 8 jumped to page 19, useful hint in notices",
        "display": "372dp, API 35"
    }, indent=2) + "\n")


if __name__ == "__main__":
    try:
        main()
    finally:
        (ui.EVIDENCE / "android-logcat.txt").write_bytes(ui.adb("logcat", "-d", "-t", "500"))
