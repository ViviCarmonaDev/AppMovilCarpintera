package com.vivicarmonadev.appmovil_carpinteria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vivicarmonadev.appmovil_carpinteria.ui.welcome.WelcomeScreen
import com.vivicarmonadev.appmovil_carpinteria.ui.theme.AppMovilCarpinteriaTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMovilCarpinteriaTheme {
                    WelcomeScreen(
                        onExploreClick = {

                        }
                    )
                }
            }
        }
    }
