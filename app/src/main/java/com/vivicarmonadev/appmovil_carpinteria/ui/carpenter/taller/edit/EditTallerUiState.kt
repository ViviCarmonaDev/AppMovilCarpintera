package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.taller.edit

data class EditTallerUiState(
    // ---- Datos ----
    val uid: String = "",
    val nombreTaller: String = "",
    val ruc: String = "",
    val descripcion: String = "",
    val direccionTaller: String = "",
    val telefonoTaller: String = "",
    val aniosExperiencia: String = "",
    val skills: List<String> = emptyList(),

    // ---- Valores originales (para detectar cambios) ----
    val originalNombreTaller: String = "",
    val originalRuc: String = "",
    val originalDescripcion: String = "",
    val originalDireccionTaller: String = "",
    val originalTelefonoTaller: String = "",
    val originalAniosExperiencia: String = "",
    val originalSkills: List<String> = emptyList(),

    // ---- Flags de "tocado" ----
    val nombreTallerTouched: Boolean = false,
    val rucTouched: Boolean = false,
    val descripcionTouched: Boolean = false,
    val direccionTallerTouched: Boolean = false,
    val telefonoTallerTouched: Boolean = false,
    val aniosExperienciaTouched: Boolean = false,
    val skillsTouched: Boolean = false,

    // ---- Estado general ----
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val showDiscardDialog: Boolean = false
) {

    // ---- VALIDACIONES ----

    val nombreTallerError: String?
        get() = when {
            !nombreTallerTouched -> null
            nombreTaller.isBlank() -> "Ingresa el nombre del taller"
            nombreTaller.trim().length < 2 -> "Mínimo 2 caracteres"
            else -> null
        }

    val rucError: String?
        get() = when {
            !rucTouched -> null
            ruc.isBlank() -> "Ingresa tu RUC"
            !ruc.matches(Regex("^\\d+$")) -> "Solo números"
            ruc.length != 11 -> "Debe tener 11 dígitos"
            else -> null
        }

    val descripcionError: String?
        get() = when {
            !descripcionTouched -> null
            descripcion.isBlank() -> "Ingresa una descripción"
            descripcion.trim().length < 20 -> "Mínimo 20 caracteres"
            else -> null
        }

    val direccionTallerError: String?
        get() = when {
            !direccionTallerTouched -> null
            direccionTaller.isBlank() -> "Ingresa la dirección"
            direccionTaller.trim().length < 5 -> "Mínimo 5 caracteres"
            else -> null
        }

    val telefonoTallerError: String?
        get() = when {
            !telefonoTallerTouched -> null
            telefonoTaller.isBlank() -> "Ingresa el teléfono"
            !telefonoTaller.matches(Regex("^\\d+$")) -> "Solo números"
            telefonoTaller.length != 9 -> "Debe tener 9 dígitos"
            else -> null
        }

    val aniosExperienciaError: String?
        get() = when {
            !aniosExperienciaTouched -> null
            aniosExperiencia.isBlank() -> "Ingresa los años"
            !aniosExperiencia.matches(Regex("^\\d+$")) -> "Solo números"
            (aniosExperiencia.toIntOrNull() ?: 0) > 60 -> "Máximo 60 años"
            else -> null
        }

    val skillsError: String?
        get() = when {
            !skillsTouched -> null
            skills.isEmpty() -> "Selecciona al menos una habilidad"
            else -> null
        }

    // ---- VALIDEZ ----

    val isFormValid: Boolean
        get() = nombreTaller.isNotBlank() && nombreTallerError == null &&
                ruc.isNotBlank() && rucError == null &&
                descripcion.isNotBlank() && descripcionError == null &&
                direccionTaller.isNotBlank() && direccionTallerError == null &&
                telefonoTaller.isNotBlank() && telefonoTallerError == null &&
                aniosExperiencia.isNotBlank() && aniosExperienciaError == null &&
                skills.isNotEmpty() && skillsError == null

    // ---- ¿HUBO CAMBIOS? ----

    val hasChanges: Boolean
        get() = nombreTaller != originalNombreTaller ||
                ruc != originalRuc ||
                descripcion != originalDescripcion ||
                direccionTaller != originalDireccionTaller ||
                telefonoTaller != originalTelefonoTaller ||
                aniosExperiencia != originalAniosExperiencia ||
                skills != originalSkills
}