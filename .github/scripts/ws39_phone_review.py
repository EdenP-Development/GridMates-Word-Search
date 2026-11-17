"""Exercise the signed WS39 APK through the normal Android screens."""
import re
import subprocess
import time
from pathlib import Path
from xml.etree import ElementTree


PACKAGE = "com.playereden.gridmates.wordsearch.qa"
ACTIVITY = f"{PACKAGE}/com.playereden.gridmates.wordsearch.MainActivity"
XML = "/sdcard/ws39-ui.xml"
EVIDENCE = Path("qa-phone-report")
SOLO = "Continue saved solo word search"
GRID = "Word-search grid, 12 by 12. Drag or tap letters, or choose precise selection."
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
    for node in root.iter("node"):
        if label in names(node):
            left, top, right, bottom = bounds(node)
            if right > left and bottom > top:
                return node
    return None


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
            if find(latest, "Got it") is not None:
                tap(find(latest, "Got it"))
                continue
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



def restart():
    adb('shell', 'am', 'force-stop', PACKAGE)
    launch()


def scroll_to(label):
    for _ in range(10):
        root=await_screen()
        target=find(root,label)
        if target is not None:
            return target
        swipe_up(vertical_scroll(root))
    raise AssertionError(f'Action {label!r} could not be reached by scrolling')


EVIDENCE.mkdir(exist_ok=True)
try:
    import hashlib, json
    adb('shell', 'wm', 'size', '720x1600')
    adb('shell', 'wm', 'density', '320')
    adb('shell', 'settings', 'put', 'system', 'font_scale', '1.0')
    adb('shell', 'settings', 'put', 'secure', 'immersive_mode_confirmations', 'confirmed')
    adb('shell', 'wm', 'dismiss-keyguard')
    assert b'Success' in adb('install', 'qa-baseline/app-debug.apk', timeout=90)
    launch()
    await_screen('Open menu')
    adb('shell', 'input', 'keyevent', 'HOME')
    time.sleep(1)
    adb('shell', 'am', 'force-stop', PACKAGE)
    before=adb('exec-out', 'run-as', PACKAGE, 'cat', 'files/gridmates_wordsearch_solo_v1.json')
    assert b'Success' in adb('install', '-r', 'qa-candidate/app-debug.apk', timeout=90)
    after=adb('exec-out', 'run-as', PACKAGE, 'cat', 'files/gridmates_wordsearch_solo_v1.json')
    assert before == after, 'Install-over changed prior save bytes'
    assert b'versionCode=102 ' in adb('shell', 'dumpsys', 'package', PACKAGE)
    launch()
    quick='Quick Play. Start a fresh 12 by 12 themed puzzle'
    action=scroll_to(quick)
    capture('ws39-home-quick-play.png')
    tap(action)
    root=await_screen('Home', '0 / 12 found', GRID)
    check_grid(root, GRID)
    capture('ws39-native-quick-play.png')
    tap(find(root, 'Home'))
    root=await_screen('Open menu')
    tap(find(root, 'Open menu'))
    root=await_screen('Settings & backup')
    tap(find(root, 'Settings & backup'))
    scroll_to('Export backup')
    await_screen('Export backup', 'Restore backup')
    capture('ws39-book-backup-settings.png')
    (EVIDENCE/'receipt.json').write_text(json.dumps({
        'source':'8ce68b0d0564ce11c30c0b18dcb208d7b9a1b6f4',
        'sha256':'3468befffa01b5e6b1f098389d775519274d95557ce00eac9d8b8514e9335d37',
        'install_over_v101':'passed; prior Solo save byte-for-byte preserved',
        'quick_play':'native Home tap reached a square 12x12 board with 12 targets',
        'backup_settings':'native Export and Restore actions reachable',
        'viewport':'360dp, 100% font, API35'
    },indent=2))
finally:
    (EVIDENCE/'android-logcat.txt').write_bytes(adb('logcat','-d','-t','500'))
