package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.portafolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CarpenterRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PortfolioRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CarpenterPortfolioViewModel(
    private val carpenterRepository: CarpenterRepositoryImpl = CarpenterRepositoryImpl(),
    private val portfolioRepository: PortfolioRepositoryImpl = PortfolioRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CarpenterPortfolioUiState())
    val uiState: StateFlow<CarpenterPortfolioUiState> = _uiState.asStateFlow()

    fun loadPortfolio(carpinteroUid: String) {
        _uiState.update {
            it.copy(
                carpinteroUid = carpinteroUid,
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            // 1. Cargar nombre del taller
            val profileResult = carpenterRepository.getCarpenterProfileOnce(carpinteroUid)
            val nombreTaller = profileResult.getOrNull()?.nombreTaller ?: "Carpintero"

            // 2. Cargar los trabajos en tiempo real
            portfolioRepository.getMyPortfolioItems(carpinteroUid).collect { items ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        nombreTaller = nombreTaller,
                        items = items
                    )
                }
            }
        }
    }
}