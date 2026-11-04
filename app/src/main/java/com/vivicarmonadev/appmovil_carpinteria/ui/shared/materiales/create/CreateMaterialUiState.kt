package com.vivicarmonadev.appmovil_carpinteria.ui.shared.materiales.create

data class CreateMaterialUiState(
    val categoriaId: String = "", // Se pasa desde la pantalla anterior
    val nombre: String = "",
    val descripcion: String = "",
    val precioReferencial: String = "", // Como String para el input
    val imagenUrl: String? = null,

    // Flags de "tocado"
    val nombreTouched: Boolean = false,
    val descripcionTouched: Boolean = false,
    val precioTouched: Boolean = false,

    // Estado general
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val showDiscardDialog: Boolean = false
) {
    val nombreError: String?
        get() = when {
            !nombreTouched -> null
            nombre.isBlank() -> "Ingresa el nombre del material"
            nombre.trim().length < 3 -> "Mínimo 3 caracteres"
            else -> null
        }

    val descripcionError: String?
        get() = when {
            !descripcionTouched -> null
            descripcion.isBlank() -> "Ingresa una descripción"
            descripcion.trim().length < 10 -> "Mínimo 10 caracteres"
            else -> null
        }

    val precioError: String?
        get() = when {
            !precioTouched -> null
            precioReferencial.isBlank() -> "Ingresa un precio referencial"
            !precioReferencial.matches(Regex("^\\d+(\\.\\d{1,2})?$")) -> "Solo números (ej: 150 o 150.50)"
            (precioReferencial.toDoubleOrNull() ?: 0.0) <= 0 -> "El precio debe ser mayor a 0"
            else -> null
        }

    val isFormValid: Boolean
        get() = nombre.isNotBlank() && nombreError == null &&
                descripcion.isNotBlank() && descripcionError == null &&
                precioReferencial.isNotBlank() && precioError == null

    val hasChanges: Boolean
        get() = nombre.isNotBlank() || descripcion.isNotBlank() || precioReferencial.isNotBlank() || imagenUrl != null
}