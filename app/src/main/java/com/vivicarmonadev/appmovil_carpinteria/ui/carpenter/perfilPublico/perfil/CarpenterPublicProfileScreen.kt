package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.perfil

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.padding
import androidx.core.net.toUri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.perfil.CarpenterPublicProfileViewModel
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.components.MenuOptionsSection
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.components.PortfolioSection
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.components.PublicProfileHeader
import com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico.components.ReviewsSection

@Composable
fun CarpenterPublicProfileScreen(
    viewModel: CarpenterPublicProfileViewModel,
    uid: String,
    onBack: () -> Unit,
    onSeeAllPortfolioClick: (String) -> Unit,
    onPortfolioItemClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uid) {
        viewModel.loadCarpenter(uid)
    }

    // ⚠️ SIN systemBarsPadding() para que el header suba hasta el status bar
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF8B5A2B))
                }
            }

            uiState.errorMessage != null && !uiState.isLoaded -> {
                ErrorState(
                    message = uiState.errorMessage ?: "",
                    onBack = onBack
                )
            }

            uiState.isLoaded -> {
                // ✅ TODO dentro de un LazyColumn → todo scrollea junto
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {

                    // ---- HEADER (scrollea con el contenido) ----
                    item {
                        PublicProfileHeader(
                            onBack = onBack,
                            onFavoriteClick = { /* TODO */ },
                            onMessageClick = {
                                val link = viewModel.buildWhatsAppLink()
                                if (link != null) {
                                    val intent = Intent(Intent.ACTION_VIEW, link.toUri())
                                    context.startActivity(intent)
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
                            onItemClick = { /* TODO */ },
                            onSeeAllClick = { /* TODO */ },
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    // ---- RESEÑAS ----
                    item {
                        ReviewsSection(
                            reviews = uiState.reviews,
                            onSeeAllClick = { /* TODO */ },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // ---- OPCIONES DE MENÚ ----
                    item {
                        MenuOptionsSection(
                            onMiPerfilClick = { /* TODO: ir a Mi perfil */ },
                            onMiTallerClick = { /* TODO: ir a Mi taller */ },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // ---- ESPACIO FINAL ----
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

// ESTADO DE ERROR
@Composable
private fun ErrorState(
    message: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "😕", fontSize = 48.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            fontSize = 16.sp,
            color = Color(0xFF2C2C2C),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "VOLVER",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color(0xFF8B5A2B),
            modifier = Modifier
                .background(
                    color = Color(0xFFF5EFE7),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .clickable(onClick = onBack)
        )
    }
}