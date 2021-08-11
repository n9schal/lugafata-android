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
        const val APP_NAME = "Grocery delivery"
        const val OK = "OK"
        const val CONFIRM = "CONFIRM"
        const val CANCEL = "Cancel"
        const val DIALOG_TAG = "DIALOG_TAG"
        const val NOT_SIGNED_IN_MESSAGE = "You need to be signed in to continue."
        const val TERMS_AND_CONDITIONS = "Terms and conditions"
        const val PRIVACY_POLICY = "Privacy policy"
    }
}