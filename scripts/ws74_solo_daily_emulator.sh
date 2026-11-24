#!/usr/bin/env bash
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
runner="$package.test/androidx.test.runner.AndroidJUnitRunner"
tests=com.playereden.gridmates.wordsearch
report=qa-ws74-solo-daily
mkdir -p "$report"
trap 'adb logcat -d -s AndroidRuntime:E > "$report/logcat.txt" || true' EXIT

run_test() {
  local selector="$1" name="$2"
  adb shell am instrument -w -r -e class "$tests.$selector" "$runner" \
    | tee "$report/$name-test.txt"
  grep -Eq '^OK \(1 test\)' "$report/$name-test.txt"
}

capture() {
  local name="$1"
  adb exec-out run-as "$package" cat "files/ws74-$name.png" > "$report/$name.png"
  cmp -s -n 8 "$report/$name.png" <(printf '\211PNG\r\n\032\n')
}

adb shell wm density 440
adb shell wm size 1080x2200
adb shell cmd overlay enable-exclusive --user 0 --category com.android.internal.systemui.navbar.threebutton
adb shell settings put system font_scale 1.0
adb shell settings put secure immersive_mode_confirmations confirmed
adb install -r qa-ws74-baseline/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

run_test Ws74V116DailySeedAndroidTest v116-seed
run_test 'Ws74SoloDailyAndroidTest#reproduceV116LosingTheSelectedDate' v116-reproduction
capture v116-daily-recreation

journal=gridmates_wordsearch_solo_v1.json
adb exec-out run-as "$package" cat "files/$journal" > "$report/v116-solo.json"
test -s "$report/v116-solo.json"
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb exec-out run-as "$package" cat "files/$journal" | cmp "$report/v116-solo.json" -
echo 'Signed v116 to v117 install-over preserved the Solo and Daily journal bytes.' \
  | tee "$report/install-over.txt"

run_test 'Ws74SoloDailyAndroidTest#earlierDailyKeepsItsDateAndProgressAfterRecreation' v117-earlier-daily
capture v117-earlier-daily
run_test 'Ws74SoloDailyAndroidTest#todayResumesAClueThenFinishesAndReviewsOnce' v117-today
capture v117-daily-clue-resume
capture v117-daily-result
run_test 'Ws74SoloDailyAndroidTest#quickPlayResumesThenStartsAFreshPuzzleAtTheSameSize' v117-quick-play
capture v117-quick-play-result
