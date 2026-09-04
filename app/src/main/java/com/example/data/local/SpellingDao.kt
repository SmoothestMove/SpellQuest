package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Badge
import com.example.data.model.SpellingWord
import com.example.data.model.UserStats
import com.example.data.model.WeeklyList
import kotlinx.coroutines.flow.Flow

@Dao
interface SpellingDao {

    // Weekly Lists
    @Query("SELECT * FROM weekly_lists ORDER BY weekNumber ASC, createdTimestamp DESC")
    fun getAllWeeklyLists(): Flow<List<WeeklyList>>

    @Query("SELECT * FROM weekly_lists WHERE isActive = 1 LIMIT 1")
    fun getActiveWeeklyList(): Flow<WeeklyList?>

    @Query("SELECT * FROM weekly_lists WHERE id = :id LIMIT 1")
    suspend fun getWeeklyListById(id: Long): WeeklyList?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyList(list: WeeklyList): Long

    @Update
    suspend fun updateWeeklyList(list: WeeklyList)

    @Query("UPDATE weekly_lists SET isActive = 0")
    suspend fun deactivateAllLists()

    @Query("UPDATE weekly_lists SET isActive = 1 WHERE id = :id")
    suspend fun setActiveList(id: Long)

    @Query("DELETE FROM weekly_lists WHERE id = :id")
    suspend fun deleteWeeklyList(id: Long)

    // Spelling Words
    @Query("SELECT * FROM spelling_words WHERE listId = :listId ORDER BY id ASC")
    fun getWordsForList(listId: Long): Flow<List<SpellingWord>>

    @Query("SELECT * FROM spelling_words WHERE listId = (SELECT id FROM weekly_lists WHERE isActive = 1 LIMIT 1) ORDER BY id ASC")
    fun getWordsForActiveList(): Flow<List<SpellingWord>>

    @Query("SELECT * FROM spelling_words WHERE listId = (SELECT id FROM weekly_lists WHERE isActive = 1 LIMIT 1) ORDER BY id ASC")
    suspend fun getWordsForActiveListDirect(): List<SpellingWord>

    @Query("SELECT * FROM spelling_words WHERE listId = :listId AND (isMastered = 0 OR boxLevel < 3) ORDER BY boxLevel ASC, incorrectAttempts DESC")
    fun getPracticeWordsForList(listId: Long): Flow<List<SpellingWord>>

    @Query("SELECT * FROM spelling_words WHERE incorrectAttempts > 0 ORDER BY incorrectAttempts DESC LIMIT 10")
    fun getTrickyWords(): Flow<List<SpellingWord>>

    @Query("SELECT * FROM spelling_words WHERE id = :id LIMIT 1")
    suspend fun getWordById(id: Long): SpellingWord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: SpellingWord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<SpellingWord>)

    @Update
    suspend fun updateWord(word: SpellingWord)

    @Query("DELETE FROM spelling_words WHERE id = :id")
    suspend fun deleteWord(id: Long)

    @Query("DELETE FROM spelling_words WHERE listId = :listId")
    suspend fun clearWordsInList(listId: Long)

    @Query("DELETE FROM spelling_words")
    suspend fun clearAllWords()

    @Query("SELECT COUNT(*) FROM spelling_words WHERE isMastered = 1")
    fun getTotalMasteredCount(): Flow<Int>

    // User Stats
    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    fun getUserStats(): Flow<UserStats?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserStats(stats: UserStats)

    // Badges
    @Query("SELECT * FROM badges ORDER BY isUnlocked DESC, id ASC")
    fun getAllBadges(): Flow<List<Badge>>

    @Query("SELECT * FROM badges WHERE isUnlocked = 1")
    fun getUnlockedBadges(): Flow<List<Badge>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInitialBadges(badges: List<Badge>)

    @Update
    suspend fun updateBadge(badge: Badge)

    @Query("UPDATE badges SET isUnlocked = 1, unlockedTimestamp = :timestamp WHERE id = :badgeId AND isUnlocked = 0")
    suspend fun unlockBadge(badgeId: String, timestamp: Long = System.currentTimeMillis()): Int
}
