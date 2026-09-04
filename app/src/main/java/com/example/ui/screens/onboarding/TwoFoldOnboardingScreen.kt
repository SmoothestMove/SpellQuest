package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import com.example.data.model.SpellerSuperpower
import com.example.data.model.SpellerSuperpowers
import com.example.ui.components.PhotoScanDialog
import com.example.ui.components.SpellerSuperpowerQuizDialog
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TtsHelper
import com.example.ui.components.highContrastInputTextStyle
import com.example.ui.components.highContrastTextFieldColors
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.AmberSecondaryText
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoBorderSubtle
import com.example.ui.theme.BentoOnPrimaryContainer
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoStreakContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.CoralError
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.EmeraldSuccessText
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseTertiary
import com.example.ui.theme.RoseTertiaryText

data class AvatarOption(
    val id: String,
    val emoji: String,
    val name: String,
    val description: String,
    val accentColor: Color
)

val ALL_AVATARS = listOf(
    AvatarOption("owl", "🦉", "Ollie Owl", "Wise & Patient", Color(0xFF6750A4)),
    AvatarOption("fox", "🦊", "Felix Fox", "Clever & Quick", Color(0xFFE65100)),
    AvatarOption("bear", "🐻", "Barnaby Bear", "Kind & Strong", Color(0xFF5D4037)),
    AvatarOption("lion", "🦁", "Maya Lion", "Brave & Proud", Color(0xFFF57C00)),
    AvatarOption("astro", "🚀", "Nova Astro", "Space Explorer", Color(0xFF1976D2)),
    AvatarOption("dragon", "🐲", "Sparky Dragon", "Fiery & Bold", Color(0xFF388E3C)),
    AvatarOption("robot", "🤖", "Beep Robot", "Smart & Helpful", Color(0xFF0097A7)),
    AvatarOption("unicorn", "🦄", "Luna Unicorn", "Magical & Wonder", Color(0xFFC2185B)),
    AvatarOption("star", "🌟", "Sunny Star", "Joyful & Bright", Color(0xFFFFA000)),
    AvatarOption("dolphin", "🐬", "Echo Dolphin", "Playful & Splashy", Color(0xFF00ACC1))
)

