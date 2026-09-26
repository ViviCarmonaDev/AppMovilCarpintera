package com.vivicarmonadev.appmovil_carpinteria.ui.projects

import androidx.compose.runtime.Composable
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem

/**
 * ProjectsScreen es un envoltorio que delega a PortfolioScreen.
 *
 * Existe para mantener el nombre "Projects" en la ruta del bottom nav,
 * pero la implementación real vive en PortfolioScreen.
 */
@Composable
fun ProjectsScreen(
    viewModel: PortfolioViewModel,
    onCreateClick: () -> Unit,
    onEditClick: (PortfolioItem) -> Unit
) {
    PortfolioScreen(
        viewModel = viewModel,
        onCreateClick = onCreateClick,
        onEditClick = onEditClick
    )
}