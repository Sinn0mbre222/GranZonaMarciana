package com.example.granzonamarciana.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.granzonamarciana.entity.Administrador;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.entity.pojo.EdicionConGalas;
import com.example.granzonamarciana.entity.pojo.EdicionConNoticias;

import java.util.List;

@Dao
public interface EdicionDAO {
    @Insert
    void insert(Edicion edicion);

    @Query("SELECT * FROM ediciones WHERE id = :id")
    LiveData<Edicion> findById(int id);

    @Query("SELECT * FROM ediciones")
    LiveData<List<Edicion>> findALl();

    @Transaction // Necesario porque Room hace dos consultas internamente
    @Query("SELECT * FROM ediciones")
    LiveData<List<EdicionConNoticias>> getEdicionesConNoticias();

    @Transaction
    @Query("SELECT * FROM ediciones WHERE id = :edicionId")
    LiveData<EdicionConGalas> getEdicionConGalas(int edicionId);

    @Query("SELECT * FROM ediciones")
    List<Edicion> findAllSync();
}
