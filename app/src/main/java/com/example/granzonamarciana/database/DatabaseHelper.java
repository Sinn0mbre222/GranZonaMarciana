package com.example.granzonamarciana.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.granzonamarciana.dao.AdministradorDao;
import com.example.granzonamarciana.dao.GalaDao;
import com.example.granzonamarciana.dao.SolicitudDao;
import com.example.granzonamarciana.entity.Administrador;
import com.example.granzonamarciana.entity.Gala;
import com.example.granzonamarciana.entity.Solicitud;

@Database(entities = {
        Administrador.class,
        Solicitud.class,
        Gala.class,
        // Agregar aquí las demás clases que se vayan creando
}, version = 1)
@TypeConverters({LocalDateConverter.class})
public abstract class DatabaseHelper extends RoomDatabase {

    //Añadir aquí los demás dao
    public abstract AdministradorDao administradorDao();
    public abstract SolicitudDao solicitudDao();
    public abstract GalaDao galaDao();

    private static volatile DatabaseHelper instanciaBD;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instanciaBD == null) {
            instanciaBD = Room.databaseBuilder(context.getApplicationContext(),
                            DatabaseHelper.class, "granzona_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instanciaBD;
    }

}
