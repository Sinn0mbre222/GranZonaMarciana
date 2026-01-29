package com.example.granzonamarciana.service;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.granzonamarciana.dao.EspectadorDao;
import com.example.granzonamarciana.database.DatabaseHelper;
import com.example.granzonamarciana.entity.Espectador;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EspectadorService {

    private final EspectadorDao espectadorDao;
    private final ExecutorService executor;

    public EspectadorService(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        this.espectadorDao = db.espectadorDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void insertar(Espectador espectador) {
        executor.execute(() -> espectadorDao.insert(espectador));
    }

    public void actualizar(Espectador espectador) {
        executor.execute(() -> espectadorDao.update(espectador));
    }

    public LiveData<Espectador> obtenerPorId(int id) {
        return espectadorDao.findById(id);
    }

    public LiveData<Espectador> buscarEspectadorPorUsername(String username) {
        return espectadorDao.findByUsername(username);
    }
}