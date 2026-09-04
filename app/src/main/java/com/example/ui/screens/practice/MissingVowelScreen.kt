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
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Extension
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MissingVowelScreen(
    words: List<SpellingWord>,
    ttsHelper: TtsHelper,
    onRecordAttempt: (SpellingWord, Boolean, String, Boolean) -> Unit,
    onNavigateBack: () -> Unit
) {
    if (words.isEmpty()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Vowel Buster", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("vowels_back_btn")) {
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
                        Text("🧩", fontSize = 44.sp)
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

    val vowels = listOf('a', 'e', 'i', 'o', 'u', 'y')
    val missingIndices = remember { mutableStateListOf<Int>() }
    val userFilledVowels = remember { mutableStateListOf<Char>() }

    var isSubmitted by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var hasTriggeredConfetti by remember { mutableStateOf(false) }

    fun setupVowelPuzzle() {
        missingIndices.clear()
        userFilledVowels.clear()
        isSubmitted = false
        isCorrect = false

        val clean = currentWord.cleanWord
        clean.forEachIndexed { index, c ->
            if (c in vowels) {
                missingIndices.add(index)
            }
        }
        // If word had no vowels (rare), pick every 2nd letter
        if (missingIndices.isEmpty()) {
            for (i in clean.indices step 2) {
                missingIndices.add(i)
            }
        }

        ttsHelper.speakWord(currentWord.word)
    }

    LaunchedEffect(currentIndex) {
        setupVowelPuzzle()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Vowel Buster", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Word ${currentIndex + 1} of ${words.size}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("vowel_back_btn")) {
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
                            Icon(Icons.Default.Extension, contentDescription = "Vowel Buster", tint = SkyAccent)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FILL IN THE MISSING VOWELS",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SkyAccent,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Audio button
                        IconButton(
                            onClick = { ttsHelper.speakWord(currentWord.word) },
                            modifier = Modifier
                                .size(44.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                .testTag("vowel_speak_btn")
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Hear Word", tint = IndigoPrimary)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Word Display with Missing Vowel Slots
                        val clean = currentWord.cleanWord
                        FlowRow(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            var vowelSlotCounter = 0
                            clean.forEachIndexed { index, originalChar ->
                                if (index in missingIndices) {
                                    val filledChar = userFilledVowels.getOrNull(vowelSlotCounter)
                                    val status = when {
                                        !isSubmitted && filledChar != null -> TileStatus.SELECTED
                                        !isSubmitted && filledChar == null -> TileStatus.BLANK
                                        isSubmitted && isCorrect -> TileStatus.CORRECT
                                        isSubmitted && !isCorrect -> TileStatus.INCORRECT
                                        else -> TileStatus.DEFAULT
                                    }

                                    LetterTile(
                                        letter = filledChar ?: ' ',
                                        status = status,
                                        size = 46.dp,
                                        modifier = Modifier.padding(3.dp)
                                    )
                                    vowelSlotCounter++
                                } else {
                                    // Fixed Consonant
                                    LetterTile(
                                        letter = originalChar,
                                        status = TileStatus.DEFAULT,
                                        size = 46.dp,
                                        modifier = Modifier.padding(3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (!isSubmitted) {
                            Text(
                                text = "Select vowel (${userFilledVowels.size}/${missingIndices.size} filled):",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Big Vowel Tiles: A E I O U Y
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                listOf('A', 'E', 'I', 'O', 'U', 'Y').forEach { v ->
                                    LetterTile(
                                        letter = v,
                                        status = TileStatus.DEFAULT,
                                        size = 48.dp,
                                        onClick = {
                                            if (userFilledVowels.size < missingIndices.size) {
                                                userFilledVowels.add(v.lowercaseChar())
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (userFilledVowels.isNotEmpty()) {
                                TextButton(
                                    onClick = { userFilledVowels.removeAt(userFilledVowels.lastIndex) },
                                    modifier = Modifier.testTag("vowel_undo_btn")
                                ) {
                                    Icon(Icons.Default.Backspace, contentDescription = "Undo", tint = AmberSecondary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Undo Vowel", color = AmberSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    // Assemble word and check
                                    val sb = StringBuilder()
                                    var vIndex = 0
                                    clean.forEachIndexed { i, c ->
                                        if (i in missingIndices) {
                                            sb.append(userFilledVowels.getOrNull(vIndex) ?: '_')
                                            vIndex++
                                        } else {
                                            sb.append(c)
                                        }
                                    }
                                    isCorrect = sb.toString() == clean
                                    isSubmitted = true
                                    if (isCorrect) {
                                        hasTriggeredConfetti = true
                                        ttsHelper.speakEncouragement()
                                    }
                                    onRecordAttempt(currentWord, isCorrect, "vowel", false)
                                },
                                enabled = userFilledVowels.size == missingIndices.size,
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("vowel_check_btn")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Check")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Check Vowels", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Feedback
                            Text(
                                text = if (isCorrect) "🎉 SPOT ON! VOWEL MASTER! ⭐" else "❌ The full word is: ${currentWord.word.uppercase()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) EmeraldSuccess else MaterialTheme.colorScheme.error
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Assembled attempt for diagnostic
                            val assembledAttempt = buildString {
                                var vIdx = 0
                                clean.forEachIndexed { i, c ->
                                    if (i in missingIndices) {
                                        append(userFilledVowels.getOrNull(vIdx) ?: '_')
                                        vIdx++
                                    } else {
                                        append(c)
                                    }
                                }
                            }

                            // Science of Reading Diagnostic Analysis
                            val analysis = SpellingDiagnostics.analyze(
                                target = currentWord.cleanWord,
                                attempt = assembledAttempt,
                                phonicsDisplay = currentWord.displayPhonics
                            )
                            DiagnosticCard(
                                analysis = analysis,
                                modifier = Modifier.testTag("vowel_diagnostic_card")
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = { setupVowelPuzzle() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("vowel_retry_btn")
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
                                        .testTag("vowel_next_btn")
                                ) {
                                    Text(if (currentIndex < words.size - 1) "Next Word" else "Finish Session")
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
