package com.vivicarmonadev.appmovil_carpinteria.data.repository

import com.vivicarmonadev.appmovil_carpinteria.data.model.toDomain
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDto
import com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore.FirestoreCotizacionDataSource
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Cotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoCotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.repository.CotizacionRepository
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Notificacion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del CotizacionRepository usando Firestore.
 * Es el "pegamento" entre la capa de dominio y la capa de datos.
 */
class CotizacionRepositoryImpl(
    private val dataSource: FirestoreCotizacionDataSource = FirestoreCotizacionDataSource(),
    private val notificacionRepository: NotificacionRepositoryImpl = NotificacionRepositoryImpl()
) : CotizacionRepository {

    override fun getCotizacionesByCarpenter(carpenterUid: String): Flow<List<Cotizacion>> {
        return dataSource.getCotizacionesByCarpenterFlow(carpenterUid).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getCotizacionesByCliente(clienteUid: String): Flow<List<Cotizacion>> {
        return dataSource.getCotizacionesByClienteFlow(clienteUid).map { list ->
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

    fun getCotizacionFlow(id: String): Flow<Cotizacion?> {
        return dataSource.getCotizacionFlow(id).map { dto -> dto?.toDomain() }
    }

    override suspend fun createCotizacion(cotizacion: Cotizacion): Result<Cotizacion> {
        return try {
            dataSource.createCotizacion(cotizacion.toDto()).fold(
                onSuccess = { createdDto ->
                    // Notificar al cliente
                    val notificacion = Notificacion(
                        destinatarioUid = cotizacion.clienteUid,
                        titulo = "Nueva cotización recibida",
                        mensaje = "${cotizacion.carpinteroNombre} te envió una cotización por S/ ${cotizacion.costoTotal}",
                        tipo = "nueva_cotizacion",
                        referenciaId = createdDto.id,
                        leida = false
                    )
                    notificacionRepository.crearNotificacion(notificacion)

                    Result.success(createdDto.toDomain())
                },
                onFailure = { exception ->
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun updateCotizacion(cotizacion: Cotizacion): Result<Cotizacion> {
        return try {
            // 1. Actualizar en Firestore
            val result = dataSource.updateCotizacion(cotizacion.toDto())

            if (result.isSuccess) {
                // 2. Notificar al carpintero

                // CASO A: El cliente solicita cambios
                if (cotizacion.solicitudCambio && cotizacion.estado == EstadoCotizacion.PENDIENTE) {
                    val notificacion = Notificacion(
                        destinatarioUid = cotizacion.carpinteroUid,
                        titulo = "Solicitud de cambios",
                        mensaje = "El cliente solicita cambios en la cotización #${cotizacion.numeroPedido}. Revisa la observación.",
                        tipo = "solicitud_cambios",
                        referenciaId = cotizacion.id,
                        leida = false
                    )
                    notificacionRepository.crearNotificacion(notificacion)
                }

                // CASO B: El cliente acepta o rechaza la cotización
                if (cotizacion.carpinteroUid.isNotBlank() &&
                    (cotizacion.estado == EstadoCotizacion.ACEPTADA || cotizacion.estado == EstadoCotizacion.RECHAZADA)) {

                    val notificacion = Notificacion(
                        destinatarioUid = cotizacion.carpinteroUid,
                        titulo = "Respuesta a tu cotización",
                        mensaje = "El cliente ${cotizacion.estado.toDisplayText().lowercase()} tu cotización.",
                        tipo = "respuesta_cotizacion",
                        referenciaId = cotizacion.id,
                        leida = false
                    )
                    notificacionRepository.crearNotificacion(notificacion)
                }

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
    override suspend fun anularCotizacion(cotizacionId: String): Result<Unit> {
        return dataSource.updateEstado(cotizacionId, "ANULADA")
    }
}