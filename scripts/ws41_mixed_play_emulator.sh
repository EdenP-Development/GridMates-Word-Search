#!/usr/bin/env bash
set -eu -o pipefail
package=com.playereden.gridmates.wordsearch.qa
report=qa-ws41-report
mkdir -p "$report"
python3 .github/scripts/ws41_phone_review.py
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
classes=$(python3 - <<'PY'
names=['GameBackupAndroidTest','QuickPlayAndroidTest','QuickPlayNavigationAndroidTest','MixedQuickPlayInputAndroidTest','Ws14IRealTwentyCellWordTraceAndroidTest','DailyPersistenceTest','V2PuzzlePersistenceTest','MultiSessionPersistenceAndroidTest','SoloSchemaGuardsAndroidTest']
print(','.join('com.playereden.gridmates.wordsearch.'+n for n in names))
PY
)
adb shell am instrument -w -r -e class "$classes" "$package.test/androidx.test.runner.AndroidJUnitRunner" | tee "$report/instrumentation.txt"
grep -Eq '^OK \(23 tests\)' "$report/instrumentation.txt"
! grep -Eq 'FAILURES!!!|INSTRUMENTATION_FAILED|Process crashed' "$report/instrumentation.txt"
for image in ws41-quick-play-home.png ws41-quick-play-chooser.png ws41-quick-play-puzzle.png; do
    adb exec-out run-as "$package" cat "files/$image" > "$report/$image"
done
adb logcat -d -s AndroidRuntime > "$report/android-runtime.txt"
