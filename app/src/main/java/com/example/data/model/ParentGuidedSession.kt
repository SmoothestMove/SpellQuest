package com.example.data.model

enum class ParentGuidedStatus {
    IDLE,               // Parent is selecting a word to send
    SENT_TO_STUDENT,    // Word audio sent to student; student attempts to spell
    STUDENT_SUBMITTED,  // Student finished spelling; guess is sent back to parent
    PARENT_REVIEWED     // Parent reviewed guess (approved or requested retry)
}

data class ParentCheckFeedback(
    val isApproved: Boolean,
    val feedbackNote: String = "",
    val sticker: String = "⭐",
    val xpAwarded: Int = 20,
    val reviewTimestamp: Long = System.currentTimeMillis()
)

data class CompletedParentWordLog(
    val word: String,
    val studentGuess: String,
    val isCorrect: Boolean,
    val parentFeedback: String = "",
    val sticker: String = "⭐",
    val timestamp: Long = System.currentTimeMillis()
)

data class ParentGuidedState(
    val status: ParentGuidedStatus = ParentGuidedStatus.IDLE,
    val activeWord: SpellingWord? = null,
    val parentNote: String = "",
    val studentGuess: String = "",
    val providePhonicsHint: Boolean = true,
    val provideSentenceClue: Boolean = true,
    val lastFeedback: ParentCheckFeedback? = null,
    val sessionHistory: List<CompletedParentWordLog> = emptyList(),
    val totalWordsTested: Int = 0,
    val totalApproved: Int = 0
)
