#!/usr/bin/env bash
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
runner="$package.test/androidx.test.runner.AndroidJUnitRunner"
tests=com.playereden.gridmates.wordsearch
report=qa-ws76-activity-album
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
  adb exec-out run-as "$package" cat "files/ws76-$name.png" > "$report/$name.png"
  cmp -s -n 8 "$report/$name.png" <(printf '\211PNG\r\n\032\n')
}

adb shell wm density 440
adb shell wm size 1080x2200
adb shell cmd overlay enable-exclusive --user 0 --category com.android.internal.systemui.navbar.threebutton
adb shell settings put system font_scale 1.0
adb shell settings put secure immersive_mode_confirmations confirmed
adb install -r qa-ws76-baseline/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
run_test Ws76V118ActivitySeedAndroidTest v118-seed

journals=(gridmates_wordsearch_solo_v1.json gridmates_wordsearch_ws73_v116_qa_progress_v1.dat)
for file in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$file" > "$report/$file"
  test -s "$report/$file"
done
adb install -r app/build/outputs/apk/debug/app-debug.apk
for file in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$file" | cmp "$report/$file" -
done
echo 'Signed v118 to v119 install-over preserved the Solo and book journal bytes.' \
  | tee "$report/install-over.txt"

run_test 'Ws76ActivityAlbumAndroidTest#earnedStampsCarryForwardAndFollowRealDailyAndQuickPlaySolves' v119-activity
for screenshot in upgraded-activity daily-stamp-earned quick-stamp-earned activity-complete; do
  capture "$screenshot"
done

run_test 'Ws76ActivityAlbumAndroidTest#gardenShowsAndPlaysTheStepToTwentyByTwenty' v119-garden
for screenshot in garden-next-page garden-page-three garden-page-three-complete; do
  capture "$screenshot"
done
adb exec-out run-as "$package" cat files/ws76-garden-boards.tsv > "$report/garden-boards.tsv"
test -s "$report/garden-boards.tsv"

adb shell am force-stop "$package"
adb shell wm size 880x1760
adb shell settings put system font_scale 1.5
run_test 'Ws76ActivityAlbumAndroidTest#bothAlbumsAreReachableAtLargeText' v119-large-text
capture large-text-album-index
capture large-text-activity
