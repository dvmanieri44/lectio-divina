package com.daviapps.liturgiadiaria.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.daviapps.liturgiadiaria.MainActivity
import com.daviapps.liturgiadiaria.R
import com.daviapps.liturgiadiaria.worker.WorkScheduler

object NotificationHelper {

    const val CHANNEL_ID = "lectio_channel"
    const val NOTIFICATION_ID = 42
    const val MOTIVATIONAL_ID = 43

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Liturgia Diária",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Lembretes e mensagens da Liturgia Diária"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun sendReminder(
        context: Context,
        data: String,
        liturgiaNome: String,
        evangelhoRef: String,
        evangelhoTexto: String
    ) {
        val nome = getUserName(context)
        val pendingIntent = buildPendingIntent(context)

        val titulo = if (nome.isNotBlank())
            "E aí $nome, não esquece da sua Lectio não! ✝"
        else
            "Não esquece da sua Lectio hoje! ✝"

        val corpo = buildString {
            if (evangelhoRef.isNotBlank()) {
                append("📖 Evangelho: $evangelhoRef")
            }
            if (liturgiaNome.isNotBlank()) {
                append(" — $liturgiaNome")
            }
        }.trim()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(titulo)
            .setContentText(corpo.ifBlank { "Separe um momento para rezar com a Palavra de Deus." })
            .setStyle(NotificationCompat.BigTextStyle().bigText(corpo.ifBlank { "Separe um momento para rezar com a Palavra de Deus." }))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        send(context, NOTIFICATION_ID, notification)
    }

    fun sendBasicReminder(context: Context) {
        val nome = getUserName(context)
        val pendingIntent = buildPendingIntent(context)

        val titulo = if (nome.isNotBlank())
            "E aí $nome, não esquece da sua Lectio não! ✝"
        else
            "Não esquece da sua Lectio hoje! ✝"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(titulo)
            .setContentText("Separe um momento para ler e orar com a Palavra de Deus hoje.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        send(context, NOTIFICATION_ID, notification)
    }

    fun sendMotivacional(context: Context, citacao: String?) {
        val nome = getUserName(context)
        val pendingIntent = buildPendingIntent(context)

        val titulo = if (nome.isNotBlank())
            "Ei $nome, Jesus se lembrou de você ✝"
        else
            "Jesus se lembrou de você ✝"

        val corpo = citacao?.takeIf { it.isNotBlank() }
            ?.let { "\"$it\"" }
            ?: "\"Tua palavra é lâmpada para meus pés e luz para o meu caminho.\" — Sl 119,105"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(titulo)
            .setContentText(corpo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(corpo))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        send(context, MOTIVATIONAL_ID, notification)
    }

    private fun getUserName(context: Context): String =
        context.getSharedPreferences(WorkScheduler.PREFS_NAME, Context.MODE_PRIVATE)
            .getString("user_name", "").orEmpty()

    private fun buildPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_lectio", true)
        }
        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun send(context: Context, id: Int, notification: android.app.Notification) {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else true

        if (hasPermission) {
            NotificationManagerCompat.from(context).notify(id, notification)
        }
    }
}
