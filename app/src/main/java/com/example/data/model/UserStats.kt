package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey
    val id: Int = 1,
    val totalXp: Int = 0,
    val currentStreakDays: Int = 0,
    val lastActiveDateString: String = "",
    val totalWordsMastered: Int = 0,
    val totalQuizzesTaken: Int = 0,
    val totalPerfectQuizzes: Int = 0,
    val currentAvatar: String = "owl",
    val studentName: String = "Super Speller",
    val spellerSuperpower: String = "Sound Detective 🔍",
    // Twofold Onboarding & Parental Controls
    val onboardingCompleted: Boolean = false,
    val parentPin: String = "",
    val dailyTimeLimitMinutes: Int = 20,
    val dailyWordGoal: Int = 10,
    val gradeLevel: String = "Grade 2"
) {
    val level: Int
        get() = (totalXp / 100) + 1

    val levelProgress: Float
        get() = ((totalXp % 100) / 100f).coerceIn(0f, 1f)

    val currentLevelTitle: String
        get() = when (level) {
            1 -> "Spelling Sprout 🌱"
            2 -> "Letter Scout 🔍"
            3 -> "Word Explorer 🧭"
            4 -> "Phonics Knight 🛡️"
            5 -> "Grammar Guardian 🏰"
            6 -> "Spelling Wizard 🧙‍♂️"
            else -> "Lexicon Legend 👑"
        }
}
