package com.example.granzonamarciana.activity;

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
    private int espectadorId;
    private String concursanteNombre, concursanteFoto;
    private List<Gala> listaGalasActivas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_participant);

        // 1. Recuperar datos del Intent
        concursanteId = getIntent().getIntExtra("CONCURSANTE_ID", -1);
        concursanteNombre = getIntent().getStringExtra("CONCURSANTE_NOMBRE");
        concursanteFoto = getIntent().getStringExtra("CONCURSANTE_FOTO");

        if (concursanteId == -1) {
            Toast.makeText(this, "Error: Concursante no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Recuperar sesión (SharedPrefs granZMUser)
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        espectadorId = prefs.getInt("id", -1);

        if (espectadorId == -1) {
            Toast.makeText(this, "Debe iniciar sesión para votar", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initServices();

        tvNombre.setText(concursanteNombre != null ? concursanteNombre : "Participante");
        cargarImagenConcursante();
        cargarGalasActivas();

        // Listener del Spinner para comprobar duplicados al cambiar de gala
        spinnerGalas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (listaGalasActivas != null && !listaGalasActivas.isEmpty()) {
                    Gala seleccionada = listaGalasActivas.get(position);
                    vincularObservadorVoto(seleccionada.getId());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ratingBar.setOnRatingBarChangeListener((rb, rating, fromUser) ->
                tvRatingValue.setText("Puntuación: " + (int)rating + "/5")
        );

        btnEnviar.setOnClickListener(v -> enviarVoto());


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
            Picasso.get().load(concursanteFoto)
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .into(ivFoto);
        } else {
            ivFoto.setImageResource(R.drawable.ic_default_avatar);
        }
    }

    /**
     * Lógica de "Favorito": Observa si ya existe el voto en la BD.
     * Si el LiveData detecta el voto, informa y cierra la actividad.
     */
    private void vincularObservadorVoto(int galaId) {
        puntuacionService.haVotado(galaId, espectadorId, concursanteId).observe(this, yaVotado -> {
            if (yaVotado != null && yaVotado) {
                Toast.makeText(this, "Ya has valorado a este participante en esta gala", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void cargarGalasActivas() {
        solicitudService.getMisSolicitudes(concursanteId).observe(this, solicitudes -> {
            if (solicitudes != null) {
                for (Solicitud s : solicitudes) {
                    if (s.getEstado() == EstadoSolicitud.ACEPTADA) {
                        cargarGalasDeEdicion(s.getEditionId());
                        return;
                    }
                }
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
                    LocalDate fGala = g.getFecha();
                    // Requisito PDF: 24h posteriores (Hoy o Ayer)
                    if (fGala.isEqual(hoy) || fGala.isEqual(hoy.minusDays(1))) {
                        listaGalasActivas.add(g);
                        nombresGalas.add("Gala " + g.getId() + " (" + fGala + ")");
                    }
                }
            }

            if (listaGalasActivas.isEmpty()) {
                btnEnviar.setEnabled(false);
                nombresGalas.add("No hay galas disponibles");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_rol_item, nombresGalas);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGalas.setAdapter(adapter);
        });
    }

    private void enviarVoto() {
        int rating = (int) ratingBar.getRating();
        if (rating < 1) {
            Toast.makeText(this, "Selecciona al menos una estrella", Toast.LENGTH_SHORT).show();
            return;
        }

        int pos = spinnerGalas.getSelectedItemPosition();
        if (pos >= 0 && !listaGalasActivas.isEmpty()) {
            Gala gala = listaGalasActivas.get(pos);

            // Crear entidad con clave compuesta (espectadorId, concursanteId, galaId)
            Puntuacion voto = new Puntuacion(
                    espectadorId,
                    concursanteId,
                    gala.getId(),
                    rating,
                    LocalDate.now()
            );

            // Inserción asíncrona mediante Thread puro (Service)
            puntuacionService.puntuar(voto);

            // Al insertar, el LiveData del observador detectará el cambio y cerrará la pantalla
        }
    }
}