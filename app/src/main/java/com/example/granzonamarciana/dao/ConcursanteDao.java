package com.example.granzonamarciana.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.granzonamarciana.entity.Concursante;
import java.util.List;

@Dao
public interface ConcursanteDao {

    @Insert
    void insert(Concursante concursante);

    @Update
    void update(Concursante concursante);

    @Query("SELECT * FROM concursantes WHERE id = :id")
    LiveData<Concursante> findById(int id);

    @Query("SELECT * FROM concursantes WHERE username = :username")
    LiveData<Concursante> findByUsername(String username);

    // Consulta importante: Obtener concursantes ACEPTADOS de una edición específica
    @Query("SELECT * FROM concursantes WHERE id IN (SELECT contestantId FROM solicitudes WHERE editionId = :editionId AND estado = 'ACEPTADA')")
    LiveData<List<Concursante>> findByEditionId(int editionId);

    @Query("SELECT * FROM concursantes")
    LiveData<List<Concursante>> findAll();
}