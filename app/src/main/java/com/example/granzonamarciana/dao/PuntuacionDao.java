package com.example.granzonamarciana.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.granzonamarciana.entity.Puntuacion;
import com.example.granzonamarciana.entity.pojo.PuntuacionConConcursante;

import java.util.List;

@Dao
public interface PuntuacionDao {

    @Insert
    void insert(Puntuacion puntuacion);

    // Para comprobar si ya ha votado
    @Query("SELECT * FROM puntuaciones WHERE galaId = :galaId AND espectadorId = :espectadorId AND concursanteId = :concursanteId")
    Puntuacion findVotoExistente(int galaId, int espectadorId, int concursanteId);

    // Para ver las puntuaciones de una gala
    @Query("SELECT * FROM puntuaciones WHERE galaId = :galaId")
    LiveData<List<Puntuacion>> findByGala(int galaId);

    // Para calcular la media de un concursante en una gala
    @Query("SELECT AVG(valor) FROM puntuaciones WHERE galaId = :galaId AND concursanteId = :concursanteId")
    LiveData<Float> getMediaPuntuacion(int galaId, int concursanteId);

    // Historial para Espectador
    @Query("SELECT * FROM puntuaciones WHERE espectadorId = :espectadorId")
    LiveData<List<Puntuacion>> findByEspectador(int espectadorId);

    // Historial para Concursante (sus votos recibidos)
    @Query("SELECT * FROM puntuaciones WHERE concursanteId = :concursanteId")
    LiveData<List<Puntuacion>> findByConcursante(int concursanteId);
    @Transaction
    @Query("SELECT * FROM puntuaciones WHERE galaId = :galaId")
    LiveData<List<PuntuacionConConcursante>> getVotosConConcursanteByGala(int galaId);

}