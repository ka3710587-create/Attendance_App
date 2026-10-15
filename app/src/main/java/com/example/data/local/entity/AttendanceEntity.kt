package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance",
    indices = [
        Index(value = ["studentId", "date"], unique = true),
        Index(value = ["department", "year", "date"])
    ]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val rollNo: String,
    val studentName: String,
    val department: String,
    val year: Int,
    val date: String,            // YYYY-MM-DD
    val status: String,          // PRESENT, ABSENT, LATE, LEAVE
    val remark: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
