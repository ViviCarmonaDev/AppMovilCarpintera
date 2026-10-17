package com.vivicarmonadev.appmovil_carpinteria.ui.shared.pedidos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Pedido
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.EstadoPedidoBadge
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.fechaCorta
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.tiempoRelativo

@Composable
fun OrderCard(
    pedido: Pedido,
    isCarpenter: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primeraImagen = pedido.imagenesUrls.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFBF1E8))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        // Fila superior: número + badge + tiempo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = pedido.numeroPedido,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B6B6B),
                    letterSpacing = 0.5.sp
                )
                EstadoPedidoBadge(estado = pedido.status)
            }

            // Tiempo con ícono de reloj
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AccessTime,
                    contentDescription = null,
                    tint = Color(0xFF8A8A8A),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = tiempoRelativo(pedido.createdAt),
                    fontSize = 11.sp,
                    color = Color(0xFF8A8A8A)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Cuerpo: imagen a la izquierda + info a la derecha
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Imagen más ancha
            Box(
                modifier = Modifier
                    .size(width = 130.dp, height = 110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5EFE7))
            ) {
                if (!primeraImagen.isNullOrBlank()) {
                    AsyncImage(
                        model = primeraImagen,
                        contentDescription = pedido.titulo,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Image,
                            contentDescription = null,
                            tint = Color(0xFF8B5A2B).copy(alpha = 0.4f),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                // Nombre según rol
                if (isCarpenter && pedido.clienteNombre.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = Color(0xFF8B5A2B),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = pedido.clienteNombre,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF8B5A2B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else if (!isCarpenter) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = if (pedido.isLibre) Color(0xFF8A8A8A) else Color(0xFF8B5A2B),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (pedido.isLibre) "Esperando carpintero"
                            else pedido.carpinteroNombre ?: "Carpintero asignado",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (pedido.isLibre) Color(0xFF8A8A8A) else Color(0xFF8B5A2B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Título
                Text(
                    text = pedido.titulo,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Chips: material | fecha
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pedido.tipoMadera.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Park,
                                contentDescription = null,
                                tint = Color(0xFF8B5A2B),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = pedido.tipoMadera.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2C2C2C),
                                letterSpacing = 0.5.sp,
                                maxLines = 1
                            )
                        }
                    }

                    // Separador |
                    if (pedido.tipoMadera.isNotBlank() && pedido.fechaEstimada != null) {
                        Text(
                            text = "|",
                            fontSize = 12.sp,
                            color = Color(0xFFBDBDBD)
                        )
                    }

                    if (pedido.fechaEstimada != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CalendarToday,
                                contentDescription = null,
                                tint = Color(0xFF8B5A2B),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Est. ${fechaCorta(pedido.fechaEstimada)}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6B6B6B),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Precio
                if (!pedido.presupuestoTexto.isNullOrBlank()) {
                    Text(
                        text = pedido.presupuestoTexto ?: "",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5A2B)
                    )
                }
            }
        }
    }
}