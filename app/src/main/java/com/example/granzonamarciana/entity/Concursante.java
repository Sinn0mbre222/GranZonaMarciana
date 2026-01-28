package com.example.granzonamarciana.entity;

import androidx.room.Entity;
import java.time.LocalDate;

@Entity(tableName = "concursantes")
public class Concursante extends Actor {

    public Concursante(String telefono, TipoRol rol, String nombre, String primerApellidp, String segundoApellido,
                       String email, String imagenUrl, LocalDate fechaRegistro, String username, String password) {
        super(telefono, rol, nombre, primerApellidp, segundoApellido, email, imagenUrl, fechaRegistro, username, password);
    }
}