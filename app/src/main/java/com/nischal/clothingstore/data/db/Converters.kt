package com.nischal.clothingstore.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.nischal.clothingstore.ui.models.Image
import java.util.*

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromImage(image: Image): String{
        return gson.toJson(image)
    }

    @TypeConverter
    fun toImage(stringifiedImage: String): Image{
        return gson.fromJson(stringifiedImage, Image::class.java)
    }

    @TypeConverter
    fun fromFacetValueIds(facetvalueIds: List<String>): String{
        return gson.toJson(facetvalueIds)
    }

    @TypeConverter
    fun toFacetValueIds(stringFacetvalueIds: String): List<String>{
        return gson.fromJson(stringFacetvalueIds, Array<String>::class.java).toList()
    }
}