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
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Puntuacion;
import com.example.granzonamarciana.entity.TipoRol;
import com.example.granzonamarciana.service.ConcursanteService;
import com.example.granzonamarciana.service.PuntuacionService;

import java.util.List;
import java.util.Locale;

public class ParticipantPublicActivity extends AppCompatActivity {

    private ImageView ivFoto;
    private TextView tvNombre, tvAvgScore, tvGalaCount;
    private Button btnRateNow;
    private ListView lvHistory;

    private ConcursanteService concursanteService;
    private PuntuacionService puntuacionService;
    private HistoryAdapter adapter;

    private int concursanteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_public);

        // 1. Recibir ID del concursante desde la lista anterior
        concursanteId = getIntent().getIntExtra("CONCURSANTE_ID", -1);
        if (concursanteId == -1) {
            Toast.makeText(this, "Error al cargar concursante", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Inicializar
        initViews();
        concursanteService = new ConcursanteService(this);
        puntuacionService = new PuntuacionService(this);

        // 3. Cargar datos
        cargarDatosConcursante();
        cargarHistorial();

        // 4. Mostrar botón de votar SOLO si eres Espectador
        configurarBotonPuntuar();
    }

    private void initViews() {
        ivFoto = findViewById(R.id.ivParticipantPublic);
        tvNombre = findViewById(R.id.tvPublicName);
        tvAvgScore = findViewById(R.id.tvAvgScore);
        tvGalaCount = findViewById(R.id.tvGalaCount);
        btnRateNow = findViewById(R.id.btnRateNow);
        lvHistory = findViewById(R.id.lvParticipantHistory);
    }

    private void cargarDatosConcursante() {
        concursanteService.obtenerPorId(concursanteId).observe(this, concursante -> {
            if (concursante != null) {
                // Usamos el método corregido getPrimerApellido
                String nombreCompleto = concursante.getNombre() + " " + concursante.getPrimerApellido();
                tvNombre.setText(nombreCompleto);

                // Imagen por defecto
                ivFoto.setImageResource(R.drawable.ic_default_avatar);
            }
        });
    }

    private void cargarHistorial() {
        // Obtenemos las puntuaciones recibidas por este concursante
        // SI ESTO SALE EN ROJO, MIRA LA NOTA DE ABAJO
        puntuacionService.obtenerHistorialConcursante(concursanteId).observe(this, puntuaciones -> {
            if (puntuaciones != null) {
                // Rellenar lista
                adapter = new HistoryAdapter(this, R.layout.item_history, puntuaciones);
                lvHistory.setAdapter(adapter);

                // Calcular Estadísticas (Media y Total)
                actualizarEstadisticas(puntuaciones);
            }
        });
    }

    private void actualizarEstadisticas(List<Puntuacion> puntuaciones) {
        tvGalaCount.setText("Votos: " + puntuaciones.size());

        if (!puntuaciones.isEmpty()) {
            double suma = 0;
            for (Puntuacion p : puntuaciones) {
                suma += p.getValor();
            }
            double media = suma / puntuaciones.size();
            tvAvgScore.setText(String.format(Locale.getDefault(), "Media: %.1f", media));
        } else {
            tvAvgScore.setText("Media: 0.0");
        }
    }

    private void configurarBotonPuntuar() {
        // Leemos el rol del usuario desde SharedPreferences (como en el Login)
        SharedPreferences prefs = getSharedPreferences("GranZonaMarcianaPrefs", Context.MODE_PRIVATE);
        String rolStr = prefs.getString("USER_ROLE", null);

        if (rolStr != null && TipoRol.valueOf(rolStr) == TipoRol.ESPECTADOR) {
            btnRateNow.setVisibility(View.VISIBLE);
            btnRateNow.setOnClickListener(v -> {
                // Navegar a la pantalla de votación (RateParticipantActivity)
                Intent intent = new Intent(ParticipantPublicActivity.this, RateParticipantActivity.class);
                intent.putExtra("CONCURSANTE_ID", concursanteId);
                intent.putExtra("CONCURSANTE_NOMBRE", tvNombre.getText().toString());
                startActivity(intent);
            });
        } else {
            btnRateNow.setVisibility(View.GONE);
        }
    }
}