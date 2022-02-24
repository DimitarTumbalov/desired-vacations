package com.synergygfs.desiredvacations

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

class Constants {

    companion object {
        @SuppressLint("SimpleDateFormat")
        var formatter = SimpleDateFormat("hh:mm a dd/MM/yy")

        const val NOTIFICATION_GROUP_DESIRED_VACATIONS = "com.synergygfs.desiredvacations"

        const val REQUEST_CODE_FIRST_REMINDER = 0
        const val REQUEST_CODE_SECOND_REMINDER = 1
        const val REQUEST_CODE_THIRD_REMINDER = 2
        const val REQUEST_CODE_FORTH_REMINDER = 3

    }

}