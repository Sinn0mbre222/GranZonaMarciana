package com.example.granzonamarciana.service;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.granzonamarciana.dao.GalaDao;
import com.example.granzonamarciana.dao.PuntuacionDao;
import com.example.granzonamarciana.database.DatabaseHelper;
import com.example.granzonamarciana.entity.Gala;
import com.example.granzonamarciana.entity.Puntuacion;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PuntuacionService {

    private final PuntuacionDao puntuacionDao;
    private final GalaDao galaDao; // Necesitamos ver la fecha de la gala
    private final ExecutorService executor;

    public PuntuacionService(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        this.puntuacionDao = db.puntuacionDao();
        this.galaDao = db.galaDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public interface VotoCallback {
        void onExito();
        void onError(String mensaje);
    }

    // Metodo principal para votar con validaciones
    public void votar(Puntuacion puntuacion, VotoCallback callback) {
        executor.execute(() -> {
            // 1. Validar duplicados
            Puntuacion existente = puntuacionDao.findVotoExistente(
                    puntuacion.getGalaId(),
                    puntuacion.getEspectadorId(),
                    puntuacion.getConcursanteId()
            );

            if (existente != null) {
                callback.onError("Ya has votado a este concursante en esta gala.");
                return;
            }

            // 2. Insertar voto (La validación de fecha la haremos en la Activity antes de llamar aquí para poder mostrar la fecha al usuario,
            // pero podríamos añadirla aquí también si tenemos la Gala a mano).
            puntuacionDao.insert(puntuacion);
            callback.onExito();
        });
    }

    public LiveData<List<Puntuacion>> obtenerPuntuacionesGala(int galaId) {
        return puntuacionDao.findByGala(galaId);
    }

    public LiveData<Float> obtenerMediaConcursante(int galaId, int concursanteId) {
        return puntuacionDao.getMediaPuntuacion(galaId, concursanteId);
    }

    // Para el historial del espectador
    public LiveData<List<Puntuacion>> obtenerHistorialEspectador(int espectadorId) {
        return puntuacionDao.findByEspectador(espectadorId);
    }

    // Obtener Historial del concursante
    public androidx.lifecycle.LiveData<java.util.List<Puntuacion>> obtenerHistorialConcursante(int concursanteId) {
        return puntuacionDao.findByConcursante(concursanteId); // Este metodo ya esta en el DAO
    }

    // Metodo para votar
    public void votar(Puntuacion puntuacion, Runnable onSuccess, java.util.function.Consumer<String> onError) {
        executor.execute(() -> {
            // 1. Comprobar si ya existe voto
            Puntuacion existe = puntuacionDao.findVotoExistente(
                    puntuacion.getGalaId(),
                    puntuacion.getEspectadorId(),
                    puntuacion.getConcursanteId()
            );

            if (existe != null) {
                // Ya votó -> Error
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                        onError.accept("Ya has votado a este concursante en esta gala.")
                );
            } else {
                // 2. Insertar
                puntuacionDao.insert(puntuacion);
                new android.os.Handler(android.os.Looper.getMainLooper()).post(onSuccess);
            }
        });
    }

}