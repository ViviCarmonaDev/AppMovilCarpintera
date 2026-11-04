package com.vivicarmonadev.appmovil_carpinteria.domain.model

data class CategoriaMaterial(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val imagenUrl: String? = null,
    val creadoPor: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)