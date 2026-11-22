#!/usr/bin/env bash
# WS57 dedicated, disposable Android emulator test of a real QA-only V5 book.
# Not a substitute for the owner's physical handset/visual approval.
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
report=qa-ws57-woodland-preview
mkdir -p "$report"
adb logcat -c || true
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

set +e
adb shell am instrument -w -r -e class \
  com.playereden.gridmates.wordsearch.Ws57PlayableWoodlandPreviewAndroidTest \
  "$package.test/androidx.test.runner.AndroidJUnitRunner" \
  | tee "$report/instrumentation.txt"
code=${PIPESTATUS[0]}
set -e

adb logcat -d -s AndroidRuntime > "$report/android-runtime.txt" || true
if [ "$code" -ne 0 ] || ! grep -Eq '^OK \(3 tests\)' "$report/instrumentation.txt" || \
  grep -Eq 'FAILURES!!!|INSTRUMENTATION_FAILED|Process crashed|INSTRUMENTATION_STATUS_CODE: -1' "$report/instrumentation.txt"; then
  echo "WS57: Android emulator execution failed; read the attached instrumentation and crash logs" >&2
  exit 1
fi
echo "WS57: all three emulator tests PASSED; 63 original pages, 18 new pinned QA pages, save upgrade/restart"
