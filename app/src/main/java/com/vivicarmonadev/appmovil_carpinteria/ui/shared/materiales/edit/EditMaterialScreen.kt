package com.vivicarmonadev.appmovil_carpinteria.ui.shared.materiales.edit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vivicarmonadev.appmovil_carpinteria.ui.auth.components.AuthTextField
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaDiscardDialog
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.MervetaEditHeader
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.PrimaryButton
import com.vivicarmonadev.appmovil_carpinteria.ui.common.components.copiarUriACache

@Composable
fun EditMaterialScreen(
    viewModel: EditMaterialViewModel,
    materialId: String,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(materialId) {
        viewModel.initialize(materialId)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val ruta = copiarUriACache(context, uri)
            if (ruta != null) viewModel.onImagenChange(ruta)
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccess()
            onSaveSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).systemBarsPadding()
    ) {
        MervetaEditHeader(
            title = "Editar material",
            onBack = {
                val canExit = viewModel.onBackClick()
                if (canExit) onBack()
            }
        )

        if (uiState.isLoading && uiState.nombre.isBlank()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF8B5A2B))
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF5EFE7))
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (!uiState.imagenUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = uiState.imagenUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("+ Imagen", fontSize = 12.sp, color = Color(0xFF8B5A2B))
                    }
                }

                Spacer(Modifier.height(24.dp))

                AuthTextField(
                    value = uiState.nombre,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = "Nombre del material",
                    placeholder = "Ej. Melamina blanca, Cedro, etc.",
                    errorMessage = uiState.nombreError
                )

                Spacer(Modifier.height(16.dp))

                AuthTextField(
                    value = uiState.descripcion,
                    onValueChange = { viewModel.onDescripcionChange(it) },
                    label = "Descripción",
                    placeholder = "Describe las características del material...",
                    errorMessage = uiState.descripcionError
                )

                Spacer(Modifier.height(16.dp))

                AuthTextField(
                    value = uiState.precioReferencial,
                    onValueChange = { viewModel.onPrecioChange(it) },
                    label = "Precio referencial (S/)",
                    placeholder = "Ej. 150 o 150.50",
                    errorMessage = uiState.precioError,
                    keyboardType = KeyboardType.Decimal
                )

                Spacer(Modifier.height(32.dp))

                if (uiState.isLoading) {
                    Box(
                        Modifier.fillMaxWidth().height(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF8B5A2B))
                    }
                } else {
                    PrimaryButton(
                        text = "Guardar cambios",
                        onClick = { viewModel.save() },
                        enabled = uiState.isFormValid
                    )
                }
            }
        }

        if (uiState.showDiscardDialog) {
            MervetaDiscardDialog(
                onConfirm = { viewModel.onDiscardConfirm(); onBack() },
                onCancel = { viewModel.onDiscardCancel() }
            )
        }
    }
}