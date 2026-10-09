package com.vivicarmonadev.appmovil_carpinteria.ui.shared.cotizaciones.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CotizacionRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PedidoRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Cotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoCotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.ItemCotizacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para CREAR una cotización sobre un pedido.
 */
class CreateCotizacionViewModel(
    private val cotizacionRepository: CotizacionRepositoryImpl = CotizacionRepositoryImpl(),
    private val pedidoRepository: PedidoRepositoryImpl = PedidoRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateCotizacionUiState())
    val uiState: StateFlow<CreateCotizacionUiState> = _uiState.asStateFlow()

    private var currentCarpenterUid: String = ""
    private var currentCarpenterNombre: String = ""

    // INICIALIZAR con el pedido

    fun initialize(pedidoId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorCarga = null) }

            // Cargar carpintero actual
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    currentCarpenterUid = user.uid
                    currentCarpenterNombre = "${user.nombres} ${user.apellidos}".trim()

                    // Cargar el pedido
                    val result = pedidoRepository.getPedidoById(pedidoId)

                    result.fold(
                        onSuccess = { pedido ->
                            if (pedido == null) {
                                _uiState.update {
                                    it.copy(isLoading = false, errorCarga = "El pedido no existe")
                                }
                            } else {
                                _uiState.update {
                                    it.copy(
                                        pedidoId = pedido.id,
                                        numeroPedido = pedido.numeroPedido,
                                        clienteUid = pedido.clientUid,
                                        clienteNombre = pedido.clienteNombre,
                                        tituloPedido = pedido.titulo,
                                        descripcionPedido = pedido.descripcion,
                                        isLoading = false
                                    )
                                }
                            }
                        },
                        onFailure = { exception ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorCarga = "Error al cargar: ${exception.message}"
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    // MATERIALES (agregar/eliminar)

    fun onOpenMaterialDialog() {
        _uiState.update {
            it.copy(
                showMaterialDialog = true,
                nuevoMaterialNombre = "",
                nuevoMaterialCantidad = "1",
                nuevoMaterialPrecio = "",
                nuevoMaterialUnidad = "unidad"
            )
        }
    }

    fun onCloseMaterialDialog() {
        _uiState.update { it.copy(showMaterialDialog = false) }
    }

    fun onNuevoMaterialNombreChange(value: String) {
        _uiState.update { it.copy(nuevoMaterialNombre = value) }
    }

    fun onNuevoMaterialCantidadChange(value: String) {
        val filtered = value.filter { it.isDigit() }
        _uiState.update { it.copy(nuevoMaterialCantidad = filtered) }
    }

    fun onNuevoMaterialPrecioChange(value: String) {
        val filtered = filtrarNumero(value)
        _uiState.update { it.copy(nuevoMaterialPrecio = filtered) }
    }

    fun onNuevoMaterialUnidadChange(value: String) {
        _uiState.update { it.copy(nuevoMaterialUnidad = value) }
    }

    fun onAgregarMaterial() {
        val state = _uiState.value
        if (state.nuevoMaterialNombre.isBlank()) return
        if (state.nuevoMaterialPrecio.isBlank()) return

        val material = ItemCotizacion(
            nombre = state.nuevoMaterialNombre.trim(),
            cantidad = state.nuevoMaterialCantidad.toIntOrNull() ?: 1,
            precio = state.nuevoMaterialPrecio.toDoubleOrNull() ?: 0.0,
            unidad = state.nuevoMaterialUnidad.ifBlank { "unidad" }
        )

        _uiState.update {
            it.copy(
                materiales = it.materiales + material,
                showMaterialDialog = false
            )
        }
    }

    fun onEliminarMaterial(index: Int) {
        _uiState.update { state ->
            val lista = state.materiales.toMutableList().apply {
                if (index in indices) removeAt(index)
            }
            state.copy(materiales = lista)
        }
    }

    // MANO DE OBRA

    fun onManoDeObraChange(value: String) {
        val filtered = filtrarNumero(value)
        _uiState.update {
            it.copy(manoDeObra = filtered, manoDeObraError = null, errorMessage = null)
        }
    }

    // TIEMPO ESTIMADO

    fun onTiempoEstimadoChange(value: String) {
        val filtered = value.filter { it.isDigit() }
        _uiState.update {
            it.copy(tiempoEstimadoDias = filtered, tiempoEstimadoError = null, errorMessage = null)
        }
    }

    // COMENTARIOS

    fun onComentariosChange(value: String) {
        _uiState.update { it.copy(comentarios = value) }
    }

    // GUARDAR

    fun save() {
        val state = _uiState.value

        // Validaciones
        val manoDeObraDouble = state.manoDeObra.toDoubleOrNull() ?: 0.0
        val tiempoInt = state.tiempoEstimadoDias.toIntOrNull() ?: 0

        if (state.materiales.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Agrega al menos un material") }
            return
        }

        if (state.manoDeObra.isBlank()) {
            _uiState.update { it.copy(manoDeObraError = "Ingresa la mano de obra") }
            return
        }

        if (tiempoInt <= 0) {
            _uiState.update { it.copy(tiempoEstimadoError = "Ingresa el tiempo estimado") }
            return
        }

        if (currentCarpenterUid.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Error: usuario no identificado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val cotizacion = Cotizacion(
                id = "",
                pedidoId = state.pedidoId,
                numeroPedido = state.numeroPedido,
                clienteUid = state.clienteUid,
                clienteNombre = state.clienteNombre,
                carpinteroUid = currentCarpenterUid,
                carpinteroNombre = currentCarpenterNombre,
                materiales = state.materiales,
                manoDeObra = manoDeObraDouble,
                costoTotal = state.costoTotal,
                tiempoEstimadoDias = tiempoInt,
                comentarios = state.comentarios.trim(),
                estado = EstadoCotizacion.PENDIENTE
            )

            val result = cotizacionRepository.createCotizacion(cotizacion)

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isSaving = false, isSuccess = true, errorMessage = null)
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            isSuccess = false,
                            errorMessage = "Error al guardar: ${exception.message}"
                        )
                    }
                }
            )
        }
    }

    // DESCARTAR

    fun onBackClick(): Boolean {
        return if (_uiState.value.hasChanges) {
            _uiState.update { it.copy(showDiscardDialog = true) }
            false
        } else true
    }

    fun onDiscardCancel() {
        _uiState.update { it.copy(showDiscardDialog = false) }
    }

    fun onDiscardConfirm() {
        _uiState.update { it.copy(showDiscardDialog = false) }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // UTILIDADES
    private fun filtrarNumero(value: String): String {
        val filtered = value.filter { it.isDigit() || it == '.' }
        return if (filtered.count { it == '.' } > 1) {
            filtered.substring(0, filtered.lastIndexOf('.'))
        } else {
            filtered
        }
    }
}