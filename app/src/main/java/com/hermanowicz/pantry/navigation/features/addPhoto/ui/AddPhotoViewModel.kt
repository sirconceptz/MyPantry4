package com.hermanowicz.pantry.navigation.features.addPhoto.ui

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermanowicz.pantry.domain.photo.CreateAndGetPhotoFileUseCase
import com.hermanowicz.pantry.domain.photo.DecodePhotoFromGalleryUseCase
import com.hermanowicz.pantry.domain.photo.FetchPhotoBitmapUseCase
import com.hermanowicz.pantry.domain.photo.GetPhotoFileNameUseCase
import com.hermanowicz.pantry.domain.product.GetProductListByIdsProductsUseCase
import com.hermanowicz.pantry.domain.settings.ObserveDatabaseModeUseCase
import com.hermanowicz.pantry.domain.photo.SetPhotoFileUseCase
import com.hermanowicz.pantry.domain.product.UpdatePhotoInProductListUseCase
import com.hermanowicz.pantry.navigation.features.addPhoto.state.AddPhotoUiState
import com.hermanowicz.pantry.utils.enums.DatabaseMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddPhotoViewModel @Inject constructor(
    private val getProductListByIdsProductsUseCase: GetProductListByIdsProductsUseCase,
    private val observeDatabaseModeUseCase: ObserveDatabaseModeUseCase,
    private val createAndGetPhotoFileUseCase: CreateAndGetPhotoFileUseCase,
    private val getPhotoFileNameUseCase: GetPhotoFileNameUseCase,
    private val updatePhotoInProductListUseCase: UpdatePhotoInProductListUseCase,
    private val decodePhotoFromGalleryUseCase: DecodePhotoFromGalleryUseCase,
    private val fetchPhotoBitmapUseCase: FetchPhotoBitmapUseCase,
    private val setPhotoFileUseCase: SetPhotoFileUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPhotoUiState())
    var uiState: StateFlow<AddPhotoUiState> = _uiState.asStateFlow()

    lateinit var databaseMode: DatabaseMode

    init {
        val savedProductIdList = savedStateHandle["productIdList"] ?: "0"
        val productIdArray = savedProductIdList.split(";")
        val productIdList = productIdArray.map { it.toInt() }
        fetchProducts(productIdList)
    }

    private fun fetchProducts(productIdList: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                observeDatabaseModeUseCase().collect { databaseMode ->
                    this@AddPhotoViewModel.databaseMode = databaseMode
                    getProductListByIdsProductsUseCase(
                        databaseMode,
                        productIdList
                    ).collect { products ->
                        _uiState.update {
                            it.copy(
                                productList = products
                            )
                        }
                        if (products.isNotEmpty()) {
                            val fileName = products[0].photoName
                            setPhotoFileUseCase(fileName)
                            val photoBitmap = fetchPhotoBitmapUseCase(fileName, databaseMode)
                            setPhotoPreview(photoBitmap)
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        productList = emptyList(),
                        onNavigateBack = true
                    )
                }
            }
        }
    }

    fun createAndGetPhotoFile(): File? {
        return createAndGetPhotoFileUseCase()
    }

    private fun setPhotoPreview(bitmap: Bitmap?) {
        _uiState.update {
            it.copy(photoPreview = bitmap)
        }
    }

    fun onGoToPermissionSettings(bool: Boolean) {
        _uiState.update {
            it.copy(goToPermissionSettings = bool)
        }
    }

    fun onClickAddPhoto(bool: Boolean) {
        _uiState.update {
            it.copy(onClickAddPhoto = bool)
        }
    }

    fun onNavigateBack(bool: Boolean) {
        _uiState.update {
            it.copy(onNavigateBack = bool)
        }
    }

    fun savePhotoToDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            val photoFileName = getPhotoFileNameUseCase() ?: ""
            updatePhotoInProductListUseCase(photoFileName, uiState.value.productList, databaseMode)
        }
    }

    fun onPhotoSavedCorrectlyInGallery(photoFileName: String?) {
        if (photoFileName != null) {
            val bitmap = decodePhotoFromGalleryUseCase(photoFileName)
            setPhotoPreview(bitmap)
        }
    }

    fun getPhotoFileName(): String? {
        return getPhotoFileNameUseCase()
    }
}