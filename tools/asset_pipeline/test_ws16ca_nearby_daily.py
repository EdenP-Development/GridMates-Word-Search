"""WS16CA Nearby Wi-Fi prerequisite, honest Join and shared live panels."""
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
SRC=ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch"

class NearbyDailyCohesionChecks(unittest.TestCase):
    def test_host_ui_and_server_use_same_wifi_prerequisite(self):
        screen=(SRC/"NearbyCoopScreen.kt").read_text()
        runtime=(SRC/"NearbyCoopRuntime.kt").read_text()
        self.assertIn('internal fun hostNetworkAvailable(): Boolean',runtime)
        self.assertIn('runCatching { localWifiIp() != null }',runtime)
        self.assertIn('val wifi = localWifiIp() ?: throw IOException(',runtime)
        self.assertIn('hostNetworkReady = runtime.hostNetworkAvailable()',screen)
        self.assertIn('canOfferNearbyHost(hostNetworkReady, state.busy)',screen)
        self.assertIn('enabled = canStartHost,',screen)
        self.assertIn('testTag("ws16ca-host-button")',screen)
        self.assertIn('testTag("ws16ca-host-recheck")',screen)
        self.assertIn('Text("Check Wi-Fi again")',screen)
        self.assertIn('Text("Wi-Fi needed to host"',screen)
        self.assertIn('runtime.host(ownName,',screen)
        self.assertIn('runtime.search()',screen)
        self.assertIn('Text("Join nearby game")',screen)
        self.assertIn('else "Join manually instead"',screen)
        self.assertIn('runtime.retryInterruptedJoin(ownName)',screen)
        self.assertIn('ready && !busy',screen)
    def test_all_nearby_and_daily_surfaces_share_live_frame(self):
        nearby=(SRC/"NearbyCoopScreen.kt").read_text()
        daily=(SRC/"DailyLandingScreen.kt").read_text()
        self.assertEqual(nearby.count('GridMatesInfoPanel('),5)
        self.assertNotIn('Card(Modifier.fillMaxWidth())',nearby)
        self.assertIn('testTag("ws16ca-nearby-host-panel")',nearby)
        self.assertIn('testTag("ws16ca-nearby-status")',nearby)
        self.assertIn('GridMatesInfoPanel(Modifier.fillMaxWidth().testTag("ws16ca-daily-today"))',daily)
        self.assertNotIn('Card(Modifier.fillMaxWidth())',daily)
        for value in ('PaperProgress(existing?.run?.foundIds?.size ?: 0, run.difficulty.targetCount)',
                      'onPlay(today)', 'past.drop(visiblePage * pageSize).take(pageSize)',
                      'store.profile.dailyCompletions'):
            self.assertIn(value,daily)
        panel=(SRC/"GridMatesInfoPanel.kt").read_text()
        self.assertIn('variant = GridMatesFrameVariant.PANEL',panel)
if __name__=="__main__":
    unittest.main()
