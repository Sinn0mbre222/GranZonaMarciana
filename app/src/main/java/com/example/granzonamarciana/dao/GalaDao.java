package com.example.granzonamarciana.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.granzonamarciana.entity.Gala;
import java.util.List;

@Dao
public interface GalaDao {

    @Insert
    void insert(Gala gala);

    // Consultar todas las galas de una edición específica
    @Query("SELECT * FROM galas WHERE editionId = :editionId ORDER BY fecha ASC")
    LiveData<List<Gala>> findByEdition(int editionId);

    @Query("SELECT * FROM galas WHERE id = :id")
    LiveData<Gala> findById(int id);
}