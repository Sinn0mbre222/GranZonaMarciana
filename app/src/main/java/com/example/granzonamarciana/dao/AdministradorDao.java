package com.example.granzonamarciana.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.example.granzonamarciana.entity.Administrador;

import java.util.List;


@Dao
public interface AdministradorDao {
    @Insert
    void insert(Administrador administrador);

    @Update
    void update(Administrador administrador);

    @Query("SELECT * FROM administradores WHERE id = :id")
    LiveData<Administrador> findById(int id);

    @Query("SELECT * FROM administradores WHERE username = :username")
    LiveData<Administrador> findByUsername(String username);

    @Query("SELECT * FROM administradores")
    LiveData<List<Administrador>> findAll();

}
