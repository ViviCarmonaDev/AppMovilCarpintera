package com.vivicarmonadev.appmovil_carpinteria.ui.carpenter.portafolio.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import com.vivicarmonadev.appmovil_carpinteria.domain.model.TipoPrecio
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthTextField
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaGalleryPicker
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.PrimaryButton

/**
 * Formulario visual compartido entre CREATE y EDIT de un trabajo.
 * NO tiene lógica. Solo recibe el estado y los callbacks.
 */

@Composable
fun PortfolioItemFormContent(
    // Datos
    titulo: String,
    descripcion: String,
    categoria: String,
    material: String,
    anchoCm: String,
    altoCm: String,
    profundidadCm: String,
    tipoPrecio: TipoPrecio?,
    precioReferencial: String,

    // Errores
    tituloError: String?,
    descripcionError: String?,
    categoriaError: String?,
    materialError: String?,
    precioError: String?,
    errorMessage: String?,

    // Estados
    isLoading: Boolean,
    isFormValid: Boolean,

    // Configuración
    buttonText: String,

    // Imágenes
    imagenesUrls: List<String>,
    onImagenClick: () -> Unit,
    onEliminarImagen: (Int) -> Unit,

    // Callbacks
    onTituloChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onCategoriaChange: (String) -> Unit,
    onMaterialChange: (String) -> Unit,
    onAnchoChange: (String) -> Unit,
    onAltoChange: (String) -> Unit,
    onProfundidadChange: (String) -> Unit,
    onTipoPrecioChange: (TipoPrecio) -> Unit,
    onPrecioChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // ---- IMÁGENES ----
        MervetaGalleryPicker(
            imagenes = imagenesUrls,
            label = "Imágenes del trabajo",
            maxImagenes = 3,
            onAgregarClick = onImagenClick,
            onEliminarClick = onEliminarImagen
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ---- TÍTULO ----
        AuthTextField(
            value = titulo,
            onValueChange = onTituloChange,
            label = "Título del trabajo",
            placeholder = "Ej. Mesa de comedor nogal",
            errorMessage = tituloError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ---- DESCRIPCIÓN ----
        AuthTextField(
            value = descripcion,
            onValueChange = onDescripcionChange,
            label = "Descripción",
            placeholder = "Describe el trabajo, sus dimensiones, acabados...",
            errorMessage = descripcionError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ---- CATEGORÍA ----
        AuthTextField(
            value = categoria,
            onValueChange = onCategoriaChange,
            label = "Categoría",
            placeholder = "Ej. Muebles, Puertas, Cocinas",
            errorMessage = categoriaError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ---- MATERIAL ----
        AuthTextField(
            value = material,
            onValueChange = onMaterialChange,
            label = "Material",
            placeholder = "Ej. Roble, Pino, Cedro, MDF",
            errorMessage = materialError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ---- TIPO DE PRECIO ----
        Text(
            text = "Tipo de precio",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2C2C2C)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TipoPrecioChip(
                text = "FIJO",
                subtitle = "Precio final",
                isSelected = tipoPrecio == TipoPrecio.FIJO,
                onClick = { onTipoPrecioChange(TipoPrecio.FIJO) },
                modifier = Modifier.weight(1f)
            )
            TipoPrecioChip(
                text = "A TRATAR",
                subtitle = "Se negocia",
                isSelected = tipoPrecio == TipoPrecio.A_TRATAR,
                onClick = { onTipoPrecioChange(TipoPrecio.A_TRATAR) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- MEDIDAS ----
        Text(
            text = "Medidas (cm)",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2C2C2C)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                AuthTextField(
                    value = anchoCm,
                    onValueChange = onAnchoChange,
                    label = "Ancho (cm)",
                    placeholder = "Ej. 120",
                    errorMessage = null,
                    keyboardType = KeyboardType.Decimal
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                AuthTextField(
                    value = altoCm,
                    onValueChange = onAltoChange,
                    label = "Alto (cm)",
                    placeholder = "Ej. 75",
                    errorMessage = null,
                    keyboardType = KeyboardType.Decimal
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                AuthTextField(
                    value = profundidadCm,
                    onValueChange = onProfundidadChange,
                    label = "Prof. (cm)",
                    placeholder = "Ej. 80",
                    errorMessage = null,
                    keyboardType = KeyboardType.Decimal
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---- PRECIO ----
        AuthTextField(
            value = precioReferencial,
            onValueChange = onPrecioChange,
            label = if (tipoPrecio == TipoPrecio.FIJO)
                "Precio (obligatorio)"
            else
                "Precio referencial (opcional)",
            placeholder = "Ej. 250 o 250.50",
            errorMessage = precioError,
            keyboardType = KeyboardType.Decimal
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ---- ERROR GENERAL ----
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                fontSize = 13.sp,
                color = Color(0xFFC5544A),
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ---- BOTÓN ----
        if (isLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF8B5A2B),
                    strokeWidth = 3.dp
                )
            }
        } else {
            PrimaryButton(
                text = buttonText,
                onClick = onSubmit,
                enabled = isFormValid
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}