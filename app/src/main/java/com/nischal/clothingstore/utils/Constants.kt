package com.nischal.clothingstore.utils

class Constants {
    object ValidationRegex {
        const val VALID_PHONE_NUMBER = "^\\+\\d{1,3}\\s\\d{10,14}(\\s\\d{1,13})?"
        const val ATLEAST_SIX_CHARACTERS = "^[A-Za-z\\d\$@\$!%*?&.]{6,}\$"
    }

    object ValidationErrorMessages {
        const val ERR_EMPTY_FIRST_NAME = "First name is required."
        const val ERR_EMPTY_LAST_NAME = "Last name is required."
        const val ERR_EMPTY_MOBILE_NUMBER = "Mobile number is required."
        const val ERR_EMPTY_EMAIL = "Email is required."
        const val ERR_EMPTY_PASSWORD = "Password is required."
        const val ERR_INVALID_MOBILE_NUMBER = "Please provide valid mobile number."
        const val ERR_INVALID_EMAIL = "Please provide valid email address."
        const val ERR_INVALID_PASSWORD = "Password has to be minimum 6 characters long."
        const val ERR_INVALID_REPASSWORD = "Passwords do not match."
    }

    object ValidationTitle {
        const val DEFAULT_TITLE = "Clothing Store"
    }

    object Args {
        const val TITLE = "TITLE"
        const val MESSAGE = "MESSAGE"
        const val POSITIVE_BTN_TXT = "POSITIVE_BTN_TXT"
        const val NEGATIVE_BTN_TXT = "NEGATIVE_BTN_TXT"
        const val DIALOG_CANCELABLE = "DIALOG_CANCELABLE"
    }

    object Strings {
        const val APP_NAME = "Clothing store"
        const val OK = "OK"
        const val CONFIRM = "CONFIRM"
        const val CANCEL = "Cancel"
        const val DIALOG_TAG = "DIALOG_TAG"
        const val NOT_SIGNED_IN_MESSAGE = "You need to be signed in to continue."
        const val TERMS_AND_CONDITIONS = "Terms and conditions"
        const val PRIVACY_POLICY = "Privacy policy"
    }

    object VendureOrderStates{
        const val CREATED = "Created"
        const val ADDING_ITEMS = "AddingItems"
        const val ARRANGING_PAYMENT = "ArrangingPayment"
        const val PAYMENT_AUTHORIZED = "PaymentAuthorized"
        const val PAYMENT_SETTLED = "PaymentSettled"
        const val PARTIALLY_SHIPPED = "PartiallyShipped"
        const val SHIPPED = "Shipped"
        const val PARTIALLY_DELIVERED = "PartiallyDelivered"
        const val DELIVERED = "Delivered"
        const val MODIFYING = "Modifying"
        const val ARRANGING_ADDITIONAL_PAYMENT = "ArrangingAdditionalPayment"
        const val CANCELLED = "Cancelled"
    }

    object ErrorHandlerMessages{
        const val GENERIC_ERROR_MESSAGE = "Oops! Something went wrong!"
    }

    object StockLevelConstants{
        const val IN_STOCK = "IN_STOCK"
        const val OUT_OF_STOCK = "OUT_OF_STOCK"
        const val LOW_STOCK = "LOW_STOCK"
    }

    object SlugConstants{
        const val HOME_PAGE = "home-page"
        const val CATEGORIES = "categories"
    }

}