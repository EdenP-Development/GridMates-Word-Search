#!/usr/bin/env bash
# Run on a disposable Android emulator.
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
runner="$package.test/androidx.test.runner.AndroidJUnitRunner"
tests=com.playereden.gridmates.wordsearch
report=qa-ws66-preview
apk=app/build/outputs/apk/debug/app-debug.apk
mkdir -p "$report"

adb install qa-ws66-baseline/app-debug.apk
adb install -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w -r -e class "$tests.Ws66PreviewUpgradeSeedAndroidTest" "$runner" \
  | tee "$report/v108-seed.txt"
grep -Eq '^OK \(1 test\)' "$report/v108-seed.txt"

journals=(gridmates_wordsearch_ws57_qa_progress_v1.dat gridmates_wordsearch_ws57_qa_sessions_v1.dat)
for file in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$file" > "$report/$file"
  test -s "$report/$file"
done

adb install -r "$apk"
adb shell am instrument -w -r -e class "$tests.Ws66PreviewAndroidTest" "$runner" \
  | tee "$report/v110-preview.txt"
grep -Eq '^OK \(3 tests\)' "$report/v110-preview.txt"

for file in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$file" | cmp "$report/$file" -
done
echo "Signed v108 bookmarks and sessions restored in v110; original bytes match." \
  | tee "$report/install-over.txt"
