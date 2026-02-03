package com.example.granzonamarciana.database;

import android.content.Context;
import com.example.granzonamarciana.entity.*;
import com.example.granzonamarciana.service.*;
import org.mindrot.jbcrypt.BCrypt;
import java.time.LocalDate;

public class PopulateBD {

    private final AdministradorService administradorService;
    private final ConcursanteService concursanteService;
    private final EspectadorService espectadorService;
    private final EdicionService edicionService;
    private final SolicitudService solicitudService;
    private final NoticiaService noticiaService;
    private final GalaService galaService;
    private final PuntuacionService puntuacionService;

    private final String IMG_MARCIANO_AZUL = "https://i.pinimg.com/736x/8a/6c/d0/8a6cd0cbf34b2646d0148c06c60d87eb.jpg";
    private final String IMG_CYBERPUNK = "https://images.steamusercontent.com/ugc/10940843880100406591/20AED04CA53DC902287155F396C335970D26AFD1/";
    private final String IMG_ANIME_GIRL = "https://i.redd.it/4qoadr4pdqs91.png";
    private final String IMG_RETRATO = "https://img.wattpad.com/cover/251546382-256-k128842.jpg";
    private final String IMG_MERO = "https://media.meer.com/attachments/a61ef3c5af84660ddd9d2b3f101e931d1f1c74c1/store/fill/860/645/6d87876e655d8c2a2aa41e9fc377c5ed10e7793b9f42c0c9de894c042d6f/Mero.jpg";
    private final String IMG_ALIEN_GREEN = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfeuLwmvj2x2RcyVpeih6J0Sh4TCxXWhk1ww&s";
    private final String IMG_ABSTRACCION = "https://i1.sndcdn.com/artworks-Ef3xqeyrwFGXeM9M-bwC5tw-t1080x1080.jpg";

    public PopulateBD(Context c) {
        this.administradorService = new AdministradorService(c);
        this.concursanteService = new ConcursanteService(c);
        this.espectadorService = new EspectadorService(c);
        this.edicionService = new EdicionService(c);
        this.solicitudService = new SolicitudService(c);
        this.noticiaService = new NoticiaService(c);
        this.galaService = new GalaService(c);
        this.puntuacionService = new PuntuacionService(c);
    }

    public void executeFullPopulate() {
        populateUsuarios();
        populateEdicionesGalasYNoticias();
    }

    private void populateUsuarios() {
        administradorService.insertarAdministrador(new Administrador("admin", BCrypt.hashpw("admin", BCrypt.gensalt()),
                "Oscar", "Ruiz", "Bejarano", "123456789", "admin@gmail.com", IMG_MARCIANO_AZUL, TipoRol.ADMINISTRADOR, LocalDate.now()));

        espectadorService.insertar(new Espectador("MarcianoFan", BCrypt.hashpw("1234", BCrypt.gensalt()),
                "Lucía", "García", "Sanz", "699888777", "lucia@email.com", IMG_ANIME_GIRL, TipoRol.ESPECTADOR, LocalDate.now()));

        espectadorService.insertar(new Espectador("GamerPro", BCrypt.hashpw("1234", BCrypt.gensalt()),
                "David", "Soto", "Mena", "611222333", "david@email.com", "ic_default_avatar", TipoRol.ESPECTADOR, LocalDate.now()));

        concursanteService.insert(new Concursante("Prueba1", BCrypt.hashpw("a", BCrypt.gensalt()), "Alberto", "Ames", "Alba", "600111222", "alberto@email.com", IMG_CYBERPUNK, TipoRol.CONCURSANTE, LocalDate.now()));
        concursanteService.insert(new Concursante("MarcianoX", BCrypt.hashpw("a", BCrypt.gensalt()), "Xavier", "Lopez", "Gala", "600333444", "xavi@email.com", IMG_RETRATO, TipoRol.CONCURSANTE, LocalDate.now()));
        concursanteService.insert(new Concursante("AlienQueen", BCrypt.hashpw("a", BCrypt.gensalt()), "Elena", "Vidal", "Oca", "600555666", "elena@email.com", IMG_ALIEN_GREEN, TipoRol.CONCURSANTE, LocalDate.now()));
        concursanteService.insert(new Concursante("Stellar", BCrypt.hashpw("a", BCrypt.gensalt()), "Sara", "Polo", "Ruiz", "600777888", "sara@email.com", IMG_MERO, TipoRol.CONCURSANTE, LocalDate.now()));
        concursanteService.insert(new Concursante("Nebula", BCrypt.hashpw("a", BCrypt.gensalt()), "Iker", "Casas", "Rey", "600999000", "iker@email.com", "ic_default_avatar", TipoRol.CONCURSANTE, LocalDate.now()));
        concursanteService.insert(new Concursante("Zorg", BCrypt.hashpw("a", BCrypt.gensalt()), "Zorg", "Alpha", "Beta", "622111000", "zorg@email.com", IMG_ABSTRACCION, TipoRol.CONCURSANTE, LocalDate.now()));
        concursanteService.insert(new Concursante("Nova", BCrypt.hashpw("a", BCrypt.gensalt()), "Nova", "Terra", "Nova", "633222111", "nova@email.com", IMG_ANIME_GIRL, TipoRol.CONCURSANTE, LocalDate.now()));
        concursanteService.insert(new Concursante("Orbit", BCrypt.hashpw("a", BCrypt.gensalt()), "Victor", "Vortex", "Sun", "644333222", "orbit@email.com", IMG_RETRATO, TipoRol.CONCURSANTE, LocalDate.now()));
    }

