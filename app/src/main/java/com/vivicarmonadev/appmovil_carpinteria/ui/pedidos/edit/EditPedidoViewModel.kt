package com.vivicarmonadev.appmovil_carpinteria.ui.pedidos.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PedidoRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de "Crear/Editar pedido".

 * Maneja dos casos:
 *  - Crear: formulario vacío. Al guardar, crea nuevo pedido con estado PENDIENTE.
 *  - Editar: formulario pre-llenado. Al guardar, actualiza el pedido.
 */
class EditPedidoViewModel(
    private val pedidoRepository: PedidoRepositoryImpl = PedidoRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPedidoUiState())
    val uiState: StateFlow<EditPedidoUiState> = _uiState.asStateFlow()

    // INICIALIZAR — CREAR (formulario vacío)

    fun initializeCreate() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            clientUid = user.uid,
                            isEditMode = false,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    // INICIALIZAR — EDITAR (pre-llenado)

    fun initializeEdit(pedidoId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = pedidoRepository.getPedidoById(pedidoId)

            result.fold(
                onSuccess = { pedido ->
                    if (pedido == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "El pedido no existe"
                            )
                        }
                        return@fold
                    }

                    // Validar que se pueda editar (solo PENDIENTE)
                    if (pedido.status != EstadoPedido.PENDIENTE) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Solo se pueden editar pedidos en estado PENDIENTE"
                            )
                        }
                        return@fold
                    }

                    val anchoStr = pedido.anchoCm?.let { formatNumero(it) } ?: ""
                    val altoStr = pedido.altoCm?.let { formatNumero(it) } ?: ""
                    val profStr = pedido.profundidadCm?.let { formatNumero(it) } ?: ""
                    val presupuestoStr = pedido.presupuestoMax?.let { formatNumero(it) } ?: ""

                    _uiState.update {
                        it.copy(
                            id = pedido.id,
                            clientUid = pedido.clientUid,
                            titulo = pedido.titulo,
                            categoria = pedido.categoria,
                            tipoMadera = pedido.tipoMadera,
                            anchoCm = anchoStr,
                            altoCm = altoStr,
                            profundidadCm = profStr,
                            descripcion = pedido.descripcion,
                            fechaEstimada = pedido.fechaEstimada,
                            presupuestoMax = presupuestoStr,
                            isEditMode = true,
                            isLoading = false,
                            // Guardar originales
                            originalTitulo = pedido.titulo,
                            originalCategoria = pedido.categoria,
                            originalTipoMadera = pedido.tipoMadera,
                            originalAnchoCm = anchoStr,
                            originalAltoCm = altoStr,
                            originalProfundidadCm = profStr,
                            originalDescripcion = pedido.descripcion,
                            originalFechaEstimada = pedido.fechaEstimada,
                            originalPresupuestoMax = presupuestoStr
                        )
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

    // EVENTOS DE EDICIÓN

    fun onTituloChange(value: String) {
        _uiState.update { it.copy(titulo = value, tituloTouched = true, errorMessage = null) }
    }

    fun onCategoriaChange(value: String) {
        _uiState.update { it.copy(categoria = value, categoriaTouched = true, errorMessage = null) }
    }

    fun onTipoMaderaChange(value: String) {
        _uiState.update { it.copy(tipoMadera = value, tipoMaderaTouched = true, errorMessage = null) }
    }

    fun onAnchoChange(value: String) {
        val filtered = filtrarNumero(value)
        _uiState.update { it.copy(anchoCm = filtered, anchoTouched = true, errorMessage = null) }
    }

    fun onAltoChange(value: String) {
        val filtered = filtrarNumero(value)
        _uiState.update { it.copy(altoCm = filtered, altoTouched = true, errorMessage = null) }
    }

    fun onProfundidadChange(value: String) {
        val filtered = filtrarNumero(value)
        _uiState.update { it.copy(profundidadCm = filtered, profundidadTouched = true, errorMessage = null) }
    }

    fun onDescripcionChange(value: String) {
        _uiState.update { it.copy(descripcion = value, descripcionTouched = true, errorMessage = null) }
    }

    fun onPresupuestoChange(value: String) {
        val filtered = filtrarNumero(value)
        _uiState.update { it.copy(presupuestoMax = filtered, presupuestoTouched = true, errorMessage = null) }
    }

    // FECHA ESTIMADA (DatePicker)

    fun openDatePicker() {
        _uiState.update { it.copy(showDatePicker = true) }
    }

    fun closeDatePicker() {
        _uiState.update { it.copy(showDatePicker = false) }
    }

    fun onFechaSeleccionada(timestamp: Long?) {
        _uiState.update {
            it.copy(
                fechaEstimada = timestamp,
                fechaTouched = true,
                showDatePicker = false,
                errorMessage = null
            )
        }
    }

    fun clearFecha() {
        _uiState.update { it.copy(fechaEstimada = null, fechaTouched = true) }
    }

    // GUARDAR (crear o actualizar)

    fun save() {
        val state = _uiState.value

        // Marcar todo como tocado
        _uiState.update {
            it.copy(
                tituloTouched = true,
                categoriaTouched = true,
                tipoMaderaTouched = true,
                descripcionTouched = true,
                anchoTouched = true,
                altoTouched = true,
                profundidadTouched = true,
                presupuestoTouched = true
            )
        }

        if (!state.isFormValid) {
            _uiState.update { it.copy(errorMessage = "Revisa los datos ingresados") }
            return
        }

        if (state.clientUid.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Error: usuario no identificado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val pedido = Pedido(
                id = state.id,
                clientUid = state.clientUid,
                titulo = state.titulo.trim(),
                categoria = state.categoria.trim(),
                tipoMadera = state.tipoMadera.trim(),
                anchoCm = state.anchoCm.toDoubleOrNull(),
                altoCm = state.altoCm.toDoubleOrNull(),
                profundidadCm = state.profundidadCm.toDoubleOrNull(),
                descripcion = state.descripcion.trim(),
                fechaEstimada = state.fechaEstimada,
                presupuestoMax = state.presupuestoMax.toDoubleOrNull(),
                status = EstadoPedido.PENDIENTE
            )

            val result = if (state.isEditMode) {
                pedidoRepository.updatePedido(pedido)
            } else {
                pedidoRepository.createPedido(pedido)
            }

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isLoading = false, isSuccess = true, errorMessage = null)
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            errorMessage = mapErrorToMessage(exception)
                        )
                    }
                }
            )
        }
    }

    // DESCARTAR CAMBIOS

    fun onBackClick(): Boolean {
        val state = _uiState.value
        return if (state.hasChanges && state.isFormValid) {
            _uiState.update { it.copy(showDiscardDialog = true) }
            false
        } else {
            true
        }
    }

    fun onDiscardCancel() {
        _uiState.update { it.copy(showDiscardDialog = false) }
    }

    fun onDiscardConfirm() {
        _uiState.update { it.copy(showDiscardDialog = false) }
    }

    // UTILIDADES

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Filtra solo dígitos y un punto decimal.
     */
    private fun filtrarNumero(value: String): String {
        val filtered = value.filter { it.isDigit() || it == '.' }
        return if (filtered.count { it == '.' } > 1) {
            filtered.substring(0, filtered.lastIndexOf('.'))
        } else {
            filtered
        }
    }

    /**
     * Formatea un Double a String. Si es entero, sin decimales.
     */
    private fun formatNumero(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toInt().toString()
        } else {
            "%.2f".format(value)
        }
    }

    private fun mapErrorToMessage(exception: Throwable): String {
        val message = exception.message ?: return "Error desconocido"
        return when {
            message.contains("network", ignoreCase = true) ->
                "Sin conexión. Revisa tu internet"
            message.contains("PERMISSION_DENIED", ignoreCase = true) ->
                "No tienes permiso para realizar esta acción"
            else -> message
        }
    }
}