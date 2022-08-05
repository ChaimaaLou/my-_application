package com.example.myapplication.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Type de sol")
data class TypeSol(
    @PrimaryKey(autoGenerate = true) val id_sol: Int?,
    @ColumnInfo(name = "name") val Nom_sol: String?
)

