package com.example.granzonamarciana.entity;

import androidx.room.Entity;

import java.time.LocalDate;

@Entity(tableName = "noticias")
public class Noticia extends DomainEntity{

    private LocalDate fechaPublicacion;
    private String cuerpo;
    private String  cabecera;
    private String imagen;

    public Noticia(LocalDate fechaPublicacion, String cuerpo, String cabecera, String imagen) {
        this.fechaPublicacion = fechaPublicacion;
        this.cuerpo = cuerpo;
        this.cabecera = cabecera;
        this.imagen = imagen;
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

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
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
