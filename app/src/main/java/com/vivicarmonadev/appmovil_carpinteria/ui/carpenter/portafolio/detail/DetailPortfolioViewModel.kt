package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PortfolioRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de detalle de un trabajo.
 *
 * Carga el item por ID, permite eliminar (si es carpintero dueño).
 */

class DetailPortfolioViewModel(
    private val portfolioRepository: PortfolioRepositoryImpl = PortfolioRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailPortfolioUiState())
    val uiState: StateFlow<DetailPortfolioUiState> = _uiState.asStateFlow()

    private var currentItemId: String = ""

    // ---- INICIALIZAR ----

    fun initialize(itemId: String) {
        currentItemId = itemId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Determinar si el usuario actual es carpintero
            authRepository.currentUser.collect { user ->
                val isCarpenter = user?.role == UserRole.CARPENTER
                _uiState.update { it.copy(isCarpenter = isCarpenter) }

                // Cargar el item
                val result = portfolioRepository.getPortfolioItemById(itemId)

                result.fold(
                    onSuccess = { item ->
                        if (item == null) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "El trabajo no existe"
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(item = item, isLoading = false)
                            }
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

    // ---- ELIMINAR ----

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onDeleteCancel() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun onDeleteConfirm() {
        val item = _uiState.value.item ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }

            val result = portfolioRepository.deletePortfolioItem(item.id)

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isDeleting = false, deleteSuccess = true)
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            showDeleteDialog = false,
                            errorMessage = "Error al eliminar: ${exception.message}"
                        )
                    }
                }
            )
        }
    }
}