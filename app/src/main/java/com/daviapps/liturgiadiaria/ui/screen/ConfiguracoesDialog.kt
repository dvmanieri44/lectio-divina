package com.daviapps.liturgiadiaria.ui.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.daviapps.liturgiadiaria.worker.WorkScheduler

private val goldShimmerCfg = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF6B4C0A), Color(0xFFAD8500), Color(0xFFD4AF37),
        Color(0xFFEDD06E), Color(0xFFD4AF37), Color(0xFFAD8500), Color(0xFF6B4C0A)
    )
)
private val goldSoftCfg = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFAEDB3), Color(0xFFFFF8E1), Color(0xFFFAEDB3))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracoesDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val (savedEnabled, savedHour, savedMinute) = remember { WorkScheduler.loadSettings(context) }

    var notifEnabled by remember { mutableStateOf(savedEnabled) }
    var timePickerState = rememberTimePickerState(
        initialHour = savedHour,
        initialMinute = savedMinute,
        is24Hour = true
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 16.dp
        ) {
            Column {
                // Cabeçalho dourado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(goldShimmerCfg, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (notifEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Lembrete Diário",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            "Lectio Divina com o Evangelho",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    // Toggle de ativação
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Ativar notificação",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = notifEnabled,
                            onCheckedChange = { notifEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFFAD8500),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(goldShimmerCfg))
                    Spacer(modifier = Modifier.height(16.dp))

                    // TimePicker
                    Text(
                        "Horário do lembrete",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFFAD8500),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        TimePicker(
                            state = timePickerState,
                            colors = TimePickerDefaults.colors(
                                clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
                                clockDialSelectedContentColor = Color.White,
                                clockDialUnselectedContentColor = Color(0xFF6B4C0A),
                                selectorColor = Color(0xFFAD8500),
                                timeSelectorSelectedContainerColor = Color(0xFFFAEDB3),
                                timeSelectorSelectedContentColor = Color(0xFF6B4C0A),
                                timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    // Preview do horário selecionado
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(goldSoftCfg, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⏰  Notificação às %02d:%02d".format(
                                timePickerState.hour,
                                timePickerState.minute
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6B4C0A)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botões
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFAD8500)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD4AF37))
                        ) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                WorkScheduler.saveSettings(
                                    context,
                                    enabled = notifEnabled,
                                    hour = timePickerState.hour,
                                    minute = timePickerState.minute
                                )
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFAD8500),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Salvar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
