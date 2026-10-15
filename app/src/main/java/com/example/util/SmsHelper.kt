package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object SmsHelper {

    fun generateAbsenteeSms(
        studentName: String,
        rollNo: String,
        departmentTitle: String,
        yearLabel: String,
        date: String
    ): String {
        return "Govt. Polytechnical Institute Gilgit Alert:\nDear Guardian, your ward $studentName ($rollNo) was marked ABSENT today ($date) in $departmentTitle ($yearLabel). Regular attendance (minimum 75%) is mandatory for board exam clearance. Admin helpline: 05811-920251."
    }

    fun generateDefaulterSms(
        studentName: String,
        rollNo: String,
        departmentTitle: String,
        yearLabel: String,
        percentage: Double
    ): String {
        return "Govt. Polytechnical Institute Gilgit - Urgent Notice:\nGuardian of $studentName ($rollNo): Current monthly attendance is ${"%.1f".format(percentage)}%, below the mandatory 75% limit for $departmentTitle ($yearLabel). Please visit the institute or call 05811-920251."
    }

    fun openSmsComposer(context: Context, phoneNumber: String, messageText: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:${phoneNumber.replace(" ", "")}")
                putExtra("sms_body", messageText)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not launch SMS app: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareReportText(context: Context, title: String, content: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, content)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Share Attendance Report via"))
        } catch (e: Exception) {
            Toast.makeText(context, "Share failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
