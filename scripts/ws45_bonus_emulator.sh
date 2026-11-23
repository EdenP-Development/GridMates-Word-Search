#!/usr/bin/env bash
# WS45: execute the WS44 bonus-word feature on a real Android emulator,
# not merely compile the instrumentation tests.
set -euo pipefail

package=com.playereden.gridmates.wordsearch.qa
report=qa-ws45-bonus
mkdir -p "$report"
adb logcat -c || true
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

classes=$(python3 - <<'PY'
names=[
    "SideWordScoringAndroidTest",
    "QuickPlayAndroidTest",
    "SoloPersistenceTest",
    "DailyPersistenceTest",
    "SoloSchemaGuardsAndroidTest",
    "GameBackupAndroidTest"
]
print(",".join("com.playereden.gridmates.wordsearch." + n for n in names))
PY
)

set +e
adb shell am instrument -w -r -e class "$classes" \
    "$package.test/androidx.test.runner.AndroidJUnitRunner" \
    | tee "$report/instrumentation.txt"
code=${PIPESTATUS[0]}
set -e
adb logcat -d -s AndroidRuntime > "$report/android-runtime.txt" || true
if [ "$code" -ne 0 ] || ! grep -Eq '^OK \([1-9][0-9]* tests?\)' "$report/instrumentation.txt" ||
    grep -Eq 'FAILURES!!!|INSTRUMENTATION_FAILED|Process crashed|INSTRUMENTATION_STATUS_CODE: -1' "$report/instrumentation.txt"; then
    echo "WS45: Android runtime test failed; inspect artifact instrument log" >&2
    exit 1
fi
echo "WS45: executed scoring, Quick Play, Solo, Daily, schema & backups on Android"
