package com.vivicarmonadev.appmovil_carpinteria.ui.auth

/**
 * Estado de la UI del login.
 *
 * Los flags "touched" indican si el usuario ya interactuó con cada campo,
 * para no mostrar errores antes de que escriba.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,

    // Flags: ¿el usuario ya tocó este campo?
    val emailTouched: Boolean = false,
    val passwordTouched: Boolean = false
) {

    // VALIDACIONES — devuelven String? (null = sin error)

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
            password.isBlank() -> "Ingresa tu contraseña"
            password.length < 6 -> "Mínimo 6 caracteres"
            else -> null
        }

    // VALIDEZ GENERAL

    val isEmailValid: Boolean
        get() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    val isPasswordValid: Boolean
        get() = password.length >= 6

    val isFormValid: Boolean
        get() = isEmailValid && isPasswordValid
}