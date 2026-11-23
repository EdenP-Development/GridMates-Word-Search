#!/usr/bin/env bash
# WS58: execute UI-driven navigation and real finger traces on disposable
# WS57 Woodland V5 pages. This is emulator QA, not owner phone approval.
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
report=qa-ws58-woodland-player
mkdir -p "$report"
adb logcat -c || true
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

set +e
adb shell am instrument -w -r -e class \
  com.playereden.gridmates.wordsearch.Ws58WoodlandPlayerJourneyAndroidTest \
  "$package.test/androidx.test.runner.AndroidJUnitRunner" \
  | tee "$report/instrumentation.txt"
code=${PIPESTATUS[0]}
set -e

adb logcat -d -s AndroidRuntime > "$report/android-runtime.txt" || true
if [ "$code" -ne 0 ] || ! grep -Eq '^OK \(3 tests\)' "$report/instrumentation.txt" || \
  grep -Eq 'FAILURES!!!|INSTRUMENTATION_FAILED|Process crashed|INSTRUMENTATION_STATUS_CODE: -1' "$report/instrumentation.txt"; then
  echo "WS58: real book UI tests failed; inspect instrumentation and runtime logs" >&2
  exit 1
fi

echo "WS58: 3 actual emulator UI tests passed: six-chapter picker and 12/20-cell V5 finger trace with persisted hints"
