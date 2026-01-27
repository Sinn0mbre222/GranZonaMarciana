package com.example.granzonamarciana.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "solicitudes")
public class Solicitud extends DomainEntity {

    private int editionId;    // ID de la edición
    private int contestantId; // ID del concursante
    private String mensaje;   // Motivo por el que desea participar
    private EstadoSolicitud estado;

    public Solicitud(int editionId, int contestantId, String mensaje, EstadoSolicitud estado) {
        this.editionId = editionId;
        this.contestantId = contestantId;
        this.mensaje = mensaje;
        this.estado = estado;
    }

    // Getters y Setters
    public int getEditionId() { return editionId; }
    public void setEditionId(int editionId) { this.editionId = editionId; }

    public int getContestantId() { return contestantId; }
    public void setContestantId(int contestantId) { this.contestantId = contestantId; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
}