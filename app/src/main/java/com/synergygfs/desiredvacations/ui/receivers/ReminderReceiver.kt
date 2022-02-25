package com.synergygfs.desiredvacations.ui.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.synergygfs.desiredvacations.Constants
import com.synergygfs.desiredvacations.ui.workers.ShowReminderWorker


class ReminderReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

        // Set the params
        val dataBuilder = Data.Builder()
        dataBuilder.putByteArray("vacation", intent.getByteArrayExtra("vacation")!!)
        dataBuilder.putInt(
            "requestCode",
            intent.getIntExtra("requestCode", Constants.REQUEST_CODE_REMINDER_TODAY)
        )

        // Create the work request to show a reminder notification
        val showReminderWorkRequest =
            OneTimeWorkRequestBuilder<ShowReminderWorker>()
                .setInputData(dataBuilder.build())
                .build()

        // Submit the work request
        WorkManager
            .getInstance(context)
            .enqueue(showReminderWorkRequest)
    }
}