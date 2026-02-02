package com.example.granzonamarciana.service;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import com.example.granzonamarciana.database.DatabaseHelper;
import com.example.granzonamarciana.dao.SolicitudDao;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.entity.EstadoSolicitud;
import com.example.granzonamarciana.entity.pojo.SolicitudConConcursante;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SolicitudService {

    private final SolicitudDao solicitudDao;
    private final ExecutorService executorService;

    public interface OnValidationListener {
        void onSuccess();
        void onError(String message);
    }
    public SolicitudService(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        solicitudDao = db.solicitudDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Insertar una nueva solicitud
    public void insert(Solicitud solicitud) {
        executorService.execute(() -> solicitudDao.insert(solicitud));
    }
    public void insertConValidacion(Solicitud solicitud, OnValidationListener listener) {
        executorService.execute(() -> {
            // 1. Validar si ya envió una solicitud a esta edición
            int existentes = solicitudDao.countSolicitudesByUsuarioYEdicion(
                    solicitud.getEditionId(),
                    solicitud.getConcursanteId()
            );

            if (existentes > 0) {
                listener.onError("Ya has enviado una solicitud para esta edición.");
                return;
            }

            // 2. Validar aforo
            int aceptadas = solicitudDao.countAceptadasByEdition(solicitud.getEditionId());
            // Nota: El aforo deberías pasarlo como parámetro o consultarlo aquí
            // Para este ejemplo, supongamos que lo validamos en la Activity antes de llamar aquí

            solicitudDao.insert(solicitud);
            listener.onSuccess();
        });
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
    public void aceptarSolicitud(Solicitud solicitud, int maxParticipantes) {
        executorService.execute(() -> {
            // Contamos cuántos han sido ya aceptados en esta edición
            int aceptadas = solicitudDao.countAceptadasByEdition(solicitud.getEditionId());
            //Comprobamos si hay menos aceptadas que el maximo de participantes
            if (aceptadas < maxParticipantes) {
                // Aceptamos la solicitud actual
                solicitud.setEstado(EstadoSolicitud.ACEPTADA);
                solicitudDao.update(solicitud);

                // Comprobamos si con esta hemos llegado al límite
                if (aceptadas + 1 >= maxParticipantes) {
                    // CANCELACIÓN MASIVA: Todas las demás de esta edición pasan a RECHAZADA
                    solicitudDao.cancelarRestantes(solicitud.getEditionId(), EstadoSolicitud.RECHAZADA);
                }
            }
        });
    }

    // Rechazar Solicitud
    public void rechazarSolicitud(Solicitud solicitud) {
        executorService.execute(() -> {
            solicitud.setEstado(EstadoSolicitud.RECHAZADA);
            solicitudDao.update(solicitud);
        });
    }

    public LiveData<List<SolicitudConConcursante>> obtenerAceptadosPorEdicion(int editionId) {
        return solicitudDao.getParticipantesAceptados(editionId);
    }

    public int contarAceptadosSync(int editionId) {
        return solicitudDao.countAceptadasByEdition(editionId);
    }
}