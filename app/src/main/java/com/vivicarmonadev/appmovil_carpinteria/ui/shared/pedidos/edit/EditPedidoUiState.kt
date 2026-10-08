package com.vivicarmonadev.appmovil_carpinteria.ui.shared.pedidos.edit

import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpinteroResumen
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido

data class EditPedidoUiState(
    // ---- Identificación ----
    val id: String = "",
    val numeroSecuencial: Long = 0L,
    val clientUid: String = "",
    val clienteNombre: String = "",
    val status: EstadoPedido = EstadoPedido.PENDIENTE,

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

    // ---- Valores originales ----
    val originalTitulo: String = "",
    val originalCategoria: String = "",
    val originalTipoMadera: String = "",
    val originalAnchoCm: String = "",
    val originalAltoCm: String = "",
    val originalProfundidadCm: String = "",
    val originalDescripcion: String = "",
    val originalFechaEstimada: Long? = null,
    val originalPresupuestoMax: String = "",
    val originalImagenesUrls: List<String> = emptyList(),
    val originalCarpenterUid: String? = null,

    // ---- Flags de "tocado" ----
    val tituloTouched: Boolean = false,
    val categoriaTouched: Boolean = false,
    val tipoMaderaTouched: Boolean = false,
    val anchoTouched: Boolean = false,
    val altoTouched: Boolean = false,
    val profundidadTouched: Boolean = false,
    val descripcionTouched: Boolean = false,
    val presupuestoTouched: Boolean = false,
    val fechaTouched: Boolean = false,

    // ---- Estado general ----
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val showDiscardDialog: Boolean = false,
    val showDatePicker: Boolean = false
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

    // VALIDEZ

    val isFormValid: Boolean
        get() = titulo.isNotBlank() && tituloError == null &&
                categoria.isNotBlank() && categoriaError == null &&
                tipoMadera.isNotBlank() && tipoMaderaError == null &&
                descripcion.isNotBlank() && descripcionError == null &&
                presupuestoError == null

    // ¿HUBO CAMBIOS?

    val hasChanges: Boolean
        get() = titulo != originalTitulo ||
                descripcion != originalDescripcion ||
                categoria != originalCategoria ||
                tipoMadera != originalTipoMadera ||
                anchoCm != originalAnchoCm ||
                altoCm != originalAltoCm ||
                profundidadCm != originalProfundidadCm ||
                presupuestoMax != originalPresupuestoMax ||
                fechaEstimada != originalFechaEstimada ||
                imagenesUrls != originalImagenesUrls ||
                carpenterUid != originalCarpenterUid
}