package com.hermanowicz.pantry.domain

import com.hermanowicz.pantry.di.repository.CategoryRepository
import com.hermanowicz.pantry.di.repository.ProductRepository
import com.hermanowicz.pantry.di.repository.StorageLocationRepository
import javax.inject.Inject

class ClearDatabaseToFileUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val storageLocationRepository: StorageLocationRepository
) : suspend () -> Unit {
    override suspend fun invoke() {
        productRepository.deleteAllCurrentDatabase()
        categoryRepository.deleteAllCurrentDatabase()
        storageLocationRepository.deleteAllCurrentDatabase()
    }
}