package com.example.granzonamarciana.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.HistoryAdapter;
import com.example.granzonamarciana.entity.Puntuacion;
import com.example.granzonamarciana.entity.TipoRol;
import com.example.granzonamarciana.service.PuntuacionService;

import java.util.List;
import java.util.Locale;

public class RatingHistoryActivity extends AppCompatActivity {

    private TextView tvTitle, tvStat1, tvStat2;
    private ListView lvHistory;
    private PuntuacionService puntuacionService;
    private HistoryAdapter adapter;

    private int currentUserId;
    private TipoRol currentUserRol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_history);

        // 1. Obtener usuario logueado con las llaves correctas
        if (!cargarDatosSesion()) {
            Toast.makeText(this, "Error: No se encontró una sesión activa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        puntuacionService = new PuntuacionService(this);

        // 2. Configurar el botón de volver
        //View btnBack = findViewById(R.id.btnBackHistory);
        //if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        cargarHistorial();
    }

    private boolean cargarDatosSesion() {
        // CORRECCIÓN: Usar el mismo nombre que en Login y Profile
        SharedPreferences prefs = getSharedPreferences("granZMUser", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("id", -1);
        String rolStr = prefs.getString("rol", null);

        if (currentUserId != -1 && rolStr != null) {
            currentUserRol = TipoRol.valueOf(rolStr);
            return true;
        }
        return false;
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvHistoryTitle);
        tvStat1 = findViewById(R.id.tvHistoryStat1);
        tvStat2 = findViewById(R.id.tvHistoryStat2);
        lvHistory = findViewById(R.id.lvHistory);
    }

    private void cargarHistorial() {
        if (currentUserRol == TipoRol.ESPECTADOR) {
            tvTitle.setText("Mis Votos Realizados");
            puntuacionService.obtenerHistorialEspectador(currentUserId).observe(this, this::mostrarDatos);
        } else if (currentUserRol == TipoRol.CONCURSANTE) {
            tvTitle.setText("Puntuaciones Recibidas");
            puntuacionService.obtenerHistorialConcursante(currentUserId).observe(this, this::mostrarDatos);
        }
    }

    private void mostrarDatos(List<Puntuacion> lista) {
        if (lista != null) {
            // Configurar lista con el adaptador personalizado
            adapter = new HistoryAdapter(this, R.layout.item_history, lista);
            lvHistory.setAdapter(adapter);

            // Estadísticas
            tvStat1.setText("Total registros: " + lista.size());

            if (!lista.isEmpty()) {
                double suma = 0;
                for (Puntuacion p : lista) suma += p.getValor();
                double media = suma / lista.size();
                tvStat2.setText(String.format(Locale.getDefault(), "Media: %.1f ⭐", media));
            } else {
                tvStat2.setText("Media: 0.0 ⭐");
            }
        }
    }
}