package com.team1.trivia

import android.app.Activity

open class TriviaActivity : Activity() {
    companion object {
        val GAME_PREFERENCES: String? = "GamePrefs"
        val GAME_PREFERENCES_PLAYER: String? = "Player1"
        val GAME_PREFERENCES_SCORE: String? = "Score"
        val GAME_PREFERENCES_CATEGORIES: String? = "Categories"
        val GAME_PREFERENCES_MODE: String? = "Mode"
        val GAME_PREFERENCES_DIFFICULTY: String? = "Difficulty"
        val GAME_PREFERENCES_DATABASE_COUNT: String? = "dbCount"
        const val GAME_PREFERENCES_CURRENT_QUESTION = 0
        const val QUESTION_BATCH_SIZE = 15
        val DEBUG_TAG: String? = "Movie Time Activity Log"
    }
}