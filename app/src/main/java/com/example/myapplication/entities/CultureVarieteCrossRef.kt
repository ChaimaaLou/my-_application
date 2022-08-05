package com.example.myapplication.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(primaryKeys = ["id_c","id_var"])
data class CultureVarieteCrossRef (
    val id_c: Int,
    val id_var: Int
)

