package com.example.myapplication.converters

import androidx.room.TypeConverter
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import org.json.JSONObject

class JsonConverter {

        @TypeConverter
        fun jsonToString(data: GeoJsonSource): String = data.toString()

        @TypeConverter
        fun stringToJson(json: String): GeoJsonSource = GeoJsonSource(json)



}