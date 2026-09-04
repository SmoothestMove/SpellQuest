package com.example.util

/**
 * Diagnostic Taxonomy and Orthographic Analysis for Early Literacy (Grades 1-3)
 * Based on the Science of Reading (Ehri's Orthographic Mapping & Structured Phonics).
 */
enum class ErrorCategory(
    val title: String,
    val badge: String,
    val pedagogicalAction: String
) {
    PHONETIC_PLAUSIBLE(
        title = "Phonetically Plausible Error",
        badge = "👂 Phonetic Mapping",
        pedagogicalAction = "Heard all the sounds correctly! Focus on the specific orthographic spelling pattern."
    ),
    DYSLEXIC_REVERSAL(
        title = "Reversal / Inversion Marker",
        badge = "🔄 Letter Directionality",
        pedagogicalAction = "Watch letter orientations (b/d, p/q, m/w) or adjacent letter order."
    ),
    VOWEL_CONFUSION(
        title = "Vowel / Digraph Marker",
        badge = "🎯 Vowel Discrimination",
        pedagogicalAction = "Focus on the vowel sound / phonogram (short vs long vowel or vowel team)."
    ),
    HEART_WORD_EXCEPTION(
        title = "Heart Word / Tricky Spelling",
        badge = "❤️ Heart Letter Rule",
        pedagogicalAction = "Memorize by heart the part of this word that doesn't follow standard phonetic rules."
    ),
    MINOR_SLIP(
        title = "Single Letter Typo / Slip",
        badge = "⚡ Minor Slip",
        pedagogicalAction = "Nearly perfect! Only one letter was off. Take a quick look and try again."
    ),
    OMISSION_ADDITION(
        title = "Phoneme Omission / Addition",
        badge = "🧩 Segmentation Alert",
        pedagogicalAction = "Tap each sound unit (phoneme) on your fingers before writing."
    )
}

data class OrthographicAnalysis(
    val isCorrect: Boolean,
    val targetWord: String,
    val userAttempt: String,
    val category: ErrorCategory?,
    val diagnosticSummary: String,
    val phonicsTip: String,
    val targetPhonics: String
)

object SpellingDiagnostics {

    private val REVERSAL_PAIRS = listOf(
        setOf('b', 'd'),
        setOf('p', 'q'),
        setOf('m', 'w'),
        setOf('n', 'u')
    )

    private val VOWELS = setOf('a', 'e', 'i', 'o', 'u', 'y')

    // Common phonetic equivalents in English for early literacy
    private val PHONETIC_EQUIVALENTS = mapOf(
        "c" to listOf("k", "ck"),
        "k" to listOf("c", "ck"),
        "ck" to listOf("k", "c"),
        "ph" to listOf("f"),
        "f" to listOf("ph"),
        "ea" to listOf("ee", "e"),
        "ee" to listOf("ea", "e"),
        "ai" to listOf("ay", "a"),
        "ay" to listOf("ai", "a"),
        "oa" to listOf("ow", "o"),
        "ow" to listOf("oa", "o"),
        "igh" to listOf("ite", "i"),
        "s" to listOf("c", "z"),
        "z" to listOf("s"),
        "j" to listOf("g", "dge"),
        "g" to listOf("j")
    )

