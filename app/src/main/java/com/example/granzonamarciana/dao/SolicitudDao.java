package com.example.granzonamarciana.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.granzonamarciana.entity.Solicitud;
import java.util.List;

@Dao
public interface SolicitudDao {

    @Insert
    void insert(Solicitud solicitud);

    @Update
    void update(Solicitud solicitud);

    // Para que el administrador vea todas las solicitudes
    @Query("SELECT * FROM solicitudes")
    LiveData<List<Solicitud>> findAll();

    // Para que un concursante vea solo las suyas
    @Query("SELECT * FROM solicitudes WHERE contestantId = :contestantId")
    LiveData<List<Solicitud>> findByContestant(int contestantId);
}