#!/usr/bin/env bash
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
runner="$package.test/androidx.test.runner.AndroidJUnitRunner"
tests=com.playereden.gridmates.wordsearch
report=qa-ws75-garden-album
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
  adb exec-out run-as "$package" cat "files/ws75-$name.png" > "$report/$name.png"
  cmp -s -n 8 "$report/$name.png" <(printf '\211PNG\r\n\032\n')
}

adb shell wm density 440
adb shell wm size 1080x2200
adb shell cmd overlay enable-exclusive --user 0 --category com.android.internal.systemui.navbar.threebutton
adb shell settings put system font_scale 1.0
adb shell settings put secure immersive_mode_confirmations confirmed
adb install -r qa-ws75-baseline/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
run_test Ws75V117AlbumSeedAndroidTest v117-seed

journals=(gridmates_wordsearch_solo_v1.json gridmates_wordsearch_ws73_v116_qa_progress_v1.dat)
for file in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$file" > "$report/$file"
  test -s "$report/$file"
done
adb install -r app/build/outputs/apk/debug/app-debug.apk
for file in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$file" | cmp "$report/$file" -
done
echo 'Signed v117 to v118 install-over preserved the Solo and book journal bytes.' \
  | tee "$report/install-over.txt"

run_test 'Ws75GardenAlbumAndroidTest#earlierCreditLiveUnlockAndBothRoutesSurviveUpgrade' v118-garden-album
capture upgraded-collection
capture stamp-earned
capture library-album

adb shell am force-stop "$package"
adb shell wm size 880x1760
adb shell settings put system font_scale 1.5
run_test 'Ws75GardenAlbumAndroidTest#albumsFitAtLargerText' v118-large-text
capture large-text-album
