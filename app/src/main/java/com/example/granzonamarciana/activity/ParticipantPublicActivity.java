package com.example.granzonamarciana.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.HistoryAdapter;
import com.example.granzonamarciana.entity.Puntuacion;
import com.example.granzonamarciana.entity.TipoRol;
import com.example.granzonamarciana.service.ConcursanteService;
import com.example.granzonamarciana.service.PuntuacionService;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

// Pantalla "Ficha Pública" de un concursante.
// Accesible para TODOS (incluidos invitados sin registrar).
public class ParticipantPublicActivity extends AppCompatActivity {

    // Vistas de la interfaz
    private ImageView ivFoto;
    private TextView tvNombre, tvAvgScore, tvGalaCount;
    private Button btnRateNow; // Botón para ir a votar (se oculta si no eres espectador)
    private ListView lvHistory;

    // Servicios de base de datos
    private ConcursanteService concursanteService;
    private PuntuacionService puntuacionService;
    private HistoryAdapter adapter;

    // ID del concursante que estamos viendo
    private int concursanteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_public);

        // 1. Recibir el ID enviado desde la lista anterior (ParticipantsListActivity)
        concursanteId = getIntent().getIntExtra("CONCURSANTE_ID", -1);
        if (concursanteId == -1) {
            Toast.makeText(this, "Error al cargar concursante", Toast.LENGTH_SHORT).show();
            finish(); // Si falla el ID, cerramos para no mostrar datos vacíos
            return;
        }

        initViews();
        concursanteService = new ConcursanteService(this);
        puntuacionService = new PuntuacionService(this);

        // 2. Cargar la información del concursante (Nombre, Foto...)
        cargarDatosConcursante();

        // 3. Cargar su historial de puntos recibidos
        cargarHistorial();

        // 4. Decidir si mostramos el botón de votar o lo ocultamos
        configurarBotonPuntuar();

        // Botón flecha atrás
        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        ivFoto = findViewById(R.id.ivParticipantPublic);
        tvNombre = findViewById(R.id.tvPublicName);
        tvAvgScore = findViewById(R.id.tvAvgScore);
        tvGalaCount = findViewById(R.id.tvGalaCount);
        btnRateNow = findViewById(R.id.btnRateNow);
        lvHistory = findViewById(R.id.lvParticipantHistory);
    }

    // Busca al concursante en la BD y pinta sus datos
    private void cargarDatosConcursante() {
        concursanteService.obtenerPorId(concursanteId).observe(this, concursante -> {
            if (concursante != null) {
                String nombreCompleto = concursante.getNombre() + " " + concursante.getPrimerApellido();
                tvNombre.setText(nombreCompleto);

                // Carga de imagen: Soporta URLs de internet y nombres de recursos locales (drawable)
                String imgData = concursante.getImagenUrl();

                if (imgData != null) {
                    if (imgData.startsWith("http")) {
                        // Caso A: Es una URL de Internet -> Usamos Picasso
                        Picasso.get()
                                .load(imgData)
                                .placeholder(R.drawable.ic_default_avatar)
                                .error(R.drawable.ic_default_avatar)
                                .into(ivFoto);
                    } else {
                        // Caso B: Es un recurso local (ej. "ic_perfil_juan") -> Buscamos el ID
                        int resId = getResources().getIdentifier(imgData, "drawable", getPackageName());

                        if (resId != 0) {
                            Picasso.get().load(resId).into(ivFoto);
                        } else {
                            ivFoto.setImageResource(R.drawable.ic_default_avatar);
                        }
                    }
                } else {
                    // Caso C: No tiene imagen -> Ponemos avatar por defecto
                    ivFoto.setImageResource(R.drawable.ic_default_avatar);
                }
            }
        });
    }

    // Busca todas las puntuaciones que ha recibido este concursante
    private void cargarHistorial() {
        puntuacionService.obtenerHistorialConcursante(concursanteId).observe(this, puntuaciones -> {
            if (puntuaciones != null) {
                // Rellenamos la lista visual con el adaptador
                adapter = new HistoryAdapter(this, R.layout.item_history, puntuaciones);
                lvHistory.setAdapter(adapter);

                // Actualizamos los textos de resumen (Media y Total)
                actualizarEstadisticas(puntuaciones);
            }
        });
    }

    // Calcula la nota media aritmética
    private void actualizarEstadisticas(List<Puntuacion> puntuaciones) {
        tvGalaCount.setText("Votos: " + puntuaciones.size());

        if (!puntuaciones.isEmpty()) {
            double suma = 0;
            for (Puntuacion p : puntuaciones) suma += p.getValor();

            double media = suma / puntuaciones.size();
            // Mostramos con 1 decimal
            tvAvgScore.setText(String.format(Locale.getDefault(), "Media: %.1f", media));
        } else {
            tvAvgScore.setText("Media: 0.0");
        }
    }

    // LÓGICA DE SEGURIDAD: Solo los Espectadores pueden ver el botón de votar
    private void configurarBotonPuntuar() {
        // Leemos el rol del usuario logueado (guardado en LoginActivity)
        SharedPreferences prefs = getSharedPreferences("granZMUser", Context.MODE_PRIVATE);
        String rolStr = prefs.getString("rol", null);

        // Comprobamos si el rol es exactamente "ESPECTADOR"
        if (rolStr != null && rolStr.equals(TipoRol.ESPECTADOR.name())) {
            // SI ES ESPECTADOR: Mostramos el botón y configuramos el click
            btnRateNow.setVisibility(View.VISIBLE);
            btnRateNow.setOnClickListener(v -> {
                Intent intent = new Intent(ParticipantPublicActivity.this, RateParticipantActivity.class);
                intent.putExtra("CONCURSANTE_ID", concursanteId);
                intent.putExtra("CONCURSANTE_NOMBRE", tvNombre.getText().toString());
                startActivity(intent);
            });
        } else {
            // SI ES INVITADO, ADMIN O CONCURSANTE: Ocultamos el botón (GONE)
            // Así pueden ver el perfil pero no pueden votar.
            btnRateNow.setVisibility(View.GONE);
        }
    }
}