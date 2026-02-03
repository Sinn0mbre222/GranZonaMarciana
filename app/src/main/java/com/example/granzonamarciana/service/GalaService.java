package com.example.granzonamarciana.service;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.granzonamarciana.database.DatabaseHelper;
import com.example.granzonamarciana.dao.GalaDao;
import com.example.granzonamarciana.entity.Gala;

import java.time.LocalDate;
import java.util.List;

public class GalaService {

    private final GalaDao galaDao;

    public GalaService(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        galaDao = db.galaDao();
    }

    public boolean insert(Gala gala, LocalDate inicioEdicion, LocalDate finEdicion) {
        if (gala.getFecha().isBefore(inicioEdicion) || gala.getFecha().isAfter(finEdicion)) {
            return false;
        }
        // Uso de Thread manual
        new Thread(() -> galaDao.insert(gala)).start();
        return true;
    }

    public void eliminar(Gala gala) {
        new Thread(() -> galaDao.delete(gala)).start();
    }

    public LiveData<List<Gala>> getGalasByEdicion(int editionId) {
        return galaDao.findByEdition(editionId);
    }

    public LiveData<Gala> getGalaById(int id) {
        return galaDao.findById(id);
    }
}