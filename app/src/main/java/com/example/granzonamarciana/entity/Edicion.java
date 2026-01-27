package com.example.granzonamarciana.entity;

import androidx.room.Entity;

import java.time.LocalDate;
@Entity(tableName = "ediciones")
public class Edicion extends DomainEntity{

    private LocalDate fechaInicio;
    private LocalDate fechaFinal;
    private int numeroParticipantesMax;

    public Edicion(LocalDate fechaInicio, LocalDate fechaFinal, int numeroParticipantesMax) {
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.numeroParticipantesMax = numeroParticipantesMax;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFinal() {
        return fechaFinal;
    }

    public int getNumeroParticipantesMax() {
        return numeroParticipantesMax;
    }

    public void setNumeroParticipantesMax(int numeroParticipantesMax) {
        this.numeroParticipantesMax = numeroParticipantesMax;
    }

    public void setFechaFinal(LocalDate fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
}
