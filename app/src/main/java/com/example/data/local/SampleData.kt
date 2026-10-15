package com.example.data.local

import com.example.data.local.entity.AttendanceEntity
import com.example.data.local.entity.StudentEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object SampleData {

    fun getInitialStudents(): List<StudentEntity> {
        val list = mutableListOf<StudentEntity>()

        // Computer Information Technology (CIT) - 1st Year (2026 Batch)
        list.addAll(
            listOf(
                StudentEntity(1, "CIT-26-01", "Ali Raza", "CIT", 1, "Ghulam Muhammad", "+923459201101", "aliraza.cit@gpigilgit.edu.pk", "A+", 1),
                StudentEntity(2, "CIT-26-02", "Murtaza Hussain", "CIT", 1, "Fida Hussain", "+923459201102", "murtaza.cit@gpigilgit.edu.pk", "B+", 2),
                StudentEntity(3, "CIT-26-03", "Daniyal Karim", "CIT", 1, "Karim Ullah", "+923459201103", "daniyal.cit@gpigilgit.edu.pk", "O+", 3),
                StudentEntity(4, "CIT-26-04", "Fatima Zahra", "CIT", 1, "Sher Baz Khan", "+923459201104", "fatima.cit@gpigilgit.edu.pk", "B-", 4),
                StudentEntity(5, "CIT-26-05", "Asadullah Baig", "CIT", 1, "Nazir Baig", "+923459201105", "asadullah.cit@gpigilgit.edu.pk", "AB+", 5),
                StudentEntity(6, "CIT-26-06", "Waseem Akram", "CIT", 1, "Akram Jan", "+923459201106", "waseem.cit@gpigilgit.edu.pk", "O-", 6)
            )
        )

        // CIT - 2nd Year (2025 Batch)
        list.addAll(
            listOf(
                StudentEntity(7, "CIT-25-01", "Shahid Ali", "CIT", 2, "Ali Murad", "+923459201201", "shahid.cit@gpigilgit.edu.pk", "A+", 7),
                StudentEntity(8, "CIT-25-02", "Rehmat Ullah", "CIT", 2, "Barkat Shah", "+923459201202", "rehmat.cit@gpigilgit.edu.pk", "B+", 8),
                StudentEntity(9, "CIT-25-03", "Mehwish Bano", "CIT", 2, "Inayat Ali", "+923459201203", "mehwish.cit@gpigilgit.edu.pk", "O+", 9),
                StudentEntity(10, "CIT-25-04", "Zahid Mehdi", "CIT", 2, "Mehdi Hassan", "+923459201204", "zahid.cit@gpigilgit.edu.pk", "A-", 10),
                StudentEntity(11, "CIT-25-05", "Farhan Shafi", "CIT", 2, "Shafi Ullah", "+923459201205", "farhan.cit@gpigilgit.edu.pk", "B+", 11)
            )
        )

        // CIT - 3rd Year (2024 Batch)
        list.addAll(
            listOf(
                StudentEntity(12, "CIT-24-01", "Naveed Alam", "CIT", 3, "Alamgir Khan", "+923459201301", "naveed.cit@gpigilgit.edu.pk", "AB+", 12),
                StudentEntity(13, "CIT-24-02", "Babar Sher", "CIT", 3, "Sher Wali", "+923459201302", "babar.cit@gpigilgit.edu.pk", "O+", 13),
                StudentEntity(14, "CIT-24-03", "Syed Yasir Shah", "CIT", 3, "Syed Iqbal Shah", "+923459201303", "yasir.cit@gpigilgit.edu.pk", "B-", 14),
                StudentEntity(15, "CIT-24-04", "Tariq Aziz", "CIT", 3, "Azizullah Baig", "+923459201304", "tariq.cit@gpigilgit.edu.pk", "A+", 15)
            )
        )

        // Civil Technology - 1st Year (2026 Batch)
        list.addAll(
            listOf(
                StudentEntity(16, "CIV-26-01", "Irfan Haider", "CIVIL", 1, "Haider Ali", "+923459202101", "irfan.civ@gpigilgit.edu.pk", "O+", 16),
                StudentEntity(17, "CIV-26-02", "Rashid Minhas", "CIVIL", 1, "Minhas Khan", "+923459202102", "rashid.civ@gpigilgit.edu.pk", "B+", 17),
                StudentEntity(18, "CIV-26-03", "Jawad Ahmad", "CIVIL", 1, "Ahmad Din", "+923459202103", "jawad.civ@gpigilgit.edu.pk", "A+", 18),
                StudentEntity(19, "CIV-26-04", "Sajid Karim", "CIVIL", 1, "Karim Dad", "+923459202104", "sajid.civ@gpigilgit.edu.pk", "B-", 19),
                StudentEntity(20, "CIV-26-05", "Kamran Baig", "CIVIL", 1, "Mirza Baig", "+923459202105", "kamran.civ@gpigilgit.edu.pk", "O+", 20)
            )
        )

        // Civil Technology - 2nd Year (2025 Batch)
        list.addAll(
            listOf(
                StudentEntity(21, "CIV-25-01", "Arif Hussain", "CIVIL", 2, "Hussain Jan", "+923459202201", "arif.civ@gpigilgit.edu.pk", "A+", 21),
                StudentEntity(22, "CIV-25-02", "Sher Baz Ali", "CIVIL", 2, "Ali Gohar", "+923459202202", "sherbaz.civ@gpigilgit.edu.pk", "B+", 22),
                StudentEntity(23, "CIV-25-03", "Ehsan Ullah", "CIVIL", 2, "Ullah Yar", "+923459202203", "ehsan.civ@gpigilgit.edu.pk", "AB+", 23),
                StudentEntity(24, "CIV-25-04", "Zulfiqar Ali", "CIVIL", 2, "Ghulam Ali", "+923459202204", "zulfiqar.civ@gpigilgit.edu.pk", "O-", 24)
            )
        )

        // Civil Technology - 3rd Year (2024 Batch)
        list.addAll(
            listOf(
                StudentEntity(25, "CIV-24-01", "Muhammad Ayub", "CIVIL", 3, "Ayub Khan", "+923459202301", "ayub.civ@gpigilgit.edu.pk", "A-", 25),
                StudentEntity(26, "CIV-24-02", "Imtiaz Hassan", "CIVIL", 3, "Hassan Mehdi", "+923459202302", "imtiaz.civ@gpigilgit.edu.pk", "B+", 26),
                StudentEntity(27, "CIV-24-03", "Haider Abbas", "CIVIL", 3, "Abbas Ali", "+923459202303", "haider.civ@gpigilgit.edu.pk", "O+", 27),
                StudentEntity(28, "CIV-24-04", "Majid Sultan", "CIVIL", 3, "Sultan Muhammad", "+923459202304", "majid.civ@gpigilgit.edu.pk", "B-", 28)
            )
        )

        // Electrical Technology - 1st Year (2026 Batch)
        list.addAll(
            listOf(
                StudentEntity(29, "ELE-26-01", "Tanveer Alam", "ELECTRICAL", 1, "Alam Noor", "+923459203101", "tanveer.ele@gpigilgit.edu.pk", "A+", 29),
                StudentEntity(30, "ELE-26-02", "Shoaib Akhtar", "ELECTRICAL", 1, "Akhtar Abbas", "+923459203102", "shoaib.ele@gpigilgit.edu.pk", "B+", 30),
                StudentEntity(31, "ELE-26-03", "Bilal Mehboob", "ELECTRICAL", 1, "Mehboob Ali", "+923459203103", "bilal.ele@gpigilgit.edu.pk", "O+", 31),
                StudentEntity(32, "ELE-26-04", "Shakeel Ahmad", "ELECTRICAL", 1, "Ahmad Shah", "+923459203104", "shakeel.ele@gpigilgit.edu.pk", "AB-", 32),
                StudentEntity(33, "ELE-26-05", "Zubair Karim", "ELECTRICAL", 1, "Karim Baig", "+923459203105", "zubair.ele@gpigilgit.edu.pk", "B+", 33)
            )
        )

        // Electrical Technology - 2nd Year (2025 Batch)
        list.addAll(
            listOf(
                StudentEntity(34, "ELE-25-01", "Junaid Iqbal", "ELECTRICAL", 2, "Iqbal Hussain", "+923459203201", "junaid.ele@gpigilgit.edu.pk", "O+", 34),
                StudentEntity(35, "ELE-25-02", "Waqar Younis", "ELECTRICAL", 2, "Younis Khan", "+923459203202", "waqar.ele@gpigilgit.edu.pk", "A-", 35),
                StudentEntity(36, "ELE-25-03", "Hamza Ali", "ELECTRICAL", 2, "Ali Asghar", "+923459203203", "hamza.ele@gpigilgit.edu.pk", "B+", 36),
                StudentEntity(37, "ELE-25-04", "Adeel Abbas", "ELECTRICAL", 2, "Abbas Jan", "+923459203204", "adeel.ele@gpigilgit.edu.pk", "AB+", 37)
            )
        )

        // Electrical Technology - 3rd Year (2024 Batch)
        list.addAll(
            listOf(
                StudentEntity(38, "ELE-24-01", "Sohail Anwar", "ELECTRICAL", 3, "Anwar Shah", "+923459203301", "sohail.ele@gpigilgit.edu.pk", "B+", 38),
                StudentEntity(39, "ELE-24-02", "Qasim Ali", "ELECTRICAL", 3, "Ali Raza", "+923459203302", "qasim.ele@gpigilgit.edu.pk", "A+", 39),
                StudentEntity(40, "ELE-24-03", "Zeeshan Haider", "ELECTRICAL", 3, "Haider Sultan", "+923459203303", "zeeshan.ele@gpigilgit.edu.pk", "O+", 40),
                StudentEntity(41, "ELE-24-04", "Shahzad Noor", "ELECTRICAL", 3, "Noor Muhammad", "+923459203304", "shahzad.ele@gpigilgit.edu.pk", "B-", 41)
            )
        )

        return list
    }

    fun getInitialAttendanceHistory(students: List<StudentEntity>): List<AttendanceEntity> {
        val records = mutableListOf<AttendanceEntity>()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = LocalDate.now()

        // Generate past 14 days of academic records (skipping Sundays)
        for (dayOffset in 1..14) {
            val date = today.minusDays(dayOffset.toLong())
            if (date.dayOfWeek.value == 7) continue // Skip Sundays

            val dateStr = date.format(formatter)
            students.forEach { student ->
                // Deterministic pseudo-random pattern based on student id and day
                val hash = (student.id * 31 + dayOffset * 17).toInt() % 100
                val status = when {
                    // One or two students with lower attendance to demonstrate defaulters list (<75%)
                    student.id == 5L || student.id == 10L || student.id == 24L || student.id == 32L -> {
                        if (hash < 45) "ABSENT" else if (hash < 65) "PRESENT" else if (hash < 75) "LATE" else "LEAVE"
                    }
                    hash < 78 -> "PRESENT"
                    hash < 88 -> "LATE"
                    hash < 94 -> "ABSENT"
                    else -> "LEAVE"
                }

                records.add(
                    AttendanceEntity(
                        studentId = student.id,
                        rollNo = student.rollNo,
                        studentName = student.name,
                        department = student.department,
                        year = student.year,
                        date = dateStr,
                        status = status,
                        remark = if (status == "ABSENT") "Uninformed Absence" else if (status == "LEAVE") "Medical / Sanctioned" else "Regular",
                        timestamp = System.currentTimeMillis() - dayOffset * 86400000L
                    )
                )
            }
        }
        return records
    }
}
