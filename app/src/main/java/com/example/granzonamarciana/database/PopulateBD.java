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

    // --- GALERÍA DE IMÁGENES ---
    private final String IMG_ADMIN = "https://i.pinimg.com/736x/8a/6c/d0/8a6cd0cbf34b2646d0148c06c60d87eb.jpg";
    private final String IMG_COSPLAY_VADER = "https://www.shutterstock.com/image-vector/darth-vader-helmet-logo-universe-600nw-2350946717.jpg";
    private final String IMG_RYUU_LION = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRh8-dEX4DE7k0ExXigGf0kihff1WZhONiyPA&s";
    private final String IMG_ESQUELETOS_ANIMADOS = "https://preview.redd.it/l-animated-the-skeleton-banging-shield-gif-v0-gda97diyodcg1.png?width=403&format=png&auto=webp&s=a96aca5115b31383062279c247e01ee94e778388";
    private final String IMG_ESQUELETOS_FLOTANTES = "https://i.pinimg.com/736x/16/c0/55/16c055d9dc975a4269a18ee0535f552e.jpg";
    private final String IMG_VEGETTA = "https://www.famousbirthdays.com/faces/vegetta777-image.jpg";
    private final String IMG_WILLYREX = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRxVCjYBdvh8g7k1BMwEi1N4SFq-daZJkSYFA&s";
    private final String IMG_GATO_ALIEN = "https://preview.redd.it/alien-cat-v0-519r9x4eyg2e1.jpg?width=640&crop=smart&auto=webp&s=ce81a80b046fd80cfd93423cc9483e3f918ac79c";
    private final String IMG_VENOM = "https://i.blogs.es/92852d/cartel-venom/450_1000.jpg";

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
        // ADMINISTRADOR
        administradorService.insertarAdministrador(new Administrador("el_super", BCrypt.hashpw("admin", BCrypt.gensalt()),
                "Oscar", "Ruiz", "Bejarano", "123456789", "produccion@marztv.com", IMG_ADMIN, TipoRol.ADMINISTRADOR, LocalDate.now()));

        // CONCURSANTES
        concursanteService.insert(new Concursante("CosplayVader", BCrypt.hashpw("a", BCrypt.gensalt()), "Arturo", "Sky", "García", "600111222", "vader@fans.com", IMG_COSPLAY_VADER, TipoRol.CONCURSANTE, LocalDate.now()));
        concursanteService.insert(new Concursante("Vegetta777", BCrypt.hashpw("a", BCrypt.gensalt()), "Samuel", "De Luque", "Batuecas", "600333444", "vegetta@planeta.com", IMG_VEGETTA, TipoRol.CONCURSANTE, LocalDate.now()));
        concursanteService.insert(new Concursante("Willyrex", BCrypt.hashpw("a", BCrypt.gensalt()), "Guillermo", "Díaz", "Ibáñez", "600555666", "willy@golem.com", IMG_WILLYREX, TipoRol.CONCURSANTE, LocalDate.now()));
        concursanteService.insert(new Concursante("Venom", BCrypt.hashpw("a", BCrypt.gensalt()), "Eddie", "Brock", "Symbiote", "600777888", "wearevenom@show.com", IMG_VENOM, TipoRol.CONCURSANTE, LocalDate.now()));

        // Concursantes sin foto de perfil
        concursanteService.insert(new Concursante("MisteriosoX", BCrypt.hashpw("a", BCrypt.gensalt()), "Juan", "Incógnito", "López", "611000111", "juan@incognito.com", "ic_default_avatar", TipoRol.CONCURSANTE, LocalDate.now()));
        concursanteService.insert(new Concursante("Anonimo22", BCrypt.hashpw("a", BCrypt.gensalt()), "Sara", "Nadie", "Gómez", "611000222", "sara@anonima.com", "ic_default_avatar", TipoRol.CONCURSANTE, LocalDate.now()));

        // ESPECTADORES
        espectadorService.insertar(new Espectador("RyuuFan", BCrypt.hashpw("1234", BCrypt.gensalt()), "Ryuu", "Lion", "Gale", "699888777", "ryuu@elfa.com", IMG_RYUU_LION, TipoRol.ESPECTADOR, LocalDate.now()));
        espectadorService.insertar(new Espectador("AlienCat", BCrypt.hashpw("1234", BCrypt.gensalt()), "Gato", "Cósmico", "Miau", "611222333", "gato@marte.com", IMG_GATO_ALIEN, TipoRol.ESPECTADOR, LocalDate.now()));
    }

    private void populateEdicionesGalasYNoticias() {
        Edicion ed1 = new Edicion(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 20);
        edicionService.insertarEdicion(ed1);

        // SOLICITUDES
        solicitudService.insert(new Solicitud(1, 1, "Vengo a demostrar que mi cosplay de Vader es el más imponente de la galaxia.", EstadoSolicitud.ACEPTADA));
        solicitudService.insert(new Solicitud(1, 2, "¡EYYYY muy buenas a todos aquí Vegetta777 y en el día de hoy me encuentro solicitando mi entrada a este reality para construir el refugio más increíble de Marte junto a mi compañero Willy!", EstadoSolicitud.ACEPTADA));
        solicitudService.insert(new Solicitud(1, 3, "¡Sinceramente Marte me parece el sitio perfecto para vivir aventuras! Entro con Vegetta para demostrar que somos el mejor equipo.", EstadoSolicitud.ACEPTADA));
        solicitudService.insert(new Solicitud(1, 4, "Hemos traído un esqueleto animado gigante que golpea su escudo para que nadie se olvide de nosotros.", EstadoSolicitud.ACEPTADA));

        // Solicitud de MisteriosoX trayendo decoración extraña
        solicitudService.insert(new Solicitud(1, 5, "No mostraré mi cara, pero he traído unos esqueletos flotantes espectrales para decorar mi cuarto. Dan un toque acogedor.", EstadoSolicitud.ACEPTADA));
        solicitudService.insert(new Solicitud(1, 6, "Prometo dar mucho salseo si me dejáis entrar.", EstadoSolicitud.PENDIENTE));

        galaService.insert(new Gala(1, LocalDate.of(2026, 3, 15)), LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

        // NOTICIAS
        noticiaService.insertarNoticia(new Noticia(LocalDate.now(),
                "¡POLÉMICA! Venom ha introducido un esqueleto gigante que no para de golpear su escudo. Los concursantes están al límite por la falta de sueño.",
                "EL REGALO 'RUIDOSO' DE VENOM", IMG_ESQUELETOS_ANIMADOS, 1, 1));

        noticiaService.insertarNoticia(new Noticia(LocalDate.now(),
                "Arturo (Vader) se niega a quitarse la máscara incluso durante las comidas. Algunos concursantes sospechan que oculta algo o que simplemente tiene miedo al aire de Marte.",
                "EL MISTERIO TRAS EL CASCO", IMG_COSPLAY_VADER, 1, 1));

        noticiaService.insertarNoticia(new Noticia(LocalDate.now(),
                "MisteriosoX ha decorado el pasillo con esqueletos flotantes que parecen seguir a los participantes con la mirada. La casa parece un túnel del terror.",
                "DECORACIÓN MACABRA EN LA BASE", IMG_ESQUELETOS_FLOTANTES, 1, 1));
    }

    public void deleteBD(Context c) {
        DatabaseHelper db = DatabaseHelper.getInstance(c);
        new Thread(() -> {
            try {
                // 1. Limpiar datos de las tablas
                db.clearAllTables();

                // 2. Reiniciar los contadores autoincrementales
                // Esto obliga a que el próximo insert sea ID 1
                db.getOpenHelper().getWritableDatabase().execSQL("DELETE FROM sqlite_sequence");

                android.util.Log.d("PopulateBD", "Tablas y contadores reiniciados");
            } catch (Exception e) {
                android.util.Log.e("PopulateBD", "Error al limpiar: " + e.getMessage());
            }
        }).start();
    }
}