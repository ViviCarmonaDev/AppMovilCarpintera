package com.vivicarmonadev.appmovil_carpinteria.data.repository

import com.vivicarmonadev.appmovil_carpinteria.data.model.toDomain
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDto
import com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore.FirestoreCotizacionDataSource
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Cotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoCotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.repository.CotizacionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del CotizacionRepository usando Firestore.
 * Es el "pegamento" entre la capa de dominio y la capa de datos.
 */
class CotizacionRepositoryImpl(
    private val dataSource: FirestoreCotizacionDataSource = FirestoreCotizacionDataSource()
) : CotizacionRepository {

    override fun getCotizacionesByCarpenter(carpenterUid: String): Flow<List<Cotizacion>> {
        return dataSource.getCotizacionesByCarpenterFlow(carpenterUid).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getCotizacionesByPedido(pedidoId: String): Flow<List<Cotizacion>> {
        return dataSource.getCotizacionesByPedidoFlow(pedidoId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getCotizacionById(id: String): Result<Cotizacion?> {
        return try {
            val result = dataSource.getCotizacionById(id)
            result.map { dto -> dto?.toDomain() }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createCotizacion(cotizacion: Cotizacion): Result<Cotizacion> {
        return try {
            val result = dataSource.createCotizacion(cotizacion.toDto())
            result.map { it.toDomain() }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCotizacion(cotizacion: Cotizacion): Result<Cotizacion> {
        return try {
            val result = dataSource.updateCotizacion(cotizacion.toDto())
            if (result.isSuccess) {
                Result.success(cotizacion)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Error al actualizar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEstado(
        cotizacionId: String,
        nuevoEstado: EstadoCotizacion
    ): Result<Unit> {
        return dataSource.updateEstado(cotizacionId, nuevoEstado.toFirestoreValue())
    }

    override suspend fun deleteCotizacion(id: String): Result<Unit> {
        return dataSource.deleteCotizacion(id)
    }

    override suspend fun aceptarCotizacion(cotizacionId: String): Result<Unit> {
        return dataSource.updateEstado(cotizacionId, "ACEPTADA")
    }

    override suspend fun rechazarCotizacion(cotizacionId: String): Result<Unit> {
        return dataSource.updateEstado(cotizacionId, "RECHAZADA")
    }
}