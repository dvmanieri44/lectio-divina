package com.daviapps.liturgiadiaria.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.daviapps.liturgiadiaria.data.local.AppDatabase
import com.daviapps.liturgiadiaria.notification.NotificationHelper

class MotivationalSendWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_USED_IDS = "motivational_used_ids"
    }

    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        val todasCitacoes = db.citacaoDao().getAllOnce()

        val prefs = applicationContext.getSharedPreferences(WorkScheduler.PREFS_NAME, Context.MODE_PRIVATE)
        val usedIdsRaw = prefs.getStringSet(KEY_USED_IDS, emptySet()) ?: emptySet()
        val usedIds = usedIdsRaw.mapNotNull { it.toLongOrNull() }.toSet()

        val naoUsadas = todasCitacoes.filter { it.id !in usedIds }

        val escolhida = when {
            naoUsadas.isNotEmpty() -> naoUsadas.random()
            todasCitacoes.isNotEmpty() -> {
                // Todas já usadas: resetar e recomeçar
                prefs.edit().remove(KEY_USED_IDS).apply()
                todasCitacoes.random()
            }
            else -> null // Sem citações salvas
        }

        NotificationHelper.sendMotivacional(applicationContext, escolhida?.texto)

        if (escolhida != null) {
            val novasUsadas = (usedIds + escolhida.id).map { it.toString() }.toSet()
            prefs.edit().putStringSet(KEY_USED_IDS, novasUsadas).apply()
        }

        return Result.success()
    }
}
