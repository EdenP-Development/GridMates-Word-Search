package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.BookPageSessionRules
import com.playereden.gridmates.wordsearch.core.ClaimStatus
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Only runs on a clean emulator with the archived, signed v120 installed. */
class WsBetaV120BackupSeedAndroidTest {
    @Test fun seedProgressForRealSettingsExport() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val solo = SoloStore(context)
        solo.updateDisplayName("V120 Test Reader")
        assertTrue(solo.startQuickPlay())
        val firstBoard = SoloProgressRules.puzzle(solo.profile.active)
        for (word in firstBoard.targets) {
            assertEquals(ClaimStatus.FOUND, solo.claim(word.positions.first(), word.positions.last()).status)
        }
        assertEquals(1, solo.profile.completions)
        assertTrue(solo.equipBoardFont("plex"))

        assertTrue(solo.startQuickPlay())
        val quickWord = SoloProgressRules.puzzle(solo.profile.active).targets.first()
        assertEquals(ClaimStatus.FOUND,
            solo.claim(quickWord.positions.first(), quickWord.positions.last()).status)

        solo.openDaily("2026-11-24")
        val daily = requireNotNull(solo.selectedDaily)
        for (word in SoloProgressRules.puzzle(daily.run).targets) {
            assertEquals(ClaimStatus.FOUND,
                solo.dailyClaim(word.positions.first(), word.positions.last()).status)
        }
        assertEquals(1, solo.profile.dailyCompletions)

        val bookshelf = QaBookshelfRuntime.installed(context)
        val progress = requireNotNull(bookshelf.progressStore)
        val sessions = requireNotNull(bookshelf.sessionStore)
        val book = "woodland-qa-volume-one"
        val complete = "woodland-qa-page-one"
        val partial = "woodland-qa-page-two"
        val finished = BookPageSessionRules.puzzle(bookshelf.catalog, book, complete)
        assertTrue(progress.recordCompletion(book, complete, finished, finished.targets.map { it.id }.toSet()))
        assertTrue(progress.bookmark(book, partial))
        assertTrue(bookshelf.syncBookRewards(solo))
        assertEquals(1, solo.profile.bookCompletions)

        val bookWord = BookPageSessionRules.puzzle(bookshelf.catalog, book, partial).targets.first()
        assertTrue(sessions.open(book, partial))
        assertEquals(ClaimStatus.FOUND,
            sessions.claim(book, partial, bookWord.positions.first(), bookWord.positions.last()).status)
    }
}
