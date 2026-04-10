package com.daviapps.liturgiadiaria.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val dialogGoldShimmer = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF6B4C0A), Color(0xFFAD8500), Color(0xFFD4AF37),
        Color(0xFFEDD06E), Color(0xFFD4AF37), Color(0xFFAD8500), Color(0xFF6B4C0A)
    )
)

@Composable
fun AdicionarCitacaoDialog(
    evangelhoReferencia: String,
    onDismiss: () -> Unit,
    onSalvar: (texto: String) -> Unit
) {
    var texto by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFFAF7F0),
            shadowElevation = 12.dp
        ) {
            Column {
                // Cabeçalho dourado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(dialogGoldShimmer, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Column {
                        Text(
                            text = "✦ Grifar citação",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (evangelhoReferencia.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = evangelhoReferencia,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Digite o trecho que te marcou:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B4C0A),
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFFAF0), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        if (texto.isEmpty()) {
                            Text(
                                text = "\"Escreva aqui a passagem que tocou seu coração...\"",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    color = Color(0xFFAD8500).copy(alpha = 0.5f),
                                    fontStyle = FontStyle.Italic
                                )
                            )
                        }
                        BasicTextField(
                            value = texto,
                            onValueChange = { texto = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp),
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                color = Color(0xFF3E2800),
                                lineHeight = 22.sp
                            ),
                            cursorBrush = SolidColor(Color(0xFFAD8500))
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFAD8500)
                            )
                        ) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                if (texto.isNotBlank()) {
                                    onSalvar(texto.trim())
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFAD8500),
                                disabledContainerColor = Color(0xFFAD8500).copy(alpha = 0.5f)
                            ),
                            enabled = texto.isNotBlank()
                        ) {
                            Text("Salvar", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
