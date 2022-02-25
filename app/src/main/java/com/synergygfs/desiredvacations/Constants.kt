package com.synergygfs.desiredvacations

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

class Constants {

    companion object {
        @SuppressLint("SimpleDateFormat")
        var formatter = SimpleDateFormat("hh:mm a dd/MM/yy")

        const val NOTIFICATION_GROUP_DESIRED_VACATIONS = "com.synergygfs.desiredvacations"

        const val REQUEST_CODE_REMINDER_TODAY = 0
        const val REQUEST_CODE_REMINDER_TOMORROW = 1
        const val REQUEST_CODE_REMINDER_WEEK = 2
    }

}