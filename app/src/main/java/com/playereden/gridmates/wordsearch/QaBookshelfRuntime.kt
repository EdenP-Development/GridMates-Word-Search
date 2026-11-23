package com.playereden.gridmates.wordsearch

import android.content.Context
import com.playereden.gridmates.wordsearch.core.BookCatalog
import java.io.File

/**
 * QA-only books use separate local, offline AtomicFile journals. WS57 adds an
 * unapproved preview book with NEW IDs; NEVER rewrite the old WS11 QA save
 * files just to show it. Older signed QA APKs must remain safely installable.
 */
internal class QaBookshelfRuntime(
    context: Context,
    // Optional QA test-only overrides let instrumentation prove migration
    // using unique disposable filenames. Never test by modifying real saves.
    private val previousProgress: String = "gridmates_wordsearch_ws11_qa_progress_v1.dat",
    private val previousSessions: String = "gridmates_wordsearch_ws11_qa_sessions_v1.dat",
    private val previewProgress: String = "gridmates_wordsearch_ws57_qa_progress_v1.dat",
    private val previewSessions: String = "gridmates_wordsearch_ws57_qa_sessions_v1.dat",
    private val chapterProgress: String? = null,
    private val chapterSessions: String? = null
) {
    init {
        require((chapterProgress == null) == (chapterSessions == null))
    }

    private val applicationContext = context.applicationContext
    val enabled: Boolean = BookQaPilot.enabled

    private val originalCatalog by lazy {
        if (enabled) BookQaPilot.catalog() else BookCatalog()
    }
    private val woodlandCatalog by lazy {
        if (enabled) BookQaPilot.catalog(applicationContext) else BookCatalog()
    }
    val catalog: BookCatalog by lazy {
        if (enabled && chapterProgress != null)
            BookQaPilot.appendChapterPreview(applicationContext, woodlandCatalog)
        else woodlandCatalog
    }
    val shelf by lazy { BookShelfPresentation(catalog, BookQaPilot.editorialBookIds) }

    companion object {
        fun installed(context: Context) = QaBookshelfRuntime(
            context,
            chapterProgress = "gridmates_wordsearch_ws66_v110_qa_progress_v1.dat",
            chapterSessions = "gridmates_wordsearch_ws66_v110_qa_sessions_v1.dat"
        )
    }

    private fun alreadySaved(name: String): Boolean {
        val path = File(applicationContext.filesDir, name)
        return path.exists() || File(path.path + ".bak").exists()
    }

    private fun copiedProgress(): BookLibraryStore? {
        val destination = chapterProgress ?: previewProgress
        val newer = BookLibraryStore(applicationContext, catalog, destination)
        if (newer.blocked || alreadySaved(destination)) return newer
        val useWoodland = chapterProgress != null && alreadySaved(previewProgress)
        val source = if (useWoodland) previewProgress else previousProgress
        if (!alreadySaved(source)) return newer
        val older = BookLibraryStore(applicationContext,
            if (useWoodland) woodlandCatalog else originalCatalog, source)
        // Fail closed: a corrupt/unknown legacy journal must be preserved for
        // recovery, not replaced by a silent blank new journal.
        val savedBytes = older.exportForManualBackup() ?: return null
        return newer.takeIf { it.importManualBackup(savedBytes) }
    }

    private fun copiedSessions(): BookPageSessionStore? {
        val destination = chapterSessions ?: previewSessions
        val newer = BookPageSessionStore(applicationContext, catalog, destination)
        if (newer.blocked || alreadySaved(destination)) return newer
        val useWoodland = chapterSessions != null && alreadySaved(previewSessions)
        val source = if (useWoodland) previewSessions else previousSessions
        if (!alreadySaved(source)) return newer
        val older = BookPageSessionStore(applicationContext,
            if (useWoodland) woodlandCatalog else originalCatalog, source)
        val savedBytes = older.exportForManualBackup() ?: return null
        return newer.takeIf { it.importManualBackup(savedBytes) }
    }

    val progressStore: BookLibraryStore? by lazy {
        if (enabled) copiedProgress() else null
    }

    private val pendingSessions = lazy {
        if (enabled) copiedSessions() else null
    }

    val sessionStore: BookPageSessionStore?
        get() = pendingSessions.value

    fun syncBookRewards(solo: SoloStore): Boolean {
        if (!enabled) return true
        val progress = progressStore ?: return true
        if (progress.blocked) return false
        // Candidate books use the real playing flow with QA-only progress.
        val awardable = progress.progress.copy(volumes =
            progress.progress.volumes.filterKeys {
                it !in BookQaPilot.editorialBookIds
            })
        return solo.syncBookRewards(catalog, awardable)
    }

    fun setForeground(foreground: Boolean) {
        if (pendingSessions.isInitialized()) {
            pendingSessions.value?.setForeground(foreground)
        }
    }
}
