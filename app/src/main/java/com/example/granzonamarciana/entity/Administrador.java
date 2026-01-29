package com.example.granzonamarciana.entity;

import androidx.room.Entity;

import java.time.LocalDate;

@Entity(tableName = "administradores")
public class Administrador extends Actor {
    public Administrador(String username, String password, String nombre, String primerApellido, String segundoApellido,
                         String telefono, String email, String imagenUrl, TipoRol rol, LocalDate fechaRegistro) {
        super(username, password, nombre, primerApellido, segundoApellido, telefono, email, imagenUrl, rol, fechaRegistro);
    }
}
