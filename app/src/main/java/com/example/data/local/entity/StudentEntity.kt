package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rollNo: String,
    val name: String,
    val department: String, // CIVIL, ELECTRICAL, CIT
    val year: Int,          // 1, 2, 3
    val guardianName: String,
    val guardianPhone: String,
    val email: String = "",
    val bloodGroup: String = "B+",
    val avatarSeed: Int = 1
)
