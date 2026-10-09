package com.vivicarmonadev.appmovil_carpinteria.domain.repository

import com.vivicarmonadev.appmovil_carpinteria.domain.model.Cotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoCotizacion
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de cotizaciones.
 * Define QUÉ se puede hacer con las cotizaciones, pero no CÓMO.
 */
interface CotizacionRepository {

    /**
     * Flujo reactivo de las cotizaciones de UN carpintero.
     */
    fun getCotizacionesByCarpenter(carpenterUid: String): Flow<List<Cotizacion>>

    /**
     * Flujo reactivo de las cotizaciones asociadas a UN pedido.
     */
    fun getCotizacionesByPedido(pedidoId: String): Flow<List<Cotizacion>>

    /**
     * Lee una cotización por su ID.
     */
    suspend fun getCotizacionById(id: String): Result<Cotizacion?>

    /**
     * Crea una nueva cotización.
     */
    suspend fun createCotizacion(cotizacion: Cotizacion): Result<Cotizacion>

    /**
     * Actualiza una cotización existente.
     */
    suspend fun updateCotizacion(cotizacion: Cotizacion): Result<Cotizacion>

    // Cambia el estado de una cotización.

    suspend fun updateEstado(
        cotizacionId: String,
        nuevoEstado: EstadoCotizacion
    ): Result<Unit>

    // Elimina una cotización por su ID.

    suspend fun deleteCotizacion(id: String): Result<Unit>

    // Acepta una cotización.
    // Al hacerlo, el cliente elige esa cotización y se asigna al carpintero.

    suspend fun aceptarCotizacion(cotizacionId: String): Result<Unit>

    // Rechaza una cotización.

    suspend fun rechazarCotizacion(cotizacionId: String): Result<Unit>
}