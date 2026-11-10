package com.vivicarmonadev.appmovil_carpinteria.ui.shared.materiales.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.MaterialRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailMaterialViewModel(
    private val materialRepository: MaterialRepositoryImpl = MaterialRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailMaterialUiState())
    val uiState: StateFlow<DetailMaterialUiState> = _uiState.asStateFlow()

    fun initialize(materialId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Verificar si el usuario es carpintero
            val user = authRepository.currentUser.firstOrNull()
            if (user != null) {
                _uiState.update { it.copy(isCarpenter = user.role == UserRole.CARPENTER) }
            }

            // Cargar material
            val result = materialRepository.getMaterialById(materialId)
            result.fold(
                onSuccess = { material ->
                    if (material == null) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "El material no existe") }
                    } else {
                        _uiState.update { it.copy(isLoading = false, material = material) }
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
        val materialId = _uiState.value.material?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }

            val result = materialRepository.deleteMaterial(materialId)

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