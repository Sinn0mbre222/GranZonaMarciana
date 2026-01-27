package com.example.granzonamarciana.entity;

import androidx.room.Entity;
import java.time.LocalDate;

@Entity(tableName = "galas")
public class Gala extends DomainEntity {

    private int editionId; // ID de la edición a la que pertenece
    private LocalDate fecha; // Fecha de realización

    public Gala(int editionId, LocalDate fecha) {
        this.editionId = editionId;
        this.fecha = fecha;
    }

    // Getters y Setters
    public int getEditionId() { return editionId; }
    public void setEditionId(int editionId) { this.editionId = editionId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}