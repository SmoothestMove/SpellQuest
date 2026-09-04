package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_lists")
data class WeeklyList(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val weekNumber: Int = 1,
    val isActive: Boolean = false,
    val gradeLevel: String = "Elementary",
    val createdTimestamp: Long = System.currentTimeMillis()
)
