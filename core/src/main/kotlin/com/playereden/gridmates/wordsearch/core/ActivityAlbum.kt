package com.playereden.gridmates.wordsearch.core

data class ActivityAlbumProgress(
    val stamps: List<AchievementProgress>,
    val solvedQuickPlaySizes: Set<PuzzleBoardSize>
) {
    val earnedCount: Int get() = stamps.count { it.unlocked }
    val next: AchievementProgress? get() = stamps.firstOrNull { !it.unlocked }
}

object ActivityAlbumRules {
    const val ALL_SIZES = "quick_all_sizes"

    fun progress(profile: SoloProfile): ActivityAlbumProgress {
        val achievements = WordSearchAchievements.progress(profile).associateBy { it.id }
        // The Solo journal already distinguishes mixed Quick Play from themed Solo.
        // Book pages and Daily have separate ledgers.
        val quickSizes = profile.results.asSequence()
            .filter { it.quickPackId != null }
            .mapNotNull { it.boardSize }
            .filter { it in PuzzleBoardSize.bookPresets }
            .toSet()
        return ActivityAlbumProgress(
            stamps = listOf(
                achievements.getValue("one_daily"),
                AchievementProgress("quick_start", "Quick Start",
                    "Finish a Quick Play puzzle.", if (quickSizes.isEmpty()) 0 else 1, 1),
                AchievementProgress(ALL_SIZES, "All Sizes",
                    "Finish a Small, Medium and Large Quick Play puzzle.",
                    quickSizes.size, PuzzleBoardSize.bookPresets.size),
                achievements.getValue("seven_dailies")
            ),
            solvedQuickPlaySizes = quickSizes
        )
    }
}
