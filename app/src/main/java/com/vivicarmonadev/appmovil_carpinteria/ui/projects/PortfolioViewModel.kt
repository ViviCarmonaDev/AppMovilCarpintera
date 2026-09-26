package com.vivicarmonadev.appmovil_carpinteria.ui.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PortfolioRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del catálogo de trabajos.
 *
 * Comportamiento según rol:
 *  - Cliente: carga TODOS los trabajos del catálogo
 *  - Carpintero: carga SOLO sus trabajos
 *
 * También maneja:
 *  - Búsqueda en memoria
 *  - Eliminación con confirmación
 */
class PortfolioViewModel(
    private val portfolioRepository: PortfolioRepositoryImpl = PortfolioRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PortfolioUiState())
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()

    // Guardamos el uid del usuario actual para saber qué cargar
    private var currentUid: String = ""
    private var currentRole: UserRole = UserRole.CLIENT

    init {
        // Observamos el usuario actual
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    currentUid = user.uid
                    currentRole = user.role

                    _uiState.update {
                        it.copy(isCarpenter = user.role == UserRole.CARPENTER)
                    }

                    // Cargar trabajos según el rol
                    loadItems()
                } else {
                    // Sin sesión → limpiar
                    _uiState.update {
                        it.copy(
                            items = emptyList(),
                            filteredItems = emptyList(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    // CARGAR TRABAJOS (según rol)
    private fun loadItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val flow = if (currentRole == UserRole.CARPENTER) {
                // Carpintero: solo sus trabajos
                portfolioRepository.getMyPortfolioItems(currentUid)
            } else {
                // Cliente: todos los trabajos
                portfolioRepository.getAllPortfolioItems()
            }

            flow.collect { items ->
                _uiState.update { state ->
                    state.copy(
                        items = items,
                        filteredItems = filterItems(items, state.searchQuery),
                        isLoading = false
                    )
                }
            }
        }
    }

    // BÚSQUEDA

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredItems = filterItems(state.items, query)
            )
        }
    }

    fun clearSearch() {
        _uiState.update { state ->
            state.copy(
                searchQuery = "",
                filteredItems = state.items
            )
        }
    }

    /**
     * Filtra la lista por título, categoría o material (case insensitive).
     */
    private fun filterItems(
        items: List<PortfolioItem>,
        query: String
    ): List<PortfolioItem> {
        if (query.isBlank()) return items

        val q = query.trim().lowercase()

        return items.filter { item ->
            item.titulo.lowercase().contains(q) ||
                    item.categoria.lowercase().contains(q) ||
                    item.material.lowercase().contains(q) ||
                    item.descripcion.lowercase().contains(q)
        }
    }

    // ELIMINAR

    fun onDeleteClick(item: PortfolioItem) {
        _uiState.update { it.copy(itemToDelete = item) }
    }

    fun onDeleteCancel() {
        _uiState.update { it.copy(itemToDelete = null) }
    }

    fun onDeleteConfirm() {
        val item = _uiState.value.itemToDelete ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, errorMessage = null) }

            val result = portfolioRepository.deletePortfolioItem(item.id)

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            itemToDelete = null,
                            deleteSuccessMessage = "Trabajo eliminado"
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            itemToDelete = null,
                            errorMessage = "Error al eliminar: ${exception.message}"
                        )
                    }
                }
            )
        }
    }

    fun clearDeleteSuccessMessage() {
        _uiState.update { it.copy(deleteSuccessMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}