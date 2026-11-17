"""Exercise the signed WS38 APK through the normal Android screens."""
import re
import subprocess
import time
from pathlib import Path
from xml.etree import ElementTree


PACKAGE = "com.playereden.gridmates.wordsearch.qa"
ACTIVITY = f"{PACKAGE}/com.playereden.gridmates.wordsearch.MainActivity"
XML = "/sdcard/ws38-ui.xml"
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


def select_cover(title):
    root=await_screen('Open menu')
    for _ in range(3):
        selected=find(root,'Play '+title)
        if selected is not None:
            return selected
        neighbor=find(root,'Select '+title)
        if neighbor is not None:
            tap(neighbor)
        else:
            start=find(root,'Play Woodland') or find(root,'Play Space') or find(root,'Play Garden')
            left,top,right,bottom=bounds(start)
            adb('shell','input','swipe',str(right-12),str((top+bottom)//2),
                str(left+12),str((top+bottom)//2),'450')
        root=await_screen('Open menu')
    raise AssertionError(f'Cover {title!r} did not become selected')


EVIDENCE.mkdir(exist_ok=True)
try:
    adb('shell','wm','size','720x1600')
    adb('shell','wm','density','320')
    adb('shell','settings','put','system','font_scale','1.25')
    adb('shell','wm','dismiss-keyguard')
    adb('shell','settings','put','secure','immersive_mode_confirmations','confirmed')
    assert b'Success' in adb('install','qa-baseline/app-debug.apk',timeout=90)
    launch()
    open_home_action(SOLO)
    root=await_screen('Home','Hint (3)',GRID)
    tap(find(root,'Hint (3)'))
    root=await_screen('Home','Hint (2)')
    tap(find(root,'Home'))
    await_screen('Open menu')
    assert b'Success' in adb('install','-r','qa-candidate/app-debug.apk',timeout=90)
    assert b'versionCode=101 ' in adb('shell','dumpsys','package',PACKAGE)
    launch()
    open_home_action(SOLO)
    root=await_screen('Home','Hint (2)',GRID)
    tap(find(root,'Home'))
    print('PASS: signed v101 installs over v100 and retains the saved Solo hint.',flush=True)

    garden=select_cover('Garden')
    capture('garden-home.png')
    left,top,right,bottom=bounds(garden)
    x,y=str((left+right)//2),str((top+bottom)//2)
    adb('shell','input','motionevent','DOWN',x,y)
    time.sleep(.3)
    capture('garden-pressed.png')
    adb('shell','input','motionevent','UP',x,y)
    root=await_screen('Back to book','Garden','0 of 12 words found')
    capture('garden-book-page.png')
    tap(find(root,'Back to book'))
    space=select_cover('Space')
    tap(space)
    root=await_screen('Back to book','Space','0 of 12 words found')
    capture('space-book-page.png')
    tap(find(root,'Back to book'))
    print('PASS: transparent Garden press and both warm-paper book pages captured through Home.',flush=True)

    open_home_action('Open Together on local Wi-Fi')
    root=await_screen('Play Together','Co-op','Race','Duel','Host Co-op game')
    capture('together-coop.png')
    tap(find(root,'Race'))
    await_screen('Host Shared Board Race')
    tap(scroll_to('Duel'))
    await_screen('Host Speed Duel')
    tap(scroll_to('Co-op'))
    await_screen('Host Co-op game')
    tap(scroll_to('Join nearby game'))
    scroll_to('Close game finder')
    capture('together-join.png')
    adb('shell','input','keyevent','4')
    await_screen('Open menu')
    print('PASS: illustrated Together choices and Join finder remain reachable.',flush=True)

    # A deterministic, genuinely generated v3 Garden game in this disposable
    # emulator. The owner's phone and Library progress are outside this runner.
    seed=int(Path('core/build/ws38-garden-seed.txt').read_text())
    import json
    profile={'schema':5,'active':{'theme':'GARDEN','difficulty':'HARD','seed':seed,
        'board':'BOOK_GIANT','challenge':'CLASSIC','found':[],'hints':{},'seconds':0}}
    Path('qa-garden.json').write_text(json.dumps(profile))
    adb('shell','am','force-stop',PACKAGE)
    adb('push','qa-garden.json','/data/local/tmp/ws38-garden.json')
    adb('shell',f"run-as {PACKAGE} sh -c 'cat /data/local/tmp/ws38-garden.json > files/gridmates_wordsearch_solo_v1.json'")
    launch()
    open_home_action(SOLO)
    large_grid='Word-search grid, 30 by 30. Drag or tap letters, or choose precise selection.'
    root=await_screen('Garden','Hint (8)','Words to find · 44 left',large_grid,
                      '○ PHOTOSYNTHESIS','Map')
    target=bounds(find(root,'○ PHOTOSYNTHESIS'))
    grid=check_grid(root,large_grid)
    column_budget=(grid[2]-grid[0]-12)/2-32
    assert target[2]-target[0]>column_budget, 'Long target should receive enough width for its full word'
    capture('garden-long-words.png')
    swipe_up(vertical_scroll(root))
    scrolled=await_screen(large_grid,'Words to find · 44 left')
    assert check_grid(scrolled,large_grid)==grid
    print('PASS: actual 30 × 30 Garden target PHOTOSYNTHESIS uses a full-width row at 125% text.',flush=True)

    adb('shell','settings','put','system','font_scale','2.0')
    restart()
    open_home_action(SOLO)
    root=await_screen('Options',large_grid,'Words to find · 44 left')
    assert words(root), 'Large-text word list must still display a target'
    capture('garden-large-text.png')
    tap(find(root,'Options'))
    menu=await_screen('Puzzle tools')
    tap(find(menu,'Puzzle tools'))
    tools=await_screen('Select by row and column','Board map','Done')
    capture('garden-tools-large-text.png')
    tap(find(tools,'Done'))
    root=await_screen('Home',large_grid)
    tap(find(root,'Home'))
    open_home_action('Open Together on local Wi-Fi')
    tap(scroll_to('Race'))
    scroll_to('Host Shared Board Race')
    capture('together-large-text.png')
    print('PASS: 200% text keeps large-board tools and Together mode actions reachable.',flush=True)
finally:
    try:
        capture('final-screen.png')
        (EVIDENCE/'final-ui.xml').write_bytes(adb('exec-out','cat',XML))
    except (subprocess.SubprocessError,OSError):
        pass
    report=subprocess.run(['adb','logcat','-d','-t','180'],timeout=10,
                          stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
    (EVIDENCE/'android-logcat.txt').write_bytes(report.stdout)
