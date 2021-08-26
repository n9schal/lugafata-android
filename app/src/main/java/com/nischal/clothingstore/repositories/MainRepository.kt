package com.nischal.clothingstore.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.nischal.clothingstore.HomePageCollectionsQuery
import com.nischal.clothingstore.SearchProductsQuery
import com.nischal.clothingstore.data.prefs.PrefsManager
import com.nischal.clothingstore.type.CollectionFilterParameter
import com.nischal.clothingstore.type.CollectionListOptions
import com.nischal.clothingstore.type.SearchInput
import com.nischal.clothingstore.type.StringOperators
import com.nischal.clothingstore.ui.models.HomeCategory
import com.nischal.clothingstore.ui.models.Product
import com.nischal.clothingstore.utils.Constants
import com.nischal.clothingstore.utils.Constants.ErrorHandlerMessages.GENERIC_ERROR_MESSAGE
import com.nischal.clothingstore.utils.Constants.Strings.APP_NAME
import com.nischal.clothingstore.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainRepository(
    private val prefsManager: PrefsManager,
    private val viewModelScope: CoroutineScope,
    private val apolloClient: ApolloClient
) {

    fun getProfileInfoFromPrefs() = prefsManager.getProfileInfo()
    fun isUserLoggedIn() = !prefsManager.getToken().isNullOrEmpty()

    fun fetchHomePageContents(): LiveData<Resource<ArrayList<HomeCategory>>> {
        val response = MutableLiveData<Resource<ArrayList<HomeCategory>>>()
        viewModelScope.launch {
            try {
                response.postValue(Resource.loading(null))
                val slug = Constants.SlugConstants.HOME_PAGE
                val collectionListOptions = CollectionListOptions(
                    take = Input.fromNullable(null),
                    skip = Input.fromNullable(null),
                    sort = Input.fromNullable(null),
                    filter = Input.fromNullable(
                        CollectionFilterParameter(
                            slug = Input.fromNullable(
                                StringOperators(
                                    contains = Input.fromNullable(slug)
                                )
                            )
                        )
                    )
                )
                val homeCollectionsQueryResult = apolloClient.query(
                    HomePageCollectionsQuery(
                        collectionListOptions = Input.fromNullable(collectionListOptions)
                    )
                ).await()
                if (homeCollectionsQueryResult.data != null) {
                    // * parse result to HomeCategory
                    val homeCategories = HomeCategory.parseToHomeCategories(
                        homeCollectionsQueryResult.data!!
                    )

                    homeCategories.forEachIndexed { index, homeCategory ->
                        val searchInput = SearchInput(
                            collectionId = Input.fromNullable(homeCategory.id),
                            groupByProduct = Input.fromNullable(true)
                        )
                        // * query product list per collection
                        val searchResult = apolloClient.query(
                            SearchProductsQuery(searchInput = searchInput)
                        ).await()
                        // * if searched product list is empty then remove the home category at that index
                        if (searchResult.data != null) {
                            val productList = Product.parseToProductList(searchResult.data!!)
                            homeCategory.productList.clear()
                            homeCategory.productList.addAll(productList)
                        }
                    }
                    val filteredHomeCategories = homeCategories.filter { homeCategory -> homeCategory.productList.isNotEmpty() }
                    response.postValue(Resource.success(filteredHomeCategories as ArrayList<HomeCategory>))
                } else {
                    throw ApolloException(GENERIC_ERROR_MESSAGE)
                }
            } catch (e: ApolloException) {
                response.postValue(
                    Resource.error(
                        msg = e.message.toString(),
                        data = null,
                        title = APP_NAME
                    )
                )
            }
        }
        return response
    }
}