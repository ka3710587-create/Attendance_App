package com.example

import com.example.data.local.AttendanceStatus
import com.example.data.local.Department
import com.example.data.local.SampleData
import com.example.data.local.StudyYear
import com.example.util.SmsHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AttendanceSystemTest {

    @Test
    fun testDepartmentAndYears() {
        assertEquals("Civil Technology", Department.CIVIL.title)
        assertEquals("Electrical Technology", Department.ELECTRICAL.title)
        assertEquals("Computer Information Technology", Department.CIT.title)

        assertEquals(3, Department.entries.size)
        assertEquals(3, StudyYear.entries.size)
    }

    @Test
    fun testSampleDataContainsAllDepartmentsAndYears() {
        val students = SampleData.getInitialStudents()
        assertTrue("Student list should not be empty", students.isNotEmpty())

        val depts = students.map { it.department }.distinct()
        assertTrue(depts.contains("CIT"))
        assertTrue(depts.contains("CIVIL"))
        assertTrue(depts.contains("ELECTRICAL"))

        val years = students.map { it.year }.distinct()
        assertTrue(years.contains(1))
        assertTrue(years.contains(2))
        assertTrue(years.contains(3))
    }

    @Test
    fun testDefaulterThresholdCalculation() {
        val totalSessions = 20
        val attended14 = 14
        val percentage = (attended14.toDouble() / totalSessions.toDouble()) * 100.0
        val isDefaulter = percentage < 75.0

        assertEquals(70.0, percentage, 0.01)
        assertTrue("70% must be marked as defaulter", isDefaulter)

        val attended16 = 16
        val percentageGood = (attended16.toDouble() / totalSessions.toDouble()) * 100.0
        assertEquals(80.0, percentageGood, 0.01)
        assertFalse("80% must not be marked as defaulter", percentageGood < 75.0)
    }

    @Test
    fun testSmsTemplateGeneration() {
        val sms = SmsHelper.generateAbsenteeSms(
            studentName = "Ali Raza",
            rollNo = "CIT-26-01",
            departmentTitle = "Computer Information Technology",
            yearLabel = "1st Year",
            date = "2026-10-15"
        )

        assertTrue(sms.contains("Govt. Polytechnical Institute Gilgit"))
        assertTrue(sms.contains("Ali Raza"))
        assertTrue(sms.contains("CIT-26-01"))
        assertTrue(sms.contains("ABSENT"))
    }
}
