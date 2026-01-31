package com.example.granzonamarciana.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

@Entity(tableName = "concursantes",
        foreignKeys = @ForeignKey(entity = Edicion.class,
                parentColumns = "id",
                childColumns = "edicionId",
                onDelete = ForeignKey.CASCADE))
public class Concursante extends DomainEntity {

    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String email;
    private String telefono;
    private String password;

    private int edicionId;

    public Concursante() {
        super();
    }

    @Ignore
    public Concursante(String nombre, String primerApellido, String segundoApellido,
                       String email, String telefono, String password, int edicionId) {
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.email = email;
        this.telefono = telefono;
        this.password = password;
        this.edicionId = edicionId;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }

    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getEdicionId() { return edicionId; }
    public void setEdicionId(int edicionId) { this.edicionId = edicionId; }
}