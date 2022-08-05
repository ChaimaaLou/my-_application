package com.example.myapplication.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Variété")
data class Variete (
    @PrimaryKey(autoGenerate = true) val id_var: Int?,
    @ColumnInfo(name = "name") val Nom_var : String?
)