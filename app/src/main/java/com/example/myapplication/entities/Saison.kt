package com.example.myapplication.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Saison(
    @PrimaryKey(autoGenerate = true) val id_s: Int?,
    @ColumnInfo(name = "Debut de saison") val Debut_sais: String?,
    @ColumnInfo(name = "Durée du cycle") val Duree_cycle: Int?,
    val id_p: Int?,
    val id_c: Int?
)