"""Open the illustrated journal after an update from a real v93 variant."""

import json
import sys
from pathlib import Path

sys.path.insert(0, "qa-harness/.github/scripts")

from ws31_archived_apk_upgrade import (
    PACKAGE,
    adb,
    back,
    find_label,
    launch,
    nodes,
    package_state,
    require,
    saved_bytes,
    shell,
    tap,
)


def open_journal():
    launcher_alert = "Pixel Launcher isn't responding"
    find_label("My Puzzles", "Open menu", launcher_alert, scroll=True)
    visible = nodes()
    if any(node.get("text") == launcher_alert for node in visible):
        tap("Close app")
        launch()
        find_label("My Puzzles", "Open menu", scroll=True)
        visible = nodes()
    if any(node.get("content-desc") == "Open menu" for node in visible):
        tap("Open menu")
    tap("My Puzzles")


def capture(name):
    screenshot = adb("exec-out", "screencap", "-p")
    require(screenshot.startswith(b"\x89PNG\r\n\x1a\n"), f"Invalid screenshot: {name}")
    Path("qa-report", name + ".png").write_bytes(screenshot)


def main():
    baseline = sys.argv[1]
    require(shell("getprop", "ro.kernel.qemu") == "1", "Expected a disposable Android emulator")
    shell("wm", "dismiss-keyguard")
    shell("settings", "put", "secure", "immersive_mode_confirmations", "confirmed")
    Path("qa-report").mkdir(exist_ok=True)

    print(adb("install", "qa-archives/v93/app-debug.apk").decode().strip(), flush=True)
    first_install = package_state(93)
    launch()
    open_journal()
    tap("New Solo")
    tap("Start puzzle")
    back()
    tap("Daily Puzzle", "Open Daily Puzzle")
    tap("Play today")
    back()
    back()

    open_journal()
    find_label("Currently playing")
    capture("01-v93-my-puzzles")
    shell("am", "force-stop", PACKAGE)
    original = saved_bytes()
    profile = json.loads(original)
    require(profile["schema"] == 5, "The baseline save was not schema 5")
    require(len(profile["pausedSolo"]) == 1, "The v93 build did not save one paused Solo puzzle")
    require(len(profile["daily"]) == 1, "The v93 build did not save today's Daily puzzle")

    print(adb("install", "-r", "qa-archives/v94/app-debug.apk").decode().strip(), flush=True)
    require(package_state(94) == first_install, "Android reported a fresh install rather than an update")
    require(saved_bytes() == original, "The update changed the on-disk profile")

    launch()
    open_journal()
    find_label("Currently playing")
    capture("02-ws32-my-puzzles")
    shell("am", "force-stop", PACKAGE)
    require(saved_bytes() == original, "Opening My Puzzles changed the profile")

    # 720 pixels at 360 dpi gives a 320dp-wide emulator viewport.
    shell("wm", "size", "720x1440")
    shell("wm", "density", "360")
    shell("settings", "put", "system", "font_scale", "2.0")
    require("Override size: 720x1440" in shell("wm", "size"), "Display size override failed")
    require("Override density: 360" in shell("wm", "density"), "Display density override failed")
    require(shell("settings", "get", "system", "font_scale") == "2.0", "Font scale override failed")
    launch()
    open_journal()
    find_label("Continue current puzzle", scroll=True)
    capture("03-ws32-large-text-active")

    find_label("Resume", scroll=True)
    find_label("Remove", scroll=True)
    capture("04-ws32-large-text-paused")
    tap("Remove")
    find_label("Remove saved puzzle?")
    tap("Keep puzzle")
    require(saved_bytes() == original, "Canceling removal changed the profile")

    tap("Resume")
    resumed = json.loads(saved_bytes())
    require(resumed["active"]["seed"] == profile["pausedSolo"][0]["seed"], "Resume opened the wrong puzzle")
    require(len(resumed["pausedSolo"]) == 1 and resumed["pausedSolo"][0]["seed"] == profile["active"]["seed"],
            "Resuming did not keep the previous active puzzle")
    require(resumed["daily"] == profile["daily"], "Resuming changed Daily progress")
    print(
        f"PASS {baseline} v93 -> v94: install-over preserved the save, "
        "and 320dp large-text actions kept and resumed the paused puzzle.",
        flush=True,
    )


if __name__ == "__main__":
    main()
