package com.example.myapplication.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.myapplication.entities.Culture
import com.example.myapplication.entities.CultureVarieteCrossRef
import com.example.myapplication.entities.Variete

data class VarieteWithCultures (
    @Embedded val variete: Variete,
    @Relation(
        parentColumn = "id_var",
        entityColumn = "id_c",
        associateBy = Junction(CultureVarieteCrossRef::class)
    )
    val cultures: List<Culture>
)