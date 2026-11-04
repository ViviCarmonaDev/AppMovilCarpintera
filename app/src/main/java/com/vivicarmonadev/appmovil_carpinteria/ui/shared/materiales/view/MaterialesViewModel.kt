package com.vivicarmonadev.appmovil_carpinteria.ui.shared.materiales.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.MaterialRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MaterialesViewModel(
    private val materialRepository: MaterialRepositoryImpl = MaterialRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaterialesUiState())
    val uiState: StateFlow<MaterialesUiState> = _uiState.asStateFlow()

    init {
        loadMateriales()
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update { it.copy(isCarpenter = user?.role == UserRole.CARPENTER) }
            }
        }
    }

    private fun loadMateriales() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            materialRepository.getAllMaterials().collect { lista ->
                _uiState.update { state ->
                    state.copy(
                        materiales = lista,
                        materialesFiltrados = filter(lista, state.searchQuery),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                materialesFiltrados = filter(state.materiales, query)
            )
        }
    }

    fun clearSearch() {
        _uiState.update { state ->
            state.copy(searchQuery = "", materialesFiltrados = state.materiales)
        }
    }

    fun loadMaterialesPorCategoria(categoriaId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            //  Filtrar materiales por categoría
            materialRepository.getMaterialesByCategoria(categoriaId).collect { lista ->
                _uiState.update { state ->
                    state.copy(
                        materiales = lista,
                        materialesFiltrados = filter(lista, state.searchQuery),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun filter(lista: List<com.vivicarmonadev.appmovil_carpinteria.domain.model.Material>, query: String): List<com.vivicarmonadev.appmovil_carpinteria.domain.model.Material> {
        if (query.isBlank()) return lista
        val q = query.trim().lowercase()
        return lista.filter {
            it.nombre.lowercase().contains(q) || it.descripcion.lowercase().contains(q)
        }
    }
}