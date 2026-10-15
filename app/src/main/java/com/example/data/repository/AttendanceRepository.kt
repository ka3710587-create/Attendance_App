package com.example.data.repository

import android.content.Context
import com.example.data.local.Department
import com.example.data.local.StudyYear
import com.example.data.local.dao.AttendanceDao
import com.example.data.local.entity.AttendanceEntity
import com.example.data.local.entity.NotificationLogEntity
import com.example.data.local.entity.StudentEntity
import com.example.util.NotificationHelper
import com.example.util.SmsHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class StudentMonthlySummary(
    val student: StudentEntity,
    val totalSessions: Int,
    val presentCount: Int,
    val absentCount: Int,
    val lateCount: Int,
    val leaveCount: Int,
    val attendancePercentage: Double,
    val isDefaulter: Boolean
)

data class ClassMonthlyStats(
    val department: Department,
    val year: StudyYear,
    val monthPrefix: String,
    val totalStudents: Int,
    val totalWorkingDays: Int,
    val averageAttendancePercentage: Double,
    val defaulterCount: Int,
    val studentSummaries: List<StudentMonthlySummary>
)

class AttendanceRepository(
    private val dao: AttendanceDao,
    private val appContext: Context
) {

    fun getAllStudents(): Flow<List<StudentEntity>> = dao.getAllStudents()

    fun getStudentsByClass(department: Department, year: StudyYear): Flow<List<StudentEntity>> =
        dao.getStudentsByClass(department.code, year.yearNumber)

    fun getStudentById(id: Long): Flow<StudentEntity?> = dao.getStudentById(id)

    fun getStudentByRollNo(rollNo: String): Flow<StudentEntity?> = dao.getStudentByRollNo(rollNo)

    fun searchStudents(query: String): Flow<List<StudentEntity>> = dao.searchStudents(query)

    suspend fun addStudent(student: StudentEntity): Long = dao.insertStudent(student)

    suspend fun updateStudent(student: StudentEntity) = dao.updateStudent(student)

    suspend fun deleteStudent(student: StudentEntity) = dao.deleteStudent(student)

    fun getAttendanceForClassAndDate(
        department: Department,
        year: StudyYear,
        date: String
    ): Flow<List<AttendanceEntity>> =
        dao.getAttendanceForClassAndDate(department.code, year.yearNumber, date)

    fun getAttendanceForDate(date: String): Flow<List<AttendanceEntity>> =
        dao.getAttendanceForDate(date)

    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceEntity>> =
        dao.getAttendanceForStudent(studentId)

    fun getAttendanceForStudentInMonth(studentId: Long, monthPrefix: String): Flow<List<AttendanceEntity>> =
        dao.getAttendanceForStudentInMonth(studentId, monthPrefix)

    suspend fun saveClassAttendance(
        records: List<AttendanceEntity>,
        sendAutomatedAlerts: Boolean,
        department: Department,
        year: StudyYear,
        date: String
    ) {
        dao.insertOrUpdateAttendance(records)

        val absentees = records.filter { it.status == "ABSENT" }
        if (absentees.isNotEmpty() && sendAutomatedAlerts) {
            val names = absentees.joinToString(", ") { "${it.studentName} (${it.rollNo})" }
            val classTitle = "${department.shortName} ${year.label}"

            // 1. Issue push alert
            NotificationHelper.sendAbsencePushAlert(
                appContext,
                classTitle = classTitle,
                absentCount = absentees.size,
                absenteeSummary = names
            )

            // 2. Queue & record simulated parent SMS logs
            val logs = absentees.map { absentee ->
                val student = dao.getStudentByIdDirect(absentee.studentId)
                val phone = student?.guardianPhone ?: "+923459200000"
                val message = SmsHelper.generateAbsenteeSms(
                    studentName = absentee.studentName,
                    rollNo = absentee.rollNo,
                    departmentTitle = department.title,
                    yearLabel = year.label,
                    date = date
                )

                NotificationLogEntity(
                    studentId = absentee.studentId,
                    studentName = absentee.studentName,
                    rollNo = absentee.rollNo,
                    department = department.code,
                    year = year.yearNumber,
                    recipientPhone = phone,
                    messageText = message,
                    channelType = "SMS",
                    status = "DELIVERED",
                    date = date
                )
            }
            dao.insertNotificationLogs(logs)
        }
    }

    suspend fun generateMonthlyReport(
        department: Department,
        year: StudyYear,
        monthPrefix: String
    ): ClassMonthlyStats {
        val students = dao.getStudentsByClass(department.code, year.yearNumber).firstOrNull() ?: emptyList()
        val allMonthAttendance = dao.getAttendanceForClassInMonth(department.code, year.yearNumber, monthPrefix).firstOrNull() ?: emptyList()

        val uniqueDates = allMonthAttendance.map { it.date }.distinct()
        val totalWorkingDays = uniqueDates.size

        val summaries = students.map { student ->
            val studentRecords = allMonthAttendance.filter { it.studentId == student.id }
            val presentCount = studentRecords.count { it.status == "PRESENT" }
            val lateCount = studentRecords.count { it.status == "LATE" }
            val leaveCount = studentRecords.count { it.status == "LEAVE" }
            val absentCount = studentRecords.count { it.status == "ABSENT" }

            // Weighted attendance: Present counts as 1.0, Late counts as 0.75, Leave as sanctioned
            val effectiveAttended = presentCount + (lateCount * 0.75)
            val totalHeld = if (totalWorkingDays > 0) totalWorkingDays else 1
            val percentage = (effectiveAttended / totalHeld.toDouble()) * 100.0
            val isDefaulter = percentage < 75.0 && totalWorkingDays >= 3

            StudentMonthlySummary(
                student = student,
                totalSessions = totalWorkingDays,
                presentCount = presentCount,
                absentCount = absentCount,
                lateCount = lateCount,
                leaveCount = leaveCount,
                attendancePercentage = percentage.coerceIn(0.0, 100.0),
                isDefaulter = isDefaulter
            )
        }

        val avgPercentage = if (summaries.isNotEmpty()) {
            summaries.map { it.attendancePercentage }.average()
        } else 0.0

        val defaulters = summaries.count { it.isDefaulter }

        return ClassMonthlyStats(
            department = department,
            year = year,
            monthPrefix = monthPrefix,
            totalStudents = students.size,
            totalWorkingDays = totalWorkingDays,
            averageAttendancePercentage = avgPercentage,
            defaulterCount = defaulters,
            studentSummaries = summaries
        )
    }

    fun getAllNotificationLogs(): Flow<List<NotificationLogEntity>> = dao.getAllNotificationLogs()

    suspend fun sendManualSmsAlert(
        student: StudentEntity,
        department: Department,
        year: StudyYear,
        customMessage: String? = null
    ) {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val message = customMessage ?: SmsHelper.generateAbsenteeSms(
            studentName = student.name,
            rollNo = student.rollNo,
            departmentTitle = department.title,
            yearLabel = year.label,
            date = today
        )

        val log = NotificationLogEntity(
            studentId = student.id,
            studentName = student.name,
            rollNo = student.rollNo,
            department = department.code,
            year = year.yearNumber,
            recipientPhone = student.guardianPhone,
            messageText = message,
            channelType = "SMS",
            status = "DELIVERED",
            date = today
        )
        dao.insertNotificationLog(log)
    }

    suspend fun clearLogs() {
        dao.clearAllLogs()
    }
}
