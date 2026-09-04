package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.BentoBadgeContainer
import com.example.ui.theme.BentoOnBadge
import com.example.ui.theme.CoralError
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.util.ErrorCategory
import com.example.util.OrthographicAnalysis

@Composable
fun DiagnosticCard(
    analysis: OrthographicAnalysis,
    modifier: Modifier = Modifier
) {
    if (analysis.isCorrect) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldSuccess.copy(alpha = 0.12f)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldSuccess.copy(alpha = 0.35f)),
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(EmeraldSuccess)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = Color.White, modifier = Modifier.size(26.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Orthographic Mapping Complete! ⭐",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = EmeraldSuccess
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Clean speech-to-print bonded in memory.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        return
    }

    val category = analysis.category ?: ErrorCategory.MINOR_SLIP
    val badgeBg = when (category) {
        ErrorCategory.PHONETIC_PLAUSIBLE -> BentoBadgeContainer
        ErrorCategory.DYSLEXIC_REVERSAL -> Color(0xFFFFE0B2) // Warm amber warning
        ErrorCategory.VOWEL_CONFUSION -> Color(0xFFE1BEE7) // Soft purple
        ErrorCategory.HEART_WORD_EXCEPTION -> Color(0xFFFFCDD2) // Soft rose
        ErrorCategory.MINOR_SLIP -> Color(0xFFE0F2FE) // Soft sky
        ErrorCategory.OMISSION_ADDITION -> Color(0xFFFFF9C4) // Soft lemon
    }

    val badgeText = when (category) {
        ErrorCategory.PHONETIC_PLAUSIBLE -> BentoOnBadge
        ErrorCategory.DYSLEXIC_REVERSAL -> Color(0xFFE65100)
        ErrorCategory.VOWEL_CONFUSION -> Color(0xFF4A148C)
        ErrorCategory.HEART_WORD_EXCEPTION -> Color(0xFFB71C1C)
        ErrorCategory.MINOR_SLIP -> Color(0xFF075985)
        ErrorCategory.OMISSION_ADDITION -> Color(0xFFF57F17)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, badgeText.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Category Pill Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(badgeBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = category.badge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeText
                    )
                }
                Text(
                    text = "Pedagogical Diagnostic",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Diagnostic Summary
            Text(
                text = category.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = analysis.diagnosticSummary,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Actionable Parent / Phonics Cue Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = "Tip",
                        tint = AmberSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Next Practice Action:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = analysis.phonicsTip,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
