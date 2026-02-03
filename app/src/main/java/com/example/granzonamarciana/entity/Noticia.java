package com.example.granzonamarciana.entity;

import androidx.room.Entity;
import java.time.LocalDate;

@Entity(tableName = "noticias")
public class Noticia extends DomainEntity {

    private LocalDate fechaPublicacion;
    private String cuerpo;
    private String cabecera;
    private String imagen;
    private int edicionId;
    private int administradorId;

    public Noticia(LocalDate fechaPublicacion, String cuerpo, String cabecera, String imagen, int edicionId, int administradorId) {
        this.fechaPublicacion = fechaPublicacion;
        this.cuerpo = cuerpo;
        this.cabecera = cabecera;
        this.imagen = imagen;
        this.edicionId = edicionId;
        this.administradorId = administradorId;
    }

    // Getters y Setters
    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
    public String getCuerpo() { return cuerpo; }
    public String getCabecera() { return cabecera; }
    public String getImagen() { return imagen; }
    public int getEdicionId() { return edicionId; }
    public int getAdministradorId() { return administradorId; }

    public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
    public void setCuerpo(String cuerpo) { this.cuerpo = cuerpo; }
    public void setCabecera(String cabecera) { this.cabecera = cabecera; }

    public void setImagen(String imagen) { this.imagen = imagen; }
    public void setEdicionId(int edicionId) { this.edicionId = edicionId; }
    public void setAdministradorId(int administradorId) { this.administradorId = administradorId; }
}