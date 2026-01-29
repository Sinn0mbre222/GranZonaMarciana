package com.example.granzonamarciana.database;

import android.content.Context;

import com.example.granzonamarciana.entity.Administrador;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Espectador;
import com.example.granzonamarciana.entity.TipoRol;
import com.example.granzonamarciana.service.AdministradorService;
import com.example.granzonamarciana.service.ConcursanteService;
import com.example.granzonamarciana.service.EspectadorService;

import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;

public class PopulateBD {

    //Añadir los demas servicios aquí
    private final AdministradorService administradorService;
    private final ConcursanteService concursanteService;
    private final EspectadorService espectadorService;


    public PopulateBD(Context c) {
        //Añadir servicios...
        this.administradorService = new AdministradorService(c);
        this.concursanteService = new ConcursanteService(c);
        this.espectadorService = new EspectadorService(c);
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

    public void populateConcursante() {
        Concursante c1 = new Concursante(
                "Prueba1",
                BCrypt.hashpw("a", BCrypt.gensalt()),
                "A",
                "a",
                "a",
                "600111222",
                "a@email.com",
                "https://imagen.url/a.jpg",
                TipoRol.CONCURSANTE,
                LocalDate.now()
        );


        concursanteService.insert(c1);
    }

    public void populateEspectador() {
        // Creamos un espectador con datos diferentes
        Espectador e1 = new Espectador(
                "MarcianoFan",
                BCrypt.hashpw("1234", BCrypt.gensalt()),
                "Lucía",
                "García",
                "Sanz",
                "699888777",
                "lucia.garcia@email.com",
                "https://imagen.url/lucia.png",
                TipoRol.ESPECTADOR,
                LocalDate.now()
        );

        espectadorService.insertar(e1);
    }
}
