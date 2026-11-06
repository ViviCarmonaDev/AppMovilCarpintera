package com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vivicarmonadev.appmovil_carpinteria.data.model.NotificacionDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreNotificacionDataSource {

    private val firestore: FirebaseFirestore = Firebase.firestore
    private val collection = "notificaciones"

    fun getNotificacionesFlow(uid: String): Flow<List<NotificacionDto>> = callbackFlow {
        val listener = firestore.collection(collection)
            .whereEqualTo("destinatarioUid", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val notificaciones = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(NotificacionDto::class.java)
                } ?: emptyList()
                trySend(notificaciones)
            }
        awaitClose { listener.remove() }
    }

    suspend fun marcarComoLeida(id: String): Result<Unit> {
        return try {
            firestore.collection(collection).document(id).update("leida", true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun marcarTodasComoLeidas(uid: String): Result<Unit> {
        return try {
            val snapshot = firestore.collection(collection)
                .whereEqualTo("destinatarioUid", uid)
                .whereEqualTo("leida", false)
                .get()
                .await()

            val batch = firestore.batch()
            snapshot.documents.forEach { doc ->
                batch.update(doc.reference, "leida", true)
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearNotificacion(dto: NotificacionDto): Result<Unit> {
        return try {
            firestore.collection("notificaciones").document().set(dto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}