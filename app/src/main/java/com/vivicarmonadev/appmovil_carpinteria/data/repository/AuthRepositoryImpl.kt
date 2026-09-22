package com.vivicarmonadev.appmovil_carpinteria.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vivicarmonadev.appmovil_carpinteria.data.model.UserDto
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDomain
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDto
import com.vivicarmonadev.appmovil_carpinteria.data.remote.auth.FirebaseAuthDataSource
import com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore.FirestoreUserDataSource
import com.vivicarmonadev.appmovil_carpinteria.domain.model.User
import com.vivicarmonadev.appmovil_carpinteria.domain.model.UserRole
import com.vivicarmonadev.appmovil_carpinteria.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Implementación del AuthRepository usando Firebase.
 *
 * Es el "pegamento" entre la capa de dominio (que solo conoce la interfaz)
 * y la capa de datos (que conoce Firebase).
 *
 * Sus responsabilidades:
 *  1. Coordinar FirebaseAuthDataSource + FirestoreUserDataSource
 *  2. Convertir entre UserDto (Firestore) y User (dominio)
 *  3. Envolver errores en Result
 */
class AuthRepositoryImpl(
    private val authDataSource: FirebaseAuthDataSource = FirebaseAuthDataSource(),
    private val userDataSource: FirestoreUserDataSource = FirestoreUserDataSource()
) : AuthRepository {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore

    // USUARIO ACTUAL (reactivo)

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser

            if (firebaseUser == null) {
                trySend(null)
            } else {
                // Cuando cambia el estado, leemos el usuario desde Firestore
                // para tener todos sus datos (nombre, rol, etc.).
                // Usamos launch para no bloquear el callback.
                kotlinx.coroutines.GlobalScope.launch {
                    val result = userDataSource.getUserById(firebaseUser.uid)
                    val user = result.getOrNull()?.toDomain()
                    trySend(user)
                }
            }
        }

        auth.addAuthStateListener(listener)

        // Cuando el Flow se cancela, removemos el listener
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    // REGISTRO

    override suspend fun register(
        nombres: String,
        apellidos: String,
        telefono: String,
        email: String,
        password: String,
        role: UserRole
    ): Result<User> {
        return try {
            // 1. Crear el usuario en Firebase Auth
            val displayName = "$nombres $apellidos".trim()
            val firebaseUser = authDataSource.createUserWithEmail(
                email = email,
                password = password,
                displayName = displayName
            )

            if (firebaseUser == null) {
                return Result.failure(Exception("No se pudo crear el usuario en Firebase Auth"))
            }

            // 2. Guardar los datos adicionales en Firestore
            val userDto = UserDto(
                uid = firebaseUser.uid,
                nombres = nombres,
                apellidos = apellidos,
                telefono = telefono,
                email = email,
                role = role.toFirestoreValue(),
                photoUrl = null
            )

            val saveResult = userDataSource.saveUser(userDto)

            if (saveResult.isFailure) {
                // Si falla el guardado en Firestore, igual devolvemos error.
                // El usuario existe en Auth pero no en Firestore.
                // TODO: manejar este caso (borrar el usuario de Auth o reintentar)
                return Result.failure(
                    saveResult.exceptionOrNull()
                        ?: Exception("Error al guardar en Firestore")
                )
            }

            // 3. Devolver el User de dominio
            Result.success(userDto.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // LOGIN

    override suspend fun login(
        email: String,
        password: String
    ): Result<User> {
        return try {
            // 1. Iniciar sesión con Firebase Auth
            val firebaseUser = authDataSource.signInWithEmail(email, password)

            if (firebaseUser == null) {
                return Result.failure(Exception("Credenciales incorrectas"))
            }

            // 2. Leer los datos del usuario desde Firestore
            val result = userDataSource.getUserById(firebaseUser.uid)

            if (result.isFailure) {
                return Result.failure(
                    result.exceptionOrNull() ?: Exception("Error al leer usuario")
                )
            }

            val userDto = result.getOrNull()

            if (userDto == null) {
                return Result.failure(Exception("El usuario no existe en Firestore"))
            }

            Result.success(userDto.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // LOGOUT
    override suspend fun logout(): Result<Unit> {
        return try {
            authDataSource.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // LEER USUARIO POR UID
    override suspend fun getUserById(uid: String): Result<User?> {
        return try {
            val result = userDataSource.getUserById(uid)
            result.map { dto -> dto?.toDomain() }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}