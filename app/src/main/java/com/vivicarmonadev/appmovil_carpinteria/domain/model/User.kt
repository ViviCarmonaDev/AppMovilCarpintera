package com.vivicarmonadev.appmovil_carpinteria.domain.model

/**
 * Modelo de dominio del Usuario, Este modelo NO depende de Firebase.
 */
data class User (
    val uid: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val email: String = "",
    val role: UserRole = UserRole.CLIENT,
    val photoUrl: String? = null,
    val profileCompleted: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    val fullName: String
        get() = "$nombres $apellidos".trim()

    val initials: String
        get() = buildString {
            if (nombres.isNotEmpty()) append(nombres.first().uppercaseChar())
            if (apellidos.isNotEmpty()) append(apellidos.first().uppercaseChar())
        }
}

enum class UserRole {
    CLIENT,
    CARPENTER;

    companion object {
        fun fromString(value: String?): UserRole {
            return when (value?.lowercase()) {
                "carpenter", "carpintero" -> CARPENTER
                else -> CLIENT
            }
        }
    }

    fun toFirestoreValue(): String {
        return when (this) {
            CLIENT -> "client"
            CARPENTER -> "carpenter"
        }
    }
}