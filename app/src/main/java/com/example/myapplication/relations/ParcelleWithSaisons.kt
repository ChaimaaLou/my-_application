package com.example.myapplication.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.myapplication.entities.Parcelle
import com.example.myapplication.entities.Saison

data class ParcelleWithSaisons (
    @Embedded val parcelle: Parcelle,
    @Relation(
        parentColumn = "id_p",
        entityColumn = "id_p"
    )
     val saisons: List<Saison>

)