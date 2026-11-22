package com.playereden.gridmates.wordsearch

import android.content.Context
import com.playereden.gridmates.wordsearch.core.BookCatalog
import java.io.File

/**
 * QA-only books use separate local, offline AtomicFile journals. WS57 adds an
 * unapproved preview book with NEW IDs; NEVER rewrite the old WS11 QA save
 * files just to show it. Older signed QA APKs must remain safely installable.
 */
internal class QaBookshelfRuntime(context: Context) {
    private val applicationContext = context.applicationContext
    val enabled: Boolean = BookQaPilot.enabled

    private val previousProgress = "gridmates_wordsearch_ws11_qa_progress_v1.dat"
    private val previousSessions = "gridmates_wordsearch_ws11_qa_sessions_v1.dat"
    private val previewProgress = "gridmates_wordsearch_ws57_qa_progress_v1.dat"
    private val previewSessions = "gridmates_wordsearch_ws57_qa_sessions_v1.dat"

    private val originalCatalog by lazy {
        if (enabled) BookQaPilot.catalog() else BookCatalog()
    }
    val catalog: BookCatalog by lazy {
        if (enabled) BookQaPilot.catalog(applicationContext) else BookCatalog()
    }

    private fun alreadySaved(name: String): Boolean {
        val path = File(applicationContext.filesDir, name)
        return path.exists() || File(path.path + ".bak").exists()
    }

    private fun copiedProgress(): BookLibraryStore? {
        val newer = BookLibraryStore(applicationContext, catalog, previewProgress)
        if (newer.blocked || alreadySaved(previewProgress)) return newer
        if (!alreadySaved(previousProgress)) return newer
        val older = BookLibraryStore(applicationContext, originalCatalog, previousProgress)
        // Fail closed: a corrupt/unknown legacy journal must be preserved for
        // recovery, not replaced by a silent blank new journal.
        val savedBytes = older.exportForManualBackup() ?: return null
        return newer.takeIf { it.importManualBackup(savedBytes) }
    }

    private fun copiedSessions(): BookPageSessionStore? {
        val newer = BookPageSessionStore(applicationContext, catalog, previewSessions)
        if (newer.blocked || alreadySaved(previewSessions)) return newer
        if (!alreadySaved(previousSessions)) return newer
        val older = BookPageSessionStore(applicationContext, originalCatalog, previousSessions)
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
        // Candidate WS57 word lists can be explored but never award permanent
        // Solo cosmetic/collection rewards while editorially unapproved.
        val awardable = progress.progress.copy(volumes =
            progress.progress.volumes.filterKeys {
                it != "woodland-ws57-qa-volume-two"
            })
        return solo.syncBookRewards(catalog, awardable)
    }

    fun setForeground(foreground: Boolean) {
        if (pendingSessions.isInitialized()) {
            pendingSessions.value?.setForeground(foreground)
        }
    }
}
