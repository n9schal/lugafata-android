package com.nischal.clothingstore.di

import com.apollographql.apollo.ApolloClient
import com.nischal.clothingstore.data.prefs.PrefsManager
import com.nischal.clothingstore.repositories.MainRepository
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module.module

val repositoryModule = module {
    factory { provideMainRepository(get(), get(), get()) }
}

fun provideMainRepository(
    prefsManager: PrefsManager,
    viewModelScope: CoroutineScope,
    apolloClient: ApolloClient
): MainRepository {
    return MainRepository(
        prefsManager,
        viewModelScope,
        apolloClient
    )
}
