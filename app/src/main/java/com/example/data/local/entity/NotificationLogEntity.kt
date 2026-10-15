package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_logs")
data class NotificationLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val rollNo: String,
    val department: String,
    val year: Int,
    val recipientPhone: String,
    val messageText: String,
    val channelType: String,      // "SMS" or "PUSH"
    val status: String,           // "SENT", "DELIVERED", "FAILED"
    val date: String,             // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis()
)
