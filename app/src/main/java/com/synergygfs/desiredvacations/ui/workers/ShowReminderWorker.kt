package com.synergygfs.desiredvacations.ui.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.synergygfs.desiredvacations.Constants
import com.synergygfs.desiredvacations.ParcelableUtils
import com.synergygfs.desiredvacations.R
import com.synergygfs.desiredvacations.data.models.Vacation
import com.synergygfs.desiredvacations.ui.MainActivity
import java.io.File

class ShowReminderWorker(private val context: Context, private val workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val data = workerParams.inputData

        // Get vacation from extras
        val vacationByteArray = data.getByteArray("vacation")
        val vacation = ParcelableUtils.unmarshall(vacationByteArray!!, Vacation.CREATOR)
        // Get request code from extras
        val requestCode = data.getInt("requestCode", Constants.REQUEST_CODE_REMINDER_TODAY)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        // Create Notification Channel for APIs starting from 26, lower API's create it with the notification.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    "dv",
                    "Desired Vacations",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            notificationManager?.createNotificationChannel(channel)
        }

        // Create bundle for deep link
        val bundle = Bundle()
        bundle.putInt("vacationId", vacation.id)

        // Create deep link to vacation fragment
        val pendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.vacationFragment)
            .setArguments(bundle)
            .createPendingIntent()

        var bmp: Bitmap? = null
        var notificationId = 0

        try {
            // Create Bitmap from saved image file
            val inputStream = context.contentResolver.openInputStream(
                Uri.fromFile(
                    File(
                        context.cacheDir,
                        "/vacations_images/${vacation.imageName}"
                    )
                )
            )
            bmp = BitmapFactory.decodeStream(inputStream)

            // Create a notification id
            notificationId = System.currentTimeMillis().toInt()
        } catch (i: Exception) {
        } finally {
            // Create the NotificationBuilder
            val notificationBuilder = NotificationCompat.Builder(context, "dv")
                .setSmallIcon(R.drawable.ic_date)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setContentIntent(pendingIntent)
                .setContentText("Location: ${vacation.location}")
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setDefaults(Notification.DEFAULT_ALL)
                .setGroup(Constants.NOTIFICATION_GROUP_DESIRED_VACATIONS)
                .setAutoCancel(true)

            if (bmp != null) {
                notificationBuilder
                    .setLargeIcon(bmp)
                    .setStyle(
                        NotificationCompat.BigPictureStyle()
                            .bigPicture(bmp)
                            .bigLargeIcon(null)
                    )
            }

            // Set the title depending on the request code
            when (requestCode) {
                Constants.REQUEST_CODE_REMINDER_WEEK -> {
                    notificationBuilder.setContentTitle(context.getString(R.string.vacation_reminder_7_days))
                }
                Constants.REQUEST_CODE_REMINDER_TOMORROW -> {
                    notificationBuilder.setContentTitle(context.getString(R.string.vacation_reminder_tomorrow))
                }
                else -> {
                    notificationBuilder.setContentTitle(context.getString(R.string.vacation_reminder_today))
                }
            }

            // Post the Notification on the Channel.
            notificationManager?.notify(notificationId, notificationBuilder.build())
        }

        // Indicate that the work finished successfully with the Result
        return Result.success()
    }
}