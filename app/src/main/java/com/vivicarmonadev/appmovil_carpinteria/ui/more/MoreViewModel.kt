package com.vivicarmonadev.appmovil_carpinteria.ui.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla "Más".
 *
 * Por ahora solo maneja el cierre de sesión, pero acá van a vivir
 * otras acciones como: cambiar preferencias, eliminar cuenta, etc.
 */
class MoreViewModel(
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    /**
     * Cierra la sesión en Firebase Auth y ejecuta `onComplete`
     * cuando termina (para navegar al Welcome).
     */
    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onComplete()
        }
    }
}