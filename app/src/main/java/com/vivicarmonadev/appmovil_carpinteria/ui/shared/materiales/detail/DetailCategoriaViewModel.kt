package com.vivicarmonadev.appmovil_carpinteria.ui.shared.materiales.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CategoriaMaterialRepositoryImpl // Asumo que usas este nombre
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailCategoriaViewModel(
    private val categoriaRepository: CategoriaMaterialRepositoryImpl = CategoriaMaterialRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailCategoriaUiState())
    val uiState: StateFlow<DetailCategoriaUiState> = _uiState.asStateFlow()

    fun initialize(categoriaId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Verificar si el usuario es carpintero
            val user = authRepository.currentUser.firstOrNull()
            if (user != null) {
                _uiState.update { it.copy(isCarpenter = user.role == UserRole.CARPENTER) }
            }

            // Cargar categoría (Asumo que tienes este método en tu repositorio)
            val result = categoriaRepository.getCategoriaById(categoriaId)
            result.fold(
                onSuccess = { categoria ->
                    if (categoria == null) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "La categoría no existe") }
                    } else {
                        _uiState.update { it.copy(isLoading = false, categoria = categoria) }
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onDeleteCancel() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun onDeleteConfirm() {
        val categoriaId = _uiState.value.categoria?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }

            // Asumo que tienes este método en tu repositorio
            val result = categoriaRepository.deleteCategoria(categoriaId)

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isDeleting = false, showDeleteDialog = false, deleteSuccess = true)
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isDeleting = false, showDeleteDialog = false, errorMessage = e.message)
                    }
                }
            )
        }
    }
}