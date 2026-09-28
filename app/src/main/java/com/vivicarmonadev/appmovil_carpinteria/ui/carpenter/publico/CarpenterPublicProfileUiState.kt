package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.publico

import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpenterProfile
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.domain.model.User

/**
 * Estado de la UI del perfil público del carpintero.
 *
 * Es la pantalla que ve un CLIENTE cuando toca un carpintero
 * en el Home o en el catálogo. Muestra:
 *  - Datos del taller (nombre, descripción, ubicación)
 *  - Skills del carpintero
 *  - Trabajos publicados
 *  - Botón para contactar por WhatsApp
 */
data class CarpenterPublicProfileUiState(
    // ---- Datos del carpintero ----
    val user: User? = null,
    val profile: CarpenterProfile? = null,
    val portfolioItems: List<PortfolioItem> = emptyList(),

    // ---- Estado ----
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {

    // ¿Cargó todo correctamente?

    val isLoaded: Boolean
        get() = !isLoading && user != null && profile != null

    // Iniciales para el avatar (ej: "MR" de "Taller Merveta").

    val initials: String
        get() {
            val nombre = profile?.nombreTaller ?: user?.fullName ?: ""
            if (nombre.isBlank()) return "?"

            val palabras = nombre.trim().split(" ").filter { it.isNotBlank() }
            return when {
                palabras.size >= 2 -> "${palabras[0].first().uppercaseChar()}${palabras[1].first().uppercaseChar()}"
                palabras.size == 1 -> palabras[0].take(2).uppercase()
                else -> "?"
            }
        }

    // ¿Tiene trabajos publicados?

    val hasPortfolio: Boolean
        get() = portfolioItems.isNotEmpty()

    // ¿Tiene skills?

    val hasSkills: Boolean
        get() = (profile?.skills?.size ?: 0) > 0

    // Texto de experiencia para mostrar.

    val experienciaTexto: String
        get() {
            val anios = profile?.aniosExperiencia ?: 0
            return when {
                anios == 0 -> "Sin experiencia"
                anios == 1 -> "1 año de experiencia"
                else -> "$anios años de experiencia"
            }
        }

    // Rating formateado (por ahora siempre 0.0).

    val ratingTexto: String
        get() = "%.1f".format(profile?.rating ?: 0f)
}