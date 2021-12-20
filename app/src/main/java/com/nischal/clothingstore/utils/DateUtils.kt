package com.nischal.clothingstore.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

object DateUtils {
    private const val DATE_FORMAT = "dd-MMM-yyyy hh:mm a"

    @JvmStatic
    fun getFormattedDate(date: String): String {
        return try {
            val instant = Instant.parse(date)
            val formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.MEDIUM )
                .withLocale( Locale.US )
                .withZone( ZoneId.systemDefault() );
            formatter.format( instant );
        } catch (e: Exception) {
            ""
        }
    }
}