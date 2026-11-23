package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.BookBoardDigest
import com.playereden.gridmates.wordsearch.core.BookPageSessionRules
import com.playereden.gridmates.wordsearch.core.BookProgressRules
import com.playereden.gridmates.wordsearch.core.WordSearchGeneratorV5
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * WS57 dedicated isolated Android QA preview:
 * - exercises the ACTUAL context-backed Debug BookQaPilot.catalog
 * - loads all 18 authored pages from APK assets and validates the frozen
 *   V5 source hashes and playable word paths
 * - proves that original WS11 QA saved sessions and progress still reopen
 * - never uses the app's real save files or resets anything.
 */
class Ws57PlayableWoodlandPreviewAndroidTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun all18FrozenPagesArePlayableAndEveryLegacyPageIsIdentical() {
        val original = BookQaPilot.catalog()
        val added = BookQaPilot.catalog(context)
        original.requireCompatibleUpgrade(added)
        assertEquals(3, original.books.size)
        assertEquals(4, added.books.size)
        assertEquals(63, original.books.flatMap { b -> b.chapters.flatMap { it.pages } }.size)
        assertEquals(81, added.books.flatMap { b -> b.chapters.flatMap { it.pages } }.size)
        for (book in original.books) {
            assertEquals(book, added.book(book.id))
        }
        val preview = requireNotNull(added.book(Ws57WoodlandPreview.BOOK_ID))
        assertEquals(2, preview.volume)
        assertEquals(6, preview.chapters.size)
        assertEquals(listOf("Along the Trail", "Under the Canopy",
            "Birdsong", "By the Stream", "Quiet Clearings",
            "Forest Discoveries"), preview.chapters.map { it.title })
        for (chapter in preview.chapters) {
            assertEquals(3, chapter.pages.size)
            for ((index, page) in chapter.pages.withIndex()) {
                assertEquals(listOf(12,16,24)[index], page.boardSize.targets)
                assertEquals(WordSearchGeneratorV5.CONTENT_VERSION, page.generatorVersion)
                val puzzle = BookPageSessionRules.puzzle(added, preview.id, page.id)
                assertEquals(page.boardSha256, BookBoardDigest.sha256(puzzle))
                assertEquals(page.boardSize.targets, puzzle.targets.size)
                assertEquals(puzzle.targets.map { it.word }.toSet().size, puzzle.targets.size)
                assertTrue(added.matchesPublishedPuzzle(preview.id, page.id, puzzle))
            }
        }
    }

    @Test fun upgradePreservesOldSavedPageAndAllowsNewPreviewBookmarks() {
        val progressName = "ws57_preview_progress_${System.nanoTime()}.dat"
        val sessionName = "ws57_preview_sessions_${System.nanoTime()}.dat"
        try {
            val old = BookQaPilot.catalog()
            val first = old.books.first()
            val firstPage = first.chapters.first().pages.first()
            val oldProgress = BookLibraryStore(context, old, progressName)
            assertFalse(oldProgress.blocked)
            assertTrue(oldProgress.bookmark(first.id, firstPage.id))
            val oldSession = BookPageSessionStore(context, old, sessionName)
            assertTrue(oldSession.open(first.id, firstPage.id))
            val savedBefore = File(context.filesDir, progressName).readBytes()
            val sessionsBefore = File(context.filesDir, sessionName).readBytes()

            val expanded = BookQaPilot.catalog(context)
            old.requireCompatibleUpgrade(expanded)
            val updatedProgress = BookLibraryStore(context, expanded, progressName)
            val updatedSession = BookPageSessionStore(context, expanded, sessionName)
            assertFalse(updatedProgress.blocked)
            assertFalse(updatedSession.blocked)
            assertEquals(firstPage.id,
                BookProgressRules.continuePage(
                    expanded, updatedProgress.progress, first.id)?.id)
            assertNotNull(updatedSession.session(first.id, firstPage.id))
            assertEquals(savedBefore.toList(),
                requireNotNull(updatedProgress.exportForManualBackup()).toList())
            assertEquals(sessionsBefore.toList(),
                requireNotNull(updatedSession.exportForManualBackup()).toList())

            val preview = requireNotNull(expanded.book(Ws57WoodlandPreview.BOOK_ID))
            val previewPage = preview.chapters.first().pages.first()
            assertTrue(updatedProgress.bookmark(preview.id, previewPage.id))
            assertTrue(updatedSession.open(preview.id, previewPage.id))
            val reopenedProgress = BookLibraryStore(context, expanded, progressName)
            val reopenedSession = BookPageSessionStore(context, expanded, sessionName)
            assertFalse(reopenedProgress.blocked)
            assertFalse(reopenedSession.blocked)
            assertEquals(previewPage.id,
                reopenedProgress.progress.volumes[preview.id]?.bookmarkedPageId)
            assertNotNull(reopenedSession.session(preview.id, previewPage.id))
            assertNotNull(reopenedSession.session(first.id, firstPage.id))
        } finally {
            for (name in listOf(progressName, sessionName)) {
                for (suffix in listOf("", ".bak", ".new"))
                    File(context.filesDir, name + suffix).delete()
            }
        }
    }
    @Test fun runtimeCopiesLegacyQaSavesToNewRollbackSafeFiles() {
        // Four unique names: never read, modify or delete the owner's saves.
        val nonce = System.nanoTime().toString()
        val legacyProgressName = "ws57_old_progress_${nonce}.dat"
        val legacySessionName = "ws57_old_sessions_${nonce}.dat"
        val nextProgressName = "ws57_new_progress_${nonce}.dat"
        val nextSessionName = "ws57_new_sessions_${nonce}.dat"
        val names = listOf(legacyProgressName, legacySessionName,
            nextProgressName, nextSessionName)
        try {
            val legacy = BookQaPilot.catalog()
            val oldBook = legacy.books.first()
            val oldPage = oldBook.chapters.first().pages.first()
            val saved = BookLibraryStore(context, legacy, legacyProgressName)
            assertTrue(saved.bookmark(oldBook.id, oldPage.id))
            val unfinished = BookPageSessionStore(context, legacy, legacySessionName)
            assertTrue(unfinished.open(oldBook.id, oldPage.id))
            val previousProgressBytes =
                File(context.filesDir, legacyProgressName).readBytes()
            val previousSessionBytes =
                File(context.filesDir, legacySessionName).readBytes()

            val runtime = QaBookshelfRuntime(
                context, legacyProgressName, legacySessionName,
                nextProgressName, nextSessionName
            )
            val migratedProgress = requireNotNull(runtime.progressStore)
            val migratedSession = requireNotNull(runtime.sessionStore)
            assertFalse(migratedProgress.blocked)
            assertFalse(migratedSession.blocked)
            assertTrue(File(context.filesDir, nextProgressName).exists())
            assertTrue(File(context.filesDir, nextSessionName).exists())
            assertEquals(previousProgressBytes.toList(),
                File(context.filesDir, legacyProgressName).readBytes().toList())
            assertEquals(previousSessionBytes.toList(),
                File(context.filesDir, legacySessionName).readBytes().toList())
            assertEquals(oldPage.id,
                migratedProgress.progress.volumes[oldBook.id]?.bookmarkedPageId)
            assertNotNull(migratedSession.session(oldBook.id, oldPage.id))

            val preview = requireNotNull(runtime.catalog.book(Ws57WoodlandPreview.BOOK_ID))
            val newPage = preview.chapters.first().pages.first()
            assertTrue(migratedProgress.bookmark(preview.id, newPage.id))
            assertTrue(migratedSession.open(preview.id, newPage.id))
            val again = QaBookshelfRuntime(
                context, legacyProgressName, legacySessionName,
                nextProgressName, nextSessionName
            )
            assertEquals(newPage.id, again.progressStore?.progress
                ?.volumes?.get(preview.id)?.bookmarkedPageId)
            assertNotNull(again.sessionStore?.session(preview.id, newPage.id))
            assertNotNull(again.sessionStore?.session(oldBook.id, oldPage.id))
            // Even after WS57 bookmarks and a new-store restart, the entire
            // old QA journal is byte-for-byte untouched for APK rollback.
            assertEquals(previousProgressBytes.toList(),
                File(context.filesDir, legacyProgressName).readBytes().toList())
            assertEquals(previousSessionBytes.toList(),
                File(context.filesDir, legacySessionName).readBytes().toList())
        } finally {
            for (name in names) for (suffix in listOf("",".bak",".new"))
                File(context.filesDir, name + suffix).delete()
        }
    }

}
