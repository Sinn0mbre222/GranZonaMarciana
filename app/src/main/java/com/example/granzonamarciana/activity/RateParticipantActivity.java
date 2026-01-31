package com.example.granzonamarciana.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.EstadoSolicitud;
import com.example.granzonamarciana.entity.Gala;
import com.example.granzonamarciana.entity.Puntuacion;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.service.GalaService;
import com.example.granzonamarciana.service.PuntuacionService;
import com.example.granzonamarciana.service.SolicitudService;
import com.squareup.picasso.Picasso;

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
    private String concursanteNombre, concursanteFoto;
    private List<Gala> listaGalasActivas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_participant);

        // 1. Recibir datos del Intent
        concursanteId = getIntent().getIntExtra("CONCURSANTE_ID", -1);
        concursanteNombre = getIntent().getStringExtra("CONCURSANTE_NOMBRE");
        concursanteFoto = getIntent().getStringExtra("CONCURSANTE_FOTO");

        if (concursanteId == -1) {
            finish();
            return;
        }

        initViews();
        initServices();

        // 2. Cargar UI inicial
        tvNombre.setText(concursanteNombre != null ? concursanteNombre : "Concursante");
        cargarImagenConcursante();

        // 3. Cargar galas disponibles
        cargarGalasActivas();

        // Listener del RatingBar
        ratingBar.setOnRatingBarChangeListener((rb, rating, fromUser) ->
                tvRatingValue.setText("Puntuación: " + (int)rating + "/5")
        );

        btnEnviar.setOnClickListener(v -> enviarVoto());
        //findViewById(R.id.btnBackRate).setOnClickListener(v -> finish());
    }

    private void initViews() {
        spinnerGalas = findViewById(R.id.spinnerActiveGala);
        ratingBar = findViewById(R.id.ratingBar);
        btnEnviar = findViewById(R.id.btnSubmitRating);
        tvNombre = findViewById(R.id.tvRateName);
        tvRatingValue = findViewById(R.id.tvRatingValue);
        ivFoto = findViewById(R.id.ivRatePhoto);
    }

    private void initServices() {
        solicitudService = new SolicitudService(this);
        galaService = new GalaService(this);
        puntuacionService = new PuntuacionService(this);
    }

    private void cargarImagenConcursante() {
        if (concursanteFoto != null && concursanteFoto.startsWith("http")) {
            Picasso.get()
                    .load(concursanteFoto)
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .into(ivFoto);
        } else {
            ivFoto.setImageResource(R.drawable.ic_default_avatar);
        }
    }

    private void cargarGalasActivas() {
        // Buscamos en qué edición está el concursante para traer sus galas
        solicitudService.getMisSolicitudes(concursanteId).observe(this, solicitudes -> {
            if (solicitudes != null) {
                for (Solicitud s : solicitudes) {
                    if (s.getEstado() == EstadoSolicitud.ACEPTADA) {
                        cargarGalasDeEdicion(s.getEditionId());
                        return;
                    }
                }
                Toast.makeText(this, "Concursante no activo.", Toast.LENGTH_SHORT).show();
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
                    LocalDate fechaGala = g.getFecha();
                    // Validación de 24h posteriores (Hoy o Ayer)
                    if (fechaGala.isEqual(hoy) || fechaGala.isEqual(hoy.minusDays(1))) {
                        listaGalasActivas.add(g);
                        nombresGalas.add("Gala " + g.getId() + " (" + fechaGala + ")");
                    }
                }
            }

            if (listaGalasActivas.isEmpty()) {
                Toast.makeText(this, "No hay galas abiertas hoy", Toast.LENGTH_LONG).show();
                btnEnviar.setEnabled(false);
                nombresGalas.add("Cerrado: No hay galas hoy");
            } else {
                btnEnviar.setEnabled(true);
            }

            // Spinner con diseño para fondo oscuro
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, R.layout.spinner_rol_item, nombresGalas
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGalas.setAdapter(adapter);
        });
    }

    private void enviarVoto() {
        int rating = (int) ratingBar.getRating();
        if (rating < 1) {
            Toast.makeText(this, "Mínimo 1 estrella", Toast.LENGTH_SHORT).show();
            return;
        }

        // CORRECCIÓN: Usar las mismas SharedPreferences que en ProfileActivity
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        int espectadorId = prefs.getInt("id", -1);

        if (espectadorId == -1) {
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        int pos = spinnerGalas.getSelectedItemPosition();
        if (pos >= 0 && pos < listaGalasActivas.size()) {
            Gala gala = listaGalasActivas.get(pos);

            Puntuacion voto = new Puntuacion(
                    gala.getId(),
                    espectadorId,
                    concursanteId,
                    rating,
                    LocalDate.now()
            );

            puntuacionService.votar(voto,
                    () -> { // Éxito
                        Toast.makeText(this, "¡Puntuación enviada!", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    msg -> Toast.makeText(this, msg, Toast.LENGTH_LONG).show() // Error (ya votó)
            );
        }
    }
}