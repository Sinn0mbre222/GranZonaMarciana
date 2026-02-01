package com.example.granzonamarciana.service;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.granzonamarciana.dao.GalaDao;
import com.example.granzonamarciana.dao.PuntuacionDao;
import com.example.granzonamarciana.database.DatabaseHelper;
import com.example.granzonamarciana.entity.Puntuacion;
import com.example.granzonamarciana.entity.pojo.PuntuacionConConcursante;

import java.util.List;

public class PuntuacionService {

    private final PuntuacionDao puntuacionDao;
    private final GalaDao galaDao;

    public PuntuacionService(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        this.puntuacionDao = db.puntuacionDao();
        this.galaDao = db.galaDao();
    }


    public LiveData<Boolean> haVotado(int galaId, int espectadorId, int concursanteId) {
        return puntuacionDao.haVotado(galaId, espectadorId, concursanteId);
    }
    public void puntuar(final Puntuacion p) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Room detectará automáticamente si intentas insertar un duplicado
                // gracias a la Primary Key compuesta que pusimos en la Entidad.
                puntuacionDao.insert(p);
            }
        }).start();
    }

    public LiveData<List<Puntuacion>> obtenerPuntuacionesGala(int galaId) {
        return puntuacionDao.findByGala(galaId);
    }

    public LiveData<Float> obtenerMediaConcursante(int galaId, int concursanteId) {
        return puntuacionDao.getMediaPuntuacion(galaId, concursanteId);
    }

    public LiveData<List<Puntuacion>> obtenerHistorialEspectador(int espectadorId) {
        return puntuacionDao.findByEspectador(espectadorId);
    }

    public LiveData<List<Puntuacion>> obtenerHistorialConcursante(int concursanteId) {
        return puntuacionDao.findByConcursante(concursanteId);
    }

    public LiveData<List<PuntuacionConConcursante>> obtenerResultadosGala(int galaId) {
        return puntuacionDao.getVotosConConcursanteByGala(galaId);
    }
}