package com.vivicarmonadev.appmovil_carpinteria.ui.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Carrusel horizontal reutilizable para mostrar hasta maxItems cards.

 * Se usa en las secciones tipo "recientes", "destacados", "activos" del home
 * (tanto cliente como carpintero) y en cualquier lista horizontal de la app.

 * Ejemplo:
 * MervetaHorizontalList(
 *     items = portfolioItems.take(3),
 *     key = { it.id }
 * ) { item ->
 *     PortfolioMiniCard(item = item, onClick = { ... })
 * }
 * ```
 */
@Composable
fun <T> MervetaHorizontalList(
    items: List<T>,
    modifier: Modifier = Modifier,
    maxItems: Int = 3,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp),
    horizontalSpacing: Int = 12,
    key: ((T) -> Any)? = null,
    content: @Composable (T) -> Unit
) {
    if (items.isEmpty()) return

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing.dp)
    ) {
        val itemsToShow = items.take(maxItems)

        if (key != null) {
            items(itemsToShow, key = key) { item ->
                content(item)
            }
        } else {
            items(itemsToShow) { item ->
                content(item)
            }
        }
    }
}