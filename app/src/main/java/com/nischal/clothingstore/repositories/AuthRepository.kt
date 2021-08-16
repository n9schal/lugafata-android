package com.nischal.clothingstore.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloExperimental
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.http.OkHttpExecutionContext
import com.nischal.clothingstore.ActiveCustomerQuery
import com.nischal.clothingstore.LoginMutation
import com.nischal.clothingstore.RegisterCustomerAccountMutation
import com.nischal.clothingstore.RequestPasswordResetMutation
import com.nischal.clothingstore.data.prefs.PrefsManager
import com.nischal.clothingstore.type.*
import com.nischal.clothingstore.ui.models.LoginRequest
import com.nischal.clothingstore.ui.models.RegisterRequest
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

    @ApolloExperimental
    fun loginMutation(loginRequest: LoginRequest): LiveData<Resource<LoginMutation.Data>> {
        val response = MutableLiveData<Resource<LoginMutation.Data>>()
        viewModelScope.launch {
            try {
                response.postValue(Resource.loading(null))
                val result = apolloClient
                    .mutate(
                        LoginMutation(
                            username = loginRequest.email,
                            password = loginRequest.password,
                            rememberMe = Input.fromNullable(loginRequest.rememberMe)
                        )
                    )
                    .await()
                if (result.data == null) {
                    throw ApolloException(GENERIC_ERROR_MESSAGE)
                }
                result.data?.login?.asCurrentUser?.let {
                    val userInfo = UserDetails(
                        id = it.id,
                        email = it.identifier
                    )
                    // * aquire token from header
                    val httpContext = result.executionContext[OkHttpExecutionContext.KEY]
                    val token = httpContext?.response?.headers?.get("vendure-auth-token") ?: ""
                    prefsManager.setProfileInfo(userInfo)
                    prefsManager.setToken(token)
                    response.postValue(Resource.success(result.data))
                }
                result.data?.login?.asInvalidCredentialsError?.let {
                    throw ApolloException(it.message)
                }
                result.data?.login?.asNotVerifiedError?.let {
                    throw ApolloException(it.message)
                }
                result.data?.login?.asNativeAuthStrategyError?.let {
                    throw ApolloException(it.message)
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

    fun registerCustomerAccountMutation(
        registerRequest: RegisterRequest
    ): LiveData<Resource<LoginRequest>> {
        val response = MutableLiveData<Resource<LoginRequest>>()
        viewModelScope.launch {
            try {
                response.postValue(Resource.loading(null))

                val registerCustomerInput = RegisterCustomerInput(
                    emailAddress = registerRequest.email,
                    title = Input.fromNullable(registerRequest.title),
                    firstName = Input.fromNullable(registerRequest.firstName),
                    lastName = Input.fromNullable(registerRequest.lastName),
                    phoneNumber = Input.fromNullable(registerRequest.mobileNumber),
                    password = Input.fromNullable(registerRequest.password)
                )
                val result = apolloClient
                    .mutate(RegisterCustomerAccountMutation(input = registerCustomerInput))
                    .await()
                //todo - returned union data type could be resolved easily via "__typename" filed
                if (result.data == null) {
                    throw ApolloException(GENERIC_ERROR_MESSAGE)
                }
                result.data?.registerCustomerAccount?.asMissingPasswordError?.let {
                    throw ApolloException(it.message)
                }
                result.data?.registerCustomerAccount?.asNativeAuthStrategyError?.let {
                    throw ApolloException(it.message)
                }
                result.data?.registerCustomerAccount?.asSuccess?.let {
                    if (it.success) {
                        response.postValue(
                            Resource.success(
                                LoginRequest(
                                    email = registerRequest.email,
                                    password = registerRequest.password
                                )
                            )
                        )
                    } else {
                        throw ApolloException(GENERIC_ERROR_MESSAGE)
                    }
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

    fun requestPasswordReset(email: String): LiveData<Resource<Any?>> {
        val response = MutableLiveData<Resource<Any?>>()
        viewModelScope.launch {
            try {
                response.postValue(Resource.loading(null))
                val result = apolloClient.mutate(
                    RequestPasswordResetMutation(
                        emailAddress = email
                    )
                ).await()
                result.data?.requestPasswordReset?.asNativeAuthStrategyError?.let {
                    throw ApolloException(it.message)
                }
                result.data?.requestPasswordReset?.asSuccess?.let {
                    response.postValue(Resource.success(null))
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