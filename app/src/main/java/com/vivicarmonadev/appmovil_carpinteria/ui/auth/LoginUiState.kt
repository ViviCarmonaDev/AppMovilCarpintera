package com.vivicarmonadev.appmovil_carpinteria.ui.auth

/**
 * Estado de la UI del login.
 *
 * Representa todo lo que la pantalla de login necesita saber en un momento dado.
 * Es inmutable: cuando algo cambia, se crea una copia nueva.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
) {

    val isEmailValid: Boolean
        get() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    val isPasswordValid: Boolean
        get() = password.length >= 6

    val isFormValid: Boolean
        get() = isEmailValid && isPasswordValid
}