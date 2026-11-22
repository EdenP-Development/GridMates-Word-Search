#!/usr/bin/env bash
# WS59: actual Android emulator execution, portrait large text then rotated
# landscape with large text. Tests use isolated WS59 save files only.
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
test_class=com.playereden.gridmates.wordsearch.Ws59WoodlandTextAndLandscapeAndroidTest
target="$package.test/androidx.test.runner.AndroidJUnitRunner"
report=qa-ws59-woodland-accessibility
mkdir -p "$report"
adb logcat -c || true
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

adb shell cmd window set-user-rotation lock 0
set +e
adb shell am instrument -w -r -e class \
  "$test_class#at200PercentTextAllSixChaptersAndLastPageActionsStayReachable,$test_class#at200PercentTextExpertPortraitRetainsHintToolsAndGrid" \
  "$target" | tee "$report/portrait-200-percent.txt"
portrait_code=${PIPESTATUS[0]}
set -e
if [ "$portrait_code" -ne 0 ] || \
  ! grep -Eq '^OK \(2 tests\)' "$report/portrait-200-percent.txt" || \
  grep -Eq 'FAILURES!!!|INSTRUMENTATION_FAILED|Process crashed' "$report/portrait-200-percent.txt"; then
  echo "WS59 portrait large-type tests failed; see report" >&2
  exit 1
fi

# The phone is actually rotated here; a wide Compose box on a portrait device
# alone is not a valid landscape verification.
adb shell cmd window set-user-rotation lock 1
sleep 3
set +e
adb shell am instrument -w -r -e class \
  "$test_class#at150PercentTextClassicLandscapeRetainsHintToolsAndGrid" \
  "$target" | tee "$report/landscape-150-percent.txt"
landscape_code=${PIPESTATUS[0]}
set -e
adb logcat -d -s AndroidRuntime > "$report/android-runtime.txt" || true
if [ "$landscape_code" -ne 0 ] || \
  ! grep -Eq '^OK \(1 test\)' "$report/landscape-150-percent.txt" || \
  grep -Eq 'FAILURES!!!|INSTRUMENTATION_FAILED|Process crashed' "$report/landscape-150-percent.txt"; then
  echo "WS59 rotated landscape test failed; see report" >&2
  exit 1
fi

for name in chapter-200-percent expert-portrait-200-percent classic-landscape-150-percent; do
  adb pull "/sdcard/Android/data/$package/files/ws59-$name.png" \
    "$report/ws59-$name.png" || echo "Screenshot export unavailable: $name" >&2
done
echo "WS59: 2 portrait large-type tests + 1 rotated landscape V5 gesture test PASS; screenshots are QA only"
