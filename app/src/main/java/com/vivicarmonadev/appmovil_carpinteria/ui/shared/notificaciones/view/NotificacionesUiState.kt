package com.vivicarmonadev.appmovil_carpinteria.ui.shared.notificaciones.view

import com.vivicarmonadev.appmovil_carpinteria.domain.model.Notificacion

data class NotificacionesUiState(
    val notificaciones: List<Notificacion> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && notificaciones.isEmpty()

    val noLeidas: Int
        get() = notificaciones.count { !it.leida }
}