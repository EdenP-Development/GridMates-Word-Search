package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ReviewedWordPackV5Test {
    private fun text(id: String): String = requireNotNull(javaClass.classLoader
        .getResource("reviewed-packs/$id.tsv")).readText()
    private fun pack(id: String = "en-cozy-mixed-v1") = ReviewedWordPackCodec.decode(text(id))

    @Test fun exactReviewedInventoryAndTamperedContentAreDistinguished() {
        val p = pack()
        assertEquals(670, p.words.size)
        assertEquals(setOf("woodland", "space", "garden", "everyday"), p.topicIds.toSet())
        assertEquals("81d7221e28283d1295708fd601f138798133435db2e5dfc02973d814a53ad51e", p.sourceSha256)
        assertTrue(runCatching { ReviewedWordPackCodec.decode(text(p.id).replace("Forest", "Forezt")) }.isFailure)
        assertTrue(runCatching { ReviewedWordPackCodec.decode("x".repeat(512_001)) }.isFailure)
        assertEquals(listOf(159,169,174,200), listOf("woodland","space","garden","everyday")
            .map { pack("en-$it-v1").words.size })
    }

    @Test fun everySizeChallengeAndMixedTopicIsPlayableAndRepeatable() {
        val p = pack()
        for (board in PuzzleBoardSize.bookPresets) for (challenge in PuzzleChallenge.entries)
            for (seed in listOf(0L,1L,42L,20261117L,Long.MAX_VALUE - 1)) {
                val exact = WordSearchGeneratorV5.generate(p, board, challenge, seed)
                assertEquals(exact, WordSearchGeneratorV5.generate(p,board,challenge,seed))
                val found = mutableSetOf<String>()
                val topics = exact.puzzle.targets.groupingBy { it.topicId }.eachCount()
                assertEquals(p.topicIds.toSet(), topics.keys)
                assertTrue(topics.values.max() - topics.values.min() <= 1)
                exact.puzzle.targets.forEach { target ->
                    assertEquals(target.word, target.positions.mapNotNull(exact.puzzle::letter).joinToString(""))
                    assertEquals(ClaimStatus.FOUND, WordSearchRules.claim(exact.puzzle,found,
                        target.positions.last(),target.positions.first()).status)
                    found += target.id
                }
                assertTrue(WordSearchRules.complete(exact.puzzle,found))
                assertEquals(exact, FrozenWordSearchCodec.decode(FrozenWordSearchCodec.encode(exact)))
            }
    }

    @Test fun savedSnapshotRetainsExactBoardAndRejectsCorruption() {
        val exact = WordSearchGeneratorV5.generate(pack(), PuzzleBoardSize.BOOK_CLASSIC,
            PuzzleChallenge.CLASSIC, 103L)
        val first = exact.puzzle.targets.first()
        val run = SoloRun(exact.puzzle.theme, exact.puzzle.difficulty, exact.puzzle.seed,
            foundIds=setOf(first.id), hints=mapOf(exact.puzzle.targets[1].id to 1),
            elapsedSeconds=47, boardSize=exact.board, challenge=exact.challenge, frozen=exact)
        assertEquals(exact.puzzle,SoloProgressRules.puzzle(run))
        assertEquals(run,SoloProgressRules.validate(run))
        val bytes=FrozenWordSearchCodec.encode(exact)
        bytes[bytes.size/2]=(bytes[bytes.size/2].toInt() xor 1).toByte()
        assertTrue(runCatching { FrozenWordSearchCodec.decode(bytes) }.isFailure)
        assertTrue(runCatching { SoloProgressRules.puzzle(run.copy(seed=104)) }.isFailure)
        assertTrue(runCatching { FrozenWordSearchCodec.decode(ByteArray(64_001)) }.isFailure)
    }

    @Test fun hundredConsecutiveBoardsHaveVocabularyAndSpatialVariety() {
        val p=pack()
        val sets=mutableListOf<Set<String>>()
        val layouts=mutableSetOf<List<String>>()
        var balanced=0
        val directions=mutableMapOf<Pair<Int,Int>,Int>()
        repeat(100) { seed ->
            val board=WordSearchGeneratorV5.generate(p,PuzzleBoardSize.BOOK_CLASSIC,
                PuzzleChallenge.CLASSIC,seed.toLong()).puzzle
            sets+=board.targets.map { it.id }.toSet()
            layouts+=board.rows
            val quadrants=IntArray(4)
            board.targets.forEach { t ->
                val a=t.positions[0]; val b=t.positions[1]
                val direction=(b.row-a.row) to (b.col-a.col)
                directions[direction]=(directions[direction]?:0)+1
                val center=t.positions[t.positions.size/2]
                quadrants[(if(center.row>=board.size/2)2 else 0)+(if(center.col>=board.size/2)1 else 0)]++
            }
            if(quadrants.all { it>0 })balanced++
        }
        val unique=sets.flatten().toSet().size
        val overlap=sets.zipWithNext().map { (a,b)->(a intersect b).size }.average()
        assertEquals(100,layouts.size)
        assertTrue("Only $unique words appeared",unique>=400)
        assertTrue("Too much adjacent repetition: $overlap",overlap<=4.0)
        assertTrue("Missing quadrant work: $balanced/100",balanced>=95)
        assertEquals(6,directions.size)
        println("WS41 VARIETY unique=$unique adjacentMean=$overlap quadrants=$balanced/100 directions=$directions")
    }

    @Test fun captureLiteralNewVersionFixturesForPublishing() {
        val p=pack()
        val report=PuzzleBoardSize.bookPresets.map { size ->
            val b=WordSearchGeneratorV5.generate(p,size,PuzzleChallenge.CLASSIC,42L)
            "${size.name}|${BookBoardDigest.sha256(b.puzzle)}|${b.puzzle.targets.first().id}"
        }.joinToString("\n")
        File("build/ws41-v5-goldens.txt").apply { parentFile.mkdirs(); writeText(report+"\n") }
        println("WS41 NEW GOLDENS\n$report")
        // Existing V1/V2/V3 literal golden tests continue to run independently.
    }

    @Test fun bookAwardsAreIdempotentAndDoNotGrantSoloOnlyAchievements() {
        val board=WordSearchGeneratorV5.generate(pack("en-woodland-v1"),
            PuzzleBoardSize.BOOK_SHORT,PuzzleChallenge.RELAXED,200L,WordTheme.WOODLAND)
        val page=BookPage("woodland-new-page",board.puzzle.id,5,board.packId,board.board,
            board.challenge,board.puzzle.seed,BookBoardDigest.sha256(board.puzzle),board)
        val book=PuzzleBook("woodland-new-book","Woodland",1,WordTheme.WOODLAND,
            listOf(BookChapter("woodland-new-chapter","Along the Trail",listOf(page))))
        val catalog=BookCatalog(listOf(book))
        val completed=BookProgressRules.recordVerifiedCompletion(catalog,BookLibraryProgress(),
            book.id,page.id,board.puzzle,board.puzzle.targets.map { it.id }.toSet())
        val profile=SoloProfile(SoloProgressRules.fresh(seed=3))
        val awarded=BookRewardRules.award(profile,catalog,completed,book.id,page.id)
        assertEquals(1,awarded.bookCompletions)
        assertEquals(12,awarded.bookWords)
        assertEquals(awarded,BookRewardRules.award(awarded,catalog,completed,book.id,page.id))
        assertTrue("woodland_rabbit" in WoodlandCollection.unlocked(awarded))
        assertEquals(1,WordSearchAchievements.progress(awarded).first { it.id=="first_puzzle" }.current)
        assertEquals(0,WordSearchAchievements.progress(awarded).first { it.id=="no_hints" }.current)
        assertTrue(runCatching { BookRewardRules.award(profile,catalog,BookLibraryProgress(),book.id,page.id) }.isFailure)
        assertTrue(runCatching { BookRewardRules.validate(awarded.bookAwards+awarded.bookAwards) }.isFailure)
    }
}
