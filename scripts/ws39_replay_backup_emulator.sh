#!/usr/bin/env bash
set -eu -o pipefail

package=com.playereden.gridmates.wordsearch.qa
report=qa-ws39-report
mkdir -p "$report"
adb install app/build/outputs/apk/debug/app-debug.apk
adb install -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell settings put system font_scale 1.0
tests=GameBackupAndroidTest,QuickPlayAndroidTest,QuickPlayNavigationAndroidTest,Ws14IRealTwentyCellWordTraceAndroidTest
classes=$(python3 - "$tests" <<'PY'
import sys
print(','.join('com.playereden.gridmates.wordsearch.' + c for c in sys.argv[1].split(',')))
PY
)
adb shell am instrument -w -r -e class "$classes" \
    "$package.test/androidx.test.runner.AndroidJUnitRunner" | tee "$report/instrumentation.txt"
grep -Eq '^OK \(10 tests\)' "$report/instrumentation.txt"
for image in ws39-quick-play-home.png ws39-quick-play-puzzle.png; do
    adb exec-out run-as "$package" cat "files/$image" > "$report/$image"
done
adb logcat -d -s AndroidRuntime > "$report/android-runtime.txt"
