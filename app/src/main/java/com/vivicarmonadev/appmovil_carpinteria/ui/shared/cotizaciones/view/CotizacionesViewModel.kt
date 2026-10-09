package com.vivicarmonadev.appmovil_carpinteria.ui.shared.cotizaciones.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CotizacionRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Cotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoCotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CotizacionesViewModel(
    private val cotizacionRepository: CotizacionRepositoryImpl = CotizacionRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CotizacionesUiState())
    val uiState: StateFlow<CotizacionesUiState> = _uiState.asStateFlow()

    private var cotizacionesJob: Job? = null

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    val uidChanged = _uiState.value.currentUid != user.uid

                    _uiState.update {
                        it.copy(
                            currentUid = user.uid,
                            isCarpenter = user.role == UserRole.CARPENTER
                        )
                    }

                    if (uidChanged) {
                        loadCotizaciones(user.uid)
                    }
                } else {
                    cotizacionesJob?.cancel()
                    _uiState.update {
                        it.copy(
                            cotizaciones = emptyList(),
                            cotizacionesFiltradas = emptyList(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun loadCotizaciones(uid: String) {
        cotizacionesJob?.cancel()
        cotizacionesJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            cotizacionRepository.getCotizacionesByCarpenter(uid).collect { lista ->
                _uiState.update { state ->
                    state.copy(
                        cotizaciones = lista,
                        cotizacionesFiltradas = aplicarFiltros(
                            lista = lista,
                            query = state.searchQuery,
                            estado = state.estadoFiltro
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun aplicarFiltros(
        lista: List<Cotizacion>,
        query: String,
        estado: EstadoCotizacion?
    ): List<Cotizacion> {
        var resultado = lista

        if (estado != null) {
            resultado = resultado.filter { it.estado == estado }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            resultado = resultado.filter {
                it.numeroPedido.lowercase().contains(q) ||
                        it.clienteNombre.lowercase().contains(q) ||
                        it.pedidoId.lowercase().contains(q)
            }
        }

        return resultado
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                cotizacionesFiltradas = aplicarFiltros(
                    lista = state.cotizaciones,
                    query = query,
                    estado = state.estadoFiltro
                )
            )
        }
    }

    fun clearSearch() {
        _uiState.update { state ->
            state.copy(
                searchQuery = "",
                cotizacionesFiltradas = aplicarFiltros(
                    lista = state.cotizaciones,
                    query = "",
                    estado = state.estadoFiltro
                )
            )
        }
    }

    fun onEstadoFiltroChange(estado: EstadoCotizacion?) {
        _uiState.update { state ->
            state.copy(
                estadoFiltro = estado,
                cotizacionesFiltradas = aplicarFiltros(
                    lista = state.cotizaciones,
                    query = state.searchQuery,
                    estado = estado
                )
            )
        }
    }
}