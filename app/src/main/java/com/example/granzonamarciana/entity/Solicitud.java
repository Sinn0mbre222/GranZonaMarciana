package com.example.granzonamarciana.entity;

import androidx.room.Entity;

@Entity(tableName = "solicitudes")
public class Solicitud extends DomainEntity {
    private int editionId;
    private int concursanteId;
    private String mensaje;
    private EstadoSolicitud estado;

    // El constructor no debe incluir el ID si DomainEntity lo autogenera
    public Solicitud(int editionId, int concursanteId, String mensaje, EstadoSolicitud estado) {
        this.editionId = editionId;
        this.concursanteId = concursanteId;
        this.mensaje = mensaje;
        this.estado = estado;
    }

    // Getters y Setters
    public int getEditionId() { return editionId; }
    public void setEditionId(int editionId) { this.editionId = editionId; }

    public int getConcursanteId() { return concursanteId; }
    public void setConcursanteId(int concursanteId) { this.concursanteId = concursanteId; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
}