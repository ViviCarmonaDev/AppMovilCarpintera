package com.vivicarmonadev.appmovil_carpinteria.ui.auth

import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole

/**
 * Estado de la UI de "Completar perfil".
 *
 * Se muestra cuando un usuario entra por Google y necesita completar
 * los datos que Google no provee: rol, teléfono y dirección.
 *
 * Los nombres, apellidos y email vienen pre-llenados desde Google.
 */
data class CompleteProfileUiState(

    // Datos pre-llenados desde Google (solo lectura, excepto nombres/apellidos)

    val uid: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val email: String = "",                    // solo lectura
    val photoUrl: String? = null,

    // Datos que el usuario debe completar
    val selectedRole: UserRole? = null,
    val telefono: String = "",
    val direccion: String = "",

    // ---- Flags de "tocado" ----
    val nombresTouched: Boolean = false,
    val apellidosTouched: Boolean = false,
    val telefonoTouched: Boolean = false,
    val direccionTouched: Boolean = false,
    val roleTouched: Boolean = false,

    // ---- Estado general ----
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
) {
    // VALIDACIONES
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

    val direccionError: String?
        get() = when {
            !direccionTouched -> null
            direccion.isBlank() -> "Ingresa tu dirección"
            direccion.trim().length < 5 -> "Mínimo 5 caracteres"
            else -> null
        }

    // VALIDEZ DEL FORMULARIO

    val isFormValid: Boolean
        get() = selectedRole != null &&
                nombres.isNotBlank() && nombresError == null &&
                apellidos.isNotBlank() && apellidosError == null &&
                telefono.isNotBlank() && telefonoError == null &&
                direccion.isNotBlank() && direccionError == null
}