#!/usr/bin/env bash
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
runner="$package.test/androidx.test.runner.AndroidJUnitRunner"
tests=com.playereden.gridmates.wordsearch
report=qa-ws73-chapter-variety
mkdir -p "$report"
trap 'adb logcat -d -s AndroidRuntime:E > "$report/logcat.txt" || true' EXIT

run_test() {
  local test="$1" name="$2"
  adb shell am instrument -w -r -e class "$tests.$test" "$runner" \
    | tee "$report/$name-test.txt"
  grep -Eq '^OK \(1 test\)' "$report/$name-test.txt"
}

adb shell wm density 440
adb shell wm size 1080x2200
adb shell cmd overlay enable-exclusive --user 0 --category com.android.internal.systemui.navbar.threebutton
adb shell settings put system font_scale 1.0
adb shell settings put secure immersive_mode_confirmations confirmed
adb install -r qa-ws73-baseline/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
run_test Ws67V110SeedAndroidTest v115-seed

earlier_suffixes=(progress sessions)
for suffix in "${earlier_suffixes[@]}"; do
  file="gridmates_wordsearch_ws66_v110_qa_${suffix}_v1.dat"
  adb exec-out run-as "$package" cat "files/$file" > "$report/$file"
  test -s "$report/$file"
done
adb install -r app/build/outputs/apk/debug/app-debug.apk
run_test 'Ws73VarietyAndroidTest#oldPageOrderAndInProgressPuzzleSurviveUpgrade' upgrade
run_test 'Ws73VarietyAndroidTest#solarRelaxedPlaysFromTheSpaceBook' solar-relaxed
run_test 'Ws73VarietyAndroidTest#quietClearingsExpertPlaysFromTheWoodlandBook' quiet-clearings-expert

for suffix in "${earlier_suffixes[@]}"; do
  old="gridmates_wordsearch_ws66_v110_qa_${suffix}_v1.dat"
  new="gridmates_wordsearch_ws73_v116_qa_${suffix}_v1.dat"
  adb exec-out run-as "$package" cat "files/$old" | cmp "$report/$old" -
  adb shell run-as "$package" test -s "files/$new"
done
echo 'The signed v115 to v116 upgrade restored progress into separate journals and kept both original journals byte-identical.' \
  | tee "$report/install-over.txt"

for capture in resumed-v115-solar solar-relaxed quiet-clearings-expert; do
  adb exec-out run-as "$package" cat "files/ws73-$capture.png" > "$report/$capture.png"
  cmp -s -n 8 "$report/$capture.png" <(printf '\211PNG\r\n\032\n')
done
