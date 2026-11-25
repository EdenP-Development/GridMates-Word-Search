package com.playereden.gridmates.wordsearch

import android.content.Context
import android.util.AtomicFile
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.playereden.gridmates.wordsearch.core.BoardFontCollection
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookPageSessionCodec
import com.playereden.gridmates.wordsearch.core.BookProgressCodec
import com.playereden.gridmates.wordsearch.core.SoloProfile
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest
import java.util.Base64

internal data class BookBackupStores(
    val catalog: BookCatalog,
    val progress: BookLibraryStore,
    val sessions: BookPageSessionStore
)

internal data class GameBackupPreview(
    val raw: String,
    val profile: SoloProfile,
    val bookProgress: ByteArray? = null,
    val bookSessions: ByteArray? = null,
    val completedPages: Int = 0,
    val savedPages: Int = 0,
    val boardFontId: String? = null,
    val portableRestoreJournal: String? = null
) {
    val includesBooks: Boolean get() = bookProgress != null
}

/**
 * Portable envelope around the EXISTING Solo JSON and checksummed book codecs.
 * Validate all components before changing any save. An atomic write-ahead file
 * makes a restore across three files recoverable after storage failure or death.
 * Until it completes, Shell offers recovery instead of starting another game.
 * A committed tombstone is durable BEFORE play is allowed, so a failed journal
 * deletion can never replay an old backup over subsequent progress.
 */
