package com.playereden.gridmates.wordsearch

import android.content.Context
import com.playereden.gridmates.wordsearch.core.BookCatalog

/**
 * App-local, offline-only preview runtime, lazy to avoid slowing normal Home.
 * The release source set has no QA books. These files have NEW names and never
 * touch existing Solo schema-5, Daily, Nearby or production book saves.
 */
internal class QaBookshelfRuntime(context: Context) {
    private val applicationContext = context.applicationContext
    val enabled: Boolean = BookQaPilot.enabled

    val catalog: BookCatalog by lazy {
        if (enabled) BookQaPilot.catalog(applicationContext) else BookCatalog()
    }

    val progressStore: BookLibraryStore? by lazy {
        if (enabled) BookLibraryStore(
            applicationContext, catalog,
            "gridmates_wordsearch_ws11_qa_progress_v1.dat"
        ) else null
    }

    private val pendingSessions = lazy {
        if (enabled) BookPageSessionStore(
            applicationContext, catalog,
            "gridmates_wordsearch_ws11_qa_sessions_v1.dat"
        ) else null
    }

    val sessionStore: BookPageSessionStore?
        get() = pendingSessions.value

    fun syncBookRewards(solo: SoloStore): Boolean {
        if (!enabled) return true
        val progress = progressStore ?: return true
        if (progress.blocked) return false
        // WS57 candidate editorial pages are playable for QA only: they MUST NOT
        // grant permanent Solo collectibles or modify old profile rewards.
        val awardable = progress.progress.copy(volumes =
            progress.progress.volumes.filterKeys {
                it != "woodland-ws57-qa-volume-two"
            })
        return solo.syncBookRewards(catalog, awardable)
    }

    fun setForeground(foreground: Boolean) {
        // Do not generate nine QA puzzles when opening ordinary Solo or Home.
        if (pendingSessions.isInitialized()) {
            pendingSessions.value?.setForeground(foreground)
        }
    }
}
