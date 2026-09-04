package com.example.ui.screens.parentguided

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ForwardToInbox
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ParentGuidedState
import com.example.data.model.ParentGuidedStatus
import com.example.data.model.SpellingWord
import com.example.data.model.WeeklyList
import com.example.ui.components.DiagnosticCard
import com.example.ui.components.TtsHelper
import com.example.util.SpellingDiagnostics
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBadgeContainer
import com.example.ui.theme.BentoBorderSubtle
import com.example.ui.theme.BentoOnBadge
import com.example.ui.theme.BentoOnPrimaryContainer
import com.example.ui.theme.BentoOnStreak
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoStreakContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.CoralError
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseTertiary
import com.example.ui.theme.SkyAccent

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ParentDashboardView(
    activeList: WeeklyList?,
    activeWords: List<SpellingWord>,
    sessionState: ParentGuidedState,
    ttsHelper: TtsHelper,
    onSendWordToStudent: (SpellingWord, String, Boolean, Boolean) -> Unit,
    onReviewStudentGuess: (isApproved: Boolean, feedbackNote: String, sticker: String, markAsMastered: Boolean) -> Unit,
    onRequestRetry: (String) -> Unit,
    onResetToPickWord: () -> Unit,
    onSwitchToStudentView: () -> Unit,
    onOpenParentZone: () -> Unit
) {
    var selectedWord by remember(sessionState.activeWord) {
        mutableStateOf(sessionState.activeWord ?: activeWords.firstOrNull { !it.isMastered } ?: activeWords.firstOrNull())
    }
    var customParentNote by remember { mutableStateOf("") }
    var providePhonicsHint by remember { mutableStateOf(true) }
    var provideSentenceClue by remember { mutableStateOf(true) }
    var selectedSticker by remember { mutableStateOf("🌟") }
    var coachingFeedbackText by remember { mutableStateOf("") }

    val quickStickers = listOf("🌟 Superstar", "🎯 Spot On!", "👏 Great Phonics!", "💡 Good Effort!", "🚀 Flying High!")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBackground)
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Bento Header: Parent Control Hub Title & Session Counters
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoPrimaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PARENT-GUIDED SESSION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoPrimary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Parent Control Dashboard",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoOnPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pick words to challenge the student, send audio speech, and review their attempts live.",
                            fontSize = 12.sp,
                            color = BentoOnPrimaryContainer.copy(alpha = 0.85f),
                            lineHeight = 16.sp
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.7f))
                    ) {
                        Text("🏡", fontSize = 26.sp)
                    }
                }
            }
        }

        // 2. Real-Time Status Card
        item {
            when (sessionState.status) {
                ParentGuidedStatus.IDLE -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface),
                        border = BorderStroke(1.dp, BentoBorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("👉", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Ready to Pick a Word",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = BentoTextPrimary
                                )
                                Text(
                                    text = "Choose a word below from '${activeList?.title ?: "Weekly List"}' to test the student.",
                                    fontSize = 12.sp,
                                    color = BentoTextSecondary
                                )
                            }
                        }
                    }
                }

                ParentGuidedStatus.SENT_TO_STUDENT -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoStreakContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.HourglassTop,
                                        contentDescription = "Waiting",
                                        tint = BentoOnStreak,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Audio Sent! Waiting for Your Child...",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = BentoOnStreak
                                    )
                                }
                                Text("🎧", fontSize = 20.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "The word '${sessionState.activeWord?.word?.uppercase()}' was dispatched to your child's screen. Your child is now listening to the audio and typing their spelling guess.",
                                fontSize = 13.sp,
                                color = BentoOnStreak.copy(alpha = 0.85f),
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onSwitchToStudentView,
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("parent_switch_to_student_btn")
                                ) {
                                    Text("Switch to Child Screen 🧒")
                                }
                                OutlinedButton(
                                    onClick = {
                                        sessionState.activeWord?.let { ttsHelper.speakWord(it.word) }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoOnStreak)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Replay")
                                }
                            }
                        }
                    }
                }

                ParentGuidedStatus.STUDENT_SUBMITTED -> {
                    val targetWord = sessionState.activeWord?.word.orEmpty()
                    val guess = sessionState.studentGuess
                    val isExactMatch = guess.equals(targetWord, ignoreCase = true)

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExactMatch) Color(0xFFF0FDF4) else Color(0xFFFFFBEB)
                        ),
                        border = BorderStroke(
                            2.dp,
                            if (isExactMatch) EmeraldSuccess else AmberSecondary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_review_submission_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📬 CHILD'S SUBMISSION RECEIVED!",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = if (isExactMatch) EmeraldSuccess else AmberSecondary,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = if (isExactMatch) "🎉 Exact Match" else "🔍 Check Letters",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Comparison Display
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // Target Word
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "TARGET WORD",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = targetWord.uppercase(),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = BentoPrimary
                                    )
                                    Text(
                                        text = sessionState.activeWord?.phonics ?: "",
                                        fontSize = 11.sp,
                                        color = BentoTextSecondary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(50.dp)
                                        .background(BentoBorderSubtle)
                                )

                                // Child's Guess
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "CHILD'S GUESS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (guess.isBlank()) "(Empty)" else guess.uppercase(),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isExactMatch) EmeraldSuccess else CoralError
                                    )
                                    Text(
                                        text = if (isExactMatch) "Matches spelling!" else "Differs from target",
                                        fontSize = 11.sp,
                                        color = if (isExactMatch) EmeraldSuccess else CoralError
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Letter Diff Breakdown
                            Text(
                                text = "Letter-by-Letter Analysis:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val maxLen = maxOf(targetWord.length, guess.length)
                                for (i in 0 until maxLen) {
                                    val tChar = targetWord.getOrNull(i)?.uppercaseChar()
                                    val gChar = guess.getOrNull(i)?.uppercaseChar()
                                    val isCharMatch = tChar != null && gChar != null && tChar == gChar

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isCharMatch) EmeraldSuccess.copy(alpha = 0.15f)
                                                else CoralError.copy(alpha = 0.15f)
                                            )
                                            .border(
                                                1.5.dp,
                                                if (isCharMatch) EmeraldSuccess else CoralError,
                                                RoundedCornerShape(8.dp)
                                            )
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = (gChar ?: '·').toString(),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCharMatch) EmeraldSuccess else CoralError
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Science of Reading Diagnostic Analysis
                            val diagnosticAnalysis = SpellingDiagnostics.analyze(
                                target = targetWord,
                                attempt = guess,
                                phonicsDisplay = sessionState.activeWord?.displayPhonics ?: ""
                            )
                            DiagnosticCard(
                                analysis = diagnosticAnalysis,
                                modifier = Modifier.testTag("parent_diagnostic_card")
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Parent Quick Feedback / Stickers
                            Text(
                                text = "Attach Parent Sticker / Encouragement:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                quickStickers.forEach { stickerText ->
                                    val isSelected = selectedSticker == stickerText.take(2)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedSticker = stickerText.take(2)
                                            coachingFeedbackText = stickerText.substring(2).trim()
                                        },
                                        label = { Text(stickerText, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BentoPrimaryContainer,
                                            selectedLabelColor = BentoOnPrimaryContainer
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = coachingFeedbackText,
                                onValueChange = { coachingFeedbackText = it },
                                label = { Text("Parent Note to Child (Optional)") },
                                placeholder = { Text("e.g. Great job! Remember the vowel 'ea' sound.") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        onReviewStudentGuess(
                                            true,
                                            coachingFeedbackText.ifBlank { "Great spelling effort!" },
                                            selectedSticker,
                                            false
                                        )
                                        ttsHelper.speakEncouragement()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("parent_approve_btn")
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Approve", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Approve & Award XP", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val hintMsg = coachingFeedbackText.ifBlank { "Look closely at the vowel sounds!" }
                                        onRequestRetry(hintMsg)
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoPrimary),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("parent_retry_request_btn")
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Retry", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ask for Retry", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                ParentGuidedStatus.PARENT_REVIEWED -> {
                    val feedback = sessionState.lastFeedback
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoBadgeContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(feedback?.sticker ?: "⭐", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (feedback?.isApproved == true) "Word Checked & Approved!" else "Feedback Sent to Child",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = BentoOnBadge
                                    )
                                }
                                Text("✅", fontSize = 18.sp)
                            }

                            if (!feedback?.feedbackNote.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Your note: \"${feedback?.feedbackNote}\"",
                                    fontSize = 13.sp,
                                    color = BentoOnBadge.copy(alpha = 0.85f)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onResetToPickWord,
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("parent_pick_next_word_btn")
                                ) {
                                    Text("Pick Next Word 🎯")
                                }
                                OutlinedButton(
                                    onClick = onSwitchToStudentView,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoPrimary)
                                ) {
                                    Text("View Child Screen")
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Word Selector & Audio Dispatch Center
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = BorderStroke(1.dp, BentoBorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SELECT WORD FROM LIST",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoPrimary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = activeList?.title ?: "No list active",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                        }

                        if (activeWords.isNotEmpty()) {
                            Text(
                                text = "${activeWords.size} words",
                                fontSize = 12.sp,
                                color = BentoTextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (activeWords.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📝", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No words in active weekly list",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = BentoTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Paste your child's weekly spelling words in the Parent Zone to practice together.",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                color = BentoTextSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onOpenParentZone,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary)
                            ) {
                                Text("Open Parent Zone")
                            }
                        }
                    } else {
                        // Word Chips FlowRow
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            activeWords.forEach { wordItem ->
                                val isSelected = selectedWord?.id == wordItem.id
                                val isWordMastered = wordItem.isMastered || wordItem.boxLevel >= 3

                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedWord = wordItem },
                                    label = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (isWordMastered) {
                                                Text("⭐", fontSize = 10.sp)
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = wordItem.word.replaceFirstChar { it.uppercase() },
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BentoPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = if (isWordMastered) BentoSurfaceVariant else BentoSurface
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = if (isSelected) BentoPrimary else BentoBorderSubtle
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = BentoBorderSubtle)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Selected Word Inspection Card
                        selectedWord?.let { word ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(BentoSurfaceVariant)
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Selected Word:",
                                            fontSize = 11.sp,
                                            color = BentoTextSecondary
                                        )
                                        Text(
                                            text = word.word.uppercase(),
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Black,
                                            color = BentoPrimary
                                        )
                                    }

                                    // TTS Preview Buttons
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = { ttsHelper.speakWord(word.word) },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.VolumeUp,
                                                contentDescription = "Hear Normal",
                                                tint = BentoPrimary
                                            )
                                        }
                                        IconButton(
                                            onClick = { ttsHelper.speakSlowWord(word.word) },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Hearing,
                                                contentDescription = "Hear Slow",
                                                tint = SkyAccent
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Phonics: ${word.phonics}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BentoTextPrimary
                                    )
                                    Text("•", color = BentoTextSecondary)
                                    Text(
                                        text = "${word.word.length} letters",
                                        fontSize = 12.sp,
                                        color = BentoTextSecondary
                                    )
                                }

                                if (word.sentence.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "\"${word.sentence}\"",
                                        fontSize = 12.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        color = BentoTextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Parent Clue Customization
                            OutlinedTextField(
                                value = customParentNote,
                                onValueChange = { customParentNote = it },
                                label = { Text("Parent Tip or Note (Sent with word)") },
                                placeholder = { Text("e.g. Listen for the initial blend 'bl'") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Hint Toggles
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Provide Phonics Clue to Child", fontSize = 13.sp, color = BentoTextPrimary)
                                Switch(
                                    checked = providePhonicsHint,
                                    onCheckedChange = { providePhonicsHint = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = BentoPrimary)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Provide Example Sentence Audio", fontSize = 13.sp, color = BentoTextPrimary)
                                Switch(
                                    checked = provideSentenceClue,
                                    onCheckedChange = { provideSentenceClue = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = BentoPrimary)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Primary Big CTA: Send Audio to Student
                            Button(
                                onClick = {
                                    onSendWordToStudent(
                                        word,
                                        customParentNote,
                                        providePhonicsHint,
                                        provideSentenceClue
                                    )
                                    ttsHelper.speakWord(word.word)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("parent_send_audio_to_student_btn")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Send Audio & Word to Child",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Session History Log
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = BorderStroke(1.dp, BentoBorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SESSION ACTIVITY LOG",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${sessionState.totalApproved} / ${sessionState.totalWordsTested} Approved",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (sessionState.totalWordsTested > 0) EmeraldSuccess else BentoTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (sessionState.sessionHistory.isEmpty()) {
                        Text(
                            text = "No words checked yet in this session. Start by selecting and sending a word above!",
                            fontSize = 12.sp,
                            color = BentoTextSecondary,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            sessionState.sessionHistory.forEach { logItem ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (logItem.isCorrect) Color(0xFFF0FDF4) else Color(0xFFFEF2F2))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(logItem.sticker, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = logItem.word.uppercase(),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = BentoTextPrimary
                                            )
                                            Text(
                                                text = "Guess: \"${logItem.studentGuess}\"",
                                                fontSize = 12.sp,
                                                color = if (logItem.isCorrect) EmeraldSuccess else CoralError
                                            )
                                        }
                                    }

                                    Text(
                                        text = if (logItem.isCorrect) "Approved ✅" else "Reviewed 📝",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (logItem.isCorrect) EmeraldSuccess else BentoTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
