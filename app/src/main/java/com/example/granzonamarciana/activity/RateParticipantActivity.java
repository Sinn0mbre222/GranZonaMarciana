package com.example.granzonamarciana.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.EstadoSolicitud;
import com.example.granzonamarciana.entity.Gala;
import com.example.granzonamarciana.entity.Puntuacion;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.service.GalaService;
import com.example.granzonamarciana.service.PuntuacionService;
import com.example.granzonamarciana.service.SolicitudService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RateParticipantActivity extends AppCompatActivity {

    private Spinner spinnerGalas;
    private RatingBar ratingBar;
    private Button btnEnviar;
    private TextView tvNombre, tvRatingValue;
    private ImageView ivFoto;

    private SolicitudService solicitudService;
    private GalaService galaService;
    private PuntuacionService puntuacionService;

    private int concursanteId;
    private String concursanteNombre;
    private List<Gala> listaGalasActivas; // Para saber qué ID tiene la gala seleccionada en el spinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_participant);

        // Recibir datos
        concursanteId = getIntent().getIntExtra("CONCURSANTE_ID", -1);
        concursanteNombre = getIntent().getStringExtra("CONCURSANTE_NOMBRE");

        if (concursanteId == -1) {
            finish();
            return;
        }

        initViews();
        initServices();

        // Cargar datos iniciales
        tvNombre.setText(concursanteNombre != null ? concursanteNombre : "Concursante");

        // Cargar galas disponibles para votar
        cargarGalasActivas();

        // Listener del RatingBar para actualizar el texto
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) ->
                tvRatingValue.setText("Puntuación: " + (int)rating + "/5")
        );

        // Botón Enviar
        btnEnviar.setOnClickListener(v -> enviarVoto());
    }

    private void initViews() {
        spinnerGalas = findViewById(R.id.spinnerActiveGala);
        ratingBar = findViewById(R.id.ratingBar);
        btnEnviar = findViewById(R.id.btnSubmitRating);
        tvNombre = findViewById(R.id.tvRateName);
        tvRatingValue = findViewById(R.id.tvRatingValue);
        ivFoto = findViewById(R.id.ivRatePhoto);

        ivFoto.setImageResource(R.drawable.ic_default_avatar); // Placeholder
    }

    private void initServices() {
        solicitudService = new SolicitudService(getApplication());
        galaService = new GalaService(getApplication());
        puntuacionService = new PuntuacionService(this);
    }

    private void cargarGalasActivas() {
        // 1. Primero averiguamos la edición del concursante buscando su solicitud ACEPTADA
        solicitudService.getMisSolicitudes(concursanteId).observe(this, solicitudes -> {
            if (solicitudes != null) {
                for (Solicitud s : solicitudes) {
                    if (s.getEstado() == EstadoSolicitud.ACEPTADA) {
                        // 2. Cargamos las galas de esa edición
                        cargarGalasDeEdicion(s.getEditionId());
                        return;
                    }
                }
                Toast.makeText(this, "Este concursante no está activo en ninguna edición", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarGalasDeEdicion(int editionId) {
        galaService.getGalasByEdicion(editionId).observe(this, galas -> {
            listaGalasActivas = new ArrayList<>();
            List<String> nombresGalas = new ArrayList<>();
            LocalDate hoy = LocalDate.now();

            if (galas != null) {
                for (Gala g : galas) {
                    // VALIDACIÓN DE FECHA (REQUISITO PDF):
                    // "Solo durante 24h posteriores al inicio de la gala"
                    // Permitimos votar si la gala es HOY o AYER.
                    LocalDate fechaGala = g.getFecha();

                    boolean esHoy = fechaGala.isEqual(hoy);
                    boolean esAyer = fechaGala.isEqual(hoy.minusDays(1));

                    if (esHoy || esAyer) {
                        listaGalasActivas.add(g);
                        nombresGalas.add("Gala " + g.getId() + " (" + fechaGala + ")");
                    }
                }
            }

            if (listaGalasActivas.isEmpty()) {
                Toast.makeText(this, "No hay galas activas para votar ahora mismo", Toast.LENGTH_LONG).show();
                btnEnviar.setEnabled(false); // Deshabilitar botón
                nombresGalas.add("Sin galas activas");
            } else {
                btnEnviar.setEnabled(true);
            }

            // Rellenar Spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    nombresGalas
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGalas.setAdapter(adapter);
        });
    }

    private void enviarVoto() {
        if (listaGalasActivas == null || listaGalasActivas.isEmpty()) return;

        int rating = (int) ratingBar.getRating();
        if (rating < 1) {
            Toast.makeText(this, "La puntuación mínima es 1 estrella", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener usuario logueado
        SharedPreferences prefs = getSharedPreferences("GranZonaMarcianaPrefs", Context.MODE_PRIVATE);
        int espectadorId = prefs.getInt("USER_ID", -1);

        if (espectadorId == -1) {
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener ID de la gala seleccionada
        int pos = spinnerGalas.getSelectedItemPosition();
        if (pos >= 0 && pos < listaGalasActivas.size()) {
            Gala galaSeleccionada = listaGalasActivas.get(pos);

            // Crear objeto Puntuación
            Puntuacion voto = new Puntuacion(
                    galaSeleccionada.getId(),
                    espectadorId,
                    concursanteId,
                    rating,
                    LocalDate.now()
            );

            // Guardar con validación
            puntuacionService.votar(voto,
                    () -> { // OnSuccess
                        Toast.makeText(this, "¡Voto enviado correctamente!", Toast.LENGTH_SHORT).show();
                        finish(); // Cerrar pantalla al terminar
                    },
                    errorMsg -> { // OnError
                        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                    }
            );
        }
    }
}