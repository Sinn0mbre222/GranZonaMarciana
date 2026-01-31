package com.example.granzonamarciana.service;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import com.example.granzonamarciana.database.DatabaseHelper;
import com.example.granzonamarciana.dao.GalaDao;
import com.example.granzonamarciana.entity.Gala;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GalaService {

    private final GalaDao galaDao;
    private final ExecutorService executorService;

    public GalaService(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        galaDao = db.galaDao();
        this.executorService = Executors.newFixedThreadPool(4);
    }

    // Insertar una gala si esta entre las fechas de inicio y fin
    public boolean insert(Gala gala, LocalDate inicioEdicion, LocalDate finEdicion) {
        if (gala.getFecha().isBefore(inicioEdicion) || gala.getFecha().isAfter(finEdicion)) {
            return false;
        }
        executorService.execute(() -> galaDao.insert(gala));
        return true;
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