package com.vivicarmonadev.appmovil_carpinteria.domain.repository

import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de pedidos.
 *
 * Define QUÉ se puede hacer con los pedidos, pero no CÓMO.
 *
 * Reglas de negocio:
 *  - Un cliente solo puede editar/cancelar pedidos en estado PENDIENTE.
 *  - Un cliente solo ve SUS pedidos.
 *  - Un carpintero ve:
 *      · Sus pedidos asignados.
 *      · Los pedidos libres (sin carpintero y en PENDIENTE).
 *  - El carpintero puede tomar pedidos libres y cambiar el estado.
 */
interface PedidoRepository {

    // CLIENTE

    // Flujo reactivo de los pedidos de UN cliente.
    fun getMyPedidos(clientUid: String): Flow<List<Pedido>>

    //Lee un pedido específico por ID. Devuelve null si no existe.
    suspend fun getPedidoById(id: String): Result<Pedido?>

    // Crea un nuevo pedido. Se crea con estado PENDIENTE por defecto.
    suspend fun createPedido(order: Pedido): Result<Pedido>

    // Actualiza un pedido existente. Solo si está en PENDIENTE.
    suspend fun updatePedido(order: Pedido): Result<Pedido>

    // Cambia el estado de un pedido.
    suspend fun updatePedidoStatus(
        pedidoId: String,
        nuevoEstado: EstadoPedido
    ): Result<Unit>

    // Elimina un pedido por ID. Solo el dueño puede eliminarlo.
    suspend fun deletePedido(id: String): Result<Unit>

    // CARPINTERO

    // Flujo reactivo de los pedidos asignados a UN carpintero.
    fun getPedidosAsignadosAMi(carpenterUid: String): Flow<List<Pedido>>

    // Flujo reactivo de los pedidos libres (sin carpintero y en PENDIENTE).
    fun getPedidosLibres(): Flow<List<Pedido>>

    // Asigna un carpintero a un pedido libre.
    suspend fun asignarCarpintero(
        pedidoId: String,
        carpenterUid: String,
        carpinteroNombre: String
    ): Result<Unit>

    // UTILIDADES

    // Obtiene el siguiente número secuencial global (para el ID del pedido).
    suspend fun getNextNumeroSecuencial(): Result<Long>
}