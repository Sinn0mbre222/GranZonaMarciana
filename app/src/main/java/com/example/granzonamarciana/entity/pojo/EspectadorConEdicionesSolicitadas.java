package com.example.granzonamarciana.entity.pojo;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.entity.Espectador;
import com.example.granzonamarciana.entity.Solicitud;
import java.util.List;

public class EspectadorConEdicionesSolicitadas {
    @Embedded
    public Espectador espectador;

    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = Solicitud.class,
                    parentColumn = "contestantId", // En Solicitud, contestantId es el ID del usuario
                    entityColumn = "editionId"
            )
    )
    public List<Edicion> edicionesSolicitadas;
}