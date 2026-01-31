package com.example.granzonamarciana.entity.pojo;

import androidx.room.Embedded;
import androidx.room.Relation;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Solicitud;

public class SolicitudConConcursante {
    @Embedded
    public Solicitud solicitud; // Aquí tienes el mensaje, el estado y la fecha

    @Relation(
            parentColumn = "concursanteId",
            entityColumn = "id"
    )
    public Concursante concursante; // Aquí tienes el nombre, la foto, etc.
}