package com.playereden.gridmates.wordsearch.core

import org.junit.Test
import java.io.File
import java.time.LocalDate

/** Provide genuine Daily solution coordinates for the emulator's touch inputs. */
class Ws37DailyTraceFixture {
    @Test fun writePuzzleTraces() {
        val today = LocalDate.parse(DailyChallenge.today())
        val output = File("build/ws37-daily-traces.txt")
        output.parentFile.mkdirs()
        output.printWriter().use { printer ->
            for (date in listOf(today.minusDays(1), today, today.plusDays(1))) {
                val puzzle = SoloProgressRules.puzzle(DailyChallenge.dailyRun(date.toString()))
                for (target in puzzle.targets) {
                    val first = target.positions.first()
                    val last = target.positions.last()
                    printer.println("$date ${puzzle.size} ${first.row} ${first.col} ${last.row} ${last.col}")
                }
            }
        }
    }
}
