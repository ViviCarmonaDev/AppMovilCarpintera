package com.vivicarmonadev.appmovil_carpinteria.domain.model

/**
 * Modelo de dominio de una reseña.
 * No depende de Firebase ni de Compose.
 */
data class Review(
    val id: String = "",
    val carpinteroUid: String = "",      // A quién se le hace la reseña
    val clienteUid: String = "",         // Quién hizo la reseña
    val clienteNombre: String = "",      // Nombre cacheado del cliente
    val clienteFotoUrl: String? = null,  // Foto cacheada del cliente
    val rating: Int = 5,                 // 1 a 5 estrellas
    val comentario: String = "",
    val createdAt: Long = 0L,            // Timestamp
    val updatedAt: Long = 0L
)
