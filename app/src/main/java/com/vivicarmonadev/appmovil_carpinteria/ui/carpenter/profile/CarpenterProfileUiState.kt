package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.profile

/**
 * Estado de la UI del "Perfil del taller".
 *
 * Se muestra cuando un carpintero completa o edita los datos
 * de su taller profesional: nombre, RUC, descripción, dirección,
 * teléfono, años de experiencia y skills.
 */
data class CarpenterProfileUiState(
    // ---- Datos ----
    val uid: String = "",
    val nombreTaller: String = "",
    val ruc: String = "",
    val descripcion: String = "",
    val direccionTaller: String = "",
    val telefonoTaller: String = "",
    val aniosExperiencia: String = "",      // guarda como String y convertimos al guardar
    val skills: List<String> = emptyList(),

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
    val isEditMode: Boolean = false       // true si ya existía un perfil
) {

    // VALIDACIONES

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
            !ruc.startsWith("10") &&
                    !ruc.startsWith("15") &&
                    !ruc.startsWith("17") &&
                    !ruc.startsWith("20") -> "RUC no válido (debe empezar con 10, 15, 17 o 20)"
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
            direccionTaller.isBlank() -> "Ingresa la dirección del taller"
            direccionTaller.trim().length < 5 -> "Mínimo 5 caracteres"
            else -> null
        }

    val telefonoTallerError: String?
        get() = when {
            !telefonoTallerTouched -> null
            telefonoTaller.isBlank() -> "Ingresa el teléfono del taller"
            !telefonoTaller.matches(Regex("^\\d+$")) -> "Solo números"
            telefonoTaller.length != 9 -> "Debe tener 9 dígitos"
            !telefonoTaller.startsWith("9") -> "Debe empezar con 9"
            else -> null
        }

    val aniosExperienciaError: String?
        get() = when {
            !aniosExperienciaTouched -> null
            aniosExperiencia.isBlank() -> "Ingresa los años de experiencia"
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

    // VALIDEZ GENERAL

    val isFormValid: Boolean
        get() = nombreTaller.isNotBlank() && nombreTallerError == null &&
                ruc.isNotBlank() && rucError == null &&
                descripcion.isNotBlank() && descripcionError == null &&
                direccionTaller.isNotBlank() && direccionTallerError == null &&
                telefonoTaller.isNotBlank() && telefonoTallerError == null &&
                aniosExperiencia.isNotBlank() && aniosExperienciaError == null &&
                skills.isNotEmpty() && skillsError == null
}