package com.vivicarmonadev.appmovil_carpinteria.ui.shared.pedidos.create

import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpinteroResumen

data class CreatePedidoUiState(
    // ---- Usuario ----
    val clientUid: String = "",
    val clienteNombre: String = "",

    // ---- Datos del formulario ----
    val titulo: String = "",
    val categoria: String = "",
    val tipoMadera: String = "",
    val anchoCm: String = "",
    val altoCm: String = "",
    val profundidadCm: String = "",
    val descripcion: String = "",
    val fechaEstimada: Long? = null,
    val presupuestoMax: String = "",

    // ---- Imágenes ----
    val imagenesUrls: List<String> = emptyList(),

    // ---- Carpintero ----
    val carpenterUid: String? = null,
    val carpinteroNombre: String? = null,
    val carpinterosDisponibles: List<CarpinteroResumen> = emptyList(),

    // ---- Flags de "tocado" ----
    val tituloTouched: Boolean = false,
    val categoriaTouched: Boolean = false,
    val tipoMaderaTouched: Boolean = false,
    val anchoTouched: Boolean = false,
    val altoTouched: Boolean = false,
    val profundidadTouched: Boolean = false,
    val descripcionTouched: Boolean = false,
    val presupuestoTouched: Boolean = false,

    // ---- Estado general ----
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val showDiscardDialog: Boolean = false,
    val showDatePicker: Boolean = false
) {

    val tituloError: String?
        get() = when {
            !tituloTouched -> null
            titulo.isBlank() -> "Ingresa un título"
            titulo.trim().length < 3 -> "Mínimo 3 caracteres"
            titulo.trim().length > 80 -> "Máximo 80 caracteres"
            else -> null
        }

    val categoriaError: String?
        get() = when {
            !categoriaTouched -> null
            categoria.isBlank() -> "Ingresa una categoría"
            categoria.trim().length < 3 -> "Mínimo 3 caracteres"
            else -> null
        }

    val tipoMaderaError: String?
        get() = when {
            !tipoMaderaTouched -> null
            tipoMadera.isBlank() -> "Ingresa un tipo de madera"
            tipoMadera.trim().length < 3 -> "Mínimo 3 caracteres"
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

    val presupuestoError: String?
        get() = when {
            !presupuestoTouched -> null
            presupuestoMax.isBlank() -> null
            !presupuestoMax.matches(Regex("^\\d+(\\.\\d{1,2})?$")) ->
                "Solo números (ej: 500 o 500.50)"
            (presupuestoMax.toDoubleOrNull() ?: 0.0) <= 0 ->
                "El presupuesto debe ser mayor a 0"
            else -> null
        }

    val isFormValid: Boolean
        get() = titulo.isNotBlank() && tituloError == null &&
                categoria.isNotBlank() && categoriaError == null &&
                tipoMadera.isNotBlank() && tipoMaderaError == null &&
                descripcion.isNotBlank() && descripcionError == null &&
                presupuestoError == null

    val hasChanges: Boolean
        get() = titulo.isNotBlank() ||
                descripcion.isNotBlank() ||
                categoria.isNotBlank() ||
                tipoMadera.isNotBlank() ||
                anchoCm.isNotBlank() ||
                altoCm.isNotBlank() ||
                profundidadCm.isNotBlank() ||
                presupuestoMax.isNotBlank() ||
                imagenesUrls.isNotEmpty() ||
                carpenterUid != null
}