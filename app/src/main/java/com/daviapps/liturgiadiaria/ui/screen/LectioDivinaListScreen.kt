package com.daviapps.liturgiadiaria.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daviapps.liturgiadiaria.data.local.LectioDivinaEntity
import com.daviapps.liturgiadiaria.viewmodel.LectioDivinaListViewModel

private val goldShimmerList = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF6B4C0A), Color(0xFFAD8500), Color(0xFFD4AF37),
        Color(0xFFEDD06E), Color(0xFFD4AF37), Color(0xFFAD8500), Color(0xFF6B4C0A)
    )
)

private val goldSoftList = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFAEDB3), Color(0xFFFFF8E1), Color(0xFFFAEDB3))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LectioDivinaListScreen(
    dataHoje: String,
    onBack: () -> Unit,
    onAbrirEntrada: (data: String) -> Unit,
    viewModel: LectioDivinaListViewModel = viewModel()
) {
    val entradas by viewModel.entradas.collectAsState()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(goldShimmerList)
                    .windowInsetsPadding(TopAppBarDefaults.windowInsets)
                    .height(56.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                }
                Text(
                    text = "Histórico de Lectio Divina",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (entradas.isEmpty()) {
                EstadoVazio()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(entradas, key = { it.data }) { entrada ->
                        EntradaCard(
                            entrada = entrada,
                            ehHoje = entrada.data == dataHoje,
                            onClick = { onAbrirEntrada(entrada.data) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EstadoVazio() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("✝", fontSize = 56.sp, color = Color(0xFFD4AF37))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Nenhuma Lectio Divina salva ainda",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Abra a Lectio Divina de hoje pelo botão\n+ na tela principal",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun EntradaCard(
    entrada: LectioDivinaEntity,
    ehHoje: Boolean,
    onClick: () -> Unit
) {
    val passosConcluidos = listOf(
        entrada.lectioNota, entrada.meditatioNota, entrada.oratioNota,
        entrada.contemplatiaNota, entrada.actioNota
    ).count { it.isNotBlank() }

    val preview = listOf(
        entrada.lectioNota, entrada.meditatioNota, entrada.oratioNota,
        entrada.contemplatiaNota, entrada.actioNota
    ).firstOrNull { it.isNotBlank() }.orEmpty()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (ehHoje) 4.dp else 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            brush = if (ehHoje) goldShimmerList
                    else Brush.horizontalGradient(
                        listOf(Color(0xFFEDD06E).copy(alpha = 0.4f), Color(0xFFEDD06E).copy(alpha = 0.4f))
                    )
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            if (ehHoje) {
                Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(goldShimmerList))
            }
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = entrada.data,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFAD8500)
                        )
                        if (ehHoje) {
                            Box(
                                modifier = Modifier
                                    .background(brush = goldSoftList, shape = RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "Hoje",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF6B4C0A),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    "Somente leitura",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (entrada.evangelhoReferencia.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = entrada.evangelhoReferencia,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Barra de progresso dos 5 passos
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        val nomes = listOf("L", "M", "O", "C", "A")
                        val notas = listOf(
                            entrada.lectioNota, entrada.meditatioNota, entrada.oratioNota,
                            entrada.contemplatiaNota, entrada.actioNota
                        )
                        nomes.forEachIndexed { i, nome ->
                            val preenchido = notas[i].isNotBlank()
                            Box(
                                modifier = Modifier
                                    .size(width = 28.dp, height = 22.dp)
                                    .background(
                                        brush = if (preenchido) goldShimmerList
                                                else Brush.horizontalGradient(
                                                    listOf(Color(0xFFEEEEEE), Color(0xFFEEEEEE))
                                                ),
                                        shape = RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = nome,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (preenchido) Color.White else Color(0xFFBDBDBD),
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$passosConcluidos/5",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (passosConcluidos == 5) Color(0xFFAD8500)
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (passosConcluidos == 5) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    if (preview.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = preview,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 18.sp
                        )
                    }
                }

                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Abrir",
                    tint = Color(0xFFD4AF37),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
