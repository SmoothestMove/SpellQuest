package com.example.ui.screens.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.DiagnosticCard
import com.example.ui.components.LetterTile
import com.example.ui.components.MasteryProgressBar
import com.example.ui.components.TileStatus
import com.example.ui.components.TtsHelper
import com.example.util.SpellingDiagnostics
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SkyAccent

enum class LscwcStep {
    LOOK_SAY,
    COVER_WRITE,
    CHECK
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardLearnScreen(
    words: List<SpellingWord>,
    ttsHelper: TtsHelper,
    onRecordAttempt: (SpellingWord, Boolean, String, Boolean) -> Unit,
    onNavigateBack: () -> Unit
) {
    if (words.isEmpty()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Look, Say, Cover, Write, Check", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("lscwc_back_btn")) {
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
                        Text("📝", fontSize = 44.sp)
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
                            "Add your child's weekly spelling words in the Parent Zone to start practicing!",
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

    var currentIndex by remember { mutableIntStateOf(0) }
    val currentWord = words.getOrElse(currentIndex) { words.first() }

    var currentStep by remember { mutableStateOf(LscwcStep.LOOK_SAY) }
    var userInput by remember { mutableStateOf("") }
    var showHint by remember { mutableStateOf(false) }
    var hasTriggeredConfetti by remember { mutableStateOf(false) }
    var xpEarnedAlert by remember { mutableIntStateOf(0) }

    // Auto-speak word when entering LOOK_SAY step
    LaunchedEffect(currentIndex, currentStep) {
        if (currentStep == LscwcStep.LOOK_SAY) {
            ttsHelper.speakWord(currentWord.word)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Look, Say, Cover, Write, Check", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Word ${currentIndex + 1} of ${words.size}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("lscwc_back_btn")) {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pedagogical Step Indicator Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StepChip(
                        number = 1,
                        label = "Look & Say",
                        isActive = currentStep == LscwcStep.LOOK_SAY,
                        isDone = currentStep != LscwcStep.LOOK_SAY,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    StepChip(
                        number = 2,
                        label = "Cover & Write",
                        isActive = currentStep == LscwcStep.COVER_WRITE,
                        isDone = currentStep == LscwcStep.CHECK,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    StepChip(
                        number = 3,
                        label = "Check",
                        isActive = currentStep == LscwcStep.CHECK,
                        isDone = false,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card Container
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
                        // Word Mastery Status bar
                        MasteryProgressBar(
                            boxLevel = currentWord.boxLevel,
                            isMastered = currentWord.isMastered,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        when (currentStep) {
                            LscwcStep.LOOK_SAY -> {
                                // Step 1: Look & Say
                                Text(
                                    text = "👀 LOOK & SAY 🗣️",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoPrimary,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Big Word Display with Syllable Break
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                                        .padding(vertical = 18.dp, horizontal = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = currentWord.word.uppercase(),
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = IndigoPrimary,
                                            letterSpacing = 2.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Phonics: ${currentWord.displayPhonics}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = AmberSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Audio Speak Buttons
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { ttsHelper.speakWord(currentWord.word) },
                                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.testTag("say_word_btn")
                                    ) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = "Hear Word")
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Hear Word")
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Button(
                                        onClick = { ttsHelper.spellOutLoud(currentWord.word) },
                                        colors = ButtonDefaults.buttonColors(containerColor = SkyAccent),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.testTag("spell_aloud_btn")
                                    ) {
                                        Text("Spell Aloud")
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Definition and Sentence
                                if (currentWord.definition.isNotBlank()) {
                                    Text(
                                        text = "Meaning: \"${currentWord.definition}\"",
                                        fontSize = 13.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                Text(
                                    text = "Sentence: \"${currentWord.displaySentence}\"",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = {
                                        userInput = ""
                                        currentStep = LscwcStep.COVER_WRITE
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("cover_and_write_btn")
                                ) {
                                    Icon(Icons.Default.VisibilityOff, contentDescription = "Cover")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Ready! Cover & Write", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            LscwcStep.COVER_WRITE -> {
                                // Step 2: Cover & Write from memory
                                Text(
                                    text = "🙈 COVERED! WRITE FROM MEMORY ✍️",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberSecondary,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(90.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF334155)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "🔒 Word is Hidden",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Quick Audio Re-play
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    IconButton(
                                        onClick = { ttsHelper.speakWord(currentWord.word) },
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                    ) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = "Hear Again", tint = IndigoPrimary)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Hear Word Again", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = userInput,
                                    onValueChange = { userInput = it },
                                    placeholder = { Text("Type the spelling here...") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("lscwc_input_field")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Hint option
                                if (currentWord.hint.isNotBlank()) {
                                    TextButton(
                                        onClick = { showHint = !showHint },
                                        modifier = Modifier.testTag("lscwc_hint_btn")
                                    ) {
                                        Icon(Icons.Default.Lightbulb, contentDescription = "Hint", tint = AmberSecondary)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (showHint) "Hide Hint" else "Need a Hint?", color = AmberSecondary)
                                    }
                                    if (showHint) {
                                        Text(
                                            text = "💡 ${currentWord.hint}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = AmberSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        val isCorrect = userInput.trim().equals(currentWord.cleanWord, ignoreCase = true)
                                        currentStep = LscwcStep.CHECK
                                        if (isCorrect) {
                                            hasTriggeredConfetti = true
                                            ttsHelper.speakEncouragement()
                                        }
                                        onRecordAttempt(currentWord, isCorrect, "lscwc", showHint)
                                    },
                                    enabled = userInput.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("lscwc_check_btn")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Check")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Check Spelling", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            LscwcStep.CHECK -> {
                                val isCorrect = userInput.trim().equals(currentWord.cleanWord, ignoreCase = true)

                                Text(
                                    text = if (isCorrect) "🎉 PERFECT MATCH! ⭐" else "💪 KEEP TRYING! ALMOST THERE",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isCorrect) EmeraldSuccess else MaterialTheme.colorScheme.error,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Side-by-side comparison
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isCorrect) EmeraldSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Target Word:",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = currentWord.word.uppercase(),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSuccess,
                                        letterSpacing = 2.sp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Your Spelling:",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = userInput.trim().uppercase(),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCorrect) EmeraldSuccess else MaterialTheme.colorScheme.error,
                                        letterSpacing = 2.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Science of Reading Diagnostic Analysis
                                val analysis = SpellingDiagnostics.analyze(
                                    target = currentWord.cleanWord,
                                    attempt = userInput,
                                    phonicsDisplay = currentWord.displayPhonics
                                )
                                DiagnosticCard(
                                    analysis = analysis,
                                    modifier = Modifier.testTag("lscwc_diagnostic_card")
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                if (isCorrect) {
                                    Text(
                                        text = "+15 Star XP Earned! 🌟 Spaced Repetition Level Promoted!",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                } else {
                                    Text(
                                        text = "Notice the tricky letters in ${currentWord.displayPhonics}. Practice makes master!",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Button(
                                        onClick = {
                                            userInput = ""
                                            showHint = false
                                            currentStep = LscwcStep.LOOK_SAY
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("lscwc_retry_btn")
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Retry", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Button(
                                        onClick = {
                                            if (currentIndex < words.size - 1) {
                                                currentIndex++
                                                userInput = ""
                                                showHint = false
                                                currentStep = LscwcStep.LOOK_SAY
                                            } else {
                                                onNavigateBack()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .weight(1.5f)
                                            .testTag("lscwc_next_btn")
                                    ) {
                                        Text(if (currentIndex < words.size - 1) "Next Word" else "Finish Session")
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }

            ConfettiEffect(
                trigger = hasTriggeredConfetti,
                onComplete = { hasTriggeredConfetti = false }
            )
        }
    }
}

@Composable
fun StepChip(
    number: Int,
    label: String,
    isActive: Boolean,
    isDone: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isActive -> IndigoPrimary
        isDone -> EmeraldSuccess
        else -> Color.Transparent
    }

    val textColor = when {
        isActive || isDone -> Color.White
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(vertical = 6.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$number. $label",
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}
