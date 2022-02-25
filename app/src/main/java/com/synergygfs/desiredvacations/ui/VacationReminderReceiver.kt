package com.synergygfs.desiredvacations.ui

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.NavDeepLinkBuilder
import com.synergygfs.desiredvacations.Constants
import com.synergygfs.desiredvacations.Constants.Companion.REQUEST_CODE_FIRST_REMINDER
import com.synergygfs.desiredvacations.Constants.Companion.REQUEST_CODE_FORTH_REMINDER
import com.synergygfs.desiredvacations.Constants.Companion.REQUEST_CODE_SECOND_REMINDER
import com.synergygfs.desiredvacations.Constants.Companion.REQUEST_CODE_THIRD_REMINDER
import com.synergygfs.desiredvacations.ParcelableUtils
import com.synergygfs.desiredvacations.R
import com.synergygfs.desiredvacations.data.models.Vacation
import java.io.File


class VacationReminderReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        // Get vacation from extras
        val vacationByteArray = intent.getByteArrayExtra("vacation")
        val vacation = ParcelableUtils.unmarshall(vacationByteArray!!, Vacation.CREATOR)
        // Get request code from extras
        val requestCode = intent.getIntExtra("requestCode", REQUEST_CODE_FIRST_REMINDER)

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
                .setColor(getColor(context, R.color.primary))
                .setDefaults(Notification.DEFAULT_SOUND)
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
                REQUEST_CODE_FORTH_REMINDER -> {
                    notificationBuilder.setContentTitle(context.getString(R.string.vacation_reminder_7_days))
                }
                REQUEST_CODE_THIRD_REMINDER -> {
                    notificationBuilder.setContentTitle(context.getString(R.string.vacation_reminder_tomorrow))
                }
                REQUEST_CODE_SECOND_REMINDER -> {
                    notificationBuilder.setContentTitle(context.getString(R.string.vacation_reminder_today))
                }
                else -> {
                    notificationBuilder.setContentTitle(context.getString(R.string.vacation_reminder_started))
                }
            }

            // Post the Notification on the Channel.
            notificationManager?.notify(notificationId, notificationBuilder.build())
        }
    }
}