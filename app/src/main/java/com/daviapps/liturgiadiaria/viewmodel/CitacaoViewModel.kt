package com.daviapps.liturgiadiaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daviapps.liturgiadiaria.data.local.AppDatabase
import com.daviapps.liturgiadiaria.data.local.CitacaoEntity
import com.daviapps.liturgiadiaria.data.repository.CitacaoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CitacaoViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = CitacaoRepository(AppDatabase.getInstance(application).citacaoDao())

    val citacoes: StateFlow<List<CitacaoEntity>> = repo.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun adicionar(texto: String, evangelhoReferencia: String, evangelhoData: String) {
        if (texto.isBlank()) return
        viewModelScope.launch {
            repo.inserir(
                CitacaoEntity(
                    texto = texto.trim(),
                    evangelhoReferencia = evangelhoReferencia,
                    evangelhoData = evangelhoData
                )
            )
        }
    }

    fun deletar(citacao: CitacaoEntity) {
        viewModelScope.launch { repo.deletar(citacao) }
    }
}
