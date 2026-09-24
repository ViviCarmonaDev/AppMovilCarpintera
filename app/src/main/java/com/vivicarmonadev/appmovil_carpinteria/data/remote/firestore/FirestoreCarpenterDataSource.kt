package com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vivicarmonadev.appmovil_carpinteria.data.model.CarpenterProfileDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * DataSource de Firestore para perfiles de carpinteros.
 *
 * Es la única clase que habla DIRECTAMENTE con la colección
 * `carpenter_profiles` de Firestore. El Repository la usa para
 * guardar y leer perfiles sin conocer los detalles de Firestore.
 */
class FirestoreCarpenterDataSource {

    private val firestore: FirebaseFirestore = Firebase.firestore

    companion object {
        private const val COLLECTION_CARPENTER_PROFILES = "carpenter_profiles"
    }

     // Guarda el perfil del carpintero en Firestore. Usa el uid como ID del documento.

    suspend fun saveProfile(profile: CarpenterProfileDto): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_CARPENTER_PROFILES)
                .document(profile.uid)
                .set(profile)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lee el perfil del carpintero una sola vez. Devuelve null si no existe.

    suspend fun getProfileOnce(uid: String): Result<CarpenterProfileDto?> {
        return try {
            val snapshot = firestore.collection(COLLECTION_CARPENTER_PROFILES)
                .document(uid)
                .get()
                .await()

            if (snapshot.exists()) {
                val profile = snapshot.toObject(CarpenterProfileDto::class.java)
                Result.success(profile)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

     // Escucha cambios en el perfil del carpintero en tiempo real. Emite el perfil cada vez que cambia en Firestore.

    fun getProfileFlow(uid: String): Flow<CarpenterProfileDto?> = callbackFlow {
        val listener = firestore.collection(COLLECTION_CARPENTER_PROFILES)
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val profile = snapshot.toObject(CarpenterProfileDto::class.java)
                    trySend(profile)
                } else {
                    trySend(null)
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    // Actualiza campos específicos del perfil del carpintero. Útil para actualizar solo algunos campos sin reemplazar todo.

    suspend fun updateProfileFields(
        uid: String,
        fields: Map<String, Any>
    ): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_CARPENTER_PROFILES)
                .document(uid)
                .update(fields)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}