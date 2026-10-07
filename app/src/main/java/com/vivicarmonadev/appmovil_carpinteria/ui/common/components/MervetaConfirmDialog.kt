package com.vivicarmonadev.appmovil_carpinteria.ui.common.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size

/**
 * Diálogo de confirmación reutilizable de Merveta.
 */

@Composable
fun MervetaConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Eliminar",
    cancelText: String = "Cancelar",
    isLoading: Boolean = false,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isLoading) onCancel() },
        title = {
            Text(text = title, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(text = message, fontSize = 14.sp)
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !isLoading) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFFC5544A)
                    )
                } else {
                    Text(
                        text = confirmText,
                        color = Color(0xFFC5544A),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel, enabled = !isLoading) {
                Text(text = cancelText, color = Color(0xFF6B6B6B))
            }
        }
    )
}