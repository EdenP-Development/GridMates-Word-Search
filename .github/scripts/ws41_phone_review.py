"""Exercise the signed WS41 APK through the normal Android screens."""
import re
import subprocess
import time
from pathlib import Path
from xml.etree import ElementTree


PACKAGE = "com.playereden.gridmates.wordsearch.qa"
ACTIVITY = f"{PACKAGE}/com.playereden.gridmates.wordsearch.MainActivity"
XML = "/sdcard/ws41-ui.xml"
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
        if any(label == value or label in value.splitlines() for value in names(node)):
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
    import hashlib, json, base64, struct
    adb('shell','wm','size','720x1600')
    adb('shell','wm','density','320')
    adb('shell','settings','put','system','font_scale','1.0')
    adb('shell','settings','put','secure','immersive_mode_confirmations','confirmed')
    adb('shell','wm','dismiss-keyguard')
    assert b'Success' in adb('install','qa-baseline/app-debug.apk',timeout=90)
    launch(); await_screen('Open menu')
    adb('shell','am','force-stop',PACKAGE)
    before=adb('exec-out','run-as',PACKAGE,'cat','files/gridmates_wordsearch_solo_v1.json')
    assert b'Success' in adb('install','-r','app/build/outputs/apk/debug/app-debug.apk',timeout=90)
    after=adb('exec-out','run-as',PACKAGE,'cat','files/gridmates_wordsearch_solo_v1.json')
    assert before==after, 'Install-over changed save bytes'
    assert b'versionCode=103 ' in adb('shell','dumpsys','package',PACKAGE)
    launch(); await_screen('Open menu')
    open_home_action('Quick Play. Choose a size for a fresh puzzle')
    root=await_screen('Medium · 15×15','16 words · Recommended')
    capture('ws41-native-size-chooser.png')
    tap(find(root,'Medium · 15×15'))
    grid_label='Word-search grid, 15 by 15. Drag or tap letters, or choose precise selection.'
    root=await_screen('Home','0 / 16 found',grid_label)
    left,top,right,bottom=check_grid(root,grid_label)
    save=json.loads(adb('exec-out','run-as',PACKAGE,'cat','files/gridmates_wordsearch_solo_v1.json'))
    encoded=save['active']['frozen']; data=base64.b64decode(encoded)
    assert hashlib.sha256(data[:-32]).digest()==data[-32:]
    cursor=0
    def integer():
        global cursor
        n=struct.unpack_from('>i',data,cursor)[0];cursor+=4;return n
    def utf():
        global cursor
        n=struct.unpack_from('>H',data,cursor)[0];cursor+=2
        value=data[cursor:cursor+n].decode('utf-8');cursor+=n;return value
    assert integer()==0x474d5735 and integer()==1
    for _ in range(5): utf()
    cursor+=8; utf(); utf()
    size=integer(); rows=[utf() for _ in range(size)]
    targets=[]
    for _ in range(integer()):
        word_id= utf();answer=utf();display=utf();topic=utf()
        coords=[(integer(),integer()) for _ in range(integer())]
        targets.append((word_id,coords))
    def trace(coords):
        (r,c),(er,ec)=coords[0],coords[-1]
        x=lambda v: str(round(left+(v+0.5)*(right-left)/size))
        y=lambda v: str(round(top+(v+0.5)*(bottom-top)/size))
        adb('shell','input','swipe',x(c),y(r),x(ec),y(er),'350')
    trace(targets[0][1]); await_screen('1 / 16 found',grid_label)
    capture('ws41-native-mixed-puzzle.png')
    tap(find(await_screen('Home'),'Home'))
    restart(); open_home_action(SOLO)
    root=await_screen('1 / 16 found',grid_label)
    restored=json.loads(adb('exec-out','run-as',PACKAGE,'cat','files/gridmates_wordsearch_solo_v1.json'))
    assert restored['active']['frozen']==encoded and targets[0][0] in restored['active']['found']
    left,top,right,bottom=check_grid(root,grid_label)
    for i,(_,coords) in enumerate(targets[1:],2):
        trace(coords)
        if i<16:await_screen(f'{i} / 16 found')
    scroll_to('Play another puzzle'); await_screen('Puzzle complete!','Change size')
    capture('ws41-native-completion.png')
    tap(scroll_to('Play another puzzle'))
    await_screen('0 / 16 found',grid_label)
    fresh=json.loads(adb('exec-out','run-as',PACKAGE,'cat','files/gridmates_wordsearch_solo_v1.json'))
    assert fresh['active']['frozen']!=encoded and len(fresh['results'])==1
    tap(find(await_screen('Home'),'Home'))
    root=await_screen('Open menu');tap(find(root,'Open menu'))
    tap(find(await_screen('Settings & backup'),'Settings & backup'))
    scroll_to('Export backup');capture('ws41-native-backup.png')
    (EVIDENCE/'receipt.json').write_text(json.dumps({
        'source':__import__('os').environ.get('WS41_SOURCE'),
        'install_over_v102':'passed, prior bytes preserved',
        'native_flow':'Home > Medium > 16 finger traces > restart/resume > completion > same-size Another',
        'backup_actions':'reachable','viewport':'360dp, 100%, API35'},indent=2))
finally:
    (EVIDENCE/'android-logcat.txt').write_bytes(adb('logcat','-d','-t','500'))
