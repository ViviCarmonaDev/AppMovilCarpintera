package com.vivicarmonadev.appmovil_carpinteria.ui.auth

import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole

/**
 * Estado de la UI del registro.
 *
 * Cada campo tiene su propio error para mostrar mensajes específicos
 * debajo del input correspondiente. Los flags "touched" indican si el
 * usuario ya interactuó con el campo, para no mostrar errores antes de tiempo.
 */
data class RegisterUiState(
    // ---- Paso 1 ----
    val selectedRole: UserRole? = null,
    val nombres: String = "",
    val apellidos: String = "",
    val telefono: String = "",
    val direccion: String = "",


    // ---- Paso 2 ----
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    // ---- Estado general ----
    val isLoading: Boolean = false,
    val errorMessage: String? = null,   // error general (Firebase)
    val isSuccess: Boolean = false,

    // ---- Flags: ¿el usuario ya tocó este campo? ----
    val nombresTouched: Boolean = false,
    val apellidosTouched: Boolean = false,
    val telefonoTouched: Boolean = false,
    val direccionTouched: Boolean = false,
    val emailTouched: Boolean = false,
    val passwordTouched: Boolean = false,
    val confirmPasswordTouched: Boolean = false
) {

    // VALIDACIONES — devuelven String? (null = sin error)

    val nombresError: String?
        get() = when {
            !nombresTouched -> null
            nombres.isBlank() -> "Ingresa tus nombres"
            nombres.trim().length < 3 -> "Mínimo 3 caracteres"
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
            else -> null
        }

    val emailError: String?
        get() = when {
            !emailTouched -> null
            email.isBlank() -> "Ingresa tu correo"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Correo no válido"
            else -> null
        }

    val passwordError: String?
        get() = when {
            !passwordTouched -> null
            password.isBlank() -> "Ingresa una contraseña"
            password.length < 8 -> "Mínimo 8 caracteres"
            else -> null
        }

    val confirmPasswordError: String?
        get() = when {
            !confirmPasswordTouched -> null
            confirmPassword.isBlank() -> "Confirma tu contraseña"
            confirmPassword != password -> "Las contraseñas no coinciden"
            else -> null
        }

    // VALIDEZ DE CADA PASO (para habilitar/deshabilitar botones)

    val isStep1Valid: Boolean
        get() = selectedRole != null &&
                nombres.isNotBlank() && nombresError == null &&
                apellidos.isNotBlank() && apellidosError == null &&
                telefono.isNotBlank() && telefonoError == null &&
                direccion.isNotBlank() && direccionError == null


    val isStep2Valid: Boolean
        get() = email.isNotBlank() && emailError == null &&
                password.isNotBlank() && passwordError == null &&
                confirmPassword.isNotBlank() && confirmPasswordError == null
}