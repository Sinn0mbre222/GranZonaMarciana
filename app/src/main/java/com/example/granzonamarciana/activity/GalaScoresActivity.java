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

        // 3. Obtener IDs del Intent
        // EDITION_ID para cargar todas las galas del spinner
        // GALA_ID para saber cuál mostrar primero (si viene de la lista)
        int editionId = getIntent().getIntExtra("EDITION_ID", 1);
        int initialGalaId = getIntent().getIntExtra("GALA_ID", -1);

        // 4. Configurar botón volver
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 5. Cargar galas y posicionar el Spinner
        cargarGalas(editionId, initialGalaId);
    }

    private void initViews() {
        spinnerGalas = findViewById(R.id.spinnerGalaResults);
        lvRanking = findViewById(R.id.lvGalaScores);
        tvBack = findViewById(R.id.tvBack);
    }

    private void cargarGalas(int editionId, int initialGalaId) {
        galaService.getGalasByEdicion(editionId).observe(this, galas -> {
            if (galas != null && !galas.isEmpty()) {
                this.listaGalas = galas;
                List<String> nombresGalas = new ArrayList<>();
                int positionToSelect = 0;

                for (int i = 0; i < galas.size(); i++) {
                    Gala g = galas.get(i);
                    nombresGalas.add("Gala " + g.getId() + " (" + g.getFecha() + ")");

                    // Si el ID de esta gala coincide con el que pulsamos en la lista, guardamos su posición
                    if (g.getId() == initialGalaId) {
                        positionToSelect = i;
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_rol_item, nombresGalas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerGalas.setAdapter(adapter);

                // Forzamos al Spinner a seleccionar la gala correcta
                spinnerGalas.setSelection(positionToSelect);

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
        puntuacionService.obtenerResultadosGala(galaId).observe(this, votos -> {
            if (votos != null && !votos.isEmpty()) {
                Map<Integer, Double> sumaNotas = new HashMap<>();
                Map<Integer, Integer> contadorVotos = new HashMap<>();
                Map<Integer, Concursante> concursantesMap = new HashMap<>();

                for (PuntuacionConConcursante item : votos) {
                    int id = item.concursante.getId();
                    concursantesMap.put(id, item.concursante);
                    sumaNotas.put(id, sumaNotas.getOrDefault(id, 0.0) + item.puntuacion.getValor());
                    contadorVotos.put(id, contadorVotos.getOrDefault(id, 0) + 1);
                }

                List<Concursante> ranking = new ArrayList<>(concursantesMap.values());
                Map<Integer, Double> mediasFinales = new HashMap<>();

                for (Concursante c : ranking) {
                    int id = c.getId();
                    double media = sumaNotas.get(id) / contadorVotos.get(id);
                    mediasFinales.put(id, media);
                }

                Collections.sort(ranking, (c1, c2) ->
                        mediasFinales.get(c2.getId()).compareTo(mediasFinales.get(c1.getId()))
                );

                GalaScoreAdapter adapter = new GalaScoreAdapter(this, R.layout.item_gala_score, ranking, mediasFinales);
                lvRanking.setAdapter(adapter);

            } else {
                lvRanking.setAdapter(null);
                Toast.makeText(this, "Aún no hay puntuaciones en esta gala.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}