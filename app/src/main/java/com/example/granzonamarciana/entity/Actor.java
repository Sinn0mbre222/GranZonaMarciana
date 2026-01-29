package com.example.granzonamarciana.entity;

import java.time.LocalDate;

public abstract class Actor extends DomainEntity {
    private String username;
    private String password;
    private TipoRol rol;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String email;
    private String telefono;
    private String imagenUrl;
    private LocalDate fechaRegistro;

    // Constructor

    public Actor(String username, String password, String nombre, String primerApellido,
                 String segundoApellido, String telefono, String email, String imagenUrl,
                 TipoRol rol, LocalDate fechaRegistro) {
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.telefono = telefono;
        this.email = email;
        this.imagenUrl = imagenUrl;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public TipoRol getRol() {
        return rol;
    }

    public void setRol(TipoRol rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellidp) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}