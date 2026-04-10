package com.daviapps.liturgiadiaria.ui.screen

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daviapps.liturgiadiaria.data.model.Leitura
import com.daviapps.liturgiadiaria.data.model.LiturgiaResponse
import com.daviapps.liturgiadiaria.data.model.Salmo
import com.daviapps.liturgiadiaria.viewmodel.CitacaoViewModel
import com.daviapps.liturgiadiaria.viewmodel.LiturgiaViewModel
import com.daviapps.liturgiadiaria.viewmodel.UiState

// ─── Gradientes dourados ──────────────────────────────────────────────────────

private val goldShimmer = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF6B4C0A), Color(0xFFAD8500), Color(0xFFD4AF37),
        Color(0xFFEDD06E), Color(0xFFD4AF37), Color(0xFFAD8500), Color(0xFF6B4C0A)
    )
)

private val goldSoft = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFAEDB3), Color(0xFFFFF8E1), Color(0xFFFAEDB3))
)

private data class LeituraTab(
    val titulo: String,
    val conteudo: @Composable () -> Unit
)

// ─── Tela principal ───────────────────────────────────────────────────────────

@Composable
fun LiturgiaScreen(
    viewModel: LiturgiaViewModel = viewModel(),
    citacaoViewModel: CitacaoViewModel = viewModel(),
    onAbrirLectioHoje: () -> Unit = {},
    onAbrirListaLectio: () -> Unit = {},
    onAbrirCitacoes: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var drawerAberto by remember { mutableStateOf(false) }
    var mostrarConfiguracoes by remember { mutableStateOf(false) }
    var mostrarSobre by remember { mutableStateOf(false) }
    var mostrarAdicionarCitacao by remember { mutableStateOf(false) }
    var citacaoRefAtual by remember { mutableStateOf("") }
    var citacaoDataAtual by remember { mutableStateOf("") }

    if (mostrarConfiguracoes) {
        ConfiguracoesDialog(onDismiss = { mostrarConfiguracoes = false })
    }
    if (mostrarSobre) {
        SobreDialog(onDismiss = { mostrarSobre = false })
    }
    if (mostrarAdicionarCitacao) {
        AdicionarCitacaoDialog(
            evangelhoReferencia = citacaoRefAtual,
            onDismiss = { mostrarAdicionarCitacao = false },
            onSalvar = { texto ->
                citacaoViewModel.adicionar(texto, citacaoRefAtual, citacaoDataAtual)
            }
        )
    }

    fun compartilharEvangelho() {
        val state = uiState
        if (state !is UiState.Success) return
        val ev = state.liturgia.leituras.evangelho.firstOrNull() ?: return
        val texto = buildString {
            appendLine("📖 ${ev.referencia}")
            if (ev.titulo.isNotBlank()) appendLine(ev.titulo)
            appendLine()
            append(ev.texto)
            appendLine()
            appendLine()
            append("— Liturgia Diária")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, texto)
        }
        context.startActivity(Intent.createChooser(intent, "Compartilhar Evangelho"))
    }

    BackHandler(enabled = drawerAberto) { drawerAberto = false }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = { GoldTopBar(onMenuClick = { drawerAberto = true }, onRefresh = { viewModel.carregarLiturgia() }) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAbrirLectioHoje,
                    containerColor = Color(0xFFAD8500),
                    contentColor = Color.White,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Lectio")
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (val state = uiState) {
                    is UiState.Loading -> LoadingState()
                    is UiState.Error   -> ErrorState(state.message) { viewModel.carregarLiturgia() }
                    is UiState.Success -> LiturgiaContent(
                        liturgia = state.liturgia,
                        onAdicionarCitacao = { ref, data ->
                            citacaoRefAtual = ref
                            citacaoDataAtual = data
                            mostrarAdicionarCitacao = true
                        }
                    )
                }
            }
        }

        // Scrim
        AnimatedVisibility(
            visible = drawerAberto,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(250))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(indication = null, interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }) {
                        drawerAberto = false
                    }
            )
        }

        // Menu lateral direito
        AnimatedVisibility(
            visible = drawerAberto,
            modifier = Modifier.align(Alignment.CenterEnd),
            enter = slideInHorizontally(tween(300)) { it },
            exit = slideOutHorizontally(tween(250)) { it }
        ) {
            MenuLateral(
                onFechar = { drawerAberto = false },
                onLectioDivina = { drawerAberto = false; onAbrirListaLectio() },
                onCitacoes = { drawerAberto = false; onAbrirCitacoes() },
                onCompartilhar = { drawerAberto = false; compartilharEvangelho() },
                onConfiguracoes = { drawerAberto = false; mostrarConfiguracoes = true },
                onSobre = { drawerAberto = false; mostrarSobre = true }
            )
        }
    }
}

