package com.synergygfs.desiredvacations.ui.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.synergygfs.desiredvacations.ui.workers.RestoreRemindersWorker

class SystemBootReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        // Create the work request to restore the vacations reminders
        val restoreRemindersWorkRequest =
            OneTimeWorkRequestBuilder<RestoreRemindersWorker>()
                .build()

        // Submit the work request
        WorkManager
            .getInstance(context)
            .enqueue(restoreRemindersWorkRequest)
    }
}