package com.vivicarmonadev.appmovil_carpinteria.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import com.vivicarmonadev.appmovil_carpinteria.ui.home.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onCarpenterClick: (String) -> Unit,
    onPortfolioItemClick: (String) -> Unit,
    onSeeAllCarpentersClick: () -> Unit,
    onSeeAllPortfolioClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFAF6F1)
    ) {
        if (uiState.currentUser == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF8B5A2B))
            }
        } else {
            val user = uiState.currentUser!!

            when (user.role) {
                UserRole.CARPENTER -> CarpenterHomeScreen(
                    userName = user.nombres.ifBlank { "Usuario" },
                    portfolioItems = uiState.myPortfolioItems,
                    onPortfolioItemClick = onPortfolioItemClick,
                    onSeeAllPortfolioClick = onSeeAllPortfolioClick
                )

                UserRole.CLIENT -> ClientHomeScreen(
                    userName = user.nombres.ifBlank { "Usuario" },
                    carpenters = uiState.carpenters,
                    recentPortfolioItems = uiState.recentPortfolioItems,
                    hasRecentOrders = uiState.hasRecentOrders,
                    onCarpenterClick = onCarpenterClick,
                    onPortfolioItemClick = onPortfolioItemClick,
                    onSeeAllCarpentersClick = onSeeAllCarpentersClick,
                    onSeeAllPortfolioClick = onSeeAllPortfolioClick
                )
            }
        }
    }
}