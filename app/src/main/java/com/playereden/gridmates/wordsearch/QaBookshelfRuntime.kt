package com.playereden.gridmates.wordsearch

import android.content.Context
import com.playereden.gridmates.wordsearch.core.BookCatalog
import java.io.File

/**
 * Each QA catalog extension copies the latest compatible book journals into
 * its own namespace so earlier signed builds can still read their journals.
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
    private val chapterSessions: String? = null,
    private val varietyProgress: String? = null,
    private val varietySessions: String? = null
) {
    init {
        require((chapterProgress == null) == (chapterSessions == null))
        require((varietyProgress == null) == (varietySessions == null))
        require(varietyProgress == null || chapterProgress != null)
    }

    private val applicationContext = context.applicationContext
    val enabled: Boolean = BookQaPilot.enabled

    private val originalCatalog by lazy {
        if (enabled) BookQaPilot.catalog() else BookCatalog()
    }
    private val woodlandCatalog by lazy {
        if (enabled) BookQaPilot.catalog(applicationContext) else BookCatalog()
    }
    private val chapterCatalog: BookCatalog by lazy {
        if (enabled && chapterProgress != null)
            BookQaPilot.appendChapterPreview(applicationContext, woodlandCatalog)
        else woodlandCatalog
    }
    val catalog: BookCatalog by lazy {
        if (enabled && varietyProgress != null)
            BookQaPilot.appendVarietyPreview(applicationContext, chapterCatalog)
        else chapterCatalog
    }
    val shelf by lazy { BookShelfPresentation(catalog, BookQaPilot.editorialBookIds) }

    companion object {
        fun installed(context: Context) = QaBookshelfRuntime(
            context,
            chapterProgress = "gridmates_wordsearch_ws66_v110_qa_progress_v1.dat",
            chapterSessions = "gridmates_wordsearch_ws66_v110_qa_sessions_v1.dat",
            varietyProgress = "gridmates_wordsearch_ws73_v116_qa_progress_v1.dat",
            varietySessions = "gridmates_wordsearch_ws73_v116_qa_sessions_v1.dat"
        )
    }

    private fun alreadySaved(name: String): Boolean {
        val path = File(applicationContext.filesDir, name)
        return path.exists() || File(path.path + ".bak").exists()
    }

    private data class SourceJournal(val name: String, val catalog: BookCatalog)

    private fun migrationSource(previous: String, preview: String, chapter: String?): SourceJournal? =
        when {
            varietyProgress != null && chapter != null && alreadySaved(chapter) ->
                SourceJournal(chapter, chapterCatalog)
            chapter != null && alreadySaved(preview) -> SourceJournal(preview, woodlandCatalog)
            alreadySaved(previous) -> SourceJournal(previous, originalCatalog)
            else -> null
        }

    private fun copiedProgress(): BookLibraryStore? {
        val destination = varietyProgress ?: chapterProgress ?: previewProgress
        val newer = BookLibraryStore(applicationContext, catalog, destination)
        if (newer.blocked || alreadySaved(destination)) return newer
        val source = migrationSource(previousProgress, previewProgress, chapterProgress) ?: return newer
        val older = BookLibraryStore(applicationContext, source.catalog, source.name)
        // Unknown or corrupt source data must block migration and preserve the original.
        val savedBytes = older.exportForManualBackup() ?: return null
        return newer.takeIf { it.importManualBackup(savedBytes) }
    }

    private fun copiedSessions(): BookPageSessionStore? {
        val destination = varietySessions ?: chapterSessions ?: previewSessions
        val newer = BookPageSessionStore(applicationContext, catalog, destination)
        if (newer.blocked || alreadySaved(destination)) return newer
        val source = migrationSource(previousSessions, previewSessions, chapterSessions) ?: return newer
        val older = BookPageSessionStore(applicationContext, source.catalog, source.name)
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
