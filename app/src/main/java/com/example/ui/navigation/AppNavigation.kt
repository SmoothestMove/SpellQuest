package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.CertificateDialog
import com.example.ui.components.TtsHelper
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.badges.BadgesScreen
import com.example.ui.screens.onboarding.TwoFoldOnboardingScreen
import com.example.ui.screens.parent.ParentPortalScreen
import com.example.ui.screens.parentguided.ParentGuidedPracticeScreen
import com.example.ui.screens.practice.FlashcardLearnScreen
import com.example.ui.screens.practice.ListenSpellScreen
import com.example.ui.screens.practice.MissingVowelScreen
import com.example.ui.screens.practice.SpellingBeeScreen
import com.example.ui.screens.practice.WordScrambleScreen
import com.example.viewmodel.SpellingViewModel

object Routes {
    const val HOME = "home"
    const val ONBOARDING = "onboarding"
    const val LSCWC = "lscwc"
    const val LISTEN_SPELL = "listen_spell"
    const val SCRAMBLE = "scramble"
    const val VOWELS = "vowels"
    const val SPELLING_BEE = "spelling_bee"
    const val BADGES = "badges"
    const val PARENT_ZONE = "parent_zone"
    const val PARENT_GUIDED = "parent_guided"
}

@Composable
fun AppNavigation(
    viewModel: SpellingViewModel,
    ttsHelper: TtsHelper,
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()
    val parentGuidedState by viewModel.parentGuidedState.collectAsState()
    val certificateToShow by viewModel.certificateToShow.collectAsState()

    var hasRedirectedToOnboarding by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.userStats.onboardingCompleted) {
        if (!uiState.userStats.onboardingCompleted && !hasRedirectedToOnboarding) {
            hasRedirectedToOnboarding = true
            navController.navigate(Routes.ONBOARDING)
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.ONBOARDING) {
            TwoFoldOnboardingScreen(
                ttsHelper = ttsHelper,
                initialPhase = "parent",
                onFinishOnboarding = { pin, timeLimit, wordGoal, grade, name, avatar, bonusXp, initialWords, superpower ->
                    viewModel.completeOnboarding(name, avatar, grade, pin, timeLimit, wordGoal, bonusXp, initialWords, superpower)
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
                onSkipToApp = {
                    viewModel.completeOnboarding(
                        uiState.userStats.studentName,
                        uiState.userStats.currentAvatar,
                        uiState.userStats.gradeLevel,
                        uiState.userStats.parentPin,
                        uiState.userStats.dailyTimeLimitMinutes,
                        uiState.userStats.dailyWordGoal,
                        welcomeBonusXp = 0
                    )
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable("${Routes.ONBOARDING}/{phase}") { backStackEntry ->
            val phase = backStackEntry.arguments?.getString("phase") ?: "parent"
            TwoFoldOnboardingScreen(
                ttsHelper = ttsHelper,
                initialPhase = phase,
                onFinishOnboarding = { pin, timeLimit, wordGoal, grade, name, avatar, bonusXp, initialWords, superpower ->
                    viewModel.completeOnboarding(name, avatar, grade, pin, timeLimit, wordGoal, bonusXp, initialWords, superpower)
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
                onSkipToApp = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                activeList = uiState.activeList,
                activeWords = uiState.activeWords,
                userStats = uiState.userStats,
                ttsHelper = ttsHelper,
                onNavigateToLscwc = { navController.navigate(Routes.LSCWC) },
                onNavigateToListenSpell = { navController.navigate(Routes.LISTEN_SPELL) },
                onNavigateToScramble = { navController.navigate(Routes.SCRAMBLE) },
                onNavigateToVowels = { navController.navigate(Routes.VOWELS) },
                onNavigateToSpellingBee = { navController.navigate(Routes.SPELLING_BEE) },
                onNavigateToBadges = { navController.navigate(Routes.BADGES) },
                onNavigateToParentZone = { navController.navigate(Routes.PARENT_ZONE) },
                onNavigateToParentGuided = { navController.navigate(Routes.PARENT_GUIDED) },
                onNavigateToOnboarding = { phase -> navController.navigate("${Routes.ONBOARDING}/$phase") },
                onAddRequiredWords = { wordsRaw ->
                    viewModel.addRequiredWords(wordsRaw)
                },
                onUpdateSuperpower = { powerTitle ->
                    viewModel.updateStudentSuperpower(powerTitle)
                }
            )
        }

        composable(Routes.PARENT_GUIDED) {
            ParentGuidedPracticeScreen(
                activeList = uiState.activeList,
                activeWords = uiState.activeWords,
                sessionState = parentGuidedState,
                studentName = uiState.userStats.studentName,
                ttsHelper = ttsHelper,
                onSendWordToStudent = { word, note, phonics, sentence ->
                    viewModel.sendWordToStudent(word, note, phonics, sentence)
                },
                onSubmitStudentGuess = { guess ->
                    viewModel.submitStudentGuess(guess)
                },
                onReviewStudentGuess = { isApproved, note, sticker, markMastered ->
                    viewModel.reviewStudentGuess(isApproved, note, sticker, markMastered)
                },
                onRequestRetry = { note ->
                    viewModel.requestStudentRetry(note)
                },
                onResetToPickWord = {
                    viewModel.resetToPickNextWord()
                },
                onResetSession = {
                    viewModel.resetParentGuidedSession()
                },
                onOpenParentZone = {
                    navController.navigate(Routes.PARENT_ZONE)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.LSCWC) {
            FlashcardLearnScreen(
                words = uiState.activeWords,
                ttsHelper = ttsHelper,
                onRecordAttempt = { word, isCorrect, mode, usedHint ->
                    viewModel.recordAttempt(word, isCorrect, mode, usedHint)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.LISTEN_SPELL) {
            ListenSpellScreen(
                words = uiState.activeWords,
                ttsHelper = ttsHelper,
                onRecordAttempt = { word, isCorrect, mode, usedHint ->
                    viewModel.recordAttempt(word, isCorrect, mode, usedHint)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SCRAMBLE) {
            WordScrambleScreen(
                words = uiState.activeWords,
                ttsHelper = ttsHelper,
                onRecordAttempt = { word, isCorrect, mode, usedHint ->
                    viewModel.recordAttempt(word, isCorrect, mode, usedHint)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.VOWELS) {
            MissingVowelScreen(
                words = uiState.activeWords,
                ttsHelper = ttsHelper,
                onRecordAttempt = { word, isCorrect, mode, usedHint ->
                    viewModel.recordAttempt(word, isCorrect, mode, usedHint)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SPELLING_BEE) {
            SpellingBeeScreen(
                words = uiState.activeWords,
                ttsHelper = ttsHelper,
                onQuizCompleted = { total, correct ->
                    viewModel.recordQuizCompleted(total, correct)
                },
                onRecordAttempt = { word, isCorrect, mode, usedHint ->
                    viewModel.recordAttempt(word, isCorrect, mode, usedHint)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.BADGES) {
            BadgesScreen(
                userStats = uiState.userStats,
                badges = uiState.badges,
                onViewCertificate = {
                    viewModel.showManualCertificate(uiState.activeList?.title ?: "Weekly Spelling Challenge")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PARENT_ZONE) {
            ParentPortalScreen(
                weeklyLists = uiState.allLists,
                activeList = uiState.activeList,
                activeWords = uiState.activeWords,
                trickyWords = uiState.trickyWords,
                userStats = uiState.userStats,
                onSetActiveList = { id -> viewModel.setActiveWeeklyList(id) },
                onCreateWeeklyList = { title, desc, weekNo, grade ->
                    viewModel.createWeeklyList(title, desc, weekNo, grade)
                },
                onDeleteWeeklyList = { id -> viewModel.deleteWeeklyList(id) },
                onAddWord = { listId, word, phonics, hint, sentence, def ->
                    viewModel.addWord(listId, word, phonics, hint, sentence, def)
                },
                onDeleteWord = { id -> viewModel.deleteWord(id) },
                onBulkImport = { listId, text -> viewModel.bulkImportWords(listId, text) },
                onClearWordsInList = { listId -> viewModel.clearWordsInList(listId) },
                onUpdateProfile = { name, avatar -> viewModel.updateStudentProfile(name, avatar) },
                onUpdateParentalSettings = { pin, timeLimit, wordGoal ->
                    viewModel.updateParentalSettings(pin, timeLimit, wordGoal)
                },
                onReplayParentTour = { navController.navigate("${Routes.ONBOARDING}/parent") },
                onReplayChildTour = { navController.navigate("${Routes.ONBOARDING}/child") },
                onNavigateToParentGuided = { navController.navigate(Routes.PARENT_GUIDED) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }

    // Floating Award Certificate Dialog when earned
    certificateToShow?.let { cert ->
        CertificateDialog(
            studentName = cert.studentName,
            listTitle = cert.listTitle,
            scorePercent = cert.scorePercent,
            onDismiss = { viewModel.clearCertificate() }
        )
    }
}