@Composable
fun TwoFoldOnboardingScreen(
    ttsHelper: TtsHelper,
    initialPhase: String = "parent", // "parent" or "child"
    onFinishOnboarding: (
        parentPin: String,
        dailyTimeLimitMinutes: Int,
        dailyWordGoal: Int,
        gradeLevel: String,
        studentName: String,
        avatar: String,
        welcomeBonusXp: Int,
        initialWords: String,
        spellerSuperpower: String
    ) -> Unit,
    onSkipToApp: () -> Unit
) {
    // Current step index:
    // 0: Parent Welcome & Curriculum
    // 1: Parent Security PIN & Daily Usage Limits
    // 2: Parent Guided Tour (Science of Reading, Lists, Guided Mode, Mastery Boxes)
    // 3: Child Welcome & Profile (Name, Inclusive Avatar, Superpower)
    // 4: Child Gamification Tour (Star XP, Streaks, Badges, Game Arenas)
    // 5: Welcome Celebration (+50 Bonus Star XP)
    var currentStep by remember {
        mutableIntStateOf(if (initialPhase == "child") 3 else 0)
    }

    // State for Parent Settings
    var weeklyWordsInput by remember { mutableStateOf("") }
    var showPhotoScanDialog by remember { mutableStateOf(false) }
    var usePinProtection by remember { mutableStateOf(false) }
    var parentPin by remember { mutableStateOf("") }
    var dailyTimeLimitMinutes by remember { mutableIntStateOf(20) }
    var dailyWordGoal by remember { mutableIntStateOf(10) }

    // State for Child Profile
    var childName by remember { mutableStateOf("") }
    var selectedAvatarId by remember { mutableStateOf("owl") }
    var selectedTitle by remember { mutableStateOf("Sound Detective 🔍") }

    val isParentPhase = currentStep in 0..2
    val totalSteps = 6
    val progress = (currentStep + 1) / totalSteps.toFloat()

    Scaffold(
        containerColor = BentoBackground,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BentoBackground)
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Phase Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                color = if (isParentPhase) BentoPrimaryContainer else BentoStreakContainer,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (isParentPhase) "👨‍👩‍👧 PARENT ONBOARDING" else "🌟 SUPER SPELLER ONBOARDING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isParentPhase) BentoOnPrimaryContainer else Color(0xFF041E49)
                        )
                    }

                    // TTS button & Skip button
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                val textToRead = when (currentStep) {
                                    0 -> "Welcome to SpellQuest! Enter your child's weekly spelling words to get started."
                                    1 -> "Set up your parental dashboard settings, usage limits, and security PIN."
                                    2 -> "Here is your guided tour of the diagnostic tools, custom word lists, and co-learning modes."
                                    3 -> "Welcome, super speller! Type your name and choose your favorite avatar."
                                    4 -> "Explore your gamification spots: Star XP, streaks, badges, and 5 game modes."
                                    5 -> "You are all set! You have earned 50 bonus star XP! Tap start to begin."
                                    else -> ""
                                }
                                ttsHelper.speakCustom(textToRead)
                            },
                            modifier = Modifier.size(36.dp).testTag("onboarding_tts_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Read aloud",
                                tint = BentoPrimary
                            )
                        }

                        TextButton(
                            onClick = {
                                if (isParentPhase) {
                                    currentStep = 3 // Fast-forward to Child Onboarding
                                } else {
                                    onFinishOnboarding(
                                        parentPin,
                                        dailyTimeLimitMinutes,
                                        dailyWordGoal,
                                        "Elementary",
                                        childName.ifBlank { "Super Speller" },
                                        selectedAvatarId,
                                        50,
                                        weeklyWordsInput,
                                        selectedTitle
                                    )
                                }
                            },
                            modifier = Modifier.testTag("onboarding_skip_btn")
                        ) {
                            Text(
                                text = if (isParentPhase) "Skip to Kid →" else "Quick Start",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BentoTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isParentPhase) BentoPrimary else AmberSecondary,
                    trackColor = BentoSurfaceVariant
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BentoBackground)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("onboarding_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Back", fontSize = 14.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = {
                        if (currentStep < 5) {
                            currentStep++
                            if (currentStep == 5) {
                                ttsHelper.speakCustom("Hooray $childName! You earned 50 bonus star XP! Let's start spelling!")
                            }
                        } else {
                            onFinishOnboarding(
                                parentPin,
                                dailyTimeLimitMinutes,
                                dailyWordGoal,
                                "Custom",
                                childName.ifBlank { "Super Speller" },
                                selectedAvatarId,
                                50,
                                weeklyWordsInput,
                                selectedTitle
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isParentPhase) BentoPrimary else AmberSecondary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("onboarding_next_btn")
                ) {
                    Text(
                        text = when (currentStep) {
                            2 -> "Hand Device to Child 🎈"
                            5 -> "Start My Quest! 🚀"
                            else -> "Continue"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (currentStep == 5) Icons.Default.Star else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "onboarding_step_anim"
            ) { step ->
                when (step) {
                    0 -> ParentWordsInputStep(
                        weeklyWordsInput = weeklyWordsInput,
                        onWordsInputChange = { weeklyWordsInput = it },
                        onScanPhotoClick = { showPhotoScanDialog = true }
                    )
                    1 -> ParentSettingsStep(
                        usePinProtection = usePinProtection,
                        onToggleUsePin = { usePinProtection = it },
                        parentPin = parentPin,
                        onPinChanged = { parentPin = it },
                        dailyTimeLimitMinutes = dailyTimeLimitMinutes,
                        onTimeLimitChanged = { dailyTimeLimitMinutes = it },
                        dailyWordGoal = dailyWordGoal,
                        onWordGoalChanged = { dailyWordGoal = it }
                    )
                    2 -> ParentGuidedTourStep()
                    3 -> ChildProfileStep(
                        childName = childName,
                        onNameChange = { childName = it },
                        selectedAvatarId = selectedAvatarId,
                        onAvatarSelect = { selectedAvatarId = it },
                        selectedTitle = selectedTitle,
                        onTitleSelect = { selectedTitle = it }
                    )
                    4 -> ChildGamificationTourStep()
                    5 -> ChildCelebrationStep(
                        childName = childName,
                        selectedAvatar = ALL_AVATARS.firstOrNull { it.id == selectedAvatarId } ?: ALL_AVATARS.first(),
                        selectedTitle = selectedTitle
                    )
                }
            }
        }
    }

    if (showPhotoScanDialog) {
        PhotoScanDialog(
            onDismiss = { showPhotoScanDialog = false },
            onWordsAutoAdded = { words ->
                if (words.isNotEmpty()) {
                    val newText = if (weeklyWordsInput.isBlank()) {
                        words.joinToString("\n")
                    } else {
                        weeklyWordsInput.trim() + "\n" + words.joinToString("\n")
                    }
                    weeklyWordsInput = newText
                }
            }
        )
    }
}

// -------------------------------------------------------------
// STEP 0: Parent Welcome & Words Input
// -------------------------------------------------------------
@Composable
private fun ParentWordsInputStep(
    weeklyWordsInput: String,
    onWordsInputChange: (String) -> Unit,
    onScanPhotoClick: () -> Unit = {}
) {
    val wordCount = weeklyWordsInput.split(Regex("[\\s,;]+"))
        .map { it.replace(Regex("^[0-9]+[.)\\s-]+\\s*"), "").trim() }
        .filter { it.isNotBlank() && it.length > 1 }
        .distinct()
        .size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentPadding = PaddingValues(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Welcome, Parents! 👋",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BentoTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "SpellQuest is built specifically for the spelling words assigned to your child. No unneeded pre-set lists—practice and master the exact words required.",
                fontSize = 14.sp,
                color = BentoTextSecondary,
                lineHeight = 20.sp
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📝", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Enter This Week's Spelling Words",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Take a photo of your child's weekly homework or type words below:",
                        fontSize = 12.sp,
                        color = BentoTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onScanPhotoClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("onboarding_scan_photo_btn")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("📸 Snap Photo of Word List", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = weeklyWordsInput,
                        onValueChange = onWordsInputChange,
                        textStyle = highContrastInputTextStyle,
                        placeholder = {
                            Text(
                                text = "e.g. because, friend, enough, laugh, caught, bright\n\n1. elephant\n2. giraffe\n3. neighbor",
                                fontSize = 13.sp,
                                color = BentoTextSecondary,
                                lineHeight = 18.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .testTag("onboarding_words_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = highContrastTextFieldColors(
                            containerColor = BentoSurface,
                            focusedBorderColor = BentoPrimary,
                            borderColor = BentoBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (wordCount > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(EmeraldSuccess.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$wordCount required words ready",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccessText
                                )
                            }
                        } else {
                            Text(
                                text = "Tip: You can also add words later anytime",
                                fontSize = 11.sp,
                                color = BentoTextSecondary
                            )
                        }

                        if (weeklyWordsInput.isNotBlank()) {
                            TextButton(onClick = { onWordsInputChange("") }) {
                                Text("Clear", fontSize = 12.sp, color = CoralError)
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoPrimaryContainer.copy(alpha = 0.3f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoPrimary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Zero friction: All multi-sensory games (Listen & Spell, Scramble, Missing Vowels, Spelling Bee) will automatically adapt strictly to these words.",
                        fontSize = 12.sp,
                        color = BentoPrimary,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 1: Parent Controls & Usage Limits
// -------------------------------------------------------------
@Composable
private fun ParentSettingsStep(
    usePinProtection: Boolean,
    onToggleUsePin: (Boolean) -> Unit,
    parentPin: String,
    onPinChanged: (String) -> Unit,
    dailyTimeLimitMinutes: Int,
    onTimeLimitChanged: (Int) -> Unit,
    dailyWordGoal: Int,
    onWordGoalChanged: (Int) -> Unit
) {
    val timeLimitOptions = listOf(10, 15, 20, 30, 0) // 0 = unlimited
    val wordGoalOptions = listOf(5, 10, 15, 20)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentPadding = PaddingValues(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Parental Dashboard & Limits ⚙️",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BentoTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Control screen time, establish daily learning goals, and protect settings with a secure PIN.",
                fontSize = 14.sp,
                color = BentoTextSecondary
            )
        }

        // Parent Lock PIN Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = "PIN", tint = BentoPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Parent Security PIN", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                                Text(
                                    if (usePinProtection) "4-digit PIN required for Parent Zone" else "Default Math Challenge Gate",
                                    fontSize = 12.sp,
                                    color = BentoTextSecondary
                                )
                            }
                        }
                        Switch(
                            checked = usePinProtection,
                            onCheckedChange = onToggleUsePin,
                            colors = SwitchDefaults.colors(checkedThumbColor = BentoPrimary, checkedTrackColor = BentoPrimaryContainer)
                        )
                    }

                    if (usePinProtection) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = parentPin,
                            onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) onPinChanged(it) },
                            textStyle = highContrastInputTextStyle,
                            label = { Text("Enter 4-digit PIN", color = BentoTextPrimary) },
                            placeholder = { Text("e.g. 1234", color = BentoTextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            colors = highContrastTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().testTag("onboarding_pin_input")
                        )
                    }
                }
            }
        }

        // Daily Time Limit Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⏳", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Daily Practice Time Target", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                    }
                    Text("Optimal focus is 15-20 minutes of active phonics engagement.", fontSize = 12.sp, color = BentoTextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        timeLimitOptions.forEach { mins ->
                            val isSelected = dailyTimeLimitMinutes == mins
                            val label = if (mins == 0) "No Limit" else "${mins}m"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) BentoPrimary else BentoSurfaceVariant)
                                    .clickable { onTimeLimitChanged(mins) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else BentoTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily Word Goal Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎯", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Daily Mastered Words Goal", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                    }
                    Text("Helps kids feel a sense of completion each study session.", fontSize = 12.sp, color = BentoTextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        wordGoalOptions.forEach { count ->
                            val isSelected = dailyWordGoal == count
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) BentoPrimary else BentoSurfaceVariant)
                                    .clickable { onWordGoalChanged(count) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$count Words",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else BentoTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 2: Guided Tour for Parents
// -------------------------------------------------------------
@Composable
private fun ParentGuidedTourStep() {
    val tourCards = listOf(
        TourItem(
            emoji = "🔬",
            title = "Orthographic Diagnostic Engine",
            desc = "When your child makes a mistake, SpellQuest diagnoses the root phonics cause: phonetic plausibility (e.g. k/c), dyslexic letter reversals (b/d/p/q), vowel slips, or heart word exceptions.",
            color = BentoPrimaryContainer,
            tint = BentoPrimary
        ),
        TourItem(
            emoji = "📋",
            title = "Weekly School Lists & Voice Input",
            desc = "Easily add Friday spelling test words in seconds! Type, copy-paste in bulk, or use voice dictation in the Parent Hub.",
            color = BentoStreakContainer,
            tint = Color(0xFF041E49)
        ),
        TourItem(
            emoji = "👥",
            title = "Live Parent-Guided Co-Learning",
            desc = "Launch Guided Mode to sit with your child: you call out words and context sentences, watch their typing in real-time, and award digital stickers and coaching.",
            color = Color(0xFFE8F5E9),
            tint = EmeraldSuccess
        ),
        TourItem(
            emoji = "📦",
            title = "5 Leitner Spaced-Repetition Boxes",
            desc = "Words move automatically through 5 mastery levels. Struggling words get frequent practice, while mastered words are periodically reviewed for permanent retention.",
            color = Color(0xFFFFF3E0),
            tint = AmberSecondary
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentPadding = PaddingValues(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Guided Tour of Parent Tools 🧭",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BentoTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Here are the 4 superpowers you have in your Parent Hub:",
                fontSize = 14.sp,
                color = BentoTextSecondary
            )
        }

        items(tourCards) { card ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(card.color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(card.emoji, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = card.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = card.tint
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = card.desc,
                            fontSize = 13.sp,
                            color = BentoTextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BentoPrimaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎈", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Ready to pass the device to your child?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoPrimary
                        )
                        Text(
                            text = "Next, we'll give them an exciting tour of the gamification spots!",
                            fontSize = 12.sp,
                            color = BentoTextSecondary
                        )
                    }
                }
            }
        }
    }
}

