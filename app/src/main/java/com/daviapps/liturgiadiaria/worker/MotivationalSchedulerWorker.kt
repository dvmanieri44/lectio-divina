package com.daviapps.liturgiadiaria.worker

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Roda todo dia às 06:55.
 * 4 em 7 chances de sortear um envio no mesmo dia entre 07:01 e 21:00.
 */
class MotivationalSchedulerWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // 4/7 de chance de enviar hoje
        if (Random.nextInt(7) >= 4) return Result.success()

        val now = Calendar.getInstance()

        val windowStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 7)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val windowEnd = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 21)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val effectiveStart = maxOf(now.timeInMillis, windowStart.timeInMillis)
        val windowMs = windowEnd.timeInMillis - effectiveStart

        if (windowMs <= 0) return Result.success() // Janela já encerrou hoje

        val delayMs = (Random.nextDouble() * windowMs).toLong()

        val request = OneTimeWorkRequestBuilder<MotivationalSendWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(request)
        return Result.success()
    }
}
