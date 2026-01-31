package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.GalaScoreAdapter;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Gala;
import com.example.granzonamarciana.entity.pojo.PuntuacionConConcursante;
import com.example.granzonamarciana.service.GalaService;
import com.example.granzonamarciana.service.PuntuacionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalaScoresActivity extends AppCompatActivity {

    private Spinner spinnerGalas;
    private ListView lvRanking;
    private GalaService galaService;
    private PuntuacionService puntuacionService;
    private List<Gala> listaGalas;
    private TextView tvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gala_scores);

        // 1. Inicializar Vistas
        initViews();

        // 2. Inicializar Servicios
        galaService = new GalaService(getApplication());
        puntuacionService = new PuntuacionService(this);

        // 3. Obtener ID de edición del Intent (por defecto 1)
        int editionId = getIntent().getIntExtra("EDITION_ID", 1);

        // 4. Configurar botón volver
        tvBack = findViewById(R.id.tvBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 5. Cargar datos iniciales
        cargarGalas(editionId);
    }

    private void initViews() {
        spinnerGalas = findViewById(R.id.spinnerGalaResults);
        lvRanking = findViewById(R.id.lvGalaScores);
    }

    private void cargarGalas(int editionId) {
        galaService.getGalasByEdicion(editionId).observe(this, galas -> {
            if (galas != null && !galas.isEmpty()) {
                this.listaGalas = galas;
                List<String> nombresGalas = new ArrayList<>();

                for (Gala g : galas) {
                    nombresGalas.add("Gala " + g.getId() + " (" + g.getFecha() + ")");
                }

                // Usamos el layout personalizado para que el texto sea blanco
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_rol_item, nombresGalas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerGalas.setAdapter(adapter);

                spinnerGalas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int galaId = listaGalas.get(position).getId();
                        actualizarRanking(galaId);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            } else {
                Toast.makeText(this, "No hay galas registradas para esta edición.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarRanking(int galaId) {
        // Obtenemos los votos usando el POJO que une Puntuación con Concursante
        puntuacionService.obtenerResultadosGala(galaId).observe(this, votos -> {
            if (votos != null && !votos.isEmpty()) {

                // Mapas para procesar los datos en una sola pasada
                Map<Integer, Double> sumaNotas = new HashMap<>();
                Map<Integer, Integer> contadorVotos = new HashMap<>();
                Map<Integer, Concursante> concursantesMap = new HashMap<>();

                for (PuntuacionConConcursante item : votos) {
                    int id = item.concursante.getId();

                    // Guardamos el objeto concursante
                    concursantesMap.put(id, item.concursante);

                    // Acumulamos la puntuación
                    sumaNotas.put(id, sumaNotas.getOrDefault(id, 0.0) + item.puntuacion.getValor());

                    // Contamos el voto
                    contadorVotos.put(id, contadorVotos.getOrDefault(id, 0) + 1);
                }

                // Calculamos las medias finales y preparamos la lista para el Ranking
                List<Concursante> ranking = new ArrayList<>(concursantesMap.values());
                Map<Integer, Double> mediasFinales = new HashMap<>();

                for (Concursante c : ranking) {
                    int id = c.getId();
                    double media = sumaNotas.get(id) / contadorVotos.get(id);
                    mediasFinales.put(id, media);
                }

                // Ordenar la lista de mayor a menor puntuación media
                Collections.sort(ranking, (c1, c2) ->
                        mediasFinales.get(c2.getId()).compareTo(mediasFinales.get(c1.getId()))
                );

                // Enviamos los datos al Adapter
                GalaScoreAdapter adapter = new GalaScoreAdapter(this, R.layout.item_gala_score, ranking, mediasFinales);
                lvRanking.setAdapter(adapter);

            } else {
                // Si no hay votos, limpiamos la lista
                lvRanking.setAdapter(null);
                Toast.makeText(this, "Aún no hay puntuaciones en esta gala.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}