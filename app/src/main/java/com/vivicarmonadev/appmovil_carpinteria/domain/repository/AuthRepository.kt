package com.vivicarmonadev.appmovil_carpinteria.domain.repository

import com.vivicarmonadev.appmovil_carpinteria.domain.model.User
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de autenticación.
 *
 * Define QUÉ se puede hacer con la autenticación, pero no CÓMO. El "cómo" lo implementa AuthRepositoryImpl en la capa data.
 *
 * Esto permite que si se cambia Firebase por otro servicio,
 * solo tengas que escribir una nueva implementación sin tocar, los ViewModels.
 */

interface AuthRepository {

    // Flujo reactivo del usuario actual, Emite null cuando no hay sesión iniciada, o el User cuando sí.

    val currentUser: Flow<User?>

    // Registra un nuevo usuario.

    suspend fun register(
        nombres: String,
        apellidos: String,
        telefono: String,
        email: String,
        password: String,
        role: UserRole
    ): Result<User>


    // Inicia sesión con email y contraseña.

    suspend fun login(
        email: String,
        password: String
    ): Result<User>

    // Cierra sesión.

    suspend fun logout(): Result<Unit>


    // Obtiene el User desde Firestore a partir del uid, Devuelve null si el documento no existe.

    suspend fun getUserById(uid: String): Result<User?>

    /**
     * Inicia sesión con Google.
     * Recibe el idToken ya obtenido por el GoogleSignInHelper.
     */
    suspend fun loginWithGoogle(idToken: String): Result<User>

    /**
     * Actualiza los datos editables del usuario en Firestore.
     * Solo se envían los campos que cambiaron.
     */
    suspend fun updateUser(
        uid: String,
        nombres: String,
        apellidos: String,
        telefono: String
    ): Result<User>
}