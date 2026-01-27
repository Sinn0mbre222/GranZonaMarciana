package com.example.granzonamarciana.service;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.granzonamarciana.database.DatabaseHelper;
import com.example.granzonamarciana.dao.SolicitudDao;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.entity.EstadoSolicitud;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SolicitudService {

    private final SolicitudDao solicitudDao;
    private final ExecutorService executorService;

    public SolicitudService(Application application) {
        DatabaseHelper db = DatabaseHelper.getInstance(application);
        solicitudDao = db.solicitudDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Insertar una nueva solicitud
    public void insert(Solicitud solicitud) {
        executorService.execute(() -> solicitudDao.insert(solicitud));
    }

    // Obtener todas las solicitudes para el administrador
    public LiveData<List<Solicitud>> getAllSolicitudes() {
        return solicitudDao.findAll();
    }

    // Obtener las solicitudes de un concursante específico
    public LiveData<List<Solicitud>> getMisSolicitudes(int contestantId) {
        return solicitudDao.findByContestant(contestantId);
    }

    // Aceptar Solicitud
    public void aceptarSolicitud(Solicitud solicitud) {
        executorService.execute(() -> {
            solicitud.setEstado(EstadoSolicitud.ACEPTADA);
            solicitudDao.update(solicitud);
            // AQUÍ: Más adelante añadiremos la lógica para cancelar
            // el resto si se llega al máximo [cite: 71, 72]
        });
    }

    // Rechazar Solicitud
    public void rechazarSolicitud(Solicitud solicitud) {
        executorService.execute(() -> {
            solicitud.setEstado(EstadoSolicitud.RECHAZADA);
            solicitudDao.update(solicitud);
        });
    }
}