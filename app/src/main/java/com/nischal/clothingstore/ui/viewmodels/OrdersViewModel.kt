package com.nischal.clothingstore.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.nischal.clothingstore.repositories.MainRepository
import com.nischal.clothingstore.ui.models.OrderDetails
import com.nischal.clothingstore.utils.Resource
import com.nischal.clothingstore.utils.SingleLiveEvent

class OrdersViewModel(private val mainRepository: MainRepository): ViewModel() {
    var getOrdersMediator = MediatorLiveData<Resource<ArrayList<OrderDetails>>>()

    val orderItemClickEvent = SingleLiveEvent<OrderDetails>()

    fun getOrders(
        currentPage: Int,
        pageSize: Int,
        isPending: Boolean
    ) {
        getOrdersMediator.addSource(
            mainRepository.getOrdersQuery(
                currentPage,
                pageSize,
                isPending
            )
        ) {
            getOrdersMediator.value = it
        }
    }

}