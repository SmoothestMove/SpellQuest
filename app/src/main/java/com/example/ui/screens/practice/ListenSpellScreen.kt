package com.example.ui.screens.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
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
import com.example.ui.components.DiagnosticCard
import com.example.ui.components.LetterTile
import com.example.ui.components.MasteryProgressBar
import com.example.ui.components.TileStatus
import com.example.ui.components.TtsHelper
import com.example.util.SpellingDiagnostics
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.AmberSecondaryText
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseTertiary
import com.example.ui.theme.RoseTertiaryText
import com.example.ui.theme.SkyAccent
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ListenSpellScreen(
    words: List<SpellingWord>,
    ttsHelper: TtsHelper,
    onRecordAttempt: (SpellingWord, Boolean, String, Boolean) -> Unit,
    onNavigateBack: () -> Unit
) {
    if (words.isEmpty()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Listen & Spell", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("listen_spell_back_btn")) {
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
                        Text("🎧", fontSize = 44.sp)
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

    var userLetters = remember { mutableStateListOf<Char>() }
    var availablePool = remember { mutableStateListOf<Char>() }
    var isSubmitted by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }
    var hasTriggeredConfetti by remember { mutableStateOf(false) }
    var useTileMode by remember { mutableStateOf(true) }
    var textInput by remember { mutableStateOf("") }

    fun setupWord() {
        userLetters.clear()
        availablePool.clear()
        isSubmitted = false
        isCorrect = false
        showHint = false
        textInput = ""

        // Pool contains all word letters + 3 random extra distractors
        val wordChars = currentWord.cleanWord.toList()
        val alphabet = ('a'..'z').toList()
        val distractors = alphabet.filter { it !in wordChars }.shuffled().take(3)
        val pool = (wordChars + distractors).shuffled(Random(System.currentTimeMillis()))
        availablePool.addAll(pool)

        ttsHelper.speakWord(currentWord.word)
    }

    LaunchedEffect(currentIndex) {
        setupWord()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Listen & Spell", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Word ${currentIndex + 1} of ${words.size}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("listen_spell_back")) {
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Word Mastery Progress
                MasteryProgressBar(
                    boxLevel = currentWord.boxLevel,
                    isMastered = currentWord.isMastered,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Big Audio Speaker Listening Station
                Card(
                    shape = RoundedCornerShape(24.dp),
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
                        Text(
                            text = "🎧 TAP TO HEAR THE WORD",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Large Speaker Bubble
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(88.dp)
                                .shadow(8.dp, CircleShape)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(IndigoPrimary, IndigoPrimary.copy(alpha = 0.8f))
                                    )
                                )
                                .clickable { ttsHelper.speakWord(currentWord.word) }
                                .testTag("listen_speaker_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Hear Word",
                                tint = Color.White,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Context sentence button
                        Button(
                            onClick = { ttsHelper.speakSentence(currentWord.displaySentence) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("hear_sentence_btn")
                        ) {
                            Icon(Icons.Default.Hearing, contentDescription = "Sentence", tint = BentoTextPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Hear in Sentence", color = BentoTextPrimary, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "\"${currentWord.sentenceWithBlank}\"",
                            fontSize = 13.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = BentoTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input Section (Letter Slots)
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SPELL THE WORD",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberSecondaryText,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Slot Tiles for Letters
                        val targetLen = currentWord.cleanWord.length
                        FlowRow(
                            horizontalArrangement = Arrangement.Center,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (i in 0 until targetLen) {
                                val charInSlot = userLetters.getOrNull(i)
                                val status = when {
                                    !isSubmitted && charInSlot != null -> TileStatus.SELECTED
                                    !isSubmitted && charInSlot == null -> TileStatus.BLANK
                                    isSubmitted && isCorrect -> TileStatus.CORRECT
                                    isSubmitted && !isCorrect -> TileStatus.INCORRECT
                                    else -> TileStatus.DEFAULT
                                }

                                LetterTile(
                                    letter = charInSlot ?: ' ',
                                    status = status,
                                    size = 46.dp,
                                    onClick = if (!isSubmitted && charInSlot != null) {
                                        {
                                            val removed = userLetters.removeAt(i)
                                            availablePool.add(removed)
                                        }
                                    } else null,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (!isSubmitted) {
                            // Pool of interactive letter buttons
                            Text(
                                text = "Tap letters to build word:",
                                fontSize = 12.sp,
                                color = BentoTextSecondary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                availablePool.forEachIndexed { index, char ->
                                    LetterTile(
                                        letter = char,
                                        status = TileStatus.DEFAULT,
                                        size = 44.dp,
                                        onClick = {
                                            if (userLetters.size < currentWord.cleanWord.length) {
                                                userLetters.add(char)
                                                availablePool.removeAt(index)
                                            }
                                        },
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Backspace / Clear button
                            Row(horizontalArrangement = Arrangement.Center) {
                                if (userLetters.isNotEmpty()) {
                                    TextButton(
                                        onClick = {
                                            val last = userLetters.removeAt(userLetters.lastIndex)
                                            availablePool.add(last)
                                        },
                                        modifier = Modifier.testTag("listen_backspace_btn")
                                    ) {
                                        Icon(Icons.Default.Backspace, contentDescription = "Backspace", tint = RoseTertiaryText)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Undo Letter", color = RoseTertiaryText)
                                    }
                                }

                                if (currentWord.hint.isNotBlank()) {
                                    TextButton(
                                        onClick = { showHint = !showHint },
                                        modifier = Modifier.testTag("listen_hint_btn")
                                    ) {
                                        Icon(Icons.Default.Lightbulb, contentDescription = "Hint", tint = AmberSecondaryText)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (showHint) "Hide Hint" else "Hint", color = AmberSecondaryText)
                                    }
                                }
                            }

                            if (showHint) {
                                Text(
                                    text = "💡 ${currentWord.hint}",
                                    fontSize = 12.sp,
                                    color = AmberSecondaryText,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    val spelled = userLetters.joinToString("").lowercase()
                                    isCorrect = spelled == currentWord.cleanWord
                                    isSubmitted = true
                                    if (isCorrect) {
                                        hasTriggeredConfetti = true
                                        ttsHelper.speakEncouragement()
                                    }
                                    onRecordAttempt(currentWord, isCorrect, "listen", showHint)
                                },
                                enabled = userLetters.size == currentWord.cleanWord.length,
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("listen_check_btn")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Submit")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Check Answer", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Result Banner
                            Text(
                                text = if (isCorrect) "🎉 Correct! Super Listener!" else "❌ The correct spelling was:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) EmeraldSuccess else MaterialTheme.colorScheme.error
                            )

                            if (!isCorrect) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = currentWord.word.uppercase(),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = IndigoPrimary,
                                    letterSpacing = 2.sp
                                )
                                Text(
                                    text = "Phonics: ${currentWord.displayPhonics}",
                                    fontSize = 13.sp,
                                    color = AmberSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Science of Reading Diagnostic Analysis
                            val analysis = SpellingDiagnostics.analyze(
                                target = currentWord.cleanWord,
                                attempt = userLetters.joinToString("").lowercase(),
                                phonicsDisplay = currentWord.displayPhonics
                            )
                            DiagnosticCard(
                                analysis = analysis,
                                modifier = Modifier.testTag("listen_diagnostic_card")
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = { setupWord() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("listen_retry_btn")
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Try Again", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Button(
                                    onClick = {
                                        if (currentIndex < words.size - 1) {
                                            currentIndex++
                                        } else {
                                            onNavigateBack()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .testTag("listen_next_btn")
                                ) {
                                    Text(if (currentIndex < words.size - 1) "Next Word" else "Finish Practice")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
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
