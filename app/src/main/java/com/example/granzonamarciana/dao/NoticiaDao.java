package com.example.granzonamarciana.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.granzonamarciana.entity.Noticia;

import java.util.List;

@Dao
public interface NoticiaDao {
    @Insert
    void insert(Noticia noticia);

    @Update
    void update(Noticia noticia);

    @Query("SELECT * FROM noticias WHERE id = :id")
    LiveData<Noticia> findById(int id);

    @Query("SELECT * FROM noticias")
    LiveData<List<Noticia>> findALl();


}
