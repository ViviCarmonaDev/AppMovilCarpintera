package com.vivicarmonadev.appmovil_carpinteria.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta de marca — Carpintería

val Primary        = Color(0xFF8B5A2B)   // marrón principal
val Secondary      = Color(0xFFD9C5B2)   // beige claro
val Tertiary       = Color(0xFF2C2C2C)   // gris oscuro
val Neutral        = Color(0xFFF9F7F5)   // blanco cálido

// Estados
val SuccessGreen   = Color(0xFF4CAF50)
val ErrorRed       = Color(0xFFE53935)
val WarningAmber   = Color(0xFFE8A33D)


// Tema claro

val LightPrimary          = Primary
val LightOnPrimary        = Color(0xFFF9F7F5)
val LightSecondary        = Secondary
val LightOnSecondary      = Tertiary
val LightTertiary         = Tertiary
val LightOnTertiary       = Color.White
val LightBackground       = Neutral
val LightOnBackground     = Tertiary
val LightSurface          = Color.White
val LightOnSurface        = Tertiary
val LightError            = ErrorRed
val LightOnError          = Color.White

// Tema oscuro
// (invirtiendo roles: los claros pasan a oscuros)

val DarkPrimary           = Secondary
val DarkOnPrimary         = Tertiary
val DarkSecondary         = Primary
val DarkOnSecondary       = Color.White
val DarkTertiary          = Neutral
val DarkOnTertiary        = Tertiary
val DarkBackground        = Color(0xFF1A1614)
val DarkOnBackground      = Neutral
val DarkSurface           = Color(0xFF241F1B)
val DarkOnSurface         = Neutral
val DarkError             = ErrorRed
val DarkOnError           = Color.White