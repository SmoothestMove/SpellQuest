package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SkyAccent

@Composable
fun MasteryProgressBar(
    boxLevel: Int,
    isMastered: Boolean,
    modifier: Modifier = Modifier
) {
    val progress = when {
        isMastered || boxLevel >= 4 -> 1.0f
        boxLevel == 3 -> 0.85f
        boxLevel == 2 -> 0.6f
        boxLevel == 1 -> 0.3f
        else -> 0.05f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600),
        label = "mastery_progress"
    )

    val stageLabel = when {
        boxLevel >= 4 -> "👑 Champion (Box 4)"
        boxLevel == 3 || isMastered -> "⭐ Mastered (Box 3)"
        boxLevel == 2 -> "🚀 Practicing (Box 2)"
        boxLevel == 1 -> "🌱 Learning (Box 1)"
        else -> "✨ New Word"
    }

    val stageColor = when {
        boxLevel >= 4 -> AmberSecondary
        boxLevel == 3 || isMastered -> EmeraldSuccess
        boxLevel == 2 -> SkyAccent
        boxLevel == 1 -> IndigoPrimary
        else -> MaterialTheme.colorScheme.outline
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stageLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = stageColor
            )
            Text(
                text = "${(animatedProgress * 100).toInt()}% Retained",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                IndigoPrimary,
                                SkyAccent,
                                if (boxLevel >= 3 || isMastered) EmeraldSuccess else AmberSecondary
                            )
                        )
                    )
            )
        }
    }
}
