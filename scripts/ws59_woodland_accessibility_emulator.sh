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

adb shell wm user-rotation lock 0
set +e
adb shell am instrument -w -r -e class \
  "$test_class#at200PercentTextAllSixChaptersAndLastPageActionsStayReachable,$test_class#at200PercentTextExpertPortraitRetainsHintToolsAndGrid,$test_class#at200PercentTextExpertPrecisePickerClaimsRealWord" \
  "$target" | tee "$report/portrait-200-percent.txt"
portrait_code=${PIPESTATUS[0]}
set -e
portrait_failed=0
if [ "$portrait_code" -ne 0 ] || \
  ! grep -Eq '^OK \(3 tests\)' "$report/portrait-200-percent.txt" || \
  grep -Eq 'FAILURES!!!|INSTRUMENTATION_FAILED|Process crashed' "$report/portrait-200-percent.txt"; then
  echo "WS59: portrait failed, still running independent landscape test" >&2
  portrait_failed=1
fi

# The phone is actually rotated here; a wide Compose box on a portrait device
# alone is not a valid landscape verification.
adb shell wm user-rotation lock 1
sleep 3
# Log the system setting, but the test also requests/inspects the ACTUAL
# Activity configuration. A user-rotation lock by itself may be ignored.
adb shell wm user-rotation > "$report/system-rotation.txt" || true
adb shell wm size >> "$report/system-rotation.txt" || true
set +e
adb shell am instrument -w -r -e class \
  "$test_class#at150PercentTextClassicLandscapeRetainsHintToolsAndGrid" \
  "$target" | tee "$report/landscape-150-percent.txt"
landscape_code=${PIPESTATUS[0]}
set -e
adb logcat -d -s AndroidRuntime > "$report/android-runtime.txt" || true
landscape_failed=0
if [ "$landscape_code" -ne 0 ] || \
  ! grep -Eq '^OK \(1 test\)' "$report/landscape-150-percent.txt" || \
  grep -Eq 'FAILURES!!!|INSTRUMENTATION_FAILED|Process crashed' "$report/landscape-150-percent.txt"; then
  echo "WS59: landscape failed, preserving evidence from both test groups" >&2
  landscape_failed=1
fi

# Export whatever screenshots exist, even when a previous assertion failed.
for name in \
  chapter-200-percent \
  expert-portrait-200-percent-before \
  expert-portrait-200-percent-after-swipe \
  precise-portrait-200-percent-before \
  precise-portrait-200-percent-after \
  classic-landscape-150-percent-before \
  classic-landscape-150-percent-after-swipe; do
  adb pull "/sdcard/Android/data/$package/files/ws59-$name.png" \
    "$report/ws59-$name.png" >/dev/null 2>&1 || \
    echo "Screenshot not available: $name" >&2
done
adb logcat -d -s WS59_DIAG:I AndroidRuntime:E > "$report/diagnostics-logcat.txt" || true
if [ "$portrait_failed" -ne 0 ] || [ "$landscape_failed" -ne 0 ]; then
  echo "WS59: FAILED/INCOMPLETE; examine independent reports, saved-ID logs and screenshots" >&2
  exit 1
fi
echo "WS59: 3 portrait 200% UI tests and 1 physically rotated landscape UI test PASS"