// ─── TopAppBar dourado ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoldTopBar(onMenuClick: () -> Unit, onRefresh: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(goldShimmer)
            .windowInsetsPadding(TopAppBarDefaults.windowInsets)
            .height(56.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Liturgia Diária",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(start = 16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "Atualizar", tint = Color.White)
            }
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Outlined.Menu, contentDescription = "Menu", tint = Color.White)
            }
        }
    }
}

// ─── Menu lateral direito ─────────────────────────────────────────────────────

@Composable
private fun MenuLateral(
    onFechar: () -> Unit,
    onLectioDivina: () -> Unit,
    onCitacoes: () -> Unit,
    onCompartilhar: () -> Unit,
    onConfiguracoes: () -> Unit,
    onSobre: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(goldShimmer)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Text(text = "✝", fontSize = 32.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Liturgia Diária",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Palavra do Senhor",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(goldShimmer))
            Spacer(modifier = Modifier.height(8.dp))

            MenuLateralItem(icon = Icons.Outlined.MenuBook, label = "Lectio Divina", destaque = true, onClick = onLectioDivina)
            MenuLateralItem(icon = Icons.Outlined.FavoriteBorder, label = "Minhas Citações", onClick = onCitacoes)
            MenuLateralItem(icon = Icons.Outlined.Share, label = "Compartilhar Evangelho", onClick = onCompartilhar)
            MenuLateralItem(icon = Icons.Outlined.Settings, label = "Configurações", onClick = onConfiguracoes)

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color(0xFFEDD06E).copy(alpha = 0.4f)
            )

            MenuLateralItem(icon = Icons.Outlined.Info, label = "Sobre o app", onClick = onSobre)

            Spacer(modifier = Modifier.weight(1f))

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(goldShimmer))
            Text(
                text = "© Liturgia Diária",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFAD8500),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MenuLateralItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    destaque: Boolean = false
) {
    val bgModifier = if (destaque) Modifier.background(brush = goldSoft) else Modifier

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(bgModifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (destaque) Color(0xFF6B4C0A) else Color(0xFFAD8500),
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (destaque) FontWeight.SemiBold else FontWeight.Normal,
            color = if (destaque) Color(0xFF6B4C0A) else MaterialTheme.colorScheme.onSurface
        )
    }
}

// ─── Estados ─────────────────────────────────────────────────────────────────

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = Color(0xFFD4AF37), strokeWidth = 3.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Carregando a liturgia do dia...", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("✝", fontSize = 48.sp, color = Color(0xFFD4AF37))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Não foi possível carregar a liturgia",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAD8500))
        ) {
            Text("Tentar novamente", color = Color.White)
        }
    }
}

// ─── Conteúdo principal ───────────────────────────────────────────────────────

