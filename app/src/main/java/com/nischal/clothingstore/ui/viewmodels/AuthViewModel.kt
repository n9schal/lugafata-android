package com.nischal.clothingstore.ui.viewmodels

import android.util.Patterns
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.api.ApolloExperimental
import com.nischal.clothingstore.ActiveCustomerQuery
import com.nischal.clothingstore.LoginMutation
import com.nischal.clothingstore.repositories.AuthRepository
import com.nischal.clothingstore.ui.models.AlertMessage
import com.nischal.clothingstore.ui.models.LoginRequest
import com.nischal.clothingstore.ui.models.RegisterRequest
import com.nischal.clothingstore.utils.Constants
import com.nischal.clothingstore.utils.Resource
import com.nischal.clothingstore.utils.SingleLiveEvent

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    var activeCustomerQueryMediator = MediatorLiveData<Resource<ActiveCustomerQuery.Data>>()
    var registerCustomerAccountMutationMediator = MediatorLiveData<Resource<LoginRequest>>()
    var loginMutationMediator = MediatorLiveData<Resource<LoginMutation.Data>>()
    var requestPasswordResetMediator = MediatorLiveData<Resource<Any?>>()

    val alertDialogEvent = SingleLiveEvent<AlertMessage>()

    fun registerCustomerAccountMutation(registerRequest: RegisterRequest) {
        if (validateSignUp(registerRequest)) {
            registerCustomerAccountMutationMediator
                .addSource(authRepository.registerCustomerAccountMutation(registerRequest)) {
                    registerCustomerAccountMutationMediator.value = it
                }
        }
    }

    @ApolloExperimental
    fun loginMutation(loginRequest: LoginRequest) {
        if (validateLogin(loginRequest)) {
            loginMutationMediator.addSource(
                authRepository.loginMutation(
                    loginRequest
                )
            ) {
                loginMutationMediator.value = it
            }
        }
    }

    fun requestPasswordReset(email: String) {
        if (validateEmail(email)) {
            requestPasswordResetMediator.addSource(authRepository.requestPasswordReset(email)) {
                requestPasswordResetMediator.value = it
            }
        }
    }

    fun activeCustomer() {
        activeCustomerQueryMediator.addSource(authRepository.activeCustomerQuery()) {
            activeCustomerQueryMediator.value = it
        }
    }

    fun clearPreferences() = authRepository.clearPreferences()

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
}