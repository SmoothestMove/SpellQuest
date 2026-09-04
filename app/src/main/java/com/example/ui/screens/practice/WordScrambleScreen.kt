package com.example.ui.screens.practice

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.RoseTertiary

data class ScrambleLetterItem(
    val id: Int,
    val char: Char
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WordScrambleScreen(
    words: List<SpellingWord>,
    ttsHelper: TtsHelper,
    onRecordAttempt: (SpellingWord, Boolean, String, Boolean) -> Unit,
    onNavigateBack: () -> Unit
) {
    if (words.isEmpty()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Scramble Quest", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("scramble_back_btn")) {
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
                        Text("🔤", fontSize = 44.sp)
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

    val scrambledPool = remember { mutableStateListOf<ScrambleLetterItem>() }
    val placedLetters = remember { mutableStateListOf<ScrambleLetterItem>() }
    var isSubmitted by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }
    var hasTriggeredConfetti by remember { mutableStateOf(false) }

    fun setupScramble() {
        scrambledPool.clear()
        placedLetters.clear()
        isSubmitted = false
        isCorrect = false
        showHint = false

        val chars = currentWord.cleanWord.toList()
        var shuffled = chars.shuffled()
        // Ensure it's not accidentally identical to the solution
        var attempts = 0
        while (shuffled == chars && chars.size > 2 && attempts < 5) {
            shuffled = chars.shuffled()
            attempts++
        }

        shuffled.forEachIndexed { idx, c ->
            scrambledPool.add(ScrambleLetterItem(id = idx, char = c))
        }
        ttsHelper.speakWord(currentWord.word)
    }

    LaunchedEffect(currentIndex) {
        setupScramble()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Scramble Quest", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Word ${currentIndex + 1} of ${words.size}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("scramble_back_btn")) {
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
                // Word Mastery
                MasteryProgressBar(
                    boxLevel = currentWord.boxLevel,
                    isMastered = currentWord.isMastered,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Shuffle, contentDescription = "Scramble", tint = RoseTertiary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "UNTANGLE THE TILES",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoseTertiary,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Audio button & Definition
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { ttsHelper.speakWord(currentWord.word) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                    .testTag("scramble_speak_btn")
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Hear Word", tint = IndigoPrimary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentWord.definition.ifBlank { "Unscramble this spelling word!" },
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Target Slots / Placed Letters
                        Text(
                            text = "Your Assembled Word:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val targetLen = currentWord.cleanWord.length
                        FlowRow(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (i in 0 until targetLen) {
                                val item = placedLetters.getOrNull(i)
                                val status = when {
                                    !isSubmitted && item != null -> TileStatus.SELECTED
                                    !isSubmitted && item == null -> TileStatus.BLANK
                                    isSubmitted && isCorrect -> TileStatus.CORRECT
                                    isSubmitted && !isCorrect -> TileStatus.INCORRECT
                                    else -> TileStatus.DEFAULT
                                }

                                LetterTile(
                                    letter = item?.char ?: ' ',
                                    status = status,
                                    size = 48.dp,
                                    onClick = if (!isSubmitted && item != null) {
                                        {
                                            placedLetters.removeAt(i)
                                            scrambledPool.add(item)
                                        }
                                    } else null,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (!isSubmitted) {
                            // Shuffled Letter Pool
                            Text(
                                text = "Available Tiles (Tap to place):",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                scrambledPool.forEach { item ->
                                    LetterTile(
                                        letter = item.char,
                                        status = TileStatus.DEFAULT,
                                        size = 46.dp,
                                        onClick = {
                                            if (placedLetters.size < currentWord.cleanWord.length) {
                                                placedLetters.add(item)
                                                scrambledPool.remove(item)
                                            }
                                        },
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Hint / Phonics button
                            if (currentWord.hint.isNotBlank() || currentWord.displayPhonics.isNotBlank()) {
                                TextButton(
                                    onClick = { showHint = !showHint },
                                    modifier = Modifier.testTag("scramble_hint_btn")
                                ) {
                                    Icon(Icons.Default.Lightbulb, contentDescription = "Hint", tint = AmberSecondary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (showHint) "Hide Syllables" else "Show Syllables Hint", color = AmberSecondary)
                                }

                                if (showHint) {
                                    Text(
                                        text = "🧩 Syllables: ${currentWord.displayPhonics}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberSecondary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val spelled = placedLetters.map { it.char }.joinToString("").lowercase()
                                    isCorrect = spelled == currentWord.cleanWord
                                    isSubmitted = true
                                    if (isCorrect) {
                                        hasTriggeredConfetti = true
                                        ttsHelper.speakEncouragement()
                                    }
                                    onRecordAttempt(currentWord, isCorrect, "scramble", showHint)
                                },
                                enabled = placedLetters.size == currentWord.cleanWord.length,
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("scramble_check_btn")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Check")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Check Scramble", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Result State
                            Text(
                                text = if (isCorrect) "🎉 YOU SOLVED IT! ⭐" else "❌ Keep practicing!",
                                fontSize = 15.sp,
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
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Science of Reading Diagnostic Analysis
                            val scrambleAttempt = placedLetters.map { it.char }.joinToString("").lowercase()
                            val analysis = SpellingDiagnostics.analyze(
                                target = currentWord.cleanWord,
                                attempt = scrambleAttempt,
                                phonicsDisplay = currentWord.displayPhonics
                            )
                            DiagnosticCard(
                                analysis = analysis,
                                modifier = Modifier.testTag("scramble_diagnostic_card")
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = { setupScramble() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("scramble_retry_btn")
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
                                        } else {
                                            onNavigateBack()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .testTag("scramble_next_btn")
                                ) {
                                    Text(if (currentIndex < words.size - 1) "Next Word" else "Finish Quest")
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
