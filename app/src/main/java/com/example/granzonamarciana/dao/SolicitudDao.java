package com.example.granzonamarciana.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.granzonamarciana.entity.EstadoSolicitud;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.entity.pojo.SolicitudConConcursante;

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
    @Query("SELECT * FROM solicitudes WHERE concursanteId = :contestantId")
    LiveData<List<Solicitud>> findByContestant(int contestantId);
    // Cuenta cuantas solicitudes hay aceptadas
    @Query("SELECT COUNT(*) FROM solicitudes WHERE editionId = :editionId AND estado = 'ACEPTADA'")
    int countAceptadasByEdition(int editionId);
    // Todos los que esten en pendiente pasan a ser cancelados
    @Query("UPDATE solicitudes SET estado = :nuevoEstado WHERE editionId = :editionId AND estado = 'PENDIENTE'")
    void cancelarRestantes(int editionId, EstadoSolicitud nuevoEstado);

    @Transaction
    @Query("SELECT * FROM solicitudes WHERE editionId = :editionId AND estado = 'ACEPTADA'")
    LiveData<List<SolicitudConConcursante>> getParticipantesAceptados(int editionId);

    // También podrías querer ver las PENDIENTES para el Admin
    @Transaction
    @Query("SELECT * FROM solicitudes WHERE editionId = :editionId AND estado = 'PENDIENTE'")
    LiveData<List<SolicitudConConcursante>> getSolicitudesPendientes(int editionId);
}