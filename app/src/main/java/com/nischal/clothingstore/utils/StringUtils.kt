package com.nischal.clothingstore.utils

object StringUtils {
    fun addNepalCountryCode(phoneNumber: String): String {
        return "+977 ${phoneNumber.trim()}"
    }

    fun removeNepalCountryCode(phoneNumber: String): String {
        return if(phoneNumber.isNotEmpty()){
            phoneNumber.split("+977")[1].trim()
        }else{
            ""
        }
    }
}
