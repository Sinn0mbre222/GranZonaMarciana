package com.example.granzonamarciana.service;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.granzonamarciana.dao.EspectadorDao;
import com.example.granzonamarciana.database.DatabaseHelper;
import com.example.granzonamarciana.entity.Espectador;

import java.util.List;

public class EspectadorService {

    private final EspectadorDao espectadorDao;

    public EspectadorService(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        this.espectadorDao = db.espectadorDao();
    }

    public void insertar(Espectador espectador) {
        new Thread(() -> espectadorDao.insert(espectador)).start();
    }

    public void actualizar(Espectador espectador) {
        new Thread(() -> espectadorDao.update(espectador)).start();
    }

    public LiveData<Espectador> obtenerPorId(int id) {
        return espectadorDao.findById(id);
    }

    public LiveData<Espectador> buscarEspectadorPorUsername(String username) {
        return espectadorDao.findByUsername(username);
    }

    public LiveData<List<Espectador>> obtenerTodos() {
        return espectadorDao.findAll();
    }
}