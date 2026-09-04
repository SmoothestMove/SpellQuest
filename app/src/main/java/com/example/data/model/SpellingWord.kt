package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "spelling_words",
    foreignKeys = [
        ForeignKey(
            entity = WeeklyList::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("listId")]
)
data class SpellingWord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val listId: Long,
    val word: String,
    val phonics: String = "",
    val hint: String = "",
    val sentence: String = "",
    val definition: String = "",
    val boxLevel: Int = 0, // 0=New, 1=Learning, 2=Practicing, 3=Mastered, 4=Champion
    val correctAttempts: Int = 0,
    val incorrectAttempts: Int = 0,
    val streak: Int = 0,
    val lastPracticedTimestamp: Long = 0L,
    val nextReviewTimestamp: Long = 0L,
    val isMastered: Boolean = false
) {
    val cleanWord: String
        get() = word.trim().lowercase()

    val displayPhonics: String
        get() = if (phonics.isNotBlank()) phonics else breakdownPhonics(word)

    val displaySentence: String
        get() = if (sentence.isNotBlank()) sentence else "Can you spell the word: _____ ?"

    val sentenceWithBlank: String
        get() {
            if (sentence.isBlank()) return "Please spell: _____"
            val regex = Regex("(?i)\\b${Regex.escape(word)}\\b")
            return if (regex.containsMatchIn(sentence)) {
                sentence.replace(regex, "_____")
            } else if (sentence.contains("_____")) {
                sentence
            } else {
                "$sentence (Spell: _____)"
            }
        }

    val masteryPercentage: Float
        get() {
            if (isMastered || boxLevel >= 3) return 1f
            return (boxLevel / 3f).coerceIn(0f, 1f)
        }

    fun isPracticedToday(currentTimestamp: Long = System.currentTimeMillis()): Boolean {
        if (lastPracticedTimestamp <= 0L) return false
        val calWord = java.util.Calendar.getInstance().apply { timeInMillis = lastPracticedTimestamp }
        val calCurrent = java.util.Calendar.getInstance().apply { timeInMillis = currentTimestamp }
        return calWord.get(java.util.Calendar.YEAR) == calCurrent.get(java.util.Calendar.YEAR) &&
               calWord.get(java.util.Calendar.DAY_OF_YEAR) == calCurrent.get(java.util.Calendar.DAY_OF_YEAR)
    }

    companion object {
        fun breakdownPhonics(input: String): String {
            val vowels = setOf('a', 'e', 'i', 'o', 'u', 'y', 'A', 'E', 'I', 'O', 'U', 'Y')
            val s = input.trim()
            if (s.length <= 3) return s
            val sb = StringBuilder()
            var vowelCount = 0
            for (i in s.indices) {
                sb.append(s[i])
                if (s[i] in vowels) {
                    vowelCount++
                    if (vowelCount >= 1 && i < s.length - 2 && s[i + 1] !in vowels && s[i + 2] in vowels) {
                        sb.append("·")
                        vowelCount = 0
                    }
                }
            }
            return sb.toString()
        }
    }
}
