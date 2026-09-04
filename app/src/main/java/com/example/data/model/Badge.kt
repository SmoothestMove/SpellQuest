package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val category: String = "general",
    val iconEmoji: String = "⭐",
    val isUnlocked: Boolean = false,
    val unlockedTimestamp: Long = 0L,
    val xpReward: Int = 50
)
