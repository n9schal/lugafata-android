package com.nischal.clothingstore.di

import com.nischal.clothingstore.ui.viewmodels.AuthViewModel
import com.nischal.clothingstore.ui.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { AuthViewModel(get()) }
}