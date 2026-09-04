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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ParentGuidedState
import com.example.data.model.ParentGuidedStatus
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.LetterTile
import com.example.ui.components.TileStatus
import com.example.ui.components.TtsHelper
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
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseTertiary
import com.example.ui.theme.SkyAccent

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudentGuidedView(
    sessionState: ParentGuidedState,
    studentName: String,
    ttsHelper: TtsHelper,
    onSubmitGuess: (String) -> Unit,
    onRetryCurrentWord: () -> Unit,
    onReadyForNextWord: () -> Unit,
    onSwitchToParentView: () -> Unit
) {
    var studentInput by remember(sessionState.activeWord, sessionState.status) {
        mutableStateOf(if (sessionState.status == ParentGuidedStatus.STUDENT_SUBMITTED) sessionState.studentGuess else "")
    }

    // Auto-play TTS when new word is sent
    LaunchedEffect(sessionState.activeWord, sessionState.status) {
        if (sessionState.status == ParentGuidedStatus.SENT_TO_STUDENT && sessionState.activeWord != null) {
            ttsHelper.speakWord(sessionState.activeWord.word)
        }
    }

    val keyboardRows = listOf(
        listOf('Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P'),
        listOf('A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L'),
        listOf('Z', 'X', 'C', 'V', 'B', 'N', 'M')
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBackground)
    ) {
        // Confetti celebration if parent approved!
        if (sessionState.status == ParentGuidedStatus.PARENT_REVIEWED && sessionState.lastFeedback?.isApproved == true) {
            ConfettiEffect(trigger = true, modifier = Modifier.fillMaxSize())
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Student Header Tile
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
                            text = "CHILD PRACTICE SCREEN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoPrimary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Hello, $studentName! 🎧",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoOnPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Listen to your parent's challenge, spell the word, and send your guess back!",
                            fontSize = 12.sp,
                            color = BentoOnPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.7f))
                    ) {
                        Text("🧒", fontSize = 24.sp)
                    }
                }
            }

            // 2. Interactive States
            when (sessionState.status) {
                ParentGuidedStatus.IDLE -> {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface),
                        border = BorderStroke(1.dp, BentoBorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🦉", fontSize = 56.sp)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Waiting for Your Parent",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Your parent is choosing a spelling word on the Parent Dashboard. As soon as they send it, you will hear the audio!",
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                color = BentoTextSecondary,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onSwitchToParentView,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                                modifier = Modifier.testTag("student_switch_to_parent_btn")
                            ) {
                                Text("Go to Parent Dashboard 🏡")
                            }
                        }
                    }
                }

                ParentGuidedStatus.SENT_TO_STUDENT -> {
                    val word = sessionState.activeWord
                    if (word != null) {
                        // Parent Note Card if present
                        if (sessionState.parentNote.isNotBlank()) {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = BentoStreakContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("💬", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "PARENT TIP:",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoOnStreak
                                        )
                                        Text(
                                            text = sessionState.parentNote,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BentoOnStreak
                                        )
                                    }
                                }
                            }
                        }

                        // Big Audio Dispatch & Listening Card
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = BentoSurface),
                            border = BorderStroke(1.5.dp, BentoPrimary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "AUDIO SPELLING CHALLENGE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoPrimary,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Main Big Speaker Button
                                Button(
                                    onClick = { ttsHelper.speakWord(word.word) },
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                                    modifier = Modifier
                                        .size(80.dp)
                                        .testTag("student_listen_word_btn")
                                ) {
                                    Icon(
                                        Icons.Default.VolumeUp,
                                        contentDescription = "Listen to Word",
                                        modifier = Modifier.size(40.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap to Listen to Word",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoPrimary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Slow & Sentence Audio Buttons
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = { ttsHelper.speakSlowWord(word.word) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SkyAccent)
                                    ) {
                                        Icon(Icons.Default.Hearing, contentDescription = "Slow", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Hear Slower (Phonics)", fontSize = 11.sp)
                                    }

                                    if (sessionState.provideSentenceClue && word.sentence.isNotBlank()) {
                                        OutlinedButton(
                                            onClick = { ttsHelper.speakSentence(word.sentence) },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseTertiary)
                                        ) {
                                            Text("Sentence 📖", fontSize = 11.sp)
                                        }
                                    }
                                }

                                if (sessionState.providePhonicsHint && word.phonics.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Phonics Clue: ${word.phonics}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BentoTextSecondary
                                    )
                                }
                            }
                        }

                        // Student Spelling Input Display (Letter Boxes)
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = BentoSurface),
                            border = BorderStroke(1.dp, BentoBorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "YOUR SPELLING GUESS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextSecondary,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Display letter tiles
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                ) {
                                    if (studentInput.isEmpty()) {
                                        // Empty placeholder slots matching word length
                                        val expectedLength = word.word.length
                                        for (i in 0 until expectedLength) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(BentoSurfaceVariant)
                                                    .border(1.5.dp, BentoBorderSubtle, RoundedCornerShape(10.dp))
                                            ) {
                                                Text("_", color = BentoTextSecondary.copy(alpha = 0.5f), fontSize = 18.sp)
                                            }
                                        }
                                    } else {
                                        studentInput.forEach { char ->
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(BentoPrimaryContainer)
                                                    .border(2.dp, BentoPrimary, RoundedCornerShape(10.dp))
                                            ) {
                                                Text(
                                                    text = char.uppercaseChar().toString(),
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BentoOnPrimaryContainer
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // On-screen keyboard for kid typing
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    keyboardRows.forEach { row ->
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            row.forEach { char ->
                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier
                                                        .size(width = 30.dp, height = 40.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(BentoSurfaceVariant)
                                                        .clickable {
                                                            if (studentInput.length < 15) {
                                                                studentInput += char
                                                            }
                                                        }
                                                        .testTag("key_$char")
                                                ) {
                                                    Text(
                                                        text = char.toString(),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = BentoTextPrimary
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Backspace & Clear row
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                if (studentInput.isNotEmpty()) {
                                                    studentInput = studentInput.dropLast(1)
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = BentoSurfaceVariant, contentColor = BentoTextPrimary),
                                            modifier = Modifier.testTag("student_backspace_btn")
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Delete", fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = { studentInput = "" },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = BentoSurfaceVariant, contentColor = BentoTextPrimary),
                                            modifier = Modifier.testTag("student_clear_btn")
                                        ) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Clear", fontSize = 12.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // Big CTA: Send Guess to Parent!
                                Button(
                                    onClick = {
                                        if (studentInput.isNotBlank()) {
                                            onSubmitGuess(studentInput)
                                        }
                                    },
                                    enabled = studentInput.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("student_send_guess_to_parent_btn")
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Send Guess to Parent! 📨",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                ParentGuidedStatus.STUDENT_SUBMITTED -> {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoBadgeContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📬", fontSize = 56.sp)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Guess Sent to Parent!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoOnBadge
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Your guess \"${sessionState.studentGuess.uppercase()}\" was delivered to the Parent Dashboard. Waiting for your parent to check and review!",
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                color = BentoOnBadge.copy(alpha = 0.85f),
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onSwitchToParentView,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                                modifier = Modifier.testTag("student_switch_to_parent_check_btn")
                            ) {
                                Text("Pass to Parent for Check 🏡")
                            }
                        }
                    }
                }

                ParentGuidedStatus.PARENT_REVIEWED -> {
                    val feedback = sessionState.lastFeedback
                    val isApproved = feedback?.isApproved == true

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isApproved) Color(0xFFF0FDF4) else Color(0xFFFFFBEB)
                        ),
                        border = BorderStroke(2.dp, if (isApproved) EmeraldSuccess else AmberSecondary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(feedback?.sticker ?: if (isApproved) "🌟" else "💪", fontSize = 56.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isApproved) "🎉 APPROVED BY PARENT!" else "💪 PARENT SAYS: TRY AGAIN!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isApproved) EmeraldSuccess else AmberSecondary
                            )

                            if (isApproved) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "+20 Star XP Earned! ⭐",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoPrimary
                                )
                            }

                            if (!feedback?.feedbackNote.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.White.copy(alpha = 0.8f))
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Parent's Note:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoTextSecondary
                                        )
                                        Text(
                                            text = feedback?.feedbackNote ?: "",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BentoTextPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            if (isApproved) {
                                Button(
                                    onClick = onReadyForNextWord,
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("student_ready_next_word_btn")
                                ) {
                                    Text("Ready for Next Word! ➡️", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            } else {
                                Button(
                                    onClick = onRetryCurrentWord,
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("student_try_again_btn")
                                ) {
                                    Text("Try Again 🔄", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
