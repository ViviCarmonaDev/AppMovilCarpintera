package com.vivicarmonadev.appmovil_carpinteria.ui.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Barra de búsqueda reutilizable de Merveta.
 * Fondo beige, sin borde, redondeada.
 */
@Composable
fun MervetaSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    placeholder: String = "Buscar...",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFF8A8A8A),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Buscar",
                tint = Color(0xFF6B6B6B),
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Limpiar",
                        tint = Color(0xFF6B6B6B)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            //  Sin borde visible
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,

            // Fondo beige (mismo que los cards)
            focusedContainerColor = Color(0xFFF5EFE7),
            unfocusedContainerColor = Color(0xFFF5EFE7),

            //  Color del cursor y texto
            cursorColor = Color(0xFF8B5A2B),
            focusedTextColor = Color(0xFF2C2C2C),
            unfocusedTextColor = Color(0xFF2C2C2C)
        )
    )
}