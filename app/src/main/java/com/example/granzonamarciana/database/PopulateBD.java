package com.example.granzonamarciana.database;

import android.content.Context;

import com.example.granzonamarciana.entity.Administrador;
import com.example.granzonamarciana.entity.TipoRol;
import com.example.granzonamarciana.service.AdministradorService;

import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;

public class PopulateBD {

    //Añadir los demas servicios aquí
    private final AdministradorService administradorService;


    public PopulateBD(Context c) {
        //Añadir servicios...
        this.administradorService = new AdministradorService(c);
    }

    public static void deleteBD(Context c){
        c.deleteDatabase("granzona_db");
    }

    public void populateAdministrador(){
        Administrador a1 = new Administrador(
                "Sinnombre222",
                BCrypt.hashpw("a", BCrypt.gensalt()),
                "Oscar",
                "Ruiz",
                "Bejarano",
                "123456789",
                "sinnombre222@gmail.com",
                "https://si",
                TipoRol.ADMINISTRADOR,
                LocalDate.now());

        administradorService.insertarAdministrador(a1);
    }
}
