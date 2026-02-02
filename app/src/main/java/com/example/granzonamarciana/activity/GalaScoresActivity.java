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
import com.example.granzonamarciana.service.ConcursanteService;
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
    private ConcursanteService concursanteService; // Añadido para traer a todos los concursantes
    private List<Gala> listaGalas;
    private TextView tvBack;
    private int editionId; // Variable global para facilitar el acceso

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gala_scores);

        // 1. Inicializar Vistas
        initViews();

        // 2. Inicializar Servicios
        galaService = new GalaService(getApplication());
        puntuacionService = new PuntuacionService(this);
        concursanteService = new ConcursanteService(this);

        // 3. Obtener IDs del Intent
        // Intentamos coger el editionId. Si no viene, usamos 1 por defecto
        editionId = getIntent().getIntExtra("EDITION_ID", 1);
        int initialGalaId = getIntent().getIntExtra("GALA_ID", -1);

        // 4. Configurar botón volver
        tvBack.setOnClickListener(v -> finish());

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
                    nombresGalas.add("Gala " + (i + 1) + " (" + g.getFecha() + ")");

                    // Posicionar el spinner en la gala que seleccionamos en la actividad anterior
                    if (g.getId() == initialGalaId) {
                        positionToSelect = i;
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_rol_item, nombresGalas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerGalas.setAdapter(adapter);

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

    /**
     * Muestra el ranking de TODOS los concursantes de la edición.
     * Si no tienen votos en esta gala, aparecerán con un 0.0.
     */
    private void actualizarRanking(int galaId) {
        // Obtenemos todos los concursantes que pertenecen a la edición actual
        concursanteService.obtenerPorEdicion(editionId).observe(this, todosLosConcursantes -> {
            if (todosLosConcursantes == null) return;

            // Obtenemos las puntuaciones que existen en esta gala
            puntuacionService.obtenerResultadosGala(galaId).observe(this, votos -> {

                Map<Integer, Double> sumaNotas = new HashMap<>();
                Map<Integer, Integer> contadorVotos = new HashMap<>();
                Map<Integer, Double> mediasFinales = new HashMap<>();

                // 1. Procesar los votos recibidos (POJO PuntuacionConConcursante)
                if (votos != null) {
                    for (PuntuacionConConcursante item : votos) {
                        int id = item.concursante.getId();
                        sumaNotas.put(id, sumaNotas.getOrDefault(id, 0.0) + item.puntuacion.getValor());
                        contadorVotos.put(id, contadorVotos.getOrDefault(id, 0) + 1);
                    }
                }

                // 2. Calcular medias para todos. Si no está en el mapa de votos, su media es 0.0
                for (Concursante c : todosLosConcursantes) {
                    int id = c.getId();
                    if (contadorVotos.containsKey(id) && contadorVotos.get(id) > 0) {
                        double media = sumaNotas.get(id) / contadorVotos.get(id);
                        mediasFinales.put(id, media);
                    } else {
                        mediasFinales.put(id, 0.0); // Concursante sin votos en esta gala
                    }
                }

                // 3. Ordenar la lista de mayor a menor puntuación media
                Collections.sort(todosLosConcursantes, (c1, c2) ->
                        mediasFinales.get(c2.getId()).compareTo(mediasFinales.get(c1.getId()))
                );

                // 4. Actualizar el adaptador con la lista completa y el mapa de medias
                GalaScoreAdapter adapter = new GalaScoreAdapter(this, R.layout.item_gala_score, todosLosConcursantes, mediasFinales);
                lvRanking.setAdapter(adapter);
            });
        });
    }
}