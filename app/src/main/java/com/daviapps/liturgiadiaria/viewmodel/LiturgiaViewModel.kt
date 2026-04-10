package com.daviapps.liturgiadiaria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daviapps.liturgiadiaria.data.model.LiturgiaResponse
import com.daviapps.liturgiadiaria.data.repository.LiturgiaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val liturgia: LiturgiaResponse) : UiState()
    data class Error(val message: String) : UiState()
}

class LiturgiaViewModel : ViewModel() {

    private val repository = LiturgiaRepository()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        carregarLiturgia()
    }

    fun carregarLiturgia() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getLiturgiaHoje().fold(
                onSuccess = { _uiState.value = UiState.Success(it) },
                onFailure = { _uiState.value = UiState.Error(it.message ?: "Erro ao carregar a liturgia") }
            )
        }
    }
}
