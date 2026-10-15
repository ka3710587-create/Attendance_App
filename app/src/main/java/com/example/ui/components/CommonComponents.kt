package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.AttendanceStatus
import com.example.data.local.Department
import com.example.data.local.NavDestination
import com.example.data.local.PortalMode
import com.example.data.local.StudyYear
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
import com.example.ui.theme.TealAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpiTopAppBar(
    currentNav: NavDestination,
    portalMode: PortalMode,
    onTogglePortalMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier.testTag("gpi_top_app_bar"),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_gpi_logo),
                    contentDescription = "GPI Gilgit Crest",
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                )
                Column {
                    Text(
                        text = "Govt. Polytechnical Institute",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Gilgit • Attendance & Alerts System",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        },
        actions = {
            // Portal Toggle Button (Faculty vs Student/Parent)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (portalMode == PortalMode.FACULTY) TealAccent else Color(0xFFD97706),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onTogglePortalMode() }
                    .testTag("portal_switch_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (portalMode == PortalMode.FACULTY) Icons.Default.School else Icons.Default.Person,
                        contentDescription = "Switch Portal",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (portalMode == PortalMode.FACULTY) "Faculty" else "Student",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = NavyPrimary,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
fun GpiBottomNavBar(
    currentNav: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    portalMode: PortalMode,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("bottom_nav_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        if (portalMode == PortalMode.FACULTY) {
            NavigationBarItem(
                selected = currentNav == NavDestination.DASHBOARD,
                onClick = { onNavigate(NavDestination.DASHBOARD) },
                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                label = { Text("Dashboard", maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.testTag("nav_dashboard")
            )
            NavigationBarItem(
                selected = currentNav == NavDestination.TAKE_ATTENDANCE,
                onClick = { onNavigate(NavDestination.TAKE_ATTENDANCE) },
                icon = { Icon(Icons.Default.HowToReg, contentDescription = "Mark Attendance") },
                label = { Text("Mark", maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.testTag("nav_mark")
            )
            NavigationBarItem(
                selected = currentNav == NavDestination.MONTHLY_REPORT,
                onClick = { onNavigate(NavDestination.MONTHLY_REPORT) },
                icon = { Icon(Icons.Default.Assessment, contentDescription = "Monthly Report") },
                label = { Text("Reports", maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.testTag("nav_reports")
            )
            NavigationBarItem(
                selected = currentNav == NavDestination.SMS_ALERTS,
                onClick = { onNavigate(NavDestination.SMS_ALERTS) },
                icon = { Icon(Icons.Default.Notifications, contentDescription = "Parent SMS & Alerts") },
                label = { Text("Alerts", maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.testTag("nav_alerts")
            )
            NavigationBarItem(
                selected = currentNav == NavDestination.ROSTER,
                onClick = { onNavigate(NavDestination.ROSTER) },
                icon = { Icon(Icons.Default.People, contentDescription = "Students Roster") },
                label = { Text("Students", maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.testTag("nav_students")
            )
        } else {
            // Student & Parent Portal Navigation
            NavigationBarItem(
                selected = currentNav == NavDestination.STUDENT_PORTAL,
                onClick = { onNavigate(NavDestination.STUDENT_PORTAL) },
                icon = { Icon(Icons.Default.Person, contentDescription = "My Attendance") },
                label = { Text("My Record") },
                colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.testTag("nav_my_record")
            )
            NavigationBarItem(
                selected = currentNav == NavDestination.DASHBOARD,
                onClick = { onNavigate(NavDestination.DASHBOARD) },
                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Class Overview") },
                label = { Text("Overview") },
                colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.testTag("nav_student_overview")
            )
            NavigationBarItem(
                selected = currentNav == NavDestination.MONTHLY_REPORT,
                onClick = { onNavigate(NavDestination.MONTHLY_REPORT) },
                icon = { Icon(Icons.Default.Assessment, contentDescription = "Monthly Class Report") },
                label = { Text("Class Stats") },
                colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.testTag("nav_student_class_stats")
            )
        }
    }
}

@Composable
fun DepartmentSelector(
    selectedDepartment: Department,
    onDepartmentSelected: (Department) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Department / Technology",
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
                Department.entries.forEach { dept ->
                    val isSelected = dept == selectedDepartment
                    val icon = when (dept) {
                        Department.CIVIL -> Icons.Default.Business
                        Department.ELECTRICAL -> Icons.Default.ElectricBolt
                        Department.CIT -> Icons.Default.Computer
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { onDepartmentSelected(dept) },
                        label = {
                            Text(
                                text = dept.shortName,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = dept.title,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dept_chip_${dept.code.lowercase()}"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun YearSelector(
    selectedYear: StudyYear,
    onYearSelected: (StudyYear) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Academic Year",
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
                StudyYear.entries.forEach { year ->
                    val isSelected = year == selectedYear
                    FilterChip(
                        selected = isSelected,
                        onClick = { onYearSelected(year) },
                        label = {
                            Text(
                                text = year.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("year_chip_${year.yearNumber}"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealAccent,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun AttendanceStatusBadge(
    status: AttendanceStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        AttendanceStatus.PRESENT -> Triple(PresentGreenLight, PresentGreen, "Present")
        AttendanceStatus.ABSENT -> Triple(AbsentRedLight, AbsentRed, "Absent")
        AttendanceStatus.LATE -> Triple(LateAmberLight, LateAmber, "Late")
        AttendanceStatus.LEAVE -> Triple(LeaveBlueLight, LeaveBlue, "Leave")
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(accentColor.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
