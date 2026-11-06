package com.vivicarmonadev.appmovil_carpinteria.data.repository

import com.vivicarmonadev.appmovil_carpinteria.data.model.toDomain
import com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore.FirestoreNotificacionDataSource
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Notificacion
import com.vivicarmonadev.appmovil_carpinteria.domain.repository.NotificacionRepository
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDto

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificacionRepositoryImpl(
    private val dataSource: FirestoreNotificacionDataSource = FirestoreNotificacionDataSource()
) : NotificacionRepository {

    override fun getNotificaciones(uid: String): Flow<List<Notificacion>> {
        return dataSource.getNotificacionesFlow(uid).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun marcarComoLeida(id: String): Result<Unit> {
        return dataSource.marcarComoLeida(id)
    }

    override suspend fun marcarTodasComoLeidas(uid: String): Result<Unit> {
        return dataSource.marcarTodasComoLeidas(uid)
    }

    suspend fun crearNotificacion(notificacion: Notificacion): Result<Unit> {
        return try {
            val dto = notificacion.toDto()
            dataSource.crearNotificacion(dto)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}