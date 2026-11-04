package com.vivicarmonadev.appmovil_carpinteria.domain.model

data class Material(
    val id: String = "",
    val categoriaId: String = "",  // ID de la categoría a la que pertenece
    val nombre: String = "",
    val descripcion: String = "",
    val precioReferencial: Double = 0.0,
    val imagenUrl: String? = null,
    val creadoPor: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    val precioTexto: String
        get() = if (precioReferencial > 0) "S/ ${"%.2f".format(precioReferencial)}" else "A tratar"
}