package com.playereden.gridmates.wordsearch

import android.os.SystemClock
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.CoopSeat
import com.playereden.gridmates.wordsearch.core.NearbyMatch
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WS62: REAL NearbyCoopRuntime host/guest objects over Android TCP sockets.
 *
 * This does NOT simulate two phones. Both distinct runtime objects are in
 * ONE isolated API35 emulator app/process, so NSD discovery across handsets,
 * router isolation, real RF/wifi loss, Android background scheduling and
 * separate-app private credentials still need physical two-device QA.
 *
 * The emulator must expose a genuine site-local wlan/ap interface: the
 * production host() requirement remains unchanged, not bypassed for tests.
 * No Solo/Library profiles, board content or saved puzzle files are modified.
 */
class Ws62NearbyTcpIntegrationAndroidTest {
    private val app get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun await(label: String, timeoutMs: Long = 12_000, condition: () -> Boolean) {
        val end = SystemClock.elapsedRealtime() + timeoutMs
        while (SystemClock.elapsedRealtime() < end) {
            if (condition()) return
            Thread.sleep(50)
        }
        throw AssertionError("WS62 timeout: $label")
    }

    private inline fun withConnected(
        mode: NearbyMatch, block: (NearbyCoopRuntime, NearbyCoopRuntime) -> Unit
    ) {
        val host = NearbyCoopRuntime(app)
        val guest = NearbyCoopRuntime(app)
        try {
            assertTrue(
                "WS62 requires the emulator's real site-local wlan/ap IPv4 interface; " +
                    "no test-only bypass of production Wi-Fi host rule",
                host.hostNetworkAvailable()
            )
            host.host("WS62 Host", WordTheme.WOODLAND,
                PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, mode)
            await("$mode actual host TCP socket") {
                host.state.role == NearbyRole.HOST &&
                    host.state.port in 1..65535 && host.state.address.isNotBlank() &&
                    host.state.snapshot != null
            }
            val address = host.state.address
            val port = host.state.port
            val room = host.state.roomId
            guest.join(address, port, room, "", "WS62 Guest")
            await("$mode real TCP welcome/accept/joined handshake") {
                host.state.snapshot?.guestConnected == true &&
                    guest.state.role == NearbyRole.GUEST &&
                    guest.state.snapshot?.guestConnected == true &&
                    guest.state.roomId == room
            }
            assertEquals(mode, host.state.snapshot?.match)
            assertEquals(mode, guest.state.snapshot?.match)
            assertEquals(host.state.snapshot?.spec, guest.state.snapshot?.spec)
            Log.i("WS62_NETWORK_QA", "$mode actual socket handshake passed")
            block(host, guest)
        } finally {
            guest.close()
            host.close()
        }
    }

    @Test fun coopGuestClaimsChatAndReconnectKeepAuthoritativeProgress() {
        withConnected(NearbyMatch.COOP) { host, guest ->
            val game = requireNotNull(host.state.snapshot).spec.regenerate()
            val target = game.targets.first()
            guest.claim(target.positions.first(), target.positions.last())
            await("Co-op guest real socket claim and FOUND receipt") {
                host.state.snapshot?.claims?.any {
                    it.wordId == target.id && it.seat == CoopSeat.GUEST
                } == true &&
                    guest.state.snapshot?.foundIds?.contains(target.id) == true &&
                    guest.state.claimReceipt?.status == "FOUND"
            }
            assertTrue("Co-op guest chat submission", guest.sendChat("Together!"))
            await("Co-op guest chat delivered and confirmed on both sockets") {
                host.state.chat.count { it.text == "Together!" } == 1 &&
                    guest.state.chat.count { it.text == "Together!" } == 1 &&
                    guest.state.pendingChat == null
            }
            val room = host.state.roomId
            val port = host.state.port
            val address = host.state.address

            // A disconnected guest must not clear host-owned progress or
            // reserve a new seat. The persisted token is used for rejoin.
            guest.close()
            await("Host notices guest socket disconnect") {
                host.state.snapshot?.guestConnected == false
            }
            val returning = NearbyCoopRuntime(app)
            try {
                returning.join(address, port, room, "", "WS62 Guest")
                await("Real socket rejoin using durable room credential") {
                    returning.state.role == NearbyRole.GUEST &&
                        returning.state.snapshot?.guestConnected == true &&
                        host.state.snapshot?.guestConnected == true
                }
                assertTrue(
                    requireNotNull(returning.state.snapshot).foundIds.contains(target.id)
                )
                assertEquals(1,
                    returning.state.chat.count { it.text == "Together!" })
                Log.i("WS62_NETWORK_QA",
                    "COOP real claim, receipt, chat and disconnect/reconnect passed")
            } finally {
                returning.close()
            }
        }
    }

