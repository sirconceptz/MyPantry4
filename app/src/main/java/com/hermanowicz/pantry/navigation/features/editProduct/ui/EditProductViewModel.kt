package com.hermanowicz.pantry.navigation.features.editProduct.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermanowicz.pantry.data.model.Product
import com.hermanowicz.pantry.domain.settings.ObserveDatabaseModeUseCase
import com.hermanowicz.pantry.domain.category.GetDetailsCategoriesUseCase
import com.hermanowicz.pantry.domain.product.GetGroupProductByIdUseCase
import com.hermanowicz.pantry.domain.category.GetMainCategoriesUseCase
import com.hermanowicz.pantry.domain.category.ObserveAllOwnCategoriesUseCase
import com.hermanowicz.pantry.domain.product.ObserveAllProductsUseCase
import com.hermanowicz.pantry.domain.product.UpdateProductsUseCase
import com.hermanowicz.pantry.navigation.features.editProduct.state.EditProductDataState
import com.hermanowicz.pantry.navigation.features.newProduct.state.NewProductUiState
import com.hermanowicz.pantry.utils.DateAndTimeConverter
import com.hermanowicz.pantry.utils.DatePickerData
import com.hermanowicz.pantry.utils.RegexFormats
import com.hermanowicz.pantry.utils.category.MainCategories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val observeAllProductsUseCase: ObserveAllProductsUseCase,
    private val getGroupProductByIdUseCase: GetGroupProductByIdUseCase,
    private val updateProductsUseCase: UpdateProductsUseCase,
    private val getMainCategoriesUseCase: GetMainCategoriesUseCase,
    private val getDetailsCategoriesUseCase: GetDetailsCategoriesUseCase,
    private val observeAllOwnCategoriesUseCase: ObserveAllOwnCategoriesUseCase,
    private val observeDatabaseModeUseCase: ObserveDatabaseModeUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val fetchDatabaseModeUseCase: ObserveDatabaseModeUseCase
) : ViewModel() {
    private val _productDataState = MutableStateFlow(EditProductDataState())
    var productDataState: StateFlow<EditProductDataState> = _productDataState.asStateFlow()

    private val stringId: String = savedStateHandle["id"] ?: "0"
    private val productId = stringId.toInt()

    init {
        fetchOwnCategories()
        fetchProductData(productId)
    }

    private fun fetchOwnCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            observeDatabaseModeUseCase().collect { databaseMode ->
                observeAllOwnCategoriesUseCase(databaseMode).collect { ownCategories ->
                    _productDataState.update { it.copy(ownCategories = ownCategories) }
                }
            }
        }
    }

    private fun fetchProductData(productId: Int) {
        viewModelScope.launch {
            fetchDatabaseModeUseCase().collect { databaseMode ->
                observeAllProductsUseCase(databaseMode).collect { products ->
                    val groupProduct = getGroupProductByIdUseCase(productId, products)
                    _productDataState.value = EditProductDataState(
                        name = groupProduct.product.name,
                        expirationDate = groupProduct.product.expirationDate,
                        productionDate = groupProduct.product.productionDate,
                        composition = groupProduct.product.composition,
                        oldQuantity = groupProduct.quantity.toString(),
                        newQuantity = groupProduct.quantity.toString(),
                        healingProperties = groupProduct.product.healingProperties,
                        dosage = groupProduct.product.dosage,
                        hasSugar = groupProduct.product.hasSugar,
                        hasSalt = groupProduct.product.hasSalt,
                        isVege = groupProduct.product.isVege,
                        isBio = groupProduct.product.isBio,
                        weight = groupProduct.product.weight.toString(),
                        volume = groupProduct.product.volume.toString(),
                        taste = groupProduct.product.taste,
                        hashCode = groupProduct.product.hashCode,
                        productsIdList = groupProduct.idList
                    )
                }
            }
        }
    }

    fun onSaveClick() {
        if (productDataState.value.name.length < 3 || productDataState.value.name.length > 40)
            _productDataState.update { it.copy(showErrorWrongName = true) }
        else {
            updateProducts()
            cleanErrors()
        }
    }

    private fun cleanErrors() {
        _productDataState.update { it.copy(showErrorWrongName = false) }
    }

    private fun updateProducts() {
        var mainCategory = ""
        var detailCategory = ""
        if (productDataState.value.mainCategory != MainCategories.CHOOSE.name)
            mainCategory = productDataState.value.mainCategory
        if (productDataState.value.detailCategory != MainCategories.CHOOSE.name)
            detailCategory = productDataState.value.detailCategory
        val product = Product(
            id = productId,
            name = productDataState.value.name,
            mainCategory = mainCategory,
            detailCategory = detailCategory,
            expirationDate = productDataState.value.expirationDate,
            productionDate = productDataState.value.productionDate,
            composition = productDataState.value.composition,
            healingProperties = productDataState.value.healingProperties,
            dosage = productDataState.value.dosage,
            hasSugar = productDataState.value.hasSugar,
            hasSalt = productDataState.value.hasSalt,
            isVege = productDataState.value.isVege,
            isBio = productDataState.value.isBio,
            weight = productDataState.value.weight.toIntOrNull() ?: 0,
            volume = productDataState.value.volume.toIntOrNull() ?: 0,
            taste = productDataState.value.taste,
            hashCode = productDataState.value.hashCode
        )
        viewModelScope.launch(Dispatchers.IO) {
            updateProductsUseCase(
                product,
                productDataState.value.productsIdList,
                productDataState.value.oldQuantity.toIntOrNull() ?: 1,
                productDataState.value.newQuantity.toIntOrNull() ?: 1
            )
        }
        onNavigateToMyPantry(true)
    }

    fun onNavigateToMyPantry(bool: Boolean) {
        _productDataState.update { it.copy(onNavigateBack = bool) }
    }

    fun onNameChange(name: String) {
        _productDataState.update { it.copy(name = name) }
    }

    fun onExpirationDateChange(expirationDatePickerData: DatePickerData) {
        _productDataState.update {
            it.copy(
                expirationDatePickerData = expirationDatePickerData,
                expirationDate = DateAndTimeConverter.getDateToDbFromDatePickerData(
                    expirationDatePickerData
                )
            )
        }
    }

    fun onProductionDateChange(productionDatePickerData: DatePickerData) {
        _productDataState.update {
            it.copy(
                productionDatePickerData = productionDatePickerData,
                productionDate = DateAndTimeConverter.getDateToDbFromDatePickerData(
                    productionDatePickerData
                )
            )
        }
    }

    fun onQuantityChange(quantity: String) {
        if (quantity.matches(RegexFormats.NUMBER.regex) || quantity.isEmpty()) _productDataState.update {
            it.copy(
                newQuantity = quantity
            )
        }
    }

    fun onCompositionChange(composition: String) {
        _productDataState.update { it.copy(composition = composition) }
    }

    fun onHealingPropertiesChange(healingProperties: String) {
        _productDataState.update { it.copy(healingProperties = healingProperties) }
    }

    fun onDosageChange(dosage: String) {
        _productDataState.update { it.copy(dosage = dosage) }
    }

    fun onWeightChange(weight: String) {
        if (weight.matches(RegexFormats.NUMBER.regex) || productDataState.value.weight.isEmpty()) _productDataState.update {
            it.copy(
                weight = weight
            )
        }
    }

    fun onVolumeChange(volume: String) {
        if (volume.matches(RegexFormats.NUMBER.regex) || productDataState.value.volume.isEmpty()) _productDataState.update {
            it.copy(
                volume = volume
            )
        }
    }

    fun onIsVegeChange(isVege: Boolean) {
        _productDataState.update { it.copy(isVege = isVege) }
    }

    fun onIsBioChange(isBio: Boolean) {
        _productDataState.update { it.copy(isBio = isBio) }
    }

    fun onHasSugarChange(hasSugar: Boolean) {
        _productDataState.update { it.copy(hasSugar = hasSugar) }
    }

    fun onHasSaltChange(hasSalt: Boolean) {
        _productDataState.update { it.copy(hasSalt = hasSalt) }
    }

    fun showMainCategoryDropdown(show: Boolean) {
        _productDataState.update { it.copy(showMainCategoryDropdown = show) }
    }

    fun showDetailCategoryDropdown(show: Boolean) {
        _productDataState.update { it.copy(showDetailCategoryDropdown = show) }
    }

    fun onMainCategoryChange(mainCategory: String) {
        _productDataState.update {
            it.copy(
                mainCategory = mainCategory, showMainCategoryDropdown = false
            )
        }
    }

    fun onDetailCategoryChange(detailCategory: String) {
        _productDataState.update {
            it.copy(
                detailCategory = detailCategory, showDetailCategoryDropdown = false
            )
        }
    }

    fun getMainCategories(): Map<String, String> {
        return getMainCategoriesUseCase()
    }

    fun getDetailCategories(): Map<String, String> {
        return getDetailsCategoriesUseCase(
            productDataState.value.ownCategories, productDataState.value.mainCategory
        )
    }

    fun onTasteSelect(taste: String) {
        _productDataState.update {
            it.copy(
                taste = taste
            )
        }
    }
}
