package com.nischal.clothingstore.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.nischal.clothingstore.ui.models.Image
import com.nischal.clothingstore.ui.models.Option
import java.util.*
import kotlin.collections.ArrayList

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
    fun fromImages(images: List<Image>): String{
        return gson.toJson(images)
    }

    @TypeConverter
    fun toImages(stringImages: String): List<Image>{
        return gson.fromJson(stringImages, Array<Image>::class.java).toList()
    }

    @TypeConverter
    fun fromOptions(options: List<Option>): String{
        return gson.toJson(options)
    }

    @TypeConverter
    fun toOptions(stringOptions: String): List<Option>{
        return gson.fromJson(stringOptions, Array<Option>::class.java).toList()
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