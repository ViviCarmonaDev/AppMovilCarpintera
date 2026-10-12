package com.vivicarmonadev.appmovil_carpinteria.ui.shared.cotizaciones.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CotizacionRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Cotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.ItemCotizacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla "Editar cotización".
 */
class EditCotizacionViewModel(
    private val cotizacionRepository: CotizacionRepositoryImpl = CotizacionRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditCotizacionUiState())
    val uiState: StateFlow<EditCotizacionUiState> = _uiState.asStateFlow()

    // Guardamos la cotización original para poder actualizarla
    private var cotizacionOriginal: Cotizacion? = null

    // INICIALIZAR

    fun initialize(cotizacionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = cotizacionRepository.getCotizacionById(cotizacionId)

            result.fold(
                onSuccess = { cotizacion ->
                    if (cotizacion == null) {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "La cotización no existe")
                        }
                        return@fold
                    }

                    cotizacionOriginal = cotizacion

                    _uiState.update {
                        it.copy(
                            id = cotizacion.id,
                            pedidoId = cotizacion.pedidoId,
                            numeroPedido = cotizacion.numeroPedido,
                            clienteUid = cotizacion.clienteUid,
                            clienteNombre = cotizacion.clienteNombre,
                            materiales = cotizacion.materiales,
                            manoDeObra = if (cotizacion.manoDeObra > 0)
                                formatNumero(cotizacion.manoDeObra) else "",
                            tiempoEstimadoDias = if (cotizacion.tiempoEstimadoDias > 0)
                                cotizacion.tiempoEstimadoDias.toString() else "",
                            comentarios = cotizacion.comentarios,
                            isLoading = false
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

    // MATERIALES

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
        _uiState.update {
            it.copy(nuevoMaterialCantidad = value.filter { c -> c.isDigit() })
        }
    }

    fun onNuevoMaterialPrecioChange(value: String) {
        _uiState.update {
            it.copy(nuevoMaterialPrecio = filtrarNumero(value))
        }
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

    // ============================================
    // CAMPOS
    // ============================================
    fun onManoDeObraChange(value: String) {
        _uiState.update {
            it.copy(
                manoDeObra = filtrarNumero(value),
                errorMessage = null
            )
        }
    }

    fun onTiempoEstimadoChange(value: String) {
        _uiState.update {
            it.copy(
                tiempoEstimadoDias = value.filter { c -> c.isDigit() },
                errorMessage = null
            )
        }
    }

    fun onComentariosChange(value: String) {
        _uiState.update { it.copy(comentarios = value) }
    }

    // ============================================
    // GUARDAR
    // ============================================
    fun save() {
        val state = _uiState.value
        val original = cotizacionOriginal

        if (original == null) {
            _uiState.update { it.copy(errorMessage = "No se cargó la cotización original") }
            return
        }

        if (state.materiales.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Agrega al menos un material") }
            return
        }

        if (state.manoDeObra.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingresa la mano de obra") }
            return
        }

        if ((state.tiempoEstimadoDias.toIntOrNull() ?: 0) <= 0) {
            _uiState.update { it.copy(errorMessage = "Ingresa el tiempo estimado") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val cotizacionActualizada = original.copy(
                materiales = state.materiales,
                manoDeObra = state.manoDeObra.toDoubleOrNull() ?: 0.0,
                costoTotal = state.costoTotal,
                tiempoEstimadoDias = state.tiempoEstimadoDias.toIntOrNull() ?: 0,
                comentarios = state.comentarios.trim()
            )

            val result = cotizacionRepository.updateCotizacion(cotizacionActualizada)

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
                            errorMessage = "Error al guardar: ${exception.message}"
                        )
                    }
                }
            )
        }
    }

    // DESCARTAR

    fun onBackClick(): Boolean {
        return true
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

    // UTILIDADES

    private fun filtrarNumero(value: String): String {
        val filtered = value.filter { it.isDigit() || it == '.' }
        return if (filtered.count { it == '.' } > 1) {
            filtered.substring(0, filtered.lastIndexOf('.'))
        } else {
            filtered
        }
    }

    private fun formatNumero(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toInt().toString()
        } else {
            "%.2f".format(value)
        }
    }
}