private data class TourItem(
    val emoji: String,
    val title: String,
    val desc: String,
    val color: Color,
    val tint: Color
)

// -------------------------------------------------------------
// STEP 3: Child Profile & Inclusive Identity
// -------------------------------------------------------------
@Composable
private fun ChildProfileStep(
    childName: String,
    onNameChange: (String) -> Unit,
    selectedAvatarId: String,
    onAvatarSelect: (String) -> Unit,
    selectedTitle: String,
    onTitleSelect: (String) -> Unit
) {
    var showSuperpowerDialog by remember { mutableStateOf(false) }
    var showQuizDialog by remember { mutableStateOf(false) }

    val activePower = SpellerSuperpowers.getByIdOrTitle(selectedTitle)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentPadding = PaddingValues(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Welcome, Super Speller! 🌟",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BentoTextPrimary
            )
            Text(
                text = "Let's set up your spelling quest identity. Enter your name and choose your superpower!",
                fontSize = 14.sp,
                color = BentoTextSecondary
            )
        }

        // Child Name
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Your Speller Name", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                    Text("Every spelling quest begins with your true name!", fontSize = 12.sp, color = BentoTextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = childName,
                        onValueChange = onNameChange,
                        textStyle = highContrastInputTextStyle,
                        placeholder = { Text("Type your name (e.g. Maya, Jordan, Alex)...", color = BentoTextSecondary) },
                        singleLine = true,
                        colors = highContrastTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("onboarding_child_name_input")
                    )
                }
            }
        }

        // Avatar Selection (Inclusive & Diverse)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Choose Your Quest Companion", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                    Text("Every speller is unique! Pick the friend that represents you:", fontSize = 12.sp, color = BentoTextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(ALL_AVATARS) { avatar ->
                            val isSelected = selectedAvatarId == avatar.id
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) BentoPrimaryContainer else BentoSurfaceVariant)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) BentoPrimary else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { onAvatarSelect(avatar.id) }
                                    .padding(12.dp)
                                    .width(90.dp)
                            ) {
                                Text(avatar.emoji, fontSize = 34.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = avatar.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = BentoTextPrimary
                                )
                                Text(
                                    text = avatar.description,
                                    fontSize = 9.sp,
                                    color = BentoTextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Speller Superpower: Rich Explanation & Selection
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Your Speller Superpower ⚡", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                            Text("How does your brain best learn & remember words?", fontSize = 12.sp, color = BentoTextSecondary)
                        }
                        IconButton(
                            onClick = { showSuperpowerDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "What is a Superpower?",
                                tint = IndigoPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 90-Second Superpower Quiz Button
                    Button(
                        onClick = { showQuizDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("take_superpower_quiz_onboarding")
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Take 90-Sec Superpower Quiz 🪄",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "— or choose your superpower directly —",
                        fontSize = 11.sp,
                        color = BentoTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Grid/List of 4 Superpowers
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SpellerSuperpowers.ALL.forEach { power ->
                            val isSelected = selectedTitle.contains(power.title, ignoreCase = true) || selectedTitle.contains(power.name, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) BentoPrimaryContainer else BentoSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) BentoPrimary else BentoBorderSubtle
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onTitleSelect("${power.name} ${power.emoji}") }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(power.emoji, fontSize = 28.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = power.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoTextPrimary
                                        )
                                        Text(
                                            text = power.tagline,
                                            fontSize = 11.sp,
                                            color = BentoTextSecondary
                                        )
                                    }
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .background(BentoPrimary, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Deep Explanation Spotlight for Selected Superpower
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = BentoStreakContainer.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(activePower.emoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "How ${activePower.name} Works",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF041E49)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = activePower.description,
                                fontSize = 12.sp,
                                color = BentoTextPrimary,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "🎯 Strategy: ${activePower.learningStrategy}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = IndigoPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "🏆 Recommended Mode: ${activePower.recommendedArena}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldSuccessText
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSuperpowerDialog) {
        AlertDialog(
            onDismissRequest = { showSuperpowerDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚡ What is a Speller Superpower?", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                }
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.height(340.dp)
                ) {
                    item {
                        Text(
                            text = "Every speller's brain processes language uniquely. Choosing a Speller Superpower helps identify your child's cognitive learning style:\n",
                            fontSize = 13.sp,
                            color = BentoTextPrimary,
                            lineHeight = 17.sp
                        )
                    }
                    items(SpellerSuperpowers.ALL) { power ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BentoSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("${power.emoji} ${power.name}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BentoTextPrimary)
                                Text(power.tagline, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = IndigoPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(power.description, fontSize = 11.sp, color = BentoTextSecondary, lineHeight = 15.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Best Arena: ${power.recommendedArena}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccessText)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            showSuperpowerDialog = false
                            showQuizDialog = true
                        }
                    ) {
                        Text("Take Quiz 🪄")
                    }
                    Button(
                        onClick = { showSuperpowerDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary)
                    ) {
                        Text("Got It! 🌟", color = Color.White)
                    }
                }
            }
        )
    }

    if (showQuizDialog) {
        SpellerSuperpowerQuizDialog(
            currentSuperpowerTitle = selectedTitle,
            onDismiss = { showQuizDialog = false },
            onSuperpowerEquipped = { power ->
                onTitleSelect("${power.name} ${power.emoji}")
                showQuizDialog = false
            }
        )
    }
}

// -------------------------------------------------------------
// STEP 4: Child Gamification Tour (The "Quest Map")
// -------------------------------------------------------------
@Composable
private fun ChildGamificationTourStep() {
    val gameModes = listOf(
        Triple("👁️", "Look • Say • Cover • Write • Check", "The classic memory method to lock words in your mind!"),
        Triple("🎧", "Listen & Spell", "Listen closely, sound out each syllable, and spell!"),
        Triple("🧩", "Missing Vowel Buster", "Sneaky vowels got lost! Can you find the right ones?"),
        Triple("🔤", "Word Scramble", "Unscramble mixed-up letter tiles to reveal secret words!"),
        Triple("🐝", "Weekly Boss Spelling Bee", "Test all your words in an epic quiz arena!")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentPadding = PaddingValues(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Your Quest Map & Gamification Spots 🗺️",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BentoTextPrimary
            )
            Text(
                text = "Here are all the ways you earn rewards, level up, and become a spelling master!",
                fontSize = 14.sp,
                color = BentoTextSecondary
            )
        }

        // Spotlight 1: XP & Levels
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(46.dp).background(Color(0xFFFFF9C4), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⭐", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Earn Star XP & Level Up!", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AmberSecondaryText)
                        Text(
                            "Every word you spell gives you +15 XP. Level up from Spelling Sprout 🌱 to Lexicon Legend 👑!",
                            fontSize = 13.sp,
                            color = BentoTextSecondary
                        )
                    }
                }
            }
        }

        // Spotlight 2: Fire Streaks
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(46.dp).background(BentoStreakContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔥", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Keep Your Daily Fire Streak Burning!", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF041E49))
                        Text(
                            "Practice EVERY word in your week's list each day to earn your daily streak and unlock fire badges! Practicing only a few words won't count—it takes the whole list to keep the flame alive!",
                            fontSize = 13.sp,
                            color = BentoTextSecondary
                        )
                    }
                }
            }
        }

        // Spotlight 3: Badges
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(46.dp).background(RoseTertiary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏆", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Unlock 12 Mystery Trophy Badges!", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoseTertiaryText)
                        Text(
                            "Can you earn 'Tile Untangler', 'Memory Master', and 'Spelling Bee Hero'?",
                            fontSize = 13.sp,
                            color = BentoTextSecondary
                        )
                    }
                }
            }
        }

        // Spotlight 4: 5 Game Modes
        item {
            Text("🎮 5 Exciting Practice Arenas:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
        }

        items(gameModes) { (icon, title, desc) ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                        Text(desc, fontSize = 12.sp, color = BentoTextSecondary)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 5: Child Celebration & Launch (+50 Bonus Star XP)
// -------------------------------------------------------------
@Composable
private fun ChildCelebrationStep(
    childName: String,
    selectedAvatar: AvatarOption,
    selectedTitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar Celebration Disc
        Box(
            modifier = Modifier
                .size(110.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(AmberSecondary, BentoPrimaryContainer)
                    ),
                    shape = CircleShape
                )
                .border(4.dp, AmberSecondary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(selectedAvatar.emoji, fontSize = 56.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "You're Ready, $childName! 🎉",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = BentoTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = selectedTitle,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = BentoPrimary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Starter Gift Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BentoSurface),
            border = androidx.compose.foundation.BorderStroke(2.dp, AmberSecondary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🎁 WELCOME STARTER BONUS 🎁", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = AmberSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("+50 Star XP", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = BentoTextPrimary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "A special head-start on your journey to becoming a Lexicon Legend!",
                    fontSize = 13.sp,
                    color = BentoTextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("💡", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Tip: Try Look-Say-Cover-Write-Check first to meet your weekly words!",
                    fontSize = 13.sp,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}
