package com.example.granzonamarciana.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.granzonamarciana.entity.EstadoSolicitud;
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
    // Cuenta cuantas solicitudes hay aceptadas
    @Query("SELECT COUNT(*) FROM solicitudes WHERE editionId = :editionId AND estado = 'ACEPTADA'")
    int countAceptadasByEdition(int editionId);
    // Todos los que esten en pendiente pasan a ser cancelados
    @Query("UPDATE solicitudes SET estado = :nuevoEstado WHERE editionId = :editionId AND estado = 'PENDIENTE'")
    void cancelarRestantes(int editionId, EstadoSolicitud nuevoEstado);
}