package com.vivicarmonadev.appmovil_carpinteria.ui.auth.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val hasError = errorMessage != null

    Column(modifier = modifier.fillMaxWidth()) {

        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2C2C2C)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF2C2C2C).copy(alpha = 0.35f),
                    fontSize = 15.sp
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            isError = hasError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Filled.Visibility
                            else
                                Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible)
                                "Ocultar contraseña" else "Mostrar contraseña",
                            tint = Color(0xFF2C2C2C).copy(alpha = 0.5f)
                        )
                    }
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (hasError) Color(0xFFC5544A) else Color.Transparent,
                unfocusedBorderColor = if (hasError) Color(0xFFC5544A) else Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = Color(0xFFC5544A),
                focusedContainerColor = Color(0xFF2C2C2C).copy(alpha = 0.05f),
                unfocusedContainerColor = Color(0xFF2C2C2C).copy(alpha = 0.05f),
                errorContainerColor = Color(0xFFC5544A).copy(alpha = 0.05f),
                focusedTextColor = Color(0xFF2C2C2C),
                unfocusedTextColor = Color(0xFF2C2C2C),
                cursorColor = Color(0xFF8B5A2B)
            )
        )

        // Mensaje de error debajo del campo
        if (hasError) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage ?: "",
                fontSize = 12.sp,
                color = Color(0xFFC5544A),
                fontWeight = FontWeight.Medium
            )
        }
    }
}