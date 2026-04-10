package com.daviapps.liturgiadiaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daviapps.liturgiadiaria.data.local.AppDatabase
import com.daviapps.liturgiadiaria.data.local.LectioDivinaEntity
import com.daviapps.liturgiadiaria.data.repository.LectioDivinaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LectioDivinaState(
    val data: String = "",
    val evangelhoReferencia: String = "",
    val lectioNota: String = "",
    val meditatioNota: String = "",
    val oratioNota: String = "",
    val contemplatiaNota: String = "",
    val actioNota: String = "",
    val salvando: Boolean = false,
    val salvoComSucesso: Boolean = false
)

class LectioDivinaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LectioDivinaRepository(
        AppDatabase.getInstance(application).lectioDivinaDao()
    )

    private val _state = MutableStateFlow(LectioDivinaState())
    val state: StateFlow<LectioDivinaState> = _state.asStateFlow()

    fun carregarParaData(data: String, evangelhoReferencia: String) {
        viewModelScope.launch {
            val salvo = repository.getByData(data)
            _state.value = if (salvo != null) {
                LectioDivinaState(
                    data = data,
                    evangelhoReferencia = salvo.evangelhoReferencia.ifBlank { evangelhoReferencia },
                    lectioNota = salvo.lectioNota,
                    meditatioNota = salvo.meditatioNota,
                    oratioNota = salvo.oratioNota,
                    contemplatiaNota = salvo.contemplatiaNota,
                    actioNota = salvo.actioNota
                )
            } else {
                LectioDivinaState(data = data, evangelhoReferencia = evangelhoReferencia)
            }
        }
    }

    fun atualizar(campo: CampoLectio, valor: String) {
        _state.value = when (campo) {
            CampoLectio.LECTIO        -> _state.value.copy(lectioNota = valor)
            CampoLectio.MEDITATIO     -> _state.value.copy(meditatioNota = valor)
            CampoLectio.ORATIO        -> _state.value.copy(oratioNota = valor)
            CampoLectio.CONTEMPLATIO  -> _state.value.copy(contemplatiaNota = valor)
            CampoLectio.ACTIO         -> _state.value.copy(actioNota = valor)
        }
    }

    fun salvar() {
        val s = _state.value
        if (s.data.isBlank()) return
        viewModelScope.launch {
            _state.value = s.copy(salvando = true)
            repository.salvar(
                LectioDivinaEntity(
                    data = s.data,
                    evangelhoReferencia = s.evangelhoReferencia,
                    lectioNota = s.lectioNota,
                    meditatioNota = s.meditatioNota,
                    oratioNota = s.oratioNota,
                    contemplatiaNota = s.contemplatiaNota,
                    actioNota = s.actioNota
                )
            )
            _state.value = _state.value.copy(salvando = false, salvoComSucesso = true)
        }
    }

    fun resetarFeedback() {
        _state.value = _state.value.copy(salvoComSucesso = false)
    }
}

enum class CampoLectio { LECTIO, MEDITATIO, ORATIO, CONTEMPLATIO, ACTIO }
