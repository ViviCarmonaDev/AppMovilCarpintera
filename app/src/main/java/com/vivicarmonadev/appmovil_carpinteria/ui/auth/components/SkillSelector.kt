package com.vivicarmonadev.appmovil_carpinteria.ui.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Lista de skills disponibles que el carpintero puede seleccionar.
 */
val AVAILABLE_SKILLS = listOf(
    "Muebles a medida",
    "Restauración",
    "Talla en madera",
    "Torneado",
    "Ensamblaje",
    "Acabados",
    "Puertas y ventanas",
    "Cocinas",
    "Closets",
    "Escaleras",
    "Muebles de baño",
    "Decoración"
)

@Composable
fun SkillSelector(
    selectedSkills: List<String>,
    onSkillToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Agrupar las skills en pares (2 por fila)
    val rows = AVAILABLE_SKILLS.chunked(2)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { rowSkills ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowSkills.forEach { skill ->
                    SkillChip(
                        skill = skill,
                        isSelected = skill in selectedSkills,
                        onToggle = { onSkillToggle(skill) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Si la fila tiene menos de 2 elementos, agregar un spacer invisible
                if (rowSkills.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SkillChip(
    skill: String,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onToggle,
        label = {
            Text(
                text = skill,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) Color(0xFFF9F7F5) else Color(0xFF2C2C2C)
            )
        },
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color(0xFFF5EFE7),
            labelColor = Color(0xFF2C2C2C),
            selectedContainerColor = Color(0xFF8B5A2B),
            selectedLabelColor = Color(0xFFF9F7F5)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) Color(0xFF8B5A2B) else Color(0xFFE0DCD7)
        ),
        modifier = modifier
    )
}