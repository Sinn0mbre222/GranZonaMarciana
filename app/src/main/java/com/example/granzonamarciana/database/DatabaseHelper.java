package com.example.granzonamarciana.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.granzonamarciana.dao.*;
import com.example.granzonamarciana.entity.*;

@Database(entities = {
        Administrador.class,
        Solicitud.class,
        Gala.class,
        Edicion.class,
        Noticia.class,
        Concursante.class,
        Espectador.class,
        Puntuacion.class
}, version = 1)
@TypeConverters({LocalDateConverter.class, EstadoSolicitudConverter.class})
public abstract class DatabaseHelper extends RoomDatabase {

    public abstract AdministradorDao administradorDao();
    public abstract SolicitudDao solicitudDao();
    public abstract GalaDao galaDao();
    public abstract EdicionDAO edicionDao();
    public abstract NoticiaDao noticiaDao();
    public abstract ConcursanteDao concursanteDao();
    public abstract EspectadorDao espectadorDao();
    public abstract PuntuacionDao puntuacionDao();

    private static volatile DatabaseHelper instanciaBD;

    public static DatabaseHelper getInstance(Context context) {
        if (instanciaBD == null) {
            synchronized (DatabaseHelper.class) {
                if (instanciaBD == null) {
                    instanciaBD = Room.databaseBuilder(context.getApplicationContext(),
                                    DatabaseHelper.class, "granzona_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instanciaBD;
    }
}