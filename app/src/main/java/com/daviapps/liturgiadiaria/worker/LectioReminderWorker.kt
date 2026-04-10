package com.daviapps.liturgiadiaria.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.daviapps.liturgiadiaria.data.api.RetrofitClient
import com.daviapps.liturgiadiaria.notification.NotificationHelper

class LectioReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val liturgia = RetrofitClient.api.getLiturgiaHoje()
            val evangelho = liturgia.leituras.evangelho.firstOrNull()

            NotificationHelper.sendReminder(
                context = applicationContext,
                data = liturgia.data,
                liturgiaNome = liturgia.liturgia,
                evangelhoRef = evangelho?.referencia.orEmpty(),
                evangelhoTexto = evangelho?.texto.orEmpty()
            )
            Result.success()
        } catch (e: Exception) {
            NotificationHelper.sendBasicReminder(applicationContext)
            Result.retry()
        }
    }
}
