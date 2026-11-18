#!/usr/bin/env bash
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
report=qa-ws42-report
mkdir -p "$report"

python3 .github/scripts/ws42_phone_review.py
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

classes=$(python3 - <<'PY'
names = [
    "GameBackupAndroidTest", "QuickPlayAndroidTest", "QuickPlayNavigationAndroidTest",
    "MixedQuickPlayInputAndroidTest", "Ws14IRealTwentyCellWordTraceAndroidTest",
    "DailyPersistenceTest", "V2PuzzlePersistenceTest", "MultiSessionPersistenceAndroidTest",
    "SoloSchemaGuardsAndroidTest", "HomeTitleLayoutAndroidTest", "HandsetPolishAndroidTest",
    "Ws14FBookDetailPolishAndroidTest", "Ws14K2GameChromeAndroidTest",
    "ScreenshotFeedbackAndroidTest"
]
print(",".join("com.playereden.gridmates.wordsearch." + name for name in names))
PY
)
adb shell am instrument -w -r -e class "$classes" \
    "$package.test/androidx.test.runner.AndroidJUnitRunner" | tee "$report/instrumentation.txt"
grep -Eq '^OK \(31 tests\)' "$report/instrumentation.txt"
! grep -Eq 'FAILURES!!!|INSTRUMENTATION_FAILED|Process crashed' "$report/instrumentation.txt"
adb logcat -d -s AndroidRuntime > "$report/android-runtime.txt"
