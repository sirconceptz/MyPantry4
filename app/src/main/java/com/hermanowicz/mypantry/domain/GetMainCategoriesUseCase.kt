package com.hermanowicz.mypantry.domain

import com.hermanowicz.mypantry.di.repository.CategoriesRepository
import javax.inject.Inject

class GetMainCategoriesUseCase @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) : () -> Map<String, String> {
    override fun invoke(): Map<String, String> {
        return categoriesRepository.getMainCategories()
    }
}
