package com.vivicarmonadev.appmovil_carpinteria.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Cotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoCotizacion
import com.vivicarmonadev.appmovil_carpinteria.domain.model.ItemCotizacion
import java.util.Date
import com.vivicarmonadev.appmovil_carpinteria.domain.model.EstadoSolicitud

data class CotizacionDto(
    @DocumentId val id: String = "",
    val pedidoId: String = "",
    val numeroPedido: String = "",
    val clienteUid: String = "",
    val clienteNombre: String = "",
    val carpinteroUid: String = "",
    val carpinteroNombre: String = "",
    val materiales: List<ItemCotizacionDto> = emptyList(),
    val manoDeObra: Double = 0.0,
    val costoTotal: Double = 0.0,
    val tiempoEstimadoDias: Int = 0,
    val comentarios: String = "",
    val estado: String = "PENDIENTE",

    val observacionCliente: String? = null,       // Lo que escribe el cliente
    val solicitudCambio: Boolean = false,         // Si pidió cambios
    val respuestaCarpintero: String? = null,
    val estadoSolicitud: String = "NINGUNA",

    @ServerTimestamp val createdAt: Date? = null,
    @ServerTimestamp val updatedAt: Date? = null
)
data class ItemCotizacionDto(
    val nombre: String = "",
    val cantidad: Int = 1,
    val precio: Double = 0.0,
    val unidad: String = "unidad"
)

fun CotizacionDto.toDomain(): Cotizacion {
    return Cotizacion(
        id = id,
        pedidoId = pedidoId,
        numeroPedido = numeroPedido,
        clienteUid = clienteUid,
        clienteNombre = clienteNombre,
        carpinteroUid = carpinteroUid,
        carpinteroNombre = carpinteroNombre,
        materiales = materiales.map {
            ItemCotizacion(
                nombre = it.nombre,
                cantidad = it.cantidad,
                precio = it.precio,
                unidad = it.unidad
            )
        },
        manoDeObra = manoDeObra,
        costoTotal = costoTotal,
        tiempoEstimadoDias = tiempoEstimadoDias,
        comentarios = comentarios,
        estado = EstadoCotizacion.fromString(estado),
        createdAt = createdAt?.time ?: 0L,
        updatedAt = updatedAt?.time ?: 0L,
        observacionCliente = observacionCliente,
        solicitudCambio = solicitudCambio,
        respuestaCarpintero = respuestaCarpintero,
        estadoSolicitud = EstadoSolicitud.valueOf(estadoSolicitud)
    )
}

fun Cotizacion.toDto(): CotizacionDto {
    return CotizacionDto(
        id = id,
        pedidoId = pedidoId,
        numeroPedido = numeroPedido,
        clienteUid = clienteUid,
        clienteNombre = clienteNombre,
        carpinteroUid = carpinteroUid,
        carpinteroNombre = carpinteroNombre,
        materiales = materiales.map {
            ItemCotizacionDto(
                nombre = it.nombre,
                cantidad = it.cantidad,
                precio = it.precio,
                unidad = it.unidad
            )
        },
        manoDeObra = manoDeObra,
        costoTotal = costoTotal,
        tiempoEstimadoDias = tiempoEstimadoDias,
        comentarios = comentarios,
        estado = estado.toFirestoreValue(),
        createdAt = if (createdAt > 0) Date(createdAt) else null,
        updatedAt = if (updatedAt > 0) Date(updatedAt) else null,
        observacionCliente = observacionCliente,
        solicitudCambio = solicitudCambio,
        respuestaCarpintero = respuestaCarpintero,
        estadoSolicitud = estadoSolicitud.name
    )
}