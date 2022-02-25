package com.synergygfs.desiredvacations

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import com.synergygfs.desiredvacations.Constants.Companion.REQUEST_CODE_REMINDER_TODAY
import com.synergygfs.desiredvacations.Constants.Companion.REQUEST_CODE_REMINDER_TOMORROW
import com.synergygfs.desiredvacations.Constants.Companion.REQUEST_CODE_REMINDER_WEEK
import com.synergygfs.desiredvacations.data.models.Vacation
import com.synergygfs.desiredvacations.ui.receivers.ReminderReceiver
import java.util.*

@SuppressLint("UnspecifiedImmutableFlag")
class ReminderManager(private var context: Context) {

    fun doRemindersExist(vacation: Vacation): Boolean {
        // Create intent
        val intent = Intent(
            context,
            ReminderReceiver::class.java
        )

        val bytes = ParcelableUtils.marshall(vacation)
        intent.putExtra("vacation", bytes)

        intent.putExtra("requestCode", REQUEST_CODE_REMINDER_TODAY)
        val firstPendingIntent =
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_REMINDER_TODAY, intent, PendingIntent.FLAG_NO_CREATE
            )

        intent.putExtra("requestCode", REQUEST_CODE_REMINDER_TOMORROW)
        val secondPendingIntent =
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_REMINDER_TOMORROW, intent, PendingIntent.FLAG_NO_CREATE
            )

        intent.putExtra("requestCode", REQUEST_CODE_REMINDER_WEEK)
        val thirdPendingIntent =
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_REMINDER_WEEK, intent, PendingIntent.FLAG_NO_CREATE
            )

        return firstPendingIntent != null || secondPendingIntent != null || thirdPendingIntent != null
    }

    fun setReminder(vacation: Vacation) {
        // Create intent
        val intent = Intent(
            context,
            ReminderReceiver::class.java
        )
        // Convert Vacation to ByteArray
        val bytes = ParcelableUtils.marshall(vacation)
        intent.putExtra("vacation", bytes)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

        val calendar = Calendar.getInstance()
        val now = calendar.time
        val vacationDate = vacation.date

        when {
            vacationDate.before(now) -> return
            DateUtils.isToday(vacationDate.time) -> { // Schedule and immediately show the reminder that vacation starts today
                intent.putExtra("requestCode", REQUEST_CODE_REMINDER_TODAY)

                val pendingIntentSecondReminder = PendingIntent.getBroadcast(
                    context,
                    REQUEST_CODE_REMINDER_TODAY,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                alarmManager?.setExact(
                    AlarmManager.RTC_WAKEUP,
                    now.time,
                    pendingIntentSecondReminder
                )
            }
            else -> {
                calendar.time = vacationDate
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                val vacationDateReminder1 = calendar.time

                intent.putExtra("requestCode", REQUEST_CODE_REMINDER_TOMORROW)

                val pendingIntentThirdReminder = PendingIntent.getBroadcast(
                    context,
                    REQUEST_CODE_REMINDER_TOMORROW,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                alarmManager?.setExact(
                    AlarmManager.RTC_WAKEUP,
                    vacationDateReminder1.time,
                    pendingIntentThirdReminder
                )

                calendar.add(Calendar.DAY_OF_MONTH, -6)
                val vacationDateReminder7 = calendar.time

                vacationDateReminder7.after(now).let {
                    if (it) {
                        intent.putExtra(
                            "requestCode",
                            REQUEST_CODE_REMINDER_WEEK
                        )

                        val pendingIntentForthReminder = PendingIntent.getBroadcast(
                            context,
                            REQUEST_CODE_REMINDER_WEEK,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )

                        alarmManager?.setExact(
                            AlarmManager.RTC_WAKEUP,
                            vacationDateReminder7.time,
                            pendingIntentForthReminder
                        )
                    }
                }
            }
        }
    }

    fun cancelReminders(vacation: Vacation) {
        // Create intent
        val intent = Intent(
            context,
            ReminderReceiver::class.java
        )
        val bytes = ParcelableUtils.marshall(vacation)
        intent.putExtra("vacation", bytes)

        intent.putExtra("requestCode", REQUEST_CODE_REMINDER_TODAY)
        val firstPendingIntent =
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_REMINDER_TODAY, intent, PendingIntent.FLAG_NO_CREATE
            )

        intent.putExtra("requestCode", REQUEST_CODE_REMINDER_TOMORROW)
        val secondPendingIntent =
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_REMINDER_TOMORROW, intent, PendingIntent.FLAG_NO_CREATE
            )

        intent.putExtra("requestCode", REQUEST_CODE_REMINDER_WEEK)
        val thirdPendingIntent =
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_REMINDER_WEEK, intent, PendingIntent.FLAG_NO_CREATE
            )

        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?)?.apply {
            firstPendingIntent?.let { cancel(it) }
            secondPendingIntent?.let { cancel(it) }
            thirdPendingIntent?.let { cancel(it) }
        }
    }
}