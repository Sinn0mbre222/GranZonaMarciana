package com.example.granzonamarciana.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.granzonamarciana.entity.Administrador;
import com.example.granzonamarciana.entity.Edicion;

import java.util.List;

@Dao
public interface EdicionDAO {
    @Insert
    void insert(Edicion edicion);

    @Query("SELECT * FROM ediciones WHERE id = :id")
    LiveData<Edicion> findById(int id);

    @Query("SELECT * FROM ediciones")
    LiveData<List<Edicion>> findALl();


}
