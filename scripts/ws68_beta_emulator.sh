#!/usr/bin/env bash
# Exercise both signed upgrades on one disposable Android emulator.
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
runner="$package.test/androidx.test.runner.AndroidJUnitRunner"
tests=com.playereden.gridmates.wordsearch
report=qa-ws68-integration
mkdir -p "$report"

adb install qa-ws66-baseline/app-debug.apk
adb install -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w -r -e class "$tests.Ws66PreviewUpgradeSeedAndroidTest" "$runner" \
  | tee "$report/v108-seed.txt"
grep -Eq '^OK \(1 test\)' "$report/v108-seed.txt"

journals=(gridmates_wordsearch_ws57_qa_progress_v1.dat gridmates_wordsearch_ws57_qa_sessions_v1.dat)
for journal in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$journal" > "$report/$journal"
  test -s "$report/$journal"
done

bash scripts/ws67_home_library_emulator.sh
adb shell am instrument -w -r -e class "$tests.Ws66PreviewAndroidTest" "$runner" \
  | tee "$report/legacy-and-preview.txt"
grep -Eq '^OK \(3 tests\)' "$report/legacy-and-preview.txt"
for journal in "${journals[@]}"; do
  adb exec-out run-as "$package" cat "files/$journal" | cmp "$report/$journal" -
done
echo 'Woodland progress and session files match their original v108 bytes after both upgrades.' \
  | tee "$report/legacy-journals.txt"

bash scripts/ws62_nearby_live_tcp_emulator.sh 111
