package com.example.ui.screens.parent

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import com.example.ui.components.PhotoScanDialog
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SpellingWord
import com.example.data.model.UserStats
import com.example.data.model.WeeklyList
import com.example.util.ErrorCategory
import com.example.util.SpellingDiagnostics
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
import com.example.ui.theme.CoralError
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.EmeraldSuccessText
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SkyAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentPortalScreen(
    weeklyLists: List<WeeklyList>,
    activeList: WeeklyList?,
    activeWords: List<SpellingWord>,
    trickyWords: List<SpellingWord>,
    userStats: UserStats,
    onSetActiveList: (Long) -> Unit,
    onCreateWeeklyList: (String, String, Int, String) -> Unit,
    onDeleteWeeklyList: (Long) -> Unit,
    onAddWord: (Long, String, String, String, String, String) -> Unit,
    onDeleteWord: (Long) -> Unit,
    onBulkImport: (Long, String) -> Unit,
    onClearWordsInList: (Long) -> Unit = {},
    onUpdateProfile: (String, String) -> Unit,
    onUpdateParentalSettings: (pin: String, timeLimit: Int, wordGoal: Int) -> Unit = { _, _, _ -> },
    onReplayParentTour: () -> Unit = {},
    onReplayChildTour: () -> Unit = {},
    onNavigateToParentGuided: () -> Unit = {},
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Weekly Lists", "Words Manager", "Child Progress", "Settings & Tour")

    var showAddListDialog by remember { mutableStateOf(false) }
    var showAddWordDialog by remember { mutableStateOf(false) }
    var showBulkImportDialog by remember { mutableStateOf(false) }
    var showPhotoScanDialog by remember { mutableStateOf(false) }
    var showConfirmClearDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BentoBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Parent Hub", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("parent_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BentoPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { showProfileDialog = true }, modifier = Modifier.testTag("parent_profile_btn")) {
                        Icon(Icons.Default.Person, contentDescription = "Child Profile", tint = BentoPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BentoBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 12.dp,
                containerColor = BentoSurfaceVariant,
                contentColor = BentoPrimary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // TAB 0: Weekly Lists
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentPadding = PaddingValues(bottom = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = BentoStreakContainer),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToParentGuided() }
                                    .testTag("parent_portal_guided_banner")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("🤝", fontSize = 26.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Parent-Guided Practice Mode",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = BentoOnStreak
                                            )
                                            Text(
                                                text = "Pick words & send audio to your child to test live.",
                                                fontSize = 11.sp,
                                                color = BentoOnStreak.copy(alpha = 0.85f)
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = onNavigateToParentGuided,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary)
                                    ) {
                                        Text("Start 🚀", fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = { showAddListDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("parent_add_list_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add List")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Create New Weekly List", fontWeight = FontWeight.Bold)
                            }
                        }

                        items(weeklyLists) { list ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (list.isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                                ),
                                border = if (list.isActive) androidx.compose.foundation.BorderStroke(2.dp, IndigoPrimary) else null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = list.title,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (list.isActive) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(IndigoPrimary)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text("ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${list.gradeLevel} • ${list.description.ifBlank { "Custom spelling list" }}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    if (!list.isActive) {
                                        TextButton(
                                            onClick = { onSetActiveList(list.id) },
                                            modifier = Modifier.testTag("set_active_list_${list.id}")
                                        ) {
                                            Text("Set Active", color = IndigoPrimary, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (weeklyLists.size > 1) {
                                        IconButton(
                                            onClick = { onDeleteWeeklyList(list.id) },
                                            modifier = Modifier.testTag("delete_list_${list.id}")
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CoralError)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: Words Manager in Active List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentPadding = PaddingValues(bottom = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Managing: ${activeList?.title ?: "No active list"}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IndigoPrimary
                                    )
                                    Text(
                                        text = "${activeWords.size} words currently in this list",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showPhotoScanDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .testTag("parent_scan_photo_btn")
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = "Scan", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Scan Photo", fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = { showBulkImportDialog = true },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("parent_bulk_import_btn")
                                ) {
                                    Icon(Icons.Default.PlaylistAdd, contentDescription = "Bulk", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Paste", fontSize = 13.sp)
                                }

                                Button(
                                    onClick = { showAddWordDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(0.9f)
                                        .testTag("parent_add_single_word_btn")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add", fontSize = 13.sp)
                                }

                                if (activeWords.isNotEmpty() && activeList != null) {
                                    IconButton(
                                        onClick = { showConfirmClearDialog = true },
                                        modifier = Modifier.testTag("parent_clear_words_btn")
                                    ) {
                                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All Words", tint = CoralError)
                                    }
                                }
                            }
                        }

                        if (activeWords.isEmpty()) {
                            item {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("📝", fontSize = 36.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "No words in this list yet",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoTextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Take a picture of your child's weekly spelling list or paste the words.",
                                            fontSize = 13.sp,
                                            color = BentoTextSecondary,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Button(
                                                onClick = { showPhotoScanDialog = true },
                                                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.testTag("parent_empty_scan_btn")
                                            ) {
                                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Scan Photo")
                                            }

                                            OutlinedButton(
                                                onClick = { showBulkImportDialog = true },
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.testTag("parent_empty_paste_btn")
                                            ) {
                                                Icon(Icons.Default.PlaylistAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Paste Words")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        items(activeWords) { word ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = word.word.uppercase(),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = IndigoPrimary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Phonics: ${word.displayPhonics}",
                                                fontSize = 12.sp,
                                                color = AmberSecondary
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Sentence: \"${word.displaySentence}\"",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Box Level: ${word.boxLevel} • Correct: ${word.correctAttempts} • Errors: ${word.incorrectAttempts}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (word.isMastered) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteWord(word.id) },
                                        modifier = Modifier.testTag("delete_word_${word.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CoralError)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: Child Progress & Pedagogical Analytics
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentPadding = PaddingValues(bottom = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = IndigoPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Child: ${userStats.studentName}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Level ${userStats.level} • ${userStats.currentLevelTitle}",
                                        fontSize = 14.sp,
                                        color = AmberSecondary
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "${userStats.totalXp}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                            Text(text = "Total XP ⭐", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "${userStats.currentStreakDays}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                            Text(text = "Day Streak 🔥", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "${userStats.totalWordsMastered}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                            Text(text = "Mastered 👑", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "⚠️ Words Needing Practice (Tricky Words)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (trickyWords.isEmpty()) {
                            item {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(modifier = Modifier.padding(16.dp)) {
                                        Text("Great job! No tricky words detected right now. All words are on track!", fontSize = 13.sp)
                                    }
                                }
                            }
                        } else {
                            items(trickyWords) { word ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CoralError.copy(alpha = 0.3f)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                                            Text(
                                                text = word.word.uppercase(),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = CoralError,
                                                letterSpacing = 1.sp
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFFFFEBEE))
                                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = "${word.incorrectAttempts} Errors • Box ${word.boxLevel}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CoralError
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Phoneme Map: ${word.displayPhonics}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        if (word.hint.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Orthographic Clue: ${word.hint}",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Diagnostic Recommendation
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = "💡 Practice in Look-Say-Cover-Write-Check or Parent-Guided to anchor orthographic mapping.",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // TAB 3: Parent Settings & Tour
                    ParentSettingsAndTourTab(
                        userStats = userStats,
                        onUpdateSettings = onUpdateParentalSettings,
                        onReplayParentTour = onReplayParentTour,
                        onReplayChildTour = onReplayChildTour
                    )
                }
            }
        }
    }

    // Dialog: Confirm Clear Words
    if (showConfirmClearDialog && activeList != null) {
        AlertDialog(
            onDismissRequest = { showConfirmClearDialog = false },
            title = { Text("Clear All Words?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Are you sure you want to remove all ${activeWords.size} words from \"${activeList.title}\"? You can paste your child's new weekly list anytime.",
                    fontSize = 13.sp,
                    color = BentoTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearWordsInList(activeList.id)
                        showConfirmClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralError)
                ) {
                    Text("Clear Words", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmClearDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Dialog: Create New Weekly List
    if (showAddListDialog) {
        var listTitle by remember { mutableStateOf("") }
        var listDesc by remember { mutableStateOf("") }
        var gradeLevel by remember { mutableStateOf("Elementary") }

        AlertDialog(
            onDismissRequest = { showAddListDialog = false },
            containerColor = BentoSurface,
            titleContentColor = BentoTextPrimary,
            textContentColor = BentoTextSecondary,
            title = { Text("New Weekly Spelling List", fontWeight = FontWeight.Bold, color = BentoTextPrimary) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = listTitle,
                        onValueChange = { listTitle = it },
                        textStyle = highContrastInputTextStyle,
                        colors = highContrastTextFieldColors(),
                        label = { Text("List Title (e.g. Week 3: Science Words)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = listDesc,
                        onValueChange = { listDesc = it },
                        textStyle = highContrastInputTextStyle,
                        colors = highContrastTextFieldColors(),
                        label = { Text("Description or Note") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = gradeLevel,
                        onValueChange = { gradeLevel = it },
                        textStyle = highContrastInputTextStyle,
                        colors = highContrastTextFieldColors(),
                        label = { Text("Grade Level") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (listTitle.isNotBlank()) {
                            val weekNo = weeklyLists.size + 1
                            onCreateWeeklyList(listTitle.trim(), listDesc.trim(), weekNo, gradeLevel.trim())
                            showAddListDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Create List")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddListDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Dialog: Add Single Word
    if (showAddWordDialog && activeList != null) {
        var wordText by remember { mutableStateOf("") }
        var phonicsText by remember { mutableStateOf("") }
        var hintText by remember { mutableStateOf("") }
        var sentenceText by remember { mutableStateOf("") }
        var defText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddWordDialog = false },
            containerColor = BentoSurface,
            titleContentColor = BentoTextPrimary,
            textContentColor = BentoTextSecondary,
            title = { Text("Add Word to ${activeList.title}", fontWeight = FontWeight.Bold, color = BentoTextPrimary) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = wordText,
                        onValueChange = {
                            wordText = it
                            if (phonicsText.isBlank()) {
                                phonicsText = SpellingWord.breakdownPhonics(it)
                            }
                        },
                        textStyle = highContrastInputTextStyle,
                        colors = highContrastTextFieldColors(),
                        label = { Text("Spelling Word*") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phonicsText,
                        onValueChange = { phonicsText = it },
                        textStyle = highContrastInputTextStyle,
                        colors = highContrastTextFieldColors(),
                        label = { Text("Phonics Breakdown (e.g. be·cause)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = sentenceText,
                        onValueChange = { sentenceText = it },
                        textStyle = highContrastInputTextStyle,
                        colors = highContrastTextFieldColors(),
                        label = { Text("Example Sentence") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = hintText,
                        onValueChange = { hintText = it },
                        textStyle = highContrastInputTextStyle,
                        colors = highContrastTextFieldColors(),
                        label = { Text("Memory Trick or Hint") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (wordText.isNotBlank()) {
                            onAddWord(
                                activeList.id,
                                wordText.trim(),
                                phonicsText.trim(),
                                hintText.trim(),
                                sentenceText.trim(),
                                defText.trim()
                            )
                            showAddWordDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Add Word")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddWordDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Dialog: Bulk Import Words
    if (showBulkImportDialog && activeList != null) {
        var bulkText by remember { mutableStateOf("") }
        val parsedCount = bulkText.split(Regex("[\n\r,;]+"))
            .map { it.replace(Regex("^[0-9]+[.)\\s-]+\\s*"), "").trim() }
            .filter { it.isNotBlank() && it.length > 1 }
            .distinct()
            .size

        AlertDialog(
            onDismissRequest = { showBulkImportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📝", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Paste Required Spelling Words", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Paste or type words assigned to your child (separated by commas, lines, or spaces). Numbering (e.g. 1. 2.) and bullets will be automatically cleaned up:",
                        fontSize = 13.sp,
                        color = BentoTextSecondary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            showBulkImportDialog = false
                            showPhotoScanDialog = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("parent_switch_to_photo_btn")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = BentoPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📸 Snap Picture of List Instead", fontSize = 12.sp, color = BentoPrimary, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bulkText,
                        onValueChange = { bulkText = it },
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
                            .height(150.dp)
                            .testTag("bulk_words_input_field"),
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
                        if (bulkText.isNotBlank()) {
                            onBulkImport(activeList.id, bulkText)
                            showBulkImportDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = parsedCount > 0
                ) {
                    Text("Add Words to List")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkImportDialog = false }) {
                    Text("Cancel")
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
                    val targetListId = activeList?.id ?: weeklyLists.firstOrNull()?.id
                    if (targetListId != null) {
                        onBulkImport(targetListId, words.joinToString("\n"))
                    } else {
                        onCreateWeeklyList("This Week's Spelling List", "Scanned from photo", 1, "Elementary")
                    }
                }
            }
        )
    }

    // Dialog: Student Profile Editor
    if (showProfileDialog) {
        var studentName by remember { mutableStateOf(userStats.studentName) }
        var selectedAvatar by remember { mutableStateOf(userStats.currentAvatar) }
        val avatars = listOf("owl", "fox", "bear", "lion", "astro", "dragon", "robot", "unicorn", "star", "dolphin")

        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            containerColor = BentoSurface,
            titleContentColor = BentoTextPrimary,
            textContentColor = BentoTextSecondary,
            title = { Text("Child Profile", fontWeight = FontWeight.Bold, color = BentoTextPrimary) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        textStyle = highContrastInputTextStyle,
                        colors = highContrastTextFieldColors(),
                        label = { Text("Child's Name / Nickname") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Choose Mascot Avatar:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(avatars) { av ->
                            val emoji = when (av) {
                                "dragon" -> "🐲"
                                "fox" -> "🦊"
                                "astro", "astronaut" -> "🚀"
                                "robot" -> "🤖"
                                "bear" -> "🐻"
                                "lion" -> "🦁"
                                "unicorn" -> "🦄"
                                "star" -> "🌟"
                                "dolphin" -> "🐬"
                                else -> "🦉"
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedAvatar == av) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { selectedAvatar = av }
                            ) {
                                Text(emoji, fontSize = 22.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (studentName.isNotBlank()) {
                            onUpdateProfile(studentName.trim(), selectedAvatar)
                            showProfileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Save Profile")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun ParentSettingsAndTourTab(
    userStats: UserStats,
    onUpdateSettings: (pin: String, timeLimit: Int, wordGoal: Int) -> Unit,
    onReplayParentTour: () -> Unit,
    onReplayChildTour: () -> Unit
) {
    var pin by remember(userStats.parentPin) { mutableStateOf(userStats.parentPin) }
    var timeLimit by remember(userStats.dailyTimeLimitMinutes) { mutableIntStateOf(userStats.dailyTimeLimitMinutes) }
    var wordGoal by remember(userStats.dailyWordGoal) { mutableIntStateOf(userStats.dailyWordGoal) }
    var isEditingPin by remember { mutableStateOf(false) }

    val timeOptions = listOf(10, 15, 20, 30, 0)
    val wordOptions = listOf(5, 10, 15, 20)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Parental Dashboard & Settings ⚙️",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextPrimary
            )
            Text(
                text = "Configure access restrictions, usage limits, and review the guided tours.",
                fontSize = 13.sp,
                color = BentoTextSecondary
            )
        }

        // Section 1: Security Lock PIN
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
                            Icon(Icons.Default.Lock, contentDescription = "Lock", tint = BentoPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Parent Zone Security", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    if (pin.isNotBlank()) "Protected by 4-digit PIN ($pin)" else "Protected by Math Challenge Gate",
                                    fontSize = 12.sp,
                                    color = BentoTextSecondary
                                )
                            }
                        }
                        TextButton(
                            onClick = { isEditingPin = !isEditingPin },
                            modifier = Modifier.testTag("parent_toggle_pin_btn")
                        ) {
                            Text(if (isEditingPin) "Done" else "Change")
                        }
                    }

                    if (isEditingPin) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = pin,
                            onValueChange = {
                                if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                                    pin = it
                                    onUpdateSettings(it, timeLimit, wordGoal)
                                }
                            },
                            textStyle = highContrastInputTextStyle,
                            colors = highContrastTextFieldColors(),
                            label = { Text("4-digit PIN (leave blank for Math Gate)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (pin.isNotBlank()) {
                            TextButton(
                                onClick = {
                                    pin = ""
                                    onUpdateSettings("", timeLimit, wordGoal)
                                    isEditingPin = false
                                }
                            ) {
                                Text("Clear PIN (Use Math Gate)", color = CoralError, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Daily Screen Time Limit
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Daily Practice Time Target", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Keeps practice short and joyful without fatigue.", fontSize = 12.sp, color = BentoTextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        timeOptions.forEach { mins ->
                            val isSelected = timeLimit == mins
                            val label = if (mins == 0) "No Limit" else "${mins}m"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) BentoPrimary else BentoSurfaceVariant)
                                    .clickable {
                                        timeLimit = mins
                                        onUpdateSettings(pin, mins, wordGoal)
                                    }
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

        // Section 3: Daily Mastered Word Goal
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Daily Word Mastery Goal", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Number of words to practice or master per day.", fontSize = 12.sp, color = BentoTextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        wordOptions.forEach { count ->
                            val isSelected = wordGoal == count
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) BentoPrimary else BentoSurfaceVariant)
                                    .clickable {
                                        wordGoal = count
                                        onUpdateSettings(pin, timeLimit, count)
                                    }
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

        // Section 4: Guided Tour Replay
        item {
            Text(
                text = "Guided Tours 🧭",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextPrimary
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoPrimaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onReplayParentTour() }
                    .testTag("parent_replay_tour_btn")
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🧭", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Replay Parent Guided Tour", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BentoPrimary)
                        Text(
                            "Walk through the diagnostic tools, weekly list synchronization, and live co-learning modes.",
                            fontSize = 12.sp,
                            color = BentoTextSecondary
                        )
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tour", modifier = Modifier.size(16.dp))
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AmberSecondary.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberSecondary.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onReplayChildTour() }
                    .testTag("parent_preview_child_tour_btn")
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🌟", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Preview Child Gamification Tour", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AmberSecondary)
                        Text(
                            "Preview the exciting Star XP, Fire Streaks, 12 Badges, and 5 game arenas shown to your child.",
                            fontSize = 12.sp,
                            color = BentoTextSecondary
                        )
                    }
                }
            }
        }
    }
}
