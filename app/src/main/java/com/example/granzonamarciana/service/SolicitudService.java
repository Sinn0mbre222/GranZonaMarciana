package com.example.granzonamarciana.service;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.granzonamarciana.database.DatabaseHelper;
import com.example.granzonamarciana.dao.SolicitudDao;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.entity.EstadoSolicitud;
import com.example.granzonamarciana.entity.pojo.SolicitudConConcursante;
import java.util.List;

public class SolicitudService {

    private final SolicitudDao solicitudDao;

    public interface OnValidationListener {
        void onSuccess();
        void onError(String message);
    }

    public SolicitudService(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        solicitudDao = db.solicitudDao();
    }

    public LiveData<List<SolicitudConConcursante>> getSolicitudesByEdicion(int edicionId) {
        return solicitudDao.findByEdicion(edicionId);
    }

    // Insertar usando hilos manuales
    public void insert(Solicitud solicitud) {
        new Thread(() -> solicitudDao.insert(solicitud)).start();
    }

    public void insertConValidacion(Solicitud solicitud, OnValidationListener listener) {
        new Thread(() -> {
            int existentes = solicitudDao.countSolicitudesByUsuarioYEdicion(
                    solicitud.getEditionId(),
                    solicitud.getConcursanteId()
            );

            if (existentes > 0) {
                listener.onError("Ya has enviado una solicitud para esta edición.");
            } else {
                solicitudDao.insert(solicitud);
                listener.onSuccess();
            }
        }).start();
    }

    public LiveData<List<SolicitudConConcursante>> getAllSolicitudes() {
        return solicitudDao.findAllConDetalle();
    }

    public void aceptarSolicitud(Solicitud solicitud, int maxParticipantes) {
        new Thread(() -> {
            int aceptadas = solicitudDao.countAceptadasByEdition(solicitud.getEditionId());
            if (aceptadas < maxParticipantes) {
                solicitud.setEstado(EstadoSolicitud.ACEPTADA);
                solicitudDao.update(solicitud);

                if (aceptadas + 1 >= maxParticipantes) {
                    solicitudDao.cancelarRestantes(solicitud.getEditionId(), EstadoSolicitud.RECHAZADA);
                }
            }
        }).start();
    }

    public void rechazarSolicitud(Solicitud solicitud) {
        new Thread(() -> {
            solicitud.setEstado(EstadoSolicitud.RECHAZADA);
            solicitudDao.update(solicitud);
        }).start();
    }

    public LiveData<List<SolicitudConConcursante>> obtenerAceptadosPorEdicion(int editionId) {
        return solicitudDao.getParticipantesAceptados(editionId);
    }
    // En SolicitudService.java
    public LiveData<List<SolicitudConConcursante>> getMisSolicitudes(int contestantId) {
        // Asegúrate de que tu DAO tenga una consulta que devuelva el POJO filtrado por concursante
        return solicitudDao.findByContestantConDetalle(contestantId);
    }

    public LiveData<SolicitudConConcursante> getSolicitudById(int id) {
        return solicitudDao.findByIdConDetalle(id);
    }
    public int contarAceptadosSync(int editionId) {
        return solicitudDao.countAceptadasByEdition(editionId);
    }

}