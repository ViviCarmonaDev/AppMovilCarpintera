package com.vivicarmonadev.appmovil_carpinteria.ui.shared.materiales.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CategoriaMaterialRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CategoriaMaterial
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriasViewModel(
    private val categoriaRepository: CategoriaMaterialRepositoryImpl = CategoriaMaterialRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriasUiState())
    val uiState: StateFlow<CategoriasUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update { it.copy(isCarpenter = user?.role == UserRole.CARPENTER) }
            }
        }
        loadCategorias()
    }

    private fun loadCategorias() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            categoriaRepository.getAllCategorias().collect { lista ->
                _uiState.update { state ->
                    state.copy(
                        categorias = lista,
                        categoriasFiltradas = filter(lista, state.searchQuery),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(searchQuery = query, categoriasFiltradas = filter(state.categorias, query))
        }
    }

    fun clearSearch() {
        _uiState.update { state -> state.copy(searchQuery = "", categoriasFiltradas = state.categorias) }
    }

    private fun filter(lista: List<CategoriaMaterial>, query: String): List<CategoriaMaterial> {
        if (query.isBlank()) return lista
        val q = query.trim().lowercase()
        return lista.filter { it.nombre.lowercase().contains(q) || it.descripcion.lowercase().contains(q) }
    }
}