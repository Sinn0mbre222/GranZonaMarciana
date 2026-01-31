package com.example.granzonamarciana.entity.pojo;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Puntuacion;

public class PuntuacionConConcursante {
    @Embedded
    public Puntuacion puntuacion; // El voto (valor, galaId, etc.)

    @Relation(
            parentColumn = "concursanteId",
            entityColumn = "id"
    )
    public Concursante concursante; // El dueño del voto
}