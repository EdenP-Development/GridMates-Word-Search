"""Exercise the two archived QA APKs on a fresh API 35 emulator."""

import hashlib
import json
import re
import subprocess
import time
from xml.etree import ElementTree

PACKAGE = "com.playereden.gridmates.wordsearch.qa"
ACTIVITY = f"{PACKAGE}/com.playereden.gridmates.wordsearch.MainActivity"
SAVE = "files/gridmates_wordsearch_solo_v1.json"
UI_DUMP = "/sdcard/ws31-upgrade-window.xml"


def adb(*args):
    return subprocess.check_output(["adb", *args], timeout=50)


def shell(*args):
    return adb("shell", *args).decode().strip()


def require(condition, message):
    if not condition:
        raise RuntimeError(message)


def package_state(version):
    info = shell("dumpsys", "package", PACKAGE)
    require(re.search(rf"\bversionCode={version}\b", info), f"Expected versionCode {version}")
    installed = re.search(r"\bfirstInstallTime=(.+)", info)
    require(installed, "Could not read the QA package installation time")
    return installed.group(1).strip()


def launch():
    # Activity Manager can time out waiting for the first frame on a cold runner.
    # The subsequent UI checks verify the launched Activity by its visible actions.
    result = shell("am", "start", "-n", ACTIVITY)
    require("Starting: Intent" in result and "Error" not in result, result)


def nodes():
    result = shell("uiautomator", "dump", UI_DUMP)
    if "dumped to" not in result:
        return []
    root = ElementTree.fromstring(adb("exec-out", "cat", UI_DUMP))
    return list(root.iter("node"))


def find_label(*labels, scroll=False):
    expected = {label.casefold() for label in labels}
    size = shell("wm", "size")
    sizes = re.findall(r"(\d+)x(\d+)", size)
    require(sizes, f"Could not read emulator size: {size}")
    width, height = map(int, sizes[-1])
    seen = set()
    for attempt in range(12):
        visible = nodes()
        for node in visible:
            candidates = (node.get("text", ""), node.get("content-desc", ""))
            seen.update(value for value in candidates if value)
            if not expected.intersection(value.casefold() for value in candidates):
                continue
            bounds = re.findall(r"\d+", node.get("bounds", ""))
            if len(bounds) == 4 and node.get("enabled") == "true":
                left, top, right, bottom = map(int, bounds)
                if right > left and bottom > top:
                    return (left + right) // 2, (top + bottom) // 2
        if scroll and any(node.get("package") == PACKAGE for node in visible) and attempt < 3:
            shell("input", "swipe", str(width // 2), str(3 * height // 4),
                  str(width // 2), str(height // 3), "350")
        time.sleep(0.8)
    raise RuntimeError(f"Missing {labels}. Visible labels: {sorted(seen)}")


def tap(*labels):
    x, y = find_label(*labels, scroll=True)
    shell("input", "tap", str(x), str(y))
    time.sleep(0.8)


def back():
    shell("input", "keyevent", "KEYCODE_BACK")
    time.sleep(0.8)


def saved_bytes():
    return adb("exec-out", "run-as", PACKAGE, "cat", SAVE)


def main():
    require(shell("getprop", "ro.kernel.qemu") == "1", "Expected a disposable Android emulator")
    shell("wm", "dismiss-keyguard")
    shell("settings", "put", "secure", "immersive_mode_confirmations", "confirmed")
    print(adb("install", "qa-archives/v92/app-debug.apk").decode().strip(), flush=True)
    first_install = package_state(92)
    launch()

    tap("My Puzzles")
    tap("New Solo")
    tap("Start puzzle")
    back()
    tap("Daily Puzzle", "Open Daily Puzzle")
    tap("Play today")
    back()
    back()
    shell("am", "force-stop", PACKAGE)

    original = saved_bytes()
    profile = json.loads(original)
    require(profile["schema"] == 5, "v92 did not create a schema-5 profile")
    require(profile["active"], "v92 did not save the active Solo puzzle")
    require(len(profile["pausedSolo"]) == 1, "v92 did not save one paused Solo puzzle")
    require(len(profile["daily"]) == 1, "v92 did not save today's Daily puzzle")
    print(f"Seeded v92 Solo, paused Solo, and Daily. Save SHA-256 {hashlib.sha256(original).hexdigest()}",
          flush=True)

    print(adb("install", "-r", "qa-archives/v93/app-debug.apk").decode().strip(), flush=True)
    require(package_state(93) == first_install, "Android reported a fresh install rather than an update")
    require(saved_bytes() == original, "The APK update changed the on-disk save")
    launch()
    tap("My Puzzles")
    find_label("Paused · 1 / 24", scroll=True)
    shell("am", "force-stop", PACKAGE)
    require(saved_bytes() == original, "Opening the saved-puzzle journal changed the profile")
    print("PASS: v92 → PR #139 v93 install-over preserved the save byte for byte and reopened My Puzzles.",
          flush=True)


if __name__ == "__main__":
    main()
