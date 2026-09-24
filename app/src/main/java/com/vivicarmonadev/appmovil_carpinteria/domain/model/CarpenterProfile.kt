package com.vivicarmonadev.appmovil_carpinteria.domain.model

/**
 * Modelo de dominio del perfil profesional del carpintero.
 *
 * Vive en una colección separada de `users` porque solo aplica a
 * usuarios con rol CARPENTER. Así el modelo `User` queda simple y
 * liviano para todos los usuarios.
 *
 * Este modelo NO depende de Firebase.
 */

data class CarpenterProfile(
    val uid: String = "",
    val nombreTaller: String = "",
    val ruc: String = "",
    val descripcion: String = "",
    val direccionTaller: String = "",
    val telefonoTaller: String = "",
    val aniosExperiencia: Int = 0,
    val skills: List<String> = emptyList(),
    val fotoTallerUrl: String? = null,
    val fotoMaestroUrl: String? = null,
    val rating: Float = 0f,
    val totalReviews: Int = 0,
    val profileCompleted: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {

    // Lista de skills formateada para mostrar en UI.
    val skillsCount: Int
        get() = skills.size

    // Cantidad de años como texto.

    val experienciaTexto: String
        get() = when {
            aniosExperiencia == 0 -> "Sin experiencia"
            aniosExperiencia == 1 -> "1 año"
            else -> "$aniosExperiencia años"
        }
}
