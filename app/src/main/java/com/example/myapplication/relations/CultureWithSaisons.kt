package com.example.myapplication.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.myapplication.entities.Exploitation
import com.example.myapplication.entities.Parcelle

import com.example.myapplication.entities.Culture;
import com.example.myapplication.entities.Saison;


public class CultureWithSaisons (
    @Embedded
    val culture: Culture,
    @Relation(
        parentColumn = "id_c",
        entityColumn = "id_c"
    )
    val saisons: List<Saison>
)