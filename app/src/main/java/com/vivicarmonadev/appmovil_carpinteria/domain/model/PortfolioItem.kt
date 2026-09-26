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
    val uid: String = "",                       // uid del carpintero dueño
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val material: String = "",
    val precioReferencial: Double? = null,      // opcional
    val fotoUrl1: String? = null,               // ← se activa después
    val fotoUrl2: String? = null,               // ← se activa después
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {

     // Precio formateado para mostrar en UI. Ej: "S/ 250" o "A convenir" si es null.

    val precioTexto: String
        get() = if (precioReferencial != null) {
            "S/ ${"%.0f".format(precioReferencial)}"
        } else {
            "A convenir"
        }

    // ¿Tiene al menos una foto?

    val tieneFotos: Boolean
        get() = !fotoUrl1.isNullOrBlank() || !fotoUrl2.isNullOrBlank()
}