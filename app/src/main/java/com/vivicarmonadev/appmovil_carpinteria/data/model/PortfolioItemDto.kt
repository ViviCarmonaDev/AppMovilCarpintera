package com.vivicarmonadev.appmovil_carpinteria.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.domain.model.TipoPrecio
import java.util.Date

/**
 * PortfolioItemDto: el "formato Firestore" de un trabajo del portafolio.
 *
 * Se guarda en la colección `portfolio_items` usando un ID autogenerado
 * por Firestore. Cada item tiene un `uid` que apunta al carpintero dueño.
 *
 * Los conversores toDomain() y toDto() traducen entre los dos modelos.
 */
data class PortfolioItemDto(
    @DocumentId
    val id: String = "",

    val uid: String = "",

    val titulo: String = "",

    val descripcion: String = "",

    val categoria: String = "",

    val material: String = "",

    val precioReferencial: Double? = null,

    val tipoPrecio: String = "A_TRATAR",

    // ---- Imágenes (hasta 3) ----
    val imagenesUrls: List<String> = emptyList(),   // 👈 NUEVO (reemplaza fotoUrl1/fotoUrl2)

    @ServerTimestamp
    val createdAt: Date? = null,

    @ServerTimestamp
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
        tipoPrecio = TipoPrecio.fromString(tipoPrecio),
        imagenesUrls = imagenesUrls,                 // 👈 NUEVO
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
        tipoPrecio = tipoPrecio.toFirestoreValue(),
        imagenesUrls = imagenesUrls,                 // 👈 NUEVO
        createdAt = if (createdAt > 0) Date(createdAt) else null,
        updatedAt = if (updatedAt > 0) Date(updatedAt) else null
    )
}