    private void populateEdicionesGalasYNoticias() {
        Edicion ed1 = new Edicion(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 20);
        edicionService.insertarEdicion(ed1);

        solicitudService.insert(new Solicitud(1, 1, "Vengo de Marte a ganar.", EstadoSolicitud.ACEPTADA));
        solicitudService.insert(new Solicitud(1, 2, "Soy un experto en supervivencia espacial.", EstadoSolicitud.ACEPTADA));
        solicitudService.insert(new Solicitud(1, 3, "La corona es mía por derecho galáctico.", EstadoSolicitud.ACEPTADA));
        solicitudService.insert(new Solicitud(1, 4, "Brillaré más que una supernova.", EstadoSolicitud.ACEPTADA));
        solicitudService.insert(new Solicitud(1, 5, "Silencioso pero letal en los juegos.", EstadoSolicitud.ACEPTADA));
        solicitudService.insert(new Solicitud(1, 6, "Quiero entrar para pagar mi nave nueva.", EstadoSolicitud.PENDIENTE));
        solicitudService.insert(new Solicitud(1, 7, "Mi planeta me envió como embajadora.", EstadoSolicitud.PENDIENTE));
        solicitudService.insert(new Solicitud(1, 8, "Vengo a romperlo todo.", EstadoSolicitud.RECHAZADA));

        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fin = LocalDate.of(2026, 12, 31);

        // CORRECCIÓN GALA: El constructor pide (editionId, fecha)
        galaService.insert(new Gala(1, LocalDate.of(2026, 3, 15)), inicio, fin);
        galaService.insert(new Gala(1, LocalDate.of(2026, 4, 15)), inicio, fin);
        galaService.insert(new Gala(1, LocalDate.of(2026, 5, 15)), inicio, fin);

        // CORRECCIÓN PUNTUACIÓN: El constructor pide (espectadorId, concursanteId, galaId, valor, fechaVoto)
        // Eliminado el String del comentario que no existe en tu entidad
        puntuacionService.puntuar(new Puntuacion(1, 1, 1, 9, LocalDate.now()));
        puntuacionService.puntuar(new Puntuacion(1, 1, 2, 8, LocalDate.now()));
        puntuacionService.puntuar(new Puntuacion(1, 2, 1, 7, LocalDate.now()));
        puntuacionService.puntuar(new Puntuacion(1, 3, 1, 10, LocalDate.now()));

        noticiaService.insertarNoticia(new Noticia(LocalDate.now(),
                "La base marciana abre sus puertas a los nuevos elegidos. ¡La competencia promete ser feroz!",
                "BIENVENIDOS A LA ZONA MARCIANA", IMG_ABSTRACCION, 1, 1));

        noticiaService.insertarNoticia(new Noticia(LocalDate.now(),
                "Alberto y Elena protagonizan la primera gran discusión por los suministros de oxígeno.",
                "TENSIÓN EN EL MÓDULO DE VIDA", IMG_MERO, 1, 1));

        noticiaService.insertarNoticia(new Noticia(LocalDate.now(),
                "Un objeto no identificado ha sido avistado cerca de la nave. ¿Aliados o enemigos?",
                "AVISTAMIENTO OVNI", IMG_CYBERPUNK, 1, 1));

        noticiaService.insertarNoticia(new Noticia(LocalDate.now(),
                "Zorg y Nova siguen esperando su oportunidad. ¿Los aceptará el administrador?",
                "SOLICITUDES EN ESPERA", IMG_ALIEN_GREEN, 1, 1));

        noticiaService.insertarNoticia(new Noticia(LocalDate.now(),
                "Mañana se celebra la primera gran gala. Las votaciones están que arden.",
                "CUENTA ATRÁS PARA LA GALA", IMG_RETRATO, 1, 1));
    }
}