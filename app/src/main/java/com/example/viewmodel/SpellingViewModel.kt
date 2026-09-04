package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Badge
import com.example.data.model.CompletedParentWordLog
import com.example.data.model.ParentCheckFeedback
import com.example.data.model.ParentGuidedState
import com.example.data.model.ParentGuidedStatus
import com.example.data.model.PredefinedGradePack
import com.example.data.model.SpellingWord
import com.example.data.model.UserStats
import com.example.data.model.WeeklyList
import com.example.data.repository.AttemptOutcome
import com.example.data.repository.QuizOutcome
import com.example.data.repository.SpellingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MainUiState(
    val activeList: WeeklyList? = null,
    val activeWords: List<SpellingWord> = emptyList(),
    val allLists: List<WeeklyList> = emptyList(),
    val trickyWords: List<SpellingWord> = emptyList(),
    val userStats: UserStats = UserStats(),
    val badges: List<Badge> = emptyList(),
    val totalMastered: Int = 0,
    val isLoading: Boolean = false
)

data class CertificateInfo(
    val studentName: String,
    val listTitle: String,
    val scorePercent: Int
)

class SpellingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SpellingRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = SpellingRepository(database.spellingDao())
        viewModelScope.launch {
            repository.ensureInitialSeedData()
        }
    }

    val uiState: StateFlow<MainUiState> = combine(
        combine(
            repository.activeWeeklyList,
            repository.activeListWords,
            repository.allWeeklyLists,
            repository.trickyWords
        ) { activeList, activeWords, allLists, trickyWords ->
            listOf<Any?>(activeList, activeWords, allLists, trickyWords)
        },
        combine(
            repository.userStats,
            repository.allBadges,
            repository.totalMasteredCount
        ) { userStats, badges, totalMastered ->
            Triple(userStats, badges, totalMastered)
        }
    ) { list1, triple ->
        @Suppress("UNCHECKED_CAST")
        val activeList = list1[0] as? WeeklyList
        @Suppress("UNCHECKED_CAST")
        val activeWords = list1[1] as List<SpellingWord>
        @Suppress("UNCHECKED_CAST")
        val allLists = list1[2] as List<WeeklyList>
        @Suppress("UNCHECKED_CAST")
        val trickyWords = list1[3] as List<SpellingWord>

        val userStats = triple.first ?: UserStats()
        val badges = triple.second
        val totalMastered = triple.third

        MainUiState(
            activeList = activeList,
            activeWords = activeWords,
            allLists = allLists,
            trickyWords = trickyWords,
            userStats = userStats,
            badges = badges,
            totalMastered = totalMastered,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState(isLoading = true)
    )

    private val _lastAttemptOutcome = MutableStateFlow<AttemptOutcome?>(null)
    val lastAttemptOutcome: StateFlow<AttemptOutcome?> = _lastAttemptOutcome.asStateFlow()

    private val _lastQuizOutcome = MutableStateFlow<QuizOutcome?>(null)
    val lastQuizOutcome: StateFlow<QuizOutcome?> = _lastQuizOutcome.asStateFlow()

    private val _certificateToShow = MutableStateFlow<CertificateInfo?>(null)
    val certificateToShow: StateFlow<CertificateInfo?> = _certificateToShow.asStateFlow()

    private val _parentGuidedState = MutableStateFlow(ParentGuidedState())
    val parentGuidedState: StateFlow<ParentGuidedState> = _parentGuidedState.asStateFlow()

    // --- Parent-Guided Practice Methods ---
    fun sendWordToStudent(
        word: SpellingWord,
        parentNote: String = "",
        providePhonicsHint: Boolean = true,
        provideSentenceClue: Boolean = true
    ) {
        _parentGuidedState.value = _parentGuidedState.value.copy(
            status = ParentGuidedStatus.SENT_TO_STUDENT,
            activeWord = word,
            parentNote = parentNote,
            studentGuess = "",
            providePhonicsHint = providePhonicsHint,
            provideSentenceClue = provideSentenceClue,
            lastFeedback = null
        )
    }

    fun submitStudentGuess(guess: String) {
        _parentGuidedState.value = _parentGuidedState.value.copy(
            status = ParentGuidedStatus.STUDENT_SUBMITTED,
            studentGuess = guess.trim()
        )
    }

    fun reviewStudentGuess(
        isApproved: Boolean,
        feedbackNote: String = "",
        sticker: String = "⭐",
        markAsMastered: Boolean = false
    ) {
        val current = _parentGuidedState.value
        val word = current.activeWord ?: return
        val guess = current.studentGuess

        val isTargetMatch = guess.equals(word.word, ignoreCase = true)
        val finalCorrect = isApproved || isTargetMatch

        // Record in repository to award stars, update Leitner box, streaks, and badges
        recordAttempt(
            word = word,
            isCorrect = finalCorrect,
            mode = "parent_guided",
            usedHint = current.providePhonicsHint
        )

        if (markAsMastered) {
            viewModelScope.launch {
                repository.updateWord(word.copy(boxLevel = 3, isMastered = true))
            }
        }

        val feedback = ParentCheckFeedback(
            isApproved = finalCorrect,
            feedbackNote = feedbackNote,
            sticker = sticker,
            xpAwarded = if (finalCorrect) 20 else 5
        )

        val completedLog = CompletedParentWordLog(
            word = word.word,
            studentGuess = guess,
            isCorrect = finalCorrect,
            parentFeedback = feedbackNote,
            sticker = sticker
        )

        val updatedHistory = listOf(completedLog) + current.sessionHistory

        _parentGuidedState.value = current.copy(
            status = ParentGuidedStatus.PARENT_REVIEWED,
            lastFeedback = feedback,
            sessionHistory = updatedHistory,
            totalWordsTested = current.totalWordsTested + 1,
            totalApproved = if (finalCorrect) current.totalApproved + 1 else current.totalApproved
        )
    }

    fun requestStudentRetry(coachingNote: String = "") {
        val current = _parentGuidedState.value
        _parentGuidedState.value = current.copy(
            status = ParentGuidedStatus.SENT_TO_STUDENT,
            parentNote = if (coachingNote.isNotBlank()) coachingNote else current.parentNote,
            studentGuess = "",
            lastFeedback = null
        )
    }

    fun resetToPickNextWord() {
        val current = _parentGuidedState.value
        _parentGuidedState.value = current.copy(
            status = ParentGuidedStatus.IDLE,
            activeWord = null,
            studentGuess = "",
            lastFeedback = null
        )
    }

    fun resetParentGuidedSession() {
        _parentGuidedState.value = ParentGuidedState()
    }

    fun recordAttempt(
        word: SpellingWord,
        isCorrect: Boolean,
        mode: String,
        usedHint: Boolean = false,
        onOutcome: ((AttemptOutcome) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val outcome = repository.recordAttempt(word, isCorrect, mode, usedHint)
            _lastAttemptOutcome.value = outcome
            onOutcome?.invoke(outcome)
        }
    }

    fun recordQuizCompleted(total: Int, correct: Int, onOutcome: ((QuizOutcome) -> Unit)? = null) {
        viewModelScope.launch {
            val outcome = repository.recordQuizCompleted(total, correct)
            _lastQuizOutcome.value = outcome
            val percent = if (total > 0) (correct * 100) / total else 0
            if (percent >= 80) {
                val currentList = uiState.value.activeList
                _certificateToShow.value = CertificateInfo(
                    studentName = uiState.value.userStats.studentName,
                    listTitle = currentList?.title ?: "Weekly Spelling Bee",
                    scorePercent = percent
                )
            }
            onOutcome?.invoke(outcome)
        }
    }

    fun clearCertificate() {
        _certificateToShow.value = null
    }

    fun showManualCertificate(listTitle: String) {
        _certificateToShow.value = CertificateInfo(
            studentName = uiState.value.userStats.studentName,
            listTitle = listTitle,
            scorePercent = 100
        )
    }

    fun setActiveWeeklyList(listId: Long) {
        viewModelScope.launch {
            repository.setActiveWeeklyList(listId)
        }
    }

    fun createWeeklyList(
        title: String,
        description: String,
        weekNumber: Int,
        gradeLevel: String = "Elementary",
        makeActive: Boolean = true
    ) {
        viewModelScope.launch {
            repository.createWeeklyList(title, description, weekNumber, gradeLevel, makeActive)
        }
    }

    fun deleteWeeklyList(listId: Long) {
        viewModelScope.launch {
            repository.deleteWeeklyList(listId)
        }
    }

    fun addWord(
        listId: Long,
        word: String,
        phonics: String = "",
        hint: String = "",
        sentence: String = "",
        definition: String = ""
    ) {
        viewModelScope.launch {
            repository.addWord(listId, word, phonics, hint, sentence, definition)
        }
    }

    fun updateWord(word: SpellingWord) {
        viewModelScope.launch {
            repository.updateWord(word)
        }
    }

    fun deleteWord(wordId: Long) {
        viewModelScope.launch {
            repository.deleteWord(wordId)
        }
    }

    fun importGradePack(pack: PredefinedGradePack, makeActive: Boolean = true) {
        viewModelScope.launch {
            repository.importGradePack(pack, makeActive)
        }
    }

    fun bulkImportWords(listId: Long, rawText: String) {
        viewModelScope.launch {
            repository.bulkImportWords(listId, rawText)
        }
    }

    fun clearWordsInList(listId: Long) {
        viewModelScope.launch {
            repository.clearWordsInList(listId)
        }
    }

    fun addRequiredWords(rawText: String) {
        viewModelScope.launch {
            val active = uiState.value.activeList
            val listId = if (active != null) {
                active.id
            } else {
                repository.createWeeklyList(
                    title = "This Week's Spelling List",
                    description = "Required spelling list entered by parent",
                    weekNumber = 1
                )
            }
            repository.bulkImportWords(listId, rawText)
        }
    }

    fun updateStudentProfile(name: String, avatar: String, superpower: String = "Sound Detective 🔍") {
        viewModelScope.launch {
            repository.updateStudentProfile(name, avatar, superpower)
        }
    }

    fun completeOnboarding(
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
        viewModelScope.launch {
            repository.completeOnboarding(
                studentName = studentName,
                avatar = avatar,
                gradeLevel = gradeLevel,
                parentPin = parentPin,
                dailyTimeLimitMinutes = dailyTimeLimitMinutes,
                dailyWordGoal = dailyWordGoal,
                welcomeBonusXp = welcomeBonusXp,
                initialWords = initialWords,
                spellerSuperpower = spellerSuperpower
            )
        }
    }

    fun updateParentalSettings(
        parentPin: String,
        dailyTimeLimitMinutes: Int,
        dailyWordGoal: Int,
        gradeLevel: String? = null
    ) {
        viewModelScope.launch {
            repository.updateParentalSettings(
                parentPin = parentPin,
                dailyTimeLimitMinutes = dailyTimeLimitMinutes,
                dailyWordGoal = dailyWordGoal,
                gradeLevel = gradeLevel
            )
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            repository.resetOnboarding()
        }
    }
}
