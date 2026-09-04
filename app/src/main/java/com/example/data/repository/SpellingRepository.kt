package com.example.data.repository

import com.example.data.local.SpellingDao
import com.example.data.model.Badge
import com.example.data.model.PredefinedGradePack
import com.example.data.model.SpellingWord
import com.example.data.model.UserStats
import com.example.data.model.WeeklyList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SpellingRepository(
    private val dao: SpellingDao
) {
    val allWeeklyLists: Flow<List<WeeklyList>> = dao.getAllWeeklyLists()
    val activeWeeklyList: Flow<WeeklyList?> = dao.getActiveWeeklyList()
    val activeListWords: Flow<List<SpellingWord>> = dao.getWordsForActiveList()
    val trickyWords: Flow<List<SpellingWord>> = dao.getTrickyWords()
    val userStats: Flow<UserStats?> = dao.getUserStats()
    val allBadges: Flow<List<Badge>> = dao.getAllBadges()
    val totalMasteredCount: Flow<Int> = dao.getTotalMasteredCount()

    fun getWordsForList(listId: Long): Flow<List<SpellingWord>> = dao.getWordsForList(listId)

    suspend fun ensureInitialSeedData() {
        try {
            // 1. Ensure UserStats
            val stats = dao.getUserStats().firstOrNull()
            if (stats == null) {
                dao.insertOrUpdateUserStats(
                    UserStats(
                        id = 1,
                        totalXp = 0,
                        currentStreakDays = 0,
                        lastActiveDateString = "",
                        totalWordsMastered = 0,
                        totalQuizzesTaken = 0,
                        totalPerfectQuizzes = 0,
                        currentAvatar = "owl",
                        studentName = "Super Speller"
                    )
                )
            }

            // 2. Ensure Badges
            val badges = dao.getAllBadges().firstOrNull() ?: emptyList()
            if (badges.isEmpty()) {
                val initialBadges = listOf(
                    Badge("first_word", "First Word Spark", "Master your very first spelling word!", "mastery", "🌟", false, 0L, 30),
                    Badge("streak_3", "3-Day Fire", "Practice 3 days in a row!", "streak", "🔥", false, 0L, 50),
                    Badge("streak_7", "Weekly Legend", "Keep your learning streak alive for 7 days!", "streak", "⚡", false, 0L, 100),
                    Badge("listen_master", "Super Listener", "Spell 5 words correctly in Listen & Spell mode!", "modes", "🎧", false, 0L, 40),
                    Badge("scramble_champ", "Tile Untangler", "Solve 5 Word Scrambles in a row!", "modes", "🔤", false, 0L, 40),
                    Badge("lscwc_pro", "Memory Master", "Complete Look, Say, Cover, Write, Check for a whole list!", "pedagogy", "🧠", false, 0L, 60),
                    Badge("vowel_hunter", "Vowel Buster", "Find all missing vowels without making a mistake!", "modes", "🧩", false, 0L, 40),
                    Badge("spelling_bee_champ", "Spelling Bee Hero", "Score 100% on the Weekly Boss Spelling Bee!", "quiz", "🏆", false, 0L, 150),
                    Badge("xp_100", "Century Club", "Earn your first 100 Star XP!", "xp", "💯", false, 0L, 50),
                    Badge("xp_500", "XP Grandmaster", "Reach an astonishing 500 Star XP!", "xp", "👑", false, 0L, 150),
                    Badge("list_master_1", "Week 1 Master", "Master all words in your active weekly list!", "mastery", "🎖️", false, 0L, 100),
                    Badge("tricky_conqueror", "Mistake Buster", "Turn 3 tricky words into Mastered words!", "mastery", "🛡️", false, 0L, 75)
                )
                dao.insertInitialBadges(initialBadges)
            }

            // 3. Ensure a Weekly List exists and is active (ready for parent to input required words)
            val lists = dao.getAllWeeklyLists().firstOrNull() ?: emptyList()
            if (lists.isEmpty()) {
                dao.insertWeeklyList(
                    WeeklyList(
                        title = "This Week's Spelling List",
                        description = "Required spelling words entered by parent",
                        weekNumber = 1,
                        isActive = true,
                        gradeLevel = "Custom"
                    )
                )
            } else {
                val active = dao.getActiveWeeklyList().firstOrNull()
                if (active == null) {
                    dao.setActiveList(lists.first().id)
                }
            }
        } catch (e: Throwable) {
            android.util.Log.e("SpellingRepository", "Failed to ensureInitialSeedData", e)
        }
    }

    suspend fun createWeeklyList(
        title: String,
        description: String,
        weekNumber: Int,
        gradeLevel: String = "Elementary",
        makeActive: Boolean = true
    ): Long {
        if (makeActive) {
            dao.deactivateAllLists()
        }
        val list = WeeklyList(
            title = title,
            description = description,
            weekNumber = weekNumber,
            isActive = makeActive,
            gradeLevel = gradeLevel
        )
        return dao.insertWeeklyList(list)
    }

    suspend fun setActiveWeeklyList(listId: Long) {
        dao.deactivateAllLists()
        dao.setActiveList(listId)
    }

    suspend fun deleteWeeklyList(listId: Long) {
        dao.deleteWeeklyList(listId)
        // If no active list remains, activate the first available
        val lists = dao.getAllWeeklyLists().firstOrNull() ?: emptyList()
        val hasActive = lists.any { it.isActive }
        if (!hasActive && lists.isNotEmpty()) {
            dao.setActiveList(lists.first().id)
        }
    }

    suspend fun addWord(
        listId: Long,
        word: String,
        phonics: String = "",
        hint: String = "",
        sentence: String = "",
        definition: String = ""
    ): Long {
        val calculatedPhonics = if (phonics.isNotBlank()) phonics else SpellingWord.breakdownPhonics(word)
        val spellingWord = SpellingWord(
            listId = listId,
            word = word.trim(),
            phonics = calculatedPhonics,
            hint = hint.trim(),
            sentence = sentence.trim(),
            definition = definition.trim(),
            boxLevel = 0
        )
        return dao.insertWord(spellingWord)
    }

    suspend fun updateWord(word: SpellingWord) {
        dao.updateWord(word)
    }

    suspend fun deleteWord(wordId: Long) {
        dao.deleteWord(wordId)
    }

    suspend fun importGradePack(pack: PredefinedGradePack, makeActive: Boolean = true): Long {
        val lists = dao.getAllWeeklyLists().firstOrNull() ?: emptyList()
        val nextWeek = (lists.maxOfOrNull { it.weekNumber } ?: 0) + 1

        val listId = createWeeklyList(
            title = "Week $nextWeek: ${pack.title.substringAfter(":")}",
            description = pack.description,
            weekNumber = nextWeek,
            gradeLevel = pack.gradeLevel,
            makeActive = makeActive
        )

        val words = pack.words.map { packWord ->
            SpellingWord(
                listId = listId,
                word = packWord.word,
                phonics = packWord.phonics,
                definition = packWord.definition,
                sentence = packWord.sentence,
                hint = packWord.hint,
                boxLevel = 0
            )
        }
        dao.insertWords(words)
        return listId
    }

    suspend fun bulkImportWords(listId: Long, rawText: String): Int {
        // Robust split by newlines, commas, semicolons, tabs
        val linesOrTokens = rawText.split(Regex("[\n\r,;]+"))
        val wordsToAdd = mutableListOf<String>()

        for (rawToken in linesOrTokens) {
            // Strip leading numbering e.g. "1. elephant", "2) cat", "- dog", "* sun"
            val cleanToken = rawToken
                .replace(Regex("^[0-9]+[.)\\s-]+\\s*"), "")
                .replace(Regex("^[-*•>~]+\\s*"), "")
                .trim()
            if (cleanToken.isNotBlank()) {
                val subTokens = cleanToken.split(Regex("\\s+")).filter { it.isNotBlank() }
                for (sub in subTokens) {
                    val finalWord = sub.filter { it.isLetter() || it == '-' || it == '\'' }.trim()
                    if (finalWord.isNotBlank() && finalWord.length > 1) {
                        wordsToAdd.add(finalWord.lowercase())
                    }
                }
            }
        }

        val newWords = wordsToAdd.distinct().map { token ->
            SpellingWord(
                listId = listId,
                word = token,
                phonics = SpellingWord.breakdownPhonics(token),
                definition = "Required spelling word",
                sentence = "Practice spelling the word: _____.",
                hint = "Listen carefully to the sound",
                boxLevel = 0
            )
        }
        if (newWords.isNotEmpty()) {
            dao.insertWords(newWords)
        }
        return newWords.size
    }

    suspend fun clearWordsInList(listId: Long) {
        dao.clearWordsInList(listId)
    }

    suspend fun clearAllWords() {
        dao.clearAllWords()
    }

    // --- Pedagogical Mastery & Retention Engine ---
    suspend fun recordAttempt(
        word: SpellingWord,
        isCorrect: Boolean,
        mode: String,
        usedHint: Boolean = false
    ): AttemptOutcome {
        val currentTime = System.currentTimeMillis()
        val currentStats = dao.getUserStats().firstOrNull() ?: UserStats(id = 1)
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // 1. Calculate Leitner Box Promotion / Demotion (Spaced Repetition)
        val newCorrect = if (isCorrect) word.correctAttempts + 1 else word.correctAttempts
        val newIncorrect = if (!isCorrect) word.incorrectAttempts + 1 else word.incorrectAttempts
        val newStreak = if (isCorrect) word.streak + 1 else 0

        // Leitner spacing intervals in ms
        val intervals = listOf(
            0L,
            1000L * 60 * 10,        // Box 1: 10 mins
            1000L * 60 * 60 * 24,    // Box 2: 1 day
            1000L * 60 * 60 * 24 * 3, // Box 3: 3 days (Mastered)
            1000L * 60 * 60 * 24 * 7  // Box 4: 7 days (Champion)
        )

        var newBoxLevel = word.boxLevel
        var wasJustMastered = false

        if (isCorrect) {
            if (newStreak >= 4) {
                newBoxLevel = 4
            } else if (newStreak >= 3) {
                newBoxLevel = 3
            } else if (newStreak >= 1) {
                newBoxLevel = (word.boxLevel + 1).coerceAtMost(3)
            }
            if (newBoxLevel >= 3 && !word.isMastered) {
                wasJustMastered = true
            }
        } else {
            // Immediate spaced demotion for active recall review
            newBoxLevel = (word.boxLevel - 1).coerceAtLeast(0)
        }

        val nextReview = currentTime + intervals.getOrElse(newBoxLevel) { 1000L * 60 * 60 * 24 }

        val updatedWord = word.copy(
            correctAttempts = newCorrect,
            incorrectAttempts = newIncorrect,
            streak = newStreak,
            boxLevel = newBoxLevel,
            isMastered = newBoxLevel >= 3 || word.isMastered,
            lastPracticedTimestamp = currentTime,
            nextReviewTimestamp = nextReview
        )
        dao.updateWord(updatedWord)

        // 2. Strict Daily Streak Rule:
        // EVERY word in the active week's list must be practiced today to earn/advance a streak.
        val activeWords = dao.getWordsForActiveListDirect()
        val totalActiveWords = activeWords.size
        val wordsPracticedTodayCount = if (totalActiveWords > 0) {
            activeWords.count { w ->
                if (w.id == updatedWord.id) true
                else w.isPracticedToday(currentTime)
            }
        } else {
            0
        }
        val allActiveWordsPracticedToday = totalActiveWords > 0 && wordsPracticedTodayCount >= totalActiveWords

        var newStreakDays = currentStats.currentStreakDays
        var newLastActiveDate = currentStats.lastActiveDateString
        var justEarnedDailyStreak = false

        if (allActiveWordsPracticedToday) {
            if (currentStats.lastActiveDateString != todayStr) {
                // First time today that every single word in this week's list has been practiced!
                justEarnedDailyStreak = true
                newLastActiveDate = todayStr
                if (currentStats.lastActiveDateString.isBlank()) {
                    newStreakDays = 1
                } else {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    try {
                        val lastDate = sdf.parse(currentStats.lastActiveDateString)
                        val todayDate = sdf.parse(todayStr)
                        if (todayDate != null && lastDate != null) {
                            val diffDays = (todayDate.time - lastDate.time) / (1000 * 60 * 60 * 24)
                            newStreakDays = if (diffDays == 1L) currentStats.currentStreakDays + 1 else 1
                        } else {
                            newStreakDays = 1
                        }
                    } catch (e: Exception) {
                        newStreakDays = 1
                    }
                }
            }
        } else {
            // Not all words practiced today yet.
            // Check if yesterday or earlier was missed:
            if (currentStats.lastActiveDateString.isNotBlank() && currentStats.lastActiveDateString != todayStr) {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                try {
                    val lastDate = sdf.parse(currentStats.lastActiveDateString)
                    val todayDate = sdf.parse(todayStr)
                    if (todayDate != null && lastDate != null) {
                        val diffDays = (todayDate.time - lastDate.time) / (1000 * 60 * 60 * 24)
                        if (diffDays > 1L) {
                            newStreakDays = 0 // Streak broke because a day was missed
                        }
                    }
                } catch (e: Exception) {
                    newStreakDays = 0
                }
            }
        }

        // 3. XP calculation
        val streakBonusXp = if (justEarnedDailyStreak) 50 else 0
        val baseWordXp = if (isCorrect) {
            var xp = 15
            if (newStreak >= 3) xp += 10 // Word streak bonus
            if (!usedHint) xp += 5 // No hint bonus
            if (wasJustMastered) xp += 25 // Mastery bonus
            xp
        } else {
            3 // Encouragement XP for effort
        }
        val xpGain = baseWordXp + streakBonusXp

        val totalMastered = if (wasJustMastered) currentStats.totalWordsMastered + 1 else currentStats.totalWordsMastered
        val updatedStats = currentStats.copy(
            totalXp = currentStats.totalXp + xpGain,
            currentStreakDays = newStreakDays,
            lastActiveDateString = newLastActiveDate,
            totalWordsMastered = totalMastered
        )
        dao.insertOrUpdateUserStats(updatedStats)

        // 4. Badges check
        if (updatedStats.totalWordsMastered >= 1) {
            dao.unlockBadge("first_word", currentTime)
        }
        if (updatedStats.currentStreakDays >= 3) {
            dao.unlockBadge("streak_3", currentTime)
        }
        if (updatedStats.currentStreakDays >= 7) {
            dao.unlockBadge("streak_7", currentTime)
        }
        if (updatedStats.totalXp >= 100) {
            dao.unlockBadge("xp_100", currentTime)
        }
        if (updatedStats.totalXp >= 500) {
            dao.unlockBadge("xp_500", currentTime)
        }
        if (mode == "listen" && isCorrect && newStreak >= 5) {
            dao.unlockBadge("listen_master", currentTime)
        }
        if (mode == "scramble" && isCorrect && newStreak >= 5) {
            dao.unlockBadge("scramble_champ", currentTime)
        }
        if (mode == "lscwc" && isCorrect) {
            dao.unlockBadge("lscwc_pro", currentTime)
        }
        if (mode == "vowel" && isCorrect) {
            dao.unlockBadge("vowel_hunter", currentTime)
        }

        return AttemptOutcome(
            isCorrect = isCorrect,
            xpEarned = xpGain,
            newBoxLevel = newBoxLevel,
            wasJustMastered = wasJustMastered,
            currentStreak = newStreak,
            isDailyStreakEarned = justEarnedDailyStreak,
            wordsPracticedTodayCount = wordsPracticedTodayCount,
            totalWordsInList = totalActiveWords
        )
    }

    suspend fun recordQuizCompleted(totalQuestions: Int, correctAnswers: Int): QuizOutcome {
        val currentTime = System.currentTimeMillis()
        val stats = dao.getUserStats().firstOrNull() ?: UserStats(id = 1)
        val isPerfect = correctAnswers == totalQuestions && totalQuestions > 0
        val baseScore = correctAnswers * 20
        val bonus = if (isPerfect) 100 else (correctAnswers * 5)
        val totalXpEarned = baseScore + bonus

        val updatedStats = stats.copy(
            totalXp = stats.totalXp + totalXpEarned,
            totalQuizzesTaken = stats.totalQuizzesTaken + 1,
            totalPerfectQuizzes = if (isPerfect) stats.totalPerfectQuizzes + 1 else stats.totalPerfectQuizzes
        )
        dao.insertOrUpdateUserStats(updatedStats)

        if (isPerfect) {
            dao.unlockBadge("spelling_bee_champ", currentTime)
            dao.unlockBadge("perfect_score", currentTime)
        }
        if (updatedStats.totalXp >= 100) {
            dao.unlockBadge("xp_100", currentTime)
        }
        if (updatedStats.totalXp >= 500) {
            dao.unlockBadge("xp_500", currentTime)
        }

        return QuizOutcome(
            totalQuestions = totalQuestions,
            correctAnswers = correctAnswers,
            xpEarned = totalXpEarned,
            isPerfect = isPerfect
        )
    }

    suspend fun updateStudentProfile(name: String, avatar: String, superpower: String = "Sound Detective 🔍") {
        val stats = dao.getUserStats().firstOrNull() ?: UserStats(id = 1)
        dao.insertOrUpdateUserStats(
            stats.copy(
                studentName = name,
                currentAvatar = avatar,
                spellerSuperpower = superpower.ifBlank { "Sound Detective 🔍" }
            )
        )
    }

    suspend fun updateStudentSuperpower(superpower: String) {
        val stats = dao.getUserStats().firstOrNull() ?: UserStats(id = 1)
        dao.insertOrUpdateUserStats(
            stats.copy(
                spellerSuperpower = superpower.ifBlank { "Sound Detective 🔍" }
            )
        )
    }

    suspend fun completeOnboarding(
        studentName: String,
        avatar: String,
        gradeLevel: String,
        parentPin: String,
        dailyTimeLimitMinutes: Int,
        dailyWordGoal: Int,
        welcomeBonusXp: Int = 50,
        initialWords: String = "",
        spellerSuperpower: String = "Sound Detective 🔍"
    ) {
        val stats = dao.getUserStats().firstOrNull() ?: UserStats(id = 1)
        val updatedStats = stats.copy(
            studentName = studentName.ifBlank { "Super Speller" },
            currentAvatar = avatar,
            spellerSuperpower = spellerSuperpower.ifBlank { "Sound Detective 🔍" },
            gradeLevel = gradeLevel,
            parentPin = parentPin,
            dailyTimeLimitMinutes = dailyTimeLimitMinutes,
            dailyWordGoal = dailyWordGoal,
            onboardingCompleted = true,
            totalXp = stats.totalXp + welcomeBonusXp
        )
        dao.insertOrUpdateUserStats(updatedStats)

        if (initialWords.isNotBlank()) {
            val activeList = dao.getActiveWeeklyList().firstOrNull()
            val listId = if (activeList != null) {
                activeList.id
            } else {
                createWeeklyList("This Week's Spelling List", "Required spelling words entered by parent", 1)
            }
            bulkImportWords(listId, initialWords)
        }
    }

    suspend fun updateParentalSettings(
        parentPin: String,
        dailyTimeLimitMinutes: Int,
        dailyWordGoal: Int,
        gradeLevel: String? = null
    ) {
        val stats = dao.getUserStats().firstOrNull() ?: UserStats(id = 1)
        val updatedStats = stats.copy(
            parentPin = parentPin,
            dailyTimeLimitMinutes = dailyTimeLimitMinutes,
            dailyWordGoal = dailyWordGoal,
            gradeLevel = gradeLevel ?: stats.gradeLevel
        )
        dao.insertOrUpdateUserStats(updatedStats)
    }

    suspend fun resetOnboarding() {
        val stats = dao.getUserStats().firstOrNull() ?: UserStats(id = 1)
        dao.insertOrUpdateUserStats(
            stats.copy(onboardingCompleted = false)
        )
    }
}

data class AttemptOutcome(
    val isCorrect: Boolean,
    val xpEarned: Int,
    val newBoxLevel: Int,
    val wasJustMastered: Boolean,
    val currentStreak: Int,
    val isDailyStreakEarned: Boolean = false,
    val wordsPracticedTodayCount: Int = 0,
    val totalWordsInList: Int = 0
)

data class QuizOutcome(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val xpEarned: Int,
    val isPerfect: Boolean
)
