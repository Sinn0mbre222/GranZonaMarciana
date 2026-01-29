package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.GalaScoreAdapter;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Gala;
import com.example.granzonamarciana.entity.Puntuacion;
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
    private ConcursanteService concursanteService;

    private List<Gala> listaGalas; // Para mapear posición del spinner a objeto Gala

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gala_scores);

        // Init Vistas
        spinnerGalas = findViewById(R.id.spinnerGalaResults);
        lvRanking = findViewById(R.id.lvGalaScores);

        // Init Servicios
        galaService = new GalaService(getApplication());
        puntuacionService = new PuntuacionService(this);
        concursanteService = new ConcursanteService(this);

        // Cargar Galas (Aquí simplificamos cargando TODAS, lo ideal sería filtrar por edición activa)
        // Como tu GalaService pide ID de edición obligatoriamente,
        // asumimos Edición 1 para probar o habría que pedir al usuario qué edición ver.
        // TRUCO: Pasamos 1 por defecto. Si quieres mejorarlo, añade un spinner de edición antes.
        cargarGalas(1);
    }

    private void cargarGalas(int editionId) {
        galaService.getGalasByEdicion(editionId).observe(this, galas -> {
            if (galas != null && !galas.isEmpty()) {
                listaGalas = galas;
                List<String> nombres = new ArrayList<>();
                for (Gala g : galas) {
                    nombres.add("Gala " + g.getId() + " - " + g.getFecha());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerGalas.setAdapter(adapter);

                spinnerGalas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarRanking(listaGalas.get(position).getId());
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            } else {
                Toast.makeText(this, "No hay galas en esta edición", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarRanking(int galaId) {
        // 1. Obtener todas las puntuaciones de esa gala
        puntuacionService.obtenerPuntuacionesGala(galaId).observe(this, puntuaciones -> {
            if (puntuaciones != null) {
                calcularRanking(puntuaciones);
            }
        });
    }

    private void calcularRanking(List<Puntuacion> puntuaciones) {
        // Mapa auxiliar: ID Concursante -> Lista de notas
        Map<Integer, List<Integer>> notasPorConcursante = new HashMap<>();

        for (Puntuacion p : puntuaciones) {
            if (!notasPorConcursante.containsKey(p.getConcursanteId())) {
                notasPorConcursante.put(p.getConcursanteId(), new ArrayList<>());
            }
            notasPorConcursante.get(p.getConcursanteId()).add(p.getValor());
        }

        // Mapa de Medias: ID Concursante -> Media
        Map<Integer, Double> medias = new HashMap<>();
        List<Integer> idsConcursantes = new ArrayList<>();

        for (Map.Entry<Integer, List<Integer>> entry : notasPorConcursante.entrySet()) {
            double suma = 0;
            for (int nota : entry.getValue()) suma += nota;
            double media = suma / entry.getValue().size();

            medias.put(entry.getKey(), media);
            idsConcursantes.add(entry.getKey());
        }

        // Ahora necesitamos los objetos Concursante (Nombres) para mostrarlos
        // Esto es un poco complejo con LiveData en bucle, así que usamos un truco:
        // Pedimos TODOS los concursantes y filtramos en memoria.
        concursanteService.obtenerTodos().observe(this, todosLosConcursantes -> {
            List<Concursante> ranking = new ArrayList<>();
            for (Concursante c : todosLosConcursantes) {
                if (medias.containsKey(c.getId())) {
                    ranking.add(c);
                }
            }

            // Ordenar por nota (Mayor a menor)
            Collections.sort(ranking, (c1, c2) -> {
                Double media1 = medias.get(c1.getId());
                Double media2 = medias.get(c2.getId());
                return media2.compareTo(media1); // Descendente
            });

            // Mostrar en lista
            GalaScoreAdapter adapter = new GalaScoreAdapter(
                    this,
                    R.layout.item_gala_score,
                    ranking,
                    medias
            );
            lvRanking.setAdapter(adapter);
        });
    }
}