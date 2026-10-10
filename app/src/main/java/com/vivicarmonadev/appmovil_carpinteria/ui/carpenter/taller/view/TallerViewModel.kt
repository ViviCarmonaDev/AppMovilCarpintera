package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.taller.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CarpenterRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la VISTA del taller del carpintero.
 */
class TallerViewModel(
    private val carpenterRepository: CarpenterRepositoryImpl = CarpenterRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TallerUiState())
    val uiState: StateFlow<TallerUiState> = _uiState.asStateFlow()

    fun initialize(uid: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = carpenterRepository.getCarpenterProfileOnce(uid)

            result.fold(
                onSuccess = { profile ->
                    _uiState.update {
                        it.copy(profile = profile, isLoading = false)
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error al cargar: ${exception.message}"
                        )
                    }
                }
            )
        }
    }
}