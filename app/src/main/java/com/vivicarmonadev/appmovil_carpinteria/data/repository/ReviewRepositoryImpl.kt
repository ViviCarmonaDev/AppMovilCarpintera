package com.vivicarmonadev.appmovil_carpinteria.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vivicarmonadev.appmovil_carpinteria.data.model.ReviewDto
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDomain
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Review
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReviewRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val reviewsCollection = firestore.collection("reviews")

    /**
     * Escucha en tiempo real las reseñas de un carpintero,
     * ordenadas por fecha descendente.
     */
    fun getReviewsByCarpenter(carpinteroUid: String): Flow<List<Review>> = callbackFlow {
        val query = reviewsCollection
            .whereEqualTo("carpinteroUid", carpinteroUid)
            .orderBy("createdAt", Query.Direction.DESCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val reviews = snapshot?.documents
                ?.mapNotNull { it.toObject(ReviewDto::class.java)?.toDomain() }
                ?: emptyList()

            trySend(reviews)
        }

        awaitClose { listener.remove() }
    }

    /**
     * Obtiene las reseñas una sola vez (sin listener).
     */
    suspend fun getReviewsOnce(carpinteroUid: String): Result<List<Review>> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("carpinteroUid", carpinteroUid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val reviews = snapshot.documents
                .mapNotNull { it.toObject(ReviewDto::class.java)?.toDomain() }

            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}