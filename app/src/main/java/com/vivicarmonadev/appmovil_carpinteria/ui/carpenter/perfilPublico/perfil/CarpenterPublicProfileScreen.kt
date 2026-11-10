package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.perfil

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.components.MenuOptionsSection
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.components.PortfolioSection
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.components.PublicProfileHeader

@Composable
fun CarpenterPublicProfileScreen(
    viewModel: CarpenterPublicProfileViewModel,
    uid: String,
    onBack: () -> Unit,
    onSeeAllPortfolioClick: (String) -> Unit,
    onPortfolioItemClick: (String) -> Unit,
    onUbicacionClick: (Double, Double, String) -> Unit,
    onPerfilClick: (String) -> Unit,
    onTallerClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uid) {
        viewModel.loadCarpenter(uid)
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF8B5A2B))
                }
            }

            uiState.errorMessage != null && !uiState.isLoaded -> {
                ErrorState(message = uiState.errorMessage ?: "", onBack = onBack)
            }

            uiState.isLoaded -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // ---- HEADER ----
                    item {
                        PublicProfileHeader(
                            onBack = onBack,
                            onFavoriteClick = { /* TODO */ },
                            onMessageClick = {
                                val link = viewModel.buildWhatsAppLink()
                                if (link != null) {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, link.toUri()))
                                }
                            },
                            fotoTallerUrl = uiState.profile?.fotoTallerUrl,
                            fotoMaestroUrl = uiState.profile?.fotoMaestroUrl,
                            initials = uiState.initials,
                            nombreTaller = uiState.profile?.nombreTaller ?: "",
                            oficio = uiState.profile?.skills?.firstOrNull() ?: "Carpintero profesional",
                            rating = uiState.ratingTexto,
                            totalReviews = uiState.profile?.totalReviews ?: 0,
                            proyectosCount = uiState.portfolioItems.size,
                            aniosExperiencia = uiState.profile?.aniosExperiencia ?: 0,
                            isVerified = uiState.profile?.profileCompleted == true
                        )
                    }

                    // ---- PORTAFOLIO ----
                    item {
                        PortfolioSection(
                            items = uiState.portfolioItems,
                            onItemClick = { itemId -> onPortfolioItemClick(itemId) }, // <-- CONECTADO
                            onSeeAllClick = { onSeeAllPortfolioClick(uid) }, // <-- CONECTADO
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    // ---- OPCIONES DE MENÚ ----
                    item {

                        MenuOptionsSection(
                            onMiPerfilClick = { onPerfilClick(uid) },
                            onMiTallerClick = { onTallerClick(uid)},
                            onMiUbicacionClick = {
                                val lat = uiState.profile?.latitud
                                val lng = uiState.profile?.longitud
                                val nombre = uiState.profile?.nombreTaller ?: "Taller"

                                if (lat != null && lng != null) {
                                    onUbicacionClick(lat, lng, nombre) // <-- CONECTADO A DATOS REALES
                                } else {
                                    // Aquí podrías mostrar un Toast o Snackbar si no hay ubicación
                                }
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ErrorState(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "😕", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, fontSize = 16.sp, color = Color(0xFF2C2C2C), textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "VOLVER",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color(0xFF8B5A2B),
            modifier = Modifier
                .background(color = Color(0xFFF5EFE7), shape = RoundedCornerShape(28.dp))
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .clickable(onClick = onBack)
        )
    }
}