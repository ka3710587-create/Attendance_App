package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.Department
import com.example.data.local.NavDestination
import com.example.data.local.PortalMode
import com.example.data.local.StudyYear
import com.example.ui.components.DepartmentSelector
import com.example.ui.components.StatCard
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
import com.example.viewmodel.AttendanceViewModel

@Composable
fun DashboardScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDept by viewModel.selectedDepartment.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val students by viewModel.classStudents.collectAsState()
    val todayAttendance by viewModel.classAttendanceForDate.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val notificationLogs by viewModel.notificationLogs.collectAsState()
    val portalMode by viewModel.portalMode.collectAsState()

    // Calculate metrics for current class
    val totalStudents = students.size
    val presentCount = todayAttendance.count { it.status == "PRESENT" }
    val absentCount = todayAttendance.count { it.status == "ABSENT" }
    val lateCount = todayAttendance.count { it.status == "LATE" }
    val leaveCount = todayAttendance.count { it.status == "LEAVE" }
    val isMarkedToday = todayAttendance.isNotEmpty()

    val attendancePercentage = if (totalStudents > 0 && isMarkedToday) {
        val effectivePresent = presentCount + (lateCount * 0.75)
        ((effectivePresent / totalStudents.toDouble()) * 100.0).coerceIn(0.0, 100.0)
    } else 0.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Institutional Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("institute_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_gpi_logo),
                        contentDescription = "GPI Crest",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = TealAccent.copy(alpha = 0.3f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "TEVTA / Technical Board GB",
                                color = Color(0xFF5EEAD4),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "Govt. Polytechnical Institute",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "Gilgit • Digital Attendance Center",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // Active Class & Date Selectors
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

        // Today's Live Attendance Overview
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("today_attendance_summary_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Class Attendance Status",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "${selectedDept.title} • ${selectedYear.label} ($selectedDate)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isMarkedToday) PresentGreenLight else LateAmberLight
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isMarkedToday) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = if (isMarkedToday) PresentGreen else LateAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (isMarkedToday) "Recorded" else "Pending Mark",
                                    color = if (isMarkedToday) PresentGreen else LateAmber,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isMarkedToday) {
                        // Attendance Rate Progress Bar & Label
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "Daily Attendance Rate",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Text(
                                text = "${"%.1f".format(attendancePercentage)}%",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (attendancePercentage >= 75.0) PresentGreen else AbsentRed
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (attendancePercentage / 100f).toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = if (attendancePercentage >= 75.0) PresentGreen else AbsentRed,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4 Metric Chips: Total, Present, Absent, Late/Leave
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatChip(
                            label = "Enrolled",
                            value = "$totalStudents",
                            color = NavyPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            label = "Present",
                            value = if (isMarkedToday) "$presentCount" else "-",
                            color = PresentGreen,
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            label = "Absent",
                            value = if (isMarkedToday) "$absentCount" else "-",
                            color = AbsentRed,
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            label = "Late/Lv",
                            value = if (isMarkedToday) "${lateCount + leaveCount}" else "-",
                            color = LateAmber,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Absentee Alert Banner (if marked and absentees exist)
        if (isMarkedToday && absentCount > 0) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("absentee_alert_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AbsentRedLight)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(AbsentRed, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Alerts",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Automated Parent Alerts Ready",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AbsentRed
                                )
                            )
                            Text(
                                text = "$absentCount student(s) absent. Push & SMS notifications triggered for parents.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF7F1D1D)
                                )
                            )
                        }
                        Button(
                            onClick = { viewModel.navigateTo(NavDestination.SMS_ALERTS) },
                            colors = ButtonDefaults.buttonColors(containerColor = AbsentRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("view_alerts_button")
                        ) {
                            Text("Logs", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // Quick Actions Section
        item {
            Text(
                text = "Faculty Quick Actions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionCard(
                    title = "Mark Attendance",
                    description = "Take daily attendance for ${selectedDept.shortName} ${selectedYear.badge}",
                    icon = Icons.Default.HowToReg,
                    accentColor = NavyPrimary,
                    onClick = { viewModel.navigateTo(NavDestination.TAKE_ATTENDANCE) },
                    modifier = Modifier.weight(1f).testTag("action_take_attendance")
                )
                ActionCard(
                    title = "Monthly Report",
                    description = "View attendance percentages & <75% defaulters",
                    icon = Icons.Default.Assessment,
                    accentColor = TealAccent,
                    onClick = { viewModel.navigateTo(NavDestination.MONTHLY_REPORT) },
                    modifier = Modifier.weight(1f).testTag("action_monthly_report")
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionCard(
                    title = "Parent SMS & Alerts",
                    description = "Real-time dispatch log & automated messaging",
                    icon = Icons.Default.NotificationsActive,
                    accentColor = Color(0xFFD97706),
                    onClick = { viewModel.navigateTo(NavDestination.SMS_ALERTS) },
                    modifier = Modifier.weight(1f).testTag("action_sms_alerts")
                )
                ActionCard(
                    title = "Student Directory",
                    description = "View & manage student rolls & guardian contacts",
                    icon = Icons.Default.People,
                    accentColor = Color(0xFF4F46E5),
                    onClick = { viewModel.navigateTo(NavDestination.ROSTER) },
                    modifier = Modifier.weight(1f).testTag("action_students_roster")
                )
            }
        }

        // Department Overview Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "GPI Gilgit Technologies Roster",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "3 Departments • 3 Academic Batches (1st, 2nd, 3rd Year)",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    DepartmentRowItem(
                        deptName = "Computer Information Tech (CIT)",
                        icon = Icons.Default.Computer,
                        totalStudents = allStudents.count { it.department == "CIT" },
                        color = NavyPrimary
                    )
                    DepartmentRowItem(
                        deptName = "Civil Technology (Civil)",
                        icon = Icons.Default.Business,
                        totalStudents = allStudents.count { it.department == "CIVIL" },
                        color = TealAccent
                    )
                    DepartmentRowItem(
                        deptName = "Electrical Technology",
                        icon = Icons.Default.ElectricBolt,
                        totalStudents = allStudents.count { it.department == "ELECTRICAL" },
                        color = Color(0xFFD97706)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatChip(
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
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
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
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(accentColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                ),
                maxLines = 2
            )
        }
    }
}

@Composable
private fun DepartmentRowItem(
    deptName: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    totalStudents: Int,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(color.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = deptName,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = deptName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = "1st, 2nd & 3rd Year DAE Classes",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = "$totalStudents Students",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
