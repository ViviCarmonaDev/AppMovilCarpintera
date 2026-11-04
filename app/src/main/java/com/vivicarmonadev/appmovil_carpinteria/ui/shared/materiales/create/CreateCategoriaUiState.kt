package com.vivicarmonadev.appmovil_carpinteria.ui.shared.materiales.create

data class CreateCategoriaUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val imagenUrl: String? = null, // URL de Cloudinary

    // Flags de "tocado"
    val nombreTouched: Boolean = false,
    val descripcionTouched: Boolean = false,

    // Estado general
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val showDiscardDialog: Boolean = false
) {
    val nombreError: String?
        get() = when {
            !nombreTouched -> null
            nombre.isBlank() -> "Ingresa el nombre de la categoría"
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

    val isFormValid: Boolean
        get() = nombre.isNotBlank() && nombreError == null &&
                descripcion.isNotBlank() && descripcionError == null

    val hasChanges: Boolean
        get() = nombre.isNotBlank() || descripcion.isNotBlank() || imagenUrl != null
}