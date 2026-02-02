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

        concursanteId = getIntent().getIntExtra("CONCURSANTE_ID", -1);
        if (concursanteId == -1) {
            Toast.makeText(this, "Error al cargar concursante", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        concursanteService = new ConcursanteService(this);
        puntuacionService = new PuntuacionService(this);

        cargarDatosConcursante();
        cargarHistorial();

        // Configurar visibilidad del botón según rol
        configurarBotonPuntuar();

        // Botón volver
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

    private void cargarDatosConcursante() {
        concursanteService.obtenerPorId(concursanteId).observe(this, concursante -> {
            if (concursante != null) {
                // Usamos getPrimerApellido corregido
                String nombreCompleto = concursante.getNombre() + " " + concursante.getPrimerApellido();
                tvNombre.setText(nombreCompleto);
                ivFoto.setImageResource(R.drawable.ic_default_avatar);
            }
        });
    }

    private void cargarHistorial() {
        puntuacionService.obtenerHistorialConcursante(concursanteId).observe(this, puntuaciones -> {
            if (puntuaciones != null) {
                adapter = new HistoryAdapter(this, R.layout.item_history, puntuaciones);
                lvHistory.setAdapter(adapter);
                actualizarEstadisticas(puntuaciones);
            }
        });
    }

    private void actualizarEstadisticas(List<Puntuacion> puntuaciones) {
        tvGalaCount.setText("Votos: " + puntuaciones.size());

        if (!puntuaciones.isEmpty()) {
            double suma = 0;
            for (Puntuacion p : puntuaciones) suma += p.getValor();
            double media = suma / puntuaciones.size();
            tvAvgScore.setText(String.format(Locale.getDefault(), "Media: %.1f", media));
        } else {
            tvAvgScore.setText("Media: 0.0");
        }
    }

    private void configurarBotonPuntuar() {
        // CORRECCIÓN: Usamos "granZMUser" que es lo que usa LoginActivity ahora
        SharedPreferences prefs = getSharedPreferences("granZMUser", Context.MODE_PRIVATE);

        // LoginActivity guarda el rol con la clave "rol"
        String rolStr = prefs.getString("rol", null);

        // Solo mostramos botón si es ESPECTADOR
        if (rolStr != null && rolStr.equals(TipoRol.ESPECTADOR.name())) {
            btnRateNow.setVisibility(View.VISIBLE);
            btnRateNow.setOnClickListener(v -> {
                Intent intent = new Intent(ParticipantPublicActivity.this, RateParticipantActivity.class);
                intent.putExtra("CONCURSANTE_ID", concursanteId);
                intent.putExtra("CONCURSANTE_NOMBRE", tvNombre.getText().toString());
                startActivity(intent);
            });
        } else {
            // Invitados, Admins y Concursantes NO ven el botón
            btnRateNow.setVisibility(View.GONE);
        }
    }
}