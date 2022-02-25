package com.synergygfs.desiredvacations.ui.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.synergygfs.desiredvacations.ReminderManager
import com.synergygfs.desiredvacations.data.DbHelper


class RestoreRemindersWorker(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {

        val dbHelper = DbHelper(context)
        val reminderManager = ReminderManager(context)

        // Get all vacations from the db
        val vacationsCollection = dbHelper.getAllVacations()

        // Create reminders for all vacations
        vacationsCollection.forEach {
            reminderManager.setReminder(it)
        }

        // Indicate that the work finished successfully with the Result
        return Result.success()
    }
}