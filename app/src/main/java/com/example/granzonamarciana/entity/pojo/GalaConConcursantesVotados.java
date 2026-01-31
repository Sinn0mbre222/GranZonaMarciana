package com.example.granzonamarciana.entity.pojo;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Gala;
import com.example.granzonamarciana.entity.Puntuacion;
import java.util.List;

public class GalaConConcursantesVotados {
    @Embedded
    public Gala gala;

    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = Puntuacion.class,
                    parentColumn = "galaId",
                    entityColumn = "concursanteId"
            )
    )
    public List<Concursante> concursantesVotados;
}