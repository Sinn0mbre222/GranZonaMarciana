package com.example.granzonamarciana.service;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.granzonamarciana.dao.AdministradorDao;
import com.example.granzonamarciana.database.DatabaseHelper;
import com.example.granzonamarciana.entity.Administrador;

import java.util.List;

public class AdministradorService {

    private AdministradorDao administradorDao;

    public AdministradorService(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        administradorDao = db.administradorDao();
    }

    public void insertarAdministrador(final Administrador administrador) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                administradorDao.insert(administrador);
            }
        }).start();
    }

    public void actualizarAdministrador(final Administrador administrador) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                administradorDao.update(administrador);
            }
        }).start();
    }

    public LiveData<List<Administrador>> listarAdministradores() {
        return administradorDao.findAll();
    }

    public LiveData<Administrador> buscarAdministradorPorId(int id) {
        return administradorDao.findById(id);
    }

    public LiveData<Administrador> buscarAdministradorPorUsername(String username) {
        return administradorDao.findByUsername(username);
    }

}