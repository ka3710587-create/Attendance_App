package com.example.data.local

enum class Department(val code: String, val title: String, val shortName: String) {
    CIVIL("CIVIL", "Civil Technology", "Civil"),
    ELECTRICAL("ELECTRICAL", "Electrical Technology", "Electrical"),
    CIT("CIT", "Computer Information Technology", "CIT");

    companion object {
        fun fromCode(code: String): Department = entries.find { it.code == code } ?: CIT
    }
}

enum class StudyYear(val yearNumber: Int, val label: String, val badge: String) {
    FIRST(1, "1st Year", "1st Yr"),
    SECOND(2, "2nd Year", "2nd Yr"),
    THIRD(3, "3rd Year", "3rd Yr");

    companion object {
        fun fromNumber(num: Int): StudyYear = entries.find { it.yearNumber == num } ?: FIRST
    }
}

enum class AttendanceStatus(val code: String, val label: String) {
    PRESENT("P", "Present"),
    ABSENT("A", "Absent"),
    LATE("L", "Late"),
    LEAVE("LV", "Leave");

    companion object {
        fun fromCode(code: String): AttendanceStatus = entries.find { it.code == code } ?: PRESENT
    }
}

enum class NotificationStatus {
    SENT,
    DELIVERED,
    FAILED
}

enum class PortalMode {
    FACULTY,
    STUDENT_PARENT
}

enum class NavDestination {
    DASHBOARD,
    TAKE_ATTENDANCE,
    MONTHLY_REPORT,
    STUDENT_PORTAL,
    SMS_ALERTS,
    ROSTER
}
