"""Exercise the signed WS37 APK through the normal Android screens."""
import re
import subprocess
import time
from pathlib import Path
from xml.etree import ElementTree


PACKAGE = "com.playereden.gridmates.wordsearch.qa"
ACTIVITY = f"{PACKAGE}/com.playereden.gridmates.wordsearch.MainActivity"
XML = "/sdcard/ws37-ui.xml"
EVIDENCE = Path("qa-phone-report")
SOLO = "Continue saved solo word search"
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



def check_grid(root, label):
    node = find(root, label)
    left, top, right, bottom = bounds(node)
    assert right - left == bottom - top, "The fit-to-screen board should be square"
    return left, top, right, bottom


def trace_daily(root, date):
    fixtures = Path("core/build/ws37-daily-traces.txt").read_text().splitlines()
    traces = [line.split() for line in fixtures if line.startswith(date + " ")]
    assert traces, f"No fixture for Daily date {date}"
    cells = int(traces[0][1])
    grid_name = (
        f"Word-search grid, {cells} by {cells}. "
        "Drag or tap letters, or choose precise selection."
    )
    left, top, right, bottom = check_grid(root, grid_name)
    capture("daily.png")
    for _, _, start_row, start_col, end_row, end_col in traces:
        def screen_point(row, col):
            x = round(left + (int(col) + 0.5) * (right - left) / cells)
            y = round(top + (int(row) + 0.5) * (bottom - top) / cells)
            return str(x), str(y)
        adb("shell", "input", "swipe",
            *screen_point(start_row, start_col), *screen_point(end_row, end_col), "430")
    count = len(traces)
    return await_screen("Daily complete!", f"{count} / {count} found", "Share results",
                        "Review completed board")


def restart():
    adb("shell", "am", "force-stop", PACKAGE)
    launch()


EVIDENCE.mkdir(exist_ok=True)
try:
    adb("shell", "wm", "size", "880x1440")
    adb("shell", "wm", "density", "320")
    adb("shell", "settings", "put", "system", "font_scale", "1.0")
    adb("shell", "wm", "dismiss-keyguard")
    adb("shell", "settings", "put", "secure", "immersive_mode_confirmations", "confirmed")
    assert b"Success" in adb("install", "qa-baseline/app-debug.apk", timeout=90)
    launch()
    open_home_action(SOLO)
    before_upgrade = await_screen(GRID, "Hint (3)")
    tap(find(before_upgrade, "Hint (3)"))
    used_hint = await_screen("Hint (2)", "Home")
    tap(find(used_hint, "Home"))
    await_screen("Open menu")

    assert b"Success" in adb("install", "-r", "qa-candidate/app-debug.apk", timeout=90)
    assert b"versionCode=100 " in adb("shell", "dumpsys", "package", PACKAGE)
    launch()
    open_home_action(SOLO)
    controls = ("Select a word without dragging", "Fit entire puzzle board",
                "Zoom in", "Zoom out")
    board = await_screen(*controls, GRID, "Hint (2)", "Words to find · 16 left", HELP)
    print("PASS: v100 installs over v99 and retains its saved Solo hint.", flush=True)
    board_bounds = check_grid(board, GRID)
    before = words(board)
    assert before
    capture("solo.png")
    swipe_up(vertical_scroll(board))
    after = await_screen(GRID, "Words to find · 16 left",
                         predicate=lambda root: bool(words(root) - before))
    assert check_grid(after, GRID) == board_bounds
    print("PASS: Solo word list scrolls with its board fixed.", flush=True)

    tap(find(after, "Options"))
    menu = await_screen("New puzzle")
    tap(find(menu, "New puzzle"))
    setup = await_screen("New puzzle", "Cancel", "Theme", "Board size", "Start puzzle")
    capture("setup.png")
    tap(find(setup, "Cancel"))
    existing_solo = await_screen("Home", GRID, "Hint (2)")
    tap(find(existing_solo, "Home"))
    print("PASS: returning from setup preserves the active board.", flush=True)

    open_home_action("Open Daily Puzzle")
    daily = await_screen("Daily Word Search", "Play today")
    date = next(label[8:] for node in daily.iter("node") for label in names(node)
                if label.startswith("Today · "))
    capture("daily-journal.png")
    tap(find(daily, "Play today"))
    root = await_screen("Daily Puzzle", "Options", "Fit entire puzzle board")
    complete = trace_daily(root, date)
    capture("daily-complete.png")
    tap(find(complete, "Review completed board"))
    await_screen("Words to find · 0 left")
    restart()
    open_home_action("Open Daily Puzzle")
    await_screen("Completed today", "Review")
    print("PASS: played every Daily word by touch, reviewed the result, and verified it after restart.",
          flush=True)

    adb("shell", "wm", "size", "640x1280")
    adb("shell", "settings", "put", "system", "font_scale", "2.0")
    restart()
    open_home_action(SOLO)
    enlarged = await_screen("Home", "Options", GRID, "Hint (2)", "Words to find · 16 left")
    capture("solo-large-text.png")
    assert words(enlarged), "A word target should remain visible at 200% font scale"
    tap(find(enlarged, "Hint (2)"))
    hinted = await_screen("Hint (1)", GRID,
                          predicate=lambda root: any("starts at row" in text
                              for node in root.iter("node") for text in names(node)))
    notice = next(node for node in hinted.iter("node")
                  if any("starts at row" in text for text in names(node)))
    tap(notice)
    enlarged = await_screen("Words to find · 16 left", GRID, "Hint (1)")
    assert words(enlarged), "Dismissed hints should leave the word list reachable"
    tap(find(enlarged, "Options"))
    menu = await_screen("Puzzle tools")
    tap(find(menu, "Puzzle tools"))
    dialog = await_screen("Select by row and column", "Zoom in", "Fit", "Done")
    capture("puzzle-tools-large-text.png")
    tap(find(dialog, "Done"))
    await_screen("Options", GRID, "Words to find · 16 left")
    print("PASS: 320 × 640 dp at 200% keeps the board, list, and precision tools reachable.",
          flush=True)
finally:
    try:
        capture("final-screen.png")
        (EVIDENCE / "final-ui.xml").write_bytes(adb("exec-out", "cat", XML))
    except (subprocess.SubprocessError, OSError):
        pass
    report = subprocess.run(["adb", "logcat", "-d", "-t", "180"], timeout=10,
                            stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    (EVIDENCE / "android-logcat.txt").write_bytes(report.stdout)
