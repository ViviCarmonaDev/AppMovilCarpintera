package com.vivicarmonadev.appmovil_carpinteria.domain.repository

import com.vivicarmonadev.appmovil_carpinteria.domain.model.CarpenterProfile
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio del perfil del carpintero.
 *
 * Define QUÉ se puede hacer con el perfil del taller, pero no CÓMO.
 * El "cómo" lo implementa CarpenterRepositoryImpl en la capa data.
 */
interface CarpenterRepository {

    // Flujo reactivo del perfil del carpintero. Emite null cuando no existe, o el profile cuando sí.

    fun getCarpenterProfile(uid: String): Flow<CarpenterProfile?>

    // Guarda el perfil del carpintero en Firestore. Crea el documento si no existe, lo actualiza si ya existe.

    suspend fun saveCarpenterProfile(profile: CarpenterProfile): Result<CarpenterProfile>

     // Lee el perfil del carpintero una sola vez (sin escuchar cambios). Devuelve null si no existe.

    suspend fun getCarpenterProfileOnce(uid: String): Result<CarpenterProfile?>
}