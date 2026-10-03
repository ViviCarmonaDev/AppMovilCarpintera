package com.vivicarmonadev.appmovil_carpinteria.data.repository

import com.vivicarmonadev.appmovil_carpinteria.data.model.toDomain
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDto
import com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore.FirestorePedidoDataSource
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.domain.repository.PedidoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del PedidoRepository usando Firestore.
 *
 * Es el "pegamento" entre la capa de dominio (que solo conoce la interfaz) y la capa de datos (que conoce Firestore).
 */
class PedidoRepositoryImpl(
    private val dataSource: FirestorePedidoDataSource = FirestorePedidoDataSource()
) : PedidoRepository {

    // FLUJO REACTIVO

    override fun getMyPedidos(clientUid: String): Flow<List<Pedido>> {
        return dataSource.getPedidosByClientFlow(clientUid).map { list ->
            list.map { it.toDomain() }
        }
    }

    // LEER UNO

    override suspend fun getPedidoById(id: String): Result<Pedido?> {
        return try {
            val result = dataSource.getPedidoById(id)

            if (result.isFailure) {
                return Result.failure(
                    result.exceptionOrNull() ?: Exception("Error al leer el pedido")
                )
            }

            val dto = result.getOrNull()
            Result.success(dto?.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // CREAR

    override suspend fun createPedido(order: Pedido): Result<Pedido> {
        return try {
            // Forzar estado PENDIENTE al crear (regla de negocio)
            val pedidoConEstado = order.copy(
                status = EstadoPedido.PENDIENTE
            )

            val dto = pedidoConEstado.toDto()
            val result = dataSource.createPedido(dto)

            if (result.isFailure) {
                return Result.failure(
                    result.exceptionOrNull() ?: Exception("Error al crear el pedido")
                )
            }

            val createdDto = result.getOrNull()
                ?: return Result.failure(Exception("No se pudo crear el pedido"))

            Result.success(createdDto.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ACTUALIZAR

    override suspend fun updatePedido(order: Pedido): Result<Pedido> {
        return try {
            if (order.id.isBlank()) {
                return Result.failure(Exception("El pedido no tiene ID"))
            }

            val dto = order.toDto()
            val result = dataSource.updatePedido(dto)

            if (result.isFailure) {
                return Result.failure(
                    result.exceptionOrNull() ?: Exception("Error al actualizar el pedido")
                )
            }

            Result.success(order)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ACTUALIZAR SOLO ESTADO

    override suspend fun updatePedidoStatus(
        pedidoId: String,
        nuevoEstado: EstadoPedido
    ): Result<Unit> {
        return try {
            if (pedidoId.isBlank()) {
                return Result.failure(Exception("El pedido no tiene ID"))
            }

            dataSource.updateEstado(
                pedidoId = pedidoId,
                nuevoEstado = nuevoEstado.toFirestoreValue()
            )

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ELIMINAR

    override suspend fun deletePedido(id: String): Result<Unit> {
        return try {
            if (id.isBlank()) {
                return Result.failure(Exception("El pedido no tiene ID"))
            }

            dataSource.deletePedido(id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}