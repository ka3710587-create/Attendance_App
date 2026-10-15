package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {

    private const val CHANNEL_ATTENDANCE_ID = "gpi_attendance_alerts"
    private const val CHANNEL_ATTENDANCE_NAME = "GPI Attendance Alerts"
    private const val CHANNEL_SMS_ID = "gpi_sms_dispatch"
    private const val CHANNEL_SMS_NAME = "SMS Dispatch Status"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val attendanceChannel = NotificationChannel(
                CHANNEL_ATTENDANCE_ID,
                CHANNEL_ATTENDANCE_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Real-time alerts for student absences and low attendance warnings"
                enableVibration(true)
            }

            val smsChannel = NotificationChannel(
                CHANNEL_SMS_ID,
                CHANNEL_SMS_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Automated parent SMS gateway status alerts"
            }

            notificationManager.createNotificationChannel(attendanceChannel)
            notificationManager.createNotificationChannel(smsChannel)
        }
    }

    fun sendAbsencePushAlert(
        context: Context,
        classTitle: String,
        absentCount: Int,
        absenteeSummary: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ATTENDANCE_ID)
            .setSmallIcon(R.drawable.ic_gpi_logo)
            .setContentTitle("GPI Gilgit: Absence Alert ($classTitle)")
            .setContentText("$absentCount student(s) marked absent today.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$absentCount student(s) marked absent in $classTitle today:\n$absenteeSummary\nAutomated parent SMS alerts have been queued.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    fun sendLowAttendanceWarning(
        context: Context,
        studentName: String,
        rollNo: String,
        percentage: Double
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ATTENDANCE_ID)
            .setSmallIcon(R.drawable.ic_gpi_logo)
            .setContentTitle("Defaulter Warning: $rollNo ($studentName)")
            .setContentText("Attendance has dropped to ${"%.1f".format(percentage)}% (<75% threshold)")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Govt. Polytechnical Institute Gilgit Notice:\nStudent $studentName ($rollNo) has an attendance of ${"%.1f".format(percentage)}%, falling below the mandatory 75% examination eligibility criteria.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(rollNo.hashCode(), notification)
    }
}
