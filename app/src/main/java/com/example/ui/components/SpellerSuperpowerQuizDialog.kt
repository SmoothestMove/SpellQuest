package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SpellerSuperpower
import com.example.data.model.SpellerSuperpowers
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorderSubtle
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.EmeraldSuccessText

private data class QuizOption(
    val emoji: String,
    val text: String,
    val superpowerId: String,
    val subtitle: String = ""
)

private data class QuizQuestion(
    val id: Int,
    val title: String,
    val subtitle: String,
    val speechPrompt: String? = null,
    val options: List<QuizOption>
)

private val QUIZ_QUESTIONS = listOf(
    QuizQuestion(
        id = 1,
        title = "Sound Sleuth Quest 🎧",
        subtitle = "Say the word SMILE! If you take away the /s/ sound at the start, what word do you hear?",
        speechPrompt = "smile",
        options = listOf(
            QuizOption(
                emoji = "🔍",
                text = "\"MILE!\" My ears instantly hear the sounds separate",
                superpowerId = "sound_detective",
                subtitle = "Auditory & Phonemic Awareness"
            ),
            QuizOption(
                emoji = "🎨",
                text = "I picture S-M-I-L-E in my head and visually erase the S",
                superpowerId = "word_artist",
                subtitle = "Orthographic & Mental Grapheme"
            ),
            QuizOption(
                emoji = "🛡️",
                text = "The magic Silent 'E' at the end makes the 'I' say its name!",
                superpowerId = "phonics_knight",
                subtitle = "Spelling Rules & Structure"
            ),
            QuizOption(
                emoji = "⚡",
                text = "It rhymes with FILE, TILE, and WHILE (same word family)!",
                superpowerId = "speedy_solver",
                subtitle = "Pattern & Word Chunks"
            )
        )
    ),
    QuizQuestion(
        id = 2,
        title = "Snapshot Memory Check 📸",
        subtitle = "Take a quick glance! Which word immediately LOOKS like the correct picture in your mind?",
        options = listOf(
            QuizOption(
                emoji = "🎨",
                text = "\"FRIEND\" (I instantly spot the right shape and letter order)",
                superpowerId = "word_artist",
                subtitle = "Visual Whole-Word Memory"
            ),
            QuizOption(
                emoji = "🛡️",
                text = "I use the rule: 'i before e except after c'!",
                superpowerId = "phonics_knight",
                subtitle = "Rules & Logic Defense"
            ),
            QuizOption(
                emoji = "🔍",
                text = "I whisper /f/ /r/ /e/ /n/ /d/ out loud to test every sound",
                superpowerId = "sound_detective",
                subtitle = "Sound-to-Letter Breakdown"
            ),
            QuizOption(
                emoji = "⚡",
                text = "I break it into chunks: FR- and -IEND like a puzzle",
                superpowerId = "speedy_solver",
                subtitle = "Fast Letter Clumping"
            )
        )
    ),
    QuizQuestion(
        id = 3,
        title = "Word Builder Mission 🧱",
        subtitle = "You need to spell the big word: UNHELPFUL. What is your secret trick?",
        speechPrompt = "unhelpful",
        options = listOf(
            QuizOption(
                emoji = "🛡️",
                text = "Snap it like Lego pieces: UN- (prefix) + HELP (root) + -FUL (suffix)!",
                superpowerId = "phonics_knight",
                subtitle = "Morphology & Word Roots"
            ),
            QuizOption(
                emoji = "⚡",
                text = "Chant the 3 rhythm beats: UN - HELP - FUL (3 quick taps)!",
                superpowerId = "speedy_solver",
                subtitle = "Syllable Rhythm & Pacing"
            ),
            QuizOption(
                emoji = "🎨",
                text = "Visualize the entire word written across a bright billboard",
                superpowerId = "word_artist",
                subtitle = "Orthographic Visualizer"
            ),
            QuizOption(
                emoji = "🔍",
                text = "Sound out all eight sounds one by one from start to end",
                superpowerId = "sound_detective",
                subtitle = "Deep Phonics Sequencing"
            )
        )
    ),
    QuizQuestion(
        id = 4,
        title = "Syllable Beat & Rhythm 🥁",
        subtitle = "Clap out the beats of AL-LI-GA-TOR! How does your brain capture words?",
        speechPrompt = "alligator",
        options = listOf(
            QuizOption(
                emoji = "⚡",
                text = "4 beats! Clapping and typing rhythm locks words in my hands",
                superpowerId = "speedy_solver",
                subtitle = "Motor & Kinesthetic Memory"
            ),
            QuizOption(
                emoji = "🔍",
                text = "I listen closely for the vowel sound hidden in every single beat",
                superpowerId = "sound_detective",
                subtitle = "Phonological Sensitivity"
            ),
            QuizOption(
                emoji = "🛡️",
                text = "I notice the spelling rule at the end: it ends in -OR not -ER!",
                superpowerId = "phonics_knight",
                subtitle = "Pattern & Orthographic Rules"
            ),
            QuizOption(
                emoji = "🎨",
                text = "I picture a cool alligator holding up letter cards",
                superpowerId = "word_artist",
                subtitle = "Creative Visual Association"
            )
        )
    ),
    QuizQuestion(
        id = 5,
        title = "Homework Reflex Challenge 🚀",
        subtitle = "When your teacher calls out a brand new, tricky spelling word, what is your first natural instinct?",
        options = listOf(
            QuizOption(
                emoji = "🔍",
                text = "Whisper it softly and stretch out every sound like a rubber band",
                superpowerId = "sound_detective",
                subtitle = "Sound Detective Reflex"
            ),
            QuizOption(
                emoji = "🎨",
                text = "Write it on scratch paper to test if it *looks* right to my eyes",
                superpowerId = "word_artist",
                subtitle = "Word Artist Reflex"
            ),
            QuizOption(
                emoji = "🛡️",
                text = "Search my brain for spelling rules, base words, and word families",
                superpowerId = "phonics_knight",
                subtitle = "Phonics Knight Reflex"
            ),
            QuizOption(
                emoji = "⚡",
                text = "Type or finger-tap it quickly to let my muscle memory solve it",
                superpowerId = "speedy_solver",
                subtitle = "Speedy Solver Reflex"
            )
        )
    )
)

