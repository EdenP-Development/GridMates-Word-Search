#!/usr/bin/env bash
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
runner="$package.test/androidx.test.runner.AndroidJUnitRunner"
tests=com.playereden.gridmates.wordsearch
report=qa-ws70-home-library
mkdir -p "$report"
trap 'adb logcat -d -s AndroidRuntime:E > "$report/logcat.txt" || true' EXIT

run_test() {
  local test="$1"
  local output="$2"
  shift 2
  adb shell am instrument -w -r -e class "$tests.$test" "$@" "$runner"     | tee "$report/$output.txt"
  grep -Eq '^OK \(1 test\)' "$report/$output.txt"
}

adb shell wm density 440
adb shell wm size 1080x2200
adb shell settings put system font_scale 1.0
adb install -r qa-ws70-baseline/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
run_test Ws67V110SeedAndroidTest v112-seed

journals=(gridmates_wordsearch_ws66_v110_qa_progress_v1.dat gridmates_wordsearch_ws66_v110_qa_sessions_v1.dat)
for journal in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$journal" > "$report/$journal"
  test -s "$report/$journal"
done
adb install -r app/build/outputs/apk/debug/app-debug.apk
for journal in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$journal" | cmp "$report/$journal" -
done
echo 'Signed v112 to v113 upgrade kept both book journals byte-identical.'   | tee "$report/install-over.txt"

check_layout() {
  local width="$1"
  local failed=0
  run_test Ws70HomeLibraryAndroidTest "v113-${width}dp" -e expectedWidthDp "$width" || failed=1
  for screen in home home-scrolled library; do
    local capture="ws70-$width-$screen.png"
    if ! adb exec-out run-as "$package" cat "files/$capture" > "$report/$capture" ||
       [ ! -s "$report/$capture" ]; then
      rm -f -- "$report/$capture"
      failed=1
    fi
  done
  return "$failed"
}
failed=0
check_layout 393 || failed=1
run_test 'QaActualBookcaseAndroidTest#newBooksReplacePlaceholdersInShelfOrder' growing-shelves || failed=1
adb shell wm size 880x1760
check_layout 320 || failed=1
exit "$failed"
