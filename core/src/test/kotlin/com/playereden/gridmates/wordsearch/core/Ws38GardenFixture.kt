package com.playereden.gridmates.wordsearch.core

import org.junit.Test
import java.io.File

/** Deterministic generated content for the handset screenshot regression. */
class Ws38GardenFixture {
    @Test fun pickLongGardenGame() {
        val seed=(0L..99L).first { candidate ->
            WordSearchGeneratorV3.generate(WordTheme.GARDEN,
                PuzzleBoardSize.BOOK_GIANT, PuzzleChallenge.CLASSIC, candidate)
                .targets.any { it.word == "PHOTOSYNTHESIS" }
        }
        val output=File("build/ws38-garden-seed.txt")
        output.parentFile.mkdirs()
        output.writeText(seed.toString())
    }
}
