package com.vivicarmonadev.appmovil_carpinteria.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpenterProfile
import java.util.Date

/**
 * CarpenterProfileDto: el "formato Firestore" del perfil del carpintero.
 *
 * Se guarda en la colección `carpenter_profiles` usando el mismo uid
 * del usuario como ID del documento.
 *
 * Existe aparte del CarpenterProfile (domain) para que el modelo
 * de negocio quede limpio y no dependa de Firebase.
 * Los conversores toDomain() y toDto() traducen entre los dos.
 */
data class CarpenterProfileDto(
    @DocumentId
    val uid: String = "",

    @PropertyName("nombre_taller")
    val nombreTaller: String = "",

    @PropertyName("ruc")
    val ruc: String = "",

    @PropertyName("descripcion")
    val descripcion: String = "",

    @PropertyName("direccion_taller")
    val direccionTaller: String = "",

    @PropertyName("telefono_taller")
    val telefonoTaller: String = "",

    @PropertyName("anios_experiencia")
    val aniosExperiencia: Int = 0,

    @PropertyName("skills")
    val skills: List<String> = emptyList(),

    @PropertyName("foto_taller_url")
    val fotoTallerUrl: String? = null,

    @PropertyName("foto_maestro_url")
    val fotoMaestroUrl: String? = null,

    @PropertyName("rating")
    val rating: Float = 0f,

    @PropertyName("total_reviews")
    val totalReviews: Int = 0,

    @PropertyName("profile_completed")
    val profileCompleted: Boolean = false,

    @ServerTimestamp
    @PropertyName("created_at")
    val createdAt: Date? = null,

    @ServerTimestamp
    @PropertyName("updated_at")
    val updatedAt: Date? = null
)

// CONVERSORES

fun CarpenterProfileDto.toDomain(): CarpenterProfile {
    return CarpenterProfile(
        uid = uid,
        nombreTaller = nombreTaller,
        ruc = ruc,
        descripcion = descripcion,
        direccionTaller = direccionTaller,
        telefonoTaller = telefonoTaller,
        aniosExperiencia = aniosExperiencia,
        skills = skills,
        fotoTallerUrl = fotoTallerUrl,
        fotoMaestroUrl = fotoMaestroUrl,
        rating = rating,
        totalReviews = totalReviews,
        profileCompleted = profileCompleted,
        createdAt = createdAt?.time ?: 0L,
        updatedAt = updatedAt?.time ?: 0L
    )
}

fun CarpenterProfile.toDto(): CarpenterProfileDto {
    return CarpenterProfileDto(
        uid = uid,
        nombreTaller = nombreTaller,
        ruc = ruc,
        descripcion = descripcion,
        direccionTaller = direccionTaller,
        telefonoTaller = telefonoTaller,
        aniosExperiencia = aniosExperiencia,
        skills = skills,
        fotoTallerUrl = fotoTallerUrl,
        fotoMaestroUrl = fotoMaestroUrl,
        rating = rating,
        totalReviews = totalReviews,
        profileCompleted = profileCompleted,
        createdAt = if (createdAt > 0) Date(createdAt) else null,
        updatedAt = if (updatedAt > 0) Date(updatedAt) else null
    )
}