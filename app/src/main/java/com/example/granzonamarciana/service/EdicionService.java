package com.example.granzonamarciana.service;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.granzonamarciana.dao.AdministradorDao;
import com.example.granzonamarciana.dao.EdicionDAO;
import com.example.granzonamarciana.database.DatabaseHelper;
import com.example.granzonamarciana.entity.Administrador;
import com.example.granzonamarciana.entity.Edicion;

import java.util.List;

public class EdicionService {

    private EdicionDAO edicionDao;

    public EdicionService(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        edicionDao = db.edicionDAO();
    }

    public void insertarEdicion(final Edicion edicion) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                edicionDao.insert(edicion);
            }
        }).start();
    }

    public LiveData<Edicion> listarEdicionePorid(int id) {
        return edicionDao.findById(id);
    }

    public LiveData<List<Edicion>> listarEdiciones(){
        return edicionDao.findALl();
    }


}