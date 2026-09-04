package com.example.ui.screens.parentguided

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ParentGuidedState
import com.example.data.model.ParentGuidedStatus
import com.example.data.model.SpellingWord
import com.example.data.model.WeeklyList
import com.example.ui.components.TtsHelper
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoOnPrimaryContainer
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentGuidedPracticeScreen(
    activeList: WeeklyList?,
    activeWords: List<SpellingWord>,
    sessionState: ParentGuidedState,
    studentName: String,
    ttsHelper: TtsHelper,
    onSendWordToStudent: (SpellingWord, String, Boolean, Boolean) -> Unit,
    onSubmitStudentGuess: (String) -> Unit,
    onReviewStudentGuess: (isApproved: Boolean, feedbackNote: String, sticker: String, markAsMastered: Boolean) -> Unit,
    onRequestRetry: (String) -> Unit,
    onResetToPickWord: () -> Unit,
    onResetSession: () -> Unit,
    onOpenParentZone: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // 0 = Parent Dashboard, 1 = Student Screen
    var selectedTab by remember { mutableIntStateOf(0) }

    // Auto-switch tabs to guide pass-and-play naturally
    LaunchedEffect(sessionState.status) {
        when (sessionState.status) {
            ParentGuidedStatus.SENT_TO_STUDENT -> {
                // Audio sent to student -> switch to student view
                selectedTab = 1
            }
            ParentGuidedStatus.STUDENT_SUBMITTED -> {
                // Guess submitted -> switch to parent view to check
                selectedTab = 0
            }
            ParentGuidedStatus.PARENT_REVIEWED -> {
                // Reviewed -> show student view for celebration/retry
                selectedTab = 1
            }
            ParentGuidedStatus.IDLE -> {
                selectedTab = 0
            }
        }
    }

    Scaffold(
        containerColor = BentoBackground,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Parent-Guided Lesson",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoOnPrimaryContainer
                            )
                            Text(
                                text = if (selectedTab == 0) "Parent Dashboard • Pick & Check" else "Child Screen • Listen & Spell",
                                fontSize = 11.sp,
                                color = BentoPrimary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("parent_guided_back_btn")) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = BentoOnPrimaryContainer
                            )
                        }
                    },
                    actions = {
                        // Quick switch role button
                        IconButton(
                            onClick = { selectedTab = if (selectedTab == 0) 1 else 0 },
                            modifier = Modifier.testTag("parent_guided_flip_role_btn")
                        ) {
                            Icon(
                                Icons.Default.SwapHoriz,
                                contentDescription = "Switch View",
                                tint = BentoPrimary
                            )
                        }

                        IconButton(
                            onClick = { onResetSession() },
                            modifier = Modifier.testTag("parent_guided_reset_btn")
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Reset Session",
                                tint = BentoOnPrimaryContainer
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BentoPrimaryContainer,
                        titleContentColor = BentoOnPrimaryContainer
                    )
                )

                // Tab Switcher between Parent Dashboard and Student Screen
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = BentoSurface,
                    contentColor = BentoPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = BentoPrimary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("🏡 Parent Dashboard", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium)
                                if (sessionState.status == ParentGuidedStatus.STUDENT_SUBMITTED) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(BentoPrimary)
                                    )
                                }
                            }
                        },
                        modifier = Modifier.testTag("tab_parent_dashboard")
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("🧒 Child Screen", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium)
                                if (sessionState.status == ParentGuidedStatus.SENT_TO_STUDENT) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(BentoPrimary)
                                    )
                                }
                            }
                        },
                        modifier = Modifier.testTag("tab_student_screen")
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Crossfade(targetState = selectedTab, label = "role_crossfade") { tabIndex ->
                when (tabIndex) {
                    0 -> {
                        ParentDashboardView(
                            activeList = activeList,
                            activeWords = activeWords,
                            sessionState = sessionState,
                            ttsHelper = ttsHelper,
                            onSendWordToStudent = onSendWordToStudent,
                            onReviewStudentGuess = onReviewStudentGuess,
                            onRequestRetry = onRequestRetry,
                            onResetToPickWord = onResetToPickWord,
                            onSwitchToStudentView = { selectedTab = 1 },
                            onOpenParentZone = onOpenParentZone
                        )
                    }
                    1 -> {
                        StudentGuidedView(
                            sessionState = sessionState,
                            studentName = studentName,
                            ttsHelper = ttsHelper,
                            onSubmitGuess = onSubmitStudentGuess,
                            onRetryCurrentWord = {
                                onSendWordToStudent(
                                    sessionState.activeWord ?: return@StudentGuidedView,
                                    sessionState.parentNote,
                                    sessionState.providePhonicsHint,
                                    sessionState.provideSentenceClue
                                )
                            },
                            onReadyForNextWord = {
                                onResetToPickWord()
                                selectedTab = 0
                            },
                            onSwitchToParentView = { selectedTab = 0 }
                        )
                    }
                }
            }
        }
    }
}
