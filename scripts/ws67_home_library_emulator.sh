#!/usr/bin/env bash
# Exercise the actual signed update on a clean emulator.
set -euo pipefail
package=com.playereden.gridmates.wordsearch.qa
runner="$package.test/androidx.test.runner.AndroidJUnitRunner"
tests=com.playereden.gridmates.wordsearch
report=qa-ws67-home-library
apk=app/build/outputs/apk/debug/app-debug.apk
mkdir -p "$report"
adb install -r qa-ws67-baseline/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w -r -e class "$tests.Ws67V110SeedAndroidTest" "$runner" \
  | tee "$report/v110-seed.txt"
grep -Eq '^OK \(1 test\)' "$report/v110-seed.txt"
journals=(gridmates_wordsearch_ws66_v110_qa_progress_v1.dat gridmates_wordsearch_ws66_v110_qa_sessions_v1.dat)
for file in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$file" > "$report/$file"
  test -s "$report/$file"
done

adb install -r "$apk"
for file in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$file" | cmp "$report/$file" -
done
echo 'Signed v110 to v111 update kept both journals byte-identical.' | tee "$report/install-over.txt"
adb shell am instrument -w -r -e class "$tests.Ws67HomeLibraryAndroidTest" "$runner" \
  | tee "$report/v111-ui.txt"
grep -Eq '^OK \(2 tests\)' "$report/v111-ui.txt"
for screen in home library resumed-solar; do
  adb exec-out run-as "$package" cat "files/ws67-$screen.png" > "$report/$screen.png"
  test -s "$report/$screen.png"
done
