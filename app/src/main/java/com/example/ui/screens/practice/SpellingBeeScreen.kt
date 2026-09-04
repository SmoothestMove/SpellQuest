package com.example.ui.screens.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SpellingWord
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.TtsHelper
import com.example.ui.components.highContrastInputTextStyle
import com.example.ui.components.highContrastTextFieldColors
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.CoralError
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.EmeraldSuccessText
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseTertiary
import com.example.ui.theme.SkyAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellingBeeScreen(
    words: List<SpellingWord>,
    ttsHelper: TtsHelper,
    onQuizCompleted: (Int, Int) -> Unit,
    onRecordAttempt: ((SpellingWord, Boolean, String, Boolean) -> Unit)? = null,
    onNavigateBack: () -> Unit
) {
    if (words.isEmpty()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Weekly Spelling Bee", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("spelling_bee_back_btn")) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🐝", fontSize = 44.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No Spelling Words Found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            color = IndigoPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Add your child's weekly spelling words in the Parent Zone to start the Spelling Bee!",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onNavigateBack,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                        ) {
                            Text("Back to Home")
                        }
                    }
                }
            }
        }
        return
    }

    val quizWords = remember { words.shuffled().take(10) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }
    var isQuizOver by remember { mutableStateOf(false) }

    val currentWord = quizWords.getOrNull(currentIndex)
    var userInput by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    var isCurrentCorrect by remember { mutableStateOf(false) }
    var hasTriggeredConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(currentIndex, isQuizOver) {
        if (!isQuizOver && currentWord != null) {
            ttsHelper.speakWord(currentWord.word)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Weekly Spelling Bee 🐝", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        // Lives Indicator ❤️❤️❤️
                        Row {
                            for (i in 1..3) {
                                Icon(
                                    imageVector = if (i <= lives) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Life",
                                    tint = if (i <= lives) CoralError else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("bee_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!isQuizOver && currentWord != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header progress and score
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question ${currentIndex + 1} of ${quizWords.size}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Score: $score pts",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AmberSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "👑 SPELL THE WORD SPOKEN ALOUD",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Large Golden Speaker
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(80.dp)
                                    .shadow(6.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(AmberSecondary, Color(0xFFD97706))
                                        )
                                    )
                                    .clickable { ttsHelper.speakWord(currentWord.word) }
                                    .testTag("bee_speaker_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak Word",
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { ttsHelper.speakSentence(currentWord.displaySentence) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("bee_sentence_btn")
                            ) {
                                Icon(Icons.Default.Hearing, contentDescription = "Sentence", tint = BentoTextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Hear in Context Sentence", color = BentoTextPrimary, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = userInput,
                                onValueChange = { userInput = it },
                                textStyle = highContrastInputTextStyle,
                                placeholder = { Text("Type word here...", color = BentoTextSecondary) },
                                singleLine = true,
                                enabled = !isSubmitted,
                                colors = highContrastTextFieldColors(),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bee_input_field")
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            if (!isSubmitted) {
                                Button(
                                    onClick = {
                                        val correct = userInput.trim().equals(currentWord.cleanWord, ignoreCase = true)
                                        isCurrentCorrect = correct
                                        isSubmitted = true
                                        onRecordAttempt?.invoke(currentWord, correct, "spelling_bee", false)
                                        if (correct) {
                                            score += 100
                                            correctCount++
                                            hasTriggeredConfetti = true
                                            ttsHelper.speakEncouragement()
                                        } else {
                                            lives--
                                        }
                                    },
                                    enabled = userInput.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("bee_submit_btn")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Submit")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Submit Spelling", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                // Result
                                Text(
                                    text = if (isCurrentCorrect) "🎉 Correct! +100 Points!" else "❌ Oops! The correct word was: ${currentWord.word.uppercase()}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrentCorrect) EmeraldSuccessText else CoralError
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        userInput = ""
                                        isSubmitted = false
                                        if (lives <= 0 || currentIndex >= quizWords.size - 1) {
                                            isQuizOver = true
                                            onQuizCompleted(quizWords.size, correctCount)
                                        } else {
                                            currentIndex++
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("bee_next_btn")
                                ) {
                                    Text(if (lives <= 0 || currentIndex >= quizWords.size - 1) "See Results" else "Next Word")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            } else {
                // Quiz Over / Results Screen
                val percentage = if (quizWords.isNotEmpty()) (correctCount * 100) / quizWords.size else 0
                val stars = when {
                    percentage == 100 -> 3
                    percentage >= 70 -> 2
                    percentage >= 50 -> 1
                    else -> 0
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (percentage >= 80) "🏆 SPELLING BEE CHAMPION! 🐝" else "🌟 GREAT EFFORT!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AmberSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stars
                    Row(horizontalArrangement = Arrangement.Center) {
                        for (i in 1..3) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star",
                                tint = if (i <= stars) AmberSecondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Final Score", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "$score Points", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = IndigoPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "$correctCount out of ${quizWords.size} words spelled correctly ($percentage%)", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "+${correctCount * 25 + if (percentage == 100) 100 else 0} Star XP Added!", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("bee_finish_btn")
                    ) {
                        Text("Return to Hub", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(36.dp))
                }
            }

            ConfettiEffect(
                trigger = hasTriggeredConfetti,
                onComplete = { hasTriggeredConfetti = false }
            )
        }
    }
}
