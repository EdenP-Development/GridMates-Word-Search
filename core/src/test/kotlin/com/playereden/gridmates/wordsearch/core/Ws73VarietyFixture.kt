package com.playereden.gridmates.wordsearch.core

import java.io.File
import java.util.Locale

/** Generates the six additive QA pages and measures their actual frozen answers. */
object Ws73VarietyFixture {
    private data class Chapter(
        val key: String,
        val theme: WordTheme,
        val size: PuzzleBoardSize,
        val challenge: PuzzleChallenge,
        val candidatesPerPage: Int,
        val seedBase: Long
    )

    private val chapters = listOf(
        Chapter("solar-system", WordTheme.SPACE, PuzzleBoardSize.BOOK_SHORT,
            PuzzleChallenge.RELAXED, 24, 736_000),
        Chapter("quiet-clearings", WordTheme.WOODLAND, PuzzleBoardSize.BOOK_BIG,
            PuzzleChallenge.EXPERT, 30, 737_000)
    )
    private const val poolSha = "7e3074574455215c3f9a7bc4701e410c61b3a53b25080e7c7985e2fe3ea42626"

    @JvmStatic
    fun main(args: Array<String>) {
        val oldAssets = File(args[0])
        val assets = File(args[1]).apply { mkdirs() }
        val raw = requireNotNull(javaClass.getResource("/ws73-variety/page-pools.tsv")).readText()
        check(ReviewedWordPackCodec.sha256(raw) == poolSha)
        val lines = raw.trimEnd().lines()
        check(lines.first() == "chapter\tpage\ttitle\tword_id\tanswer\tdisplay\tsource")
        val rows = lines.drop(1).map { it.split('\t') }
        check(rows.size == chapters.sumOf { 3 * it.candidatesPerPage } && rows.all { it.size == 7 })

        val index = mutableListOf("snapshot\tboard_sha256")
        val metrics = mutableListOf(
            "chapter\told_slots\told_distinct\told_adjacent_repeats\tnew_candidates" +
                "\tnew_slots\tnew_distinct\tnew_adjacent_repeats\tboundary_repeats"
        )
        val answers = mutableListOf("chapter\tset\tpage\tseed\tanswers")
        for (chapter in chapters) {
            val chapterRows = rows.filter { it[0] == chapter.key }
            check(chapterRows.map { it[4] }.distinct().size == 3 * chapter.candidatesPerPage)
            val old = (1..3).map { position ->
                FrozenWordSearchCodec.decode(File(oldAssets, "${chapter.key}-$position.bin").readBytes()).puzzle
            }
            val fresh = (1..3).map { position ->
                val pageRows = chapterRows.filter { it[1] == position.toString() }
                check(pageRows.size == chapter.candidatesPerPage)
                val frozen = generate(chapter, position, pageRows)
                val puzzle = frozen.puzzle
                val bytes = FrozenWordSearchCodec.encode(frozen)
                check(FrozenWordSearchCodec.decode(bytes) == frozen)
                checkPlayable(chapter, puzzle)
                val filename = "${chapter.key}-$position.bin"
                File(assets, filename).writeBytes(bytes)
                index += "$filename\t${BookBoardDigest.sha256(puzzle)}"
                puzzle
            }
            val oldAnswers = old.map(::answerSet)
            val newAnswers = fresh.map(::answerSet)
            val boundary = oldAnswers.last().intersect(newAnswers.first()).size
            val newDistinct = newAnswers.flatten().toSet().size
            check(boundary == 0 && newDistinct == 3 * chapter.size.targets)
            metrics += listOf(
                chapter.key, oldAnswers.sumOf { it.size }, oldAnswers.flatten().toSet().size,
                adjacentRepeats(oldAnswers), chapterRows.size, newAnswers.sumOf { it.size },
                newDistinct, adjacentRepeats(newAnswers), boundary
            ).joinToString("\t")
            for ((set, boards) in listOf("v115" to old, "v116" to fresh)) {
                boards.forEachIndexed { indexInSet, puzzle ->
                    answers += "${chapter.key}\t$set\t${indexInSet + 1}\t${puzzle.seed}\t" +
                        puzzle.targets.joinToString(",") { it.word }
                }
            }
        }

        val manifest = index.joinToString("\n", postfix = "\n")
        val expected = requireNotNull(javaClass.getResource("/ws73-variety/expected-boards.tsv")).readText()
        check(manifest == expected) { "WS73 pinned page digest changed" }
        File(assets, "boards.tsv").writeText(manifest)
        val evidence = assets.parentFile.parentFile
        File(evidence, "answers.tsv").writeText(answers.joinToString("\n", postfix = "\n"))
        File(evidence, "variety.tsv").writeText(metrics.joinToString("\n", postfix = "\n"))
        println(metrics.joinToString("\n"))
        println(manifest)
    }

    private fun generate(chapter: Chapter, position: Int, rows: List<List<String>>): FrozenWordSearch {
        val theme = chapter.theme.name.lowercase(Locale.ROOT)
        val title = rows.map { it[2] }.distinct().single()
        val tier = if (chapter.challenge == PuzzleChallenge.RELAXED) "familiar" else "intermediate"
        val body = (listOf(
            "GRIDMATES_WORD_PACK_V1", "en-ws73-${chapter.key}-p$position-v1", poolSha, title, theme
        ) + rows.map { row ->
            check(row[4].length < chapter.size.cells && row[4] != row[4].reversed())
            "${row[3]}\t${row[4]}\t${row[5]}\t$theme:${chapter.key}:$tier"
        }).joinToString("\n")
        val pack = ReviewedWordPackCodec.decode("$body\nSHA256\t${ReviewedWordPackCodec.sha256(body)}")
        return WordSearchGeneratorV5.generate(
            pack, chapter.size, chapter.challenge, chapter.seedBase + position, chapter.theme
        )
    }

    private fun checkPlayable(chapter: Chapter, puzzle: WordSearchPuzzle) {
        check(puzzle.size == chapter.size.cells && puzzle.targets.size == chapter.size.targets)
        val found = mutableSetOf<String>()
        for (target in puzzle.targets) {
            check(WordSearchGeneratorV5.occurrences(puzzle, target.word) == 1)
            val claim = WordSearchRules.claim(puzzle, found, target.positions.first(), target.positions.last())
            check(claim.status == ClaimStatus.FOUND && claim.target == target)
            found += target.id
        }
        check(WordSearchRules.complete(puzzle, found))
    }

    private fun answerSet(puzzle: WordSearchPuzzle) = puzzle.targets.map { it.word }.toSet()

    private fun adjacentRepeats(answers: List<Set<String>>) =
        answers.zipWithNext().joinToString(",") { (first, next) -> first.intersect(next).size.toString() }
}
