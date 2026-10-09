package com.vivicarmonadev.appmovil_carpinteria.ui.shared.cotizaciones.create

import com.vivicarmonadev.appmovil_carpinteria.domain.model.ItemCotizacion

/**
 * Estado del formulario de creación de cotización.
 */
data class CreateCotizacionUiState(
    // ---- Datos del pedido (readonly) ----
    val pedidoId: String = "",
    val numeroPedido: String = "",
    val clienteUid: String = "",
    val clienteNombre: String = "",
    val tituloPedido: String = "",
    val descripcionPedido: String = "",

    // ---- Datos de la cotización ----
    val materiales: List<ItemCotizacion> = emptyList(),
    val manoDeObra: String = "",
    val tiempoEstimadoDias: String = "",
    val comentarios: String = "",

    // ---- Datos del nuevo material (temporal) ----
    val nuevoMaterialNombre: String = "",
    val nuevoMaterialCantidad: String = "1",
    val nuevoMaterialPrecio: String = "",
    val nuevoMaterialUnidad: String = "unidad",

    // ---- Errores ----
    val manoDeObraError: String? = null,
    val tiempoEstimadoError: String? = null,
    val errorMessage: String? = null,

    // ---- Estado general ----
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val showDiscardDialog: Boolean = false,
    val showMaterialDialog: Boolean = false,
    val errorCarga: String? = null
) {
    // Costo total calculado
    val totalMateriales: Double
        get() = materiales.sumOf { it.precio * it.cantidad }

    val costoTotal: Double
        get() = totalMateriales + (manoDeObra.toDoubleOrNull() ?: 0.0)

    val costoTotalTexto: String
        get() = "S/ ${"%.2f".format(costoTotal)}"

    // Validaciones
    val isFormValid: Boolean
        get() = materiales.isNotEmpty() &&
                manoDeObra.isNotBlank() &&
                (manoDeObra.toDoubleOrNull() ?: 0.0) >= 0 &&
                tiempoEstimadoDias.isNotBlank() &&
                (tiempoEstimadoDias.toIntOrNull() ?: 0) > 0

    val hasChanges: Boolean
        get() = materiales.isNotEmpty() ||
                manoDeObra.isNotBlank() ||
                tiempoEstimadoDias.isNotBlank() ||
                comentarios.isNotBlank()
}