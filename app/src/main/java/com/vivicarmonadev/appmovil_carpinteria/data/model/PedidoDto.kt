package com.vivicarmonadev.appmovil_carpinteria.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoPedido
import java.util.Date

/**
 * PedidoDto: el "formato Firestore" de un pedido.
 *
 * Se guarda en la colección `orders` usando un ID autogenerado por Firestore.
 * Los conversores toDomain() y toDto() traducen entre los dos modelos.
 */
data class PedidoDto(
    @DocumentId
    val id: String = "",

    // ---- Número de pedido ----
    val numeroSecuencial: Long = 0L,              // contador global

    // ---- Cliente ----
    val clientUid: String = "",
    val clienteNombre: String = "",

    // ---- Carpintero ----
    val carpenterUid: String? = null,             // null = libre
    val carpinteroNombre: String? = null,

    // ---- Datos del pedido ----
    val titulo: String = "",
    val categoria: String = "",
    val tipoMadera: String = "",

    // ---- Medidas ----
    val anchoCm: Double? = null,
    val altoCm: Double? = null,
    val profundidadCm: Double? = null,

    // ---- Detalles ----
    val descripcion: String = "",
    val fechaEstimada: Date? = null,
    val presupuestoMax: Double? = null,

    // ---- Imágenes de referencia ----
    val imagenesUrls: List<String> = emptyList(), // hasta 3

    // ---- Estado ----
    val status: String = "PENDIENTE",

    // ---- Timestamps ----
    @ServerTimestamp
    val createdAt: Date? = null,

    @ServerTimestamp
    val updatedAt: Date? = null
)

// CONVERSORES

fun PedidoDto.toDomain(): Pedido {
    return Pedido(
        id = id,
        numeroSecuencial = numeroSecuencial,
        clientUid = clientUid,
        clienteNombre = clienteNombre,
        titulo = titulo,
        categoria = categoria,
        tipoMadera = tipoMadera,
        anchoCm = anchoCm,
        altoCm = altoCm,
        profundidadCm = profundidadCm,
        descripcion = descripcion,
        fechaEstimada = fechaEstimada?.time,
        presupuestoMax = presupuestoMax,
        status = EstadoPedido.fromString(status),
        carpenterUid = carpenterUid,
        carpinteroNombre = carpinteroNombre,
        imagenesUrls = imagenesUrls,
        createdAt = createdAt?.time ?: 0L,
        updatedAt = updatedAt?.time ?: 0L
    )
}

fun Pedido.toDto(): PedidoDto {
    return PedidoDto(
        id = id,
        numeroSecuencial = numeroSecuencial,
        clientUid = clientUid,
        clienteNombre = clienteNombre,
        titulo = titulo,
        categoria = categoria,
        tipoMadera = tipoMadera,
        anchoCm = anchoCm,
        altoCm = altoCm,
        profundidadCm = profundidadCm,
        descripcion = descripcion,
        fechaEstimada = fechaEstimada?.let { Date(it) },
        presupuestoMax = presupuestoMax,
        status = status.toFirestoreValue(),
        carpenterUid = carpenterUid,
        carpinteroNombre = carpinteroNombre,
        imagenesUrls = imagenesUrls,
        createdAt = if (createdAt > 0) Date(createdAt) else null,
        updatedAt = if (updatedAt > 0) Date(updatedAt) else null
    )
}