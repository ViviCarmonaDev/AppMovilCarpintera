package com.vivicarmonadev.appmovil_carpinteria.domain.repository

import com.vivicarmonadev.appmovil_carpinteria.domain.model.Notificacion
import kotlinx.coroutines.flow.Flow

interface NotificacionRepository {
    fun getNotificaciones(uid: String): Flow<List<Notificacion>>
    suspend fun marcarComoLeida(id: String): Result<Unit>
    suspend fun marcarTodasComoLeidas(uid: String): Result<Unit>
}