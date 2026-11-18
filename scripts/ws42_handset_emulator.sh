#!/usr/bin/env bash
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
report=qa-ws42-report
phase="${1:?choose upgrade, book or regression}"
case "$phase" in
    upgrade|book|regression) ;;
    *) echo "Unknown WS42 test phase: $phase" >&2; exit 2 ;;
esac
mkdir -p "$report"

adb logcat -T 1 -s AndroidRuntime ActivityManager lmkd OpenGLRenderer > "$report/$phase-logcat.txt" 2>&1 &
logcat_pid=$!
trap 'kill "$logcat_pid" 2>/dev/null || true' EXIT

if [ "$phase" != regression ]; then
    python3 .github/scripts/ws42_phone_review.py "$phase"
    exit
fi

adb install -r app/build/outputs/apk/debug/app-debug.apk
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
