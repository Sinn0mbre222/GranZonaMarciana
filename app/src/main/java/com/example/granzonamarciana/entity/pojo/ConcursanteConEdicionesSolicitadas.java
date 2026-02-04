package com.example.granzonamarciana.entity.pojo;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.entity.Espectador;
import com.example.granzonamarciana.entity.Solicitud;
import java.util.List;

public class ConcursanteConEdicionesSolicitadas {
    @Embedded
    public Concursante concursante;

    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = Solicitud.class,
                    parentColumn = "contestantId",
                    entityColumn = "editionId"
            )
    )
    public List<Edicion> edicionesSolicitadas;
}