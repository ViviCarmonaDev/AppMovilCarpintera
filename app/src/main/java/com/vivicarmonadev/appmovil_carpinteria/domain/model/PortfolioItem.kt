package com.vivicarmonadev.appmovil_carpinteria.domain.model

/**
 * Modelo de dominio de un trabajo del portafolio del carpintero.
 *
 * Representa un trabajo realizado que el carpintero publica para
 * mostrar su experiencia y atraer clientes.
 *
 * Este modelo NO depende de Firebase.
 */

data class PortfolioItem(
    val id: String = "",
    val uid: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val material: String = "",
    val precioReferencial: Double? = null,
    val tipoPrecio: TipoPrecio = TipoPrecio.A_TRATAR,
    val fotoUrl1: String? = null,
    val fotoUrl2: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    // Precio formateado para mostrar en UI (ej: "S/ 250" o "A tratar")
    val precioTexto: String
        get() = when {
            precioReferencial == null -> "A tratar"
            else -> "S/ ${"%.0f".format(precioReferencial)}"
        }

    // Etiqueta al lado del precio. Null si no corresponde mostrar nada.
    val etiquetaPrecio: String?
        get() = when (tipoPrecio) {
            TipoPrecio.FIJO -> "FIJO"
            TipoPrecio.A_TRATAR -> if (precioReferencial != null) "A TRATAR" else null
        }

    // ¿Tiene al menos una foto?
    val tieneFotos: Boolean
        get() = !fotoUrl1.isNullOrBlank() || !fotoUrl2.isNullOrBlank()
}

// Tipo de precio de un trabajo: FIJO o A_TRATAR
enum class TipoPrecio {
    FIJO,
    A_TRATAR;

    companion object {
        fun fromString(value: String?): TipoPrecio {
            return when (value?.uppercase()) {
                "FIJO" -> FIJO
                else -> A_TRATAR
            }
        }
    }

    fun toFirestoreValue(): String {
        return when (this) {
            FIJO -> "FIJO"
            A_TRATAR -> "A_TRATAR"
        }
    }
}