package com.daviapps.liturgiadiaria.worker

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkScheduler {

    const val WORK_NAME = "lectio_reminder_daily"
    const val MOTIVATIONAL_WORK_NAME = "motivational_scheduler_daily"
    const val PREFS_NAME = "liturgia_prefs"
    const val KEY_NOTIF_ENABLED = "notif_enabled"
    const val KEY_NOTIF_HOUR = "notif_hour"
    const val KEY_NOTIF_MINUTE = "notif_minute"
    const val KEY_FIRST_LAUNCH = "first_launch"

    const val DEFAULT_HOUR = 7
    const val DEFAULT_MINUTE = 0

    fun scheduleIfNeeded(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean(KEY_FIRST_LAUNCH, true)

        if (isFirstLaunch) {
            prefs.edit()
                .putBoolean(KEY_FIRST_LAUNCH, false)
                .putBoolean(KEY_NOTIF_ENABLED, true)
                .putInt(KEY_NOTIF_HOUR, DEFAULT_HOUR)
                .putInt(KEY_NOTIF_MINUTE, DEFAULT_MINUTE)
                .apply()
            schedule(context, DEFAULT_HOUR, DEFAULT_MINUTE)
        }

        // Motivacional: sempre garante agendamento (KEEP = não reenfileira se já existe)
        scheduleMotivacional(context)
    }

    fun schedule(context: Context, hour: Int, minute: Int) {
        val delay = calculateDelayMs(hour, minute)

        val request = PeriodicWorkRequestBuilder<LectioReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    fun saveSettings(context: Context, enabled: Boolean, hour: Int, minute: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_NOTIF_ENABLED, enabled)
            .putInt(KEY_NOTIF_HOUR, hour)
            .putInt(KEY_NOTIF_MINUTE, minute)
            .apply()

        if (enabled) schedule(context, hour, minute) else cancel(context)
    }

    fun loadSettings(context: Context): Triple<Boolean, Int, Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return Triple(
            prefs.getBoolean(KEY_NOTIF_ENABLED, true),
            prefs.getInt(KEY_NOTIF_HOUR, DEFAULT_HOUR),
            prefs.getInt(KEY_NOTIF_MINUTE, DEFAULT_MINUTE)
        )
    }

    private fun scheduleMotivacional(context: Context) {
        // Roda todo dia às 06:55 para sortear se enviará notificação motivacional
        val delay = calculateDelayMs(6, 55)

        val request = PeriodicWorkRequestBuilder<MotivationalSchedulerWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            MOTIVATIONAL_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun calculateDelayMs(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (now >= target) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }
}
