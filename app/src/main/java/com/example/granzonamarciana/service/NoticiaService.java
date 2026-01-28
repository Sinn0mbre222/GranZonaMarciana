package com.example.granzonamarciana.service;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.granzonamarciana.dao.NoticiaDao;
import com.example.granzonamarciana.database.DatabaseHelper;
import com.example.granzonamarciana.entity.Noticia;

import java.util.List;

public class NoticiaService {

    private NoticiaDao noticiaDao;

    public NoticiaService(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        noticiaDao = db.noticiaDao();
    }

    public void insertarNoticia(final Noticia noticia) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                noticiaDao.insert(noticia);
            }
        }).start();
    }

    public void actualizarNoticia(final Noticia noticia) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                noticiaDao.update(noticia);
            }
        }).start();
    }

    public LiveData<Noticia> listarNoticiaePorid(int id) {
        return noticiaDao.findById(id);
    }

    public LiveData<List<Noticia>> listarNoticias(){
        return noticiaDao.findALl();
    }


}