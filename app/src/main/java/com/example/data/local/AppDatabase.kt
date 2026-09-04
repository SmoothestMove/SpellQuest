package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Badge
import com.example.data.model.GradePacks
import com.example.data.model.SpellingWord
import com.example.data.model.UserStats
import com.example.data.model.WeeklyList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        WeeklyList::class,
        SpellingWord::class,
        UserStats::class,
        Badge::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun spellingDao(): SpellingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "spellquest_clean_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.spellingDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: SpellingDao) {
            // Initial Clean User Stats
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
                    studentName = "Super Speller",
                    onboardingCompleted = false,
                    parentPin = "",
                    dailyTimeLimitMinutes = 20,
                    dailyWordGoal = 10,
                    gradeLevel = "Grade 2"
                )
            )

            // Initial Badge Definitions (All locked)
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
    }
}
