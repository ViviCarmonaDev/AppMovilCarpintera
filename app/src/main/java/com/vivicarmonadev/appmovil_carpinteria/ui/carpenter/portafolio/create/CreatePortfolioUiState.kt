package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.create

import com.vivicarmonadev.appmovil_carpinteria.domain.model.TipoPrecio

/**
 * Estado de la UI para CREAR un trabajo nuevo.
 * Formulario vacío por defecto.
 */
data class CreatePortfolioUiState(
    // ---- Datos del formulario ----
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val material: String = "",
    val precioReferencial: String = "",
    val tipoPrecio: TipoPrecio? = null,

    // ---- Flags de "tocado" ----
    val tituloTouched: Boolean = false,
    val descripcionTouched: Boolean = false,
    val categoriaTouched: Boolean = false,
    val materialTouched: Boolean = false,
    val precioTouched: Boolean = false,

    // ---- Estado general ----
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val showDiscardDialog: Boolean = false
) {

    // ---- VALIDACIONES ----

    val tituloError: String?
        get() = when {
            !tituloTouched -> null
            titulo.isBlank() -> "Ingresa un título"
            titulo.trim().length < 3 -> "Mínimo 3 caracteres"
            titulo.trim().length > 80 -> "Máximo 80 caracteres"
            else -> null
        }

    val descripcionError: String?
        get() = when {
            !descripcionTouched -> null
            descripcion.isBlank() -> "Ingresa una descripción"
            descripcion.trim().length < 20 -> "Mínimo 20 caracteres"
            descripcion.trim().length > 500 -> "Máximo 500 caracteres"
            else -> null
        }

    val categoriaError: String?
        get() = when {
            !categoriaTouched -> null
            categoria.isBlank() -> "Ingresa una categoría"
            categoria.trim().length < 3 -> "Mínimo 3 caracteres"
            else -> null
        }

    val materialError: String?
        get() = when {
            !materialTouched -> null
            material.isBlank() -> "Ingresa un material"
            material.trim().length < 3 -> "Mínimo 3 caracteres"
            else -> null
        }

    val precioError: String?
        get() = when {
            !precioTouched -> null
            tipoPrecio == null -> "Elige un tipo de precio"
            tipoPrecio == TipoPrecio.FIJO && precioReferencial.isBlank() ->
                "El precio es obligatorio cuando es FIJO"
            precioReferencial.isBlank() -> null
            !precioReferencial.matches(Regex("^\\d+(\\.\\d{1,2})?$")) ->
                "Solo números (ej: 250 o 250.50)"
            (precioReferencial.toDoubleOrNull() ?: 0.0) <= 0 ->
                "El precio debe ser mayor a 0"
            else -> null
        }

    // ---- VALIDEZ DEL FORMULARIO ----

    val isFormValid: Boolean
        get() = titulo.isNotBlank() && tituloError == null &&
                descripcion.isNotBlank() && descripcionError == null &&
                categoria.isNotBlank() && categoriaError == null &&
                material.isNotBlank() && materialError == null &&
                tipoPrecio != null &&
                precioError == null

    // ---- ¿HUBO CAMBIOS? ----
    // En CREATE, cualquier campo lleno cuenta como "cambio"
    val hasChanges: Boolean
        get() = titulo.isNotBlank() ||
                descripcion.isNotBlank() ||
                categoria.isNotBlank() ||
                material.isNotBlank() ||
                precioReferencial.isNotBlank()
}