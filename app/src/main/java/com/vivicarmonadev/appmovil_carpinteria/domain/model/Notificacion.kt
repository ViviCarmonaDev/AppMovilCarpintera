package com.vivicarmonadev.appmovil_carpinteria.domain.model

data class Notificacion(
    val id: String = "",
    val destinatarioUid: String = "",
    val titulo: String = "",
    val mensaje: String = "",
    val tipo: String = "", // "nuevo_pedido", "nueva_cotizacion", "respuesta_cotizacion", "estado_pedido"
    val referenciaId: String = "", // ID del pedido o cotización
    val leida: Boolean = false,
    val createdAt: Long = 0L
)