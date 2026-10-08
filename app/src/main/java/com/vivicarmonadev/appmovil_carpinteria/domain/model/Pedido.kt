package com.vivicarmonadev.appmovil_carpinteria.domain.model

/**
 * Modelo de dominio de un pedido.
 *
 * Un pedido es la solicitud que hace un cliente para que un carpintero, le fabrique un mueble u objeto de madera.

 * El cliente crea el pedido. Puede:
 *  - Asignarlo a un carpintero específico.
 *  - Dejarlo libre para que cualquier carpintero lo tome.

 * El carpintero va cambiando el estado: PENDIENTE → ACEPTADO → EN_PROCESO → TERMINADO → ENTREGADO.
 */
data class Pedido(
    val id: String = "",

    // ---- Número de pedido (contador global) ----
    val numeroSecuencial: Long = 0L,

    // ---- Cliente ----
    val clientUid: String = "",
    val clienteNombre: String = "",

    // ---- Carpintero ----
    val carpenterUid: String? = null,                    // null = libre
    val carpinteroNombre: String? = null,

    // ---- Datos del pedido ----
    val titulo: String = "",
    val categoria: String = "",
    val tipoMadera: String = "",
    val anchoCm: Double? = null,
    val altoCm: Double? = null,
    val profundidadCm: Double? = null,
    val descripcion: String = "",
    val fechaEstimada: Long? = null,
    val presupuestoMax: Double? = null,

    // ---- Imágenes de referencia (hasta 3) ----
    val imagenesUrls: List<String> = emptyList(),

    // ---- Estado ----
    val status: EstadoPedido = EstadoPedido.PENDIENTE,

    // ---- Metadata ----
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {

    // PROPIEDADES CALCULADAS

    /** ¿Está libre (sin carpintero asignado)? */
    val isLibre: Boolean
        get() = carpenterUid.isNullOrBlank()

    /** ¿Tiene imágenes de referencia? */
    val tieneImagenes: Boolean
        get() = imagenesUrls.isNotEmpty()

    /** Cantidad de imágenes (máx 3) */
    val cantidadImagenes: Int
        get() = imagenesUrls.size.coerceAtMost(3)

    /**
     * Texto de medidas formateado.
     * Ej: "120 x 75 x 80 cm"
     */
    val medidasTexto: String?
        get() {
            val partes = listOfNotNull(
                anchoCm?.let { "%.0f".format(it) },
                altoCm?.let { "%.0f".format(it) },
                profundidadCm?.let { "%.0f".format(it) }
            )
            return if (partes.isEmpty()) null else "${partes.joinToString(" x ")} cm"
        }

    /**
     * Presupuesto formateado.
     * Ej: "S/ 500"
     */
    val presupuestoTexto: String?
        get() = presupuestoMax?.let { "S/ ${"%.0f".format(it)}" }

    /**
     * Número de pedido formateado.
     *
     * Formato: P + inicial del cliente + número con 2 dígitos.
     * Ejemplos:
     *  - María, número 1  → "PM01"
     *  - Juan, número 2   → "PJ02"
     *  - Ana, número 10   → "PA10"
     */
    val numeroPedido: String
        get() {
            val inicial = clienteNombre
                .trim()
                .take(1)
                .uppercase()
                .ifBlank { "X" }
            return "P$inicial${numeroSecuencial.toString().padStart(2, '0')}"
        }
}

// ESTADOS DE UN PEDIDO

enum class EstadoPedido {
    PENDIENTE,      // Recién creado, esperando aceptación
    ACEPTADO,       // El carpintero lo aceptó
    EN_PROCESO,     // El carpintero está trabajando
    TERMINADO,      // El carpintero lo terminó
    ENTREGADO,      // El cliente lo recibió
    CANCELADO;      // El cliente lo canceló

    companion object {
        fun fromString(value: String?): EstadoPedido {
            return when (value?.uppercase()) {
                "ACEPTADO" -> ACEPTADO
                "EN_PROCESO" -> EN_PROCESO
                "TERMINADO" -> TERMINADO
                "ENTREGADO" -> ENTREGADO
                "CANCELADO" -> CANCELADO
                else -> PENDIENTE
            }
        }
    }

    fun toFirestoreValue(): String = name

    fun toDisplayText(): String = when (this) {
        PENDIENTE -> "Pendiente"
        ACEPTADO -> "Aceptado"
        EN_PROCESO -> "En proceso"
        TERMINADO -> "Terminado"
        ENTREGADO -> "Entregado"
        CANCELADO -> "Cancelado"
    }
}