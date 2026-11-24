#!/usr/bin/env bash
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
runner="$package.test/androidx.test.runner.AndroidJUnitRunner"
tests=com.playereden.gridmates.wordsearch
report=qa-ws72-home-fit
mkdir -p "$report"
trap 'adb logcat -d -s AndroidRuntime:E > "$report/logcat.txt" || true' EXIT

run_test() {
  local test="$1" name="$2"
  shift 2
  adb shell am instrument -w -r -e class "$tests.$test" "$@" "$runner" \
    | tee "$report/$name-test.txt"
  grep -Eq '^OK \(1 test\)' "$report/$name-test.txt"
}

collect_layout() {
  local name="$1"
  adb exec-out run-as "$package" cat "files/ws72-$name.txt" > "$report/$name.txt"
  grep -q '^verticalScrollMaxPx=' "$report/$name.txt"
  adb exec-out run-as "$package" cat "files/ws72-$name.png" > "$report/$name.png"
  cmp -s -n 8 "$report/$name.png" <(printf '\211PNG\r\n\032\n')
}

# Same tall portrait aspect as the owner screenshot, with the paired cards visible.
adb shell wm density 440
adb shell wm size 990x2255
adb shell cmd overlay enable-exclusive --user 0 --category com.android.internal.systemui.navbar.threebutton
adb shell settings put system font_scale 1.0
adb shell settings put secure immersive_mode_confirmations confirmed
adb install -r qa-ws72-baseline/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

run_test 'Ws72HomeFitAndroidTest#captureV114Home' v114-home
collect_layout v114-home
run_test Ws67V110SeedAndroidTest v114-seed

journals=(gridmates_wordsearch_ws66_v110_qa_progress_v1.dat gridmates_wordsearch_ws66_v110_qa_sessions_v1.dat)
for journal in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$journal" > "$report/$journal"
  test -s "$report/$journal"
done
adb install -r app/build/outputs/apk/debug/app-debug.apk
for journal in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$journal" | cmp "$report/$journal" -
done
echo 'Signed v114 to v115 upgrade kept both book journals byte-identical.' \
  | tee "$report/install-over.txt"

failed=0
run_test 'Ws72HomeFitAndroidTest#tallPortraitHomeFitsWithoutVerticalScrolling' \
  v115-home || failed=1
collect_layout v115-home || failed=1

adb shell am force-stop "$package"
adb shell wm size 880x1760
adb shell settings put system font_scale 2.0
run_test 'Ws72HomeFitAndroidTest#shortPhoneAtLargeTextKeepsAllActionsReachable' \
  v115-home-large-text || failed=1
collect_layout v115-home-large-text || failed=1
exit "$failed"