@Composable
fun SpellerSuperpowerQuizDialog(
    currentSuperpowerTitle: String,
    onDismiss: () -> Unit,
    onSuperpowerEquipped: (SpellerSuperpower) -> Unit
) {
    val context = LocalContext.current
    val ttsHelper = remember { TtsHelper(context) }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val scores = remember {
        mutableStateMapOf(
            "sound_detective" to 0,
            "speedy_solver" to 0,
            "phonics_knight" to 0,
            "word_artist" to 0
        )
    }
    var quizCompleted by remember { mutableStateOf(false) }
    var selectedPower by remember { mutableStateOf<SpellerSuperpower?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = BentoBackground,
            border = BorderStroke(1.5.dp, BentoBorderSubtle),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 560.dp)
                .padding(vertical = 16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (quizCompleted && selectedPower != null) {
                    ConfettiEffect(trigger = true, modifier = Modifier.fillMaxSize())
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = BentoPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (quizCompleted) "Your Superpower Revealed!" else "Superpower Quiz 🪄",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("quiz_close_button")
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = BentoTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!quizCompleted) {
                        // Progress Bar & Step Label
                        val progress = (currentQuestionIndex + 1).toFloat() / QUIZ_QUESTIONS.size
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = BentoPrimary,
                            trackColor = BentoPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Question ${currentQuestionIndex + 1} of ${QUIZ_QUESTIONS.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BentoPrimary
                            )
                            Text(
                                text = "Takes ~90 seconds",
                                fontSize = 11.sp,
                                color = BentoTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val question = QUIZ_QUESTIONS[currentQuestionIndex]

                        AnimatedContent(
                            targetState = question,
                            transitionSpec = {
                                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> -width } + fadeOut()
                                )
                            },
                            label = "question_transition"
                        ) { q ->
                            Column {
                                // Question Card
                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                                    border = BorderStroke(1.dp, BentoBorderSubtle),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = q.title,
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BentoTextPrimary,
                                                modifier = Modifier.weight(1f)
                                            )

                                            if (q.speechPrompt != null) {
                                                IconButton(
                                                    onClick = { ttsHelper.speakWord(q.speechPrompt) },
                                                    modifier = Modifier
                                                        .size(38.dp)
                                                        .background(BentoPrimaryContainer, CircleShape)
                                                ) {
                                                    Icon(
                                                        Icons.AutoMirrored.Filled.VolumeUp,
                                                        contentDescription = "Listen to word",
                                                        tint = BentoPrimary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = q.subtitle,
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp,
                                            color = BentoTextSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Choose the option that matches you best:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BentoTextSecondary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Options List
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    q.options.forEachIndexed { index, option ->
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = BentoSurface,
                                            border = BorderStroke(1.dp, BentoBorderSubtle),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    // Record answer
                                                    scores[option.superpowerId] = (scores[option.superpowerId] ?: 0) + 1

                                                    if (currentQuestionIndex < QUIZ_QUESTIONS.size - 1) {
                                                        currentQuestionIndex++
                                                    } else {
                                                        // Tally winner
                                                        val topId = scores.maxByOrNull { it.value }?.key ?: "sound_detective"
                                                        val finalPower = SpellerSuperpowers.ALL.find { it.id == topId } ?: SpellerSuperpowers.DEFAULT
                                                        selectedPower = finalPower
                                                        quizCompleted = true
                                                        ttsHelper.speakCustom("Hooray! You are a ${finalPower.name}!")
                                                    }
                                                }
                                                .testTag("quiz_option_${q.id}_$index")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(42.dp)
                                                        .background(BentoPrimaryContainer, RoundedCornerShape(12.dp)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(option.emoji, fontSize = 22.sp)
                                                }

                                                Spacer(modifier = Modifier.width(12.dp))

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = option.text,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        lineHeight = 18.sp,
                                                        color = BentoTextPrimary
                                                    )
                                                    if (option.subtitle.isNotBlank()) {
                                                        Text(
                                                            text = option.subtitle,
                                                            fontSize = 11.sp,
                                                            color = BentoTextSecondary
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Results Screen
                        val power = selectedPower ?: SpellerSuperpowers.DEFAULT

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Hero Disc
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .background(Color(power.colorHex).copy(alpha = 0.15f), CircleShape)
                                    .border(3.dp, Color(power.colorHex), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(power.emoji, fontSize = 46.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "🎉 You Are A...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BentoTextSecondary
                            )

                            Text(
                                text = power.title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(power.colorHex),
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = power.tagline,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = BentoTextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Profile Cards
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                                border = BorderStroke(1.dp, BentoBorderSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "⚡ Your Speller Strength:",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = power.description,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = BentoTextSecondary
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "🧠 Best Study Method:",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = power.learningStrategy,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = BentoTextSecondary
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "🏆 Recommended Practice Arena:",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = power.recommendedArena,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSuccessText
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Trait Compatibility Breakdown
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = BentoSurfaceVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "📊 Your Quiz Trait Breakdown:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    SpellerSuperpowers.ALL.forEach { p ->
                                        val count = scores[p.id] ?: 0
                                        val pct = (count.toFloat() / QUIZ_QUESTIONS.size.toFloat()).coerceIn(0f, 1f)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${p.emoji} ${p.name}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = BentoTextPrimary,
                                                modifier = Modifier.width(130.dp)
                                            )
                                            LinearProgressIndicator(
                                                progress = { pct },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(6.dp)
                                                    .clip(RoundedCornerShape(3.dp)),
                                                color = Color(p.colorHex),
                                                trackColor = BentoBackground
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "$count pts",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BentoTextSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Action Buttons
                            Button(
                                onClick = {
                                    onSuperpowerEquipped(power)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(power.colorHex)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("equip_superpower_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Equip ${power.name}! 🚀",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    // Reset quiz
                                    currentQuestionIndex = 0
                                    scores.keys.forEach { scores[it] = 0 }
                                    quizCompleted = false
                                    selectedPower = null
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retake Quiz", fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
