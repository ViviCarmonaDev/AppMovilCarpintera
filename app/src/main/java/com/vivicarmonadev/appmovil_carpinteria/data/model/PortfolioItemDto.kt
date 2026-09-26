package com.vivicarmonadev.appmovil_carpinteria.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import java.util.Date

/**
 * PortfolioItemDto: el "formato Firestore" de un trabajo del portafolio.
 *
 * Se guarda en la colección `portfolio_items` usando un ID autogenerado
 * por Firestore.
 *
 * Cada item tiene un `uid` que apunta al carpintero dueño. Esto permite:
 *  - Filtrar por carpintero (ver solo sus trabajos)
 *  - Aplicar reglas de seguridad (solo el dueño puede editar/borrar)
 *
 * Los conversores toDomain() y toDto() traducen entre los dos modelos.
 */
data class PortfolioItemDto(
    @DocumentId
    val id: String = "",

    @PropertyName("uid")
    val uid: String = "",

    @PropertyName("titulo")
    val titulo: String = "",

    @PropertyName("descripcion")
    val descripcion: String = "",

    @PropertyName("categoria")
    val categoria: String = "",

    @PropertyName("material")
    val material: String = "",

    @PropertyName("precio_referencial")
    val precioReferencial: Double? = null,

    @PropertyName("foto_url_1")
    val fotoUrl1: String? = null,

    @PropertyName("foto_url_2")
    val fotoUrl2: String? = null,

    @ServerTimestamp
    @PropertyName("created_at")
    val createdAt: Date? = null,

    @ServerTimestamp
    @PropertyName("updated_at")
    val updatedAt: Date? = null
)

// CONVERSORES

fun PortfolioItemDto.toDomain(): PortfolioItem {
    return PortfolioItem(
        id = id,
        uid = uid,
        titulo = titulo,
        descripcion = descripcion,
        categoria = categoria,
        material = material,
        precioReferencial = precioReferencial,
        fotoUrl1 = fotoUrl1,
        fotoUrl2 = fotoUrl2,
        createdAt = createdAt?.time ?: 0L,
        updatedAt = updatedAt?.time ?: 0L
    )
}

fun PortfolioItem.toDto(): PortfolioItemDto {
    return PortfolioItemDto(
        id = id,
        uid = uid,
        titulo = titulo,
        descripcion = descripcion,
        categoria = categoria,
        material = material,
        precioReferencial = precioReferencial,
        fotoUrl1 = fotoUrl1,
        fotoUrl2 = fotoUrl2,
        createdAt = if (createdAt > 0) Date(createdAt) else null,
        updatedAt = if (updatedAt > 0) Date(updatedAt) else null
    )
}