package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.local.AttendanceStatus
import com.example.data.local.entity.StudentEntity
import com.example.ui.components.DepartmentSelector
import com.example.ui.components.YearSelector
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.AbsentRedLight
import com.example.ui.theme.LateAmber
import com.example.ui.theme.LateAmberLight
import com.example.ui.theme.LeaveBlue
import com.example.ui.theme.LeaveBlueLight
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PresentGreenLight
import com.example.util.SmsHelper
import com.example.viewmodel.AttendanceViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TakeAttendanceScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedDept by viewModel.selectedDepartment.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val students by viewModel.classStudents.collectAsState()
    val savedAttendance by viewModel.classAttendanceForDate.collectAsState()
    val draftAttendance by viewModel.draftAttendance.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveSuccessEvent by viewModel.saveSuccessEvent.collectAsState()

    var sendAlertsChecked by remember { mutableStateOf(true) }

    // Sync draft attendance whenever students or saved records are loaded
    LaunchedEffect(students, savedAttendance) {
        viewModel.syncDraftWithSaved(students, savedAttendance)
    }

    // Handle save success feedback
    LaunchedEffect(saveSuccessEvent) {
        saveSuccessEvent?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            viewModel.clearSaveEvent()
        }
    }

    // Live counts from current draft
    val presentCount = draftAttendance.values.count { it == AttendanceStatus.PRESENT }
    val absentCount = draftAttendance.values.count { it == AttendanceStatus.ABSENT }
    val lateCount = draftAttendance.values.count { it == AttendanceStatus.LATE }
    val leaveCount = draftAttendance.values.count { it == AttendanceStatus.LEAVE }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Header with Session Date
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
                            text = "Daily Attendance Session",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "${selectedDept.title} • ${selectedYear.label}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Date",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = selectedDate,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Department & Year Pickers
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

        // Live Counter Summary Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("attendance_counter_bar"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MiniCounter(label = "Present", count = presentCount, color = PresentGreen)
                    MiniCounter(label = "Absent", count = absentCount, color = AbsentRed)
                    MiniCounter(label = "Late", count = lateCount, color = LateAmber)
                    MiniCounter(label = "Leave", count = leaveCount, color = LeaveBlue)
                }
            }
        }

        // Batch Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.markAllAs(AttendanceStatus.PRESENT, students) },
                    colors = ButtonDefaults.buttonColors(containerColor = PresentGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mark_all_present_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("All Present", style = MaterialTheme.typography.labelMedium)
                }

                OutlinedButton(
                    onClick = { viewModel.markAllAs(AttendanceStatus.ABSENT, students) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mark_all_absent_button")
                ) {
                    Text("All Absent", color = AbsentRed, style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // Student Roster Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enrolled Students (${students.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Tap status to toggle",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }
        }

        // Student List Items
        items(students, key = { it.id }) { student ->
            val status = draftAttendance[student.id] ?: AttendanceStatus.PRESENT

            StudentAttendanceCard(
                student = student,
                currentStatus = status,
                onStatusChange = { newStatus ->
                    viewModel.setStudentStatus(student.id, newStatus)
                },
                onDirectSms = {
                    val msg = SmsHelper.generateAbsenteeSms(
                        studentName = student.name,
                        rollNo = student.rollNo,
                        departmentTitle = selectedDept.title,
                        yearLabel = selectedYear.label,
                        date = selectedDate
                    )
                    SmsHelper.openSmsComposer(context, student.guardianPhone, msg)
                }
            )
        }

        // Automated Parent Alerts Option & Save FAB/Button
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = sendAlertsChecked,
                            onCheckedChange = { sendAlertsChecked = it },
                            modifier = Modifier.testTag("automated_alerts_checkbox")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Automated Parent Alerts & Push",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Dispatches SMS queue & push alerts to guardian phone for absentees",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.saveAttendance(sendAutomatedAlerts = sendAlertsChecked)
                        },
                        enabled = !isSaving && students.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_attendance_button")
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Saving & Dispatching Alerts...")
                        } else {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save Attendance",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save Attendance & Dispatch Alerts",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun MiniCounter(
    label: String,
    count: Int,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
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

@Composable
private fun StudentAttendanceCard(
    student: StudentEntity,
    currentStatus: AttendanceStatus,
    onStatusChange: (AttendanceStatus) -> Unit,
    onDirectSms: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("student_card_${student.rollNo.lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Avatar with Student initials
                val initials = student.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(NavyPrimary.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    )
                }

                // Info: Name & Roll
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Roll: ${student.rollNo} • Guardian: ${student.guardianName}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Direct SMS icon to test parent SMS
                IconButton(
                    onClick = onDirectSms,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("direct_sms_${student.rollNo.lowercase()}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = "SMS Guardian",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4 Status Pill Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatusPillButton(
                    status = AttendanceStatus.PRESENT,
                    isSelected = currentStatus == AttendanceStatus.PRESENT,
                    onClick = { onStatusChange(AttendanceStatus.PRESENT) },
                    modifier = Modifier.weight(1f)
                )
                StatusPillButton(
                    status = AttendanceStatus.ABSENT,
                    isSelected = currentStatus == AttendanceStatus.ABSENT,
                    onClick = { onStatusChange(AttendanceStatus.ABSENT) },
                    modifier = Modifier.weight(1f)
                )
                StatusPillButton(
                    status = AttendanceStatus.LATE,
                    isSelected = currentStatus == AttendanceStatus.LATE,
                    onClick = { onStatusChange(AttendanceStatus.LATE) },
                    modifier = Modifier.weight(1f)
                )
                StatusPillButton(
                    status = AttendanceStatus.LEAVE,
                    isSelected = currentStatus == AttendanceStatus.LEAVE,
                    onClick = { onStatusChange(AttendanceStatus.LEAVE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatusPillButton(
    status: AttendanceStatus,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (baseColor, lightColor) = when (status) {
        AttendanceStatus.PRESENT -> Pair(PresentGreen, PresentGreenLight)
        AttendanceStatus.ABSENT -> Pair(AbsentRed, AbsentRedLight)
        AttendanceStatus.LATE -> Pair(LateAmber, LateAmberLight)
        AttendanceStatus.LEAVE -> Pair(LeaveBlue, LeaveBlueLight)
    }

    Surface(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("status_pill_${status.name.lowercase()}"),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) baseColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "${status.code} - ${status.label}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                    fontSize = 11.sp,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1
            )
        }
    }
}
