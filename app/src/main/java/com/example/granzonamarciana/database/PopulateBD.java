package com.example.granzonamarciana.database;

import android.content.Context;
import android.util.Log;

import com.example.granzonamarciana.entity.Administrador;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Espectador;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.entity.EstadoSolicitud;
import com.example.granzonamarciana.entity.TipoRol;
import com.example.granzonamarciana.service.AdministradorService;
import com.example.granzonamarciana.service.ConcursanteService;
import com.example.granzonamarciana.service.EspectadorService;
import com.example.granzonamarciana.service.EdicionService;
import com.example.granzonamarciana.service.SolicitudService;

import org.mindrot.jbcrypt.BCrypt;
import java.time.LocalDate;

public class PopulateBD {

    private final AdministradorService administradorService;
    private final ConcursanteService concursanteService;
    private final EspectadorService espectadorService;
    private final EdicionService edicionService;
    private final SolicitudService solicitudService;

    public PopulateBD(Context c) {
        this.administradorService = new AdministradorService(c);
        this.concursanteService = new ConcursanteService(c);
        this.espectadorService = new EspectadorService(c);
        this.edicionService = new EdicionService(c);
        this.solicitudService = new SolicitudService(c);
    }


    public void deleteBD(Context c){
        c.deleteDatabase("granzona_db");
    }


    public void executeFullPopulate() {
        populateAdministrador();
        populateEspectador();
        populateEdicionesConParticipantes();
    }

    private void populateAdministrador(){
        Administrador a1 = new Administrador(
                "Sinnombre222",
                BCrypt.hashpw("a", BCrypt.gensalt()),
                "Oscar",
                "Ruiz",
                "Bejarano",
                "123456789",
                "sinnombre222@gmail.com",
                "ic_person",
                TipoRol.ADMINISTRADOR,
                LocalDate.now());
        administradorService.insertarAdministrador(a1);
    }

    private void populateEspectador() {
        Espectador e1 = new Espectador(
                "MarcianoFan",
                BCrypt.hashpw("1234", BCrypt.gensalt()),
                "Lucía",
                "García",
                "Sanz",
                "699888777",
                "lucia.garcia@email.com",
                "ic_default_avatar",
                TipoRol.ESPECTADOR,
                LocalDate.now()
        );
        espectadorService.insertar(e1);
    }

    private void populateEdicionesConParticipantes() {
        // 1. Creamos la Edición 1 (ID esperado: 1)
        Edicion e1 = new Edicion(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                10
        );
        edicionService.insertarEdicion(e1);

        // 2. Creamos los Concursantes (Independientes de la edición)
        Concursante c1 = new Concursante(
                "Prueba1",
                BCrypt.hashpw("a", BCrypt.gensalt()),
                "A", "a", "a",
                "600111222", "a@email.com",
                "ic_default_avatar",
                TipoRol.CONCURSANTE,
                LocalDate.now()
        );

        Concursante c2 = new Concursante(
                "MarcianoX",
                BCrypt.hashpw("a", BCrypt.gensalt()),
                "Xavier", "Lopez", "García",
                "600333444", "xavi@email.com",
                "ic_default_avatar",
                TipoRol.CONCURSANTE,
                LocalDate.now()
        );

        concursanteService.insert(c1); // ID esperado: 1
        concursanteService.insert(c2); // ID esperado: 2

        // 3. Creamos las Solicitudes (El vínculo ACEPTADO para que salgan en la lista)
        // Solicitud para el Concursante 1 en la Edición 1
        Solicitud s1 = new Solicitud(
                1, // ID de la edición
                1, // ID del concursante Prueba1
                "Quiero demostrar que un marciano puede sobrevivir al reality.",
                EstadoSolicitud.ACEPTADA
        );

        // Solicitud para el Concursante 2 en la Edición 1
        Solicitud s2 = new Solicitud(
                1, // ID de la edición
                2, // ID del concursante MarcianoX
                "Vengo a ganar y a llevarme el gran premio intergaláctico.",
                EstadoSolicitud.ACEPTADA
        );

        // Solicitud PENDIENTE (Ejemplo para que el Admin la vea)
        Solicitud s3 = new Solicitud(
                1,
                1, // Reutilizamos un ID para el ejemplo
                "Esta solicitud no debería aparecer en la lista de participantes aún.",
                EstadoSolicitud.PENDIENTE
        );

        solicitudService.insert(s1);
        solicitudService.insert(s2);
        solicitudService.insert(s3);
    }
}