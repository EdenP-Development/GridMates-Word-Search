#!/usr/bin/env bash
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
runner="$package.test/androidx.test.runner.AndroidJUnitRunner"
tests=com.playereden.gridmates.wordsearch
report=qa-ws71-book-play
mkdir -p "$report"
trap 'adb logcat -d -s AndroidRuntime:E > "$report/logcat.txt" || true' EXIT

run_test() {
  local test="$1" name="$2"
  shift 2
  adb shell am instrument -w -r -e class "$tests.$test" "$@" "$runner" \
    | tee "$report/$name.txt"
  grep -Eq '^OK \(1 test\)' "$report/$name.txt"
}

collect_capture() {
  local name="$1"
  adb exec-out run-as "$package" cat "files/ws71-$name.png" > "$report/$name.png"
  cmp -s -n 8 "$report/$name.png" <(printf '\211PNG\r\n\032\n')
}

adb shell wm density 440
adb shell wm size 1080x2200
adb shell settings put system font_scale 1.0
adb shell settings put secure immersive_mode_confirmations confirmed
adb install -r qa-ws71-baseline/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
run_test Ws67V110SeedAndroidTest v113-seed

journals=(gridmates_wordsearch_ws66_v110_qa_progress_v1.dat gridmates_wordsearch_ws66_v110_qa_sessions_v1.dat)
for journal in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$journal" > "$report/$journal"
  test -s "$report/$journal"
done
adb install -r app/build/outputs/apk/debug/app-debug.apk
for journal in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$journal" | cmp "$report/$journal" -
done
echo 'Signed v113 to v114 upgrade kept both book journals byte-identical.' \
  | tee "$report/install-over.txt"

failed=0
run_test 'Ws70HomeLibraryAndroidTest#libraryShelvesFillInOrderAndReopenSavedPuzzle' \
  v114-saved-solar -e expectedWidthDp 393 || failed=1
run_test 'Ws71BookPlayAndroidTest#completeReviewPracticeAndContinueFromTheLibrary' \
  v114-book-journey || failed=1
for name in resume result next-page; do collect_capture "$name" || failed=1; done

adb shell am force-stop "$package"
adb shell wm size 880x1760
adb shell settings put system font_scale 2.0
run_test 'Ws71BookPlayAndroidTest#chaptersAndResultsRemainReachableAtLargeText' \
  v114-book-large-text || failed=1
for name in chapters-200 detail-200 result-200; do collect_capture "$name" || failed=1; done
exit "$failed"
