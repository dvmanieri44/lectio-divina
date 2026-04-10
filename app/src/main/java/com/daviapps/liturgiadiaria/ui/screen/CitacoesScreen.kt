package com.daviapps.liturgiadiaria.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daviapps.liturgiadiaria.data.local.CitacaoEntity
import com.daviapps.liturgiadiaria.viewmodel.CitacaoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val citacoesGoldShimmer = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF6B4C0A), Color(0xFFAD8500), Color(0xFFD4AF37),
        Color(0xFFEDD06E), Color(0xFFD4AF37), Color(0xFFAD8500), Color(0xFF6B4C0A)
    )
)

@Composable
fun CitacoesScreen(
    viewModel: CitacaoViewModel = viewModel(),
    onBack: () -> Unit
) {
    val citacoes by viewModel.citacoes.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFAF7F0))) {
        // TopBar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(citacoesGoldShimmer)
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(56.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
            Text(
                text = "Minhas Citações",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (citacoes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✦", fontSize = 48.sp, color = Color(0xFFD4AF37))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Nenhuma citação grifada ainda",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF6B4C0A),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No Evangelho do dia, toque em\n\"Grifar citação\" para salvar\npassagens que te marcaram.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFAD8500).copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(citacoes, key = { it.id }) { citacao ->
                    CitacaoCard(
                        citacao = citacao,
                        onDeletar = { viewModel.deletar(citacao) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CitacaoCard(citacao: CitacaoEntity, onDeletar: () -> Unit) {
    var confirmarDelete by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(citacoesGoldShimmer)
            )
            Column(modifier = Modifier.padding(18.dp)) {
                // Citação principal
                Text(
                    text = "\"${citacao.texto}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF3E2800),
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(citacoesGoldShimmer))
                Spacer(modifier = Modifier.height(8.dp))

                // Referência e data — discretos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = citacao.evangelhoReferencia.ifBlank { "Evangelho" },
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFAD8500),
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (citacao.evangelhoData.isNotBlank()) {
                            Text(
                                text = citacao.evangelhoData,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFAD8500).copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        if (confirmarDelete) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                TextButton(
                                    onClick = { confirmarDelete = false },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                ) {
                                    Text("Não", color = Color(0xFFAD8500), fontSize = 12.sp)
                                }
                                TextButton(
                                    onClick = onDeletar,
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                ) {
                                    Text("Remover", color = Color(0xFFC62828), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        } else {
                            IconButton(
                                onClick = { confirmarDelete = true },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Remover",
                                    tint = Color(0xFFAD8500).copy(alpha = 0.5f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
