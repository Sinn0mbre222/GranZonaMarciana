package com.example.granzonamarciana.entity.pojo;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.entity.Noticia;

import java.util.List;

public class EdicionConNoticias {
    @Embedded
    public Edicion edicion;

    @Relation(
            parentColumn = "id", // El ID de la clase Edicion
            entityColumn = "edicionId" // El campo que añadiste a la clase Noticia
    )
    public List<Noticia> noticias;
}