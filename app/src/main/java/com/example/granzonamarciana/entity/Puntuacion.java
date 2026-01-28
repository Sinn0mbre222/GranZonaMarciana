package com.example.granzonamarciana.entity;

import androidx.room.Entity;
import java.time.LocalDate;

@Entity(tableName = "puntuaciones")
public class Puntuacion extends DomainEntity {

    private int galaId;
    private int espectadorId;
    private int concursanteId;
    private int valor; // Entre 1 y 5
    private LocalDate fechaVoto;

    public Puntuacion(int galaId, int espectadorId, int concursanteId, int valor, LocalDate fechaVoto) {
        this.galaId = galaId;
        this.espectadorId = espectadorId;
        this.concursanteId = concursanteId;
        this.valor = valor;
        this.fechaVoto = fechaVoto;
    }

    // Getters y Setters
    public int getGalaId() { return galaId; }
    public void setGalaId(int galaId) { this.galaId = galaId; }

    public int getEspectadorId() { return espectadorId; }
    public void setEspectadorId(int espectadorId) { this.espectadorId = espectadorId; }

    public int getConcursanteId() { return concursanteId; }
    public void setConcursanteId(int concursanteId) { this.concursanteId = concursanteId; }

    public int getValor() { return valor; }
    public void setValor(int valor) { this.valor = valor; }

    public LocalDate getFechaVoto() { return fechaVoto; }
    public void setFechaVoto(LocalDate fechaVoto) { this.fechaVoto = fechaVoto; }
}