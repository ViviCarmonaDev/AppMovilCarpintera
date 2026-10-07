package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.perfilPublico

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vivicarmonadev.appmovil_carpinteria.domain.model.PortfolioItem
import com.vivicarmonadev.appmovil_carpinteria.domain.model.TipoPrecio

@Composable
fun CarpenterPublicProfileScreen(
    viewModel: CarpenterPublicProfileViewModel,
    uid: String,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Cargar el perfil al abrir la pantalla
    LaunchedEffect(uid) {
        viewModel.loadCarpenter(uid)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF6F1))
            .systemBarsPadding()
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
                Column(modifier = Modifier.fillMaxSize()) {

                    // ---- HEADER CON BOTÓN ATRÁS ----
                    PublicProfileHeader(onBack = onBack)

                    // ---- CONTENIDO SCROLLEABLE ----
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {

                        // ---- INFO PRINCIPAL ----
                        item {
                            MainInfoSection(
                                initials = uiState.initials,
                                nombreTaller = uiState.profile?.nombreTaller ?: "",
                                rating = uiState.ratingTexto,
                                experiencia = uiState.experienciaTexto
                            )
                        }

                        // ---- DESCRIPCIÓN ----
                        if (!uiState.profile?.descripcion.isNullOrBlank()) {
                            item {
                                SectionCard(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = uiState.profile?.descripcion ?: "",
                                        fontSize = 14.sp,
                                        color = Color(0xFF2C2C2C),
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }

                        // ---- INFO DEL TALLER (dirección, teléfono, RUC) ----
                        item {
                            InfoCard(
                                direccion = uiState.profile?.direccionTaller ?: "",
                                telefono = uiState.profile?.telefonoTaller ?: "",
                                ruc = uiState.profile?.ruc ?: ""
                            )
                        }

                        // ---- SKILLS ----
                        if (uiState.hasSkills) {
                            item {
                                SkillsSection(
                                    skills = uiState.profile?.skills ?: emptyList()
                                )
                            }
                        }

                        // ---- PORTAFOLIO ----
                        if (uiState.hasPortfolio) {
                            item {
                                Text(
                                    text = "Trabajos publicados",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2C2C2C),
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 20.dp,
                                        bottom = 8.dp
                                    )
                                )
                            }

                            items(uiState.portfolioItems, key = { it.id }) { item ->
                                PortfolioMiniCard(
                                    item = item,
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 6.dp
                                    )
                                )
                            }
                        } else {
                            item {
                                EmptyPortfolioSection()
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    // ---- BOTÓN WHATSAPP (fijo abajo) ----
                    WhatsAppButton(
                        onClick = {
                            val link = viewModel.buildWhatsAppLink()
                            if (link != null) {
                                val intent = Intent(Intent.ACTION_VIEW, link.toUri())
                                context.startActivity(intent)
                            }
                        },
                        enabled = (uiState.profile?.telefonoTaller?.filter { it.isDigit() }?.length ?: 0) == 9
                    )
                }
            }
        }
    }
}

// HEADER CON BOTÓN ATRÁS

@Composable
private fun PublicProfileHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF8B5A2B))
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Atrás",
                tint = Color(0xFFF9F7F5),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Perfil del carpintero",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF9F7F5)
        )
    }
}

// INFO PRINCIPAL (avatar + nombre + rating)
@Composable
private fun MainInfoSection(
    initials: String,
    nombreTaller: String,
    rating: String,
    experiencia: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar con iniciales
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color(0xFF8B5A2B)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF9F7F5)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre del taller
        Text(
            text = nombreTaller.ifBlank { "Carpintero" },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Rating + Experiencia
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Color(0xFFE0A458),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = rating,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2C2C2C)
            )
            Text(
                text = "  ·  ",
                fontSize = 14.sp,
                color = Color(0xFF6B6B6B)
            )
            Text(
                text = experiencia,
                fontSize = 14.sp,
                color = Color(0xFF6B6B6B)
            )
        }
    }
}

// CARD GENÉRICA DE SECCIÓN
@Composable
private fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        content()
    }
}

// TARJETA DE INFO (dirección, teléfono, RUC)
@Composable
private fun InfoCard(
    direccion: String,
    telefono: String,
    ruc: String
) {
    SectionCard(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            if (direccion.isNotBlank()) {
                InfoRow(
                    icon = Icons.Filled.LocationOn,
                    label = "Dirección",
                    value = direccion
                )
            }

            if (telefono.isNotBlank()) {
                InfoRow(
                    icon = Icons.Filled.Phone,
                    label = "Teléfono",
                    value = telefono
                )
            }

            if (ruc.isNotBlank()) {
                InfoRow(
                    icon = Icons.Filled.Business,
                    label = "RUC",
                    value = ruc
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF8B5A2B),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF6B6B6B)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2C2C2C)
            )
        }
    }
}
// SECCIÓN DE SKILLS

@Composable
private fun SkillsSection(skills: List<String>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Habilidades",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Chips de skills en filas de 2
        val rows = skills.chunked(2)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            rows.forEach { rowSkills ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowSkills.forEach { skill ->
                        SkillChipPublic(
                            text = skill,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowSkills.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillChipPublic(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF5EFE7))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF8B5A2B),
            textAlign = TextAlign.Center
        )
    }
}

// TARJETA MINI DE UN TRABAJO

@Composable
private fun PortfolioMiniCard(
    item: PortfolioItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        // Título
        Text(
            text = item.titulo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Categoría + Material
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (item.categoria.isNotBlank()) {
                SmallTag(text = item.categoria, color = Color(0xFFF5EFE7), textColor = Color(0xFF8B5A2B))
            }
            if (item.material.isNotBlank()) {
                SmallTag(text = item.material, color = Color(0xFFF5EFE7), textColor = Color(0xFF6B6B6B))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Precio + etiqueta
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = item.precioTexto,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B5A2B)
            )

            if (item.etiquetaPrecio != null) {
                TipoPrecioTagPublic(
                    texto = item.etiquetaPrecio!!,
                    tipo = item.tipoPrecio
                )
            }
        }
    }
}

@Composable
private fun SmallTag(text: String, color: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun TipoPrecioTagPublic(texto: String, tipo: TipoPrecio) {
    val (bgColor, textColor) = when (tipo) {
        TipoPrecio.FIJO -> Color(0xFF8B5A2B) to Color(0xFFF9F7F5)
        TipoPrecio.A_TRATAR -> Color(0xFFD4E0D9) to Color(0xFF2E4A3E)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = texto,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = textColor
        )
    }
}

// ESTADO VACÍO DE PORTAFOLIO

@Composable
private fun EmptyPortfolioSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Este carpintero aún no publicó trabajos",
            fontSize = 14.sp,
            color = Color(0xFF6B6B6B),
            textAlign = TextAlign.Center
        )
    }
}

// BOTÓN WHATSAPP (fijo abajo)

@Composable
private fun WhatsAppButton(
    onClick: () -> Unit,
    enabled: Boolean
) {
    val bgColor = if (enabled) Color(0xFF25D366) else Color(0xFFBDBDBD)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFAF6F1))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(bgColor)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📱",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CONTACTAR POR WHATSAPP",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Color.White
            )
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
        Text(
            text = "😕",
            fontSize = 48.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            fontSize = 16.sp,
            color = Color(0xFF2C2C2C),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF8B5A2B))
                .clickable(onClick = onBack)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "VOLVER",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Color(0xFFF9F7F5)
            )
        }
    }
}