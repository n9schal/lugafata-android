package com.nischal.clothingstore.ui.viewmodels

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.nischal.clothingstore.repositories.AuthRepository
import com.nischal.clothingstore.ui.models.AlertMessage
import com.nischal.clothingstore.ui.models.LoginRequest
import com.nischal.clothingstore.ui.models.RegisterRequest
import com.nischal.clothingstore.utils.Constants
import com.nischal.clothingstore.utils.SingleLiveEvent

class AuthViewModel(
    authRepository: AuthRepository
) : ViewModel() {

    val alertDialogEvent = SingleLiveEvent<AlertMessage>()

    fun registerCustomerAccountMutation(registerRequest: RegisterRequest) {
        if (validateSignUp(registerRequest)){
            // todo do register
        }
    }

    fun loginMutation(loginRequest: LoginRequest) {
        if(validateLogin(loginRequest)){
            //todo do login
        }
    }

    private fun validateSignUp(registerRequest: RegisterRequest): Boolean {
        when {
            registerRequest.firstName.trim().isEmpty() -> {
                alertDialogEvent.value =
                    AlertMessage(message = Constants.ValidationErrorMessages.ERR_EMPTY_FIRST_NAME)
                return false
            }
            registerRequest.lastName.trim().isEmpty() -> {
                alertDialogEvent.value =
                    AlertMessage(message = Constants.ValidationErrorMessages.ERR_EMPTY_LAST_NAME)
                return false
            }
            registerRequest.mobileNumber.trim().isEmpty() -> {
                alertDialogEvent.value =
                    AlertMessage(message = Constants.ValidationErrorMessages.ERR_EMPTY_MOBILE_NUMBER)
                return false
            }
            !registerRequest.mobileNumber.trim()
                .matches(Constants.ValidationRegex.VALID_PHONE_NUMBER.toRegex()) -> {
                alertDialogEvent.value =
                    AlertMessage(message = Constants.ValidationErrorMessages.ERR_INVALID_MOBILE_NUMBER)
                return false
            }
            registerRequest.email.trim().isEmpty() -> {
                alertDialogEvent.value =
                    AlertMessage(message = Constants.ValidationErrorMessages.ERR_EMPTY_EMAIL)
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(registerRequest.email.trim()).matches() -> {
                alertDialogEvent.value =
                    AlertMessage(message = Constants.ValidationErrorMessages.ERR_INVALID_EMAIL)
                return false
            }
            !registerRequest.password.trim()
                .matches(Constants.ValidationRegex.ATLEAST_SIX_CHARACTERS.toRegex()) -> {
                alertDialogEvent.value =
                    AlertMessage(message = Constants.ValidationErrorMessages.ERR_INVALID_PASSWORD)
                return false
            }
            registerRequest.password.trim() != registerRequest.rePassword.trim() -> {
                alertDialogEvent.value =
                    AlertMessage(message = Constants.ValidationErrorMessages.ERR_INVALID_REPASSWORD)
                return false
            }
        }
        return true
    }

    private fun validateLogin(loginRequest: LoginRequest): Boolean {
        when {
            loginRequest.email.trim().isEmpty() -> {
                alertDialogEvent.value =
                    AlertMessage(message = Constants.ValidationErrorMessages.ERR_EMPTY_EMAIL)
                return false
            }
            loginRequest.password.trim().isEmpty() -> {
                alertDialogEvent.value =
                    AlertMessage(message = Constants.ValidationErrorMessages.ERR_EMPTY_PASSWORD)
                return false
            }
        }
        return true
    }

    private fun validateEmail(email: String): Boolean {
        when {
            email.trim().isEmpty() -> {
                alertDialogEvent.value =
                    AlertMessage(message = Constants.ValidationErrorMessages.ERR_EMPTY_EMAIL)
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> {
                alertDialogEvent.value =
                    AlertMessage(message = Constants.ValidationErrorMessages.ERR_INVALID_EMAIL)
                return false
            }
        }
        return true
    }

    fun requestPasswordReset(email: String) {
        if(validateEmail(email)){
            //todo do send password reset link
        }
    }
}