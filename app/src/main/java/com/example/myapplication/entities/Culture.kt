package com.example.myapplication.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Culture (
    @PrimaryKey(autoGenerate = true) val id_c: Int?,
    @ColumnInfo(name = "name") val Culture_name : String?

)
