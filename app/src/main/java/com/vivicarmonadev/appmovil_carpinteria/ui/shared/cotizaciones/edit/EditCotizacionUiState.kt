package com.vivicarmonadev.appmovil_carpinteria.ui.shared.cotizaciones.edit

import com.vivicarmonadev.appmovil_carpinteria.domain.model.ItemCotizacion

data class EditCotizacionUiState(

    // ---- Identificación ----
    val id: String = "",
    val pedidoId: String = "",
    val numeroPedido: String = "",
    val clienteUid: String = "",
    val clienteNombre: String = "",

    // ---- Datos de la cotización ----
    val materiales: List<ItemCotizacion> = emptyList(),
    val manoDeObra: String = "",
    val tiempoEstimadoDias: String = "",
    val comentarios: String = "",

    // ---- Material temporal (para el diálogo) ----
    val nuevoMaterialNombre: String = "",
    val nuevoMaterialCantidad: String = "1",
    val nuevoMaterialPrecio: String = "",
    val nuevoMaterialUnidad: String = "unidad",

    // ---- Estado general ----
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val showMaterialDialog: Boolean = false,
    val showDiscardDialog: Boolean = false
) {

    // PROPIEDADES CALCULADAS

    /**
     * Suma del precio × cantidad de todos los materiales.
     */
    val totalMateriales: Double
        get() = materiales.sumOf { it.precio * it.cantidad }

    /**
     * Costo total = materiales + mano de obra.
     */
    val costoTotal: Double
        get() = totalMateriales + (manoDeObra.toDoubleOrNull() ?: 0.0)

    /**
     * Costo total formateado como texto.
     */
    val costoTotalTexto: String
        get() = "S/ ${"%.2f".format(costoTotal)}"

    /**
     * ¿El formulario es válido para guardar?
     */
    val isFormValid: Boolean
        get() = materiales.isNotEmpty() &&
                manoDeObra.isNotBlank() &&
                (manoDeObra.toDoubleOrNull() ?: 0.0) >= 0 &&
                tiempoEstimadoDias.isNotBlank() &&
                (tiempoEstimadoDias.toIntOrNull() ?: 0) > 0
}