    @Test fun raceFirstHostClaimWinsAndGuestCannotStealButCanScoreNext() {
        withConnected(NearbyMatch.SHARED_BOARD_RACE) { host, guest ->
            val board = requireNotNull(host.state.snapshot).spec.regenerate()
            val first = board.targets[0]
            val second = board.targets[1]
            host.claim(first.positions.first(), first.positions.last())
            await("Race host's first authoritative claim visible to guest") {
                host.state.snapshot?.claims?.any {
                    it.wordId == first.id && it.seat == CoopSeat.HOST
                } == true && guest.state.snapshot?.claims?.any {
                    it.wordId == first.id && it.seat == CoopSeat.HOST
                } == true
            }
            guest.claim(first.positions.first(), first.positions.last())
            await("Race guest receives ALREADY_FOUND and no stolen point") {
                guest.state.claimReceipt?.status == "ALREADY_FOUND"
            }
            assertEquals(1, requireNotNull(host.state.snapshot).hostWords)
            assertEquals(0, requireNotNull(host.state.snapshot).guestWords)

            guest.claim(second.positions.first(), second.positions.last())
            await("Race guest receives actual FOUND and one own point") {
                guest.state.claimReceipt?.status == "FOUND" &&
                    host.state.snapshot?.claims?.any {
                        it.wordId == second.id && it.seat == CoopSeat.GUEST
                    } == true
            }
            assertEquals(1, requireNotNull(host.state.snapshot).hostWords)
            assertEquals(1, requireNotNull(host.state.snapshot).guestWords)
            assertFalse(requireNotNull(host.state.snapshot).finished)
            Log.i("WS62_NETWORK_QA",
                "RACE host first claim, guest rejected steal, next guest score passed")
        }
    }

    @Test fun duelReadyGoAndIndependentPrivateWordLedgersOverRealSockets() {
        withConnected(NearbyMatch.SPEED_DUEL) { host, guest ->
            val initial = requireNotNull(guest.state.snapshot)
            assertFalse(requireNotNull(initial.duel).started)
            assertEquals(CoopSeat.GUEST, initial.duel?.recipient)
            assertTrue(initial.duel?.ownFoundIds.orEmpty().isEmpty())

            // Real user-flow buttons call exactly these runtime methods.
            guest.readySpeedDuel()
            host.readySpeedDuel()
            await("Duel host and guest receive ready/go") {
                host.state.snapshot?.duel?.started == true &&
                    guest.state.snapshot?.duel?.started == true
            }
            val board = requireNotNull(host.state.snapshot).spec.regenerate()
            val target = board.targets.first()
            host.claim(target.positions.first(), target.positions.last())
            await("Duel host private word accepted and guest sees count only") {
                host.state.snapshot?.duel?.ownFoundIds?.contains(target.id) == true &&
                    guest.state.snapshot?.duel?.hostCount == 1
            }
            val guestBefore = requireNotNull(guest.state.snapshot).duel
            assertNotNull(guestBefore)
            assertFalse(requireNotNull(guestBefore).ownFoundIds.contains(target.id))
            assertEquals(0, guestBefore.guestCount)

            // In Speed Duel BOTH players may find the SAME word privately.
            guest.claim(target.positions.first(), target.positions.last())
            await("Duel guest own word accepted without host private IDs leaking") {
                guest.state.claimReceipt?.status == "FOUND" &&
                    guest.state.snapshot?.duel?.guestCount == 1 &&
                    host.state.snapshot?.duel?.guestCount == 1
            }
            val hostView = requireNotNull(host.state.snapshot?.duel)
            val guestView = requireNotNull(guest.state.snapshot?.duel)
            assertEquals(CoopSeat.HOST, hostView.recipient)
            assertEquals(CoopSeat.GUEST, guestView.recipient)
            assertEquals(setOf(target.id), hostView.ownFoundIds)
            assertEquals(setOf(target.id), guestView.ownFoundIds)
            assertTrue(requireNotNull(guest.state.snapshot).claims.isEmpty())
            Log.i("WS62_NETWORK_QA",
                "DUEL real ready/go, separate private claims and count-only sharing passed")
        }
    }
}
