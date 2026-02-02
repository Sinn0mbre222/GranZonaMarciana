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

    // LISTAR TODAS (Para NewsListActivity)
    public LiveData<List<Noticia>> listarNoticias() {
        return noticiaDao.findALl();
    }

    // BUSCAR UNA (Para NewsDetailActivity y Edición)
    public LiveData<Noticia> buscarPorId(int id) {
        return noticiaDao.findById(id);
    }

    // CREAR (Para CreateEditNewsActivity)
    public void insertarNoticia(final Noticia noticia) {
        new Thread(() -> noticiaDao.insert(noticia)).start();
    }

    // ACTUALIZAR (Para CreateEditNewsActivity)
    public void actualizarNoticia(final Noticia noticia) {
        new Thread(() -> noticiaDao.update(noticia)).start();
    }

    // ELIMINAR (Para NewsDetailActivity)
    public void eliminarNoticia(final Noticia noticia) {
        new Thread(() -> noticiaDao.delete(noticia)).start();
    }
}