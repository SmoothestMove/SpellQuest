package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoBorderSubtle
import com.example.ui.theme.BentoOnPrimaryContainer
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.CoralError
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary

enum class TileStatus {
    DEFAULT,
    SELECTED,
    CORRECT,
    INCORRECT,
    BLANK
}

@Composable
fun LetterTile(
    letter: Char,
    status: TileStatus = TileStatus.DEFAULT,
    size: Dp = 52.dp,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (status == TileStatus.SELECTED) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "tile_scale"
    )

    val bgColor = when (status) {
        TileStatus.DEFAULT -> BentoSurface
        TileStatus.SELECTED -> BentoPrimaryContainer
        TileStatus.CORRECT -> EmeraldSuccess
        TileStatus.INCORRECT -> CoralError
        TileStatus.BLANK -> BentoSurfaceVariant
    }

    val textColor = when (status) {
        TileStatus.DEFAULT -> BentoTextPrimary
        TileStatus.SELECTED -> BentoOnPrimaryContainer
        TileStatus.CORRECT -> Color.White
        TileStatus.INCORRECT -> Color.White
        TileStatus.BLANK -> BentoTextSecondary
    }

    val borderColor = when (status) {
        TileStatus.DEFAULT -> BentoBorder
        TileStatus.SELECTED -> BentoPrimary
        TileStatus.CORRECT -> EmeraldSuccess
        TileStatus.INCORRECT -> CoralError
        TileStatus.BLANK -> BentoBorderSubtle
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .scale(scale)
            .shadow(
                elevation = if (status == TileStatus.BLANK) 0.dp else 4.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .then(
                if (onClick != null) {
                    Modifier
                        .clickable { onClick() }
                        .testTag("letter_tile_${letter.uppercaseChar()}")
                } else Modifier
            )
    ) {
        Text(
            text = if (letter == ' ') "_" else letter.uppercaseChar().toString(),
            fontSize = (size.value * 0.45f).sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
