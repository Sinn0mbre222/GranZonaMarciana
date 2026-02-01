package com.example.granzonamarciana.entity.pojo;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.entity.Gala;

import java.util.List;

public class EdicionConGalas {
    @Embedded
    public Edicion edicion;

    @Relation(
            parentColumn = "id",
            entityColumn = "edicionId" // Debe existir este campo en la entidad Gala
    )
    public List<Gala> galas;
}