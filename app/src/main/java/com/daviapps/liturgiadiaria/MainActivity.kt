package com.daviapps.liturgiadiaria

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandlergit
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daviapps.liturgiadiaria.notification.NotificationHelper
import com.daviapps.liturgiadiaria.ui.screen.CitacoesScreen
import com.daviapps.liturgiadiaria.ui.screen.LectioDivinaListScreen
import com.daviapps.liturgiadiaria.ui.screen.LectioDivinaScreen
import com.daviapps.liturgiadiaria.ui.screen.LiturgiaScreen
import com.daviapps.liturgiadiaria.ui.screen.OnboardingScreen
import com.daviapps.liturgiadiaria.ui.theme.LiturgiaDiariaTheme
import com.daviapps.liturgiadiaria.viewmodel.CitacaoViewModel
import com.daviapps.liturgiadiaria.viewmodel.LiturgiaViewModel
import com.daviapps.liturgiadiaria.viewmodel.UiState
import com.daviapps.liturgiadiaria.worker.WorkScheduler
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class AppScreen {
    object Liturgia : AppScreen()
    object LectioDivinaLista : AppScreen()
    object Citacoes : AppScreen()
    data class LectioDivina(
        val data: String,
        val evangelhoRef: String,
        val evangelhoTitulo: String = "",
        val evangelhoTexto: String = "",
        val readOnly: Boolean
    ) : AppScreen()
}

fun dataDeHoje(): String =
    SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date())

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createChannel(this)
        WorkScheduler.scheduleIfNeeded(this)
        val openLectio = intent.getBooleanExtra("open_lectio", false)
        enableEdgeToEdge()
        setContent {
            LiturgiaDiariaTheme {
                RequestNotificationPermission()
                AppNavigation(startOnLectio = openLectio)
            }
        }
    }
}

@Composable
private fun RequestNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
        LaunchedEffect(Unit) { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }
    }
}

@Composable
fun AppNavigation(startOnLectio: Boolean = false) {
    val context = LocalContext.current

    var onboardingFeito by remember {
        mutableStateOf(
            context.getSharedPreferences(WorkScheduler.PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean("onboarding_done", false)
        )
    }

    if (!onboardingFeito) {
        OnboardingScreen(onComplete = { onboardingFeito = true })
        return
    }

    val backstack: MutableList<AppScreen> = remember {
        if (startOnLectio)
            mutableStateListOf(AppScreen.Liturgia, AppScreen.LectioDivina(dataDeHoje(), "", readOnly = false))
        else
            mutableStateListOf(AppScreen.Liturgia)
    }

    val tela = backstack.last()
    val liturgiaViewModel: LiturgiaViewModel = viewModel()
    val citacaoViewModel: CitacaoViewModel = viewModel()

    fun navegar(destino: AppScreen) = backstack.add(destino)
    fun voltar() { if (backstack.size > 1) backstack.removeAt(backstack.lastIndex) }

    BackHandler(enabled = backstack.size > 1) { voltar() }

    fun evangelhoFromState(): Triple<String, String, String> {
        val s = liturgiaViewModel.uiState.value
        if (s !is UiState.Success) return Triple("", "", "")
        val ev = s.liturgia.leituras.evangelho.firstOrNull()
        return Triple(ev?.referencia.orEmpty(), ev?.titulo.orEmpty(), ev?.texto.orEmpty())
    }
    fun dataLiturgia(): String {
        val s = liturgiaViewModel.uiState.value
        return if (s is UiState.Success && s.liturgia.data.isNotBlank()) s.liturgia.data else dataDeHoje()
    }

    when (val t = tela) {
        AppScreen.Liturgia -> {
            LiturgiaScreen(
                viewModel = liturgiaViewModel,
                citacaoViewModel = citacaoViewModel,
                onAbrirLectioHoje = {
                    val (ref, titulo, texto) = evangelhoFromState()
                    navegar(AppScreen.LectioDivina(
                        data = dataLiturgia(),
                        evangelhoRef = ref,
                        evangelhoTitulo = titulo,
                        evangelhoTexto = texto,
                        readOnly = false
                    ))
                },
                onAbrirListaLectio = { navegar(AppScreen.LectioDivinaLista) },
                onAbrirCitacoes = { navegar(AppScreen.Citacoes) }
            )
        }

        AppScreen.LectioDivinaLista -> {
            LectioDivinaListScreen(
                dataHoje = dataDeHoje(),
                onBack = { voltar() },
                onAbrirEntrada = { data ->
                    val (ref, titulo, texto) = evangelhoFromState()
                    navegar(AppScreen.LectioDivina(
                        data = data,
                        evangelhoRef = if (data == dataLiturgia()) ref else "",
                        evangelhoTitulo = if (data == dataLiturgia()) titulo else "",
                        evangelhoTexto = if (data == dataLiturgia()) texto else "",
                        readOnly = data != dataDeHoje()
                    ))
                }
            )
        }

        is AppScreen.LectioDivina -> {
            LectioDivinaScreen(
                data = t.data,
                evangelhoReferencia = t.evangelhoRef,
                evangelhoTitulo = t.evangelhoTitulo,
                evangelhoTexto = t.evangelhoTexto,
                readOnly = t.readOnly,
                onBack = { voltar() }
            )
        }

        AppScreen.Citacoes -> {
            CitacoesScreen(
                viewModel = citacaoViewModel,
                onBack = { voltar() }
            )
        }
    }
}
