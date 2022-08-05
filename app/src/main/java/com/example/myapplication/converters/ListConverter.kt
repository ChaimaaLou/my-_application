package com.example.myapplication.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mapbox.geojson.Point


class ListConverter {

    @TypeConverter
    fun restoreList(listOfString: String?): List<Point?>? {
        return Gson().fromJson(listOfString, object : TypeToken<List<Point?>?>() {}.type)
    }

    @TypeConverter
    fun saveList(listOfString: List<Point?>?): String? {
        return Gson().toJson(listOfString)
    }

}