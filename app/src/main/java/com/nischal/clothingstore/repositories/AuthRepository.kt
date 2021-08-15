package com.nischal.clothingstore.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.nischal.clothingstore.ActiveCustomerQuery
import com.nischal.clothingstore.data.prefs.PrefsManager
import com.nischal.clothingstore.type.BooleanOperators
import com.nischal.clothingstore.type.OrderFilterParameter
import com.nischal.clothingstore.type.OrderListOptions
import com.nischal.clothingstore.type.StringOperators
import com.nischal.clothingstore.ui.models.UserDetails
import com.nischal.clothingstore.utils.Constants.ErrorHandlerMessages.GENERIC_ERROR_MESSAGE
import com.nischal.clothingstore.utils.Constants.Strings.APP_NAME
import com.nischal.clothingstore.utils.Constants.VendureOrderStates.ADDING_ITEMS
import com.nischal.clothingstore.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AuthRepository(
    private val prefsManager: PrefsManager,
    private val viewModelScope: CoroutineScope,
    private val apolloClient: ApolloClient
) {
    fun activeCustomerQuery(): LiveData<Resource<ActiveCustomerQuery.Data>> {
        val response = MediatorLiveData<Resource<ActiveCustomerQuery.Data>>()
        viewModelScope.launch {
            try {
                response.postValue(Resource.loading(null))
                val options = OrderListOptions(
                    filter = Input.fromNullable(
                        OrderFilterParameter(
                            state = Input.fromNullable(
                                StringOperators(
                                    eq = Input.fromNullable(ADDING_ITEMS)
                                )
                            ),
                            active = Input.fromNullable(
                                BooleanOperators(
                                    eq = Input.fromNullable(true)
                                )
                            )
                        )
                    ),
                    take = Input.fromNullable(1)
                )
                val result =
                    apolloClient.query(ActiveCustomerQuery(options = Input.fromNullable(options)))
                        .await()
                if (result.data != null && result.data?.activeCustomer != null) {
                    // * store user details in pref
                    val profile: UserDetails =
                        UserDetails.parseToUserDetails(result.data!!.activeCustomer!!)
                    prefsManager.setProfileInfo(profile)
                    // * store active order in db
//                    if(result.data!!.activeCustomer!!.orders.totalItems > 0){
//                        val orderedProducts = Product.parseToProductList(result.data!!.activeCustomer!!.orders)
//                        shoppingListDao.deleteAllShoppingList()
//                        shoppingListDao.insertProducts(orderedProducts)
//                    }
                    response.postValue(Resource.success(result.data))
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

    fun clearPreferences() = prefsManager.clearData()
}