#!/usr/bin/env bash
# WS62: Real Android NearbyCoopRuntime host/guest transport over live TCP.
# BOTH runtimes are in one disposable emulator process, NOT two phones.
# No production networking/NSD policies are bypassed.
set -euo pipefail
package=com.playereden.gridmates.wordsearch.qa
report=qa-ws62-nearby-tcp
mkdir -p "$report"
adb logcat -c || true
adb install -r app/build/outputs/apk/debug/app-debug.apk | tee "$report/install-qa-v108.txt"
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
  | tee "$report/install-instrumentation.txt"
adb shell dumpsys package "$package" | grep -m 1 "versionCode=108" \
  > "$report/installed-version.txt"

# The actual host() still enforces a site-local wlan/ap IPv4 address.
# Save network interface diagnostics so a missing emulator Wi-Fi interface
# is recognized as infrastructure, not a silently bypassed game gate.
adb shell ip -4 addr show > "$report/emulator-ipv4.txt" || true
adb shell dumpsys wifi > "$report/emulator-wifi-dump.txt" || true

target="$package.test/androidx.test.runner.AndroidJUnitRunner"
class="com.playereden.gridmates.wordsearch.Ws62NearbyTcpIntegrationAndroidTest"
set +e
adb shell am instrument -w -r -e class \
  "$class#coopGuestClaimsChatAndReconnectKeepAuthoritativeProgress,$class#raceFirstHostClaimWinsAndGuestCannotStealButCanScoreNext,$class#duelReadyGoAndIndependentPrivateWordLedgersOverRealSockets" \
  "$target" | tee "$report/three-live-tcp-modes.txt"
result=${PIPESTATUS[0]}
set -e
adb logcat -d -s WS62_NETWORK_QA:I AndroidRuntime:E \
  > "$report/nearby-transport-logcat.txt" || true
if [ "$result" -ne 0 ] || \
  ! grep -Eq '^OK \(3 tests\)' "$report/three-live-tcp-modes.txt" || \
  grep -Eq 'FAILURES!!!|INSTRUMENTATION_FAILED|Process crashed' \
    "$report/three-live-tcp-modes.txt"; then
  echo "WS62 FAILED or blocked: check real TCP/Android Wi-Fi evidence" >&2
  exit 1
fi
grep -F 'COOP real claim, receipt, chat and disconnect/reconnect passed' \
  "$report/nearby-transport-logcat.txt"
grep -F 'RACE host first claim, guest rejected steal, next guest score passed' \
  "$report/nearby-transport-logcat.txt"
grep -F 'DUEL real ready/go, separate private claims and count-only sharing passed' \
  "$report/nearby-transport-logcat.txt"
echo "WS62 PASS: three LIVE Android TCP host/guest modes in one emulator; " \
  "this does not certify separate-phone NSD discovery or physical Wi-Fi."