internal class GameBackupManager(
    context: Context,
    private val solo: SoloStore,
    private val books: () -> BookBackupStores?,
    journalName: String = "gridmates_wordsearch_restore_v1.json",
    private val allowRestoreWithoutCatalog: Boolean = false
) {
    private val journal: AtomicFile
    var needsRecovery by mutableStateOf(false)
        private set
    var message by mutableStateOf("")
        private set

    init {
        require(Regex("[a-z0-9_-]{3,80}\\.json").matches(journalName))
        journal = AtomicFile(File(context.filesDir, journalName))
        if (journal.baseFile.exists() || File(journal.baseFile.path + ".bak").exists()) {
            needsRecovery = true
            retryRestore()
        }
    }

    fun exportBackup(): String {
        check(!needsRecovery) { "Finish the pending restore first" }
        val profile = JSONObject(solo.exportBackup())
        val library = books()
        val bookData = library?.let {
            JSONObject()
                .put("progress", Base64.getEncoder().encodeToString(
                    checkNotNull(it.progress.exportForManualBackup())))
                .put("sessions", Base64.getEncoder().encodeToString(
                    checkNotNull(it.sessions.exportForManualBackup())))
        }
        val payload = JSONObject().put("solo", profile)
            .put("boardFont", solo.effectiveBoardFontId)
            .put("books", bookData ?: JSONObject.NULL)
        val raw = encodeEnvelope(payload)
        require(raw.toByteArray(Charsets.UTF_8).size <= BackupDocumentReader.MAX_BYTES)
        return raw
    }

    /** Also accepts schema-1..5 legacy exports, which leave existing books alone. */
    fun previewBackup(raw: String): GameBackupPreview {
        require(raw.toByteArray(Charsets.UTF_8).size <= BackupDocumentReader.MAX_BYTES)
        val envelope = JSONObject(raw)
        if (!envelope.has("format")) {
            return GameBackupPreview(raw, solo.previewBackup(raw))
        }
        require(envelope.getString("format") == FORMAT && envelope.getInt("version") == 1)
        val payload = envelope.getString("payload")
        require(envelope.getString("sha256") == sha256(payload)) { "Backup checksum mismatch" }
        val data = JSONObject(payload)
        val profile = solo.previewBackup(data.getJSONObject("solo").toString())
        val font = data.getString("boardFont")
        require(BoardFontCollection.allowed(profile, font)) { "Unsupported board font" }
        require(data.has("books"))
        if (data.isNull("books")) return GameBackupPreview(raw, profile, boardFontId = font)
        val bookData = data.getJSONObject("books")
        val progress = Base64.getDecoder().decode(bookData.getString("progress"))
        val sessions = Base64.getDecoder().decode(bookData.getString("sessions"))
        val library = books()
        if (library == null && allowRestoreWithoutCatalog) {
            require(progress.size in 44..BookProgressCodec.MAX_BYTES)
            require(sessions.size in 44..BookPageSessionCodec.MAX_BYTES)
            // Record the confirmed portable scope so a future build with a catalog
            // can finish an interrupted restore without opening these QA pages.
            data.put("books", JSONObject.NULL)
            return GameBackupPreview(raw, profile, boardFontId = font,
                portableRestoreJournal = encodeEnvelope(data))
        }
        requireNotNull(library) { "These books are unavailable in this build" }
        val decodedProgress = BookProgressCodec.decode(library.catalog, progress)
        val decodedSessions = BookPageSessionCodec.decode(library.catalog, sessions)
        return GameBackupPreview(raw, profile, progress, sessions,
            decodedProgress.volumes.values.sumOf { it.completedPageIds.size },
            decodedSessions.sessions.size, font)
    }

    /** Called only by the player's confirmation button. Revalidate the raw file. */
    fun restoreBackup(raw: String): Boolean {
        if (needsRecovery) return false
        return try {
            val preview = previewBackup(raw)
            check(solo.readableForRestore) { "The unreadable device save is preserved" }
            if (preview.includesBooks) {
                val library = requireNotNull(books())
                check(!library.progress.blocked && !library.sessions.blocked) {
                    "Unreadable device book saves are preserved"
                }
            }
            check(writeJournal(preview.portableRestoreJournal ?: raw)) {
                "Could not start restore. Current progress is unchanged."
            }
            needsRecovery = true
            applyPending(preview)
        } catch (_: Exception) {
            message = if (needsRecovery) "Restore paused. The recovery file is kept. " +
                "Check device storage, then retry to finish saving all progress."
                else "Could not start restore. Your current device saves are preserved."
            false
        }
    }

    fun retryRestore(): Boolean {
        return try {
            val raw = BackupDocumentReader.readUtf8(journal.openRead())
            if (raw == COMMITTED) {
                needsRecovery = false
                runCatching { journal.delete() }
                true
            } else {
                needsRecovery = true
                applyPending(previewBackup(raw))
            }
        } catch (_: Exception) {
            needsRecovery = true
            message = "The restore could not finish. Its recovery file is preserved. " +
                "Check available device storage, then try again."
            false
        }
    }

    private fun applyPending(preview: GameBackupPreview): Boolean {
        val library = if (preview.includesBooks) requireNotNull(books()) else null
        val applied = solo.restoreBackup(preview.profile) &&
            (library == null || (library.progress.importManualBackup(
                requireNotNull(preview.bookProgress)) &&
                library.sessions.importManualBackup(requireNotNull(preview.bookSessions)))) &&
            (preview.boardFontId == null || solo.restoreBoardFontSelection(preview.boardFontId))
        if (!applied || !writeJournal(COMMITTED)) {
            message = "Restore paused before all progress could be saved. " +
                "Free device storage, then retry. Your backup is kept for recovery."
            return false
        }
        needsRecovery = false
        message = "Backup restored. Your saved progress is ready."
        // A durable COMMITTED marker makes cleanup optional for correctness.
        runCatching { journal.delete() }
        return true
    }

    private fun writeJournal(raw: String): Boolean {
        var stream: java.io.FileOutputStream? = null
        return try {
            stream = journal.startWrite()
            stream.write(raw.toByteArray(Charsets.UTF_8))
            journal.finishWrite(stream)
            stream = null
            true
        } catch (_: Exception) {
            stream?.let { runCatching { journal.failWrite(it) } }
            false
        }
    }

    private fun encodeEnvelope(data: JSONObject): String {
        val payload = data.toString()
        return JSONObject().put("format", FORMAT).put("version", 1)
            .put("payload", payload).put("sha256", sha256(payload)).toString()
    }

    companion object {
        private const val FORMAT = "gridmates-wordsearch-backup"
        internal const val COMMITTED = "GRIDMATES_RESTORE_COMMITTED_V1"
        internal fun sha256(text: String): String = MessageDigest.getInstance("SHA-256")
            .digest(text.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it.toInt() and 255) }
    }
}
