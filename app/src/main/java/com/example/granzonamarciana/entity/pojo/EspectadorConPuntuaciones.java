package com.example.granzonamarciana.entity.pojo;

import androidx.room.Embedded;
import androidx.room.Relation;
import com.example.granzonamarciana.entity.Espectador;
import com.example.granzonamarciana.entity.Puntuacion;
import java.util.List;

public class EspectadorConPuntuaciones {
    @Embedded
    public Espectador espectador;

    @Relation(
            parentColumn = "id",
            entityColumn = "espectadorId"
    )
    public List<Puntuacion> puntuaciones;

    public Espectador getEspectador() {
        return espectador;
    }

    public void setEspectador(Espectador espectador) {
        this.espectador = espectador;
    }

    public List<Puntuacion> getPuntuaciones() {
        return puntuaciones;
    }

    public void setPuntuaciones(List<Puntuacion> puntuaciones) {
        this.puntuaciones = puntuaciones;
    }
}