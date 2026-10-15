package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.StudentMonthlySummary
import com.example.ui.components.DepartmentSelector
import com.example.ui.components.YearSelector
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.AbsentRedLight
import com.example.ui.theme.LateAmber
import com.example.ui.theme.LateAmberLight
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PresentGreenLight
import com.example.ui.theme.TealAccent
import com.example.util.SmsHelper
import com.example.viewmodel.AttendanceViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MonthlyReportScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedDept by viewModel.selectedDepartment.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedMonthPrefix by viewModel.selectedMonthPrefix.collectAsState()
    val reportStats by viewModel.monthlyReportStats.collectAsState()

    LaunchedEffect(selectedDept, selectedYear, selectedMonthPrefix) {
        viewModel.loadMonthlyReport()
    }

    val stats = reportStats

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Monthly Attendance Report",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Govt. Polytechnical Institute Gilgit",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )
                    }

                    // Share button
                    Button(
                        onClick = {
                            if (stats != null) {
                                val reportText = generateReportText(stats)
                                SmsHelper.shareReportText(
                                    context,
                                    "GPI Gilgit - ${selectedDept.shortName} ${selectedYear.label} Report",
                                    reportText
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("share_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Report",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // Department & Year Selectors
        item {
            DepartmentSelector(
                selectedDepartment = selectedDept,
                onDepartmentSelected = { viewModel.setDepartment(it) }
            )
        }

        item {
            YearSelector(
                selectedYear = selectedYear,
                onYearSelected = { viewModel.setYear(it) }
            )
        }

        // Month Selector Chips
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Report Month",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val currentYear = LocalDate.now().year
                        val months = listOf(
                            Pair("$currentYear-10", "October $currentYear"),
                            Pair("$currentYear-09", "September $currentYear"),
                            Pair("$currentYear-08", "August $currentYear")
                        )

                        months.forEach { (prefix, label) ->
                            val isSelected = prefix == selectedMonthPrefix
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setMonthPrefix(prefix) },
                                label = { Text(label, fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("month_chip_$prefix"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyPrimary,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // High-level Performance Metrics
        item {
            if (stats != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Monthly Class Performance Summary",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${selectedDept.title} • ${selectedYear.label} (${selectedMonthPrefix})",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ReportStatBox(
                                label = "Total Students",
                                value = "${stats.totalStudents}",
                                color = NavyPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            ReportStatBox(
                                label = "Working Days",
                                value = "${stats.totalWorkingDays}",
                                color = TealAccent,
                                modifier = Modifier.weight(1f)
                            )
                            ReportStatBox(
                                label = "Avg. Attendance",
                                value = "${"%.1f".format(stats.averageAttendancePercentage)}%",
                                color = if (stats.averageAttendancePercentage >= 75) PresentGreen else AbsentRed,
                                modifier = Modifier.weight(1f)
                            )
                            ReportStatBox(
                                label = "Defaulters (<75%)",
                                value = "${stats.defaulterCount}",
                                color = if (stats.defaulterCount == 0) PresentGreen else AbsentRed,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Defaulters Warning Notice (Board 75% rule)
        if (stats != null && stats.defaulterCount > 0) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("defaulters_warning_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AbsentRedLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Defaulters",
                                tint = AbsentRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Board Examination Defaulter Notice",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AbsentRed
                                    )
                                )
                                Text(
                                    text = "${stats.defaulterCount} student(s) below mandatory 75% attendance threshold",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF7F1D1D)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val defaulters = stats.studentSummaries.filter { it.isDefaulter }
                        defaulters.forEach { def ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• ${def.student.name} (${def.student.rollNo})",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = AbsentRed
                                )
                                Text(
                                    text = "${"%.1f".format(def.attendancePercentage)}%",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = AbsentRed
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                defaulters.forEach { def ->
                                    val msg = SmsHelper.generateDefaulterSms(
                                        studentName = def.student.name,
                                        rollNo = def.student.rollNo,
                                        departmentTitle = selectedDept.title,
                                        yearLabel = selectedYear.label,
                                        percentage = def.attendancePercentage
                                    )
                                    viewModel.sendSingleSmsAlert(def.student, msg)
                                }
                                Toast.makeText(
                                    context,
                                    "Sent urgent defaulter SMS alerts to all ${defaulters.size} guardians",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AbsentRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("notify_defaulters_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Message,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dispatch Defaulter SMS to All Guardians")
                        }
                    }
                }
            }
        }

        // Student-by-Student Roster Table
        item {
            Text(
                text = "Detailed Student Attendance Breakdown",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (stats != null) {
            items(stats.studentSummaries, key = { it.student.id }) { summary ->
                StudentMonthlyReportCard(
                    summary = summary,
                    onSmsGuardian = {
                        val msg = if (summary.isDefaulter) {
                            SmsHelper.generateDefaulterSms(
                                studentName = summary.student.name,
                                rollNo = summary.student.rollNo,
                                departmentTitle = selectedDept.title,
                                yearLabel = selectedYear.label,
                                percentage = summary.attendancePercentage
                            )
                        } else {
                            "Govt. Polytechnical Institute Gilgit: Dear Guardian, student ${summary.student.name} (${summary.student.rollNo}) has good attendance of ${"%.1f".format(summary.attendancePercentage)}% for ${selectedMonthPrefix}. Keep up the great work!"
                        }
                        SmsHelper.openSmsComposer(context, summary.student.guardianPhone, msg)
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun ReportStatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.08f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StudentMonthlyReportCard(
    summary: StudentMonthlySummary,
    onSmsGuardian: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isGood = summary.attendancePercentage >= 75.0
    val statusColor = if (isGood) PresentGreen else AbsentRed

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = summary.student.name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                    Text(
                        text = "Roll No: ${summary.student.rollNo} • Parent: ${summary.student.guardianName}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isGood) PresentGreenLight else AbsentRedLight
                ) {
                    Text(
                        text = "${"%.1f".format(summary.attendancePercentage)}%",
                        color = statusColor,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { (summary.attendancePercentage / 100f).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Breakdown counters & SMS Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "P: ${summary.presentCount}",
                        style = MaterialTheme.typography.labelSmall.copy(color = PresentGreen, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "A: ${summary.absentCount}",
                        style = MaterialTheme.typography.labelSmall.copy(color = AbsentRed, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "L: ${summary.lateCount}",
                        style = MaterialTheme.typography.labelSmall.copy(color = LateAmber, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Lv: ${summary.leaveCount}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                    )
                }

                IconButton(
                    onClick = onSmsGuardian,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = "SMS Guardian",
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun generateReportText(stats: com.example.data.repository.ClassMonthlyStats): String {
    val sb = StringBuilder()
    sb.appendLine("==========================================")
    sb.appendLine("GOVT. POLYTECHNICAL INSTITUTE GILGIT")
    sb.appendLine("DEPARTMENT OF ${stats.department.title.uppercase()}")
    sb.appendLine("MONTHLY ATTENDANCE REPORT - ${stats.year.label.uppercase()}")
    sb.appendLine("Session Month: ${stats.monthPrefix}")
    sb.appendLine("==========================================")
    sb.appendLine("Total Students: ${stats.totalStudents}")
    sb.appendLine("Total Working Days Held: ${stats.totalWorkingDays}")
    sb.appendLine("Class Average Attendance: ${"%.1f".format(stats.averageAttendancePercentage)}%")
    sb.appendLine("Defaulters (<75% eligibility): ${stats.defaulterCount}")
    sb.appendLine("------------------------------------------")
    sb.appendLine("STUDENT ROSTER:")
    stats.studentSummaries.forEachIndexed { index, s ->
        val statusLabel = if (s.isDefaulter) "DEFAULTER [NO EXAM]" else "ELIGIBLE"
        sb.appendLine(
            "${index + 1}. [${s.student.rollNo}] ${s.student.name} - " +
                    "${"%.1f".format(s.attendancePercentage)}% (P:${s.presentCount}, A:${s.absentCount}, L:${s.lateCount}) - $statusLabel"
        )
    }
    sb.appendLine("==========================================")
    sb.appendLine("Certified by HOD / Academic Incharge")
    sb.appendLine("Govt. Polytechnical Institute Gilgit, GB")
    return sb.toString()
}
