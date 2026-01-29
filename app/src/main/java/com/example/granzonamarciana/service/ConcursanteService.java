package com.example.granzonamarciana.service;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.granzonamarciana.dao.ConcursanteDao;
import com.example.granzonamarciana.database.DatabaseHelper;
import com.example.granzonamarciana.entity.Concursante;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcursanteService {

    private final ConcursanteDao concursanteDao;
    private final ExecutorService executor;

    public ConcursanteService(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        this.concursanteDao = db.concursanteDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void insert(Concursante concursante) {
        executor.execute(() -> concursanteDao.insert(concursante));
    }

    public void actualizar(Concursante concursante) {
        executor.execute(() -> concursanteDao.update(concursante));
    }

    public LiveData<Concursante> obtenerPorId(int id) {
        return concursanteDao.findById(id);
    }

    public LiveData<Concursante> buscarConcursantePorUsername(String username) {
        return concursanteDao.findByUsername(username);
    }

    // Devuelve la lista de concursantes ACEPTADOS en una edición
    public LiveData<List<Concursante>> obtenerPorEdicion(int editionId) {
        return concursanteDao.findByEditionId(editionId);
    }

    public LiveData<List<Concursante>> obtenerTodos() {
        return concursanteDao.findAll(); // Ya existe en el DAO
    }
}