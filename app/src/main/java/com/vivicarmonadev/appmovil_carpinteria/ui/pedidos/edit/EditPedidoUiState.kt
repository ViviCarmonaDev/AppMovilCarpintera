package com.vivicarmonadev.appmovil_carpinteria.ui.pedidos.edit

/**
 * Estado de la UI de "Crear/Editar pedido".

 * Se usa para dos casos:
 *  - Crear: formulario vacío, guarda con create
 *  - Editar: formulario pre-llenado, guarda con update

 * El flag `isEditMode` diferencia entre los dos casos.
 */
data class EditPedidoUiState(
    // ---- Datos del formulario ----
    val id: String = "",
    val clientUid: String = "",

    // Sección 1: Tipo de mueble y medidas
    val titulo: String = "",
    val categoria: String = "",
    val tipoMadera: String = "",
    val anchoCm: String = "",               // como String, se convierte a Double al guardar
    val altoCm: String = "",
    val profundidadCm: String = "",

    // Sección 2: Detalles
    val descripcion: String = "",
    val fechaEstimada: Long? = null,        // timestamp
    val presupuestoMax: String = "",        // como String, opcional

    // ---- Valores originales (para detectar cambios) ----
    val originalTitulo: String = "",
    val originalCategoria: String = "",
    val originalTipoMadera: String = "",
    val originalAnchoCm: String = "",
    val originalAltoCm: String = "",
    val originalProfundidadCm: String = "",
    val originalDescripcion: String = "",
    val originalFechaEstimada: Long? = null,
    val originalPresupuestoMax: String = "",

    // ---- Flags de "tocado" ----
    val tituloTouched: Boolean = false,
    val categoriaTouched: Boolean = false,
    val tipoMaderaTouched: Boolean = false,
    val anchoTouched: Boolean = false,
    val altoTouched: Boolean = false,
    val profundidadTouched: Boolean = false,
    val descripcionTouched: Boolean = false,
    val fechaTouched: Boolean = false,
    val presupuestoTouched: Boolean = false,

    // ---- Estado general ----
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val isEditMode: Boolean = false,
    val showDiscardDialog: Boolean = false,

    // ---- Date picker ----
    val showDatePicker: Boolean = false
) {

    // VALIDACIONES

    val tituloError: String?
        get() = when {
            !tituloTouched -> null
            titulo.isBlank() -> "Ingresa un título"
            titulo.trim().length < 3 -> "Mínimo 3 caracteres"
            titulo.trim().length > 100 -> "Máximo 100 caracteres"
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

    val anchoError: String?
        get() = validarMedida(anchoCm, anchoTouched, "ancho")

    val altoError: String?
        get() = validarMedida(altoCm, altoTouched, "alto")

    val profundidadError: String?
        get() = validarMedida(profundidadCm, profundidadTouched, "profundidad")

    val descripcionError: String?
        get() = when {
            !descripcionTouched -> null
            descripcion.isBlank() -> "Ingresa una descripción"
            descripcion.trim().length < 20 -> "Mínimo 20 caracteres"
            descripcion.trim().length > 1000 -> "Máximo 1000 caracteres"
            else -> null
        }

    val presupuestoError: String?
        get() = when {
            !presupuestoTouched -> null
            presupuestoMax.isBlank() -> null               // opcional
            !presupuestoMax.matches(Regex("^\\d+(\\.\\d{1,2})?$")) ->
                "Solo números (ej: 500 o 500.50)"
            (presupuestoMax.toDoubleOrNull() ?: 0.0) <= 0 ->
                "El presupuesto debe ser mayor a 0"
            else -> null
        }

    private fun validarMedida(
        valor: String,
        touched: Boolean,
        nombre: String
    ): String? = when {
        !touched -> null
        valor.isBlank() -> null                            // opcional
        !valor.matches(Regex("^\\d+(\\.\\d{1,2})?$")) ->
            "Solo números"
        (valor.toDoubleOrNull() ?: 0.0) <= 0 ->
            "Debe ser mayor a 0"
        (valor.toDoubleOrNull() ?: 0.0) > 5000 ->
            "Máximo 5000 cm"
        else -> null
    }

    // VALIDEZ DEL FORMULARIO

    val isFormValid: Boolean
        get() = titulo.isNotBlank() && tituloError == null &&
                categoria.isNotBlank() && categoriaError == null &&
                tipoMadera.isNotBlank() && tipoMaderaError == null &&
                descripcion.isNotBlank() && descripcionError == null &&
                anchoError == null &&
                altoError == null &&
                profundidadError == null &&
                presupuestoError == null

    // ¿HUBO CAMBIOS?

    val hasChanges: Boolean
        get() = if (!isEditMode) true
        else titulo != originalTitulo ||
                categoria != originalCategoria ||
                tipoMadera != originalTipoMadera ||
                anchoCm != originalAnchoCm ||
                altoCm != originalAltoCm ||
                profundidadCm != originalProfundidadCm ||
                descripcion != originalDescripcion ||
                fechaEstimada != originalFechaEstimada ||
                presupuestoMax != originalPresupuestoMax
}