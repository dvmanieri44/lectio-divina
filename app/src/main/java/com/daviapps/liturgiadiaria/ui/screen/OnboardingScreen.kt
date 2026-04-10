package com.daviapps.liturgiadiaria.ui.screen

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daviapps.liturgiadiaria.worker.WorkScheduler

private val goldOnboarding = Brush.verticalGradient(
    colors = listOf(Color(0xFF6B4C0A), Color(0xFFAD8500), Color(0xFFD4AF37))
)
private val goldShimmerOb = Brush.horizontalGradient(
    colors = listOf(Color(0xFF6B4C0A), Color(0xFFAD8500), Color(0xFFD4AF37), Color(0xFFAD8500), Color(0xFF6B4C0A))
)
private val goldSoftOb = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFAEDB3), Color(0xFFFFF8E1), Color(0xFFFAEDB3))
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val context = LocalContext.current
    var step by remember { mutableIntStateOf(0) }
    var nome by remember { mutableStateOf("") }
    var idade by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFAF0)),
        contentAlignment = Alignment.Center
    ) {
        // Ornamento de fundo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .align(Alignment.TopCenter)
                .background(goldOnboarding)
        )

        AnimatedContent(
            targetState = step,
            transitionSpec = {
                slideInHorizontally(tween(350)) { it } + fadeIn(tween(350)) togetherWith
                slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(200))
            },
            label = "onboarding_step"
        ) { s ->
            when (s) {
                0 -> StepBoasVindas(onProximo = { step = 1 })
                1 -> StepNome(nome = nome, onChange = { nome = it }, onProximo = { if (nome.isNotBlank()) step = 2 })
                2 -> StepIdade(idade = idade, onChange = { idade = it }, onProximo = { if (idade.isNotBlank()) step = 3 })
                3 -> StepGenero(generoSelecionado = genero, onSelect = { genero = it }, onProximo = {
                    if (genero.isNotBlank()) {
                        salvarPerfil(context, nome, idade, genero)
                        step = 4
                    }
                })
                4 -> StepConclusao(nome = nome, onComecar = onComplete)
            }
        }

        // Indicador de progresso
        if (step in 1..3) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .size(if (step - 1 == i) 24.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (step - 1 == i) Color(0xFFAD8500) else Color(0xFFEDD06E))
                    )
                }
            }
        }
    }
}

@Composable
private fun StepBoasVindas(onProximo: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("✝", fontSize = 64.sp, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Liturgia Diária",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            "A Palavra de Deus para o seu dia",
            style = MaterialTheme.typography.bodyLarge,
            fontStyle = FontStyle.Italic,
            color = Color.White.copy(alpha = 0.9f)
        )
        Spacer(modifier = Modifier.height(80.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Antes de começar,\nprecisamos te conhecer 🙏",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF3E2A00),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Serão apenas 3 perguntas rápidas para personalizar sua experiência.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8D6E63),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                BotaoDourado("Começar", onClick = onProximo)
            }
        }
    }
}

@Composable
private fun StepNome(nome: String, onChange: (String) -> Unit, onProximo: () -> Unit) {
    StepContainer(titulo = "Como você se chama?", subtitulo = "Assim poderemos te cumprimentar pessoalmente 😊") {
        CampoParchment(
            valor = nome,
            onValorChange = onChange,
            placeholder = "Seu nome...",
            keyboardOptions = KeyboardOptions.Default
        )
        Spacer(modifier = Modifier.height(24.dp))
        BotaoDourado("Próximo →", onClick = onProximo, enabled = nome.isNotBlank())
    }
}

@Composable
private fun StepIdade(idade: String, onChange: (String) -> Unit, onProximo: () -> Unit) {
    StepContainer(titulo = "Qual a sua idade?", subtitulo = "Usaremos isso para melhorar sua experiência") {
        CampoParchment(
            valor = idade,
            onValorChange = { if (it.length <= 3 && it.all(Char::isDigit)) onChange(it) },
            placeholder = "Ex: 32",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(24.dp))
        BotaoDourado("Próximo →", onClick = onProximo, enabled = idade.isNotBlank())
    }
}

@Composable
private fun StepGenero(generoSelecionado: String, onSelect: (String) -> Unit, onProximo: () -> Unit) {
    val opcoes = listOf("Masculino", "Feminino", "Prefiro não dizer")
    StepContainer(titulo = "Com qual gênero você se identifica?", subtitulo = "") {
        opcoes.forEach { opcao ->
            val selecionado = generoSelecionado == opcao
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selecionado) Brush.horizontalGradient(listOf(Color(0xFFFAEDB3), Color(0xFFFFF8E1))) else Brush.horizontalGradient(listOf(Color(0xFFF5F5F5), Color(0xFFF5F5F5))))
                    .border(
                        width = if (selecionado) 1.5.dp else 1.dp,
                        brush = if (selecionado) goldShimmerOb else Brush.horizontalGradient(listOf(Color(0xFFE0E0E0), Color(0xFFE0E0E0))),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(opcao) }
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selecionado,
                    onClick = { onSelect(opcao) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFAD8500))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    opcao,
                    fontWeight = if (selecionado) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selecionado) Color(0xFF6B4C0A) else Color(0xFF555555)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        BotaoDourado("Próximo →", onClick = onProximo, enabled = generoSelecionado.isNotBlank())
    }
}

@Composable
private fun StepConclusao(nome: String, onComecar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("✝", fontSize = 64.sp, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Bem-vindo,\n$nome!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(80.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "\"Tua palavra é lâmpada para os meus pés\ne luz para o meu caminho.\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF6B4C0A),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
                Text(
                    "— Salmo 119, 105",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFAD8500),
                    modifier = Modifier.padding(top = 6.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                BotaoDourado("Ir para o APP ✝", onClick = onComecar)
            }
        }
    }
}

@Composable
private fun StepContainer(titulo: String, subtitulo: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        Text(
            titulo,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        if (subtitulo.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                subtitulo,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun CampoParchment(valor: String, onValorChange: (String) -> Unit, placeholder: String, keyboardOptions: KeyboardOptions) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFFAF0), RoundedCornerShape(12.dp))
            .border(1.dp, Brush.horizontalGradient(listOf(Color(0xFFEDD06E).copy(0.5f), Color(0xFFD4AF37).copy(0.5f))), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        if (valor.isEmpty()) {
            Text(placeholder, color = Color(0xFFC4A45A), fontStyle = FontStyle.Italic, fontSize = 16.sp)
        }
        androidx.compose.foundation.text.BasicTextField(
            value = valor,
            onValueChange = onValorChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 18.sp,
                color = Color(0xFF3E2A00),
                fontWeight = FontWeight.Medium
            ),
            keyboardOptions = keyboardOptions,
            singleLine = true
        )
    }
}

@Composable
private fun BotaoDourado(texto: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFAD8500),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFEDD06E).copy(alpha = 0.4f),
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Text(texto, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

private fun salvarPerfil(context: Context, nome: String, idade: String, genero: String) {
    context.getSharedPreferences(WorkScheduler.PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putBoolean("onboarding_done", true)
        .putString("user_name", nome)
        .putString("user_age", idade)
        .putString("user_gender", genero)
        .apply()
}
