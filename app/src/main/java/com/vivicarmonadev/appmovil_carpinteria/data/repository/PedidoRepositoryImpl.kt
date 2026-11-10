package com.vivicarmonadev.appmovil_carpinteria.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDomain
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDto
import com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore.FirestorePedidoDataSource
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.domain.repository.PedidoRepository
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Notificacion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class PedidoRepositoryImpl(
    private val dataSource: FirestorePedidoDataSource = FirestorePedidoDataSource(),
    private val notificacionRepository: NotificacionRepositoryImpl = NotificacionRepositoryImpl()
) : PedidoRepository {

    override fun getMyPedidos(clientUid: String): Flow<List<Pedido>> {
        return dataSource.getPedidosByClientFlow(clientUid).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getPedidoFlow(id: String): Flow<Pedido?> {
        return dataSource.getPedidoFlow(id).map { it?.toDomain() }
    }

    override suspend fun getPedidoById(id: String): Result<Pedido?> {
        return try {
            val result = dataSource.getPedidoById(id)
            if (result.isFailure) {
                return Result.failure(result.exceptionOrNull() ?: Exception("Error al leer el pedido"))
            }
            val dto = result.getOrNull()
            Result.success(dto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createPedido(order: Pedido): Result<Pedido> {
        return try {
            val pedidoConEstado = order.copy(status = EstadoPedido.PENDIENTE)
            val dto = pedidoConEstado.toDto()
            val result = dataSource.createPedido(dto)

            if (result.isFailure) {
                return Result.failure(result.exceptionOrNull() ?: Exception("Error al crear el pedido"))
            }

            val createdDto = result.getOrNull() ?: return Result.failure(Exception("No se pudo crear el pedido"))

            if (!order.carpenterUid.isNullOrBlank()) {
                val notificacion = Notificacion(destinatarioUid = order.carpenterUid!!, titulo = "Nuevo pedido recibido", mensaje = "Tienes un nuevo pedido: ${order.titulo}", tipo = "nuevo_pedido", referenciaId = createdDto.id, leida = false)
                notificacionRepository.crearNotificacion(notificacion)
            } else {
                val snapshot = Firebase.firestore.collection("users").whereEqualTo("role", "carpenter").get().await()
                snapshot.documents.forEach { doc ->
                    val carpenterUid = doc.id
                    val notificacion = Notificacion(destinatarioUid = carpenterUid, titulo = "Nuevo pedido libre", mensaje = "Hay una nueva solicitud disponible: ${order.titulo}", tipo = "pedido_libre", referenciaId = createdDto.id, leida = false)
                    notificacionRepository.crearNotificacion(notificacion)
                }
            }

            Result.success(createdDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePedido(order: Pedido): Result<Pedido> {
        return try {
            if (order.id.isBlank()) return Result.failure(Exception("El pedido no tiene ID"))
            val dto = order.toDto()
            val result = dataSource.updatePedido(dto)
            if (result.isFailure) return Result.failure(result.exceptionOrNull() ?: Exception("Error al actualizar el pedido"))
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePedidoStatus(pedidoId: String, nuevoEstado: EstadoPedido): Result<Unit> {
        return try {
            if (pedidoId.isBlank()) return Result.failure(Exception("El pedido no tiene ID"))
            dataSource.updateEstado(pedidoId, nuevoEstado.toFirestoreValue())
            val pedido = getPedidoById(pedidoId).getOrNull()
            if (pedido != null) {
                val notificacion = Notificacion(destinatarioUid = pedido.clientUid, titulo = "Estado de tu pedido actualizado", mensaje = "Tu pedido \"${pedido.titulo}\" cambió a ${nuevoEstado.toDisplayText()}.", tipo = "estado_pedido", referenciaId = pedidoId, leida = false)
                notificacionRepository.crearNotificacion(notificacion)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePedido(id: String): Result<Unit> {
        return try {
            if (id.isBlank()) return Result.failure(Exception("El pedido no tiene ID"))
            dataSource.deletePedido(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPedidosAsignadosAMi(carpenterUid: String): Flow<List<Pedido>> {
        return dataSource.getPedidosByCarpenterFlow(carpenterUid).map { list -> list.map { it.toDomain() } }
    }

    override fun getPedidosLibres(): Flow<List<Pedido>> {
        return dataSource.getPedidosLibresFlow().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun asignarCarpintero(pedidoId: String, carpenterUid: String, carpinteroNombre: String): Result<Unit> {
        return try {
            if (pedidoId.isBlank()) return Result.failure(Exception("El pedido no tiene ID"))
            dataSource.asignarCarpintero(pedidoId = pedidoId, carpenterUid = carpenterUid, carpinteroNombre = carpinteroNombre)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNextNumeroSecuencial(): Result<Long> {
        return try {
            dataSource.getNextNumeroSecuencial()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}