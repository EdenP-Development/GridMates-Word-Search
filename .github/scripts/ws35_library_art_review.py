"""Exercise the signed v98 Library on a fresh Android 15 emulator."""
import re
import subprocess
import time
from pathlib import Path
from xml.etree import ElementTree

PACKAGE = "com.playereden.gridmates.wordsearch.qa"
ACTIVITY = f"{PACKAGE}/com.playereden.gridmates.wordsearch.MainActivity"
XML = "/sdcard/ws35-ui.xml"
EVIDENCE = Path("qa-cabinet-report")


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
    assert b"Success" in adb("install", "qa-baseline/app-debug.apk", timeout=90)
    adb("shell", "wm", "dismiss-keyguard")
    adb("shell", "settings", "put", "secure", "immersive_mode_confirmations", "confirmed")
    adb("shell", "settings", "put", "system", "font_scale", "1.0")
    assert b"Success" in adb("install", "-r", "qa-candidate/app-debug.apk", timeout=90)
    assert b"versionCode=98 " in adb("shell", "dumpsys", "package", PACKAGE)
    print("PASS: replacement install kept the package and accepted the v97 signer.")
    start_at(720, 1600)
    open_library()
    time.sleep(1)
    capture("v98-library.png")
    shelf = await_nodes("Your Library", "Open Woodland", "Open Space", "Open Garden")
    capture("v98-library.png")
    woodland = bounds(find(shelf, "Open Woodland"))
    space = bounds(find(shelf, "Open Space"))
    garden = bounds(find(shelf, "Open Garden"))
    assert woodland[2] <= space[0], (woodland, space)
    assert max(woodland[3], space[3]) < garden[1], (woodland, space, garden)
    print("PASS: v98 installed over v97; the three real books have nonoverlapping targets on both shelves.")

    tap(find(shelf, "Open Garden"))
    book = await_nodes("Garden", "View pages")
    tap(find(book, "View pages"))
    await_nodes("Book progress")
    capture("v98-garden-pages.png")
    print("PASS: Garden opens its real page list from the lower shelf.")

    adb("shell", "settings", "put", "system", "font_scale", "2.0")
    assert adb("shell", "settings", "get", "system", "font_scale").strip() == b"2.0"
    start_at(640, 1440)
    open_library()
    await_nodes("Your Library", "Open Woodland", "Open Space", "Open Garden")
    capture("v98-library-320dp-200-percent.png")
    print("PASS: the Library heading and all three book targets are available at 320dp with 200% text.")

    # A short 260dp phone fits two real books before the swipe.
    start_at(520, 900)
    open_library()
    await_nodes("Open Woodland", "Open Space", "Next bookcase")
    capture("v98-cabinet-1-260dp.png")
    adb("shell", "input", "swipe", "393", "475", "129", "475", "350")
    second = await_nodes("Open Garden", "Bookcase 2 of 2")
    assert find(second, "Open Woodland") is None
    capture("v98-cabinet-2-260dp.png")
    print("PASS: swiping the short layout reaches the final real book.")
finally:
    try:
        result = subprocess.run(["adb", "logcat", "-d", "-t", "200"], timeout=8,
                                stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        (EVIDENCE / "android-logcat.txt").write_bytes(result.stdout)
    except subprocess.TimeoutExpired:
        print("Runner lost ADB while collecting the final log.")
