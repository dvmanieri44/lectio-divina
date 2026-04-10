package com.daviapps.liturgiadiaria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daviapps.liturgiadiaria.data.local.AppDatabase
import com.daviapps.liturgiadiaria.data.local.LectioDivinaEntity
import com.daviapps.liturgiadiaria.data.repository.LectioDivinaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class LectioDivinaListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LectioDivinaRepository(
        AppDatabase.getInstance(application).lectioDivinaDao()
    )

    val entradas: StateFlow<List<LectioDivinaEntity>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
