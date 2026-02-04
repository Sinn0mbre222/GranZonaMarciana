package com.example.granzonamarciana.service;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.granzonamarciana.dao.ConcursanteDao;
import com.example.granzonamarciana.database.DatabaseHelper;
import com.example.granzonamarciana.entity.Concursante;
import java.util.List;

public class ConcursanteService {

    private final ConcursanteDao concursanteDao;

    public ConcursanteService(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        this.concursanteDao = db.concursanteDao();
    }


    public void insert(Concursante concursante) {
        new Thread(() -> concursanteDao.insert(concursante)).start();
    }

    public void actualizar(Concursante concursante) {
        new Thread(() -> concursanteDao.update(concursante)).start();
    }

    public void eliminar(Concursante concursante) {
        new Thread(() -> concursanteDao.delete(concursante)).start();
    }

    public LiveData<Concursante> obtenerPorId(int id) {
        return concursanteDao.findById(id);
    }

    public LiveData<Concursante> buscarConcursantePorUsername(String username) {
        return concursanteDao.findByUsername(username);
    }

    public LiveData<List<Concursante>> obtenerPorEdicion(int editionId) {
        return concursanteDao.findByEditionId(editionId);
    }

    public LiveData<List<Concursante>> obtenerTodos() {
        return concursanteDao.findAll();
    }
}