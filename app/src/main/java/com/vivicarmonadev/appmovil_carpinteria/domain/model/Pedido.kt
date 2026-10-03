package com.vivicarmonadev.appmovil_carpinteria.domain.model

/**
 * Modelo de dominio de un pedido.

 * Un pedido es la solicitud que hace un cliente para que un carpintero le fabrique un mueble u objeto de madera.
 * En esta primera versión, el cliente crea el pedido y queda en estado PENDIENTE.
 **/

data class Pedido(
    val id: String = "",
    val clientUid: String = "",                          // quién lo creó

    // ---- Datos del pedido ----
    val titulo: String = "",
    val categoria: String = "",                          // Muebles, Puertas, etc.
    val tipoMadera: String = "",                         // Pino, Roble, etc.
    val anchoCm: Double? = null,                         // medidas en cm
    val altoCm: Double? = null,
    val profundidadCm: Double? = null,
    val descripcion: String = "",
    val fechaEstimada: Long? = null,                     // timestamp de la fecha deseada
    val presupuestoMax: Double? = null,                  // presupuesto máximo opcional

    // ---- Estado ----
    val status: EstadoPedido = EstadoPedido.PENDIENTE,

    // ---- Carpintero asignado
    val carpenterUid: String? = null,                    // null hasta que se asigne

    // ---- Imágenes
    val imagenReferenciaUrl: String? = null,

    // ---- Metadata ----
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {

    // Texto de medidas formateado. Ej: "120 x 75 x 80 cm", Si no hay medidas, devuelve null.

    val medidasTexto: String?
        get() {
            val partes = listOfNotNull(
                anchoCm?.let { "%.0f".format(it) },
                altoCm?.let { "%.0f".format(it) },
                profundidadCm?.let { "%.0f".format(it) }
            )
            return if (partes.isEmpty()) null else "${partes.joinToString(" x ")} cm"
        }

    // Presupuesto formateado. Ej: "S/ 500"

    val presupuestoTexto: String?
        get() = presupuestoMax?.let { "S/ ${"%.0f".format(it)}" }

    // ID público formateado para mostrar. Ej: "#MD-1042" Los primeros 6 caracteres del ID en mayúsculas.

    val numeroPedido: String
        get() = if (id.length >= 6) "#${id.take(6).uppercase()}" else "#$id"
}

// Estados posibles de un pedido.
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

    // Texto legible para mostrar en UI.

    fun toDisplayText(): String = when (this) {
        PENDIENTE -> "Pendiente"
        ACEPTADO -> "Aceptado"
        EN_PROCESO -> "En proceso"
        TERMINADO -> "Terminado"
        ENTREGADO -> "Entregado"
        CANCELADO -> "Cancelado"
    }
}