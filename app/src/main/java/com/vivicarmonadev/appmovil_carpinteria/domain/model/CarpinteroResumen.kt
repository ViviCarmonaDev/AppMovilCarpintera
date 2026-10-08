package com.vivicarmonadev.appmovil_carpinteria.domain.model

data class CarpinteroResumen(
    val uid: String,
    val nombre: String,
    val nombreTaller: String? = null
) {
    val displayName: String
        get() = nombreTaller?.takeIf { it.isNotBlank() } ?: nombre
}