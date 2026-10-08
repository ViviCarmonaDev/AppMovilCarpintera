package com.vivicarmonadev.appmovil_carpinteria.ui.common.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Convierte un timestamp a un texto relativo.
 * Ej: "Hace 2 días", "Hace 5 min", "Ahora"
 */
fun tiempoRelativo(timestamp: Long): String {
    if (timestamp == 0L) return ""

    val ahora = System.currentTimeMillis()
    val diferencia = ahora - timestamp

    val minutos = diferencia / (1000 * 60)
    val horas = diferencia / (1000 * 60 * 60)
    val dias = diferencia / (1000 * 60 * 60 * 24)

    return when {
        minutos < 1 -> "Ahora"
        minutos < 60 -> "Hace ${minutos}m"
        horas < 24 -> "Hace ${horas}h"
        dias == 1L -> "Ayer"
        dias < 7 -> "Hace $dias días"
        dias < 30 -> "Hace ${dias / 7} semanas"
        else -> fechaCorta(timestamp)
    }
}

/**
 * Formatea una fecha a "15 Nov".
 */
fun fechaCorta(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM", Locale("es", "PE"))
    return sdf.format(Date(timestamp))
}

/**
 * Formatea una fecha a "15 de noviembre de 2025".
 */
fun fechaLarga(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "PE"))
    return sdf.format(Date(timestamp))
}