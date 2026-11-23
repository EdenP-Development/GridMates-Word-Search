package com.playereden.gridmates.wordsearch.core

import java.security.MessageDigest

data class WordTopic(val topicId: String, val subtopic: String, val tier: String)
data class ReviewedWord(val id: String, val answer: String, val display: String,
                        val topics: List<WordTopic>)
data class ReviewedWordPack(val id: String, val sourceSha256: String,
                            val sha256: String, val title: String,
                            val topicIds: List<String>, val words: List<ReviewedWord>)

/** Bounded deterministic runtime projection of the separately reviewed JSON. */
object ReviewedWordPackCodec {
    private val sha = Regex("[0-9a-f]{64}")
    private val id = Regex("[a-z][a-z0-9_-]{4,39}")
    private val topic = Regex("[a-z][a-z0-9-]{2,40}")
    fun sha256(value: String): String = MessageDigest.getInstance("SHA-256")
        .digest(value.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 255) }

    fun decode(raw: String): ReviewedWordPack {
        require(raw.length <= 512_000) { "Word pack is too large" }
        val lines = raw.trim('\n', '\r').split('\n')
        require(lines.size in 7..10_010 && lines[0] == "GRIDMATES_WORD_PACK_V1")
        require(Regex("en-[a-z][a-z0-9-]{2,38}-v[1-9][0-9]*").matches(lines[1]))
        require(sha.matches(lines[2]) && lines[3].length in 1..60)
        val topics = lines[4].split(',')
        require(topics.isNotEmpty() && topics.distinct() == topics && topics.all(topic::matches))
        val expected = lines.last().removePrefix("SHA256\t")
        require(sha.matches(expected) && expected == sha256(lines.dropLast(1).joinToString("\n")))
        val words = lines.subList(5, lines.lastIndex).map { row ->
            val fields = row.split('\t')
            require(fields.size == 4 && id.matches(fields[0]))
            val answer = fields[1]
            require(answer.length in 3..20 && answer.all { it in 'A'..'Z' })
            val display = fields[2]
            require(display.length in 1..60 && display.none(Char::isISOControl))
            require(display.uppercase(java.util.Locale.ROOT).filter { it in 'A'..'Z' } == answer)
            val assignments = fields[3].split('|').map { s ->
                val pieces = s.split(':')
                require(pieces.size == 3 && pieces[0] in topics && topic.matches(pieces[1]))
                require(pieces[2] in setOf("familiar", "intermediate", "advanced"))
                WordTopic(pieces[0], pieces[1], pieces[2])
            }
            require(assignments.isNotEmpty() && assignments.distinct() == assignments)
            ReviewedWord(fields[0], answer, display, assignments)
        }
        require(words.size >= 24 && words.map { it.id }.distinct().size == words.size)
        require(words.map { it.answer }.distinct().size == words.size)
        return ReviewedWordPack(lines[1], lines[2], expected, lines[3], topics, words)
    }
}
