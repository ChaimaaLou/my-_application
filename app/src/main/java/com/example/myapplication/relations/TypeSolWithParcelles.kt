package com.example.myapplication.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.myapplication.entities.Parcelle
import com.example.myapplication.entities.TypeSol

data class TypeSolWithParcelles (
    @Embedded
    val typeSol: TypeSol,
    @Relation(
        parentColumn = "id_sol",
        entityColumn = "id_sol"
    )
    val parcelles: List<Parcelle>
)