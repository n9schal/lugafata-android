package com.nischal.clothingstore.ui.models

import androidx.room.PrimaryKey
import com.nischal.clothingstore.ActiveCustomerQuery
import com.nischal.clothingstore.utils.Constants

data class RegisterRequest(
    val title: String? = null,
    val firstName: String,
    val lastName: String,
    val mobileNumber: String,
    val email: String,
    val password: String,
    val rePassword: String
)

data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false
)

data class AlertMessage(
    val title: String = Constants.ValidationTitle.DEFAULT_TITLE,
    val message: String
)

data class UserDetails(
    var id: String = "",
    val displayName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = ""
) {
    companion object {
        fun parseToUserDetails(data: ActiveCustomerQuery.ActiveCustomer): UserDetails {
            return UserDetails(
                id = data.id,
                displayName = data.firstName + " " + data.lastName,
                firstName = data.firstName,
                lastName = data.lastName,
                phoneNumber = data.phoneNumber ?: "",
                email = data.emailAddress
            )
        }
    }
}

data class Product(
    @PrimaryKey
    var productId: String = "",
    var orderLineId: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val stockLevel: String = "",
    val description: String = "",
    val discountPercent: Int = 0,
    val image: Image = Image(),
    val productName: String = "",
    var productPrice: Int = 0,
    val productUnit: String = "",
    val unitsSold: Int = 0,
    var qtyInCart: Int = 0,
    val facetValueIds: List<String> = listOf()
)

data class Image(
    var src: String = "",
    val title: String = ""
)

data class HomeCategory(
    val id: String = "",
    val title: String = "",
    val adUrl: String = "",
    val productList: ArrayList<Product> = arrayListOf()
)