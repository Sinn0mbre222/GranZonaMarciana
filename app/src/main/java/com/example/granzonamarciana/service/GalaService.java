package com.example.granzonamarciana.service;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.granzonamarciana.database.DatabaseHelper;
import com.example.granzonamarciana.dao.GalaDao;
import com.example.granzonamarciana.entity.Gala;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GalaService {

    private final GalaDao galaDao;
    private final ExecutorService executorService;

    public GalaService(Application application) {
        DatabaseHelper db = DatabaseHelper.getInstance(application);
        galaDao = db.galaDao();
        // Usamos un pool de hilos para operaciones en segundo plano
        this.executorService = Executors.newFixedThreadPool(4);
    }

    // Insertar una gala
    public void insert(Gala gala) {
        executorService.execute(() -> galaDao.insert(gala));
    }

    // Obtener las galas de una edición
    public LiveData<List<Gala>> getGalasByEdicion(int editionId) {
        return galaDao.findByEdition(editionId);
    }

    // Buscar una gala específica por id
    public LiveData<Gala> getGalaById(int id) {
        return galaDao.findById(id);
    }
}