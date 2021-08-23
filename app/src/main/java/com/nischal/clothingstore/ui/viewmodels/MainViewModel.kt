package com.nischal.clothingstore.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.nischal.clothingstore.repositories.MainRepository
import com.nischal.clothingstore.ui.models.HomeCategory
import com.nischal.clothingstore.utils.SingleLiveEvent

class MainViewModel(
    private val mainRepository: MainRepository
): ViewModel() {
    val viewAllClickedEvent = SingleLiveEvent<HomeCategory>()

    fun getProfileInfoFromPrefs() = mainRepository.getProfileInfoFromPrefs()
    fun isUserLoggedIn() = mainRepository.isUserLoggedIn()
}