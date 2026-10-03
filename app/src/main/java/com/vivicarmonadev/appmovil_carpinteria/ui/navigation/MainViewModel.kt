package com.vivicarmonadev.appmovil_carpinteria.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivicarmonadev.appmovil_carpinteria.data.repository.AuthRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.data.repository.CarpenterRepositoryImpl
import com.vivicarmonadev.appmovil_carpinteria.domain.model.User
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl(),
    private val carpenterRepository: CarpenterRepositoryImpl = CarpenterRepositoryImpl()
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // ¿El carpintero tiene su perfil de taller?
    // null = no aplica (no es carpintero o no hay sesión)
    // true = tiene perfil de taller
    // false = NO tiene perfil de taller todavía
    private val _tieneCarpenterProfile = MutableStateFlow<Boolean?>(null)
    val tieneCarpenterProfile: StateFlow<Boolean?> = _tieneCarpenterProfile.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _currentUser.value = user

                if (user != null && user.role == UserRole.CARPENTER) {
                    // Chequear si tiene perfil de taller
                    val result = carpenterRepository.getCarpenterProfileOnce(user.uid)
                    _tieneCarpenterProfile.value = result.getOrNull() != null
                } else {
                    _tieneCarpenterProfile.value = null
                }
            }
        }
    }
}