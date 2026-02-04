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

// Esta pantalla muestra el HISTORIAL de puntuaciones.
// Es dinámica: Muestra datos diferentes dependiendo de si eres Espectador o Concursante.
public class RatingHistoryActivity extends AppCompatActivity {

    // Elementos visuales
    private TextView tvTitle, tvStat1, tvStat2; // Título y textos de estadísticas (Total y Media)
    private ListView lvHistory; // La lista donde se pintarán las filas

    // Servicio y Adaptador
    private PuntuacionService puntuacionService;
    private HistoryAdapter adapter; // Adaptador especial para pintar filas de historial

    // Datos del usuario logueado
    private int currentUserId;
    private TipoRol currentUserRol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_history);

        // 1. Intentamos cargar quién es el usuario que ha entrado
        if (!cargarDatosSesion()) {
            Toast.makeText(this, "Error: No se encontró una sesión activa", Toast.LENGTH_SHORT).show();
            finish(); // Si falla, cerramos la actividad
            return;
        }

        initViews();
        puntuacionService = new PuntuacionService(this);

        // 2. Configurar el botón de volver (tvBack)
        View btnBack = findViewById(R.id.tvBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // 3. Cargar la lista de datos correspondiente
        cargarHistorial();
    }

    // Lee las SharedPreferences para saber ID y ROL del usuario
    private boolean cargarDatosSesion() {
        // IMPORTANTE: Usamos "granZMUser" para mantener la sesión iniciada en LoginActivity
        SharedPreferences prefs = getSharedPreferences("granZMUser", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("id", -1);
        String rolStr = prefs.getString("rol", null);

        if (currentUserId != -1 && rolStr != null) {
            currentUserRol = TipoRol.valueOf(rolStr); // Convertimos el texto a TipoRol (Enum)
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

    // Lógica principal: Decide qué buscar en la base de datos
    private void cargarHistorial() {
        if (currentUserRol == TipoRol.ESPECTADOR) {
            // CASO A: Soy ESPECTADOR -> Quiero ver MIS VOTOS (a quién he puntuado)
            tvTitle.setText("Mis Votos Realizados");
            puntuacionService.obtenerHistorialEspectador(currentUserId).observe(this, this::mostrarDatos);

        } else if (currentUserRol == TipoRol.CONCURSANTE) {
            // CASO B: Soy CONCURSANTE -> Quiero ver PUNTUACIONES RECIBIDAS (quién me ha votado)
            tvTitle.setText("Puntuaciones Recibidas");
            puntuacionService.obtenerHistorialConcursante(currentUserId).observe(this, this::mostrarDatos);
        }
    }

    // Recibe la lista de datos de la BD y la pinta en pantalla
    private void mostrarDatos(List<Puntuacion> lista) {
        if (lista != null) {
            // 1. Llenamos el ListView usando el adaptador
            adapter = new HistoryAdapter(this, R.layout.item_history, lista);
            lvHistory.setAdapter(adapter);

            // 2. Calculamos Estadísticas para mostrar en la cabecera
            tvStat1.setText("Total registros: " + lista.size());

            if (!lista.isEmpty()) {
                double suma = 0;
                for (Puntuacion p : lista) suma += p.getValor();

                // Calculamos la media aritmética
                double media = suma / lista.size();
                tvStat2.setText(String.format(Locale.getDefault(), "Media: %.1f ⭐", media));
            } else {
                tvStat2.setText("Media: 0.0 ⭐");
            }
        }
    }
}