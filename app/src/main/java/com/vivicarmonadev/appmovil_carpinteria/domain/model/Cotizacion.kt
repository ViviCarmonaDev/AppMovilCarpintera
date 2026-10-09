package com.vivicarmonadev.appmovil_carpinteria.domain.model

/**
 * Modelo de dominio de una cotización. La crea el carpintero sobre un pedido del cliente.
 */
data class Cotizacion(
    val id: String = "",
    val pedidoId: String = "",
    val numeroPedido: String = "",
    val clienteUid: String = "",
    val clienteNombre: String = "",
    val carpinteroUid: String = "",
    val carpinteroNombre: String = "",

    // ---- Datos de la cotización ----
    val materiales: List<ItemCotizacion> = emptyList(),
    val manoDeObra: Double = 0.0,
    val costoTotal: Double = 0.0,
    val tiempoEstimadoDias: Int = 0,
    val comentarios: String = "",

    // ---- Estado ----
    val estado: EstadoCotizacion = EstadoCotizacion.PENDIENTE,

    // ---- Metadata ----
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    // Total calculado (materiales + mano de obra)
    val totalMateriales: Double
        get() = materiales.sumOf { it.precio * it.cantidad }

    val costoTotalTexto: String
        get() = "S/ ${"%.2f".format(costoTotal)}"

    val tiempoEstimadoTexto: String
        get() = when (tiempoEstimadoDias) {
            0 -> "Sin definir"
            1 -> "1 día"
            else -> "$tiempoEstimadoDias días"
        }

    val puedeEditarse: Boolean
        get() = estado == EstadoCotizacion.PENDIENTE

    val puedeEliminarse: Boolean
        get() = estado == EstadoCotizacion.PENDIENTE
}

/**
 * Un item de material dentro de una cotización.
 */
data class ItemCotizacion(
    val nombre: String = "",
    val cantidad: Int = 1,
    val precio: Double = 0.0,
    val unidad: String = "unidad"
)