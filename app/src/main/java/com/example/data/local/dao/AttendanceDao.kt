package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AttendanceEntity
import com.example.data.local.entity.NotificationLogEntity
import com.example.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    // --- Students ---
    @Query("SELECT * FROM students ORDER BY department ASC, year ASC, rollNo ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE department = :dept AND year = :year ORDER BY rollNo ASC")
    fun getStudentsByClass(dept: String, year: Int): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentByIdDirect(id: Long): StudentEntity?

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    fun getStudentById(id: Long): Flow<StudentEntity?>

    @Query("SELECT * FROM students WHERE LOWER(rollNo) = LOWER(:rollNo) LIMIT 1")
    fun getStudentByRollNo(rollNo: String): Flow<StudentEntity?>

    @Query("SELECT * FROM students WHERE name LIKE '%' || :query || '%' OR rollNo LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchStudents(query: String): Flow<List<StudentEntity>>

    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    // --- Attendance ---
    @Query("SELECT * FROM attendance WHERE department = :dept AND year = :year AND date = :date")
    fun getAttendanceForClassAndDate(dept: String, year: Int, date: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE date = :date")
    fun getAttendanceForDate(date: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId AND date LIKE :monthPrefix || '%' ORDER BY date ASC")
    fun getAttendanceForStudentInMonth(studentId: Long, monthPrefix: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE department = :dept AND year = :year AND date LIKE :monthPrefix || '%' ORDER BY date ASC, rollNo ASC")
    fun getAttendanceForClassInMonth(dept: String, year: Int, monthPrefix: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance ORDER BY date DESC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttendance(records: List<AttendanceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecord(record: AttendanceEntity): Long

    // --- Notifications ---
    @Query("SELECT * FROM notification_logs ORDER BY timestamp DESC")
    fun getAllNotificationLogs(): Flow<List<NotificationLogEntity>>

    @Query("SELECT * FROM notification_logs WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getLogsForStudent(studentId: Long): Flow<List<NotificationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificationLog(log: NotificationLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificationLogs(logs: List<NotificationLogEntity>)

    @Query("DELETE FROM notification_logs")
    suspend fun clearAllLogs()
}
