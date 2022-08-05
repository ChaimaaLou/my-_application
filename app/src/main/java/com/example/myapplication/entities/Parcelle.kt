package com.example.myapplication.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.JsonArray
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource


@Entity
data class Parcelle(
    @PrimaryKey(autoGenerate = true) val id_p: Int?,
    @ColumnInfo(name = "name") val Nom_Parcelle: String?,
    @ColumnInfo(name = "Date de semis") val Date_semi: String?,
    @ColumnInfo(name = "Plot") val plot: String?,
    val id_exp: Int?,
    val id_sol: Int?
)