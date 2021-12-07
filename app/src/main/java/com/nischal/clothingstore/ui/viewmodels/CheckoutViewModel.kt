package com.nischal.clothingstore.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.nischal.clothingstore.ActiveOrderQuery
import com.nischal.clothingstore.repositories.MainRepository
import com.nischal.clothingstore.ui.models.AlertMessage
import com.nischal.clothingstore.ui.models.OrderDetails
import com.nischal.clothingstore.utils.Constants.ValidationErrorMessages.EMPTY_DELIVERY_LOCATION
import com.nischal.clothingstore.utils.Constants.ValidationErrorMessages.EMPTY_PAYMENT_OPTION
import com.nischal.clothingstore.utils.Resource
import com.nischal.clothingstore.utils.SingleLiveEvent

class CheckoutViewModel(
    private val mainRepository: MainRepository
): ViewModel() {
    var orderDetailsChangedEvent = SingleLiveEvent<OrderDetails>()
    var activeOrderQueryMediator = MediatorLiveData<Resource<ActiveOrderQuery.ActiveOrder>>()
    var placeOrderOperationMediator = MediatorLiveData<Resource<Any?>>()

    var orderDetails: OrderDetails? = null
    val alertDialogEvent = SingleLiveEvent<AlertMessage>()
    val notLoggedInEvent = SingleLiveEvent<Void>()

    fun activeOrderQuery() {
        activeOrderQueryMediator.addSource(mainRepository.activeOrderQuery()) {
            activeOrderQueryMediator.value = it
        }
    }

    fun placeOrderOperation(orderDetails: OrderDetails) {
        if (isUserLoggedIn()) {
            placeOrderOperationMediator.addSource(mainRepository.placeOrderOperation(orderDetails)) {
                placeOrderOperationMediator.value = it
            }
        } else {
            notLoggedInEvent.call()
        }
    }

    fun clearShoppingList() {
        mainRepository.clearShoppingList()
    }

    fun validateCheckoutDetails(): Boolean {
        when {
            orderDetails?.deliveryLocation == null -> {
                alertDialogEvent.value = AlertMessage(message = EMPTY_DELIVERY_LOCATION)
                return false
            }
            // * if savedLocation property is blank
            orderDetails?.deliveryLocation!!.savedLocationName.isNullOrBlank()-> {
                alertDialogEvent.value = AlertMessage(message = EMPTY_DELIVERY_LOCATION)
                return false
            }
            orderDetails?.paymentOption == null -> {
                alertDialogEvent.value = AlertMessage(message = EMPTY_PAYMENT_OPTION)
                return false
            }
            orderDetails?.paymentOption!!.paymentName.isNullOrBlank() -> {
                alertDialogEvent.value = AlertMessage(message = EMPTY_PAYMENT_OPTION)
                return false
            }
        }
        return true
    }

    fun isUserLoggedIn() = mainRepository.isUserLoggedIn()
}