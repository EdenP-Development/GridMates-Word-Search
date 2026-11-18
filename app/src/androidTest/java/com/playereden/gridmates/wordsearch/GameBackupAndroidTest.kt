package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.*
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.Base64

/** Real Android AtomicFile, process-reopen and interrupted multi-file restores. */
class GameBackupAndroidTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val boardA = WordSearchGeneratorV2.generate(WordTheme.WOODLAND,
        PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.CLASSIC, 1701L)
    private val boardB = WordSearchGeneratorV2.generate(WordTheme.WOODLAND,
        PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.CLASSIC, 1702L)
    private val bookId = "woodland-backup-fixture"
    private val catalog = BookCatalog(listOf(PuzzleBook(bookId, "Woodland", 1,
        WordTheme.WOODLAND, listOf(BookChapter("chapter-one", "Trees",
            listOf(boardA, boardB).mapIndexed { index, puzzle ->
                BookPage("page-${index + 1}", puzzle.id, puzzle.contentVersion,
                    "backup-fixture-en-v1", PuzzleBoardSize.BOOK_SHORT,
                    PuzzleChallenge.CLASSIC, puzzle.seed, BookBoardDigest.sha256(puzzle))
            })))))

    private inner class Saves(val prefix: String) {
        val solo = SoloStore(context, "${prefix}_solo.json")
        val progress = BookLibraryStore(context, catalog, "${prefix}_progress.dat")
        val sessions = BookPageSessionStore(context, catalog, "${prefix}_sessions.dat")
        val journalName = "${prefix}_restore.json"
        fun manager() = GameBackupManager(context, solo, books = {
            BookBackupStores(catalog, progress, sessions)
        }, journalName = journalName)
        fun populate() {
            assertTrue(solo.startQuickPlay())
            val p = SoloProgressRules.puzzle(solo.profile.active)
            p.targets.forEach { t -> assertEquals(ClaimStatus.FOUND,
                solo.claim(t.positions.last(), t.positions.first()).status) }
            assertTrue(solo.equipBoardFont("plex"))
            assertTrue(solo.startQuickPlay())
            val current = SoloProgressRules.puzzle(solo.profile.active).targets.first()
            assertEquals(ClaimStatus.FOUND,
                solo.claim(current.positions.first(), current.positions.last()).status)
            assertTrue(progress.recordCompletion(bookId, "page-1", boardA,
                boardA.targets.map { it.id }.toSet()))
            assertTrue(solo.syncBookRewards(catalog, progress.progress))
            assertEquals(1, solo.profile.bookCompletions)
            assertTrue(progress.bookmark(bookId, "page-2"))
            assertTrue(sessions.open(bookId, "page-2"))
            val first = boardB.targets.first()
            assertEquals(ClaimStatus.FOUND, sessions.claim(bookId, "page-2",
                first.positions.last(), first.positions.first()).status)
            assertNotNull(sessions.hint(bookId, "page-2"))
            assertTrue(sessions.tick(bookId, "page-2", 23L))
        }
    }

    private fun withSaves(test: (Saves, Saves) -> Unit) {
        val root = "ws39_${System.nanoTime()}"
        try { test(Saves("${root}_from"), Saves("${root}_to")) }
        finally {
            context.filesDir.listFiles()?.filter { it.name.startsWith(root) }
                ?.forEach { it.deleteRecursively() }
            context.applicationInfo.dataDir.let { dataDir ->
                File(dataDir, "shared_prefs").listFiles()
                    ?.filter { it.name.startsWith(root) }?.forEach { it.delete() }
            }
        }
    }

    @Test fun fullBackupRestoresBookCompletionBookmarkPartialWordsHintsTimeAndProfile() =
        withSaves { source, target ->
            source.populate()
            val raw = source.manager().exportBackup()
            val manager = target.manager()
            val preview = manager.previewBackup(raw)
            assertTrue(preview.includesBooks)
            assertEquals(1, preview.completedPages)
            assertEquals(1, preview.savedPages)
            assertTrue(manager.restoreBackup(raw))
            assertFalse(manager.needsRecovery)
            val reopened = Saves(target.prefix)
            assertEquals(source.solo.profile, reopened.solo.profile)
            assertNotNull(reopened.solo.profile.active.frozen)
            assertEquals(1, reopened.solo.profile.bookCompletions)
            assertTrue(reopened.solo.syncBookRewards(catalog, reopened.progress.progress))
            assertEquals(1, reopened.solo.profile.bookCompletions)
            assertEquals("plex", reopened.solo.effectiveBoardFontId)
            assertEquals(source.progress.progress, reopened.progress.progress)
            assertEquals(source.sessions.journal, reopened.sessions.journal)
            assertEquals(23L, reopened.sessions.session(bookId, "page-2")?.elapsedSeconds)
            assertEquals(1, reopened.sessions.session(bookId, "page-2")?.foundIds?.size)
            assertFalse(reopened.sessions.session(bookId, "page-2")?.hints.isNullOrEmpty())
        }

    @Test fun legacySoloBackupLeavesExistingBookProgressAndPartialPagesAlone() =
        withSaves { source, target ->
            target.populate()
            val beforeProgress = target.progress.progress
            val beforeSessions = target.sessions.journal
            val raw = source.solo.exportBackup()
            val manager = target.manager()
            assertFalse(manager.previewBackup(raw).includesBooks)
            assertTrue(manager.restoreBackup(raw))
            assertEquals(source.solo.profile, target.solo.profile)
            assertEquals(beforeProgress, target.progress.progress)
            assertEquals(beforeSessions, target.sessions.journal)
        }

    @Test fun invalidOuterOrNestedChecksumsCannotChangeAnySave() =
        withSaves { source, target ->
            source.populate()
            val raw = source.manager().exportBackup()
            target.solo.flush()
            val before = target.solo.exportBackup()
            val envelope = JSONObject(raw)
            val payload = JSONObject(envelope.getString("payload"))
            payload.getJSONObject("books").put("sessions",
                Base64.getEncoder().encodeToString(byteArrayOf(1, 2, 3)))
            envelope.put("payload", payload.toString())
            val manager = target.manager()
            assertFalse(manager.restoreBackup(envelope.toString()))
            envelope.put("sha256", GameBackupManager.sha256(payload.toString()))
            assertFalse(manager.restoreBackup(envelope.toString()))
            assertEquals(before, target.solo.exportBackup())
            assertTrue(target.progress.progress.volumes.isEmpty())
            assertTrue(target.sessions.journal.sessions.isEmpty())
            assertFalse(File(context.filesDir, target.journalName).exists())
        }

    @Test fun pendingRestoreReplaysAfterDeathBetweenEachFileAndNeverReplaysAfterCommit() =
        withSaves { source, target ->
            source.populate()
            val raw = source.manager().exportBackup()
            // Simulate process death after 0, 1, or 2 of the three save commits.
            for (committedFiles in 0..2) {
                val fresh = Saves("${target.prefix}_$committedFiles")
                val preview = fresh.manager().previewBackup(raw)
                File(context.filesDir, fresh.journalName).writeText(raw)
                if (committedFiles >= 1) assertTrue(fresh.solo.restoreBackup(preview.profile))
                if (committedFiles >= 2) assertTrue(fresh.progress.importManualBackup(
                    requireNotNull(preview.bookProgress)))
                val restarted = Saves(fresh.prefix)
                assertFalse(restarted.manager().needsRecovery)
                assertEquals(source.solo.profile, restarted.solo.profile)
                assertEquals(source.progress.progress, restarted.progress.progress)
                assertEquals(source.sessions.journal, restarted.sessions.journal)
                // Cleanup can fail after a successful restore. A tombstone
                // must not erase a NEW puzzle made after that restore.
                assertTrue(restarted.solo.startQuickPlay())
                val newer = restarted.solo.profile
                File(context.filesDir, restarted.journalName)
                    .writeText(GameBackupManager.COMMITTED)
                val again = Saves(fresh.prefix)
                assertFalse(again.manager().needsRecovery)
                assertEquals(newer, again.solo.profile)
            }
        }

    @Test fun diskWriteFailureKeepsPendingBackupAndRecoversAfterStorageIsAvailable() =
        withSaves { source, target ->
            source.populate()
            val raw = source.manager().exportBackup()
            // An unwritable AtomicFile .new path causes a real Android IO error.
            val obstruction = File(context.filesDir, "${target.prefix}_progress.dat.new")
            assertTrue(obstruction.mkdir())
            val manager = target.manager()
            assertFalse(manager.restoreBackup(raw))
            assertTrue(manager.needsRecovery)
            assertEquals(raw, File(context.filesDir, target.journalName).readText())
            assertTrue(obstruction.delete())
            val restarted = Saves(target.prefix)
            assertFalse(restarted.manager().needsRecovery)
            assertEquals(source.solo.profile, restarted.solo.profile)
            assertEquals(source.progress.progress, restarted.progress.progress)
            assertEquals(source.sessions.journal, restarted.sessions.journal)
        }

    @Test fun unavailableCatalogAndUnreadableDeviceSavesAreRejectedBeforeStaging() =
        withSaves { source, target ->
            source.populate()
            val raw = source.manager().exportBackup()
            val noBooks = GameBackupManager(context, target.solo, { null },
                "${target.prefix}_missing.json")
            assertFalse(noBooks.restoreBackup(raw))
            val file = File(context.filesDir, "${target.prefix}_progress.dat")
            val corrupt = byteArrayOf(4, 3, 2, 1)
            file.writeBytes(corrupt)
            val unreadable = Saves(target.prefix)
            assertFalse(unreadable.manager().restoreBackup(raw))
            assertArrayEquals(corrupt, file.readBytes())
            assertFalse(File(context.filesDir, target.journalName).exists())
        }
}
