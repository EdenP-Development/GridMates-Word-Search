#!/usr/bin/env bash
# WS16CD: Run actual Android instrumentation against the verified signed v85
# application, not the rebuilt Gradle test host's optional fallback art.
set -euo pipefail
PKG="com.playereden.gridmates.wordsearch.qa"
APK="qa-baseline/app-debug.apk"
TEST_APK="app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"

test -s "$APK"
test -s "$TEST_APK"
echo "Installing verified v85 signed QA APK and current instrumentation APK."
adb install -r "$APK"
adb install -r -t "$TEST_APK"

# Five tests: saved unfinished Solo->AtomicFile reopen->assisted completion->
# Rabbit/First Page/Records; two live UI 200%-font checks; two word-list
# regression checks, all on a running API35 device. No LAN host is started.
CLASSES="com.playereden.gridmates.wordsearch.SoloRewardRecordsJourneyAndroidTest,com.playereden.gridmates.wordsearch.NearbyDailyLargeTextAndroidTest,com.playereden.gridmates.wordsearch.ContinuousSoloWordIndexAndroidTest"
mkdir -p qa-report
adb shell am instrument -w -r -e class "$CLASSES" \
    "$PKG.test/androidx.test.runner.AndroidJUnitRunner" \
    | tee qa-report/ws16cd-instrumentation.txt

grep -E 'OK \(5 tests\)' qa-report/ws16cd-instrumentation.txt
if grep -E 'FAILURES!!!|INSTRUMENTATION_FAILED|INSTRUMENTATION_ABORTED' qa-report/ws16cd-instrumentation.txt; then
    echo "::error::Targeted emulator run reported a failure"
    exit 1
fi
echo "PASS: all five WS16CD tests executed successfully on API35 emulator."
