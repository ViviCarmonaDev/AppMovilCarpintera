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

    @ServerTimestamp
    val createdAt: Date? = null,

    @ServerTimestamp
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