package com.example.granzonamarciana.entity;

import androidx.room.Entity;
import androidx.room.Index;
import java.time.LocalDate;

@Entity(
        tableName = "puntuaciones",
        primaryKeys = {"espectadorId", "concursanteId", "galaId"},
        indices = {
                @Index("espectadorId"),
                @Index("concursanteId"),
                @Index("galaId")
        }
)
public class Puntuacion {

    private int espectadorId;
    private int concursanteId;
    private int galaId;
    private int valor;
    private LocalDate fechaVoto;

    public Puntuacion(int espectadorId, int concursanteId, int galaId, int valor, LocalDate fechaVoto) {
        this.espectadorId = espectadorId;
        this.concursanteId = concursanteId;
        this.galaId = galaId;
        this.valor = valor;
        this.fechaVoto = fechaVoto;
    }

    // Getters y Setters
    public int getEspectadorId() { return espectadorId; }
    public void setEspectadorId(int espectadorId) { this.espectadorId = espectadorId; }

    public int getConcursanteId() { return concursanteId; }
    public void setConcursanteId(int concursanteId) { this.concursanteId = concursanteId; }

    public int getGalaId() { return galaId; }
    public void setGalaId(int galaId) { this.galaId = galaId; }

    public int getValor() { return valor; }
    public void setValor(int valor) { this.valor = valor; }

    public LocalDate getFechaVoto() { return fechaVoto; }
    public void setFechaVoto(LocalDate fechaVoto) { this.fechaVoto = fechaVoto; }
}