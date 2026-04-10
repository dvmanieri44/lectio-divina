package com.daviapps.liturgiadiaria.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daviapps.liturgiadiaria.viewmodel.CampoLectio
import com.daviapps.liturgiadiaria.viewmodel.LectioDivinaViewModel
import kotlinx.coroutines.delay

private val goldShimmer = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF6B4C0A), Color(0xFFAD8500), Color(0xFFD4AF37),
        Color(0xFFEDD06E), Color(0xFFD4AF37), Color(0xFFAD8500), Color(0xFF6B4C0A)
    )
)
private val goldSoft = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFAEDB3), Color(0xFFFFF8E1), Color(0xFFFAEDB3))
)

private data class Passo(
    val campo: CampoLectio,
    val numero: Int,
    val nomeLatim: String,
    val nomePortugues: String,
    val placeholder: String,
    val emoji: String
)

private val passos = listOf(
    Passo(CampoLectio.LECTIO, 1, "Lectio", "Leitura",
        "O que o texto me diz? Que palavra ou frase chamou minha atenção?", "📖"),
    Passo(CampoLectio.MEDITATIO, 2, "Meditatio", "Meditação",
        "O que essa palavra ressoa no meu coração? Que sentimentos ela desperta em mim?", "🤍"),
    Passo(CampoLectio.ORATIO, 3, "Oratio", "Oração",
        "O que quero dizer a Deus a partir deste texto? Falo com Ele de coração aberto...", "🙏"),
    Passo(CampoLectio.CONTEMPLATIO, 4, "Contemplatio", "Contemplação",
        "Simplesmente estou na presença de Deus. Deixo-me amar por Ele...", "✨"),
    Passo(CampoLectio.ACTIO, 5, "Actio", "Ação",
        "Que gesto concreto vou levar desta oração para o meu dia de hoje?", "🕊️")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LectioDivinaScreen(
    data: String,
    evangelhoReferencia: String,
    evangelhoTitulo: String = "",
    evangelhoTexto: String = "",
    readOnly: Boolean,
    onBack: () -> Unit,
    viewModel: LectioDivinaViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(data) {
        viewModel.carregarParaData(data, evangelhoReferencia)
    }

    // Vibra + volta para home ao salvar
    LaunchedEffect(state.salvoComSucesso) {
        if (state.salvoComSucesso) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            viewModel.resetarFeedback()
            delay(120)
            onBack()
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(goldShimmer)
                    .windowInsetsPadding(TopAppBarDefaults.windowInsets)
                    .height(56.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                }
                Text(
                    "Lectio Divina",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
                if (readOnly) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Somente leitura",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp).size(18.dp)
                    )
                }
            }
        },
        bottomBar = {
            if (!readOnly) {
                Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
                    Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(goldShimmer))
                        Button(
                            onClick = { viewModel.salvar() },
                            enabled = !state.salvando,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAD8500), contentColor = Color.White),
                            contentPadding = PaddingValues(vertical = 14.dp)
                        ) {
                            if (state.salvando) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Salvando...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Salvar Lectio Divina", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Banner somente leitura
            if (readOnly) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFAD8500), modifier = Modifier.size(16.dp))
                            Text("Somente leitura — apenas o dia de hoje pode ser editado", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B4C0A))
                        }
                    }
                }
            }

            // Card do Evangelho (colapsável)
            item {
                EvangelhoCard(
                    data = data,
                    referencia = evangelhoReferencia,
                    titulo = evangelhoTitulo,
                    texto = evangelhoTexto
                )
            }

            // Cinco passos
            itemsIndexed(passos) { _, passo ->
                val nota = when (passo.campo) {
                    CampoLectio.LECTIO       -> state.lectioNota
                    CampoLectio.MEDITATIO    -> state.meditatioNota
                    CampoLectio.ORATIO       -> state.oratioNota
                    CampoLectio.CONTEMPLATIO -> state.contemplatiaNota
                    CampoLectio.ACTIO        -> state.actioNota
                }
                PassoCard(
                    passo = passo,
                    nota = nota,
                    readOnly = readOnly,
                    onNotaChange = { viewModel.atualizar(passo.campo, it) }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

// ─── Card do Evangelho ────────────────────────────────────────────────────────

@Composable
private fun EvangelhoCard(data: String, referencia: String, titulo: String, texto: String) {
    var expandido by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(tween(300)),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Faixa dourada no topo
            Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(goldShimmer))

            // Cabeçalho clicável
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandido = !expandido }
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(goldSoft, RoundedCornerShape(5.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(data, style = MaterialTheme.typography.labelSmall, color = Color(0xFF6B4C0A), fontWeight = FontWeight.Bold)
                        }
                        Text("📖", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        referencia.ifBlank { "Evangelho do dia" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFAD8500)
                    )
                    if (titulo.isNotBlank()) {
                        Text(titulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (!expandido) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Toque para ler o Evangelho completo",
                            style = MaterialTheme.typography.labelSmall,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFFD4AF37)
                        )
                    }
                }
                Icon(
                    if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFFD4AF37)
                )
            }

            // Texto expandido
            AnimatedVisibility(
                visible = expandido,
                enter = expandVertically(tween(300)),
                exit = shrinkVertically(tween(250))
            ) {
                Column(modifier = Modifier.padding(horizontal = 18.dp).padding(bottom = 18.dp)) {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(goldShimmer))
                    Spacer(modifier = Modifier.height(14.dp))
                    if (texto.isNotBlank()) {
                        Text(
                            texto,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 26.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        Text(
                            "Texto do Evangelho disponível após carregar a liturgia",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ─── Card de cada passo ───────────────────────────────────────────────────────

@Composable
private fun PassoCard(passo: Passo, nota: String, readOnly: Boolean, onNotaChange: (String) -> Unit) {
    val temNota = nota.isNotBlank()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            if (temNota) {
                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(goldShimmer))
            }
            Column(modifier = Modifier.padding(16.dp)) {
                // Cabeçalho compacto
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(goldShimmer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(passo.numero.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(passo.nomeLatim, fontWeight = FontWeight.Bold, color = Color(0xFFAD8500), fontSize = 15.sp)
                        Text(passo.nomePortugues, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(passo.emoji, fontSize = 18.sp)
                    if (temNota) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFD4AF37), modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(goldShimmer))
                Spacer(modifier = Modifier.height(12.dp))

                if (readOnly) {
                    if (nota.isNotBlank()) {
                        Text(nota, style = MaterialTheme.typography.bodyMedium, lineHeight = 24.sp, color = MaterialTheme.colorScheme.onSurface)
                    } else {
                        Text("Não preenchido", style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    }
                } else {
                    // Campo estilo pergaminho — sem borda, convidativo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFFAF0), RoundedCornerShape(10.dp))
                            .padding(14.dp)
                            .defaultMinSize(minHeight = 110.dp)
                    ) {
                        if (nota.isEmpty()) {
                            Text(
                                passo.placeholder,
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    lineHeight = 24.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = Color(0xFFC4A45A)
                                )
                            )
                        }
                        BasicTextField(
                            value = nota,
                            onValueChange = onNotaChange,
                            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 110.dp),
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                lineHeight = 24.sp,
                                color = Color(0xFF3E2A00)
                            ),
                            minLines = 4
                        )
                    }
                }
            }
        }
    }
}
