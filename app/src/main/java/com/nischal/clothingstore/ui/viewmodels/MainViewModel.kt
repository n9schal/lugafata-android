package com.nischal.clothingstore.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.nischal.clothingstore.repositories.MainRepository
import com.nischal.clothingstore.ui.models.Category
import com.nischal.clothingstore.ui.models.HomeCategory
import com.nischal.clothingstore.utils.Resource
import com.nischal.clothingstore.utils.SingleLiveEvent

class MainViewModel(
    private val mainRepository: MainRepository
): ViewModel() {
    val fetchHomePageContentsMediator = MediatorLiveData<Resource<ArrayList<HomeCategory>>>()
    val fetchCategoriesMediator = MediatorLiveData<Resource<ArrayList<Category>>>()

    val viewAllClickedEvent = SingleLiveEvent<HomeCategory>()
    val categoryClickedEvent = SingleLiveEvent<Category>()

    fun fetchHomePageContents(){
        fetchHomePageContentsMediator.addSource(mainRepository.fetchHomePageContents()){
            fetchHomePageContentsMediator.value = it
        }
    }

    fun fetchCategories(){
        fetchCategoriesMediator.addSource(mainRepository.fetchCategories()){
            fetchCategoriesMediator.value = it
        }
    }

    fun getProfileInfoFromPrefs() = mainRepository.getProfileInfoFromPrefs()
    fun isUserLoggedIn() = mainRepository.isUserLoggedIn()
}