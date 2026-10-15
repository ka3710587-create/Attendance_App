package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.AttendanceStatus
import com.example.data.local.Department
import com.example.data.local.NavDestination
import com.example.data.local.NotificationStatus
import com.example.data.local.PortalMode
import com.example.data.local.StudyYear
import com.example.data.local.entity.AttendanceEntity
import com.example.data.local.entity.NotificationLogEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.repository.AttendanceRepository
import com.example.data.repository.ClassMonthlyStats
import com.example.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AttendanceRepository

    init {
        val database = AppDatabase.getInstance(application)
        repository = AttendanceRepository(database.attendanceDao(), application.applicationContext)
        NotificationHelper.initNotificationChannels(application.applicationContext)
    }

    // Portal Mode
    private val _portalMode = MutableStateFlow(PortalMode.FACULTY)
    val portalMode: StateFlow<PortalMode> = _portalMode.asStateFlow()

    fun setPortalMode(mode: PortalMode) {
        _portalMode.value = mode
        if (mode == PortalMode.STUDENT_PARENT) {
            _currentNav.value = NavDestination.STUDENT_PORTAL
        } else {
            _currentNav.value = NavDestination.DASHBOARD
        }
    }

    // Navigation
    private val _currentNav = MutableStateFlow(NavDestination.DASHBOARD)
    val currentNav: StateFlow<NavDestination> = _currentNav.asStateFlow()

    fun navigateTo(destination: NavDestination) {
        _currentNav.value = destination
    }

    // Active Selection
    private val _selectedDepartment = MutableStateFlow(Department.CIT)
    val selectedDepartment: StateFlow<Department> = _selectedDepartment.asStateFlow()

    private val _selectedYear = MutableStateFlow(StudyYear.FIRST)
    val selectedYear: StateFlow<StudyYear> = _selectedYear.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    fun setDepartment(dept: Department) {
        _selectedDepartment.value = dept
        loadMonthlyReport()
    }

    fun setYear(year: StudyYear) {
        _selectedYear.value = year
        loadMonthlyReport()
    }

    fun setDate(date: String) {
        _selectedDate.value = date
    }

    // Enrolled Students in current class
    val classStudents: StateFlow<List<StudentEntity>> = combine(
        _selectedDepartment,
        _selectedYear
    ) { dept, year ->
        Pair(dept, year)
    }.flatMapLatest { (dept, year) ->
        repository.getStudentsByClass(dept, year)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Students across institute
    val allStudents: StateFlow<List<StudentEntity>> = repository.getAllStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's attendance records from database for current class
    val classAttendanceForDate: StateFlow<List<AttendanceEntity>> = combine(
        _selectedDepartment,
        _selectedYear,
        _selectedDate
    ) { dept, year, date ->
        Triple(dept, year, date)
    }.flatMapLatest { (dept, year, date) ->
        repository.getAttendanceForClassAndDate(dept, year, date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Editable draft attendance state for marking screen: StudentId -> AttendanceStatus
    private val _draftAttendance = MutableStateFlow<Map<Long, AttendanceStatus>>(emptyMap())
    val draftAttendance: StateFlow<Map<Long, AttendanceStatus>> = _draftAttendance.asStateFlow()

    // Sync draft attendance whenever database records change
    fun syncDraftWithSaved(students: List<StudentEntity>, saved: List<AttendanceEntity>) {
        val savedMap = saved.associate { it.studentId to AttendanceStatus.fromCode(it.status) }
        val newDraft = students.associate { student ->
            student.id to (savedMap[student.id] ?: AttendanceStatus.PRESENT)
        }
        _draftAttendance.value = newDraft
    }

    fun setStudentStatus(studentId: Long, status: AttendanceStatus) {
        val current = _draftAttendance.value.toMutableMap()
        current[studentId] = status
        _draftAttendance.value = current
    }

    fun markAllAs(status: AttendanceStatus, students: List<StudentEntity>) {
        val updated = students.associate { it.id to status }
        _draftAttendance.value = updated
    }

    // Save attendance & trigger alerts
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveSuccessEvent = MutableStateFlow<String?>(null)
    val saveSuccessEvent: StateFlow<String?> = _saveSuccessEvent.asStateFlow()

    fun clearSaveEvent() {
        _saveSuccessEvent.value = null
    }

    fun saveAttendance(sendAutomatedAlerts: Boolean) {
        val students = classStudents.value
        val draft = _draftAttendance.value
        val dept = _selectedDepartment.value
        val year = _selectedYear.value
        val date = _selectedDate.value

        if (students.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            _isSaving.value = true
            val records = students.map { student ->
                val status = draft[student.id] ?: AttendanceStatus.PRESENT
                AttendanceEntity(
                    studentId = student.id,
                    rollNo = student.rollNo,
                    studentName = student.name,
                    department = dept.code,
                    year = year.yearNumber,
                    date = date,
                    status = status.name,
                    remark = if (status == AttendanceStatus.ABSENT) "Absent on $date" else "",
                    timestamp = System.currentTimeMillis()
                )
            }

            repository.saveClassAttendance(
                records = records,
                sendAutomatedAlerts = sendAutomatedAlerts,
                department = dept,
                year = year,
                date = date
            )

            _isSaving.value = false
            _saveSuccessEvent.value = "Attendance saved successfully for ${dept.shortName} ${year.label}!"
            loadMonthlyReport()
        }
    }

    // Monthly Report
    private val _monthlyReportStats = MutableStateFlow<ClassMonthlyStats?>(null)
    val monthlyReportStats: StateFlow<ClassMonthlyStats?> = _monthlyReportStats.asStateFlow()

    private val _selectedMonthPrefix = MutableStateFlow(
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    )
    val selectedMonthPrefix: StateFlow<String> = _selectedMonthPrefix.asStateFlow()

    fun setMonthPrefix(monthPrefix: String) {
        _selectedMonthPrefix.value = monthPrefix
        loadMonthlyReport()
    }

    fun loadMonthlyReport() {
        viewModelScope.launch(Dispatchers.IO) {
            val stats = repository.generateMonthlyReport(
                department = _selectedDepartment.value,
                year = _selectedYear.value,
                monthPrefix = _selectedMonthPrefix.value
            )
            _monthlyReportStats.value = stats
        }
    }

    // Student & Parent Portal
    private val _selectedPortalStudent = MutableStateFlow<StudentEntity?>(null)
    val selectedPortalStudent: StateFlow<StudentEntity?> = _selectedPortalStudent.asStateFlow()

    private val _portalStudentHistory = MutableStateFlow<List<AttendanceEntity>>(emptyList())
    val portalStudentHistory: StateFlow<List<AttendanceEntity>> = _portalStudentHistory.asStateFlow()

    fun selectStudentForPortal(student: StudentEntity) {
        _selectedPortalStudent.value = student
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAttendanceForStudent(student.id).collect { history ->
                _portalStudentHistory.value = history
            }
        }
    }

    fun searchStudentByRoll(rollNo: String) {
        val found = allStudents.value.find { it.rollNo.equals(rollNo.trim(), ignoreCase = true) }
        if (found != null) {
            selectStudentForPortal(found)
        }
    }

    // Notification Logs
    val notificationLogs: StateFlow<List<NotificationLogEntity>> = repository.getAllNotificationLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearAllNotificationLogs() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearLogs()
        }
    }

    fun sendSingleSmsAlert(student: StudentEntity, customMsg: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val dept = Department.fromCode(student.department)
            val yr = StudyYear.fromNumber(student.year)
            repository.sendManualSmsAlert(student, dept, yr, customMsg)
        }
    }

    // Student Roster CRUD
    fun addNewStudent(
        name: String,
        rollNo: String,
        department: Department,
        year: StudyYear,
        guardianName: String,
        guardianPhone: String,
        email: String,
        bloodGroup: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newStudent = StudentEntity(
                name = name.trim(),
                rollNo = rollNo.trim().uppercase(),
                department = department.code,
                year = year.yearNumber,
                guardianName = guardianName.trim(),
                guardianPhone = guardianPhone.trim(),
                email = email.trim(),
                bloodGroup = bloodGroup.trim().ifEmpty { "B+" }
            )
            repository.addStudent(newStudent)
            loadMonthlyReport()
        }
    }

    fun deleteStudent(student: StudentEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteStudent(student)
            loadMonthlyReport()
        }
    }

    init {
        loadMonthlyReport()
    }
}
