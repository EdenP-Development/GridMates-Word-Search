#!/usr/bin/env bash
set -eu -o pipefail

package=com.playereden.gridmates.wordsearch.qa
test_namespace=com.playereden.gridmates.wordsearch
report=qa-library-report
mkdir -p "$report"
adb install app/build/outputs/apk/debug/app-debug.apk
adb install -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

run_test() {
    local test_name="$1"
    adb shell am instrument -w -r -e class "$test_namespace.$test_name" \
        "$package.test/androidx.test.runner.AndroidJUnitRunner" \
        | tee "$report/${test_name#*#}.txt"
    grep -Eq '^OK \(1 test\)' "$report/${test_name#*#}.txt"
}

run_test 'QaWoodlandBookJourneyAndroidTest#startAndPauseWoodland'
adb exec-out run-as "$package" cat files/woodland-shelf.png > "$report/woodland-shelf.png"
adb shell am force-stop "$package"
run_test 'QaWoodlandBookJourneyAndroidTest#reopenAndCompleteAllWoodlandPages'
adb exec-out run-as "$package" cat files/woodland-complete.png > "$report/woodland-complete.png"
adb shell am force-stop "$package"
run_test 'QaWoodlandBookJourneyAndroidTest#reopenCompletedWoodland'
