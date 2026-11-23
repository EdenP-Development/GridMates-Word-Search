package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.BookPageSessionRules
import com.playereden.gridmates.wordsearch.core.ClaimStatus
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WS61: Native QA app journals created with the signed WS57/code107 APK must
 * remain readable by WS60/code108 after a real adb install -r, not a reinstall.
 *
 * The first test executes against installed CODE 107. The second test executes
 * against installed CODE 108, with exactly the same instrument test APK.
 * Only an isolated CI emulator is used. Never run this seeding test on
 * someone's actual phone or mutate a production/owner save.
 */
class Ws61SignedUpgradeAndroidTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val legacyProgressFile = "gridmates_wordsearch_ws11_qa_progress_v1.dat"
    private val legacySessionsFile = "gridmates_wordsearch_ws11_qa_sessions_v1.dat"
    private val previewBookId = Ws57WoodlandPreview.BOOK_ID

    @Test fun seedRealCode107SoloLegacyAndWoodlandProgress() {
        // Seed legacy 63-page journals FIRST, exercising WS57's actual
        // safe-copy path into the 81-page QA catalog before adding Woodland.
        val oldCatalog = BookQaPilot.catalog()
        val original = oldCatalog.books.first()
        val originalPage = original.chapters.first().pages.first()
        val oldProgress = BookLibraryStore(context, oldCatalog, legacyProgressFile)
        val oldSessions = BookPageSessionStore(context, oldCatalog, legacySessionsFile)
        assertFalse(oldProgress.blocked)
        assertFalse(oldSessions.blocked)
        assertTrue(oldProgress.bookmark(original.id, originalPage.id))
        assertTrue(oldSessions.open(original.id, originalPage.id))
        val oldBoard = BookPageSessionRules.puzzle(oldCatalog, original.id, originalPage.id)
        val oldTarget = oldBoard.targets.first()
        assertEquals(ClaimStatus.FOUND, oldSessions.claim(
            original.id, originalPage.id,
            oldTarget.positions.first(), oldTarget.positions.last()
        ).status)
        assertNotNull(oldSessions.hint(original.id, originalPage.id))
        assertTrue(oldSessions.tick(original.id, originalPage.id, 17L))

        val runtime = QaBookshelfRuntime(context)
        assertTrue(runtime.enabled)
        val catalog = runtime.catalog
        assertEquals(81, catalog.books.sumOf { book ->
            book.chapters.sumOf { it.pages.size }
        })
        val progress = requireNotNull(runtime.progressStore)
        val sessions = requireNotNull(runtime.sessionStore)
        assertFalse(progress.blocked)
        assertFalse(sessions.blocked)
        assertTrue(originalPage.id in sessions.startedPageIds(original.id))

        // A REAL new WS57 Woodland page, not a synthetic or repurposed ID.
        val preview = requireNotNull(catalog.book(previewBookId))
        val newPage = preview.chapters.first().pages.first()
        assertTrue(progress.bookmark(previewBookId, newPage.id))
        assertTrue(sessions.open(previewBookId, newPage.id))
        val newBoard = BookPageSessionRules.puzzle(catalog, previewBookId, newPage.id)
        val newTarget = newBoard.targets.first()
        assertEquals(ClaimStatus.FOUND, sessions.claim(
            previewBookId, newPage.id,
            newTarget.positions.first(), newTarget.positions.last()
        ).status)
        assertNotNull(sessions.hint(previewBookId, newPage.id))
        assertTrue(sessions.tick(previewBookId, newPage.id, 23L))

        // Real V5 Quick Play and local feedback preferences also survive.
        val solo = SoloStore(context)
        assertTrue(solo.readableForRestore)
        assertTrue(solo.startQuickPlay())
        val soloBoard = SoloProgressRules.puzzle(solo.profile.active)
        val soloWord = soloBoard.targets.first()
        assertEquals(ClaimStatus.FOUND, solo.claim(
            soloWord.positions.first(), soloWord.positions.last()
        ).status)
        solo.updateHapticsPreference(false)
        solo.updateSoundPreference(true)
        solo.flush()
        assertTrue(solo.profile.active.foundIds.isNotEmpty())

        verifyDecodedState("v107 seeded")
    }

    @Test fun verifyIdenticalExistingSavesAfterCode108InstallOver() {
        // The script checks byte-for-byte BEFORE/IMMEDIATELY AFTER install -r.
        // Here we separately prove the newer APK can DECODE the same saves.
        verifyDecodedState("v108 reopened")
    }

    private fun verifyDecodedState(label: String) {
        val oldCatalog = BookQaPilot.catalog()
        val original = oldCatalog.books.first()
        val originalPage = original.chapters.first().pages.first()
        val oldProgress = BookLibraryStore(context, oldCatalog, legacyProgressFile)
        val oldSessions = BookPageSessionStore(context, oldCatalog, legacySessionsFile)
        assertFalse(oldProgress.blocked)
        assertFalse(oldSessions.blocked)
        assertEquals(originalPage.id,
            oldProgress.progress.volumes[original.id]?.bookmarkedPageId)
        val oldTarget = BookPageSessionRules
            .puzzle(oldCatalog, original.id, originalPage.id).targets.first()
        val originalSaved = requireNotNull(oldSessions.session(original.id, originalPage.id))
        assertTrue(oldTarget.id in originalSaved.foundIds)
        assertEquals(1, originalSaved.hints.values.sum())
        assertEquals(17L, originalSaved.elapsedSeconds)

        val runtime = QaBookshelfRuntime(context)
        val catalog = runtime.catalog
        val progress = requireNotNull(runtime.progressStore)
        val sessions = requireNotNull(runtime.sessionStore)
        assertFalse(progress.blocked)
        assertFalse(sessions.blocked)
        val fromLegacy = requireNotNull(sessions.session(original.id, originalPage.id))
        assertTrue(oldTarget.id in fromLegacy.foundIds)
        assertEquals(17L, fromLegacy.elapsedSeconds)
        assertEquals(originalPage.id,
            progress.progress.volumes[original.id]?.bookmarkedPageId)

        val preview = requireNotNull(catalog.book(previewBookId))
        val newPage = preview.chapters.first().pages.first()
        val newTarget = BookPageSessionRules
            .puzzle(catalog, previewBookId, newPage.id).targets.first()
        assertEquals(newPage.id,
            progress.progress.volumes[previewBookId]?.bookmarkedPageId)
        val previewSaved = requireNotNull(sessions.session(previewBookId, newPage.id))
        assertTrue(newTarget.id in previewSaved.foundIds)
        assertEquals(1, previewSaved.hints.values.sum())
        assertEquals(23L, previewSaved.elapsedSeconds)

        val solo = SoloStore(context)
        assertTrue(solo.readableForRestore)
        assertFalse(solo.hapticsEnabled)
        assertTrue(solo.soundEnabled)
        assertNotNull(solo.profile.active.frozen)
        val soloPuzzle = SoloProgressRules.puzzle(solo.profile.active)
        assertTrue(soloPuzzle.targets.first().id in solo.profile.active.foundIds)
        assertTrue(solo.profile.active.foundIds.size < soloPuzzle.targets.size)
        println(
            "WS61_PASS $label: original=${oldTarget.id}, " +
                "woodland=${newTarget.id}, solo=${soloPuzzle.targets.first().id}, " +
                "prefs=haptics-off,sound-on, legacy+preview+solo journals readable"
        )
    }
}