    fun analyze(target: String, attempt: String, phonicsDisplay: String = ""): OrthographicAnalysis {
        val cleanTarget = target.trim().lowercase()
        val cleanAttempt = attempt.trim().lowercase()

        if (cleanTarget == cleanAttempt) {
            return OrthographicAnalysis(
                isCorrect = true,
                targetWord = cleanTarget,
                userAttempt = cleanAttempt,
                category = null,
                diagnosticSummary = "Orthographic match! Clean grapheme-to-phoneme retrieval.",
                phonicsTip = "Stored in long-term memory!",
                targetPhonics = phonicsDisplay.ifBlank { cleanTarget }
            )
        }

        val category: ErrorCategory
        val summary: String
        val tip: String

        // 1. Check for Dyslexic Reversals (e.g. b <-> d, p <-> q) or Transpositions (adjacent letter swap like "teh" for "the")
        val isReversal = checkReversal(cleanTarget, cleanAttempt)
        val isTransposition = checkTransposition(cleanTarget, cleanAttempt)

        if (isReversal || isTransposition) {
            category = ErrorCategory.DYSLEXIC_REVERSAL
            if (isReversal) {
                summary = "Reversal marker detected: Letter orientation shifted (e.g. b/d or p/q)."
                tip = "Tactile trace: Make 'bed' with your thumbs to remember 'b' (left hand) and 'd' (right hand)!"
            } else {
                summary = "Letter transposition: All phonemes present, but order was inverted."
                tip = "Say each sound slowly from left to right as you write."
            }
        } else if (isPhoneticPlausible(cleanTarget, cleanAttempt)) {
            category = ErrorCategory.PHONETIC_PLAUSIBLE
            summary = "Phonetically plausible spelling! The ear heard the sounds, but English uses a different pattern here."
            tip = "Great phonemic awareness! In '$cleanTarget', the sound is spelled with '${targetSpecificGrapheme(cleanTarget, cleanAttempt)}'."
        } else if (isVowelConfusionOnly(cleanTarget, cleanAttempt)) {
            category = ErrorCategory.VOWEL_CONFUSION
            summary = "Vowel discrimination error: Consonant framework is intact, but vowel core differed."
            tip = "Listen to the vowel sound: Is it short (apple, egg, igloo) or long (saying its name)?"
        } else if (cleanTarget.length == cleanAttempt.length && editDistance(cleanTarget, cleanAttempt) == 1) {
            category = ErrorCategory.MINOR_SLIP
            val diffIndex = cleanTarget.indices.firstOrNull { cleanTarget[it] != cleanAttempt[it] } ?: 0
            summary = "Minor single-letter slip at position ${diffIndex + 1}."
            tip = "You were 90% there! Just replace '${cleanAttempt.getOrNull(diffIndex) ?: '?'}' with '${cleanTarget[diffIndex]}'."
        } else if (Math.abs(cleanTarget.length - cleanAttempt.length) == 1) {
            category = ErrorCategory.OMISSION_ADDITION
            if (cleanAttempt.length < cleanTarget.length) {
                summary = "Phoneme omission: A sound unit was skipped."
                tip = "Stretch the word like a rubber band: '${cleanTarget.map { it }.joinToString("-")}'"
            } else {
                summary = "Phoneme addition: An extra sound or silent letter was inserted."
                tip = "Tap the syllables: Count each beat before typing."
            }
        } else {
            category = ErrorCategory.HEART_WORD_EXCEPTION
            summary = "Irregular / Heart word pattern: Does not follow standard phonetic decoding."
            tip = "Mark the tricky heart part: Focus on '${phonicsDisplay.ifBlank { cleanTarget }}'."
        }

        return OrthographicAnalysis(
            isCorrect = false,
            targetWord = cleanTarget,
            userAttempt = cleanAttempt,
            category = category,
            diagnosticSummary = summary,
            phonicsTip = tip,
            targetPhonics = phonicsDisplay.ifBlank { cleanTarget }
        )
    }

    private fun checkReversal(target: String, attempt: String): Boolean {
        if (target.length != attempt.length) return false
        var mismatchCount = 0
        var foundReversalPair = false
        for (i in target.indices) {
            val t = target[i]
            val a = attempt[i]
            if (t != a) {
                mismatchCount++
                if (REVERSAL_PAIRS.any { pair -> pair.contains(t) && pair.contains(a) }) {
                    foundReversalPair = true
                }
            }
        }
        return mismatchCount <= 2 && foundReversalPair
    }

    private fun checkTransposition(target: String, attempt: String): Boolean {
        if (target.length != attempt.length) return false
        val diffIndices = mutableListOf<Int>()
        for (i in target.indices) {
            if (target[i] != attempt[i]) {
                diffIndices.add(i)
            }
        }
        if (diffIndices.size == 2) {
            val i = diffIndices[0]
            val j = diffIndices[1]
            if (Math.abs(i - j) == 1 && target[i] == attempt[j] && target[j] == attempt[i]) {
                return true
            }
        }
        return false
    }

    private fun isVowelConfusionOnly(target: String, attempt: String): Boolean {
        if (target.length != attempt.length) return false
        var mismatchCount = 0
        var onlyVowelsMismatched = true
        for (i in target.indices) {
            val t = target[i]
            val a = attempt[i]
            if (t != a) {
                mismatchCount++
                if (t !in VOWELS || a !in VOWELS) {
                    onlyVowelsMismatched = false
                }
            }
        }
        return mismatchCount in 1..2 && onlyVowelsMismatched
    }

    private fun isPhoneticPlausible(target: String, attempt: String): Boolean {
        // Check known phonetic transformations (e.g. k <-> c, ee <-> ea, f <-> ph, etc.)
        for ((pattern, equivalents) in PHONETIC_EQUIVALENTS) {
            if (target.contains(pattern)) {
                for (equiv in equivalents) {
                    val candidate = target.replaceFirst(pattern, equiv)
                    if (candidate == attempt) return true
                }
            }
        }
        return false
    }

    private fun targetSpecificGrapheme(target: String, attempt: String): String {
        for ((pattern, equivalents) in PHONETIC_EQUIVALENTS) {
            if (target.contains(pattern)) {
                for (equiv in equivalents) {
                    if (target.replaceFirst(pattern, equiv) == attempt) {
                        return pattern
                    }
                }
            }
        }
        return target
    }

    fun editDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }
}
