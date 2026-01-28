package com.example.granzonamarciana.entity;

import androidx.room.Entity;
import java.time.LocalDate;

@Entity(tableName = "espectadores")
public class Espectador extends Actor {

    public Espectador(String telefono, TipoRol rol, String nombre, String primerApellidp, String segundoApellido,
                      String email, String imagenUrl, LocalDate fechaRegistro, String username, String password) {
        super(telefono, rol, nombre, primerApellidp, segundoApellido, email, imagenUrl, fechaRegistro, username, password);
    }
}