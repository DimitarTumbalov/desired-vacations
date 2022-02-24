package com.synergygfs.desiredvacations

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.util.Log
import com.synergygfs.desiredvacations.data.models.Vacation
import com.synergygfs.desiredvacations.ui.VacationReminderReceiver
import java.util.*

@SuppressLint("UnspecifiedImmutableFlag")
class AlarmManager(private var context: Context) {

    fun doRemindersExist(vacation: Vacation): Boolean {
        val bytes = ParcelableUtils.marshall(vacation)

        // Create intent
        val intent = Intent(
            context,
            VacationReminderReceiver::class.java
        )

        intent.putExtra("vacation", bytes)

        intent.putExtra("requestCode", Constants.REQUEST_CODE_FIRST_REMINDER)
        val firstPendingIntent =
            PendingIntent.getBroadcast(
                context,
                Constants.REQUEST_CODE_FIRST_REMINDER, intent, PendingIntent.FLAG_NO_CREATE
            )

        intent.putExtra("requestCode", Constants.REQUEST_CODE_SECOND_REMINDER)
        val secondPendingIntent =
            PendingIntent.getBroadcast(
                context,
                Constants.REQUEST_CODE_SECOND_REMINDER, intent, PendingIntent.FLAG_NO_CREATE
            )

        intent.putExtra("requestCode", Constants.REQUEST_CODE_THIRD_REMINDER)
        val thirdPendingIntent =
            PendingIntent.getBroadcast(
                context,
                Constants.REQUEST_CODE_THIRD_REMINDER, intent, PendingIntent.FLAG_NO_CREATE
            )

        intent.putExtra("requestCode", Constants.REQUEST_CODE_FORTH_REMINDER)
        val forthPendingIntent =
            PendingIntent.getBroadcast(
                context,
                Constants.REQUEST_CODE_FORTH_REMINDER, intent, PendingIntent.FLAG_NO_CREATE
            )

        return firstPendingIntent != null || secondPendingIntent != null || thirdPendingIntent != null || forthPendingIntent != null
    }

    fun setReminder(vacation: Vacation) {
        // Convert Vacation to ByteArray
        val bytes = ParcelableUtils.marshall(vacation)

        // Create intent
        val intent = Intent(
            context,
            VacationReminderReceiver::class.java
        )
        intent.putExtra("vacation", bytes)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

        val calendar = Calendar.getInstance()
        val now = calendar.time
        val vacationDate = vacation.date

        Log.d("TESTING", "now: $now, vacation: $vacationDate")

        when {
            vacationDate.before(now) -> { // Schedule and immediately show the remainder that vacation has started
                intent.putExtra("requestCode", Constants.REQUEST_CODE_FIRST_REMINDER)

                val pendingIntentFirstReminder = PendingIntent.getBroadcast(
                    context,
                    Constants.REQUEST_CODE_FIRST_REMINDER,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT
                )

                alarmManager?.set(
                    AlarmManager.RTC_WAKEUP,
                    vacationDate.time,
                    pendingIntentFirstReminder
                )
            }
            DateUtils.isToday(vacationDate.time) -> { // Schedule and immediately show the remainder that vacation starts today

                intent.putExtra("requestCode", Constants.REQUEST_CODE_FIRST_REMINDER)

                val pendingIntentSecondReminder = PendingIntent.getBroadcast(
                    context,
                    Constants.REQUEST_CODE_SECOND_REMINDER,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT
                )

                alarmManager?.set(AlarmManager.RTC_WAKEUP, now.time, pendingIntentSecondReminder)
            }
            else -> {
                calendar.time = vacationDate
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                val vacationDateRemainder1 = calendar.time

                intent.putExtra("requestCode", Constants.REQUEST_CODE_THIRD_REMINDER)

                val pendingIntentThirdReminder = PendingIntent.getBroadcast(
                    context,
                    Constants.REQUEST_CODE_THIRD_REMINDER,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT
                )

                alarmManager?.set(
                    AlarmManager.RTC_WAKEUP,
                    vacationDateRemainder1.time,
                    pendingIntentThirdReminder
                )

                calendar.add(Calendar.DAY_OF_MONTH, -6)
                val vacationDateRemainder7 = calendar.time

                vacationDateRemainder7.after(now).let {
                    if (it) {
                        intent.putExtra(
                            "requestCode",
                            Constants.REQUEST_CODE_FORTH_REMINDER
                        )

                        val pendingIntentForthReminder = PendingIntent.getBroadcast(
                            context,
                            Constants.REQUEST_CODE_FORTH_REMINDER,
                            intent,
                            PendingIntent.FLAG_ONE_SHOT
                        )

                        alarmManager?.set(
                            AlarmManager.RTC_WAKEUP,
                            vacationDateRemainder7.time,
                            pendingIntentForthReminder
                        )
                    }
                }
            }
        }
    }

    fun cancelReminders(vacation: Vacation) {
        val bytes = ParcelableUtils.marshall(vacation)

        // Create intent
        val intent = Intent(
            context,
            VacationReminderReceiver::class.java
        )

        intent.putExtra("vacation", bytes)

        intent.putExtra("requestCode", Constants.REQUEST_CODE_FIRST_REMINDER)
        val firstPendingIntent =
            PendingIntent.getBroadcast(
                context,
                Constants.REQUEST_CODE_FIRST_REMINDER, intent, PendingIntent.FLAG_NO_CREATE
            )

        intent.putExtra("requestCode", Constants.REQUEST_CODE_SECOND_REMINDER)
        val secondPendingIntent =
            PendingIntent.getBroadcast(
                context,
                Constants.REQUEST_CODE_SECOND_REMINDER, intent, PendingIntent.FLAG_NO_CREATE
            )

        intent.putExtra("requestCode", Constants.REQUEST_CODE_THIRD_REMINDER)
        val thirdPendingIntent =
            PendingIntent.getBroadcast(
                context,
                Constants.REQUEST_CODE_THIRD_REMINDER, intent, PendingIntent.FLAG_NO_CREATE
            )

        intent.putExtra("requestCode", Constants.REQUEST_CODE_FORTH_REMINDER)
        val forthPendingIntent =
            PendingIntent.getBroadcast(
                context,
                Constants.REQUEST_CODE_FORTH_REMINDER, intent, PendingIntent.FLAG_NO_CREATE
            )

        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?)?.apply {
            firstPendingIntent?.let { cancel(it) }
            secondPendingIntent?.let { cancel(it) }
            thirdPendingIntent?.let { cancel(it) }
            forthPendingIntent?.let { cancel(it) }
        }
    }
}