package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EmojiEvents
import com.example.ui.components.PhotoScanDialog
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import com.example.data.model.SpellerSuperpowers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SpellingWord
import com.example.data.model.UserStats
import com.example.data.model.WeeklyList
import com.example.ui.components.MascotBubble
import com.example.ui.components.ParentGateDialog
import com.example.ui.components.TtsHelper
import com.example.ui.components.highContrastInputTextStyle
import com.example.ui.components.highContrastTextFieldColors
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBadgeContainer
import com.example.ui.theme.BentoBorder
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
import com.example.ui.theme.EmeraldSuccessText
import com.example.ui.theme.RoseTertiary
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.VioletMagic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    activeList: WeeklyList?,
    activeWords: List<SpellingWord>,
    userStats: UserStats,
    ttsHelper: TtsHelper,
    onNavigateToLscwc: () -> Unit,
    onNavigateToListenSpell: () -> Unit,
    onNavigateToScramble: () -> Unit,
    onNavigateToVowels: () -> Unit,
    onNavigateToSpellingBee: () -> Unit,
    onNavigateToBadges: () -> Unit,
    onNavigateToParentZone: () -> Unit,
    onNavigateToParentGuided: () -> Unit,
    onNavigateToOnboarding: (String) -> Unit = {},
    onAddRequiredWords: (String) -> Unit = {}
) {
    var showParentGate by remember { mutableStateOf(false) }
    var showQuickAddDialog by remember { mutableStateOf(false) }
    var showPhotoScanDialog by remember { mutableStateOf(false) }
    var showSuperpowerDialog by remember { mutableStateOf(false) }
    var showStreakDialog by remember { mutableStateOf(false) }
    var selectedBottomNav by remember { mutableIntStateOf(0) }
    var selectedLearningModeTab by remember { mutableIntStateOf(0) } // 0 = Self-Guided, 1 = Parent-Guided

    val wordsPracticedTodayCount = activeWords.count { it.isPracticedToday() }
    val totalActiveWords = activeWords.size
    val isDailyStreakEarnedToday = totalActiveWords > 0 && wordsPracticedTodayCount >= totalActiveWords

    val masteredCount = activeWords.count { it.isMastered || it.boxLevel >= 3 }
    val progressFraction = if (activeWords.isNotEmpty()) masteredCount.toFloat() / activeWords.size else 0f

    val mascotMessage = when {
        activeWords.isEmpty() -> "Ready to practice? Tap below to enter this week's spelling words!"
        isDailyStreakEarnedToday -> "🔥 SPECTACULAR! You practiced all $totalActiveWords words today! Your ${userStats.currentStreakDays}-day streak is secured!"
        wordsPracticedTodayCount > 0 -> "Keep going! You've practiced $wordsPracticedTodayCount of $totalActiveWords words today. Practice all of them to earn today's 🔥 streak!"
        masteredCount == activeWords.size && activeWords.isNotEmpty() -> "🌟 WOW! You've mastered all words this week! Take the Spelling Bee test!"
        else -> "Ready to practice? Practice every word in your list today to keep your daily streak alive!"
    }

    val featuredWord = activeWords.firstOrNull { !it.isMastered } ?: activeWords.firstOrNull()

    val avatarEmoji = when (userStats.currentAvatar) {
        "dragon" -> "🐲"
        "fox" -> "🦊"
        "astro", "astronaut" -> "🚀"
        "wizard" -> "🧙‍♂️"
        "robot" -> "🤖"
        "bear" -> "🐻"
        "lion" -> "🦁"
        "unicorn" -> "🦄"
        "star" -> "🌟"
        "dolphin" -> "🐬"
        else -> "🦉"
    }

    Scaffold(
        containerColor = BentoBackground,
        bottomBar = {
            BentoBottomNavigationBar(
                selectedIndex = selectedBottomNav,
                onHomeClick = { selectedBottomNav = 0 },
                onPracticeClick = {
                    selectedBottomNav = 1
                    onNavigateToListenSpell()
                },
                onStatsClick = {
                    selectedBottomNav = 2
                    onNavigateToBadges()
                },
                onParentClick = {
                    selectedBottomNav = 3
                    showParentGate = true
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // 1. Bento Header: Eyebrow + Greeting + Avatar Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SPELLING HERO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoPrimary,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Hello, ${userStats.studentName}! 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Speller Superpower Pill Chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(BentoPrimaryContainer)
                            .clickable { showSuperpowerDialog = true }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "⚡ Superpower: ${userStats.spellerSuperpower}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Explain Superpower",
                            tint = BentoPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                // Avatar Circle + Parent lock button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(BentoPrimaryContainer)
                            .border(2.dp, BentoPrimary, CircleShape)
                            .clickable { onNavigateToBadges() }
                            .testTag("home_avatar_badge")
                    ) {
                        Text(
                            text = avatarEmoji,
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { showPhotoScanDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("home_scan_camera_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Scan Word List Photo",
                            tint = BentoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = { onNavigateToOnboarding("parent") },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("home_tour_btn")
                    ) {
                        Text("🧭", fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = { showParentGate = true },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("home_parent_zone_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Parent Zone",
                            tint = BentoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 2. Bento Stats Row (2 Columns: TOTAL POINTS + STREAK)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bento Card: TOTAL POINTS (bg-[#EADDFF], text-[#21005D], rounded-3xl)
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoPrimaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToBadges() }
                        .testTag("bento_points_tile")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "TOTAL POINTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoOnPrimaryContainer.copy(alpha = 0.7f),
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⭐ ${userStats.totalXp}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoOnPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Lv ${userStats.level} Speller",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BentoOnPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                // Bento Card: STREAK (bg-[#D3E2FD], text-[#041E49], rounded-3xl)
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoStreakContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showStreakDialog = true }
                        .testTag("bento_streak_tile")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "STREAK",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoOnStreak.copy(alpha = 0.7f),
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🔥 ${userStats.currentStreakDays} Days",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoOnStreak
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = when {
                                totalActiveWords == 0 -> "Add words to begin"
                                isDailyStreakEarnedToday -> "✓ Done today ($totalActiveWords/$totalActiveWords)"
                                else -> "$wordsPracticedTodayCount/$totalActiveWords words today\n(All required for streak)"
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BentoOnStreak.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center,
                            lineHeight = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Bento Mascot Guide Bubble
            MascotBubble(
                message = mascotMessage,
                avatarKey = userStats.currentAvatar,
                onSpeakMessage = { ttsHelper.speakSentence(mascotMessage) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3.5 Dual-Mode Selection Card: Self-Guided vs Parent-Guided
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BentoPrimary.copy(alpha = 0.25f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bento_dual_mode_card")
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
                        Text(
                            text = "CHOOSE LEARNING MODE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "2 Ways to Learn",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BentoTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Mode 1: Self-Guided Lesson
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedLearningModeTab == 0) BentoPrimaryContainer else BentoSurfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (selectedLearningModeTab == 0) BentoPrimary else Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedLearningModeTab = 0 }
                                .testTag("mode_tab_self_guided")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🎮", fontSize = 20.sp)
                                    if (selectedLearningModeTab == 0) {
                                        Text("Active", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BentoPrimary)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Self-Guided",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (selectedLearningModeTab == 0) BentoOnPrimaryContainer else BentoTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Interactive solo games for your child",
                                    fontSize = 11.sp,
                                    color = BentoTextSecondary,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        // Mode 2: Parent-Guided Practice
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedLearningModeTab == 1) BentoStreakContainer else BentoSurfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (selectedLearningModeTab == 1) BentoOnStreak else Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedLearningModeTab = 1
                                    onNavigateToParentGuided()
                                }
                                .testTag("mode_tab_parent_guided")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("👨‍👩‍👧", fontSize = 20.sp)
                                    Text("Dual Screen", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BentoOnStreak)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Parent-Guided",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = BentoOnStreak
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Parent picks word & checks spelling",
                                    fontSize = 11.sp,
                                    color = BentoTextSecondary,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }

                    if (selectedLearningModeTab == 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onNavigateToParentGuided,
                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("start_parent_guided_banner_btn")
                        ) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = "Parent Guided", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Parent-Guided Session 🚀", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Hero Bento Tile: Daily Challenge / Active List Spotlight (bg-white, border border-[#CAC4D0], rounded-3xl)
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bento_hero_challenge_tile")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Eyebrow and Tag Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (activeWords.isNotEmpty()) "Current Word (${masteredCount + 1} of ${activeWords.size})" else "Weekly Quest",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = BentoTextSecondary
                            )
                            Text(
                                text = activeList?.title ?: if (activeWords.isEmpty()) "Get Started: Add Word List" else "Weekly Spelling Challenge",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Daily Challenge Pill Tag (bg-[#FFD8E4], text-[#31111D])
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(BentoBadgeContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (activeWords.isNotEmpty()) "DAILY CHALLENGE" else "SETUP LIST",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BentoOnBadge,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (featuredWord != null) {
                        // Letter Tiles Breakdown / Mystery Word Slots
                        val letters = featuredWord.word.uppercase().toList()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            letters.forEachIndexed { index, char ->
                                val isHidden = index == 1 && letters.size > 3
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f, fill = false)
                                        .size(width = 34.dp, height = 42.dp)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(if (isHidden) BentoBorderSubtle else Color.Transparent)
                                        .drawBottomBorder(4.dp, BentoPrimary)
                                ) {
                                    Text(
                                        text = if (isHidden) "?" else char.toString(),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isHidden) BentoPrimary.copy(alpha = 0.6f) else BentoTextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Example Sentence Quote
                        Text(
                            text = "\"${featuredWord.displaySentence}\"",
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            color = BentoTextSecondary,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Phonics Tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Phonics: ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextSecondary
                            )
                            Text(
                                text = featuredWord.displayPhonics,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BentoPrimary
                            )
                        }
                    } else {
                        Text(
                            text = "Welcome to SpellQuest! Enter the spelling words assigned to your child to begin. Multi-sensory games and spaced repetition practice will focus strictly on these required words.",
                            fontSize = 13.sp,
                            color = BentoTextSecondary,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bento CTA Button
                    if (activeWords.isNotEmpty()) {
                        Button(
                            onClick = onNavigateToListenSpell,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BentoPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("bento_hero_listen_spell_btn")
                        ) {
                            Icon(Icons.Default.Headphones, contentDescription = "Listen & Spell", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Listen & Spell",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { showPhotoScanDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BentoPrimary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("bento_hero_scan_words_btn")
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Snap Photo of Word List",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = { showQuickAddDialog = true },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("bento_hero_add_words_btn")
                            ) {
                                Text("📝", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Type or Paste Words",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            OutlinedButton(
                                onClick = { showParentGate = true },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("bento_hero_parent_zone_btn")
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = "Parent Zone", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Open Parent Hub",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Split Bento Row: WEEKLY WORDS (bg-[#F3EDF7]) vs BADGES EARNED (bg-[#FFD8E4])
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Bento Tile: WEEKLY WORDS (bg-[#F3EDF7], border border-[#E7E0EC], rounded-3xl)
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(170.dp)
                        .clickable { onNavigateToLscwc() }
                        .testTag("bento_weekly_words_tile")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "WEEKLY WORDS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoPrimary,
                            letterSpacing = 0.8.sp
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            if (activeWords.isEmpty()) {
                                Text(
                                    text = "No words added yet",
                                    fontSize = 11.sp,
                                    color = BentoTextSecondary
                                )
                            } else {
                                activeWords.take(4).forEach { word ->
                                    val isWordMastered = word.isMastered || word.boxLevel >= 3
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (isWordMastered) "✅" else "⏳",
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = word.word.replaceFirstChar { it.uppercase() },
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isWordMastered) BentoTextPrimary else BentoTextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        // Mini progress bar
                        Column {
                            LinearProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = BentoPrimary,
                                trackColor = BentoBorderSubtle
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$masteredCount / ${activeWords.size} Mastered",
                                fontSize = 9.sp,
                                color = BentoTextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Right Bento Tile: BADGES EARNED (bg-[#FFD8E4], rounded-3xl)
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoBadgeContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(170.dp)
                        .clickable { onNavigateToBadges() }
                        .testTag("bento_badges_tile")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "BADGES EARNED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoOnBadge,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        // 2x2 Grid of Badge Discs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.6f))
                            ) {
                                Text("⭐", fontSize = 18.sp)
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.6f))
                            ) {
                                Text("📚", fontSize = 18.sp)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (userStats.totalWordsMastered >= 10) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.25f))
                            ) {
                                Text(
                                    text = "🏆",
                                    fontSize = 18.sp,
                                    modifier = if (userStats.totalWordsMastered < 10) Modifier.alpha(0.35f) else Modifier
                                )
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (userStats.currentStreakDays >= 7) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.25f))
                            ) {
                                Text(
                                    text = "🚀",
                                    fontSize = 18.sp,
                                    modifier = if (userStats.currentStreakDays < 7) Modifier.alpha(0.35f) else Modifier
                                )
                            }
                        }

                        Text(
                            text = "Next: ${userStats.currentLevelTitle}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoOnBadge,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Practice Modes Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PRACTICE & LESSON MODES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "6 Learning Modes",
                    fontSize = 11.sp,
                    color = BentoTextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Spotlight Mode: Parent-Guided Spelling Practice
            BentoGameModeCard(
                title = "Parent-Guided Practice 🤝",
                subtitle = "Parent picks the word & audio is sent to child; child attempts spelling & parent checks!",
                badgeText = "Parent + Child",
                icon = Icons.Default.RecordVoiceOver,
                accentColor = BentoPrimary,
                containerBg = BentoStreakContainer.copy(alpha = 0.5f),
                onClick = onNavigateToParentGuided,
                testTag = "mode_parent_guided"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Mode 1: Look, Say, Cover, Write, Check (LSCWC)
            BentoGameModeCard(
                title = "Look, Say, Cover, Write & Check",
                subtitle = "The 4-step memory method for lasting spelling retention",
                badgeText = "Learning Standard",
                icon = Icons.Default.Visibility,
                accentColor = BentoPrimary,
                containerBg = BentoSurface,
                onClick = onNavigateToLscwc,
                testTag = "mode_lscwc"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Mode 2: Listen & Spell (Auditory Recall)
            BentoGameModeCard(
                title = "Listen & Spell",
                subtitle = "Hear words spoken aloud and build with tactile letter tiles",
                badgeText = "Auditory Recall",
                icon = Icons.Default.Headphones,
                accentColor = SkyAccent,
                containerBg = BentoSurface,
                onClick = onNavigateToListenSpell,
                testTag = "mode_listen_spell"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Mode 3: Scramble Quest
            BentoGameModeCard(
                title = "Scramble Quest",
                subtitle = "Untangle shuffled letter anagrams into the correct word",
                badgeText = "Tile Puzzle",
                icon = Icons.Default.Shuffle,
                accentColor = RoseTertiary,
                containerBg = BentoSurface,
                onClick = onNavigateToScramble,
                testTag = "mode_scramble"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Mode 4: Vowel Buster
            BentoGameModeCard(
                title = "Vowel Buster & Phonics",
                subtitle = "Master tricky vowel teams, dipthongs, and silent letters",
                badgeText = "Phonics",
                icon = Icons.Default.Extension,
                accentColor = VioletMagic,
                containerBg = BentoSurface,
                onClick = onNavigateToVowels,
                testTag = "mode_vowel_buster"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Mode 5: Spelling Bee Boss Challenge
            BentoGameModeCard(
                title = "Weekly Spelling Bee 🐝",
                subtitle = "Boss challenge! 3 hearts to prove your weekly mastery & earn a crown",
                badgeText = "Weekly Test",
                icon = Icons.Default.EmojiEvents,
                accentColor = AmberSecondary,
                containerBg = BentoSurface,
                onClick = onNavigateToSpellingBee,
                testTag = "mode_spelling_bee"
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 7. Word Retention List Bento Section
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📖 Word Retention List (${activeWords.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                        if (activeWords.isNotEmpty()) {
                            Text(
                                text = "Tap to Pronounce",
                                fontSize = 11.sp,
                                color = BentoPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (activeWords.isEmpty()) {
                        Text(
                            text = "No words in this week's list yet. Tap \"Enter Required Spelling Words\" above or open the Parent Hub to add your child's weekly words!",
                            fontSize = 12.sp,
                            color = BentoTextSecondary,
                            lineHeight = 16.sp
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(activeWords) { word ->
                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                                    modifier = Modifier.width(150.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = word.word.uppercase(),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BentoPrimary
                                            )
                                            IconButton(
                                                onClick = { ttsHelper.speakWord(word.word) },
                                                modifier = Modifier.size(26.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.VolumeUp,
                                                    contentDescription = "Hear",
                                                    tint = BentoPrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = word.displayPhonics,
                                            fontSize = 11.sp,
                                            color = BentoTextSecondary,
                                            fontWeight = FontWeight.Medium
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        val levelText = when {
                                            word.isMastered || word.boxLevel >= 3 -> "⭐ Mastered"
                                            word.boxLevel == 2 -> "🚀 Practicing"
                                            word.boxLevel == 1 -> "🌱 Learning"
                                            else -> "✨ New"
                                        }
                                        Text(
                                            text = levelText,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (word.isMastered || word.boxLevel >= 3) EmeraldSuccessText else BentoPrimary
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Daily Streak Practice Indicator for this word
                                        val practicedToday = word.isPracticedToday()
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (practicedToday) Color(0xFFD1E7DD) else Color(0xFFFFF3CD),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = if (practicedToday) "✓ Practiced Today" else "⏳ Needs Practice",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (practicedToday) Color(0xFF0F5132) else Color(0xFF664D03),
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    if (showParentGate) {
        ParentGateDialog(
            parentPin = userStats.parentPin,
            onDismiss = { showParentGate = false },
            onSuccess = {
                showParentGate = false
                onNavigateToParentZone()
            }
        )
    }

    if (showQuickAddDialog) {
        var wordsInput by remember { mutableStateOf("") }
        val parsedCount = wordsInput.split(Regex("[\n\r,;]+"))
            .map { it.replace(Regex("^[0-9]+[.)\\s-]+\\s*"), "").trim() }
            .filter { it.isNotBlank() && it.length > 1 }
            .distinct()
            .size

        AlertDialog(
            onDismissRequest = { showQuickAddDialog = false },
            containerColor = BentoSurface,
            titleContentColor = BentoTextPrimary,
            textContentColor = BentoTextSecondary,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📝", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enter Required Spelling Words", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = BentoTextPrimary)
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Paste or type words from your child's weekly homework or school list (separated by commas, lines, or spaces):",
                        fontSize = 13.sp,
                        color = BentoTextSecondary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            showQuickAddDialog = false
                            showPhotoScanDialog = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("quick_add_switch_to_photo_btn")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = BentoPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📸 Snap Picture of List Instead", fontSize = 12.sp, color = BentoPrimary, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = wordsInput,
                        onValueChange = { wordsInput = it },
                        textStyle = highContrastInputTextStyle,
                        placeholder = {
                            Text(
                                text = "1. because\n2. friend\n3. laugh\n4. enough\n5. caught",
                                color = BentoTextSecondary,
                                lineHeight = 20.sp
                            )
                        },
                        colors = highContrastTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("quick_add_words_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (parsedCount > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "✓ $parsedCount words detected",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccessText
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (wordsInput.isNotBlank()) {
                            onAddRequiredWords(wordsInput)
                            showQuickAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    enabled = parsedCount > 0
                ) {
                    Text("Add Words", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuickAddDialog = false }) {
                    Text("Cancel", color = BentoTextSecondary)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showPhotoScanDialog) {
        PhotoScanDialog(
            onDismiss = { showPhotoScanDialog = false },
            onWordsAutoAdded = { words ->
                if (words.isNotEmpty()) {
                    onAddRequiredWords(words.joinToString("\n"))
                }
            }
        )
    }

    if (showSuperpowerDialog) {
        val currentPower = SpellerSuperpowers.getByIdOrTitle(userStats.spellerSuperpower)
        AlertDialog(
            onDismissRequest = { showSuperpowerDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚡ Speller Superpower", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = BentoPrimaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(currentPower.emoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = currentPower.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoPrimary
                                    )
                                    Text(
                                        text = currentPower.tagline,
                                        fontSize = 12.sp,
                                        color = BentoTextSecondary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = currentPower.description,
                                fontSize = 12.sp,
                                color = BentoTextPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "🧠 Learning Strategy:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Text(
                        text = currentPower.learningStrategy,
                        fontSize = 12.sp,
                        color = BentoTextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "🏆 Recommended Practice Arena:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Text(
                        text = currentPower.recommendedArena,
                        fontSize = 12.sp,
                        color = EmeraldSuccessText,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Other Speller Superpowers:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    SpellerSuperpowers.ALL.filter { it.id != currentPower.id }.forEach { other ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BentoSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(other.emoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(other.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                                    Text(other.tagline, fontSize = 10.sp, color = BentoTextSecondary)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSuperpowerDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary)
                ) {
                    Text("Awesome! 🌟", color = Color.White)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showStreakDialog) {
        AlertDialog(
            onDismissRequest = { showStreakDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥 Daily Fire Streak Rules", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "To build lasting spelling memory, you must practice EVERY word in your week's list each day to earn your daily streak.",
                        fontSize = 13.sp,
                        color = BentoTextPrimary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDailyStreakEarnedToday) Color(0xFFD1E7DD) else Color(0xFFFFF3CD),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isDailyStreakEarnedToday) "🎉 Streak Earned Today!" else "⏳ Streak In Progress",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isDailyStreakEarnedToday) Color(0xFF0F5132) else Color(0xFF664D03)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$wordsPracticedTodayCount of $totalActiveWords words practiced today.",
                                fontSize = 12.sp,
                                color = if (isDailyStreakEarnedToday) Color(0xFF0F5132) else Color(0xFF664D03)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Words in This Week's List:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (activeWords.isEmpty()) {
                        Text("No words in active list. Add words to start your streak!", fontSize = 11.sp, color = BentoTextSecondary)
                    } else {
                        activeWords.forEach { word ->
                            val practiced = word.isPracticedToday()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = word.word.uppercase(),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BentoTextPrimary
                                )
                                Text(
                                    text = if (practiced) "✓ Practiced" else "⏳ Needs Practice",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (practiced) Color(0xFF0F5132) else Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showStreakDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary)
                ) {
                    Text("Got It! 🔥", color = Color.White)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun BentoGameModeCard(
    title: String,
    subtitle: String,
    badgeText: String,
    icon: ImageVector,
    accentColor: Color,
    containerBg: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accentColor.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = BentoTextSecondary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun BentoBottomNavigationBar(
    selectedIndex: Int,
    onHomeClick: () -> Unit,
    onPracticeClick: () -> Unit,
    onStatsClick: () -> Unit,
    onParentClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BentoSurfaceVariant)
            .border(
                width = 1.dp,
                color = BentoBorder,
                shape = RoundedCornerShape(0.dp)
            )
            .padding(WindowInsets.navigationBars.asPaddingValues())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home
        BentoNavItem(
            label = "Home",
            emoji = "🏠",
            isSelected = selectedIndex == 0,
            onClick = onHomeClick,
            testTag = "bento_nav_home"
        )

        // Practice
        BentoNavItem(
            label = "Practice",
            emoji = "📔",
            isSelected = selectedIndex == 1,
            onClick = onPracticeClick,
            testTag = "bento_nav_practice"
        )

        // Stats / Badges
        BentoNavItem(
            label = "Stats",
            emoji = "📊",
            isSelected = selectedIndex == 2,
            onClick = onStatsClick,
            testTag = "bento_nav_stats"
        )

        // Parent
        BentoNavItem(
            label = "Parent",
            emoji = "⚙️",
            isSelected = selectedIndex == 3,
            onClick = onParentClick,
            testTag = "bento_nav_parent"
        )
    }
}

@Composable
fun BentoNavItem(
    label: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .heightIn(min = 48.dp)
            .testTag(testTag)
    ) {
        if (isSelected) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(BentoPrimaryContainer)
                    .padding(horizontal = 18.dp, vertical = 4.dp)
            ) {
                Text(emoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = BentoOnPrimaryContainer
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = emoji,
                    fontSize = 18.sp,
                    color = BentoTextSecondary.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = BentoTextSecondary.copy(alpha = 0.7f)
            )
        }
    }
}

fun Modifier.drawBottomBorder(strokeWidth: Dp, color: Color): Modifier = this.then(
    Modifier.drawBehind {
        val widthPx = strokeWidth.toPx()
        drawLine(
            color = color,
            start = Offset(0f, size.height - widthPx / 2),
            end = Offset(size.width, size.height - widthPx / 2),
            strokeWidth = widthPx
        )
    }
)
