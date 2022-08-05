package com.example.myapplication.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.myapplication.entities.Culture
import com.example.myapplication.entities.CultureVarieteCrossRef
import com.example.myapplication.entities.Variete

data class CultureWithVarieties (
    @Embedded val culture: Culture,
    @Relation(
        parentColumn = "id_c",
        entityColumn = "id_var",
        associateBy = Junction(CultureVarieteCrossRef::class)
    )
    val varietes: List<Variete>
)