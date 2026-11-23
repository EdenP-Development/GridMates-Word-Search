package com.playereden.gridmates.wordsearch.core

import java.io.File

/** Build-time selection. The Android catalog opens the encoded boards directly. */
object Ws66PreviewFixture {
    private data class Chapter(
        val key: String, val title: String, val theme: WordTheme,
        val size: PuzzleBoardSize, val difficulty: PuzzleChallenge,
        val words: Int, val firstSeed: Long
    )

    private val chapters = listOf(
        Chapter("solar-system", "Our Solar System", WordTheme.SPACE,
            PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, 47, 668_000),
        Chapter("quiet-clearings", "Quiet Clearings", WordTheme.WOODLAND,
            PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.EXPERT, 65, 669_000)
    )
    private const val poolSha = "314b07e3e4a36f81ec3bf27f013d3b01d995ba94b7da47efea6613691b8b3c11"

    private data class Trial(val frozen: FrozenWordSearch) {
        val answers = frozen.puzzle.targets.map { it.word }.toSet()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val assets = File(args.single()).apply { mkdirs() }
        val raw = requireNotNull(javaClass.getResource("/ws66-preview/chapter-pools.tsv")).readText()
        check(ReviewedWordPackCodec.sha256(raw) == poolSha) {
            "Changing the preview vocabulary requires new page IDs and fixture pins"
        }
        val rows = raw.trimEnd().lines().drop(1).map { it.split('\t') }
        check(rows.size == chapters.sumOf { it.words } && rows.all { it.size == 7 })
        val index = mutableListOf("snapshot\tboard_sha256")
        val trials = mutableListOf("chapter\tseed\tstatus\tanswers")
        val metrics = mutableListOf("chapter\tpool\tpassed\tattempted\tfirst_three_repeats\tselected_repeats\tdistinct_answers\tselected_seeds")

        for (chapter in chapters) {
            val packRows = rows.filter { it[0] == chapter.title }
            check(packRows.size == chapter.words)
            val topic = chapter.theme.name.lowercase(java.util.Locale.ROOT)
            val body = (listOf(
                "GRIDMATES_WORD_PACK_V1", "en-ws66-v110-${chapter.key}-v1", poolSha,
                chapter.title, topic
            ) + packRows.map { row ->
                "${row[2]}\t${row[3]}\t${row[4]}\t$topic:${row[1]}:${row[5]}"
            }).joinToString("\n")
            val pack = ReviewedWordPackCodec.decode(
                "$body\nSHA256\t${ReviewedWordPackCodec.sha256(body)}"
            )
            val generated = (chapter.firstSeed until chapter.firstSeed + 32).mapNotNull { seed ->
                try {
                    val trial = Trial(WordSearchGeneratorV5.generate(
                        pack, chapter.size, chapter.difficulty, seed, chapter.theme
                    ))
                    trials += "${chapter.key}\t$seed\tPASS\t${trial.answers.joinToString(",")}"
                    trial
                } catch (e: IllegalStateException) {
                    trials += "${chapter.key}\t$seed\t${e.javaClass.simpleName}\t"
                    null
                }
            }
            check(generated.size >= 3) { "${chapter.title} needs three playable pages" }
            var selected = generated.take(3)
            for (first in 0 until generated.size - 2) {
                for (second in first + 1 until generated.size - 1) {
                    for (third in second + 1 until generated.size) {
                        val batch = listOf(generated[first], generated[second], generated[third])
                        if (repeats(batch) < repeats(selected) ||
                            repeats(batch) == repeats(selected) && distinct(batch) > distinct(selected)) {
                            selected = batch
                        }
                    }
                }
            }
            val seeds = selected.joinToString(",") { it.frozen.puzzle.seed.toString() }
            metrics += listOf(chapter.key, chapter.words, generated.size, 32,
                repeats(generated.take(3)), repeats(selected), distinct(selected), seeds).joinToString("\t")
            selected.forEachIndexed { position, trial ->
                val grid = trial.frozen
                val bytes = FrozenWordSearchCodec.encode(grid)
                check(FrozenWordSearchCodec.decode(bytes) == grid)
                val name = "${chapter.key}-${position + 1}.bin"
                File(assets, name).writeBytes(bytes)
                index += "$name\t${BookBoardDigest.sha256(grid.puzzle)}"
            }
        }
        val manifest = index.joinToString("\n", postfix = "\n")
        val expected = requireNotNull(javaClass.getResource("/ws66-preview/expected-boards.tsv")).readText()
        check(manifest == expected) { "WS66 pinned page digest changed" }
        File(assets, "boards.tsv").writeText(manifest)
        val evidence = assets.parentFile.parentFile
        File(evidence, "trial-boards.tsv").writeText(trials.joinToString("\n", postfix = "\n"))
        File(evidence, "variety.tsv").writeText(metrics.joinToString("\n", postfix = "\n"))
        println(metrics.joinToString("\n"))
    }

    private fun repeats(pages: List<Trial>) =
        pages.zipWithNext().sumOf { (a, b) -> a.answers.intersect(b.answers).size }

    private fun distinct(pages: List<Trial>) = pages.flatMap { it.answers }.toSet().size
}
