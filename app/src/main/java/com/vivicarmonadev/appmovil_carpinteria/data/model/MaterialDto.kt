package com.vivicarmonadev.appmovil_carpinteria.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Material
import java.util.Date

data class MaterialDto(
    @DocumentId val id: String = "",
    val categoriaId: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precioReferencial: Double = 0.0,
    val imagenUrl: String? = null,
    val creadoPor: String = "",
    @ServerTimestamp val createdAt: Date? = null,
    @ServerTimestamp val updatedAt: Date? = null
)

fun MaterialDto.toDomain(): Material {
    return Material(
        id = id,
        categoriaId = categoriaId,
        nombre = nombre,
        descripcion = descripcion,
        precioReferencial = precioReferencial,
        imagenUrl = imagenUrl,
        creadoPor = creadoPor,
        createdAt = createdAt?.time ?: 0L,
        updatedAt = updatedAt?.time ?: 0L
    )
}

fun Material.toDto(): MaterialDto {
    return MaterialDto(
        id = id,
        categoriaId = categoriaId,
        nombre = nombre,
        descripcion = descripcion,
        precioReferencial = precioReferencial,
        imagenUrl = imagenUrl,
        creadoPor = creadoPor,
        createdAt = if (createdAt > 0) Date(createdAt) else null,
        updatedAt = if (updatedAt > 0) Date(updatedAt) else null
    )
}