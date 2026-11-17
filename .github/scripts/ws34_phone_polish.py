"""Exercise the signed handset polish build through normal app screens."""
import re
import subprocess
import time
from pathlib import Path
from xml.etree import ElementTree


PACKAGE = "com.playereden.gridmates.wordsearch.qa"
ACTIVITY = f"{PACKAGE}/com.playereden.gridmates.wordsearch.MainActivity"
XML = "/sdcard/ws34-ui.xml"
EVIDENCE = Path("qa-phone-report")
COLLECTION = "Open Collection gallery and unlocks"
SOLO = "Continue saved solo word search"
BACK = "Back to Home"
GRID = "Word-search grid, 15 by 15. Drag or tap letters, or choose precise selection."
HELP = (
    "Trace a word with one finger. Pinch with two fingers to zoom, "
    "or tap its first and last letters."
)


def adb(*args, timeout=18):
    return subprocess.check_output(["adb", *args], timeout=timeout)


def bounds(node):
    return tuple(map(int, re.findall(r"\d+", node.get("bounds", ""))))


def names(node):
    return node.get("text", ""), node.get("content-desc", "")


def find(root, label):
    return next((node for node in root.iter("node") if label in names(node)), None)


def tap(node):
    left, top, right, bottom = bounds(node)
    assert right > left and bottom > top
    adb("shell", "input", "tap", str((left + right) // 2), str((top + bottom) // 2))


def launch():
    adb("shell", "am", "start", "-W", "-n", ACTIVITY, timeout=40)


def await_screen(*labels, predicate=None):
    latest = None
    for _ in range(6):
        try:
            adb("shell", "rm", "-f", XML)
            adb("shell", "uiautomator", "dump", XML)
            latest = ElementTree.fromstring(adb("exec-out", "cat", XML))
            if find(latest, "Pixel Launcher isn't responding") is not None:
                tap(find(latest, "Close app"))
                launch()
                continue
            if (all(find(latest, label) is not None for label in labels)
                    and (predicate is None or predicate(latest))):
                return latest
        except (subprocess.SubprocessError, ElementTree.ParseError):
            if b"device" not in adb("get-state"):
                raise
        time.sleep(0.6)
    visible = [names(node) for node in latest.iter("node")
               if any(names(node))] if latest is not None else []
    raise AssertionError(f"Expected screen with {labels!r}; displayed {visible!r}")


def vertical_scroll(root):
    scrolls = [node for node in root.iter("node") if node.get("scrollable") == "true"]
    return max(scrolls, key=lambda node: bounds(node)[3] - bounds(node)[1])


def swipe_up(node):
    left, top, right, bottom = bounds(node)
    x = str((left + right) // 2)
    assert bottom - top > 64
    adb("shell", "input", "swipe", x, str(bottom - 22), x, str(top + 22), "350")


def capture(name):
    (EVIDENCE / name).write_bytes(adb("exec-out", "screencap", "-p"))


def open_home_action(label):
    root = await_screen("Open menu")
    for _ in range(4):
        action = find(root, label)
        if action is not None:
            tap(action)
            return
        swipe_up(vertical_scroll(root))
        root = await_screen()
    raise AssertionError(f"Home action {label!r} is outside its scrolling content")


def words(root):
    return {node.get("text") for node in root.iter("node")
            if node.get("text", "").startswith(("○ ", "✓ "))}


EVIDENCE.mkdir(exist_ok=True)
try:
    # In fullscreen the Pixel profile reserves a 30dp gesture inset plus
    # an 18dp app margin per side. Leave at least 320dp between the margins.
    adb("shell", "wm", "size", "880x1440")
    adb("shell", "wm", "density", "320")
    adb("shell", "settings", "put", "system", "font_scale", "1.0")
    adb("shell", "wm", "dismiss-keyguard")
    adb("shell", "settings", "put", "secure", "immersive_mode_confirmations", "confirmed")
    assert b"Success" in adb("install", "qa-baseline/app-debug.apk", timeout=90)
    assert b"Success" in adb("install", "-r", "qa-candidate/app-debug.apk", timeout=90)
    assert b"versionCode=97 " in adb("shell", "dumpsys", "package", PACKAGE)
    launch()
    print("PASS: signed v97 installed over v96 at 440 x 720 dp.")

    open_home_action(COLLECTION)
    tabs = ("Your Collection", BACK, "Portraits", "Fonts", "Achievements")
    gallery = await_screen(*tabs, "Woodland", "Page 1 of 3")
    capture("collection-top.png")
    woodland_title = bounds(find(gallery, "Woodland"))
    swipe_up(vertical_scroll(gallery))
    scrolled = await_screen(*tabs, "1 puzzle to unlock",
                            predicate=lambda root: find(root, "Woodland") is None
                            or bounds(find(root, "Woodland")) != woodland_title)
    assert bounds(find(gallery, BACK)) == bounds(find(scrolled, BACK))
    assert bounds(find(gallery, "Fonts")) == bounds(find(scrolled, "Fonts"))
    capture("collection-scrolled.png")
    tap(find(scrolled, "Fonts"))
    fonts = await_screen(*tabs, "Board fonts", "1 of 4 unlocked")
    capture("collection-fonts.png")
    tap(find(fonts, BACK))
    print("PASS: Collection scrolls behind its fixed Back and tabs; Fonts opens at its top.")

    open_home_action(SOLO)
    controls = ("Select a word without dragging", "Fit entire puzzle board",
                "Zoom in", "Zoom out")
    board = await_screen(*controls, GRID, HELP, "Words to find · 16 left")
    assert not any((node.get("text") or "").startswith("Trace words ·")
                   for node in board.iter("node"))
    before = words(board)
    assert before
    capture("solo-word-list-top.png")
    swipe_up(vertical_scroll(board))
    after = await_screen(GRID, "Words to find · 16 left",
                         predicate=lambda root: bool(words(root) - before))
    assert bounds(find(board, GRID)) == bounds(find(after, GRID))
    capture("solo-word-list-scrolled.png")
    print("PASS: full gesture help and selection controls are visible; the word list scrolls independently.")
finally:
    report = subprocess.run(["adb", "logcat", "-d", "-t", "160"], timeout=10,
                            stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    (EVIDENCE / "android-logcat.txt").write_bytes(report.stdout)
