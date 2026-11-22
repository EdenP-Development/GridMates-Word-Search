#!/usr/bin/env bash
# WS61 — real signed v107 -> v108 QA APK replacement, preserving nonempty
# legacy book, Woodland preview and Solo saves + local feedback settings.
# Runs ONLY on an empty disposable GitHub Actions Android emulator.
set -euo pipefail

PKG="com.playereden.gridmates.wordsearch.qa"
ACTIVITY="com.playereden.gridmates.wordsearch.MainActivity"
OLD="qa-baseline/app-debug.apk"
NEW="app/build/outputs/apk/debug/app-debug.apk"
TEST="app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"
TEST_CLASS="com.playereden.gridmates.wordsearch.Ws61SignedUpgradeAndroidTest"
RUNNER="$PKG.test/androidx.test.runner.AndroidJUnitRunner"
REPORT="qa-ws61-signed-upgrade"
mkdir -p "$REPORT/before" "$REPORT/after"

# All files were created using v107 production/debug runtime, not pasted
# arbitrary JSON. Include the old WS11 raw journals, migrated WS57 journals,
# current Solo profile and the player's real saved haptics/sound preference.
SAVES=(
  "files/gridmates_wordsearch_ws11_qa_progress_v1.dat"
  "files/gridmates_wordsearch_ws11_qa_sessions_v1.dat"
  "files/gridmates_wordsearch_ws57_qa_progress_v1.dat"
  "files/gridmates_wordsearch_ws57_qa_sessions_v1.dat"
  "files/gridmates_wordsearch_solo_v1.json"
  "shared_prefs/gridmates_wordsearch_solo_v1.json_feedback.xml"
)
for path in "$OLD" "$NEW" "$TEST"; do test -s "$path"; done

AAPT="$ANDROID_HOME/build-tools/35.0.0/aapt"
APKSIGNER="$ANDROID_HOME/build-tools/35.0.0/apksigner"
"$AAPT" dump badging "$OLD" | grep -E "package: name='$PKG' versionCode='107'" \
  > "$REPORT/v107-badging.txt"
"$AAPT" dump badging "$NEW" | grep -E "package: name='$PKG' versionCode='108'" \
  > "$REPORT/v108-badging.txt"
"$APKSIGNER" verify --print-certs "$OLD" > "$REPORT/v107-apk-cert.txt"
"$APKSIGNER" verify --print-certs "$NEW" > "$REPORT/v108-apk-cert.txt"
grep 'Signer #1 certificate SHA-256 digest:' "$REPORT/v107-apk-cert.txt" \
  > "$REPORT/v107-cert-digest.txt"
grep 'Signer #1 certificate SHA-256 digest:' "$REPORT/v108-apk-cert.txt" \
  > "$REPORT/v108-cert-digest.txt"
diff -u "$REPORT/v107-cert-digest.txt" "$REPORT/v108-cert-digest.txt"
sha256sum "$OLD" "$NEW" > "$REPORT/both-apk-checksums.txt"

adb install "$OLD" | tee "$REPORT/install-v107.txt"
adb shell dumpsys package "$PKG" | grep -m 1 'versionCode=107'
adb install -t "$TEST" | tee "$REPORT/install-instrumentation.txt"

adb shell am instrument -w -r \
  -e class "$TEST_CLASS#seedRealCode107SoloLegacyAndWoodlandProgress" \
  "$RUNNER" | tee "$REPORT/v107-seed-test.txt"
grep -Eq '^OK \(1 test\)' "$REPORT/v107-seed-test.txt"
if grep -Eq 'FAILURES!!!|INSTRUMENTATION_FAILED|Process crashed' \
  "$REPORT/v107-seed-test.txt"; then exit 1; fi
adb shell am force-stop "$PKG" || true

# Preserve ACTUAL raw bytes and SHA-256 of each v107 private app file,
# then assert adb install -r itself changes NONE of them.
for file in "${SAVES[@]}"; do
  name="${file##*/}"
  adb exec-out run-as "$PKG" cat "$file" > "$REPORT/before/$name"
  test -s "$REPORT/before/$name" || { echo "Empty old save: $file"; exit 1; }
done
(cd "$REPORT/before" && sha256sum * | sort) > "$REPORT/before-sha256.txt"

adb install -r "$NEW" | tee "$REPORT/install-v108-over-v107.txt"
grep -F 'Success' "$REPORT/install-v108-over-v107.txt"
adb shell dumpsys package "$PKG" | grep -m 1 'versionCode=108'
for file in "${SAVES[@]}"; do
  name="${file##*/}"
  adb exec-out run-as "$PKG" cat "$file" > "$REPORT/after/$name"
  cmp "$REPORT/before/$name" "$REPORT/after/$name" ||
    { echo "PRIVATE USER FILE CHANGED DURING APK INSTALL: $file"; exit 1; }
done
(cd "$REPORT/after" && sha256sum * | sort) > "$REPORT/after-sha256.txt"
diff -u "$REPORT/before-sha256.txt" "$REPORT/after-sha256.txt"

# Verify the updated Android APK really launches. The second instrumentation
# test reopens and decodes old legacy + Woodland journals and Solo/prefs.
adb shell am start -W -n "$PKG/$ACTIVITY" | tee "$REPORT/v108-activity-start.txt"
adb shell pidof "$PKG" >/dev/null
adb shell am force-stop "$PKG" || true
adb shell am instrument -w -r \
  -e class "$TEST_CLASS#verifyIdenticalExistingSavesAfterCode108InstallOver" \
  "$RUNNER" | tee "$REPORT/v108-reopen-test.txt"
grep -Eq '^OK \(1 test\)' "$REPORT/v108-reopen-test.txt"
if grep -Eq 'FAILURES!!!|INSTRUMENTATION_FAILED|Process crashed' \
  "$REPORT/v108-reopen-test.txt"; then exit 1; fi

adb logcat -d -s AndroidRuntime:E > "$REPORT/android-runtime.txt" || true
echo "WS61 PASS: v107 seeded real legacy+Woodland+Solo saves, 6 raw private files" \
  "byte-identical immediately after signed v108 install-over, new APK launched," \
  "and all old saved progress, words, hints, time and prefs decoded successfully."
