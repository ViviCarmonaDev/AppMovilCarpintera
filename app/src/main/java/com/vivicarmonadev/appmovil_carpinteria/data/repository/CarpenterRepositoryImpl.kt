package com.vivicarmonadev.appmovil_carpinteria.data.repository

import com.vivicarmonadev.appmovil_carpinteria.data.model.CarpenterProfileDto
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDomain
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDto
import com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore.FirestoreCarpenterDataSource
import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpenterProfile
import com.vivicarmonadev.appmovil_carpinteria.domain.repository.CarpenterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del CarpenterRepository usando Firebase.
 *
 * Es el "pegamento" entre la capa de dominio (que solo conoce la interfaz)
 * y la capa de datos (que conoce Firestore).
 *
 * Responsabilidades:
 *  1. Coordinar el FirestoreCarpenterDataSource
 *  2. Convertir entre CarpenterProfileDto (Firestore) y CarpenterProfile (dominio)
 *  3. Envolver errores en Result
 */
class CarpenterRepositoryImpl(
    private val dataSource: FirestoreCarpenterDataSource = FirestoreCarpenterDataSource()
) : CarpenterRepository {

    // FLUJO REACTIVO

    override fun getCarpenterProfile(uid: String): Flow<CarpenterProfile?> {
        return dataSource.getProfileFlow(uid).map { dto ->
            dto?.toDomain()
        }
    }

    // GUARDAR

    override suspend fun saveCarpenterProfile(
        profile: CarpenterProfile
    ): Result<CarpenterProfile> {
        return try {
            val dto = profile.toDto()
            val saveResult = dataSource.saveProfile(dto)

            if (saveResult.isFailure) {
                return Result.failure(
                    saveResult.exceptionOrNull() ?: Exception("Error al guardar el perfil")
                )
            }

            // Devolver el perfil actualizado (con profileCompleted según lo que se guardó)
            Result.success(profile)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // LEER UNA VEZ

    override suspend fun getCarpenterProfileOnce(
        uid: String
    ): Result<CarpenterProfile?> {
        return try {
            val result = dataSource.getProfileOnce(uid)

            if (result.isFailure) {
                return Result.failure(
                    result.exceptionOrNull() ?: Exception("Error al leer el perfil")
                )
            }

            val dto = result.getOrNull()
            Result.success(dto?.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}