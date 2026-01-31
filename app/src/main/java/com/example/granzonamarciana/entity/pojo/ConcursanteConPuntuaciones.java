package com.example.granzonamarciana.entity.pojo;

import androidx.room.Embedded;
import androidx.room.Relation;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Puntuacion;
import java.util.List;

public class ConcursanteConPuntuaciones {
    @Embedded
    public Concursante concursante;

    @Relation(
            parentColumn = "id",
            entityColumn = "concursanteId"
    )
    public List<Puntuacion> puntuacionesRecibidas;
}