package com.vivicarmonadev.appmovil_carpinteria.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Notificacion
import java.util.Date

data class NotificacionDto(
    @DocumentId val id: String = "",
    val destinatarioUid: String = "",
    val titulo: String = "",
    val mensaje: String = "",
    val tipo: String = "",
    val referenciaId: String = "",
    val leida: Boolean = false,
    @ServerTimestamp val createdAt: Date? = null
)

fun NotificacionDto.toDomain(): Notificacion {
    return Notificacion(
        id = id,
        destinatarioUid = destinatarioUid,
        titulo = titulo,
        mensaje = mensaje,
        tipo = tipo,
        referenciaId = referenciaId,
        leida = leida,
        createdAt = createdAt?.time ?: 0L
    )
}

fun Notificacion.toDto(): NotificacionDto {
    return NotificacionDto(
        id = id,
        destinatarioUid = destinatarioUid,
        titulo = titulo,
        mensaje = mensaje,
        tipo = tipo,
        referenciaId = referenciaId,
        leida = leida,
        createdAt = if (createdAt > 0) Date(createdAt) else null
    )
}