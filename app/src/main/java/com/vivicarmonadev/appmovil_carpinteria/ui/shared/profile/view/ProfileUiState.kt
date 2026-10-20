package com.vivicarmonadev.appmovil_carpinteria.ui.shared.profile.view

import com.vivicarmonadev.appmovil_carpinteria.domain.model.User

data class ProfileUiState(
    // ---- Datos del usuario ----
    val user: User? = null,
    val isUploadingPhoto: Boolean = false,
    val photoError: String? = null,

    // ---- Estadísticas ----
    val totalResenas: Int = 0,
    val totalPedidos: Int = 0,
    val ratingPromedio: Double = 0.0,

    // ---- Estado general ----
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val isLoggedIn: Boolean
        get() = user != null

    val isCarpenter: Boolean
        get() = user?.role?.name?.lowercase() == "carpenter"

    // Texto del rating (ej. "4.8" o "—" si no tiene)
    val ratingTexto: String
        get() = if (ratingPromedio > 0) "%.1f".format(ratingPromedio) else "—"

    // Texto de las reseñas (ej. "12")
    val resenasTexto: String
        get() = totalResenas.toString()

    // Texto de los pedidos (ej. "5")
    val pedidosTexto: String
        get() = totalPedidos.toString()
}