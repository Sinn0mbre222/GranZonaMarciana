package com.example.granzonamarciana.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

    private int concursanteId, espectadorId;
    private String concursanteNombre, concursanteFoto;
    private List<Gala> listaGalasActivas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_participant);

        // LOG DE ENTRADA
        Log.d("DEBUG_VOTAR", "¡Llegamos a RateParticipantActivity!");

        concursanteId = getIntent().getIntExtra("CONCURSANTE_ID", -1);
        concursanteNombre = getIntent().getStringExtra("CONCURSANTE_NOMBRE");
        concursanteFoto = getIntent().getStringExtra("CONCURSANTE_FOTO");

        Log.d("DEBUG_VOTAR", "Datos recibidos -> ID: " + concursanteId + ", Nombre: " + concursanteNombre);

        if (concursanteId == -1) {
            Toast.makeText(this, "Error: Concursante no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        espectadorId = prefs.getInt("id", -1);

        initViews();
        initServices();

        tvNombre.setText(concursanteNombre);
        cargarImagenConcursante();
        cargarGalasActivas();

        spinnerGalas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                if (listaGalasActivas != null && !listaGalasActivas.isEmpty()) {
                    vincularObservadorVoto(listaGalasActivas.get(pos).getId());
                }
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
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
            Picasso.get().load(concursanteFoto).placeholder(R.drawable.ic_default_avatar).into(ivFoto);
        } else {
            ivFoto.setImageResource(R.drawable.ic_default_avatar);
        }
    }

    private void vincularObservadorVoto(int galaId) {
        puntuacionService.haVotado(galaId, espectadorId, concursanteId).observe(this, yaVotado -> {
            if (yaVotado != null && yaVotado) {
                Toast.makeText(this, "Ya has votado en esta gala", Toast.LENGTH_SHORT).show();
                btnEnviar.setEnabled(false); // Bloquear botón si ya votó
            } else {
                btnEnviar.setEnabled(true);
            }
        });
    }

    private void cargarGalasActivas() {
        solicitudService.getMisSolicitudes(concursanteId).observe(this, solicitudes -> {
            if (solicitudes != null) {
                for (Solicitud s : solicitudes) {
                    if (s.getEstado() == EstadoSolicitud.ACEPTADA) {
                        cargarGalasDeEdicion(s.getEditionId());
                        break;
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
                    // Solo galas de las últimas 24h (Hoy o Ayer)
                    if (g.getFecha().isEqual(hoy) || g.getFecha().isEqual(hoy.minusDays(1))) {
                        listaGalasActivas.add(g);
                        nombresGalas.add("Gala " + g.getId() + " (" + g.getFecha() + ")");
                    }
                }
            }

            if (listaGalasActivas.isEmpty()) {
                btnEnviar.setEnabled(false);
                nombresGalas.add("No hay galas activas");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_rol_item, nombresGalas);
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

        int pos = spinnerGalas.getSelectedItemPosition();
        if (pos >= 0 && !listaGalasActivas.isEmpty()) {
            Puntuacion voto = new Puntuacion(espectadorId, concursanteId, listaGalasActivas.get(pos).getId(), rating, LocalDate.now());
            puntuacionService.puntuar(voto);
            Toast.makeText(this, "Voto enviado", Toast.LENGTH_SHORT).show();
            finish(); // Cerrar tras votar
        }
    }
}