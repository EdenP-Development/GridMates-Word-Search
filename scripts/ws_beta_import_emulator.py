"""Exercise Android's document picker with archived QA and signed beta APKs."""

import hashlib
import json
from pathlib import Path
import re
import subprocess
import time
import xml.etree.ElementTree as ET


QA = "com.playereden.gridmates.wordsearch.qa"
BETA = "com.playereden.gridmates.wordsearch.beta"
ACTIVITY = "com.playereden.gridmates.wordsearch.MainActivity"
RUNNER = f"{QA}.test/androidx.test.runner.AndroidJUnitRunner"
QA_DIR = Path("qa-v120")
REPORT = Path("beta-import-report")
SIGNED = Path("beta-candidate") / "GridMates-WordSearch-Beta1.apk"
TEST_APK = "app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"
BACKUP = "GridMates-WordSearch-Backup.json"
QA_BACKUP = f"/sdcard/Download/{BACKUP}"
BETA_BACKUP = "/sdcard/Download/WordSearch-Beta1-RoundTrip.json"
UI_DUMP = "/sdcard/wordsearch-rehearsal.xml"
JOURNALS = (
    "gridmates_wordsearch_solo_v1.json",
    "gridmates_wordsearch_ws73_v116_qa_progress_v1.dat",
    "gridmates_wordsearch_ws73_v116_qa_sessions_v1.dat",
)


def adb(*args):
    return subprocess.check_output(["adb", *args], timeout=120)


def hierarchy():
    result = adb("shell", "uiautomator", "dump", UI_DUMP)
    if b"dumped to" not in result:
        raise RuntimeError(result.decode(errors="replace"))
    return ET.fromstring(adb("exec-out", "cat", UI_DUMP))


def has_label(node, label):
    return label in (node.get("text"), node.get("content-desc"))


def wait_for_node(predicate, description):
    deadline = time.monotonic() + 36
    while time.monotonic() < deadline:
        try:
            tree = hierarchy()
        except (subprocess.CalledProcessError, RuntimeError, ET.ParseError):
            continue
        (REPORT / "last-window.xml").write_bytes(ET.tostring(tree))
        match = next((node for node in tree.iter("node") if predicate(node)), None)
        if match is not None:
            return match
    raise AssertionError(f"Timed out waiting for {description}")


def tap(node):
    left, top, right, bottom = map(int, re.findall(r"\d+", node.get("bounds", "")))
    assert right > left and bottom > top, node.attrib
    adb("shell", "input", "tap", str((left + right) // 2), str((top + bottom) // 2))


def ui(label):
    return wait_for_node(lambda node: has_label(node, label), repr(label))


def open_settings(package):
    adb("shell", "am", "force-stop", package)
    adb("shell", "am", "start", "-W", "-n", f"{package}/{ACTIVITY}")
    tap(ui("Open menu"))
    tap(ui("Settings & backup"))
    ui("Export backup")


def picker_downloads():
    # DocumentsUI uses its own package and exposes the location drawer.
    drawer = wait_for_node(
        lambda node: node.get("package", "").endswith("documentsui")
        and node.get("content-desc") in ("Show roots", "Open navigation drawer"),
        "DocumentsUI location drawer",
    )
    tap(drawer)
    tap(wait_for_node(
        lambda node: node.get("package", "").endswith("documentsui")
        and node.get("text") == "Downloads",
        "Downloads location",
    ))


def export_backup(output_path, filename=None):
    tap(ui("Export backup"))
    picker_downloads()
    if filename:
        tap(wait_for_node(
            lambda node: node.get("package", "").endswith("documentsui")
            and node.get("class") == "android.widget.EditText",
            "backup filename",
        ))
        adb("shell", "input", "keycombination", "KEYCODE_CTRL_LEFT", "KEYCODE_A")
        adb("shell", "input", "text", filename)
    tap(wait_for_node(
        lambda node: node.get("package", "").endswith("documentsui")
        and node.get("text", "").casefold() == "save",
        "DocumentsUI Save button",
    ))
    ui("Backup exported to your chosen location.")
    return adb("exec-out", "cat", output_path)


def open_restore_preview():
    tap(ui("Restore backup"))
    picker_downloads()
    tap(ui(BACKUP))
    ui("Replace saved progress?")
    wait_for_node(lambda node: node.get("text", "").startswith("Profile: V120 Test Reader · "),
                  "source profile in the preview")
    wait_for_node(lambda node: "Preview book saves stay in the QA app." in node.get("text", ""),
                  "book-save scope in the preview")


def qa_journal_hashes():
    return {
        name: hashlib.sha256(adb("exec-out", "run-as", QA, "cat", f"files/{name}")).hexdigest()
        for name in JOURNALS
    }


def payload(raw):
    envelope = json.loads(raw)
    assert envelope["format"] == "gridmates-wordsearch-backup" and envelope["version"] == 1
    assert hashlib.sha256(envelope["payload"].encode()).hexdigest() == envelope["sha256"]
    return json.loads(envelope["payload"])


def instrument(test_class, count):
    name = f"com.playereden.gridmates.wordsearch.{test_class}"
    args = ("shell", "am", "instrument", "-w", "-r", "-e", "class", name, RUNNER)
    result = adb(*args).decode()
    (REPORT / f"{test_class}.txt").write_text(result)
    tests = "test" if count == 1 else "tests"
    assert f"OK ({count} {tests})" in result, result


def main():
    REPORT.mkdir(exist_ok=True)
    adb("shell", "settings", "put", "system", "font_scale", "1.0")
    adb("install", str(QA_DIR / "app-debug.apk"))
    adb("install", "-t", TEST_APK)
    instrument("WsBetaV120BackupSeedAndroidTest", 1)

    open_settings(QA)
    source = payload(export_backup(QA_BACKUP))
    assert source["books"]["progress"] and source["books"]["sessions"]
    assert source["boardFont"] == "plex" and len(source["solo"]["bookAwards"]) == 1
    before = qa_journal_hashes()

    adb("install", str(SIGNED))
    open_settings(BETA)
    ui("0 Solo · 0 Daily · 0 Races · 0 Duels")
    open_restore_preview()
    tap(ui("Keep current"))
    ui("0 Solo · 0 Daily · 0 Races · 0 Duels")
    open_restore_preview()
    tap(wait_for_node(
        lambda node: node.get("class") == "android.widget.Button"
        and has_label(node, "Restore backup"),
        "dialog confirmation",
    ))
    ui("Backup restored. Your saved progress is ready.")

    open_settings(BETA)
    ui("1 Solo · 1 Daily · 0 Races · 0 Duels")
    restored = payload(export_backup(BETA_BACKUP, Path(BETA_BACKUP).name))
    assert restored["solo"] == source["solo"], "Portable profile or puzzle progress differs after restart"
    assert restored["boardFont"] == source["boardFont"], "Equipped board font changed"
    assert restored["books"] is None
    assert qa_journal_hashes() == before, "v120 source journals changed during beta restore"

    adb("install", "-r", "app/build/outputs/apk/debug/app-debug.apk")
    instrument("GameBackupAndroidTest", 7)
    summary = (
        "PASS: v120 exported from Settings; beta preview and cancel passed; restore persisted "
        "after restart; portable profile, puzzle and reward data and board font match; "
        "v120 Solo, book progress and book session journals are byte-identical; "
        "seven Android backup regression tests passed."
    )
    (REPORT / "result.txt").write_text(summary + "\n")
    print(summary)


if __name__ == "__main__":
    main()