@Composable
private fun LiturgiaContent(
    liturgia: LiturgiaResponse,
    onAdicionarCitacao: (ref: String, data: String) -> Unit
) {
    val tabs = remember(liturgia) { buildTabs(liturgia) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val isEvangelhoTab = selectedTab == 0 && liturgia.leituras.evangelho.isNotEmpty()
    val evangelhoRef = liturgia.leituras.evangelho.firstOrNull()?.referencia.orEmpty()
    val liturgiaData = liturgia.data

    Column(modifier = Modifier.fillMaxSize()) {
        LiturgiaHeader(data = liturgia.data, liturgiaNome = liturgia.liturgia, cor = liturgia.cor)

        if (tabs.size <= 4) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color(0xFFAD8500),
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(3.dp)
                            .background(goldShimmer)
                    )
                },
                divider = { HorizontalDivider(color = Color(0xFFEDD06E).copy(alpha = 0.4f)) }
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                tab.titulo,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color(0xFFAD8500) else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    )
                }
            }
        } else {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color(0xFFAD8500),
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(3.dp)
                            .background(goldShimmer)
                    )
                },
                divider = { HorizontalDivider(color = Color(0xFFEDD06E).copy(alpha = 0.4f)) }
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                tab.titulo,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color(0xFFAD8500) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        }

        // Tab content
        Box(modifier = Modifier.weight(1f)) {
            tabs[selectedTab].conteudo()
        }

        // Botão Grifar — só no tab do Evangelho
        if (isEvangelhoTab) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                OutlinedButton(
                    onClick = { onAdicionarCitacao(evangelhoRef, liturgiaData) },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.5.dp, goldShimmer),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6B4C0A))
                ) {
                    Text(
                        "✦  Grifar citação",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

private fun buildTabs(liturgia: LiturgiaResponse): List<LeituraTab> {
    val tabs = mutableListOf<LeituraTab>()
    if (liturgia.leituras.evangelho.isNotEmpty()) {
        val l = liturgia.leituras.evangelho
        tabs.add(LeituraTab("Evangelho") { LeituraListContent(l, isEvangelho = true) })
    }
    if (liturgia.leituras.primeiraLeitura.isNotEmpty()) {
        val l = liturgia.leituras.primeiraLeitura
        tabs.add(LeituraTab("1ª Leitura") { LeituraListContent(l, isEvangelho = false) })
    }
    if (liturgia.leituras.salmo.isNotEmpty()) {
        val s = liturgia.leituras.salmo
        tabs.add(LeituraTab("Salmo") { SalmoListContent(s) })
    }
    if (liturgia.leituras.segundaLeitura.isNotEmpty()) {
        val l = liturgia.leituras.segundaLeitura
        tabs.add(LeituraTab("2ª Leitura") { LeituraListContent(l, isEvangelho = false) })
    }
    return tabs
}

// ─── Header ───────────────────────────────────────────────────────────────────

@Composable
private fun LiturgiaHeader(data: String, liturgiaNome: String, cor: String) {
    val litColor = liturgicalColor(cor)
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(goldShimmer))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(litColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cor,
                        style = MaterialTheme.typography.labelSmall,
                        color = litColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = liturgiaNome,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Box(
                    modifier = Modifier
                        .background(brush = goldSoft, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = data,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF6B4C0A),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(goldShimmer))
    }
}

// ─── Listas ───────────────────────────────────────────────────────────────────

@Composable
private fun LeituraListContent(leituras: List<Leitura>, isEvangelho: Boolean) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        leituras.forEach { leitura ->
            item { LeituraCard(leitura = leitura, isEvangelho = isEvangelho) }
        }
    }
}

@Composable
private fun SalmoListContent(salmos: List<Salmo>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        salmos.forEach { salmo ->
            item { SalmoCard(salmo = salmo) }
        }
    }
}

// ─── Cards ────────────────────────────────────────────────────────────────────

@Composable
private fun LeituraCard(leitura: Leitura, isEvangelho: Boolean) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isEvangelho) 4.dp else 2.dp),
        border = BorderStroke(
            width = 1.dp,
            brush = if (isEvangelho) goldShimmer else SolidColor(Color(0xFFEDD06E).copy(alpha = 0.5f))
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            if (isEvangelho) {
                Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(goldShimmer))
            }
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (leitura.referencia.isNotBlank()) {
                            Text(
                                leitura.referencia,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFAD8500)
                            )
                        }
                        if (leitura.titulo.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                leitura.titulo,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFFD4AF37)
                    )
                }
                AnimatedVisibility(visible = expanded) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        GoldDivider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            leitura.texto,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 26.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SalmoCard(salmo: Salmo) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, SolidColor(Color(0xFFEDD06E).copy(alpha = 0.5f))),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    salmo.referencia.ifBlank { "Salmo" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFAD8500),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFFD4AF37)
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    if (salmo.refrao.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(brush = goldSoft, shape = RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                "R/. ${salmo.refrao}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF6B4C0A),
                                lineHeight = 22.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    if (salmo.texto.isNotBlank()) {
                        GoldDivider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            salmo.texto,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 26.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// ─── Auxiliares ───────────────────────────────────────────────────────────────

@Composable
private fun GoldDivider() {
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(goldShimmer))
}

private fun liturgicalColor(cor: String): Color = when {
    cor.contains("branco", ignoreCase = true)   -> Color(0xFFD4AF37)
    cor.contains("vermelho", ignoreCase = true) -> Color(0xFFC62828)
    cor.contains("roxo", ignoreCase = true)     -> Color(0xFF7B1FA2)
    cor.contains("verde", ignoreCase = true)    -> Color(0xFF2E7D32)
    cor.contains("rosa", ignoreCase = true)     -> Color(0xFFE91E8C)
    cor.contains("preto", ignoreCase = true)    -> Color(0xFF424242)
    else                                         -> Color(0xFFAD8500)
}
