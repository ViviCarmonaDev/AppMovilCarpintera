package com.vivicarmonadev.appmovil_carpinteria.ui.projects.edit

import com.vivicarmonadev.appmovil_carpinteria.domain.model.TipoPrecio

/**
 * Estado de la UI de "Crear/Editar trabajo".
 *
 * Se usa para dos casos:
 *  - Crear: formulario vacío, guarda con create
 *  - Editar: formulario pre-llenado, guarda con update
 *
 * El flag `isEditMode` diferencia entre los dos casos.
 */
data class EditPortfolioItemUiState(
    // ---- Datos del formulario ----
    val id: String = "",
    val uid: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val material: String = "",
    val precioReferencial: String = "",
    val tipoPrecio: TipoPrecio = TipoPrecio.A_TRATAR,
    val fotoUrl1: String? = null,
    val fotoUrl2: String? = null,

    // ---- Valores originales (para detectar cambios en modo edición) ----
    val originalTitulo: String = "",
    val originalDescripcion: String = "",
    val originalCategoria: String = "",
    val originalMaterial: String = "",
    val originalPrecio: String = "",
    val originalTipoPrecio: TipoPrecio = TipoPrecio.A_TRATAR,

    // ---- Flags de "tocado" ----
    val tituloTouched: Boolean = false,
    val descripcionTouched: Boolean = false,
    val categoriaTouched: Boolean = false,
    val materialTouched: Boolean = false,
    val precioTouched: Boolean = false,
    val tipoPrecioTouched: Boolean = false,

    // ---- Estado general ----
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val isEditMode: Boolean = false,
    val showDiscardDialog: Boolean = false
) {

    // VALIDACIONES

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

    // El precio es obligatorio si el tipo es FIJO. Opcional si es A_TRATAR.
    val precioError: String?
        get() = when {
            !precioTouched -> null
            tipoPrecio == TipoPrecio.FIJO && precioReferencial.isBlank() ->
                "El precio es obligatorio cuando es FIJO"
            precioReferencial.isBlank() -> null      // opcional si es A_TRATAR
            !precioReferencial.matches(Regex("^\\d+(\\.\\d{1,2})?$")) ->
                "Solo números (ej: 250 o 250.50)"
            (precioReferencial.toDoubleOrNull() ?: 0.0) <= 0 ->
                "El precio debe ser mayor a 0"
            else -> null
        }

    // VALIDEZ DEL FORMULARIO

    val isFormValid: Boolean
        get() = titulo.isNotBlank() && tituloError == null &&
                descripcion.isNotBlank() && descripcionError == null &&
                categoria.isNotBlank() && categoriaError == null &&
                material.isNotBlank() && materialError == null &&
                precioError == null

    // ¿HUBO CAMBIOS?
    val hasChanges: Boolean
        get() = if (!isEditMode) true
        else titulo != originalTitulo ||
                descripcion != originalDescripcion ||
                categoria != originalCategoria ||
                material != originalMaterial ||
                precioReferencial != originalPrecio ||
                tipoPrecio != originalTipoPrecio
}