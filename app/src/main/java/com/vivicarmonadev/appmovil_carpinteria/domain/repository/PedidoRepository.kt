package com.vivicarmonadev.appmovil_carpinteria.domain.repository

import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de pedidos.

 * Define QUÉ se puede hacer con los pedidos, pero no CÓMO.
 * El "cómo" lo implementa OrderRepositoryImpl en la capa data.

 * Reglas de negocio:
 *  - Un cliente solo puede editar/cancelar pedidos en estado PENDIENTE.
 *  - Un cliente solo ve SUS pedidos.
 *  - En futuras versiones, los carpinteros verán pedidos disponibles.
 */
interface PedidoRepository {

     // Flujo reactivo de los pedidos de UN cliente.
     // Emite la lista cada vez que cambia (creación, edición, cancelación).

    fun getMyPedidos(clientUid: String): Flow<List<Pedido>>

     // Lee un pedido específico por ID. Devuelve null si no existe.

    suspend fun getPedidoById(id: String): Result<Pedido?>

     // Crea un nuevo pedido. El pedido se crea con estado PENDIENTE por defecto.
    suspend fun createPedido(order: Pedido): Result<Pedido>

     // Actualiza un pedido existente. Solo debería permitirse si el pedido está en PENDIENTE.

    suspend fun updatePedido(order: Pedido): Result<Pedido>

     // Cambia el estado de un pedido.
     // Útil para cancelar (PENDIENTE → CANCELADO) o avanzar (PENDIENTE → ACEPTADO).

    suspend fun updatePedidoStatus(
        pedidoId: String,
        nuevoEstado: EstadoPedido
    ): Result<Unit>

     // Elimina un pedido por ID. Solo el dueño puede eliminarlo.

    suspend fun deletePedido(id: String): Result<Unit>
}