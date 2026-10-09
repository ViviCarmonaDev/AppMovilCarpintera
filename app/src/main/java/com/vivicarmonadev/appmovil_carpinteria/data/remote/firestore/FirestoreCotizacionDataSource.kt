package com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vivicarmonadev.appmovil_carpinteria.data.model.CotizacionDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * DataSource de Firestore para cotizaciones.
 */
class FirestoreCotizacionDataSource {

    private val firestore: FirebaseFirestore = Firebase.firestore

    companion object {
        private const val COLLECTION_COTIZACIONES = "cotizaciones"
    }

    // LECTURA — Cotizaciones de un carpintero
    fun getCotizacionesByCarpenterFlow(carpenterUid: String): Flow<List<CotizacionDto>> = callbackFlow {
        val listener = firestore.collection(COLLECTION_COTIZACIONES)
            .whereEqualTo("carpinteroUid", carpenterUid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val cotizaciones = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CotizacionDto::class.java)
                } ?: emptyList()
                trySend(cotizaciones)
            }
        awaitClose { listener.remove() }
    }

    // LECTURA — Cotizaciones de un pedido
    fun getCotizacionesByPedidoFlow(pedidoId: String): Flow<List<CotizacionDto>> = callbackFlow {
        val listener = firestore.collection(COLLECTION_COTIZACIONES)
            .whereEqualTo("pedidoId", pedidoId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val cotizaciones = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CotizacionDto::class.java)
                } ?: emptyList()
                trySend(cotizaciones)
            }
        awaitClose { listener.remove() }
    }

    // LECTURA — Una cotización por ID
    suspend fun getCotizacionById(id: String): Result<CotizacionDto?> {
        return try {
            val snapshot = firestore.collection(COLLECTION_COTIZACIONES)
                .document(id)
                .get()
                .await()
            if (snapshot.exists()) {
                Result.success(snapshot.toObject(CotizacionDto::class.java))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // CREAR
    suspend fun createCotizacion(cotizacion: CotizacionDto): Result<CotizacionDto> {
        return try {
            val docRef = if (cotizacion.id.isBlank()) {
                firestore.collection(COLLECTION_COTIZACIONES).document()
            } else {
                firestore.collection(COLLECTION_COTIZACIONES).document(cotizacion.id)
            }
            val dtoWithId = cotizacion.copy(id = docRef.id)
            docRef.set(dtoWithId).await()
            Result.success(dtoWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ACTUALIZAR
    suspend fun updateCotizacion(cotizacion: CotizacionDto): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_COTIZACIONES)
                .document(cotizacion.id)
                .set(cotizacion)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ACTUALIZAR SOLO EL ESTADO
    suspend fun updateEstado(cotizacionId: String, nuevoEstado: String): Result<Unit> {
        return try {
            val fields = mapOf(
                "estado" to nuevoEstado,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            firestore.collection(COLLECTION_COTIZACIONES)
                .document(cotizacionId)
                .update(fields)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ELIMINAR
    suspend fun deleteCotizacion(id: String): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_COTIZACIONES)
                .document(id)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}