package com.vivicarmonadev.appmovil_carpinteria.ui.shared.notificaciones.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Notificacion
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaEmptyState
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.home.components.MervetaHeader
import com.vivicarmonadev.appmovil_carpinteria.ui.shared.notificaciones.components.NotificacionItem

@Composable
fun NotificacionesScreen(
    viewModel: NotificacionesViewModel,
    onNotificacionClick: (Notificacion) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        MervetaHeader(
            title = "Notificaciones",
            subtitle = "Tus alertas recientes",
            avatarIcon = Icons.Filled.Notifications,
            verticalPadding = 40.dp,
            bottomPadding = 20.dp
        )

        Box(modifier = Modifier.weight(1f)) {
            when {
                uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF8B5A2B))
                }

                uiState.isEmpty -> MervetaEmptyState(
                    icon = Icons.Filled.Notifications,
                    title = "Sin notificaciones",
                    subtitle = "Cuando tengas nuevas alertas, aparecerán acá"
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.notificaciones, key = { it.id }) { notificacion ->
                        NotificacionItem(
                            notificacion = notificacion,
                            onClick = {
                                if (!notificacion.leida) {
                                    viewModel.marcarComoLeida(notificacion.id)
                                }
                                onNotificacionClick(notificacion)
                            }
                        )
                    }
                }
            }
        }
    }
}