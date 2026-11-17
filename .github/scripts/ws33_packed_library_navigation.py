"""Check the signed QA app through its normal screens on a fresh emulator."""
import re
import subprocess
import time
from pathlib import Path
from xml.etree import ElementTree

PACKAGE = "com.playereden.gridmates.wordsearch.qa"
ACTIVITY = f"{PACKAGE}/com.playereden.gridmates.wordsearch.MainActivity"
XML = "/sdcard/ws33-ui.xml"
EVIDENCE = Path("qa-layout-report")


def adb(*args, timeout=18):
    return subprocess.check_output(["adb", *args], timeout=timeout)


def tree():
    adb("shell", "rm", "-f", XML)
    adb("shell", "uiautomator", "dump", XML)
    return ElementTree.fromstring(adb("exec-out", "cat", XML))


def names(node):
    return node.get("text", ""), node.get("content-desc", "")


def find(root, label):
    return next((node for node in root.iter("node") if label in names(node)), None)


def await_nodes(*labels):
    latest = None
    for _ in range(5):
        try:
            latest = tree()
            launcher_alert = find(latest, "Pixel Launcher isn't responding")
            if launcher_alert is not None:
                tap(find(latest, "Close app"))
                adb("shell", "am", "start", "-W", "-n", ACTIVITY, timeout=40)
                continue
            if all(find(latest, label) is not None for label in labels):
                return latest
        except (subprocess.SubprocessError, ElementTree.ParseError):
            if b"device" not in adb("get-state"):
                raise
        time.sleep(0.6)
    details = [names(node) for node in latest.iter("node")] if latest is not None else []
    raise AssertionError(f"Missing {labels!r}; visible screen labels: {details!r}")


def bounds(node):
    return tuple(map(int, re.findall(r"\d+", node.get("bounds", ""))))


def tap(node):
    left, top, right, bottom = bounds(node)
    assert right > left and bottom > top
    adb("shell", "input", "tap", str((left + right) // 2), str((top + bottom) // 2))


def start_at(width, height):
    adb("shell", "am", "force-stop", PACKAGE)
    adb("shell", "wm", "size", f"{width}x{height}")
    adb("shell", "wm", "density", "320")
    assert f"Override size: {width}x{height}".encode() in adb("shell", "wm", "size")
    assert b"Override density: 320" in adb("shell", "wm", "density")
    adb("shell", "am", "start", "-W", "-n", ACTIVITY, timeout=40)


def open_library():
    screen = await_nodes("Open menu")
    shortcut = find(screen, "Open Full Library puzzle books")
    if shortcut is not None:
        tap(shortcut)
        return
    tap(find(screen, "Open menu"))
    screen = await_nodes("Word Search menu")
    for _ in range(4):
        shortcut = find(screen, "Full Library")
        if shortcut is not None:
            tap(shortcut)
            return
        scroll = next(node for node in screen.iter("node") if node.get("scrollable") == "true")
        left, top, right, bottom = bounds(scroll)
        x = str((left + right) // 2)
        adb("shell", "input", "swipe", x, str(bottom - 16), x, str(top + 16), "350")
        screen = tree()
    raise AssertionError("Full Library is outside the scrollable Home menu")


def capture(name):
    (EVIDENCE / name).write_bytes(adb("exec-out", "screencap", "-p"))


EVIDENCE.mkdir(exist_ok=True)
try:
    assert adb("shell", "getprop", "ro.kernel.qemu").strip() == b"1"
    adb("install", "qa-baseline/app-debug.apk", timeout=90)
    adb("shell", "wm", "dismiss-keyguard")
    adb("shell", "settings", "put", "secure", "immersive_mode_confirmations", "confirmed")
    adb("shell", "settings", "put", "system", "font_scale", "2.0")
    assert adb("shell", "settings", "get", "system", "font_scale").strip() == b"2.0"

    start_at(640, 1440)
    open_library()
    shelf = await_nodes("Your Library", "Open Woodland", "Open Space", "Open Garden")
    capture("library-320dp-200-percent.png")
    tap(find(shelf, "Open Garden"))
    book = await_nodes("Garden", "View pages")
    capture("garden-cover-320dp-200-percent.png")
    tap(find(book, "View pages"))
    await_nodes("Book progress")
    print("PASS: 320dp at 200% text visits all three named bindings and opens Garden.")

    # At 260x450dp, the menu needs scrolling and one row of two books
    # splits the three-volume catalog across two real cases.
    start_at(520, 900)
    open_library()
    await_nodes("Open Woodland", "Open Space", "Next bookcase")
    capture("library-first-bookcase.png")
    adb("shell", "input", "swipe", "393", "475", "129", "475", "350")
    second = await_nodes("Open Garden", "Bookcase 2 of 2")
    assert find(second, "Open Woodland") is None
    capture("library-next-bookcase.png")
    print("PASS: a horizontal swipe reached Garden in the second populated case.")
finally:
    result = subprocess.run(["adb", "logcat", "-d", "-t", "200"], timeout=10,
                            stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    (EVIDENCE / "android-logcat.txt").write_bytes(result.stdout)
