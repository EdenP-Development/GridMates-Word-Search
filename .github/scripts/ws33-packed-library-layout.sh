#!/usr/bin/env bash
set -e -o pipefail
mkdir -p qa-layout-report
adb install qa-baseline/app-debug.apk
adb install -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

layout_test=com.playereden.gridmates.wordsearch.QaActualBookcaseAndroidTest
adb shell am instrument -w -r -e class "$layout_test#packedCaseShowsAllThreeBooksAndSceneryAtLargeText,$layout_test#packedCaseSwipesToMorePlayableBooks" \
    com.playereden.gridmates.wordsearch.qa.test/androidx.test.runner.AndroidJUnitRunner \
    | tee qa-layout-report/instrumentation.txt
grep -Eq '^OK \(2 tests\)' qa-layout-report/instrumentation.txt
