package com.vivicarmonadev.appmovil_carpinteria.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CategoriaMaterial
import java.util.Date

data class CategoriaMaterialDto(
    @DocumentId val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val imagenUrl: String? = null,
    val creadoPor: String = "",
    @ServerTimestamp val createdAt: Date? = null,
    @ServerTimestamp val updatedAt: Date? = null
)

fun CategoriaMaterialDto.toDomain(): CategoriaMaterial {
    return CategoriaMaterial(
        id = id,
        nombre = nombre,
        descripcion = descripcion,
        imagenUrl = imagenUrl,
        creadoPor = creadoPor,
        createdAt = createdAt?.time ?: 0L,
        updatedAt = updatedAt?.time ?: 0L
    )
}

fun CategoriaMaterial.toDto(): CategoriaMaterialDto {
    return CategoriaMaterialDto(
        id = id,
        nombre = nombre,
        descripcion = descripcion,
        imagenUrl = imagenUrl,
        creadoPor = creadoPor,
        createdAt = if (createdAt > 0) Date(createdAt) else null,
        updatedAt = if (updatedAt > 0) Date(updatedAt) else null
    )
}