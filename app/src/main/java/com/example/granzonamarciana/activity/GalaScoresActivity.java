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

// Esta pantalla muestra el RANKING (clasificación) de los concursantes en una gala específica.
// Se ordenan de mayor a menor puntuación media.
public class GalaScoresActivity extends AppCompatActivity {

    // Elementos de la interfaz
    private Spinner spinnerGalas; // Desplegable para elegir qué gala ver
    private ListView lvRanking;   // La lista con los resultados
    private TextView tvBack;      // Botón volver

    // Servicios para conectar con la base de datos
    private GalaService galaService;
    private PuntuacionService puntuacionService;
    private ConcursanteService concursanteService;

    // Datos temporales
    private List<Gala> listaGalas;
    private int editionId; // ID de la edición actual (para filtrar galas y concursantes)

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

        // 3. Obtener IDs enviados desde la pantalla anterior (si los hay)
        // Intentamos coger el editionId. Si no viene, usamos 1 por defecto para que no falle.
        editionId = getIntent().getIntExtra("EDITION_ID", 1);
        int initialGalaId = getIntent().getIntExtra("GALA_ID", -1);

        // 4. Configurar botón volver
        tvBack.setOnClickListener(v -> finish());

        // 5. Cargar las galas en el spinner y seleccionar la inicial si corresponde
        cargarGalas(editionId, initialGalaId);
    }

    private void initViews() {
        spinnerGalas = findViewById(R.id.spinnerGalaResults);
        lvRanking = findViewById(R.id.lvGalaScores);
        tvBack = findViewById(R.id.tvBack);
    }

    // Busca las galas de esta edición y rellena el Spinner
    private void cargarGalas(int editionId, int initialGalaId) {
        galaService.getGalasByEdicion(editionId).observe(this, galas -> {
            if (galas != null && !galas.isEmpty()) {
                this.listaGalas = galas;
                List<String> nombresGalas = new ArrayList<>();
                int positionToSelect = 0;

                // Creamos la lista de nombres para el desplegable (Ej: "Gala 1 (2023-10-01)")
                for (int i = 0; i < galas.size(); i++) {
                    Gala g = galas.get(i);
                    nombresGalas.add("Gala " + (i + 1) + " (" + g.getFecha() + ")");

                    // Si venimos de votar en una gala concreta, hacemos que el spinner la seleccione automáticamente
                    if (g.getId() == initialGalaId) {
                        positionToSelect = i;
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_rol_item, nombresGalas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerGalas.setAdapter(adapter);

                // Seleccionamos la gala por defecto o la que venía del intent
                spinnerGalas.setSelection(positionToSelect);

                // Listener: Cuando el usuario cambia la gala, recalculamos el ranking
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
     * LÓGICA PRINCIPAL DEL RANKING:
     * 1. Trae a TODOS los concursantes (incluso los que tienen 0 votos).
     * 2. Trae los votos de la gala seleccionada.
     * 3. Calcula la media de cada uno.
     * 4. Ordena la lista de mejor a peor nota.
     */
    private void actualizarRanking(int galaId) {
        // Paso 1: Obtener todos los concursantes de la edición (para que nadie falte en la lista)
        concursanteService.obtenerPorEdicion(editionId).observe(this, todosLosConcursantes -> {
            if (todosLosConcursantes == null) return;

            // Paso 2: Obtener las puntuaciones reales de esta gala
            puntuacionService.obtenerResultadosGala(galaId).observe(this, votos -> {

                // Mapas auxiliares para hacer los cálculos matemáticos
                Map<Integer, Double> sumaNotas = new HashMap<>();    // ID Concursante -> Suma total de estrellas
                Map<Integer, Integer> contadorVotos = new HashMap<>(); // ID Concursante -> Cantidad de votos
                Map<Integer, Double> mediasFinales = new HashMap<>();  // ID Concursante -> Nota Media Final

                // Procesamos los votos (si hay)
                if (votos != null) {
                    for (PuntuacionConConcursante item : votos) {
                        int id = item.concursante.getId();
                        // Sumamos el valor del voto (ej: +5 estrellas)
                        sumaNotas.put(id, sumaNotas.getOrDefault(id, 0.0) + item.puntuacion.getValor());
                        // Incrementamos el contador de votos (+1 voto)
                        contadorVotos.put(id, contadorVotos.getOrDefault(id, 0) + 1);
                    }
                }

                // Paso 3: Calcular medias para TODOS los concursantes
                for (Concursante c : todosLosConcursantes) {
                    int id = c.getId();
                    // Si tiene votos, calculamos: Suma / Cantidad
                    if (contadorVotos.containsKey(id) && contadorVotos.get(id) > 0) {
                        double media = sumaNotas.get(id) / contadorVotos.get(id);
                        mediasFinales.put(id, media);
                    } else {
                        // Si no tiene votos, le ponemos un 0.0 para que salga al final pero salga
                        mediasFinales.put(id, 0.0);
                    }
                }

                // Paso 4: Ordenar la lista. Usamos un "Comparator" personalizado.
                // Compara las medias finales para poner primero a los que tienen más nota.
                Collections.sort(todosLosConcursantes, (c1, c2) ->
                        mediasFinales.get(c2.getId()).compareTo(mediasFinales.get(c1.getId()))
                );

                // Paso 5: Mostrar en pantalla usando el Adaptador personalizado
                // Le pasamos la lista ordenada Y el mapa de medias para que pinte la nota
                GalaScoreAdapter adapter = new GalaScoreAdapter(this, R.layout.item_gala_score, todosLosConcursantes, mediasFinales);
                lvRanking.setAdapter(adapter);
            });
        });
    }
}