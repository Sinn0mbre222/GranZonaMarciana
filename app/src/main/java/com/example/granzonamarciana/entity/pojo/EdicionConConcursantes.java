package com.example.granzonamarciana.entity.pojo;

import androidx.room.Embedded;
import androidx.room.Relation;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Edicion;
import java.util.List;

public class EdicionConConcursantes {
    @Embedded
    public Edicion edicion;

    @Relation(
            parentColumn = "id",
            entityColumn = "edicionId"
    )
    public List<Concursante> concursantes;
}