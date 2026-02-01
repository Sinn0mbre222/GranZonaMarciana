package com.example.granzonamarciana.entity;

import androidx.room.Entity;

import java.time.LocalDate;

@Entity(tableName = "noticias")
public class Noticia extends DomainEntity{

    private LocalDate fechaPublicacion;
    private String cuerpo;
    private String  cabecera;
    private String imagen;

    private int edicionId;

    public Noticia(LocalDate fechaPublicacion, String cuerpo, String cabecera, String imagen, int edicionId) {
        this.fechaPublicacion = fechaPublicacion;
        this.cuerpo = cuerpo;
        this.cabecera = cabecera;
        this.imagen = imagen;
        this.edicionId=edicionId;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public String getImagen() {
        return imagen;
    }

    public String getCabecera() {
        return cabecera;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public int getEdicionId() {
        return edicionId;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public void setEdicionId(int edicionId) {
        this.edicionId = edicionId;
    }
    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public void setCabecera(String cabecera) {
        this.cabecera = cabecera;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
