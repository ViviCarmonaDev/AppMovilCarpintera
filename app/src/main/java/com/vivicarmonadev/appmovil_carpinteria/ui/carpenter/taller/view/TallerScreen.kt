package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.taller.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaInfoRowsCard
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaProfileHeader
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaStatsRow
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaTag
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.PrimaryButton
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.StatItem

@Composable
fun TallerScreen(
    viewModel: TallerViewModel,
    uid: String,
    onBack: () -> Unit,
    onEditClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uid) {
        viewModel.initialize(uid)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
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

            uiState.profile == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage ?: "No hay taller registrado",
                        fontSize = 14.sp,
                        color = Color(0xFF6B6B6B)
                    )
                }
            }

            else -> {
                val profile = uiState.profile!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    MervetaProfileHeader(
                        title = profile.nombreTaller,
                        avatarInitials = getInitials(profile.nombreTaller),
                        subtitle = profile.experienciaTexto
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats
                    MervetaStatsRow(
                        stats = listOf(
                            StatItem(
                                icon = Icons.Filled.Person,
                                value = profile.skills.size.toString(),
                                label = "Skills"
                            ),
                            StatItem(
                                icon = Icons.Filled.Star,
                                value = profile.totalReviews.toString(),
                                label = "Reseñas"
                            ),
                            StatItem(
                                icon = Icons.Filled.Star,
                                value = "%.1f".format(profile.rating),
                                label = "Rating"
                            )
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Info
                    SectionTitleText(text = "Información del taller")

                    MervetaInfoRowsCard(
                        rows = listOf(
                            "RUC" to profile.ruc.ifBlank { "—" },
                            "Descripción" to profile.descripcion.ifBlank { "—" },
                            "Dirección" to profile.direccionTaller.ifBlank { "—" },
                            "Teléfono" to profile.telefonoTaller.ifBlank { "—" }
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Skills
                    if (profile.skills.isNotEmpty()) {
                        SectionTitleText(text = "Habilidades")

                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            profile.skills.forEach { skill ->
                                MervetaTag(
                                    text = skill,
                                    backgroundColor = Color(0xFFF5EFE7),
                                    textColor = Color(0xFF8B5A2B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón editar
                    PrimaryButton(
                        text = "EDITAR TALLER",
                        icon = Icons.Filled.Edit,
                        onClick = onEditClick,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// TÍTULO DE SECCIÓN
@Composable
private fun SectionTitleText(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        color = Color(0xFF2C2C2C),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

// HELPERS

private fun getInitials(nombre: String): String {
    if (nombre.isBlank()) return "?"
    val palabras = nombre.trim().split(" ").filter { it.isNotBlank() }
    return when {
        palabras.size >= 2 ->
            "${palabras[0].first().uppercaseChar()}${palabras[1].first().uppercaseChar()}"
        palabras.size == 1 -> palabras[0].take(2).uppercase()
        else -> "?"
    }
}