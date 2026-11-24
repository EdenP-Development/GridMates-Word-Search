#!/usr/bin/env bash
# Verify the actual signed install-over and both portrait sizes on one emulator.
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
runner="$package.test/androidx.test.runner.AndroidJUnitRunner"
tests=com.playereden.gridmates.wordsearch
layout_test="$tests.Ws69LibraryPolishAndroidTest"
report=qa-ws69-library-polish
mkdir -p "$report"
trap 'adb logcat -d -s WS69_LAYOUT:I AndroidRuntime:E > "$report/layout-logcat.txt" || true' EXIT

adb shell wm density 440
adb shell wm size 1080x2280
adb shell settings put system font_scale 1.0
adb install -r qa-ws69-baseline/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

# This test writes an original Space bookmark and a found Solar System word.
adb shell am instrument -w -r -e class "$tests.Ws67V110SeedAndroidTest" "$runner" \
  | tee "$report/v111-seed.txt"
grep -Eq '^OK \(1 test\)' "$report/v111-seed.txt"
adb shell am instrument -w -r -e class "$layout_test#captureV111Library" "$runner" \
  | tee "$report/v111-capture.txt"
grep -Eq '^OK \(1 test\)' "$report/v111-capture.txt"
adb exec-out run-as "$package" cat files/ws69-v111-library.png > "$report/v111-library.png"
test -s "$report/v111-library.png"

journals=(gridmates_wordsearch_ws66_v110_qa_progress_v1.dat gridmates_wordsearch_ws66_v110_qa_sessions_v1.dat)
for journal in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$journal" > "$report/$journal"
  test -s "$report/$journal"
done
adb install -r app/build/outputs/apk/debug/app-debug.apk
for journal in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$journal" | cmp "$report/$journal" -
done
echo 'Signed v111 to v112 upgrade kept both book journals byte-identical.' \
  | tee "$report/install-over.txt"

check_layout() {
  local width="$1"
  local failed=0
  adb shell am instrument -w -r -e expectedWidthDp "$width" -e class \
    "$layout_test#shelfBracketsClearTheBaseAndSavedPuzzleFitsThePhone" "$runner" \
    | tee "$report/v112-${width}dp.txt" || failed=1
  for screen in home library puzzle; do
    local capture="$report/v112-$width-$screen.png"
    if ! adb exec-out run-as "$package" cat "files/ws69-$width-$screen.png" > "$capture" ||
       [ ! -s "$capture" ]; then
      rm -f -- "$capture"
      failed=1
    fi
  done
  grep -Eq '^OK \(1 test\)' "$report/v112-${width}dp.txt" || failed=1
  return "$failed"
}
layout_failed=0
check_layout 393 || layout_failed=1
adb shell wm size 880x1760
check_layout 320 || layout_failed=1
exit "$layout_failed"
