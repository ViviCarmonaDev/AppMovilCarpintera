package com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vivicarmonadev.appmovil_carpinteria.data.model.PortfolioItemDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * DataSource de Firestore para trabajos del portafolio.
 *
 * Es la única clase que habla DIRECTAMENTE con la colección
 * `portfolio_items`. El Repository la usa para gestionar los trabajos.
 */
class FirestorePortfolioDataSource {

    private val firestore: FirebaseFirestore = Firebase.firestore

    companion object {
        private const val COLLECTION_PORTFOLIO = "portfolio_items"
    }

    // LECTURA — TODOS los trabajos (catálogo general)

    fun getAllItemsFlow(): Flow<List<PortfolioItemDto>> = callbackFlow {
        val listener = firestore.collection(COLLECTION_PORTFOLIO)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(PortfolioItemDto::class.java)
                } ?: emptyList()

                trySend(items)
            }

        awaitClose { listener.remove() }
    }

    // LECTURA — Trabajos de UN carpintero

    fun getItemsByUidFlow(uid: String): Flow<List<PortfolioItemDto>> = callbackFlow {
        val listener = firestore.collection(COLLECTION_PORTFOLIO)
            .whereEqualTo("uid", uid)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(PortfolioItemDto::class.java)
                } ?: emptyList()

                trySend(items)
            }

        awaitClose { listener.remove() }
    }

    // LECTURA — Un item por ID

    suspend fun getItemById(id: String): Result<PortfolioItemDto?> {
        return try {
            val snapshot = firestore.collection(COLLECTION_PORTFOLIO)
                .document(id)
                .get()
                .await()

            if (snapshot.exists()) {
                Result.success(snapshot.toObject(PortfolioItemDto::class.java))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // CREAR

    suspend fun createItem(item: PortfolioItemDto): Result<PortfolioItemDto> {
        return try {
            // Si el DTO ya tiene un id, lo respetamos; si no, Firestore lo genera
            val docRef = if (item.id.isBlank()) {
                firestore.collection(COLLECTION_PORTFOLIO).document()
            } else {
                firestore.collection(COLLECTION_PORTFOLIO).document(item.id)
            }

            val dtoWithId = item.copy(id = docRef.id)
            docRef.set(dtoWithId).await()

            Result.success(dtoWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ACTUALIZAR

    suspend fun updateItem(item: PortfolioItemDto): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_PORTFOLIO)
                .document(item.id)
                .set(item)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ELIMINAR
    suspend fun deleteItem(id: String): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_PORTFOLIO)
                .document(id)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}