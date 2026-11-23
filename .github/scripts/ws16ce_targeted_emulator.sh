#!/usr/bin/env bash
# WS16CE: execute v86 tests on a real API35 emulator with pinned signed app.
set -euxo pipefail
PKG="com.playereden.gridmates.wordsearch.qa"
APP="qa-baseline/app-debug.apk"
TEST_APP="app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"
test -s "$APP"
test -s "$TEST_APP"
adb install -r "$APP"
adb install -r -t "$TEST_APP"
mkdir -p qa-report
CLASSES="com.playereden.gridmates.wordsearch.SoloRewardRecordsJourneyAndroidTest,com.playereden.gridmates.wordsearch.NearbyDailyLargeTextAndroidTest,com.playereden.gridmates.wordsearch.ContinuousSoloWordIndexAndroidTest,com.playereden.gridmates.wordsearch.NearbyAutoWifiRefreshAndroidTest"
adb shell am instrument -w -r -e class "$CLASSES" \
    "$PKG.test/androidx.test.runner.AndroidJUnitRunner" \
    | tee qa-report/ws16ce-instrumentation.txt
grep -F 'OK (6 tests)' qa-report/ws16ce-instrumentation.txt
if grep -E 'FAILURES!!!|INSTRUMENTATION_FAILED|INSTRUMENTATION_ABORTED' qa-report/ws16ce-instrumentation.txt; then
    echo "::error::WS16CE Android test failure"
    exit 1
fi
echo "PASS: six real Android API35 tests, including auto Wi-Fi restoration."
