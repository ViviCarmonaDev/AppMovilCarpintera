package com.vivicarmonadev.appmovil_carpinteria.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Review
import java.util.Date

/**
 * ReviewDto: formato Firestore de una reseña.
 * Se guarda en la colección `reviews` con ID autogenerado.
 */
data class ReviewDto(
    @DocumentId
    val id: String = "",

    val carpinteroUid: String = "",
    val clienteUid: String = "",
    val clienteNombre: String = "",
    val clienteFotoUrl: String? = null,
    val rating: Int = 5,
    val comentario: String = "",

    @ServerTimestamp
    val createdAt: Date? = null,

    @ServerTimestamp
    val updatedAt: Date? = null
)

// CONVERSORES

fun ReviewDto.toDomain(): Review {
    return Review(
        id = id,
        carpinteroUid = carpinteroUid,
        clienteUid = clienteUid,
        clienteNombre = clienteNombre,
        clienteFotoUrl = clienteFotoUrl,
        rating = rating,
        comentario = comentario,
        createdAt = createdAt?.time ?: 0L,
        updatedAt = updatedAt?.time ?: 0L
    )
}

fun Review.toDto(): ReviewDto {
    return ReviewDto(
        id = id,
        carpinteroUid = carpinteroUid,
        clienteUid = clienteUid,
        clienteNombre = clienteNombre,
        clienteFotoUrl = clienteFotoUrl,
        rating = rating,
        comentario = comentario,
        createdAt = if (createdAt > 0) Date(createdAt) else null,
        updatedAt = if (updatedAt > 0) Date(updatedAt) else null
    )
}