package com.nischal.clothingstore.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.nischal.clothingstore.repositories.MainRepository
import com.nischal.clothingstore.ui.models.*
import com.nischal.clothingstore.utils.Resource
import com.nischal.clothingstore.utils.SingleLiveEvent

class MainViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {
    val fetchHomePageContentsMediator = MediatorLiveData<Resource<ArrayList<HomeCategory>>>()
    val fetchCategoriesMediator = MediatorLiveData<Resource<ArrayList<Category>>>()
    val fetchSubCategoriesMediator = MediatorLiveData<Resource<ArrayList<Product>>>()
    val fetchSearchedProductsMediator = MediatorLiveData<Resource<SearchResponse>>()
    val fetchProductDetailsMediator = MediatorLiveData<Resource<Product>>()

    val viewAllClickedEvent = SingleLiveEvent<HomeCategory>()
    val categoryClickedEvent = SingleLiveEvent<Category>()
    val productClickedEvent = SingleLiveEvent<Product>()
    val productDeleteClicked = SingleLiveEvent<ProductVariant>()

    fun fetchHomePageContents() {
        fetchHomePageContentsMediator.addSource(mainRepository.fetchHomePageContents()) {
            fetchHomePageContentsMediator.value = it
        }
    }

    fun fetchCategories() {
        fetchCategoriesMediator.addSource(mainRepository.fetchCategories()) {
            fetchCategoriesMediator.value = it
        }
    }

    fun fetchSubCategories(
        currentPage: Int,
        pageSize: Int,
        subCategoryId: String
    ) {
        fetchSubCategoriesMediator.addSource(
            mainRepository.fetchSubCategories(
                currentPage,
                pageSize,
                subCategoryId
            )
        ) {
            fetchSubCategoriesMediator.value = it
        }
    }

    fun fetchSearchedProducts(
        currentPage: Int,
        pageSize: Int,
        term: String
    ) {
        fetchSearchedProductsMediator.addSource(
            mainRepository.fetchSearchedProducts(
                currentPage,
                pageSize,
                term
            )
        ) {
            fetchSearchedProductsMediator.value = it
        }
    }

    fun fetchProductDetail(
        id: String,
        slug: String
    ) {
        fetchProductDetailsMediator.addSource(
            mainRepository.fetchProductDetails(
                id,
                slug
            )
        ) {
            fetchProductDetailsMediator.value = it
        }
    }

    fun updateShoppingListInDb(productVariant: ProductVariant) {
        mainRepository.updateShoppingList(productVariant)
    }

    fun getShoppingListFromDb() = mainRepository.getShoppingList()
    fun deleteShoppingListItemFromDb(productVariant: ProductVariant) =
        mainRepository.deleteShoppingListItem(productVariant)

    fun getProfileInfoFromPrefs() = mainRepository.getProfileInfoFromPrefs()
    fun isUserLoggedIn() = mainRepository.isUserLoggedIn()
}