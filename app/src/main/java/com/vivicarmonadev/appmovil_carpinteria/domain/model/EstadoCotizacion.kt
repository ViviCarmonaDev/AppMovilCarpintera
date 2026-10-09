package com.vivicarmonadev.appmovil_carpinteria.domain.model

enum class EstadoCotizacion {
    PENDIENTE,      // Enviada al cliente, esperando respuesta
    ACEPTADA,       // Cliente aceptó
    RECHAZADA,      // Cliente rechazó
    ANULADA;        // El carpintero la anuló

    fun toDisplayText(): String = when (this) {
        PENDIENTE -> "Pendiente"
        ACEPTADA -> "Aceptada"
        RECHAZADA -> "Rechazada"
        ANULADA -> "Anulada"
    }

    fun toFirestoreValue(): String = name

    companion object {
        fun fromString(value: String?): EstadoCotizacion {
            return when (value?.uppercase()) {
                "ACEPTADA" -> ACEPTADA
                "RECHAZADA" -> RECHAZADA
                "ANULADA" -> ANULADA
                else -> PENDIENTE
            }
        }
    }
}