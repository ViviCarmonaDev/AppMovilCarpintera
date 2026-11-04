package com.vivicarmonadev.appmovil_carpinteria.data.repository

import com.vivicarmonadev.appmovil_carpinteria.data.model.toDomain
import com.vivicarmonadev.appmovil_carpinteria.data.model.toDto
import com.vivicarmonadev.appmovil_carpinteria.data.remote.firestore.FirestoreMaterialDataSource
import com.vivicarmonadev.appmovil_carpinteria.domain.model.Material
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MaterialRepositoryImpl(
    private val dataSource: FirestoreMaterialDataSource = FirestoreMaterialDataSource()
) {
    fun getAllMaterials(): Flow<List<Material>> {
        return dataSource.getAllMaterialsFlow().map { list -> list.map { it.toDomain() } }
    }

    fun getMaterialesByCategoria(categoriaId: String): Flow<List<Material>> {
        return dataSource.getMaterialesByCategoriaFlow(categoriaId).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun getMaterialById(id: String): Result<Material?> {
        return dataSource.getMaterialById(id).map { it?.toDomain() }
    }

    suspend fun createMaterial(material: Material): Result<Material> {
        return dataSource.createMaterial(material.toDto()).map { it.toDomain() }
    }

    suspend fun updateMaterial(material: Material): Result<Unit> {
        return dataSource.updateMaterial(material.toDto())
    }

    suspend fun deleteMaterial(id: String): Result<Unit> {
        return dataSource.deleteMaterial(id)
    }
}