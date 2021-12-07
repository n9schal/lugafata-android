package com.nischal.clothingstore.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.nischal.clothingstore.*
import com.nischal.clothingstore.data.db.dao.ShoppingListDao
import com.nischal.clothingstore.data.prefs.PrefsManager
import com.nischal.clothingstore.type.*
import com.nischal.clothingstore.ui.models.*
import com.nischal.clothingstore.utils.Constants
import com.nischal.clothingstore.utils.Constants.ErrorHandlerMessages.GENERIC_ERROR_MESSAGE
import com.nischal.clothingstore.utils.Constants.SlugConstants.CATEGORIES
import com.nischal.clothingstore.utils.Constants.Strings.APP_NAME
import com.nischal.clothingstore.utils.Constants.VendureOrderStates.ADDING_ITEMS
import com.nischal.clothingstore.utils.Constants.VendureOrderStates.ARRANGING_PAYMENT
import com.nischal.clothingstore.utils.Constants.VendurePaymentMethods.PAYMENT_ON_DELIVERY
import com.nischal.clothingstore.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainRepository(
    private val prefsManager: PrefsManager,
    private val viewModelScope: CoroutineScope,
    private val apolloClient: ApolloClient,
    private val shoppingListDao: ShoppingListDao
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
                    val filteredHomeCategories =
                        homeCategories.filter { homeCategory -> homeCategory.productList.isNotEmpty() }
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

    fun fetchCategories(): LiveData<Resource<ArrayList<Category>>> {
        val response = MutableLiveData<Resource<ArrayList<Category>>>()
        viewModelScope.launch {
            try {
                response.postValue(Resource.loading(null))
                val collectionListOptions = CollectionListOptions(
                    take = Input.fromNullable(null),
                    skip = Input.fromNullable(null),
                    sort = Input.fromNullable(null),
                    filter = Input.fromNullable(
                        CollectionFilterParameter(
                            slug = Input.fromNullable(
                                StringOperators(contains = Input.fromNullable(CATEGORIES))
                            )
                        )
                    )
                )
                val apolloResult = apolloClient.query(
                    CategoryCollectionsQuery(
                        collectionListOptions = Input.fromNullable(collectionListOptions)
                    )
                ).await()
                if (apolloResult.data != null) {
                    val categories = Category.parseToCategories(apolloResult.data!!)
                    response.postValue(Resource.success(categories))
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

    fun fetchSubCategories(
        currentPage: Int,
        pageSize: Int,
        subCategoryId: String
    ): LiveData<Resource<ArrayList<Product>>> {
        val response = MutableLiveData<Resource<ArrayList<Product>>>()
        viewModelScope.launch {
            try {
                response.postValue(Resource.loading(null))
                val skip = currentPage * pageSize
                val searchInput = SearchInput(
                    skip = Input.fromNullable(skip),
                    take = Input.fromNullable(pageSize),
                    collectionId = Input.fromNullable(subCategoryId),
                    groupByProduct = Input.fromNullable(true)
                )
                val searchResult = apolloClient.query(
                    SearchProductsQuery(searchInput = searchInput)
                ).await()
                if (searchResult.data != null) {
                    val productList = Product.parseToProductList(searchResult.data!!)
                    response.postValue(Resource.success(productList))
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

    fun fetchSearchedProducts(
        currentPage: Int,
        pageSize: Int,
        term: String
    ): LiveData<Resource<SearchResponse>> {
        val response = MutableLiveData<Resource<SearchResponse>>()
        viewModelScope.launch {
            try {
                response.postValue(Resource.loading(null))
                val skip = currentPage * pageSize
                val searchInput = SearchInput(
                    skip = Input.fromNullable(skip),
                    take = Input.fromNullable(pageSize),
                    term = Input.fromNullable(term),
                    groupByProduct = Input.fromNullable(true)
                )
                val searchResult = apolloClient.query(
                    SearchProductsQuery(searchInput = searchInput)
                ).await()
                if (searchResult.data != null) {
                    val productList = Product.parseToProductList(searchResult.data!!)
                    val searchResponse = SearchResponse(
                        products = productList,
                        facetValues = searchResult.data!!.search.facetValues
                    )
                    response.postValue(Resource.success(searchResponse))
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


    // * query product details for updating product variants and option groups only
    // * this query is not for already available values like product price and image assets
    fun fetchProductDetails(
        id: String,
        slug: String
    ): LiveData<Resource<Product>> {
        val response = MutableLiveData<Resource<Product>>()
        viewModelScope.launch {
            try {
                response.postValue(Resource.loading(null))
                val productResult = apolloClient.query(
                    ProductQuery(id = Input.fromNullable(id), slug = Input.fromNullable(slug))
                ).await()
                if(productResult.data != null && productResult.data?.product != null){
                    val product = Product.parseToProduct(productResult.data!!.product!!)
                    response.postValue(Resource.success(product))
                }else{
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

    /**
     * ============================================================================================================
     * Order related operations
     * ============================================================================================================
     * */

    fun activeOrderQuery(): LiveData<Resource<ActiveOrderQuery.ActiveOrder>> {
        val response = MutableLiveData<Resource<ActiveOrderQuery.ActiveOrder>>()
        viewModelScope.launch {
            try {
                response.postValue(Resource.loading(null))
                val result =
                    apolloClient.query(ActiveOrderQuery()).await()
                response.postValue(Resource.success(result.data?.activeOrder))
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

    fun proceedToCheckoutOperation(
        shoppingCartItems: ArrayList<ProductVariant>
    ): LiveData<Resource<Any?>> {
        val response = MutableLiveData<Resource<Any?>>()
        viewModelScope.launch {
            try {
                response.postValue(Resource.loading(null))
                val activeOrderQueryResult =
                    apolloClient.query(ActiveOrderQuery()).await()
                // * if active order is null, just start placing orders
                if (activeOrderQueryResult.data?.activeOrder == null) {
                    addItemsToOrder(shoppingCartItems)
                }
                activeOrderQueryResult.data?.activeOrder?.let {
                    when (it.state) {
                        ADDING_ITEMS -> {
                            // * clear out previously added orderlines first
                            removeAllOrderLines()
                            // * then, add items to order
                            addItemsToOrder(shoppingCartItems)
                        }
                        ARRANGING_PAYMENT -> {
                            // * first transition to Adding items state
                            transitionOrderState(ADDING_ITEMS)
                            // * clear out previously added orderlines first
                            removeAllOrderLines()
                            // * then, add items to order
                            addItemsToOrder(shoppingCartItems)
                        }
                    }
                }
                response.postValue(Resource.success(null))
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

    fun placeOrderOperation(orderDetails: OrderDetails): LiveData<Resource<Any?>> {
        val response = MutableLiveData<Resource<Any?>>()
        viewModelScope.launch {
            try {
                response.postValue(Resource.loading(null))
                when (orderDetails.vendureOrderState) {
                    ADDING_ITEMS -> {
                        // * change state to ARRANGING_PAYMENT first
                        transitionOrderState(ARRANGING_PAYMENT)
                        // * set shipping address
                        setOrderShippingAddress(orderDetails.deliveryLocation!!)
                        // * then add payment method
                        addPaymentMethodToOrder()
                    }
                    ARRANGING_PAYMENT -> {
                        // * set shipping address
                        setOrderShippingAddress(orderDetails.deliveryLocation!!)
                        // * then add payment method
                        addPaymentMethodToOrder()
                    }
                }
                response.postValue(Resource.success(null))
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

    /**
     * method that adds items to order
     * @param
     * */
    private suspend fun addItemsToOrder(
        shoppingCartItems: ArrayList<ProductVariant>
    ){
        shoppingCartItems.forEach { productVariant ->
            val result = apolloClient.mutate(
                AddItemToOrderMutation(
                    productVariantId = productVariant.productVariantId,
                    quantity = productVariant.qtyInCart
                )
            ).await()

            result.data?.addItemToOrder?.asOrderModificationError?.let {
                throw ApolloException(it.message)
            }
            result.data?.addItemToOrder?.asNegativeQuantityError?.let {
                throw ApolloException(it.message)
            }
            result.data?.addItemToOrder?.asOrderLimitError?.let {
                throw ApolloException(it.message)
            }
            result.data?.addItemToOrder?.asInsufficientStockError?.let {
                // * update shoppinglist with available stock
                if (it.quantityAvailable > 0) {
                    productVariant.qtyInCart = it.quantityAvailable
                    shoppingListDao.upsert(productVariant)
                }
                throw ApolloException(it.message)
            }
            result.data?.addItemToOrder?.asOrder?.let {
                // todo do some operation if needed
            }
        }
    }

    private suspend fun removeAllOrderLines() {
        val result =
            apolloClient.mutate(RemoveAllOrderLinesMutation()).await()
        result.data?.removeAllOrderLines?.asOrderModificationError?.let {
            throw ApolloException(it.message)
        }
        if (result.data?.removeAllOrderLines?.asOrder == null) {
            throw ApolloException("Something went wrong!")
        }
    }

    private suspend fun transitionOrderState(state: String) {
        val result =
            apolloClient.mutate(TransitionOrderToStateMutation(state = state)).await()
        result.data?.transitionOrderToState?.asOrderStateTransitionError?.let {
            throw ApolloException(it.message)
        }
        if (result.data?.transitionOrderToState?.asOrder == null) {
            throw ApolloException("Something went wrong!")
        }
    }

    private suspend fun setOrderShippingAddress(location: Location) {
        val input = CreateAddressInput(
            streetLine1 = location.savedLocationName,
            city = Input.fromNullable(location.city),
            countryCode = "NP"
        )

        val result =
            apolloClient.mutate(SetOrderShippingAddressMutation(input = input)).await()

        result.data?.setOrderShippingAddress?.asNoActiveOrderError?.let {
            throw ApolloException(it.message)
        }
        result.data?.setOrderShippingAddress?.asOrder?.let {
            //todo do some operation if need be
        }
    }

    private suspend fun addPaymentMethodToOrder() {
        val input = PaymentInput(
            method = PAYMENT_ON_DELIVERY,
            metadata = ""
        )
        val result = apolloClient.mutate(AddPaymentToOrderMutation(input = input)).await()
        result.data?.addPaymentToOrder?.asOrderPaymentStateError?.let {
            throw ApolloException(it.message)
        }
        result.data?.addPaymentToOrder?.asIneligiblePaymentMethodError?.let {
            throw ApolloException(it.message)
        }
        result.data?.addPaymentToOrder?.asPaymentFailedError?.let {
            throw ApolloException(it.message)
        }
        result.data?.addPaymentToOrder?.asPaymentDeclinedError?.let {
            throw ApolloException(it.message)
        }
        result.data?.addPaymentToOrder?.asOrderStateTransitionError?.let {
            throw ApolloException(it.message)
        }
        result.data?.addPaymentToOrder?.asNoActiveOrderError?.let {
            throw ApolloException(it.message)
        }
        result.data?.addPaymentToOrder?.asOrder?.let {
            //todo do some operation if need be
        }
    }

    /**
     * ============================================================================================================
     * Database Operations
     * ============================================================================================================
     * */

    fun updateShoppingList(productVariant: ProductVariant) {
        viewModelScope.launch {
            shoppingListDao.upsert(productVariant)
        }
    }

    fun deleteShoppingListItem(productVariant: ProductVariant) {
        viewModelScope.launch {
            shoppingListDao.deleteShoppingListItem(productVariant)
        }
    }

    fun clearShoppingList() {
        viewModelScope.launch {
            shoppingListDao.deleteAllShoppingList()
        }
    }

    fun getShoppingList() = shoppingListDao.getShoppingList()
}