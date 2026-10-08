package com.vivicarmonadev.appmovil_carpinteria.ui.shared.pedidos.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.PedidoRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpinteroResumen
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel de EDICIÓN de pedido.
 */
class EditPedidoViewModel(
    private val pedidoRepository: PedidoRepositoryImpl = PedidoRepositoryImpl(),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPedidoUiState())
    val uiState: StateFlow<EditPedidoUiState> = _uiState.asStateFlow()

    // INICIALIZAR — EDITAR

    fun initializeEdit(pedidoId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = pedidoRepository.getPedidoById(pedidoId)

            result.fold(
                onSuccess = { pedido ->
                    if (pedido == null) {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "El pedido no existe")
                        }
                        return@fold
                    }

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
                            numeroSecuencial = pedido.numeroSecuencial,
                            clientUid = pedido.clientUid,
                            clienteNombre = pedido.clienteNombre,
                            titulo = pedido.titulo,
                            categoria = pedido.categoria,
                            tipoMadera = pedido.tipoMadera,
                            anchoCm = anchoStr,
                            altoCm = altoStr,
                            profundidadCm = profStr,
                            descripcion = pedido.descripcion,
                            fechaEstimada = pedido.fechaEstimada,
                            presupuestoMax = presupuestoStr,
                            imagenesUrls = pedido.imagenesUrls,
                            carpenterUid = pedido.carpenterUid,
                            carpinteroNombre = pedido.carpinteroNombre,
                            status = pedido.status,
                            isLoading = false,

                            // Originales
                            originalTitulo = pedido.titulo,
                            originalCategoria = pedido.categoria,
                            originalTipoMadera = pedido.tipoMadera,
                            originalAnchoCm = anchoStr,
                            originalAltoCm = altoStr,
                            originalProfundidadCm = profStr,
                            originalDescripcion = pedido.descripcion,
                            originalFechaEstimada = pedido.fechaEstimada,
                            originalPresupuestoMax = presupuestoStr,
                            originalImagenesUrls = pedido.imagenesUrls,
                            originalCarpenterUid = pedido.carpenterUid
                        )
                    }

                    cargarCarpinterosDisponibles()
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
        _uiState.update { it.copy(anchoCm = filtrarNumero(value), anchoTouched = true, errorMessage = null) }
    }

    fun onAltoChange(value: String) {
        _uiState.update { it.copy(altoCm = filtrarNumero(value), altoTouched = true, errorMessage = null) }
    }

    fun onProfundidadChange(value: String) {
        _uiState.update { it.copy(profundidadCm = filtrarNumero(value), profundidadTouched = true, errorMessage = null) }
    }

    fun onDescripcionChange(value: String) {
        _uiState.update { it.copy(descripcion = value, descripcionTouched = true, errorMessage = null) }
    }

    fun onPresupuestoChange(value: String) {
        _uiState.update { it.copy(presupuestoMax = filtrarNumero(value), presupuestoTouched = true, errorMessage = null) }
    }

    fun onCarpinteroChange(carpenterUid: String?, carpinteroNombre: String?) {
        _uiState.update {
            it.copy(carpenterUid = carpenterUid, carpinteroNombre = carpinteroNombre)
        }
    }

    // IMÁGENES

    fun onAgregarImagen(url: String) {
        _uiState.update { state ->
            if (state.imagenesUrls.size >= 3) state
            else state.copy(imagenesUrls = state.imagenesUrls + url)
        }
    }

    fun onEliminarImagen(index: Int) {
        _uiState.update { state ->
            val nuevas = state.imagenesUrls.toMutableList().apply {
                if (index in indices) removeAt(index)
            }
            state.copy(imagenesUrls = nuevas)
        }
    }

    // CARGAR CARPINTEROS

    private fun cargarCarpinterosDisponibles() {
        viewModelScope.launch {
            try {
                val snapshot = Firebase.firestore
                    .collection("users")
                    .whereEqualTo("role", "carpenter")
                    .get()
                    .await()

                val carpinteros = snapshot.documents.mapNotNull { doc ->
                    val uid = doc.id
                    val nombres = doc.getString("nombres") ?: ""
                    val apellidos = doc.getString("apellidos") ?: ""
                    val nombreCompleto = "$nombres $apellidos".trim()

                    if (nombreCompleto.isBlank()) null
                    else CarpinteroResumen(uid = uid, nombre = nombreCompleto)
                }

                _uiState.update { it.copy(carpinterosDisponibles = carpinteros) }
            } catch (e: Exception) {
                _uiState.update { it.copy(carpinterosDisponibles = emptyList()) }
            }
        }
    }

    // FECHA

    fun openDatePicker() { _uiState.update { it.copy(showDatePicker = true) } }
    fun closeDatePicker() { _uiState.update { it.copy(showDatePicker = false) } }

    fun onFechaSeleccionada(timestamp: Long?) {
        _uiState.update {
            it.copy(fechaEstimada = timestamp, fechaTouched = true, showDatePicker = false, errorMessage = null)
        }
    }

    fun clearFecha() {
        _uiState.update { it.copy(fechaEstimada = null, fechaTouched = true) }
    }

    // GUARDAR

    fun save() {
        val state = _uiState.value

        _uiState.update {
            it.copy(
                tituloTouched = true,
                categoriaTouched = true,
                tipoMaderaTouched = true,
                descripcionTouched = true,
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
                numeroSecuencial = state.numeroSecuencial,
                clientUid = state.clientUid,
                clienteNombre = state.clienteNombre,
                titulo = state.titulo.trim(),
                categoria = state.categoria.trim(),
                tipoMadera = state.tipoMadera.trim(),
                anchoCm = state.anchoCm.toDoubleOrNull(),
                altoCm = state.altoCm.toDoubleOrNull(),
                profundidadCm = state.profundidadCm.toDoubleOrNull(),
                descripcion = state.descripcion.trim(),
                fechaEstimada = state.fechaEstimada,
                presupuestoMax = state.presupuestoMax.toDoubleOrNull(),
                imagenesUrls = state.imagenesUrls,
                carpenterUid = state.carpenterUid,
                carpinteroNombre = state.carpinteroNombre,
                status = state.status
            )

            val result = pedidoRepository.updatePedido(pedido)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true, errorMessage = null) }
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

    // DESCARTAR

    fun onBackClick(): Boolean {
        return if (_uiState.value.hasChanges && _uiState.value.isFormValid) {
            _uiState.update { it.copy(showDiscardDialog = true) }
            false
        } else true
    }

    fun onDiscardCancel() { _uiState.update { it.copy(showDiscardDialog = false) } }
    fun onDiscardConfirm() { _uiState.update { it.copy(showDiscardDialog = false) } }
    fun resetSuccess() { _uiState.update { it.copy(isSuccess = false) } }
    fun clearError() { _uiState.update { it.copy(errorMessage = null) } }

    // UTILIDADES

    private fun filtrarNumero(value: String): String {
        val filtered = value.filter { it.isDigit() || it == '.' }
        return if (filtered.count { it == '.' } > 1)
            filtered.substring(0, filtered.lastIndexOf('.'))
        else filtered
    }

    private fun formatNumero(value: Double): String {
        return if (value % 1.0 == 0.0) value.toInt().toString()
        else "%.2f".format(value)
    }

    private fun mapErrorToMessage(exception: Throwable): String {
        val message = exception.message ?: return "Error desconocido"
        return when {
            message.contains("network", ignoreCase = true) -> "Sin conexión. Revisa tu internet"
            message.contains("PERMISSION_DENIED", ignoreCase = true) -> "No tienes permiso para realizar esta acción"
            else -> message
        }
    }
}