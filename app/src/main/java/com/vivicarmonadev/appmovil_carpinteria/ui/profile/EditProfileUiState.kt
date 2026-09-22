package com.vivicarmonadev.appmovil_carpinteria.ui.profile

/**
 * Estado de la UI de "Editar perfil".
 *
 * Guarda dos versiones de los datos:
 *  - Los originales (como estaban al abrir la pantalla)
 *  - Los editados (como el usuario los va cambiando)
 *
 * Así podemos saber si el usuario cambió algo (`hasChanges`).
 */
data class EditProfileUiState(
    // ---- Datos originales ----
    val originalNombres: String = "",
    val originalApellidos: String = "",
    val originalTelefono: String = "",

    // ---- Datos editados ----
    val nombres: String = "",
    val apellidos: String = "",
    val telefono: String = "",

    // ---- Flags de "tocado" ----
    val nombresTouched: Boolean = false,
    val apellidosTouched: Boolean = false,
    val telefonoTouched: Boolean = false,

    // ---- Estado general ----
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
) {

    // ============================================
    // VALIDACIONES (mismas reglas que el registro)
    // ============================================

    val nombresError: String?
        get() = when {
            !nombresTouched -> null
            nombres.isBlank() -> "Ingresa tus nombres"
            nombres.trim().length < 2 -> "Mínimo 2 caracteres"
            !nombres.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) -> "Solo letras y espacios"
            else -> null
        }

    val apellidosError: String?
        get() = when {
            !apellidosTouched -> null
            apellidos.isBlank() -> "Ingresa tus apellidos"
            apellidos.trim().length < 2 -> "Mínimo 2 caracteres"
            !apellidos.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) -> "Solo letras y espacios"
            else -> null
        }

    val telefonoError: String?
        get() = when {
            !telefonoTouched -> null
            telefono.isBlank() -> "Ingresa tu teléfono"
            !telefono.matches(Regex("^\\d+$")) -> "Solo números"
            telefono.length != 9 -> "Debe tener 9 dígitos"
            !telefono.startsWith("9") -> "Debe empezar con 9"
            else -> null
        }

    // ============================================
    // VALIDEZ DEL FORMULARIO
    // ============================================

    val isFormValid: Boolean
        get() = nombres.isNotBlank() && nombresError == null &&
                apellidos.isNotBlank() && apellidosError == null &&
                telefono.isNotBlank() && telefonoError == null

    // ============================================
    // ¿HUBO CAMBIOS?
    // ============================================
    // Sirve para habilitar el botón "Guardar" solo si hay algo distinto.
    val hasChanges: Boolean
        get() = nombres != originalNombres ||
                apellidos != originalApellidos ||
                telefono != originalTelefono
}