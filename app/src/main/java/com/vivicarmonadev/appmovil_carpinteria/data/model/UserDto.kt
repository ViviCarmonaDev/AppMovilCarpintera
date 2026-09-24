package com.vivicarmonadev.appmovil_carpinteria.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.vivicarmonadev.appmovil_carpinteria.domain.model.User
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import java.util.Date

/**
 * UserDto: el "formato Firestore" del usuario.
 * Este es el molde que usa la app para guardar y leer usuarios
 * en la base de datos de Firebase
 *
 * Firestore necesita nombres de campos en snake_case (created_at)
 * y anotaciones especiales (@DocumentId, @ServerTimestamp) que no queremos
 * mezclar con la lógica del negocio. El User queda limpio, este se encarga
 * de lo técnico.
 *
 * Los conversores toDomain() y toDto() traducen entre los dos.
 */
data class UserDto(
    @DocumentId
    val uid: String = "",

    @PropertyName("nombres")
    val nombres: String = "",

    @PropertyName("apellidos")
    val apellidos: String = "",

    @PropertyName("telefono")
    val telefono: String = "",

    @PropertyName("direccion")
    val direccion: String = "",

    @PropertyName("email")
    val email: String = "",

    @PropertyName("role")
    val role: String = "client",   // "client" o "carpenter"

    @PropertyName("photo_url")
    val photoUrl: String? = null,

    @PropertyName("photo_url")
    val profileCompleted: Boolean = false,

    @ServerTimestamp
    @PropertyName("created_at")
    val createdAt: Date? = null,

    @ServerTimestamp
    @PropertyName("updated_at")
    val updatedAt: Date? = null
)

// CONVERSORES

//Convierte de DTO (Firestore) a modelo de dominio.

fun UserDto.toDomain(): User {
    return User(
        uid = uid,
        nombres = nombres,
        apellidos = apellidos,
        telefono = telefono,
        direccion = direccion,
        email = email,
        role = UserRole.fromString(role),
        photoUrl = photoUrl,
        profileCompleted = profileCompleted,
        createdAt = createdAt?.time ?: 0L,
        updatedAt = updatedAt?.time ?: 0L
    )
}

/**
 * Convierte de modelo de dominio a DTO (Firestore).
 */
fun User.toDto(): UserDto {
    return UserDto(
        uid = uid,
        nombres = nombres,
        apellidos = apellidos,
        telefono = telefono,
        direccion = direccion,
        email = email,
        role = role.toFirestoreValue(),
        photoUrl = photoUrl,
        profileCompleted = profileCompleted,
        createdAt = if (createdAt > 0) Date(createdAt) else null,
        updatedAt = if (updatedAt > 0) Date(updatedAt) else null
    )
}