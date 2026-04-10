package com.daviapps.liturgiadiaria.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private val goldShimmerSobre = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF6B4C0A), Color(0xFFAD8500), Color(0xFFD4AF37),
        Color(0xFFEDD06E), Color(0xFFD4AF37), Color(0xFFAD8500), Color(0xFF6B4C0A)
    )
)
private val goldSoftSobre = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFAEDB3), Color(0xFFFFF8E1), Color(0xFFFAEDB3))
)

@Composable
fun SobreDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column {
                // Cabeçalho dourado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(goldShimmerSobre, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .padding(20.dp)
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color.White)
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("✝", fontSize = 36.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Liturgia Diária",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                        Text(
                            "Sobre o Aplicativo",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                // Conteúdo scrollável
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Sobre o app
                    SecaoSobre(titulo = "Nossa Missão ✝") {
                        Text(
                            "Este aplicativo foi feito de forma altruísta, com muito amor e fé, para ajudar a todos a se manterem conectados com Deus através da Palavra diária.",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Que o Evangelho de cada dia seja uma luz no seu caminho e que a Lectio Divina o leve a um encontro cada vez mais profundo com o Senhor.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            lineHeight = 24.sp,
                            color = Color(0xFF6B4C0A)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(goldSoftSobre, RoundedCornerShape(10.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                "\"Tua palavra é lâmpada para os meus pés\ne luz para o meu caminho.\"\n— Sl 119, 105",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                                color = Color(0xFF6B4C0A),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // Contato
                    SecaoSobre(titulo = "Dúvidas, Sugestões ou Elogios?") {
                        Text(
                            "Adoramos ouvir você! Mande uma mensagem, sua opinião faz esse projeto crescer.",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val tel = "5516997037115"
                                val msg = Uri.encode("Olá! Tenho uma mensagem sobre o app Liturgia Diária 🙏")
                                val uri = Uri.parse("https://wa.me/$tel?text=$msg")
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                        ) {
                            Text("📱  Falar no WhatsApp — (16) 99703-7115", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // O que é Lectio Divina
                    SecaoSobre(titulo = "O que é a Lectio Divina?") {
                        Text(
                            "A Lectio Divina (\"Leitura Divina\") é uma prática milenar de oração com a Sagrada Escritura, que nos conduz do texto sagrado até o encontro pessoal com o Senhor.",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        val passos = listOf(
                            Triple("1. Lectio", "Leitura", "Leia o texto sagrado lentamente, com atenção e reverência. Deixe cada palavra entrar em seu coração. Releia quantas vezes precisar."),
                            Triple("2. Meditatio", "Meditação", "Fique com a palavra ou frase que tocou seu coração. O que ela desperta? Que memórias, sentimentos ou imagens surgem?"),
                            Triple("3. Oratio", "Oração", "Responda a Deus com as palavras que nascem do seu coração. É um diálogo de amor — não há respostas certas ou erradas."),
                            Triple("4. Contemplatio", "Contemplação", "Simplesmente esteja na presença de Deus. Em silêncio interior, descanse nEle. Não são necessárias palavras — apenas seja."),
                            Triple("5. Actio", "Ação", "Como esta oração transforma o seu dia? Que gesto concreto, pequeno ou grande, nasce desta experiência?")
                        )

                        passos.forEach { (latim, portugues, descricao) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(modifier = Modifier.width(90.dp)) {
                                    Text(latim, fontWeight = FontWeight.Bold, color = Color(0xFFAD8500), fontSize = 13.sp)
                                    Text(portugues, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    descricao,
                                    style = MaterialTheme.typography.bodySmall,
                                    lineHeight = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (latim != passos.last().first) {
                                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFEDD06E).copy(alpha = 0.4f)))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Feito com fé e amor ✝",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFAD8500),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun SecaoSobre(titulo: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f).height(1.dp).background(goldShimmerSobre))
            Text(
                "  $titulo  ",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFAD8500)
            )
            Box(modifier = Modifier.weight(1f).height(1.dp).background(goldShimmerSobre))
        }
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}
