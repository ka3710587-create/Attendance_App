package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.NavDestination
import com.example.data.local.PortalMode
import com.example.ui.components.GpiBottomNavBar
import com.example.ui.components.GpiTopAppBar
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ManageStudentsScreen
import com.example.ui.screens.MonthlyReportScreen
import com.example.ui.screens.SmsAlertsCenterScreen
import com.example.ui.screens.StudentPortalScreen
import com.example.ui.screens.TakeAttendanceScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AttendanceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent()
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: AttendanceViewModel = viewModel()
) {
    val currentNav by viewModel.currentNav.collectAsState()
    val portalMode by viewModel.portalMode.collectAsState()

    // Request notification permission on Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { /* Result handled gracefully */ }

        LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Predictive back handler: return to dashboard if on sub-screens
    BackHandler(enabled = currentNav != NavDestination.DASHBOARD) {
        viewModel.navigateTo(NavDestination.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GpiTopAppBar(
                currentNav = currentNav,
                portalMode = portalMode,
                onTogglePortalMode = {
                    val newMode = if (portalMode == PortalMode.FACULTY) {
                        PortalMode.STUDENT_PARENT
                    } else {
                        PortalMode.FACULTY
                    }
                    viewModel.setPortalMode(newMode)
                }
            )
        },
        bottomBar = {
            GpiBottomNavBar(
                currentNav = currentNav,
                onNavigate = { destination -> viewModel.navigateTo(destination) },
                portalMode = portalMode
            )
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        when (currentNav) {
            NavDestination.DASHBOARD -> DashboardScreen(
                viewModel = viewModel,
                modifier = screenModifier
            )
            NavDestination.TAKE_ATTENDANCE -> TakeAttendanceScreen(
                viewModel = viewModel,
                modifier = screenModifier
            )
            NavDestination.MONTHLY_REPORT -> MonthlyReportScreen(
                viewModel = viewModel,
                modifier = screenModifier
            )
            NavDestination.STUDENT_PORTAL -> StudentPortalScreen(
                viewModel = viewModel,
                modifier = screenModifier
            )
            NavDestination.SMS_ALERTS -> SmsAlertsCenterScreen(
                viewModel = viewModel,
                modifier = screenModifier
            )
            NavDestination.ROSTER -> ManageStudentsScreen(
                viewModel = viewModel,
                modifier = screenModifier
            )
        }
    }
}
