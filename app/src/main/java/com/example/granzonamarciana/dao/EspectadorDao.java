package com.example.granzonamarciana.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.granzonamarciana.entity.Espectador;
import java.util.List;

@Dao
public interface EspectadorDao {

    @Insert
    void insert(Espectador espectador);

    @Update
    void update(Espectador espectador);

    @Query("SELECT * FROM espectadores WHERE id = :id")
    LiveData<Espectador> findById(int id);

    @Query("SELECT * FROM espectadores WHERE username = :username")
    LiveData<Espectador> findByUsername(String username);

    @Query("SELECT * FROM espectadores")
    LiveData<List<Espectador>> findAll();
}