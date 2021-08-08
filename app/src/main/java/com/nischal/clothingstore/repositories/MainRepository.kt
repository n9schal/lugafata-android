package com.nischal.clothingstore.repositories

import com.apollographql.apollo.ApolloClient
import com.nischal.clothingstore.data.prefs.PrefsManager
import kotlinx.coroutines.CoroutineScope

class MainRepository(
    private val prefsManager: PrefsManager,
    private val viewModelScope: CoroutineScope,
    private val apolloClient: ApolloClient
) {
}