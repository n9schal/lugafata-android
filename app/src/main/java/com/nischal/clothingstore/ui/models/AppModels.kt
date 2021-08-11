package com.nischal.clothingstore.ui.models

import com.nischal.clothingstore.utils.Constants

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val mobileNumber: String,
    val email: String,
    val password: String,
    val rePassword: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AlertMessage(
    val title: String = Constants.ValidationTitle.DEFAULT_TITLE,
    val message: String
)