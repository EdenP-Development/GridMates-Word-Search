#!/usr/bin/env bash
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
runner="$package.test/androidx.test.runner.AndroidJUnitRunner"
tests=com.playereden.gridmates.wordsearch
report=qa-ws77-woodland-album
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
  adb exec-out run-as "$package" cat "files/ws77-$name.png" > "$report/$name.png"
  cmp -s -n 8 "$report/$name.png" <(printf '\211PNG\r\n\032\n')
}

adb shell wm density 440
adb shell wm size 1080x2200
adb shell cmd overlay enable-exclusive --user 0 --category com.android.internal.systemui.navbar.threebutton
adb shell settings put system font_scale 1.0
adb shell settings put secure immersive_mode_confirmations confirmed
adb install -r qa-ws77-baseline/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
run_test Ws77V119WoodlandSeedAndroidTest v119-seed

journals=(
  gridmates_wordsearch_solo_v1.json
  gridmates_wordsearch_ws73_v116_qa_progress_v1.dat
  gridmates_wordsearch_ws73_v116_qa_sessions_v1.dat
)
for file in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$file" > "$report/$file"
  test -s "$report/$file"
done
adb install -r app/build/outputs/apk/debug/app-debug.apk
for file in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$file" | cmp "$report/$file" -
done
echo 'Signed v119 to v120 install-over preserved the Solo, book and page-session journal bytes.' \
  | tee "$report/install-over.txt"

run_test 'Ws77WoodlandAlbumAndroidTest#oldSolvesAndLiveChapterUnlocksShareOneWoodlandAlbum' v120-woodland
for screenshot in from-previous-saves trailhead dawn-chorus woodland-album; do
  capture "$screenshot"
done

adb shell am force-stop "$package"
adb shell wm size 880x1760
adb shell settings put system font_scale 1.5
run_test 'Ws77WoodlandAlbumAndroidTest#allThreeAlbumsOpenOnACompactPhoneWithLargeText' v120-large-text
capture album-list-large-text
capture last-stamp-large-text
